/*
 * xml resource resolver
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.mikutoga.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * URL変換マップに従い、XML文書からの外部参照をリダイレクトする。
 * 相対URIはこのクラスをベースに解決される。
 * 主な用途は外部スキーマのリソース化など。
 */
public class XmlResourceResolver
        implements LSResourceResolver, EntityResolver {

    public static final String SCHEMA_XML =
            "http://www.w3.org/2001/xml.xsd";
    public static final String NS_XSD =
            "http://www.w3.org/2001/XMLSchema-instance";

    private static final String LOCAL_SCHEMA_XML =
            "resources/xml-2009-01.xsd";
    private static final URI EMPTY_URI = URI.create("");
    private static final Class THISCLASS = XmlResourceResolver.class;

    private final Map<URI, URI> uriMap = new HashMap<URI, URI>();

    /**
     * コンストラクタ。
     */
    public XmlResourceResolver(){
        super();

        assert this.getClass().equals(THISCLASS);

        URI originalURI = URI.create(SCHEMA_XML);
        URL redirectURL = THISCLASS.getResource(LOCAL_SCHEMA_XML);
        URI redirectURI;
        try{
            redirectURI = redirectURL.toURI();
        }catch(URISyntaxException e){
            assert false;
            throw new AssertionError(e);
        }

        this.uriMap.put(originalURI, redirectURI);

        return;
    }

    /**
     * 絶対URIと相対URIを合成したURIを返す。
     * 正規化も行われる。
     * @param base 絶対URIでなければならない。nullでもよい。
     * @param relative 絶対URIでもよいがその場合baseは無視される。null可。
     * @return 合成結果のURLオブジェクト。必ず絶対URIになる。
     * @throws java.net.URISyntaxException URIとして変。
     * @throws java.lang.IllegalArgumentException 絶対URIが生成できない。
     */
    protected static URI buildBaseRelativeURI(String base, String relative)
            throws URISyntaxException,
                   IllegalArgumentException {
        URI baseURI = null;
        if(base != null){
            baseURI = new URI(base);
            if( ! baseURI.isAbsolute() ) throw new IllegalArgumentException();
        }

        URI relativeURI = EMPTY_URI;
        if(relative != null){
            relativeURI = new URI(relative);
        }

        URI resultURI;
        if(baseURI == null || relativeURI.isAbsolute()){
            resultURI = relativeURI;
        }else{
            resultURI = baseURI.resolve(relativeURI);
        }

        if( ! resultURI.isAbsolute() ) throw new IllegalArgumentException();

        resultURI = resultURI.normalize();

        return resultURI;
    }

    /**
     * LSInput実装を生成する。
     * @return LSInput実装
     */
    public static LSInput createLSInput(){
        LSInput input = new LSInputImpl();
        return input;
    }

    /**
     * オリジナルURIとリダイレクト先のURIを登録する。
     * オリジナルURIへのアクセスはリダイレクトされる。
     * @param original オリジナルURI
     * @param redirect リダイレクトURI
     */
    public void putURIMap(URI original, URI redirect){
        this.uriMap.put(original.normalize(), redirect.normalize());
        return;
    }

    /**
     * 変換後のリソースの入力ストリームを得る。
     * @param originalURI オリジナルURI
     * @return 入力ストリーム
     * @throws java.io.IOException 入出力エラー
     */
    public InputStream getXMLResourceAsStream(URI originalURI)
            throws IOException{
        URI resourceURI = this.uriMap.get(originalURI.normalize());
        URL resourceURL = resourceURI.toURL();
        InputStream is = resourceURL.openStream();

        return is;
    }

    /**
     * {@inheritDoc}
     * URL変換したあとの入力ソースを返す。
     * @param type {@inheritDoc}
     * @param namespaceURI {@inheritDoc}
     * @param publicId {@inheritDoc}
     * @param systemId {@inheritDoc}
     * @param baseURI {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public LSInput resolveResource(String type,
                                     String namespaceURI,
                                     String publicId,
                                     String systemId,
                                     String baseURI ){
        if(systemId == null) return null;

        URI originalURI;
        try{
            originalURI = buildBaseRelativeURI(baseURI, systemId);
        }catch(URISyntaxException e){
            return null;
        }

        InputStream is;
        try{
            is = getXMLResourceAsStream(originalURI);
        }catch(IOException e){
            return null;
        }

        LSInput input = createLSInput();
        input.setBaseURI(baseURI);
        input.setPublicId(publicId);
        input.setSystemId(systemId);
        input.setByteStream(is);

        return input;
    }

    /**
     * {@inheritDoc}
     * URL変換したあとの入力ソースを返す。
     * @param publicId {@inheritDoc}
     * @param systemId {@inheritDoc}
     * @return {@inheritDoc}
     * @throws org.xml.sax.SAXException {@inheritDoc}
     * @throws java.io.IOException {@inheritDoc}
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException{
        if(systemId == null) return null;

        URI originalUri;
        try{
            originalUri = new URI(systemId);
        }catch(URISyntaxException e){
            return null;
        }

        InputStream is = getXMLResourceAsStream(originalUri);

        InputSource source = new InputSource(is);
        source.setPublicId(publicId);
        source.setSystemId(systemId);

        return source;
    }

    /**
     * JRE1.5用LSInput実装。
     * JRE1.6なら
     * org.w3c.dom.ls.DOMImplementationLS#createLSInput()
     * で生成可能かも。
     */
    private static final class LSInputImpl implements LSInput {

        private String baseURI = null;
        private InputStream byteStream = null;
        private boolean certifiedText = false;
        private Reader characterStream = null;
        private String encoding = null;
        private String publicId = null;
        private String stringData = null;
        private String systemId = null;

        /**
         * コンストラクタ。
         */
        private LSInputImpl(){
            super();
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public String getBaseURI(){
            return this.baseURI;
        }

        /**
         * {@inheritDoc}
         * @param baseURI {@inheritDoc}
         */
        @Override
        public void setBaseURI(String baseURI){
            this.baseURI = baseURI;
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public InputStream getByteStream(){
            return this.byteStream;
        }

        /**
         * {@inheritDoc}
         * @param byteStream {@inheritDoc}
         */
        @Override
        public void setByteStream(InputStream byteStream){
            this.byteStream = byteStream;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public boolean getCertifiedText(){
            return this.certifiedText;
        }

        /**
         * {@inheritDoc}
         * @param certifiedText {@inheritDoc}
         */
        @Override
        public void setCertifiedText(boolean certifiedText){
            this.certifiedText = certifiedText;
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public Reader getCharacterStream(){
            return this.characterStream;
        }

        /**
         * {@inheritDoc}
         * @param characterStream {@inheritDoc}
         */
        @Override
        public void setCharacterStream(Reader characterStream){
            this.characterStream = characterStream;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public String getEncoding(){
            return this.encoding;
        }

        /**
         * {@inheritDoc}
         * @param encoding {@inheritDoc}
         */
        @Override
        public void setEncoding(String encoding){
            this.encoding = encoding;
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public String getPublicId(){
            return this.publicId;
        }

        /**
         * {@inheritDoc}
         * @param publicId {@inheritDoc}
         */
        @Override
        public void setPublicId(String publicId){
            this.publicId = publicId;
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public String getStringData(){
            return this.stringData;
        }

        /**
         * {@inheritDoc}
         * @param stringData {@inheritDoc}
         */
        @Override
        public void setStringData(String stringData){
            this.stringData = stringData;
            return;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public String getSystemId(){
            return this.systemId;
        }

        /**
         * {@inheritDoc}
         * @param systemId {@inheritDoc}
         */
        @Override
        public void setSystemId(String systemId){
            this.systemId = systemId;
            return;
        }

    }

    // TODO OASIS XML Catalog などと調和したい。
}
