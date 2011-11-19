/*
 * morph information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;

/**
 * 個別モーフ情報。
 */
public class MorphPart implements SerialNumbered, Iterable<MorphVertex> {

    private final I18nText morphName = new I18nText();

    private MorphType type;

    private final List<MorphVertex> morphVertexList =
            new ArrayList<MorphVertex>();

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public MorphPart(){
        super();
        return;
    }

    /**
     * モーフ名を返す。
     * @return モーフ名
     */
    public I18nText getMorphName(){
        return this.morphName;
    }

    /**
     * モーフ種別を設定する。
     * @param typeArg モーフ種別
     * @throws NullPointerException 引数がnull
     */
    public void setMorphType(MorphType typeArg) throws NullPointerException{
        if(typeArg == null) throw new NullPointerException();
        this.type = typeArg;
        return;
    }

    /**
     * モーフ種別を返す。
     * @return モーフ種別。
     */
    public MorphType getMorphType(){
        return this.type;
    }

    /**
     * モーフ頂点情報リストを返す。
     * @return モーフ頂点情報リスト
     */
    public List<MorphVertex> getMorphVertexList(){
        return this.morphVertexList;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<MorphVertex> iterator(){
        return this.morphVertexList.iterator();
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

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("Morph(").append(this.morphName).append(") ");
        result.append("type=").append(this.type);
        result.append(" vertexNum=").append(this.morphVertexList.size());

        return result.toString();
    }

}
