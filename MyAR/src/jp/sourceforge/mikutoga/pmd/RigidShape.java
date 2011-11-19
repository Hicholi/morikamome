/*
 * rigid shape information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * 剛体形状に関する情報。
 * 球及びカプセルの半径と箱の幅は同じ値が用いられる。
 */
public class RigidShape {

    private RigidShapeType type = RigidShapeType.BOX;
    private float width  = 0.1f;
    private float height = 0.1f;
    private float depth  = 0.1f;

    /**
     * コンストラクタ。
     */
    public RigidShape(){
        super();
        return;
    }

    /**
     * 剛体形状種別を返す。
     * @return 剛体形状種別
     */
    public RigidShapeType getShapeType(){
        return this.type;
    }

    /**
     * 剛体形状種別を設定する。
     * @param typeArg 剛体形状種別
     * @throws NullPointerException 引数がnull
     */
    public void setShapeType(RigidShapeType typeArg)
            throws NullPointerException{
        if(typeArg == null) throw new NullPointerException();
        this.type = typeArg;
        return;
    }

    /**
     * 箱の幅を返す。
     * @return 箱の幅
     */
    public float getWidth(){
        return this.width;
    }

    /**
     * 箱の幅を設定する。
     * @param width 箱の幅
     */
    public void setWidth(float width){
        this.width = width;
        return;
    }

    /**
     * 箱及びカプセルの高さを返す。
     * @return 箱及びカプセルの高さ
     */
    public float getHeight(){
        return this.height;
    }

    /**
     * 箱及びカプセルの高さを設定する。
     * @param height 箱及びカプセルの高さ
     */
    public void setHeight(float height){
        this.height = height;
        return;
    }

    /**
     * 箱の奥行きを返す。
     * @return 箱の奥行き
     */
    public float getDepth(){
        return this.depth;
    }

    /**
     * 箱の奥行きを設定する。
     * @param depth 箱の奥行き
     */
    public void setDepth(float depth){
        this.depth = depth;
        return;
    }

    /**
     * 球及びカプセルの半径を返す。
     * @return 球及びカプセルの半径
     */
    public float getRadius(){
        return this.width;
    }

    /**
     * 球及びカプセルの半径を設定する。
     * @param radius 球及びカプセルの半径
     */
    public void setRadius(float radius){
        this.width = radius;
        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append(this.type).append(' ');

        switch(this.type){
        case SPHERE:
            result.append("r=").append(this.width);
            break;
        case BOX:
            result.append("w=").append(this.width).append(", ");
            result.append("h=").append(this.height).append(", ");
            result.append("d=").append(this.depth);
            break;
        case CAPSULE:
            result.append("r=").append(this.width).append(", ");
            result.append("h=").append(this.height);
            break;
        default:
            assert false;
            throw new AssertionError();
        }

        return  result.toString();
    }

}
