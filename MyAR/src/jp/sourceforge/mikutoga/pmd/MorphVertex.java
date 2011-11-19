/*
 * morph vertex information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.Comparator;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * モーフアニメーションを構成する個別の頂点移動の情報。
 */
public class MorphVertex implements SerialNumbered{

    /** 頂点IDを昇順に順序づけるComaparator。 */
    public static final Comparator<MorphVertex> VIDCOMPARATOR =
            new VertexIdComparator();

    private Vertex baseVertex;
    private final Pos3d offset = new Pos3d();

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public MorphVertex(){
        super();
        return;
    }

    /**
     * 移動元頂点情報を返す。
     * @return 移動元頂点
     */
    public Vertex getBaseVertex(){
        return this.baseVertex;
    }

    /**
     * 移動元頂点情報を設定する。
     * @param vertex 移動元頂点
     * @throws NullPointerException 引数がnull
     */
    public void setBaseVertex(Vertex vertex) throws NullPointerException{
        if(vertex == null) throw new NullPointerException();
        this.baseVertex = vertex;
        return;
    }

    /**
     * 頂点移動量を返す。
     * @return 頂点移動量
     */
    public Pos3d getOffset(){
        return this.offset;
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

        result.append("vid(")
              .append(this.baseVertex.getSerialNumber())
              .append(") ");
        result.append(this.baseVertex.getPosition());
        result.append(" >> ");
        result.append(this.offset);

        return result.toString();
    }

    /**
     * 頂点IDによる比較子Comparator。
     */
    private static final class VertexIdComparator
            implements Comparator<MorphVertex> {

        /**
         * コンストラクタ。
         */
        private VertexIdComparator(){
            super();
            return;
        }

        /**
         * {@inheritDoc}
         * @param o1 {@inheritDoc}
         * @param o2 {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public int compare(MorphVertex o1, MorphVertex o2){
            if(o1 == o2) return 0;
            if(o1 == null) return -1;
            if(o2 == null) return +1;

            int ser1 = o1.getBaseVertex().getSerialNumber();
            int ser2 = o2.getBaseVertex().getSerialNumber();

            return ser1 - ser2;
        }

    }

}
