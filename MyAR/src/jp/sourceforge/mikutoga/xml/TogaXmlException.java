/*
 * exception about xml
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.xml;

/**
 *　意図しないXML文書を検出した際の例外。
 */
@SuppressWarnings("serial")
public class TogaXmlException extends Exception{

    /**
     * コンストラクタ。
     */
    public TogaXmlException(){
        super();
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     */
    public TogaXmlException(String message){
        super(message);
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     * @param cause 原因の例外
     */
    public TogaXmlException(String message, Throwable cause){
        super(message, cause);
        return;
    }

    /**
     * コンストラクタ。
     * @param cause 原因の例外
     */
    public TogaXmlException(Throwable cause){
        super(cause);
        return;
    }

}
