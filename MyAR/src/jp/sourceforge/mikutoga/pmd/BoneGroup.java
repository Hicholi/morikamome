/*
 * bone group
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
 * ボーングループ。
 * ボーングループ名と0個以上のボーンを配下に持つ。
 * 通し番号0のボーングループは、暗黙に用意される「デフォルトボーングループ」とする。
 */
public class BoneGroup implements SerialNumbered , Iterable<BoneInfo> {

    private final I18nText groupName = new I18nText();

    private final List<BoneInfo> boneList = new ArrayList<BoneInfo>();

    private int serialNo = -1;

    /**
     * コンストラクタ。
     */
    public BoneGroup(){
        super();
        return;
    }

    /**
     * ボーングループ名を返す。
     * @return ボーングループ名
     */
    public I18nText getGroupName(){
        return this.groupName;
    }

    /**
     * ボーンリストを取得する。
     * @return ボーンリスト
     */
    public List<BoneInfo> getBoneList(){
        return this.boneList;
    }

    /**
     * デフォルトボーングループか否か判定する。
     * 通し番号が0ならデフォルトボーングループ。
     * @return デフォルトボーングループならtrue
     */
    public boolean isDefaultBoneGroup(){
        if(this.serialNo == 0) return true;
        return false;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<BoneInfo> iterator(){
        return this.boneList.iterator();
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
     * ボーングループ番号(ボーン枠番号)を返す。
     * 常に通し番号より1少ない値となる。
     * デフォルトボーングループは-1となる。
     * @return ボーングループ番号
     */
    public int getBoneGroupNumber(){
        return this.serialNo - 1;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("BoneGroup(")
              .append(this.groupName)
              .append(") [");

        boolean dumped = false;
        for(BoneInfo bone : this){
            if(dumped) result.append(", ");
            result.append(bone.getBoneName());
            dumped = true;
        }

        result.append(']');

        return result.toString();
    }

}
