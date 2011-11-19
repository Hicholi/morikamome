/*
 * PMD file loader
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.MmdSource;
import jp.sourceforge.mikutoga.parser.pmd.PmdParser;
import jp.sourceforge.mikutoga.pmd.MorphPart;
import jp.sourceforge.mikutoga.pmd.PmdModel;

/**
 * PMDモデルファイルを読み込むためのローダ。
 */
public class PmdLoader {

    private PmdModel model;
    private PmdParser parser;
    private TextBuilder textBuilder;

    private boolean loaded = false;
    private boolean hasMoreData = false;

    /**
     * コンストラクタ。
     * @param source PMDファイル入力ソース
     */
    public PmdLoader(MmdSource source){
        super();

        this.model = new PmdModel();
        this.parser = new PmdParser(source);
        this.textBuilder = new TextBuilder(this.model);

        setHandler();

        return;
    }

    /**
     * パーサに各種ハンドラの設定を行う。
     */
    private void setHandler(){
        ShapeBuilder    shapeBuilder    = new ShapeBuilder(this.model);
        MaterialBuilder materialBuilder = new MaterialBuilder(this.model);
        BoneBuilder     boneBuilder     = new BoneBuilder(this.model);
        MorphBuilder    morphBuilder    = new MorphBuilder(this.model);
        ToonBuilder     toonBuilder     = new ToonBuilder(this.model);
        RigidBuilder    rigidBuilder    = new RigidBuilder(this.model);
        JointBuilder    jointBuilder    = new JointBuilder(this.model);

        this.parser.setBasicHandler(this.textBuilder);
        this.parser.setShapeHandler(shapeBuilder);
        this.parser.setMaterialHandler(materialBuilder);
        this.parser.setBoneHandler(boneBuilder);
        this.parser.setMorphHandler(morphBuilder);
        this.parser.setEngHandler(this.textBuilder);
        this.parser.setToonHandler(toonBuilder);
        this.parser.setRigidHandler(rigidBuilder);
        this.parser.setJointHandler(jointBuilder);

        List<MorphPart> morphPartList = new ArrayList<MorphPart>();
        morphBuilder.setMorphPartList(morphPartList);
        this.textBuilder.setMorphPartList(morphPartList);
        morphPartList.clear();

        return;
    }

    /**
     * PMDファイルの読み込みを行いモデル情報を返す。
     * 1インスタンスにつき一度しかロードできない。
     * @return モデル情報
     * @throws IOException 入力エラー
     * @throws MmdFormatException PMDファイルフォーマットの異常を検出
     * @throws IllegalStateException このインスタンスで再度のロードを試みた。
     */
    public PmdModel load()
            throws IOException,
                   MmdFormatException,
                   IllegalStateException {
        if(this.loaded) throw new IllegalStateException();

        PmdModel result;
        try{
            this.parser.parsePmd();
        }finally{
            this.loaded = true;

            result = this.model;
            this.hasMoreData = this.textBuilder.hasMoreData();

            this.model = null;
            this.parser = null;
            this.textBuilder = null;
        }

        return result;
    }

    /**
     * ロード処理が正常終了したのにまだ読み込んでいない部分が放置されているか判定する。
     * MMDでの仕様拡張によるPMDファイルフォーマットの拡張が行われた場合を想定。
     * @return 読み込んでいない部分があればtrue
     */
    public boolean hasMoreData(){
        return this.hasMoreData;
    }

}
