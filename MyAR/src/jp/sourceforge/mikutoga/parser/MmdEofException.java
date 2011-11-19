/*
 * unexpected file EOF founded exception
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

/**
 * 入力ソースが予期せずに終了した異常系。
 */
@SuppressWarnings("serial")
public class MmdEofException extends MmdFormatException {

    /**
     * コンストラクタ。
     */
    public MmdEofException(){
        this(null);
        return;
    }

    /**
     * コンストラクタ。
     * @param message エラーメッセージ
     */
    public MmdEofException(String message){
        this(message, -1L);
        return;
    }

    /**
     * コンストラクタ。
     * @param position 入力ソース先頭からのエラー位置。(バイト単位)
     * 負の値を与えると、エラー位置は無効と解釈される。
     */
    public MmdEofException(long position){
        this(null, position);
        return;
    }

    /**
     * コンストラクタ。
     * @param message エラーメッセージ
     * @param position 入力ソース先頭からのエラー位置。(バイト単位)
     * 負の値を与えると、エラー位置は無効と解釈される。
     */
    public MmdEofException(String message, long position){
        super(message, position);
        return;
    }

}
