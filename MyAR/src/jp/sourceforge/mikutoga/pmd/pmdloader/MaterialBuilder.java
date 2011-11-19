/*
 * building material information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import jp.sourceforge.mikutoga.corelib.ListUtil;
import jp.sourceforge.mikutoga.parser.ParseStage;
import jp.sourceforge.mikutoga.parser.pmd.PmdMaterialHandler;
import jp.sourceforge.mikutoga.pmd.Material;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.ShadeInfo;
import jp.sourceforge.mikutoga.pmd.Surface;
import jp.sourceforge.mikutoga.pmd.ToonMap;

/**
 * マテリアル素材関連の通知をパーサから受け取る。
 */
class MaterialBuilder implements PmdMaterialHandler {

    private final List<Material> materialList;
    private Iterator<Material> materialIt;
    private Material currentMaterial = null;

    private final List<Surface> surfacelList;
    private Iterator<Surface> surfaceIt;

    private final ToonMap toonMap;

    /**
     * コンストラクタ。
     * @param model モデル
     */
    MaterialBuilder(PmdModel model){
        super();

        this.materialList = model.getMaterialList();
        this.surfacelList = model.getSurfaceList();
        this.toonMap = model.getToonMap();

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     * @param loops {@inheritDoc}
     */
    @Override
    public void loopStart(ParseStage stage, int loops){
        assert stage == PmdMaterialHandler.MATERIAL_LIST;

        ListUtil.prepareDefConsList(this.materialList, Material.class, loops);

        this.materialIt = this.materialList.iterator();
        if(this.materialIt.hasNext()){
            this.currentMaterial = this.materialIt.next();
        }

        this.surfaceIt = this.surfacelList.iterator();

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopNext(ParseStage stage){
        assert stage == PmdMaterialHandler.MATERIAL_LIST;

        if(this.materialIt.hasNext()){
            this.currentMaterial = this.materialIt.next();
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopEnd(ParseStage stage){
        assert stage == PmdMaterialHandler.MATERIAL_LIST;
        return;
    }

    /**
     * {@inheritDoc}
     * @param red {@inheritDoc}
     * @param green {@inheritDoc}
     * @param blue {@inheritDoc}
     * @param alpha {@inheritDoc}
     */
    @Override
    public void pmdMaterialDiffuse(float red,
                                   float green,
                                   float blue,
                                   float alpha ){
        Color diffuse = new Color(red, green, blue, alpha);
        this.currentMaterial.setDiffuseColor(diffuse);
        return;
    }

    /**
     * {@inheritDoc}
     * @param red {@inheritDoc}
     * @param green {@inheritDoc}
     * @param blue {@inheritDoc}
     */
    @Override
    public void pmdMaterialAmbient(float red,
                                   float green,
                                   float blue ){
        Color ambient = new Color(red, green, blue);
        this.currentMaterial.setAmbientColor(ambient);
        return;
    }

    /**
     * {@inheritDoc}
     * @param red {@inheritDoc}
     * @param green {@inheritDoc}
     * @param blue {@inheritDoc}
     * @param shininess {@inheritDoc}
     */
    @Override
    public void pmdMaterialSpecular(float red,
                                    float green,
                                    float blue,
                                    float shininess ){
        Color specular = new Color(red, green, blue);
        this.currentMaterial.setSpecularColor(specular);
        this.currentMaterial.setShininess(shininess);
        return;
    }

    /**
     * {@inheritDoc}
     * @param hasEdge {@inheritDoc}
     * @param vertexNum {@inheritDoc}
     */
    @Override
    public void pmdMaterialInfo(boolean hasEdge, int vertexNum){
        this.currentMaterial.setEdgeAppearance(hasEdge);

        List<Surface> list = this.currentMaterial.getSurfaceList();

        int surfaceNum = vertexNum / 3;
        for(int ct = 1; ct <= surfaceNum; ct++){
            Surface surface = this.surfaceIt.next();
            list.add(surface);
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param toonIdx {@inheritDoc}
     * @param textureFile {@inheritDoc}
     * @param sphereFile {@inheritDoc}
     */
    @Override
    public void pmdMaterialShading(int toonIdx,
                                   String textureFile,
                                   String sphereFile ){
        ShadeInfo info = this.currentMaterial.getShadeInfo();

        ToonMap map = this.toonMap;

        info.setToonMap(map);
        info.setToonIndex(toonIdx);
        info.setTextureFileName(textureFile);
        info.setSpheremapFileName(sphereFile);

        return;
    }

}
