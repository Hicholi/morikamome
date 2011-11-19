/*
 * building rigid information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.ListUtil;
import jp.sourceforge.mikutoga.parser.ParseStage;
import jp.sourceforge.mikutoga.parser.pmd.PmdLimits;
import jp.sourceforge.mikutoga.parser.pmd.PmdRigidHandler;
import jp.sourceforge.mikutoga.pmd.BoneInfo;
import jp.sourceforge.mikutoga.pmd.DynamicsInfo;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.Pos3d;
import jp.sourceforge.mikutoga.pmd.Rad3d;
import jp.sourceforge.mikutoga.pmd.RigidBehaviorType;
import jp.sourceforge.mikutoga.pmd.RigidGroup;
import jp.sourceforge.mikutoga.pmd.RigidInfo;
import jp.sourceforge.mikutoga.pmd.RigidShape;
import jp.sourceforge.mikutoga.pmd.RigidShapeType;

/**
 * 剛体関係の通知をパーサから受け取る。
 */
class RigidBuilder implements PmdRigidHandler {

    private final List<BoneInfo> boneList;

    private final List<RigidInfo> rigidList;
    private Iterator<RigidInfo> rigidIt;
    private RigidInfo currentRigid = null;

    private final List<RigidGroup> rigidGroupList;

    /**
     * コンストラクタ。
     * @param model モデル
     */
    RigidBuilder(PmdModel model){
        super();
        this.boneList = model.getBoneList();
        this.rigidList = model.getRigidList();
        this.rigidGroupList = model.getRigidGroupList();
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     * @param loops {@inheritDoc}
     */
    @Override
    public void loopStart(ParseStage stage, int loops){
        ListUtil.prepareDefConsList(this.rigidList, RigidInfo.class, loops);
        ListUtil.assignIndexedSerial(this.rigidList);

        this.rigidIt = this.rigidList.iterator();
        if(this.rigidIt.hasNext()){
            this.currentRigid = this.rigidIt.next();
        }

        ListUtil.prepareDefConsList(this.rigidGroupList,
                                    RigidGroup.class,
                                    PmdLimits.RIGIDGROUP_FIXEDNUM );
        ListUtil.assignIndexedSerial(this.rigidGroupList);

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopNext(ParseStage stage){
        if(this.rigidIt.hasNext()){
            this.currentRigid = this.rigidIt.next();
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopEnd(ParseStage stage){
        return;
    }

    /**
     * {@inheritDoc}
     * @param rigidName {@inheritDoc}
     */
    @Override
    public void pmdRigidName(String rigidName){
        this.currentRigid.getRigidName().setPrimaryText(rigidName);
        return;
    }

    /**
     * {@inheritDoc}
     * @param rigidGroupId {@inheritDoc}
     * @param linkedBoneId {@inheritDoc}
     */
    @Override
    public void pmdRigidInfo(int rigidGroupId, int linkedBoneId){
        BoneInfo bone;
        if(linkedBoneId < 0 || 65535 <= linkedBoneId){
            bone = null;
        }else{
            bone = this.boneList.get(linkedBoneId);
        }
        RigidGroup group = this.rigidGroupList.get(rigidGroupId);

        this.currentRigid.setLinkedBone(bone);
        this.currentRigid.setRigidGroup(group);
        group.getRigidList().add(this.currentRigid);

        return;
    }

    /**
     * {@inheritDoc}
     * @param shapeType {@inheritDoc}
     * @param width {@inheritDoc}
     * @param height {@inheritDoc}
     * @param depth {@inheritDoc}
     */
    @Override
    public void pmdRigidShape(byte shapeType,
                              float width, float height, float depth){
        RigidShape shape = this.currentRigid.getRigidShape();

        shape.setWidth(width);
        shape.setHeight(height);
        shape.setDepth(depth);

        RigidShapeType type = RigidShapeType.decode(shapeType);
        shape.setShapeType(type);

        return;
    }

    /**
     * {@inheritDoc}
     * @param posX {@inheritDoc}
     * @param posY {@inheritDoc}
     * @param posZ {@inheritDoc}
     */
    @Override
    public void pmdRigidPosition(float posX, float posY, float posZ){
        Pos3d position = this.currentRigid.getPosition();
        position.setXPos(posX);
        position.setYPos(posY);
        position.setZPos(posZ);
        return;
    }

    /**
     * {@inheritDoc}
     * @param radX {@inheritDoc}
     * @param radY {@inheritDoc}
     * @param radZ {@inheritDoc}
     */
    @Override
    public void pmdRigidRotation(float radX, float radY, float radZ){
        Rad3d rotation = this.currentRigid.getRotation();
        rotation.setXRad(radX);
        rotation.setYRad(radY);
        rotation.setZRad(radZ);
        return;
    }

    /**
     * {@inheritDoc}
     * @param mass {@inheritDoc}
     * @param dampingPos {@inheritDoc}
     * @param dampingRot {@inheritDoc}
     * @param restitution {@inheritDoc}
     * @param friction {@inheritDoc}
     */
    @Override
    public void pmdRigidPhysics(float mass,
                                  float dampingPos,
                                  float dampingRot,
                                  float restitution,
                                  float friction ){
        DynamicsInfo info = this.currentRigid.getDynamicsInfo();

        info.setMass(mass);
        info.setDampingPosition(dampingPos);
        info.setDampingRotation(dampingRot);
        info.setRestitution(restitution);
        info.setFriction(friction);

        return;
    }

    /**
     * {@inheritDoc}
     * @param behaveType {@inheritDoc}
     * @param collisionMap {@inheritDoc}
     */
    @Override
    public void pmdRigidBehavior(byte behaveType, short collisionMap){
        RigidBehaviorType type = RigidBehaviorType.decode(behaveType);
        this.currentRigid.setBehaviorType(type);

        for(int bitPos = 0; bitPos < PmdLimits.RIGIDGROUP_FIXEDNUM; bitPos++){
            short mask = 0x0001;
            mask <<= bitPos;
            if((collisionMap & mask) == 0){
                RigidGroup group = this.rigidGroupList.get(bitPos);
                this.currentRigid.getThroughGroupColl().add(group);
            }
        }

        return;
    }

}
