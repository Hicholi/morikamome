/*
 * IK chained bone
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IK連鎖ボーン群。
 */
public class IKChain implements Iterable<BoneInfo> {

    private BoneInfo ikBone;

    private int ikDepth;
    private float ikWeight;

    private final List<BoneInfo> chainList = new ArrayList<BoneInfo>();

    /**
     * コンストラクタ。
     */
    public IKChain(){
        super();
        return;
    }

    /**
     * IKボーンを設定する。
     * @param bone IKボーン
     */
    public void setIkBone(BoneInfo bone){
        this.ikBone = bone;
        return;
    }

    /**
     * IKボーンを返す。
     * @return IKボーン
     */
    public BoneInfo getIkBone(){
        return this.ikBone;
    }

    /**
     * IK演算再帰深度を設定する。
     * @param depth IK演算再帰深度
     */
    public void setIKDepth(int depth){
        this.ikDepth = depth;
        return;
    }

    /**
     * IK演算再帰深度を返す。
     * @return IK演算再帰深度
     */
    public int getIKDepth(){
        return this.ikDepth;
    }

    /**
     * IKウェイトを設定する。
     * @param weight IKウェイト
     */
    public void setIKWeight(float weight){
        this.ikWeight = weight;
        return;
    }

    /**
     * IKウェイトを返す。
     * @return IKウェイト
     */
    public float getIKWeight(){
        return this.ikWeight;
    }

    /**
     * IK連鎖ボーンリストを返す。
     * 最初の要素は必ずIK接続先ボーン。それ以降はIK影響下ボーン。
     * @return IK連鎖ボーンリスト
     */
    public List<BoneInfo> getChainedBoneList(){
        return this.chainList;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<BoneInfo> iterator(){
        return this.chainList.iterator();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("IKChain");

        result.append(" depth:").append(this.ikDepth);
        result.append(" weight:").append(this.ikWeight);

        result.append(" IKbone:").append(this.ikBone.getBoneName());

        result.append(" [");

        boolean chaindumped = false;
        for(BoneInfo chain : this.chainList){
            if(chaindumped) result.append(" => ");
            result.append(chain.getBoneName());
            chaindumped = true;
        }

        result.append("]");

        return result.toString();
    }

}
