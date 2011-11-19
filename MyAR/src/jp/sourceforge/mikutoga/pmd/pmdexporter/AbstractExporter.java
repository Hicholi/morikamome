/*
 * abstract exporter
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdexporter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * 抽象化されたエクスポーター共通部。
 */
public abstract class AbstractExporter {

    private static final Charset CS_WIN31J = Charset.forName("windows-31j");

    private static final String ERRMSG_TOOLONGTEXT = "too long text";
    private static final String ERRMSG_INVUCSSEQ = "invalid unicode sequence";
    private static final String ERRMSG_NONWIN31J = "no character in win31j";

    private static final int BYTES_SHORT = Short  .SIZE / Byte.SIZE;
    private static final int BYTES_INT   = Integer.SIZE / Byte.SIZE;
    private static final int BYTES_FLOAT = Float  .SIZE / Byte.SIZE;

    private static final int BUFSZ_BYTE = 512;
    private static final int BUFSZ_CHAR = 512;

    private final OutputStream ostream;

    private final byte[] barray;
    private final ByteBuffer bbuf;

    private final CharBuffer cbuf;
    private final CharsetEncoder encoder;


    /**
     * コンストラクタ。
     * @param stream 出力ストリーム
     * @throws NullPointerException 引数がnull
     */
    protected AbstractExporter(OutputStream stream)
            throws NullPointerException{
        super();

        if(stream == null) throw new NullPointerException();
        this.ostream = stream;

        this.barray = new byte[BUFSZ_BYTE];
        this.bbuf = ByteBuffer.wrap(this.barray);
        this.bbuf.order(ByteOrder.LITTLE_ENDIAN);

        this.cbuf = CharBuffer.allocate(BUFSZ_CHAR);
        this.encoder = CS_WIN31J.newEncoder();
        this.encoder.onMalformedInput(CodingErrorAction.REPORT);
        this.encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        this.encoder.reset();

        return;
    }

    /**
     * 出力をフラッシュする。
     * I/O効率とデバッグ効率のバランスを考え、ご利用は計画的に。
     * @throws IOException 出力エラー
     */
    protected void flush() throws IOException{
        this.ostream.flush();
        return;
    }

    /**
     * byte値を出力する。
     * @param bVal byte値
     * @throws IOException 出力エラー
     */
    protected void dumpByte(byte bVal) throws IOException{
        this.ostream.write((int)bVal);
        return;
    }

    /**
     * int値をbyte値で出力する。
     * byte値に収まらない上位ビットはそのまま捨てられる。
     * @param iVal int値
     * @throws IOException 出力エラー
     */
    protected void dumpByte(int iVal) throws IOException{
        dumpByte((byte)iVal);
        return;
    }

    /**
     * short値を出力する。
     * @param sVal short値
     * @throws IOException 出力エラー
     */
    protected void dumpShort(short sVal) throws IOException{
        this.bbuf.clear();
        this.bbuf.putShort(sVal);
        this.ostream.write(this.barray, 0, BYTES_SHORT);
        return;
    }

    /**
     * int値をshort値で出力する。
     * short値に収まらない上位ビットはそのまま捨てられる。
     * @param iVal int値
     * @throws IOException 出力エラー
     */
    protected void dumpShort(int iVal) throws IOException{
        dumpShort((short)iVal);
        return;
    }

    /**
     * int値を出力する。
     * @param iVal int値
     * @throws IOException 出力エラー
     */
    protected void dumpInt(int iVal) throws IOException{
        this.bbuf.clear();
        this.bbuf.putInt(iVal);
        this.ostream.write(this.barray, 0, BYTES_INT);
        return;
    }

    /**
     * float値を出力する。
     * @param fVal float値
     * @throws IOException 出力エラー
     */
    protected void dumpFloat(float fVal) throws IOException{
        this.bbuf.clear();
        this.bbuf.putFloat(fVal);
        this.ostream.write(this.barray, 0, BYTES_FLOAT);
        return;
    }

    /**
     * 文字列をwindows-31jエンコーディングで出力する。
     * @param seq 文字列
     * @return 出力されたbyte総数。
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException 文字エンコーディングに関するエラー
     */
    protected int dumpCharSequence(CharSequence seq)
            throws IOException, IllegalPmdTextException{
        encodeToByteBuffer(seq);
        this.bbuf.flip();
        int length = dumpByteBuffer();
        return length;
    }

    /**
     * windows-31jにエンコーディングした文字列を内部バッファに蓄積する。
     * @param seq 文字列
     * @throws IllegalPmdTextException 文字エンコーディングに関するエラー。
     * 考えられる状況としては、
     * <ul>
     * <li>入力文字列がUnicodeとしてすでにおかしい。
     * (サロゲートペアがペアになってないなど)
     * <li>windows-31jにない文字をエンコーディングしようとした
     * <li>エンコーディング結果が内部バッファに収まらなかった。
     * </ul>
     * など。
     */
    private void encodeToByteBuffer(CharSequence seq)
            throws IllegalPmdTextException{
        this.cbuf.clear();
        try{
            this.cbuf.append(seq);
        }catch(BufferOverflowException e){
            throw new IllegalPmdTextException(ERRMSG_TOOLONGTEXT);
        }
        this.cbuf.flip();

        this.bbuf.clear();

        CoderResult result = this.encoder.encode(this.cbuf, this.bbuf, true);
        if( ! result.isUnderflow()){
            if(result.isOverflow()){
                throw new IllegalPmdTextException(ERRMSG_TOOLONGTEXT);
            }else if(result.isMalformed()){
                throw new IllegalPmdTextException(ERRMSG_INVUCSSEQ);
            }else if(result.isUnmappable()){
                throw new IllegalPmdTextException(ERRMSG_NONWIN31J);
            }else{
                assert false;
                throw new AssertionError();
            }
        }

        return;
    }

    /**
     * 内部バッファに蓄積されたbyte値並びを出力する。
     * @return 出力されたbyte数
     * @throws IOException 出力エラー
     */
    private int dumpByteBuffer() throws IOException{
        int offset = this.bbuf.position();
        int limit = this.bbuf.limit();
        int length = limit - offset;
        this.ostream.write(this.barray, offset, length);

        this.bbuf.position(limit);

        return length;
    }

}
