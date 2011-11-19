/*
 * 3D vector
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * XYZ三次元ベクトル。
 */
public class Vec3d {

    private float xVal;
    private float yVal;
    private float zVal;

    /**
     * コンストラクタ。
     */
    public Vec3d(){
        super();
        return;
    }

    /**
     * X値を設定する。
     * @param xVal X値
     */
    public void setXVal(float xVal){
        this.xVal = xVal;
        return;
    }

    /**
     * X値を返す。
     * @return X値
     */
    public float getXVal(){
        return this.xVal;
    }

    /**
     * Y値を設定する。
     * @param yVal Y値
     */
    public void setYVal(float yVal){
        this.yVal = yVal;
        return;
    }

    /**
     * Y値を返す。
     * @return Y値
     */
    public float getYVal(){
        return this.yVal;
    }

    /**
     * Z値を設定する。
     * @param zVal Z値
     */
    public void setZVal(float zVal){
        this.zVal = zVal;
        return;
    }

    /**
     * Z値を返す。
     * @return Z値
     */
    public float getZVal(){
        return this.zVal;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("vec=[")
              .append(this.xVal).append(", ")
              .append(this.yVal).append(", ")
              .append(this.zVal).append(']');

        return result.toString();
    }

}
