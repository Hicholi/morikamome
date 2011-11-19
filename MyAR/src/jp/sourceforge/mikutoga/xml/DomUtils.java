/*
 * XML DOM utilities
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.xml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.bind.DatatypeConverter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMユーティリティ。
 */
public final class DomUtils {

    /**
     * 隠しコンストラクタ。
     */
    private DomUtils(){
        super();
        assert false;
        throw new AssertionError();
    }

    /**
     * 要素からxsd:string型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return 文字列
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    public static String getStringAttr(Element elem, String attrName)
            throws TogaXmlException{
        if( ! elem.hasAttribute(attrName) ){
            String message = "Attr:[" + attrName + "] "
                    + "was not found in "
                    + "Elem:[" + elem.getTagName()+"]";
            throw new TogaXmlException(message);
        }

        String result;
        try{
            result = elem.getAttribute(attrName);
        }catch(IllegalArgumentException e){
            String message = "Invalid attribute form [" + attrName + "]";
            throw new TogaXmlException(message, e);
        }

        return result;
    }

    /**
     * 要素からxsd:boolean型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return 真ならtrue
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    public static boolean getBooleanAttr(Element elem, String attrName)
            throws TogaXmlException{
        String value = getStringAttr(elem, attrName);

        boolean result;
        try{
            result = DatatypeConverter.parseBoolean(value);
        }catch(IllegalArgumentException e){
            String message =
                    "Invalid boolean attribute form "
                    + "[" + attrName + "][" + value + "]";
            throw new TogaXmlException(message, e);
        }

        return result;
    }

    /**
     * 要素からxsd:integer型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return int値
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    public static int getIntegerAttr(Element elem, String attrName)
            throws TogaXmlException{
        String value = getStringAttr(elem, attrName);

        int result;
        try{
            result = DatatypeConverter.parseInt(value);
        }catch(IllegalArgumentException e){
            String message =
                    "Invalid integer attribute form "
                    + "[" + attrName + "][" + value + "]";
            throw new TogaXmlException(message, e);
        }

        return result;
    }

    /**
     * 要素からxsd:float型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return float値
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    public static float getFloatAttr(Element elem, String attrName)
            throws TogaXmlException{
        String value = getStringAttr(elem, attrName);

        float result;
        try{
            result = DatatypeConverter.parseFloat(value);
        }catch(IllegalArgumentException e){
            String message =
                    "Invalid float attribute form "
                    + "[" + attrName + "][" + value + "]";
            throw new TogaXmlException(message, e);
        }

        return result;
    }

    /**
     * 要素から日本語Windows用ファイル名を属性値として読み取る。
     * 念のため文字U+00A5は文字U-005Cに変換される。
     * @param elem 要素
     * @param attrName 属性名
     * @return ファイル名
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    public static String getSjisFileNameAttr(Element elem, String attrName)
            throws TogaXmlException{
        String result;
        try{
            result = getStringAttr(elem, attrName);
        }catch(IllegalArgumentException e){
            String message =
                    "Invalid winfile attribute form "
                    + "[" + attrName + "]";
            throw new TogaXmlException(message, e);
        }

        result.replace("" + '\u00a5', "" + '\u005c\u005c');

        return result;
    }

    /**
     * 指定された名前の子要素を1つだけ返す。
     * @param parent 親要素
     * @param tagName 子要素名
     * @return 子要素
     * @throws TogaXmlException 1つも見つからなかった
     */
    public static Element getChild(Element parent, String tagName)
            throws TogaXmlException{
        Element result = null;

        for(Node node = parent.getFirstChild();
            node != null;
            node = node.getNextSibling() ){

            if(node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elem = (Element) node;

            String elemTagName = elem.getTagName();
            if( tagName.equals(elemTagName) ){
                result = elem;
                break;
            }
        }

        if(result == null){
            String message =
                    "Elem:[" + tagName + "] was not found in "
                    +"Elem:[" + parent.getTagName() + "]";
            throw new TogaXmlException(message);
        }

        return result;
    }

    /**
     * 親要素が指定された名前の子要素を持つか判定する。
     * @param parent 親要素
     * @param tagName 子要素名
     * @return 指定名の子要素が存在すればtrue
     */
    public static boolean hasChild(Element parent, String tagName){
        for(Node node = parent.getFirstChild();
            node != null;
            node = node.getNextSibling() ){

            if(node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elem = (Element) node;

            String elemTagName = elem.getTagName();
            if( tagName.equals(elemTagName) ) return true;
        }

        return false;
    }

    /**
     * 指定された名前の子要素のリストを返す。
     * @param parent 親要素
     * @param childTag 子要素名
     * @return 子要素のリスト
     */
    public static List<Element> getChildList(Element parent,
                                               String childTag){
        List<Element> result = new LinkedList<Element>();

        for(Node node = parent.getFirstChild();
            node != null;
            node = node.getNextSibling() ){

            if(node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elem = (Element) node;

            String tagName = elem.getTagName();
            if( ! childTag.equals(tagName) ) continue;

            result.add(elem);
        }

        return result;
    }

    /**
     * 指定された名前の子要素の列挙子を返す。
     * @param parent 親要素
     * @param childTag 子要素名
     * @return 子要素の列挙子
     */
    public static Iterator<Element> getChildIterator(Element parent,
                                                       String childTag){
        Element firstElem;
        try{
            firstElem = getChild(parent, childTag);
        }catch(TogaXmlException e){
            firstElem = null;
        }

        Iterator<Element> result = new ElemIterator(firstElem);

        return result;
    }

    /**
     * 指定された名前の子要素のforeachを返す。
     * @param parent 親要素
     * @param childTag 子要素名
     * @return 子要素のforeach
     */
    public static Iterable<Element> getEachChild(Element parent,
                                                   String childTag){
        final Iterator<Element> iterator = getChildIterator(parent, childTag);
        Iterable<Element> result = new Iterable<Element>(){
            @Override
            public Iterator<Element> iterator(){
                return iterator;
            }
        };
        return result;
    }

    /**
     * 要素の次の要素を返す。
     * @param elem 要素
     * @return 次の要素。なければnull
     */
    public static Element nextElement(Element elem){
        Node nextNode = elem;
        for(;;){
            nextNode = nextNode.getNextSibling();
            if(nextNode == null) break;
            if(nextNode.getNodeType() == Node.ELEMENT_NODE){
                break;
            }
        }

        return (Element) nextNode;
    }

    /**
     * 同じ要素名を持つ次の要素を返す。
     * @param elem 要素
     * @return 次の要素。なければnull
     */
    public static Element nextNamedElement(Element elem){
        String tagName = elem.getTagName();
        Element nextElem = elem;
        for(;;){
            nextElem = nextElement(nextElem);
            if(nextElem == null) break;
            if(tagName.equals(nextElem.getTagName())) break;
        }

        return nextElem;
    }

    /**
     * 同じ親要素と同じ要素名を持つ兄弟要素を列挙する列挙子。
     */
    private static class ElemIterator implements Iterator<Element>{
        private Element next;

        /**
         * コンストラクタ。
         * @param elem 最初の要素。nullを指定すれば空列挙子となる。
         */
        private ElemIterator(Element elem){
            super();
            this.next = elem;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public boolean hasNext(){
            if(this.next == null) return false;
            return true;
        }

        /**
         * {@inheritDoc}
         * @return {@inheritDoc}
         * @throws NoSuchElementException {@inheritDoc}
         */
        @Override
        public Element next() throws NoSuchElementException{
            if(this.next == null) throw new NoSuchElementException();
            Element result = this.next;
            this.next = nextNamedElement(this.next);
            return result;
        }

        /**
         * {@inheritDoc}
         * ※ 未サポート。
         */
        @Override
        public void remove(){
            throw new UnsupportedOperationException();
        }

    }

}
