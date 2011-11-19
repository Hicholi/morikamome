/*
 * morph type
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * モーフ種別。
 * <ul>
 * <li>0:base
 * <li>1:まゆ
 * <li>2:目
 * <li>3:リップ
 * <li>4:その他
 * </ul>
 */
public enum MorphType {

    /** base。 */
    BASE(0x00),
    /** まゆ。 */
    EYEBROW(0x01),
    /** 目。 */
    EYE(0x02),
    /** リップ。 */
    LIP(0x03),
    /** その他。 */
    EXTRA(0x04),
    ;

    private static final String FAMILY_NAME =
            "jp.sourceforge.mikutoga.pmd.resources.MorphTypeName";

    private final byte encoded;

    /**
     * コンストラクタ。
     * @param code 符号化int値
     */
    private MorphType(int code){
        this((byte)code);
        return;
    }

    /**
     * コンストラクタ。
     * @param code 符号化byte値
     */
    private MorphType(byte code){
        this.encoded = code;
        return;
    }

    /**
     * byte値からデコードする。
     * @param code byte値
     * @return デコードされた列挙子。該当するものがなければnull
     */
    public static MorphType decode(byte code){
        MorphType result = null;

        for(MorphType type : values()){
            if(type.encode() == code){
                result = type;
                break;
            }
        }

        return result;
    }

    /**
     * byte値にエンコードする。
     * @return byte値
     */
    public byte encode(){
        return this.encoded;
    }

    /**
     * デフォルトロケールでの表示名を返す。
     * @return 表示名
     */
    public String getGuiName(){
        Locale locale = Locale.getDefault();
        return getGuiName(locale);
    }

    /**
     * ロケールに準じた表示名を返す。
     * @param locale ロケール。nullならデフォルトロケールと解釈される。
     * @return 表示名
     */
    public String getGuiName(Locale locale){
        if(locale == null) return getGuiName();
        ResourceBundle rb = ResourceBundle.getBundle(FAMILY_NAME, locale);
        String key = name();
        String result = rb.getString(key);
        return result;
    }

    /**
     * モーフ種別がbaseか否か判定する。
     * @return baseならtrue
     */
    public boolean isBase(){
        if(this == BASE) return true;
        return false;
    }

}
