/*
 * MMD file input source
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * MMD各種ファイルの入力ソース。
 * 入力ソース終端の判定が可能。
 * パースエラー発生位置(バイト単位)の取得が可能。
 * リトルエンディアン形式で格納された各種プリミティブ型値の解決を行う。
 */
public class MmdSource implements Closeable {

    private static final int BYTES_SHORT = Short  .SIZE / Byte.SIZE;
    private static final int BYTES_INT   = Integer.SIZE / Byte.SIZE;
    private static final int BYTES_FLOAT = Float  .SIZE / Byte.SIZE;
    private static final int BUF_SZ = 4;

    private static final int MASK_8BIT = 0xff;
    private static final int MASK_16BIT = 0xffff;

    static{
        assert BUF_SZ >= BYTES_SHORT;
        assert BUF_SZ >= BYTES_INT;
        assert BUF_SZ >= BYTES_FLOAT;
    }

    private final PushbackInputStream istream;
    private final byte[] readArray;       // 読み込みバッファ
    private final ByteBuffer readBuffer;  // 読み込みバッファの別ビュー
    private long position;                // 読み込み位置

    /**
     * コンストラクタ。
     * @param is 入力ストリーム。
     * I/O効率が考慮されたバッファリングを行うストリームを渡すのが望ましい。
     * @throws NullPointerException ストリーム引数がnull。
     */
    public MmdSource(InputStream is)
            throws NullPointerException {
        super();

        if(is == null) throw new NullPointerException();

        // 読み戻しバッファは1byte確保
        this.istream = new PushbackInputStream(is);

        this.readArray = new byte[BUF_SZ];
        this.readBuffer = ByteBuffer.wrap(this.readArray);
        this.readBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.readBuffer.clear();

        this.position = 0L;

        return;
    }

    /**
     * 今までに読み込みに成功したバイト数を返す。
     * @return 読み込みに成功したバイト数。
     */
    public long getPosition(){
        return this.position;
    }

    /**
     * 入力ソースを読み飛ばす。
     * 入力ソースがディスクファイルに由来する場合、
     * 空読みするより早くなるかも。
     * @param skipLength 読み飛ばすバイト数。
     * @return 実際に読み飛ばしたバイト数。
     * @throws IOException IOエラー
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long skipLength)
            throws IOException{
        if(skipLength <= 0L) return 0L;

        long remain = skipLength;
        while(remain > 0L){      // BufferedInputStream対策
            long result = this.istream.skip(remain);
            if(result <= 0L) break;
            this.position += result;
            remain -= result;
        }

        return skipLength - remain;
    }

    /**
     * 入力ソースにまだデータが残っているか判定する。
     * @return まだ読み込んでいないデータが残っていればtrue
     * @throws IOException IOエラー
     */
    public boolean hasMore() throws IOException{
        int bData = this.istream.read();
        if(bData < 0){
            return false;
        }

        this.istream.unread(bData);

        return true;
    }

    /**
     * 入力ソースを閉じる。
     * 読み込み済みバイト数の情報は保持される。
     * @throws IOException IOエラー
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException{
        this.istream.close();
        this.readBuffer.clear();
        return;
    }

    /**
     * 指定したバイト数だけ内部バッファに読み込む。
     * @param fillSize 読み込むバイト数
     * @throws IOException IOエラー
     * @throws IndexOutOfBoundsException 引数がバッファサイズと矛盾。
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    protected void fillBuffer(int fillSize)
            throws IOException, IndexOutOfBoundsException, MmdEofException{
        int result = this.istream.read(this.readArray, 0, fillSize);
        if(result >= 0){
            this.position += result;
        }

        if(result != fillSize){
            throw new MmdEofException(this.position);
        }

        this.readBuffer.rewind();

        return;
    }

    /**
     * byte値を読み込む。
     * @return 読み込んだbyte値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public byte parseByte() throws IOException, MmdEofException{
        int bData = this.istream.read();
        if(bData < 0){
            throw new MmdEofException(this.position);
        }else{
            this.position++;
        }

        byte result = (byte) bData;
        return result;
    }

    /**
     * 符号無し値としてbyte値を読み込み、int型に変換して返す。
     * 符号は拡張されない。(0xffは0x000000ffとなる)
     * @return 読み込まれた値のint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public int parseUByteAsInteger()
            throws IOException, MmdEofException{
        return ((int) parseByte()) & MASK_8BIT;
    }

    /**
     * byte値を読み込み、boolean型に変換して返す。
     * 0x00は偽、それ以外は真と解釈される。
     * @return 読み込まれた値のboolean値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public boolean parseBoolean() throws IOException, MmdEofException{
        byte result = parseByte();
        if(result == 0x00) return false;
        return true;
    }

    /**
     * short値を読み込む。
     * short値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだshort値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public short parseShort() throws IOException, MmdEofException{
        fillBuffer(BYTES_SHORT);
        short result = this.readBuffer.getShort();
        return result;
    }

    /**
     * 符号無し値としてshort値を読み込み、int型に変換して返す。
     * 符号は拡張されない。(0xffffは0x0000ffffとなる)
     * short値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込まれた値のint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public int parseUShortAsInteger()
            throws IOException, MmdEofException{
        return ((int) parseShort()) & MASK_16BIT;
    }

    /**
     * int値を読み込む。
     * int値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだint値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public int parseInteger() throws IOException, MmdEofException{
        fillBuffer(BYTES_INT);
        int result = this.readBuffer.getInt();
        return result;
    }

    /**
     * float値を読み込む。
     * float値はリトルエンディアンで格納されていると仮定される。
     * @return 読み込んだfloat値
     * @throws IOException IOエラー
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public float parseFloat() throws IOException, MmdEofException{
        fillBuffer(BYTES_FLOAT);
        float result = this.readBuffer.getFloat();
        return result;
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
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public void parseByteArray(byte[] dst, int offset, int length)
            throws IOException,
                   NullPointerException,
                   IndexOutOfBoundsException,
                   MmdEofException {
        int result = this.istream.read(dst, offset, length);
        if(result >= 0){
            this.position += result;
        }

        if(result != length){
            throw new MmdEofException(this.position);
        }

        return;
    }

    /**
     * byte配列を読み込む。
     * @param dst 格納先配列
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public void parseByteArray(byte[] dst)
            throws IOException, NullPointerException, MmdEofException{
        parseByteArray(dst, 0, dst.length);
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
     */
    public void parseFloatArray(float[] dst, int offset, int length)
            throws IOException,
                   NullPointerException,
                   IndexOutOfBoundsException,
                   MmdEofException {
        if(offset < 0 || length < 0 || dst.length - offset < length){
            throw new IndexOutOfBoundsException();
        }

        for(int idx = 0; idx < length; idx++){
            dst[offset+idx] = parseFloat();
        }

        return;
    }

    /**
     * float配列を読み込む。
     * @param dst 格納先配列
     * @throws IOException IOエラー
     * @throws NullPointerException 配列がnull
     * @throws MmdEofException 読み込む途中でストリーム終端に達した。
     */
    public void parseFloatArray(float[] dst)
            throws IOException, NullPointerException, MmdEofException{
        parseFloatArray(dst, 0, dst.length);
        return;
    }

    // TODO ビッグエンディアン対応が今後必要になる状況はありうるか？
}
