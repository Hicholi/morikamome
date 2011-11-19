/*
 * common MMD parser
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * 各種パーサの共通実装。
 */
public class CommonParser {

    /** 日本語デコード作業用入力バッファ長。バイト単位。 */
    public static final int TEXTBUF_SZ = 512;

    /**
     * MMD各種ファイルで用いられる日本語エンコーディング。(windows-31j)
     * ほぼShift_JISのスーパーセットと思ってよい。
     * デコード結果はUCS-2集合に収まるはず。
     */
    protected static final Charset CS_WIN31J = Charset.forName("windows-31j");

    private static final byte TERMINATOR = (byte) '\0';  // 0x00
    private static final char UCSYEN = '\u00a5';
    private static final char SJISYEN = (char) 0x005c;  // '\u005c\u005c';

    private final MmdSource source;
    private final CharsetDecoder decoder;
    private final byte[] textArray;
    private final ByteBuffer textBuffer;  // textArrayの別ビュー
    private final CharBuffer charBuffer;

    /**
     * コンストラクタ。
     * @param source 入力ソース
     */
    public CommonParser(MmdSource source){
        super();

        this.source = source;
        this.decoder = CS_WIN31J.newDecoder();
        this.textArray = new byte[TEXTBUF_SZ];
        this.textBuffer = ByteBuffer.wrap(this.textArray);
        int maxChars =
                (int)(TEXTBUF_SZ * (this.decoder.maxCharsPerByte())) + 1;
        this.charBuffer = CharBuffer.allocate(maxChars);

        this.decoder.onMalformedInput(CodingErrorAction.REPORT);
        this.decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        this.textBuffer.clear();
        this.charBuffer.clear();

        return;
    }

    /**
     * 入力ソースにまだデータが残っているか判定する。
     * @return まだ読み込んでいないデータが残っていればtrue
     * @throws IOException IOエラー
     * @see MmdSource#hasMore()
     */
    protected boolean hasMore() throws IOException{
        boolean result = this.source.hasMore();
        return result;
    }

    /**
     * 入力ソースを読み飛ばす。
     * @param skipLength 読み飛ばすバイト数。
     * @throws IOException IOエラー
     * @throws MmdEofException 読み飛ばす途中でストリーム終端に達した。
     * @see MmdSource#skip(long)
     */
    protected void skip(long skipLength)
            throws IOException, MmdEofException {
        long result = this.source.skip(skipLength);
        if(result != skipLength){
            throw new MmdEofException(this.source.getPosition());
        }

        return;
    }

    /**
     * 入力ソースを読み飛ばす。
     * @param skipLength 読み飛ばすバイト数。
     * @throws IOException IOエラー
     * @throws MmdEofException 読み飛ばす途中でストリーム終端に達した。
     * @see MmdSource#skip(long)
     */
    protected void skip(int skipLength)
            throws IOException, MmdEofException {
        skip((long) skipLength);
    }

    /**
     * byte値を読み込む。
     * @return 読み込んだbyte値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseByte()
     */
    protected byte parseByte()
            throws IOException, MmdEofException{
        return this.source.parseByte();
    }

    /**
     * 符号無し値としてbyte値を読み込み、int型に変換して返す。
     * 符号は拡張されない。(0xffは0x000000ffとなる)
     * @return 読み込まれた値のint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseUByteAsInteger()
     */
    protected int parseUByteAsInteger()
            throws IOException, MmdEofException{
        return this.source.parseUByteAsInteger();
    }

    /**
     * byte値を読み込み、boolean型に変換して返す。
     * 0x00は偽、それ以外は真と解釈される。
     * @return 読み込まれた値のboolean値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseBoolean()
     */
    protected boolean parseBoolean()
            throws IOException, MmdEofException{
        return this.source.parseBoolean();
    }

    /**
     * short値を読み込む。
     * short値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだshort値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseShort()
     */
    protected short parseShort()
            throws IOException, MmdEofException{
        return this.source.parseShort();
    }

    /**
     * 符号無し値としてshort値を読み込み、int型に変換して返す。
     * 符号は拡張されない。(0xffffは0x0000ffffとなる)
     * short値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込まれた値のint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseUShortAsInteger()
     */
    protected int parseUShortAsInteger()
            throws IOException, MmdEofException{
        return this.source.parseUShortAsInteger();
    }

