/*
 * illegal text in model exception
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdexporter;

/**
 * 不正なテキスト情報をモデルデータ中に発見した場合の例外。
 * <p>
 * 考えられる理由としては
 * <ul>
 * <li>用意されたフォーマットに対し文字列が長すぎる。
 * <li>文字エンコーディングできない文字が含まれている
 * <li>ユニコード文字列として既に変。
 * </ul>
 * など。
 */
@SuppressWarnings("serial")
public class IllegalPmdTextException extends IllegalPmdException{

    /**
     * コンストラクタ。
     */
    public IllegalPmdTextException(){
        super();
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     */
    public IllegalPmdTextException(String message){
        super(message);
        return;
    }

}
