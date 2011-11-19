/*
 * Windows File utils
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.corelib;

/**
 * Windowsに特化したFileユーティリティ。
 */
public final class WinFile {

    public static final char SEPARATOR_CHAR = '\\';  // \
    public static final String SEPARATOR =
            Character.toString(SEPARATOR_CHAR);
    public static final String PFX_UNC =
            SEPARATOR + SEPARATOR;                   // \\

    static{
        assert '\\' == 0x005c;
    }

    /**
     * 隠しコンストラクタ。
     */
    private WinFile(){
        assert false;
        throw new AssertionError();
    }

    /**
     * Windowsファイル名の正規化を行う。
     * UNCも考慮される。
     * 相対パスは相対パスのまま。
     * <ul>
     * <li>頭の3回以上連続する\記号は2個の\記号に置き換えられる。
     * <li>末尾の1回以上連続する\記号は削除。
     * ただし頭から連続している場合は削除しない。
     * <li>2回以上連続する\記号は1個の\記号にまとめられる。
     * ただし頭から連続している場合はまとめない。
     * </ul>
     * @param seq 対象ファイル名
     * @return 正規化されたファイル名
     */
    public static String normalizeWinFileName(CharSequence seq){
        String text = seq.toString();
        text = text.replaceAll("^\\\\{3,}", "\\\\\\\\");
        text = text.replaceAll("(.*[^\\\\])\\\\+$", "$1");
        text = text.replaceAll("([^\\\\])\\\\{2,}", "$1\\\\");
        return text;
    }

}
