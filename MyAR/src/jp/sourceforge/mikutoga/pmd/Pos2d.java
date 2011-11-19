/*
 * 2D position
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * 二次元空間座標及び変量を表す。
 */
public class Pos2d {

    private float xPos;
    private float yPos;

    /**
     * コンストラクタ。
     * [0,0]が設定される
     */
    public Pos2d(){
        this(0.0f, 0.0f);
        return;
    }

    /**
     * コンストラクタ。
     * @param xPos X座標
     * @param yPos Y座標
     */
    public Pos2d(float xPos, float yPos){
        super();
        this.xPos = xPos;
        this.yPos = yPos;
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
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("pos=[")
              .append(this.xPos).append(", ")
              .append(this.yPos).append(']');

        return result.toString();
    }

}
