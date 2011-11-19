/*
 * triplet-value range limitation
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * XYZ三組float値の範囲制約。
 */
public class TripletRange {

    private float xFrom;
    private float xTo;
    private float yFrom;
    private float yTo;
    private float zFrom;
    private float zTo;

    /**
     * コンストラクタ。
     */
    public TripletRange(){
        super();
        return;
    }

    /**
     * X値有効範囲を設定する。
     * 下限値が上限値より大きければ入れ替える。
     * @param xFrom X値下限
     * @param xTo X値上限
     */
    public void setXRange(float xFrom, float xTo){
        if(xFrom <= xTo){
            this.xFrom = xFrom;
            this.xTo = xTo;
        }else{
            this.xFrom = xTo;
            this.xTo = xFrom;
        }
        return;
    }

    /**
     * Y値有効範囲を設定する。
     * 下限値が上限値より大きければ入れ替える。
     * @param yFrom Y値下限
     * @param yTo Y値上限
     */
    public void setYRange(float yFrom, float yTo){
        if(yFrom <= yTo){
            this.yFrom = yFrom;
            this.yTo = yTo;
        }else{
            this.yFrom = yTo;
            this.yTo = yFrom;
        }
        return;
    }

    /**
     * Z値有効範囲を設定する。
     * 下限値が上限値より大きければ入れ替える。
     * @param zFrom Z値下限
     * @param zTo Z値上限
     */
    public void setZRange(float zFrom, float zTo){
        if(zFrom <= zTo){
            this.zFrom = zFrom;
            this.zTo = zTo;
        }else{
            this.zFrom = zTo;
            this.zTo = zFrom;
        }
        return;
    }

    /**
     * X値下限を返す。
     * @return X値下限
     */
    public float getXFrom(){
        return this.xFrom;
    }

    /**
     * X値上限を返す。
     * @return X値上限
     */
    public float getXTo(){
        return this.xTo;
    }

    /**
     * Y値下限を返す。
     * @return Y値下限
     */
    public float getYFrom(){
        return this.yFrom;
    }

    /**
     * Y値上限を返す。
     * @return Y値上限
     */
    public float getYTo(){
        return this.yTo;
    }

    /**
     * Z値下限を返す。
     * @return Z値下限
     */
    public float getZFrom(){
        return this.zFrom;
    }

    /**
     * Z値上限を返す。
     * @return Z値上限
     */
    public float getZTo(){
        return this.zTo;
    }

    /**
     * X値が範囲制約を満たすか判定する。
     * @param xVal X値
     * @return 制約を満たすならtrue
     */
    public boolean isValidX(float xVal){
        if(this.xFrom <= xVal && xVal <= this.xTo) return true;
        return false;
    }

    /**
     * Y値が範囲制約を満たすか判定する。
     * @param yVal Y値
     * @return 制約を満たすならtrue
     */
    public boolean isValidY(float yVal){
        if(this.yFrom <= yVal && yVal <= this.yTo) return true;
        return false;
    }

    /**
     * Z値が範囲制約を満たすか判定する。
     * @param zVal Z値
     * @return 制約を満たすならtrue
     */
    public boolean isValidZ(float zVal){
        if(this.zFrom <= zVal && zVal <= this.zTo) return true;
        return false;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("x=[")
              .append(xFrom)
              .append(" - ")
              .append(xTo)
              .append("] ");
        result.append("y=[")
              .append(yFrom)
              .append(" - ")
              .append(yTo)
              .append("] ");
        result.append("z=[")
              .append(zFrom)
              .append(" - ")
              .append(zTo)
              .append("]");

        return result.toString();
    }

}
