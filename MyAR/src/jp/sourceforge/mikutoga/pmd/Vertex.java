/*
 * vertex information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * 頂点情報。
 */
public class Vertex implements SerialNumbered {

    private static final int MIN_WEIGHT = 0;
    private static final int MAX_WEIGHT = 100;

    private final Pos3d position = new Pos3d();
    private final Vec3d normal = new Vec3d();

    private final Pos2d uvPosition = new Pos2d();

    private BoneInfo boneA = null;
    private BoneInfo boneB = null;

    private int boneWeight = 50;

    private boolean edgeAppearance = true;

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public Vertex(){
        super();
        return;
    }

    /**
     * 頂点位置座標を返す。
     * @return 頂点の位置座標
     */
    public Pos3d getPosition(){
        return this.position;
    }

    /**
     * 法線ベクトルを返す。
     * @return 法線ベクトル
     */
    public Vec3d getNormal(){
        return this.normal;
    }

    /**
     * UVマップ座標を返す。
     * @return UVマップ情報
     */
    public Pos2d getUVPosition(){
        return this.uvPosition;
    }

    /**
     * 頂点の属するボーンを設定する。
     * @param boneA ボーンA
     * @param boneB ボーンB
     * @throws NullPointerException 引数がnull
     */
    public void setBonePair(BoneInfo boneA, BoneInfo boneB)
            throws NullPointerException{
        if(boneA == null || boneB == null) throw new NullPointerException();
        this.boneA = boneA;
        this.boneB = boneB;
        return;
    }

    /**
     * ボーンAを返す。
     * @return ボーンA
     */
    public BoneInfo getBoneA(){
        return this.boneA;
    }

    /**
     * ボーンBを返す。
     * @return ボーンB
     */
    public BoneInfo getBoneB(){
        return this.boneB;
    }

    /**
     * ボーンAのウェイト値を設定する。
     * @param weight ウェイト値。0(影響小)-100(影響大)
     * @throws IllegalArgumentException ウェイト値が範囲外
     */
    public void setWeightA(int weight) throws IllegalArgumentException{
        if(   weight < MIN_WEIGHT
           || MAX_WEIGHT < weight ){
            throw new IllegalArgumentException();
        }
        this.boneWeight = weight;
        return;
    }

    /**
     * ボーンBのウェイト値を設定する。
     * @param weight ウェイト値。0(影響小)-100(影響大)
     * @throws IllegalArgumentException ウェイト値が範囲外
     */
    public void setWeightB(int weight) throws IllegalArgumentException{
        setWeightA(MAX_WEIGHT - weight);
        return;
    }

    /**
     * ボーンAのウェイト値を返す。
     * @return ウェイト値
     */
    public int getWeightA(){
        return this.boneWeight;
    }

    /**
     * ボーンBのウェイト値を返す。
     * @return ウェイト値
     */
    public int getWeightB(){
        int result = MAX_WEIGHT - this.boneWeight;
        return result;
    }

    /**
     * ボーンAのウェイト率を返す。
     * @return ウェイト率。0.0(影響小)-1.0(影響大)
     */
    public float getWeightRatioA(){
        return ((float)this.boneWeight) / (float)MAX_WEIGHT;
    }

    /**
     * ボーンBのウェイト率を返す。
     * @return ウェイト率。0.0(影響小)-1.0(影響大)
     */
    public float getWeightRatioB(){
        return ((float)MAX_WEIGHT - (float)this.boneWeight)
                / (float)MAX_WEIGHT;
    }

    /**
     * エッジを表示するか設定する。
     * マテリアル材質単位の設定より優先度は高い。
     * @param show 表示するならtrue
     */
    public void setEdgeAppearance(boolean show){
        this.edgeAppearance = show;
        return;
    }

    /**
     * エッジを表示するか判定する。
     * マテリアル材質単位の設定より優先度は高い。
     * @return 表示するならtrue
     */
    public boolean getEdgeAppearance(){
        return this.edgeAppearance;
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

        result.append("Vertex(").append(this.serialNo).append(") ");
        result.append(this.position).append(' ');
        result.append(this.normal).append(' ');
        result.append("UV").append(this.uvPosition).append(' ');

        result.append("[")
              .append(this.boneA.getBoneName())
              .append("<>")
              .append(this.boneB.getBoneName())
              .append("] ");

        result.append("weight=").append(this.boneWeight).append(' ');

        if(this.edgeAppearance) result.append("showEdge");
        else                    result.append("hideEdge");

        return result.toString();
    }

}
