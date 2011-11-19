/*
 * building joint information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.ListUtil;
import jp.sourceforge.mikutoga.parser.ParseStage;
import jp.sourceforge.mikutoga.parser.pmd.PmdJointHandler;
import jp.sourceforge.mikutoga.pmd.Deg3d;
import jp.sourceforge.mikutoga.pmd.JointInfo;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.Pos3d;
import jp.sourceforge.mikutoga.pmd.Rad3d;
import jp.sourceforge.mikutoga.pmd.RigidInfo;
import jp.sourceforge.mikutoga.pmd.TripletRange;

/**
 * ジョイント関係の通知をパーサから受け取る。
 */
class JointBuilder implements PmdJointHandler {

    private final List<RigidInfo> rigidList;

    private final List<JointInfo> jointList;
    private Iterator<JointInfo> jointIt;
    private JointInfo currentJoint = null;

    /**
     * コンストラクタ。
     * @param model モデル
     */
    JointBuilder(PmdModel model){
        super();
        this.rigidList = model.getRigidList();
        this.jointList = model.getJointList();
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     * @param loops {@inheritDoc}
     */
    @Override
    public void loopStart(ParseStage stage, int loops){
        assert stage == PmdJointHandler.JOINT_LIST;

        ListUtil.prepareDefConsList(this.jointList, JointInfo.class, loops);

        this.jointIt = this.jointList.iterator();
        if(this.jointIt.hasNext()){
            this.currentJoint = this.jointIt.next();
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopNext(ParseStage stage){
        assert stage == PmdJointHandler.JOINT_LIST;

        if(this.jointIt.hasNext()){
            this.currentJoint = this.jointIt.next();
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopEnd(ParseStage stage){
        assert stage == PmdJointHandler.JOINT_LIST;
        return;
    }

    /**
     * {@inheritDoc}
     * @param jointName {@inheritDoc}
     */
    @Override
    public void pmdJointName(String jointName){
        this.currentJoint.getJointName().setPrimaryText(jointName);
        return;
    }

    /**
     * {@inheritDoc}
     * @param rigidIdA {@inheritDoc}
     * @param rigidIdB {@inheritDoc}
     */
    @Override
    public void pmdJointLink(int rigidIdA, int rigidIdB){
        RigidInfo rigidA = this.rigidList.get(rigidIdA);
        RigidInfo rigidB = this.rigidList.get(rigidIdB);
        this.currentJoint.setRigidPair(rigidA, rigidB);
        return;
    }

    /**
     * {@inheritDoc}
     * @param posX {@inheritDoc}
     * @param posY {@inheritDoc}
     * @param posZ {@inheritDoc}
     */
    @Override
    public void pmdJointPosition(float posX, float posY, float posZ){
        Pos3d position = this.currentJoint.getPosition();
        position.setXPos(posX);
        position.setYPos(posY);
        position.setZPos(posZ);
        return;
    }

    /**
     * {@inheritDoc}
     * @param rotX {@inheritDoc}
     * @param rotY {@inheritDoc}
     * @param rotZ {@inheritDoc}
     */
    @Override
    public void pmdJointRotation(float rotX, float rotY, float rotZ){
        Rad3d rotation = this.currentJoint.getRotation();
        rotation.setXRad(rotX);
        rotation.setYRad(rotY);
        rotation.setZRad(rotZ);
        return;
    }

    /**
     * {@inheritDoc}
     * @param posXlim1 {@inheritDoc}
     * @param posXlim2 {@inheritDoc}
     * @param posYlim1 {@inheritDoc}
     * @param posYlim2 {@inheritDoc}
     * @param posZlim1 {@inheritDoc}
     * @param posZlim2 {@inheritDoc}
     */
    @Override
    public void pmdPositionLimit(float posXlim1, float posXlim2,
                                 float posYlim1, float posYlim2,
                                 float posZlim1, float posZlim2){
        TripletRange range = this.currentJoint.getPositionRange();
        range.setXRange(posXlim1, posXlim2);
        range.setYRange(posYlim1, posYlim2);
        range.setZRange(posZlim1, posZlim2);
        return;
    }

    /**
     * {@inheritDoc}
     * @param rotXlim1 {@inheritDoc}
     * @param rotXlim2 {@inheritDoc}
     * @param rotYlim1 {@inheritDoc}
     * @param rotYlim2 {@inheritDoc}
     * @param rotZlim1 {@inheritDoc}
     * @param rotZlim2 {@inheritDoc}
     */
    @Override
    public void pmdRotationLimit(float rotXlim1, float rotXlim2,
                                 float rotYlim1, float rotYlim2,
                                 float rotZlim1, float rotZlim2){
        TripletRange range = this.currentJoint.getRotationRange();
        range.setXRange(rotXlim1, rotXlim2);
        range.setYRange(rotYlim1, rotYlim2);
        range.setZRange(rotZlim1, rotZlim2);
        return;
    }

    /**
     * {@inheritDoc}
     * @param elasticPosX {@inheritDoc}
     * @param elasticPosY {@inheritDoc}
     * @param elasticPosZ {@inheritDoc}
     */
    @Override
    public void pmdElasticPosition(float elasticPosX,
                                   float elasticPosY,
                                   float elasticPosZ){
        Pos3d position = this.currentJoint.getElasticPosition();
        position.setXPos(elasticPosX);
        position.setYPos(elasticPosY);
        position.setZPos(elasticPosZ);
        return;
    }

    /**
     * {@inheritDoc}
     * @param elasticRotX {@inheritDoc}
     * @param elasticRotY {@inheritDoc}
     * @param elasticRotZ {@inheritDoc}
     */
    @Override
    public void pmdElasticRotation(float elasticRotX,
                                   float elasticRotY,
                                   float elasticRotZ){
        Deg3d rotation = this.currentJoint.getElasticRotation();
        rotation.setXDeg(elasticRotX);
        rotation.setYDeg(elasticRotY);
        rotation.setZDeg(elasticRotZ);
        return;
    }

}
