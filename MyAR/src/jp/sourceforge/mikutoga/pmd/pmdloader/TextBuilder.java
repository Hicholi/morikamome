/*
 * building text info
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.parser.ParseStage;
import jp.sourceforge.mikutoga.parser.pmd.PmdBasicHandler;
import jp.sourceforge.mikutoga.parser.pmd.PmdEngHandler;
import jp.sourceforge.mikutoga.pmd.BoneGroup;
import jp.sourceforge.mikutoga.pmd.BoneInfo;
import jp.sourceforge.mikutoga.pmd.MorphPart;
import jp.sourceforge.mikutoga.pmd.PmdModel;

/**
 * テキスト関係の通知をパーサから受け取る。
 */
class TextBuilder implements PmdBasicHandler, PmdEngHandler {

    private final PmdModel model;

    private final I18nText modelName;
    private final I18nText description;

    private final List<BoneInfo> boneList;
    private Iterator<BoneInfo> boneIt;
    private BoneInfo currentBone = null;

    private List<MorphPart> morphPartList;
    private Iterator<MorphPart> morphPartIt;
    private MorphPart currentMorphPart = null;

    private final List<BoneGroup> boneGroupList;
    private Iterator<BoneGroup> boneGroupIt;
    private BoneGroup currentBoneGroup = null;

    private boolean hasMoreData = false;

    /**
     * コンストラクタ。
     * @param model モデル
     */
    TextBuilder(PmdModel model){
        super();

        this.model = model;

        this.modelName   = model.getModelName();
        this.description = model.getDescription();

        this.boneList      = model.getBoneList();
        this.boneGroupList = model.getBoneGroupList();

        return;
    }

    /**
     * PMDファイル中の出現順で各モーフを格納するためのリストを設定する。
     * 主な用途はモーフ和英名の突き合わせ作業。
     * @param list モーフ格納リスト
     */
    void setMorphPartList(List<MorphPart> list){
        this.morphPartList = list;
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pmdParseStart(){
        return;
    }

    /**
     * {@inheritDoc}
     * @param hasMoreData {@inheritDoc}
     */
    @Override
    public void pmdParseEnd(boolean hasMoreData){
        this.hasMoreData = hasMoreData;
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     * @param loops {@inheritDoc}
     */
    @Override
    public void loopStart(ParseStage stage, int loops){
        assert stage instanceof PmdEngStage;

        if(stage == PmdEngHandler.ENGBONE_LIST){
            this.boneIt = this.boneList.iterator();
            if(this.boneIt.hasNext()){
                this.currentBone = this.boneIt.next();
            }
        }else if(stage == PmdEngHandler.ENGMORPH_LIST){
            this.morphPartIt = this.morphPartList.iterator();

            // 「base」モーフを読み飛ばす
            assert this.morphPartIt.hasNext();
            MorphPart part = this.morphPartIt.next();
            assert part != null;

            if(this.morphPartIt.hasNext()){
                this.currentMorphPart = this.morphPartIt.next();
            }
        }else if(stage == PmdEngHandler.ENGBONEGROUP_LIST){
            this.boneGroupIt = this.boneGroupList.iterator();

            // デフォルトボーングループを読み飛ばす
            assert this.boneGroupIt.hasNext();
            BoneGroup group = this.boneGroupIt.next();
            assert group != null;

            if(this.boneGroupIt.hasNext()){
                this.currentBoneGroup = this.boneGroupIt.next();
            }
        }else{
            assert false;
            throw new AssertionError();
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopNext(ParseStage stage){
        assert stage instanceof PmdEngStage;

        if(stage == PmdEngHandler.ENGBONE_LIST){
            if(this.boneIt.hasNext()){
                this.currentBone = this.boneIt.next();
            }
        }else if(stage == PmdEngHandler.ENGMORPH_LIST){
            if(this.morphPartIt.hasNext()){
                this.currentMorphPart = this.morphPartIt.next();
            }
        }else if(stage == PmdEngHandler.ENGBONEGROUP_LIST){
            if(this.boneGroupIt.hasNext()){
                this.currentBoneGroup = this.boneGroupIt.next();
            }
        }else{
            assert false;
            throw new AssertionError();
        }
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopEnd(ParseStage stage){
        assert stage instanceof PmdEngStage;
        return;
    }

    /**
     * {@inheritDoc}
     * @param ver {@inheritDoc}
     */
    @Override
    public void pmdHeaderInfo(float ver){
        this.model.setHeaderVersion(ver);
        return;
    }

    /**
     * {@inheritDoc}
     * @param modelName {@inheritDoc}
     * @param description {@inheritDoc}
     */
    @Override
    public void pmdModelInfo(String modelName, String description){
        this.modelName  .setPrimaryText(modelName);
        this.description.setPrimaryText(description);
        return;
    }

    /**
     * {@inheritDoc}
     * @param hasEnglishInfo {@inheritDoc}
     */
    @Override
    public void pmdEngEnabled(boolean hasEnglishInfo){
        return;
    }

    /**
     * {@inheritDoc}
     * @param modelName {@inheritDoc}
     * @param description {@inheritDoc}
     */
    @Override
    public void pmdEngModelInfo(String modelName, String description){
        this.modelName  .setGlobalText(modelName);
        this.description.setGlobalText(description);
        return;
    }

    /**
     * {@inheritDoc}
     * @param boneName {@inheritDoc}
     */
    @Override
    public void pmdEngBoneInfo(String boneName){
        this.currentBone.getBoneName().setGlobalText(boneName);
        return;
    }

    /**
     * {@inheritDoc}
     * @param morphName {@inheritDoc}
     */
    @Override
    public void pmdEngMorphInfo(String morphName){
        this.currentMorphPart.getMorphName().setGlobalText(morphName);
        return;
    }

    /**
     * {@inheritDoc}
     * @param groupName {@inheritDoc}
     */
    @Override
    public void pmdEngBoneGroupInfo(String groupName){
        this.currentBoneGroup.getGroupName().setGlobalText(groupName);
        return;
    }

    /**
     * 読み残したデータがあるか判定する。
     * @return 読み残したデータがあればtrue
     */
    public boolean hasMoreData(){
        return this.hasMoreData;
    }

}
