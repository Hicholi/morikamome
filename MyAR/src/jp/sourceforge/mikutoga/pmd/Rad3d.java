/*
 * 3d rotation (radian)
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * XYZ3軸による回転量(radian)。
 * degereeではなくradian。(直角はΠ/2)
 */
public class Rad3d {

    private float xRad;
    private float yRad;
    private float zRad;

    /**
     * コンストラクタ。
     */
    public Rad3d(){
        super();
        return;
    }

    /**
     * X軸回転量を設定する。
     * @param xRad X軸回転量(radian)
     */
    public void setXRad(float xRad){
        this.xRad = xRad;
        return;
    }

    /**
     * X軸回転量を返す。
     * @return X軸回転量(radian)
     */
    public float getXRad(){
        return this.xRad;
    }

    /**
     * Y軸回転量を設定する。
     * @param yRad Y軸回転量(radian)
     */
    public void setYRad(float yRad){
        this.yRad = yRad;
        return;
    }

    /**
     * Y軸回転量を返す。
     * @return Y軸回転量(radian)
     */
    public float getYRad(){
        return this.yRad;
    }

    /**
     * Z軸回転量を設定する。
     * @param zRad Z軸回転量(radian)
     */
    public void setZRad(float zRad){
        this.zRad = zRad;
        return;
    }

    /**
     * Z軸回転量を返す。
     * @return Z軸回転量(radian)
     */
    public float getZRad(){
        return this.zRad;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("rad=[")
              .append(this.xRad).append(", ")
              .append(this.yRad).append(", ")
              .append(this.zRad).append(']');

        return result.toString();
    }

}
