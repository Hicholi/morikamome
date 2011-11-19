/*
 * 3D position
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * 三次元空間座標及び変量を表す。
 */
public class Pos3d {

    private float xPos;
    private float yPos;
    private float zPos;

    /**
     * コンストラクタ。
     * [0,0,0]が設定される。
     */
    public Pos3d(){
        this(0.0f, 0.0f, 0.0f);
        return;
    }

    /**
     * コンストラクタ。
     * @param xPos X座標
     * @param yPos Y座標
     * @param zPos Z座標
     */
    public Pos3d(float xPos, float yPos, float zPos){
        super();
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        return;
    }

    /**
     * X座標を設定する。
     * @param xPos X座標
     */
    public void setXPos(float xPos){
        this.xPos = xPos;
        return;
    }

    /**
     * X座標を返す。
     * @return X座標
     */
    public float getXPos(){
        return this.xPos;
    }

    /**
     * Y座標を設定する。
     * @param yPos Y座標
     */
    public void setYPos(float yPos){
        this.yPos = yPos;
        return;
    }

    /**
     * Y座標を返す。
     * @return Y座標
     */
    public float getYPos(){
        return this.yPos;
    }

    /**
     * Z座標を設定する。
     * @param zPos Z座標
     */
    public void setZPos(float zPos){
        this.zPos = zPos;
        return;
    }

    /**
     * Z座標を返す。
     * @return Z座標
     */
    public float getZPos(){
        return this.zPos;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("pos=[")
              .append(this.xPos).append(", ")
              .append(this.yPos).append(", ")
              .append(this.zPos).append(']');

        return result.toString();
    }

}
