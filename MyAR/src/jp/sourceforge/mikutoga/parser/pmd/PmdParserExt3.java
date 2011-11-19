/*
 * pmd parser extension 3
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import java.io.IOException;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.MmdSource;

/**
 * PMDモデルファイルのパーサ拡張その3。
 * ※剛体情報対応
 */
public class PmdParserExt3 extends PmdParserExt2 {

    private static final int RIGID_DATA_SZ = 83;
    private static final int JOINT_DATA_SZ = 124;

    private PmdRigidHandler rigidHandler = null;
    private PmdJointHandler jointHandler = null;

    /**
     * コンストラクタ。
     * @param source 入力ソース
     */
    public PmdParserExt3(MmdSource source){
        super(source);
        return;
    }

    /**
     * 剛体ハンドラを登録する。
     * @param handler 剛体ハンドラ
     */
    public void setRigidHandler(PmdRigidHandler handler){
        this.rigidHandler = handler;
        return;
    }

    /**
     * ジョイントハンドラを登録する。
     * @param handler ジョイントハンドラ
     */
    public void setJointHandler(PmdJointHandler handler){
        this.jointHandler = handler;
        return;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @throws MmdFormatException {@inheritDoc}
     */
    @Override
    protected void parseBody()
            throws IOException, MmdFormatException {
        super.parseBody();

        if(hasMore()){
            parseRigidList();
            parseJointList();
        }

        return;
    }

    /**
     * 剛体情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseRigidList() throws IOException, MmdFormatException{
        int rigidNum = parseInteger();

        if(this.rigidHandler == null){
            skip(RIGID_DATA_SZ * rigidNum);
            return;
        }

        this.rigidHandler.loopStart(PmdRigidHandler.RIGID_LIST, rigidNum);

        for(int ct = 0; ct < rigidNum; ct++){
            String rigidName =
                    parseZeroTermString(PmdLimits.MAXBYTES_RIGIDNAME);
            this.rigidHandler.pmdRigidName(rigidName);

            int linkedBoneId = parseUShortAsInteger();
            int rigidGroupId = parseUByteAsInteger();
            short collisionMap = parseShort();
            this.rigidHandler.pmdRigidInfo(rigidGroupId, linkedBoneId);

            byte shapeType = parseByte();
            float width = parseFloat();
            float height = parseFloat();
            float depth = parseFloat();
            this.rigidHandler.pmdRigidShape(shapeType, width, height, depth);

            float posX = parseFloat();
            float posY = parseFloat();
            float posZ = parseFloat();
            this.rigidHandler.pmdRigidPosition(posX, posY, posZ);

            float rotX = parseFloat();
            float rotY = parseFloat();
            float rotZ = parseFloat();
            this.rigidHandler.pmdRigidRotation(rotX, rotY, rotZ);

            float mass = parseFloat();
            float dampingPos = parseFloat();
            float dampingRot = parseFloat();
            float restitution = parseFloat();
            float friction = parseFloat();
            this.rigidHandler.pmdRigidPhysics(mass,
                                              dampingPos, dampingRot,
                                              restitution, friction );

            byte behaveType = parseByte();
            this.rigidHandler.pmdRigidBehavior(behaveType, collisionMap);

            this.rigidHandler.loopNext(PmdRigidHandler.RIGID_LIST);
        }

        this.rigidHandler.loopEnd(PmdRigidHandler.RIGID_LIST);

        return;
    }

    /**
     * ジョイント情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseJointList() throws IOException, MmdFormatException{
        int jointNum = parseInteger();

        if(this.jointHandler == null){
            skip(JOINT_DATA_SZ * jointNum);
            return;
        }

        this.jointHandler.loopStart(PmdJointHandler.JOINT_LIST, jointNum);

        for(int ct = 0; ct < jointNum; ct++){
            String jointName =
                    parseZeroTermString(PmdLimits.MAXBYTES_JOINTNAME);
            this.jointHandler.pmdJointName(jointName);

            int rigidIdA = parseInteger();
            int rigidIdB = parseInteger();
            this.jointHandler.pmdJointLink(rigidIdA, rigidIdB);

            float posX = parseFloat();
            float posY = parseFloat();
            float posZ = parseFloat();
            this.jointHandler.pmdJointPosition(posX, posY, posZ);

            float rotX = parseFloat();
            float rotY = parseFloat();
            float rotZ = parseFloat();
            this.jointHandler.pmdJointRotation(rotX, rotY, rotZ);

            float posXlim1 = parseFloat();
            float posYlim1 = parseFloat();
            float posZlim1 = parseFloat();
            float posXlim2 = parseFloat();
            float posYlim2 = parseFloat();
            float posZlim2 = parseFloat();
            this.jointHandler.pmdPositionLimit(posXlim1, posXlim2,
                                               posYlim1, posYlim2,
                                               posZlim1, posZlim2 );

            float rotXlim1 = parseFloat();
            float rotYlim1 = parseFloat();
            float rotZlim1 = parseFloat();
            float rotXlim2 = parseFloat();
            float rotYlim2 = parseFloat();
            float rotZlim2 = parseFloat();
            this.jointHandler.pmdRotationLimit(rotXlim1, rotXlim2,
                                               rotYlim1, rotYlim2,
                                               rotZlim1, rotZlim2 );

            float elasticPosX = parseFloat();
            float elasticPosY = parseFloat();
            float elasticPosZ = parseFloat();
            this.jointHandler.pmdElasticPosition(elasticPosX,
                                                 elasticPosY,
                                                 elasticPosZ );

            float elasticRotX = parseFloat();
            float elasticRotY = parseFloat();
            float elasticRotZ = parseFloat();
            this.jointHandler.pmdElasticRotation(elasticRotX,
                                                 elasticRotY,
                                                 elasticRotZ );

            this.jointHandler.loopNext(PmdJointHandler.JOINT_LIST);
        }

        this.jointHandler.loopEnd(PmdJointHandler.JOINT_LIST);

        return;
    }

}
