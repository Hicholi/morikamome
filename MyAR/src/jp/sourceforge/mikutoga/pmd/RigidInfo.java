/*
 * rigid information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.ArrayList;
import java.util.Collection;
import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * 個別の剛体の情報。
 */
public class RigidInfo implements SerialNumbered {

    private final I18nText rigidName = new I18nText();

    private RigidBehaviorType behaviorType = RigidBehaviorType.FOLLOWBONE;

    private final RigidShape rigidShape = new RigidShape();
    private final Pos3d position = new Pos3d();
    private final Rad3d rotation = new Rad3d();

    private BoneInfo linkedBone;

    private final DynamicsInfo dynamicsInfo = new DynamicsInfo();

    private final Collection<RigidGroup> throughGroupColl =
            new ArrayList<RigidGroup>();

    private RigidGroup rigidGroup;

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public RigidInfo(){
        super();
        return;
    }

    /**
     * 剛体名を返す。
     * @return 剛体名
     */
    public I18nText getRigidName(){
        return this.rigidName;
    }

    /**
     * 剛体の振る舞い種別を返す。
     * @return 剛体の振る舞い種別
     */
    public RigidBehaviorType getBehaviorType(){
        return this.behaviorType;
    }

    /**
     * 剛体の振る舞い種別を設定する。
     * @param type 剛体の振る舞い種別。
     * @throws NullPointerException 引数がnull
     */
    public void setBehaviorType(RigidBehaviorType type)
            throws NullPointerException{
        if(type == null) throw new NullPointerException();
        this.behaviorType = type;
        return;
    }

    /**
     * 剛体形状を返す。
     * @return 剛体形状
     */
    public RigidShape getRigidShape(){
        return this.rigidShape;
    }

    /**
     * 剛体位置を返す。
     * @return 剛体位置
     */
    public Pos3d getPosition(){
        return this.position;
    }

    /**
     * 剛体姿勢を返す。
     * @return 剛体姿勢
     */
    public Rad3d getRotation(){
        return this.rotation;
    }

    /**
     * 接続ボーンを返す。
     * @return 接続ボーン
     */
    public BoneInfo getLinkedBone(){
        return this.linkedBone;
    }

    /**
     * 接続ボーンを設定する。
     * @param bone 接続ボーン
     */
    public void setLinkedBone(BoneInfo bone){
        this.linkedBone = bone;
        return;
    }

    /**
     * 剛体の力学パラメータを返す。
     * @return 力学パラメータ
     */
    public DynamicsInfo getDynamicsInfo(){
        return this.dynamicsInfo;
    }

    /**
     * 非衝突グループ集合を返す。
     * @return 非衝突グループ集合
     */
    public Collection<RigidGroup> getThroughGroupColl(){
        return this.throughGroupColl;
    }

    /**
     * 所属する剛体グループを返す。
     * @return 所属する剛体グループ
     */
    public RigidGroup getRigidGroup(){
        return this.rigidGroup;
    }

    /**
     * 所属する剛体グループを設定する。
     * @param group 所属する剛体グループ
     */
    public void setRigidGroup(RigidGroup group){
        this.rigidGroup = group;
        return;
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

        String boneName;
        if(this.linkedBone == null){
            boneName = "NOBONE";
        }else{
            boneName = this.linkedBone.getBoneName().toString();
        }

        result.append("Rigid(").append(this.rigidName).append(") ");
        result.append("[=>")
              .append(boneName)
              .append("bone]");
        result.append(" [").append(this.rigidShape).append("]");
        result.append(" ").append(this.position);
        result.append(" ").append(this.rotation);
        result.append(" [").append(this.dynamicsInfo).append("]");
        result.append("  [").append(this.behaviorType).append("]");

        result.append(" through[");
        boolean dumped = false;
        for(RigidGroup group : this.throughGroupColl){
            if(dumped) result.append(" ");
            result.append(group.getGroupNumber());
            dumped = true;
        }
        result.append("]");

        return result.toString();
    }

}
