/*
 * bone information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * ボーン情報。
 */
public class BoneInfo implements SerialNumbered {

    private final I18nText boneName = new I18nText();
    private BoneType boneType;

    private BoneInfo prevBone;
    private BoneInfo nextBone;
    private BoneInfo ikBone;

    private final Pos3d position = new Pos3d();

    private int rotationRatio;

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public BoneInfo(){
        super();
        return;
    }

    /**
     * ボーン名を返す。
     * @return ボーン名
     */
    public I18nText getBoneName(){
        return this.boneName;
    }

    /**
     * ボーン種別を設定する。
     * @param type ボーン種別
     * @throws NullPointerException 引数がnull
     */
    public void setBoneType(BoneType type) throws NullPointerException{
        if(type == null) throw new NullPointerException();
        this.boneType = type;
        return;
    }

    /**
     * ボーン種別を返す。
     * @return ボーン種別
     */
    public BoneType getBoneType(){
        return this.boneType;
    }

    /**
     * 親(前)ボーンを設定する。
     * @param prevBone 前ボーン。ない場合はnullを指定。
     */
    public void setPrevBone(BoneInfo prevBone){
        this.prevBone = prevBone;
        return;
    }

    /**
     * 親(前)ボーンを返す。
     * @return 前ボーン。ない場合はnullを返す。
     */
    public BoneInfo getPrevBone(){
        return this.prevBone;
    }

    /**
     * 子(次)ボーンを設定する。
     * 捩りボーンでは軸方向に位置するボーン、
     * 回転連動ボーンでは影響元ボーン。
     * @param nextBone 次ボーン。ない場合はnullを指定。
     */
    public void setNextBone(BoneInfo nextBone){
        this.nextBone = nextBone;
        return;
    }

    /**
     * 子(次)ボーンを返す。
     * 捩りボーンでは軸方向に位置するボーン、
     * 回転連動ボーンでは影響元ボーン。
     * @return 次ボーン。ない場合はnullを返す。
     */
    public BoneInfo getNextBone(){
        return this.nextBone;
    }

    /**
     * このボーンが影響を受けるIKボーンを設定する。
     * @param ikBoneArg IKボーン。ない場合はnullを指定。
     */
    public void setIKBone(BoneInfo ikBoneArg){
        this.ikBone = ikBoneArg;
        return;
    }

    /**
     * このボーンが影響を受けるIKボーンを返す。
     * @return IKボーン。ない場合はnull
     */
    public BoneInfo getIKBone(){
        return this.ikBone;
    }

    /**
     * ボーン位置を返す。
     * @return ボーン位置
     */
    public Pos3d getPosition(){
        return this.position;
    }

    /**
     * 回転連動の影響度を返す。
     * 回転連動ボーンの場合のみ有効。
     * @return 回転連動の影響度
     */
    public int getRotationRatio(){
        return this.rotationRatio;
    }

    /**
     * 回転連動の影響度を設定する。
     * 回転連動ボーンの場合のみ有効。
     * @param ratio 回転連動の影響度
     */
    public void setRotationRatio(int ratio){
        this.rotationRatio = ratio;
    }

    /**
     * {@inheritDoc}
     * @param num {@inheritDoc}
     */
    @Override
    public void setSerialNumber(int num){
        this.serialNo = num;
        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int getSerialNumber(){
        return this.serialNo;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("Bone")
              .append(this.serialNo)
              .append("(")
              .append(this.boneName.getPrimaryText())
              .append(")");

        result.append(" type=")
              .append(this.boneType);

        result.append(" prev=");
        if(this.prevBone != null) result.append(this.prevBone.getBoneName());
        else                      result.append("NONE");

        result.append(" next=");
        if(this.nextBone != null) result.append(this.nextBone.getBoneName());
        else                      result.append("NONE");

        if(this.boneType == BoneType.LINKEDROT){
            result.append(" rotraio=").append(this.rotationRatio);
        }else{
            result.append(" ik=");
            if(this.ikBone != null) result.append(this.ikBone.getBoneName());
            else                    result.append("NONE");
        }

        result.append(" ").append(this.position);

        return result.toString();
    }

}