    /**
     * int値を読み込む。
     * int値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseInteger()
     */
    protected int parseInteger()
            throws IOException, MmdEofException{
        return this.source.parseInteger();
    }

    /**
     * float値を読み込む。
     * float値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだfloat値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseFloat()
     */
    protected float parseFloat()
            throws IOException, MmdEofException{
        return this.source.parseFloat();
    }

    /**
     * byte配列を読み込む。
     * @param dst 格納先配列
     * @param offset 読み込み開始オフセット
     * @param length 読み込みバイト数
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws IndexOutOfBoundsException 引数が配列属性と矛盾
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseByteArray(byte[], int, int)
     */
    protected void parseByteArray(byte[] dst, int offset, int length)
            throws IOException,
                   NullPointerException,
                   IndexOutOfBoundsException,
                   MmdEofException {
        this.source.parseByteArray(dst, offset, length);
        return;
    }

    /**
     * byte配列を読み込む。
     * 配列要素全ての読み込みが試みられる。
     * @param dst 格納先配列
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseByteArray(byte[])
     */
    protected void parseByteArray(byte[] dst)
            throws IOException, NullPointerException, MmdEofException{
        this.source.parseByteArray(dst);
        return;
    }

    /**
     * float配列を読み込む。
     * @param dst 格納先配列
     * @param offset 読み込み開始オフセット
     * @param length 読み込みfloat要素数
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws IndexOutOfBoundsException 引数が配列属性と矛盾
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseFloatArray(float[], int, int)
     */
    protected void parseFloatArray(float[] dst, int offset, int length)
            throws IOException,
                   NullPointerException,
                   IndexOutOfBoundsException,
                   MmdEofException {
        this.source.parseFloatArray(dst, offset, length);
        return;
    }

    /**
     * float配列を読み込む。
     * 配列要素全ての読み込みが試みられる。
     * @param dst 格納先配列
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @see MmdSource#parseFloatArray(float[])
     */
    protected void parseFloatArray(float[] dst)
            throws IOException, NullPointerException, MmdEofException{
        this.source.parseFloatArray(dst);
        return;
    }

    /**
     * 指定された最大バイト長に収まるゼロ終端(0x00)文字列を読み込む。
     * 入力バイト列はwindows-31jエンコーディングとして解釈される。
     * ゼロ終端以降のデータは無視されるが、
     * IO入力は指定バイト数だけ読み進められる。
     * ゼロ終端が見つからないまま指定バイト数が読み込み終わった場合、
     * そこまでのデータから文字列を構成する。
     * <p>
     * 戻り結果にはU+00A5(UCS円通貨記号)が含まれないことが保証される。
     * ※0x5c(Win31J円通貨)はU+005C(UCSバックスラッシュ)にデコードされる。
     *
     * @param maxlen 読み込みバイト数
     * @return デコードされた文字列
     * @throws IOException IOエラー
     * @throws IllegalArgumentException 読み込みバイト数が負であるか、
     * または内部バッファに対し大きすぎる。
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     * @throws MmdFormatException 不正な文字エンコーディングが検出された。
     */
    protected String parseZeroTermString(int maxlen)
            throws IOException,
                   IllegalArgumentException,
                   MmdEofException,
                   MmdFormatException {
        if(this.textArray.length < maxlen || maxlen < 0){
            throw new IllegalArgumentException();
        }

        this.source.parseByteArray(this.textArray, 0, maxlen);

        int length = -1;
        for(int pos = 0; pos < maxlen; pos++){
            byte ch = this.textArray[pos];
            if(ch == TERMINATOR){
                length = pos;
                break;
            }
        }
        if(length < 0) length = maxlen;

        this.textBuffer.rewind();
        this.textBuffer.limit(length);
        this.charBuffer.clear();
        CoderResult decResult =
                this.decoder.decode(this.textBuffer, this.charBuffer, true);
        if( ! decResult.isUnderflow() || decResult.isError()){
            throw new MmdFormatException("illegal character encoding",
                                         this.source.getPosition() );
        }

        this.charBuffer.flip();
        String result = this.charBuffer.toString();

        if(result.indexOf(UCSYEN) >= 0){
            result = result.replace(UCSYEN, SJISYEN);
        }

        return result;
    }

}
