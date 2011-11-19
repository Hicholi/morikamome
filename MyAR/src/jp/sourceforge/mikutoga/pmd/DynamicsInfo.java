/*
 * dynamics parameter
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * 剛体間力学演算の各種パラメータ。
 * 各剛体に設定可能なパラメータは
 * 「質量」、「移動減衰率」、「回転減衰率」、「反発力」、「摩擦力」の5種類。
 */
public class DynamicsInfo {

    /** 質量。 */
    private float mass;
    /** 移動減衰率。 */
    private float dampingPos;
    /** 回転減衰率。 */
    private float dampingRot;
    /** 反発力。 */
    private float restitution;
    /** 摩擦力。 */
    private float friction;

    /**
     * コンストラクタ。
     */
    public DynamicsInfo(){
        super();
        return;
    }

    /**
     * 質量を返す。
     * @return 質量
     */
    public float getMass(){
        return this.mass;
    }

    /**
     * 質量を設定する。
     * @param mass 質量
     */
    public void setMass(float mass){
        this.mass = mass;
        return;
    }

    /**
     * 移動減衰率を返す。
     * @return 移動減衰率
     */
    public float getDampingPosition(){
        return this.dampingPos;
    }

    /**
     * 移動減衰率を設定する。
     * @param damping 移動減衰率
     */
    public void setDampingPosition(float damping){
        this.dampingPos = damping;
        return;
    }

    /**
     * 回転減衰率を返す。
     * @return 回転減衰率
     */
    public float getDampingRotation(){
        return this.dampingRot;
    }

    /**
     * 回転減衰率を設定する。
     * @param damping 回転減衰率
     */
    public void setDampingRotation(float damping){
        this.dampingRot = damping;
        return;
    }

    /**
     * 反発力を返す。
     * @return 反発力
     */
    public float getRestitution(){
        return this.restitution;
    }

    /**
     * 反発力を設定する。
     * @param restitution 反発力
     */
    public void setRestitution(float restitution){
        this.restitution = restitution;
        return;
    }

    /**
     * 摩擦力を返す。
     * @return 摩擦力
     */
    public float getFriction(){
        return this.friction;
    }

    /**
     * 摩擦力を設定する。
     * @param friction 摩擦力
     */
    public void setFriction(float friction){
        this.friction = friction;
        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("mass=").append(this.mass).append(", ");
        result.append("damping(Pos)=").append(this.dampingPos).append(", ");
        result.append("damping(Rot)=").append(this.dampingRot).append(", ");
        result.append("restitution=").append(this.restitution).append(", ");
        result.append("friction=").append(this.friction);

        return result.toString();
    }

}
