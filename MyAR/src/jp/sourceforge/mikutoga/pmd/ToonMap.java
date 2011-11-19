/*
 * toon file mapping
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * インデックス化されたトゥーンファイル構成。
 * 既存のトゥーンファイル構成と異なるトゥーンファイル名を用いることが可能。
 * <h1>デフォルトのトゥーンファイル構成。</h1>
 * <ul>
 * <li>0x00:toon01.bmp
 * <li>0x01:toon02.bmp
 * <li>.....
 * <li>0x09:toon10.bmp
 * <li>0xff:toon0.bmp
 * </ul>
 */
public class ToonMap {

    private static final Map<Integer, String> DEF_TOONMAP;

    static{
        Map<Integer, String> map = new TreeMap<Integer, String>();

        map.put(0x00, "toon01.bmp");
        map.put(0x01, "toon02.bmp");
        map.put(0x02, "toon03.bmp");
        map.put(0x03, "toon04.bmp");
        map.put(0x04, "toon05.bmp");
        map.put(0x05, "toon06.bmp");
        map.put(0x06, "toon07.bmp");
        map.put(0x07, "toon08.bmp");
        map.put(0x08, "toon09.bmp");
        map.put(0x09, "toon10.bmp");
        map.put(0xff, "toon0.bmp");

        DEF_TOONMAP = Collections.unmodifiableMap(map);
    }

    private final Map<Integer, String> toonMap =
            new TreeMap<Integer, String>(DEF_TOONMAP);

    /**
     * コンストラクタ。
     */
    public ToonMap(){
        super();
        return;
    }

    /**
     * 指定したインデックス値に対応したトゥーンファイル名を返す。
     * @param idx インデックス値
     * @return トゥーンファイル名。該当するものがなければnull
     */
    public String getIndexedToon(int idx){
        String result = this.toonMap.get(idx);
        return result;
    }

    /**
     * 指定したインデックス値にトゥーンファイル名を設定する。
     * @param idx インデックス値
     * @param toonFileName トゥーンフィル名
     * @throws NullPointerException トゥーンファイル名がnull
     */
    public void setIndexedToon(int idx, String toonFileName)
            throws NullPointerException{
        if(toonFileName == null) throw new NullPointerException();
        this.toonMap.put(idx, toonFileName);
        return;
    }

    /**
     * このトゥーンファイル構成がデフォルトのトゥーンファイル構成と等しいか判定する。
     * @return 等しければtrue
     */
    public boolean isDefaultMap(){
        if(this.toonMap.equals(DEF_TOONMAP)) return true;
        return false;
    }

    /**
     * 指定インデックスのトゥーンファイル名がデフォルトと等しいか判定する。
     * @param idx インデックス
     * @return デフォルトと等しければtrue。
     */
    public boolean isDefaultToon(int idx){
        String thisToon = this.toonMap.get(idx);
        if(thisToon == null) return false;

        String defToon = DEF_TOONMAP.get(idx);
        if(thisToon.equals(defToon)) return true;

        return false;
    }

    /**
     * このトゥーンファイル構成をデフォルト構成内容でリセットする。
     */
    public void resetDefaultMap(){
        this.toonMap.clear();
        this.toonMap.putAll(DEF_TOONMAP);
        return;
    }

    /**
     * 指定インデックスのトゥーンファイル名をデフォルトのトゥーンファイル名にリセットする。
     * @param idx インデックス値
     */
    public void resetIndexedToon(int idx){
        String toonFile = DEF_TOONMAP.get(idx);
        this.toonMap.put(idx, toonFile);
        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        boolean dumped = false;
        for(Map.Entry<Integer, String> entry : this.toonMap.entrySet()){
            Integer idx = entry.getKey();
            String toonFile = entry.getValue();

            if(dumped) result.append(", ");
            result.append('(').append(idx).append(')');
            result.append(toonFile);
            dumped = true;
        }

        return result.toString();
    }

}
