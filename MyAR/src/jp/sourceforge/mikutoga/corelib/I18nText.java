/*
 * international text
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.corelib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 多言語のバリアントを持つ文字列情報。
 * <ul>
 * <li>プライマリ：識別子にはこちらを使う。基本は日本語。
 * <li>グローバル：基本は英語。UCS Basic-Latinオンリーの貧弱な言語環境でも
 * 読める文字列が望ましい。
 * <li>その他：必要に応じて好きな言語を。
 * </ul>
 */
public class I18nText implements CharSequence {

    /** プライマリ言語のロケール。 */
    public static final Locale LOCALE_PRIMARY = Locale.JAPANESE;
    /** プライマリ言語の言語コード。 */
    public static final String CODE639_PRIMARY = LOCALE_PRIMARY.getLanguage();

    /** グローバル言語のロケール。 */
    public static final Locale LOCALE_GLOBAL = Locale.ENGLISH;
    /** グローバル言語の言語コード。 */
    public static final String CODE639_GLOBAL = LOCALE_GLOBAL.getLanguage();

    static{
        assert CODE639_PRIMARY.equals("ja");
        assert CODE639_GLOBAL .equals("en");
    }

    private final Map<String, String> nameMap = new HashMap<String, String>();

    /**
     * コンストラクタ。
     */
    public I18nText(){
        super();
        return;
    }

    /**
     * プライマリ文字列の登録。
     * @param seq プライマリ文字列。nullの場合は削除動作
     */
    public void setPrimaryText(CharSequence seq){
        setText(CODE639_PRIMARY, seq);
        return;
    }

    /**
     * グローバル文字列の登録。
     * @param seq グローバル文字列。nullの場合は削除動作
     */
    public void setGlobalText(CharSequence seq){
        setText(CODE639_GLOBAL, seq);
        return;
    }

    /**
     * 任意のロケールに関連付けられた文字列の登録。
     * @param locale ロケール
     * @param seq 文字列。nullの場合は削除動作
     * @throws NullPointerException ロケール引数がnull
     */
    public void setText(Locale locale, CharSequence seq)
            throws NullPointerException{
        String code639 = locale.getLanguage();
        setText(code639, seq);
        return;
    }

    /**
     * 任意の言語コードに関連付けられた文字列の登録。
     * @param code639 ISO639言語コード
     * @param seq 文字列。nullの場合は削除動作
     * @throws NullPointerException 言語コードがnull
     */
    public void setText(String code639, CharSequence seq)
            throws NullPointerException{
        if(code639 == null) throw new NullPointerException();

        if(seq != null){
            String text = seq.toString();
            this.nameMap.put(code639, text);
        }else{
            this.nameMap.remove(code639);
        }

        return;
    }

    /**
     * 言語コードに応じた文字列を返す。
     * @param code639 ISO639言語コード
     * @return 文字列。見つからなければnullを返す。
     * @throws NullPointerException 引数がnull
     */
    public String getText(String code639) throws NullPointerException{
        if(code639 == null) throw new NullPointerException();
        String result = this.nameMap.get(code639);
        return result;
    }

    /**
     * ロケールに応じた文字列を返す。
     * @param locale ロケール
     * @return 文字列。見つからなければnullを返す。
     * @throws NullPointerException 引数がnull
     */
    public String getText(Locale locale) throws NullPointerException{
        String code639 = locale.getLanguage();
        String result = getText(code639);
        return result;
    }

    /**
     * プライマリ文字列を返す。
     * @return 文字列。見つからなければnullを返す。
     */
    public String getPrimaryText(){
        String result = getText(CODE639_PRIMARY);
        return result;
    }

    /**
     * グローバル文字列を返す。
     * @return 文字列。見つからなければnullを返す。
     */
    public String getGlobalText(){
        String result = getText(CODE639_GLOBAL);
        return result;
    }

    /**
     * プライマリ文字列を返す。
     * 見つからなければグローバル文字列を返す。
     * それでも見つからなければ空文字列を返す。
     * @return 文字列
     */
    public String getText(){
        String result;

        result = getPrimaryText();

        if(result == null){
            result = getGlobalText();
        }

        if(result == null){
            result = "";
        }

        return result;
    }

    /**
     * 実行環境のデフォルトロケールに応じた文字列を返す。
     * 見つからなければグローバル文字列、プライマリ文字列の順に返す。
     * それでも見つからなければ適当な言語コードの文字列を返す。
     * それでも見つからなければ空文字列を返す。
     * デフォルトロケールの確認はその都度行われる。
     * @return 文字列
     */
    public String getLocalizedText(){
        Locale locale = Locale.getDefault();
        String langCode = locale.getLanguage();

        String result;

        result = this.nameMap.get(langCode);

        if(result == null){
            result = this.nameMap.get(CODE639_GLOBAL);
        }

        if(result == null){
            result = this.nameMap.get(CODE639_PRIMARY);
        }

        if(result == null){
            Set<String> langSet = this.nameMap.keySet();
            Iterator<String> it = langSet.iterator();
            while(it.hasNext()){
                String lang = it.next();
                result = this.nameMap.get(lang);
                if(result != null) break;
            }
        }

        if(result == null){
            result = "";
        }

        return result;
    }

    /**
     * 全言語の文字列を削除する。
     */
    public void removeAllText(){
        this.nameMap.clear();
        return;
    }

    /**
     * 登録済みの全ISO639言語コードリストを返す。
     * 優先度はプライマリ、グローバル、その他の順。
     * @return 全ISO639言語コード
     */
    public List<String> lang639CodeList(){
        Set<String> set = this.nameMap.keySet();
        List<String> result = new ArrayList<String>(set.size());

        for(String lang : set){
            if(lang.equals(CODE639_PRIMARY)) result.add(lang);
        }

        for(String lang : set){
            if(lang.equals(CODE639_GLOBAL)) result.add(lang);
        }

        for(String lang : set){
            if(lang.equals(CODE639_PRIMARY)) continue;
            if(lang.equals(CODE639_GLOBAL)) continue;
            result.add(lang);
        }

        return result;
    }

    /**
     * プライマリ文字列が登録されているか判定する。
     * @return 登録されていればtrue
     */
    public boolean hasPrimaryText(){
        boolean result = this.nameMap.containsKey(CODE639_PRIMARY);
        return result;
    }

    /**
     * グローバル文字列が登録されているか判定する。
     * @return 登録されていればtrue
     */
    public boolean hasGlobalText(){
        boolean result = this.nameMap.containsKey(CODE639_GLOBAL);
        return result;
    }

    /**
     * {@inheritDoc}
     * {@link #getText()}に準ずる。
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public char charAt(int index){
        String text = getText();
        char result = text.charAt(index);
        return result;
    }

    /**
     * {@inheritDoc}
     * {@link #getText()}に準ずる。
     * @return {@inheritDoc}
     */
    @Override
    public int length(){
        String text = getText();
        int result = text.length();
        return result;
    }

    /**
     * {@inheritDoc}
     * {@link #getText()}に準ずる。
     * @param start {@inheritDoc}
     * @param end {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CharSequence subSequence(int start, int end){
        String text = getText();
        CharSequence result = text.subSequence(start, end);
        return result;
    }

    /**
     * {@inheritDoc}
     * {@link #getText()}に準ずる。
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        return getText();
    }

}
