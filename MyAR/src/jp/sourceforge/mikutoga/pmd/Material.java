/*
 * material information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.awt.Color;
import java.awt.Transparency;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.I18nText;

/**
 * マテリアル素材情報。
 */
public class Material implements Iterable<Surface> {

    private final I18nText materialName = new I18nText();

    private Color diffuseColor;

    private Color specularColor;
    private float shininess;

    private Color ambientColor;

    private final ShadeInfo shadeInfo = new ShadeInfo();

    private boolean edgeAppearance = true;

    private final List<Surface> surfaceList = new ArrayList<Surface>();

    /**
     * コンストラクタ。
     */
    public Material(){
        super();
        return;
    }

    /**
     * 色を不透明化する。
     * @param color 色
     * @return 不透明化した色。引数と同じ場合もありうる。
     */
    private static Color forceOpaque(Color color){
        if(color.getTransparency() == Transparency.OPAQUE){
            return color;
        }

        float[] rgba = new float[4];
        color.getRGBColorComponents(rgba);

        Color result = new Color(rgba[0], rgba[1], rgba[2], 1.0f);

        return result;
    }

    /**
     * マテリアル名を返す。
     * PMDEditorのみでのサポート？
     * @return マテリアル名
     */
    public I18nText getMaterialName(){
        return this.materialName;
    }

    /**
     * 拡散光を設定する。
     * アルファ成分も反映される。
     * @param color 拡散光
     * @throws NullPointerException 引数がnull
     */
    public void setDiffuseColor(Color color) throws NullPointerException{
        if(color == null) throw new NullPointerException();
        this.diffuseColor = color;
        return;
    }

    /**
     * 拡散光を返す。
     * @return 拡散光
     */
    public Color getDiffuseColor(){
        return this.diffuseColor;
    }

    /**
     * 反射光を設定する。
     * 透過成分があれば不透明化される。
     * @param color 反射光
     * @throws NullPointerException 引数がnull
     */
    public void setSpecularColor(Color color)
            throws NullPointerException{
        if(color == null) throw new NullPointerException();
        this.specularColor = forceOpaque(color);
        return;
    }

    /**
     * 反射光を返す。
     * @return 反射光
     */
    public Color getSpecularColor(){
        return this.specularColor;
    }

    /**
     * 環境光を設定する。
     * 透過成分があれば不透明化される。
     * @param color 環境光
     * @throws NullPointerException 引数がnull
     */
    public void setAmbientColor(Color color)
            throws NullPointerException{
        if(color == null) throw new NullPointerException();
        this.ambientColor = forceOpaque(color);
        return;
    }

    /**
     * 環境光を返す。
     * @return 環境光
     */
    public Color getAmbientColor(){
        return this.ambientColor;
    }

    /**
     * 光沢強度を設定する。
     * MMDで用いられるのは1.0〜15.0あたり。
     * @param shininess 光沢強度
     */
    public void setShininess(float shininess){
        this.shininess = shininess;
        return;
    }

    /**
     * 光沢強度を返す。
     * @return 光沢強度
     */
    public float getShininess(){
        return this.shininess;
    }

    /**
     * シェーディング設定を返す。
     * @return シェーディング設定
     */
    public ShadeInfo getShadeInfo(){
        return this.shadeInfo;
    }

    /**
     * エッジを表示するか設定する。
     * 頂点単位の設定より優先度は低い。
     * @param show 表示するならtrue
     */
    public void setEdgeAppearance(boolean show){
        this.edgeAppearance = show;
        return;
    }

    /**
     * エッジを表示するか判定する。
     * 頂点単位の設定より優先度は低い。
     * @return 表示するならtrue
     */
    public boolean getEdgeAppearance(){
        return this.edgeAppearance;
    }

    /**
     * 面リストを返す。
     * @return 面リスト
     */
    public List<Surface> getSurfaceList(){
        return this.surfaceList;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<Surface> iterator(){
        return this.surfaceList.iterator();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("Material ");

        float[] rgba = new float[4];

        this.diffuseColor.getRGBComponents(rgba);
        result.append("diffuse=[")
              .append(rgba[0]).append(", ")
              .append(rgba[1]).append(", ")
              .append(rgba[2]).append(", ")
              .append(rgba[3]).append(']')
              .append(' ');

        this.specularColor.getRGBComponents(rgba);
        result.append("specular=[")
              .append(rgba[0]).append(", ")
              .append(rgba[1]).append(", ")
              .append(rgba[2]).append(", ")
              .append(rgba[3]).append(']')
              .append(' ');

        this.ambientColor.getRGBComponents(rgba);
        result.append("ambient=[")
              .append(rgba[0]).append(", ")
              .append(rgba[1]).append(", ")
              .append(rgba[2]).append(", ")
              .append(rgba[3]).append(']')
              .append(' ');

        result.append("shininess=")
              .append(this.shininess)
              .append(' ');
        result.append("edge=")
              .append(this.edgeAppearance)
              .append(' ');
        result.append("surfaces=")
              .append(this.surfaceList.size())
              .append(' ');
        result.append(this.shadeInfo);

        return result.toString();
    }

}
