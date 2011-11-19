/*
 * 3d rotation (degree)
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * XYZ3軸による回転量(degree)。
 * radianではなくdegree。(直角は90.0)
 */
public class Deg3d {

    private float xDeg;
    private float yDeg;
    private float zDeg;

    /**
     * コンストラクタ。
     */
    public Deg3d(){
        super();
        return;
    }

    /**
     * X軸回転量を設定する。
     * @param xDeg X軸回転量(degree)
     */
    public void setXDeg(float xDeg){
        this.xDeg = xDeg;
        return;
    }

    /**
     * X軸回転量を返す。
     * @return X軸回転量(degree)
     */
    public float getXDeg(){
        return this.xDeg;
    }

    /**
     * Y軸回転量を設定する。
     * @param yDeg Y軸回転量(degree)
     */
    public void setYDeg(float yDeg){
        this.yDeg = yDeg;
        return;
    }

    /**
     * Y軸回転量を返す。
     * @return Y軸回転量(degree)
     */
    public float getYDeg(){
        return this.yDeg;
    }

    /**
     * Z軸回転量を設定する。
     * @param zDeg Z軸回転量(degree)
     */
    public void setZDeg(float zDeg){
        this.zDeg = zDeg;
        return;
    }

    /**
     * Z軸回転量を返す。
     * @return Z軸回転量(degree)
     */
    public float getZDeg(){
        return this.zDeg;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("deg=[")
              .append(this.xDeg).append(", ")
              .append(this.yDeg).append(", ")
              .append(this.zDeg).append(']');

        return result.toString();
    }

}
