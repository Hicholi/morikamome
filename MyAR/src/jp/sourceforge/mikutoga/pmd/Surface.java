/*
 * triangle surface
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * 3頂点の三角形からなる面情報。
 */
public class Surface implements SerialNumbered, Iterable<Vertex> {

    private Vertex vertex1;
    private Vertex vertex2;
    private Vertex vertex3;

    private int serialNo = -1;

    /**
     * コンストラクタ。
     * 3頂点がnullの状態で生成される。
     */
    public Surface(){
        super();
        return;
    }

    /**
     * 3頂点を設定する。
     * @param vertex1 頂点1
     * @param vertex2 頂点2
     * @param vertex3 頂点3
     * @throws  IllegalArgumentException 重複する頂点が引数に含まれた
     */
    public void setTriangle(Vertex vertex1, Vertex vertex2, Vertex vertex3)
            throws IllegalArgumentException{
        if(vertex1 != null && (vertex1 == vertex2 || vertex1 == vertex3)){
            throw new IllegalArgumentException();
        }
        if(vertex2 != null && vertex2 == vertex3){
            throw new IllegalArgumentException();
        }

        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;

        return;
    }

    /**
     * 3頂点を返す。
     * @param store 頂点格納用配列。nullもしくは3要素に満たない場合は無視され、
     * 新規に格納用配列が生成される。
     * @return 先頭3要素に3頂点が収められた配列。未設定要素にはnullが入る。
     * 引数が長さ3以上の配列であれば引数と同じ配列が返る。
     */
    public Vertex[] getTriangle(Vertex[] store){
        Vertex[] result;
        if(store == null || store.length < 3){
            result = new Vertex[3];
        }else{
            result = store;
        }

        result[0] = this.vertex1;
        result[1] = this.vertex2;
        result[2] = this.vertex3;

        return result;
    }

    /**
     * 頂点その1を返す。
     * @return 頂点その1
     */
    public Vertex getVertex1(){
        return this.vertex1;
    }

    /**
     * 頂点その2を返す。
     * @return 頂点その2
     */
    public Vertex getVertex2(){
        return this.vertex2;
    }

    /**
     * 頂点その3を返す。
     * @return 頂点その3
     */
    public Vertex getVertex3(){
        return this.vertex3;
    }

    /**
     * {@inheritDoc}
     * 頂点を返す反復子を生成する。
     * 反復子がnullを返す可能性もありうる。
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<Vertex> iterator(){
        List<Vertex> list = new ArrayList<Vertex>(3);

        list.add(this.vertex1);
        list.add(this.vertex2);
        list.add(this.vertex3);

        return list.iterator();
    }

    /**
     * 3頂点全てが設定されているか判定する。
     * @return 3頂点とも非nullが設定されていればtrue
     */
    public boolean isCompleted(){
        if(   this.vertex1 != null
           && this.vertex2 != null
           && this.vertex3 != null ){
            return true;
        }
        return false;
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

        result.append("Surface(")
              .append(getSerialNumber())
              .append(")");

        if(isCompleted()){
            result.append(" VID=[")
                  .append(this.vertex1.getSerialNumber())
                  .append(',')
                  .append(this.vertex2.getSerialNumber())
                  .append(',')
                  .append(this.vertex3.getSerialNumber())
                  .append(']');
        }

        return result.toString();
    }

}
