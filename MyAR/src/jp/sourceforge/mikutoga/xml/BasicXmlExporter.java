/*
 * basic xml exporter
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.xml;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;

/**
 * 各種XMLエクスポータの基本機能。
 * UCS4は未サポート。
 */
public class BasicXmlExporter {

    /** デフォルトエンコーディング。 */
    private static final Charset CS_UTF8 = Charset.forName("UTF-8");

    /** デフォルトの改行文字列。 */
    private static final String LF = "\n";       // 0x0a
    /** デフォルトのインデント単位。 */
    private static final String DEFAULT_INDENT_UNIT = "\u0020\u0020";

    private static final char[] HEXCHAR_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F',
    };

    static{
        assert HEXCHAR_TABLE.length == 16;
    }


    private final Appendable appendable;

    private String newline = LF;
    private String indentUnit = DEFAULT_INDENT_UNIT;

    private int indentNest = 0;
    private boolean basicLatinOnlyOut = true;


    /**
     * コンストラクタ。
     * 文字エンコーディングはUTF-8が用いられる。
     * @param stream 出力ストリーム
     */
    public BasicXmlExporter(OutputStream stream){
        this(stream, CS_UTF8);
        return;
    }

    /**
     * コンストラクタ。
     * @param stream 出力ストリーム
     * @param charSet 文字エンコーディング指定
     */
    public BasicXmlExporter(OutputStream stream, Charset charSet){
        this(
            new BufferedWriter(
                new OutputStreamWriter(stream, charSet)
            )
        );
        return;
    }

    /**
     * コンストラクタ。
     * @param appendable 文字列出力
     */
    public BasicXmlExporter(Appendable appendable){
        super();
        this.appendable = appendable;
        return;
    }

    /**
     * ASCIIコード相当(UCS:Basic-Latin)の文字か否か判定する。
     * @param ch 判定対象文字
     * @return Basic-Latin文字ならtrue
     */
    public static boolean isBasicLatin(char ch){
        if(ch <= 0x7f) return true;
        return false;
    }

    /**
     * 改行文字列を設定する。
     * @param newLine 改行文字列
     * @throws NullPointerException 引数がnull
     */
    public void setNewLine(String newLine) throws NullPointerException{
        if(newLine == null) throw new NullPointerException();
        this.newline = newLine;
        return;
    }

    /**
     * BasicLatin文字だけで出力するか設定する。
     * BasicLatin以外の文字(≒日本語)をそのまま出力するか
     * 文字参照で出力するかの設定が可能。
     * コメント部中身は対象外。
     * @param bool BasicLatin文字だけで出力するならtrue
     */
    public void setBasicLatinOnlyOut(boolean bool){
        this.basicLatinOnlyOut = bool;
        return;
    }

    /**
     * BasicLatin文字だけを出力する状態か判定する。
     * コメント部中身は対象外。
     * @return BasicLatin文字だけで出力するならtrue
     */
    public boolean isBasicLatinOnlyOut(){
        return this.basicLatinOnlyOut;
    }

    /**
     * 改行文字列を設定する。
     * デフォルトではLF(0x0a)\nが用いられる。
     * @param seq 改行文字列。nullは空文字列""と解釈される。
     */
    public void setNewLine(CharSequence seq){
        if(seq == null) this.newline = "";
        else            this.newline = seq.toString();
        return;
    }

    /**
     * インデント単位文字列を設定する。
     * デフォルトでは空白2個。
     * @param seq インデント単位文字列。nullは空文字列""と解釈される。
     */
    public void setIndentUnit(CharSequence seq){
        if(seq == null) this.indentUnit = "";
        else            this.indentUnit = seq.toString();
    }

    /**
     * 可能であれば出力をフラッシュする。
     * @throws IOException 出力エラー
     */
    public void flush() throws IOException{
        if(this.appendable instanceof Flushable){
            ((Flushable)this.appendable).flush();
        }
        return;
    }

    /**
     * 可能であれば出力をクローズする。
     * @throws IOException 出力エラー
     */
    public void close() throws IOException{
        if(this.appendable instanceof Closeable){
            ((Closeable)this.appendable).close();
        }
        return;
    }

    /**
     * 1文字出力する。
     * @param ch 文字
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter put(char ch) throws IOException{
        this.appendable.append(ch);
        return this;
    }

    /**
     * 文字列を出力する。
     * @param seq 文字列
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter put(CharSequence seq) throws IOException{
        this.appendable.append(seq);
        return this;
    }

    /**
     * int値を出力する。
     * @param iVal int値
     * @return this本体
     * @throws IOException 出力エラー
     * @see java.lang.Integer#toString(int)
     */
    public BasicXmlExporter put(int iVal) throws IOException{
        String value = DatatypeConverter.printInt(iVal);
        this.appendable.append(value);
        return this;
    }

    /**
     * float値を出力する。
     * @param fVal float値
     * @return this本体
     * @throws IOException 出力エラー
     * @see java.lang.Float#toString(float)
     */
    public BasicXmlExporter put(float fVal) throws IOException{
        String value = DatatypeConverter.printFloat(fVal);
        this.appendable.append(value);
        return this;
    }

    /**
     * 改行を出力する。
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter ln() throws IOException{
        this.appendable.append(this.newline);
        return this;
    }

    /**
     * 改行を指定回数出力する。
     * @param count 改行回数。0以下の場合は何も出力しない。
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter ln(int count) throws IOException{
        for(int ct = 1; ct <= count; ct++){
            this.appendable.append(this.newline);
        }
        return this;
    }

    /**
     * インデントを出力する。
     * インデント単位文字列をネストレベル回数分出力する。
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter ind() throws IOException{
        for(int ct = 1; ct <= this.indentNest; ct++){
            put(this.indentUnit);
        }
        return this;
    }

    /**
     * インデントレベルを一段下げる。
     */
    public void pushNest(){
        this.indentNest++;
        return;
    }

    /**
     * インデントレベルを一段上げる。
     * インデントレベル0の状態をさらに上げようとした場合、何も起こらない。
     */
    public void popNest(){
        this.indentNest--;
        if(this.indentNest < 0) this.indentNest = 0;
        return;
    }

    /**
     * 指定された文字を16進2桁の文字参照形式で出力する。
     * 2桁で出力できない場合は4桁で出力する。
     * @param ch 文字
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putCharRef2Hex(char ch) throws IOException{
        if(ch > 0xff) return putCharRef4Hex(ch);

        char hex3 = HEXCHAR_TABLE[(ch >> 4) & 0x000f];
        char hex4 = HEXCHAR_TABLE[(ch     ) & 0x000f];

        this.appendable.append("&#x");
        this.appendable.append(hex3);
        this.appendable.append(hex4);
        this.appendable.append(';');

        return this;
    }

    /**
     * 指定された文字を16進4桁の文字参照形式で出力する。
     * UCS4に伴うサロゲートペアは未サポート
     * @param ch 文字
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putCharRef4Hex(char ch) throws IOException{
        char hex1 = HEXCHAR_TABLE[(ch >> 12) & 0x000f];
        char hex2 = HEXCHAR_TABLE[(ch >>  8) & 0x000f];
        char hex3 = HEXCHAR_TABLE[(ch >>  4) & 0x000f];
        char hex4 = HEXCHAR_TABLE[(ch      ) & 0x000f];

        this.appendable.append("&#x");
        this.appendable.append(hex1);
        this.appendable.append(hex2);
        this.appendable.append(hex3);
        this.appendable.append(hex4);
        this.appendable.append(';');

        return this;
    }

    /**
     * 要素の中身および属性値中身を出力する。
     * 必要に応じてXML定義済み実体文字が割り振られた文字、
     * コントロールコード、および非BasicLatin文字がエスケープされる。
     * @param content 内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putContent(CharSequence content)
            throws IOException{
        int length = content.length();

        for(int pos = 0; pos < length; pos++){
            char ch = content.charAt(pos);
            if(Character.isISOControl(ch)){
                putCharRef2Hex(ch);
            }else if( ! isBasicLatin(ch) && isBasicLatinOnlyOut()){
                putCharRef4Hex(ch);
            }else{
                switch(ch){
                case '&':  this.appendable.append("&amp;");  break;
                case '<':  this.appendable.append("&lt;");   break;
                case '>':  this.appendable.append("&gt;");   break;
                case '"':  this.appendable.append("&quot;"); break;
                case '\'': this.appendable.append("&apos;"); break;
                case '\u00a5': this.appendable.append('\u005c\u005c'); break;
                default:   this.appendable.append(ch);       break;
                }
            }
        }

        return this;
    }

    /**
     * 属性値を出力する。
     * @param attrName 属性名
     * @param content 属性内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putAttr(CharSequence attrName,
                                     CharSequence content)
            throws IOException{
        put(attrName).put('=').put('"').putContent(content).put('"');
        return this;
    }

    /**
     * int型属性値を出力する。
     * @param attrName 属性名
     * @param iVal int値
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putIntAttr(CharSequence attrName,
                                           int iVal)
            throws IOException{
        put(attrName).put('=').put('"').put(iVal).put('"');
        return this;
    }

    /**
     * float型属性値を出力する。
     * @param attrName 属性名
     * @param fVal float値
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putFloatAttr(CharSequence attrName,
                                              float fVal)
            throws IOException{
        put(attrName).put('=').put('"').put(fVal).put('"');
        return this;
    }

    /**
     * コメントの内容を出力する。
     * コメント中の\n記号出現に伴い、
     * あらかじめ指定された改行文字が出力される。
     * \n以外のコントロールコード各種、
     * 及び非BasicLatin文字はそのまま出力される。
     * 連続するハイフン(-)記号間には強制的にスペースが挿入される。
     * @param comment コメント内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putCommentContent(CharSequence comment)
            throws IOException{
        int length = comment.length();

        char prev = '\0';
        for(int pos = 0; pos < length; pos++){
            char ch = comment.charAt(pos);
            if(ch == '\n'){
                ln();
                prev = ch;
                continue;
            }
            if(prev == '-' && ch == '-') put(' ');
            put(ch);
            prev = ch;
        }

        return this;
    }

    /**
     * 1行コメントを出力する。
     * コメント中の\n記号出現に伴い、
     * あらかじめ指定された改行文字が出力される。
     * \n以外のコントロールコード各種、
     * 及び非BasicLatin文字はそのまま出力される。
     * 連続するハイフン(-)記号間には強制的にスペースが挿入される。
     * @param comment コメント内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putLineComment(CharSequence comment)
            throws IOException{
        put("<!--").put(' ');
        putCommentContent(comment);
        put(' ').put("-->");
        return this;
    }

    /**
     * ブロックコメントを出力する。
     * コメント中の\n記号出現に伴い、
     * あらかじめ指定された改行文字が出力される。
     * \n以外のコントロールコード各種、
     * 及び非BasicLatin文字はそのまま出力される。
     * 連続するハイフン(-)記号間には強制的にスペースが挿入される。
     * @param comment コメント内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    public BasicXmlExporter putBlockComment(CharSequence comment)
            throws IOException{
        put("<!--").ln();

        putCommentContent(comment);

        int commentLength = comment.length();
        if(commentLength > 0){
            char lastCh = comment.charAt(commentLength - 1);
            if(lastCh != '\n') ln();
        }

        put("-->").ln();

        return this;
    }

}
