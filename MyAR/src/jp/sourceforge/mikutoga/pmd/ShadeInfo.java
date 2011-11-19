/*
 * shading information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

/**
 * シェーディング情報。
 */
public class ShadeInfo {

    private ToonMap toonMap = new ToonMap();
    private int toonIdx;

    private String textureFileName = null;
    private String spheremapFileName = null;

    /**
     * コンストラクタ。
     */
    public ShadeInfo(){
        super();
        return;
    }

    /**
     * トゥーンマップを設定する。
     * @param map トゥーンマップ
     */
    public void setToonMap(ToonMap map){
        this.toonMap = map;
        return;
    }

    /**
     * トゥーンマップを返す。
     * @return トゥーンマップ
     */
    public ToonMap getToonMap(){
        return this.toonMap;
    }

    /**
     * トゥーンインデックスを設定する。
     * @param idx トゥーンインデックス
     */
    public void setToonIndex(int idx){
        this.toonIdx = idx;
        return;
    }

    /**
     * トゥーンインデックス値を返す。
     * @return トゥーンインデックス値
     */
    public int getToonIndex(){
        return this.toonIdx;
    }

    /**
     * トゥーンインデックス値が有効か判定する。
     * 現時点では0から9までの値を有効とする。
     * @return 有効ならtrue
     */
    public boolean isValidToonIndex(){
        if(0 <= this.toonIdx && this.toonIdx <= 9) return true;
        return false;
    }

    /**
     * トゥーンファイル名を返す。
     * @return トゥーンファイル名
     * @throws IllegalStateException トゥーンマップが設定されていない。
     */
    public String getToonFileName() throws IllegalStateException{
        if(this.toonMap == null) throw new IllegalStateException();
        String result = this.toonMap.getIndexedToon(this.toonIdx);
        return result;
    }

    /**
     * テクスチャファイル名を設定する。
     * @param fileName テクスチャファイル名
     */
    public void setTextureFileName(String fileName){
        this.textureFileName = fileName;
        return;
    }

    /**
     * テクスチャファイル名を返す。
     * @return テクスチャファイル名
     */
    public String getTextureFileName(){
        return this.textureFileName;
    }

    /**
     * スフィアマップファイル名を設定する。
     * @param fileName スフィアマップファイル名
     */
    public void setSpheremapFileName(String fileName){
        this.spheremapFileName = fileName;
        return;
    }

    /**
     * スフィアマップファイル名を返す。
     * @return スフィアマップファイル名
     */
    public String getSpheremapFileName(){
        return this.spheremapFileName;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("toon(")
              .append(this.toonIdx)
              .append(")=")
              .append(getToonFileName())
              .append(' ');
        result.append("texture=")
              .append(this.textureFileName)
              .append(' ');
        result.append("sphere=")
              .append(this.spheremapFileName);

        return result.toString();
    }

}
