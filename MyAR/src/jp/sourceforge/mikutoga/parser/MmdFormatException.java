/*
 * unexpected file format founded exception
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

/**
 * MMD関連ファイルのパース異常系。
 * 必要に応じて、パースに失敗した位置を保持する。
 */
@SuppressWarnings("serial")
public class MmdFormatException extends Exception {

    private final long position;

    /**
     * コンストラクタ。
     */
    public MmdFormatException(){
        this(null);
        return;
    }

    /**
     * コンストラクタ。
     * @param message エラーメッセージ
     */
    public MmdFormatException(String message){
        this(message, -1L);
        return;
    }

    /**
     * コンストラクタ。
     * @param position 入力ソース先頭から数えたエラー位置。(バイト単位)
     * 負の値を与えると、エラー位置は無効と解釈される。
     */
    public MmdFormatException(long position){
        this(null, position);
        return;
    }

    /**
     * コンストラクタ。
     * @param message エラーメッセージ
     * @param position 入力ソース先頭から数えたエラー位置。(バイト単位)
     * 負の値を与えると、エラー位置は無効と解釈される。
     */
    public MmdFormatException(String message, long position){
        super(message);
        this.position = position;
        return;
    }

    /**
     * {@inheritDoc}
     * 有効なエラー発生位置を保持している場合、追加出力される。
     * @return {@inheritDoc}
     */
    @Override
    public String getMessage(){
        StringBuilder result = new StringBuilder();

        String msg = super.getMessage();
        if(msg != null) result.append(msg);

        if(hasPosition()){
            result.append('(')
                  .append("position:")
                  .append(this.position)
                  .append(')');
        }

        if(result.length() <= 0) return null;

        return result.toString();
    }

    /**
     * エラー位置を取得する。
     * @return 入力ソース先頭からのバイト数で表されるエラー位置。
     * 負なら無効なエラー位置。
     */
    public long getPosition(){
        return this.position;
    }

    /**
     * 有効なエラー位置が設定されているか判定する。
     * @return エラー位置が有効(非負)ならtrue
     */
    public boolean hasPosition(){
        if(this.position < 0L) return false;
        return true;
    }

}
