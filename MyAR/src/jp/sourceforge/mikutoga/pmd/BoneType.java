/*
 * bone type
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ボーン種別。
 * <ul>
 * <li>0x00:回転
 * <li>0x01:回転/移動
 * <li>0x02:IK
 * <li>0x03:不明
 * <li>0x04:IK影響下(回転)
 * <li>0x05:回転影響下
 * <li>0x06:IK接続先
 * <li>0x07:非表示
 * <li>0x08:捩り
 * <li>0x09:回転連動
 * </ul>
 */
public enum BoneType {

    /** 回転。 */
    ROTATE(0x00),
    /** 回転/移動。 */
    ROTMOV(0x01),
    /** IK。 */
    IK(0x02),
    /** 不明。 */
    UNKNOWN(0x03),
    /** IK影響下(回転)。 */
    UNDERIK(0x04),
    /** 回転影響下。 */
    UNDERROT(0x05),
    /** IK接続先。 */
    IKCONNECTED(0x06),
    /** 非表示。 */
    HIDDEN(0x07),
    /** 捩り。 */
    TWIST(0x08),
    /** 回転連動。 */
    LINKEDROT(0x09),
    ;

    private static final String FAMILY_NAME =
            "jp.sourceforge.mikutoga.pmd.resources.BoneTypeName";

    private final byte encoded;

    /**
     * コンストラクタ。
     * @param code 符号化int値
     */
    private BoneType(int code){
        this((byte)code);
        return;
    }

    /**
     * コンストラクタ。
     * @param code 符号化byte値
     */
    private BoneType(byte code){
        this.encoded = code;
        return;
    }

    /**
     * byte値からデコードする。
     * @param code byte値
     * @return デコードされた列挙子。該当するものがなければnull
     */
    public static BoneType decode(byte code){
        BoneType result = null;

        for(BoneType type : values()){
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

}
