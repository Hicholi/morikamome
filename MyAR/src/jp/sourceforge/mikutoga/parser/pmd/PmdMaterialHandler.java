/*
 * PMD material information handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import jp.sourceforge.mikutoga.parser.LoopHandler;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.ParseStage;

/**
 * PMDモデルの各種材質情報の通知用ハンドラ。
 * 色空間はsRGB?
 */
public interface PmdMaterialHandler extends LoopHandler {

    /**
     * モデル材質パースステージ。
     */
    class PmdMaterialStage extends ParseStage{
        /** コンストラクタ。 */
        PmdMaterialStage(){ super(); return; }
    }

    /** 材質抽出ループ。 */
    PmdMaterialStage MATERIAL_LIST = new PmdMaterialStage();

    /**
     * 材質の拡散光成分の通知を受け取る。
     * {@link #MATERIAL_LIST}ループの構成要素。
     * @param red 0.0～1.0の範囲の赤成分
     * @param green 0.0～1.0の範囲の緑成分
     * @param blue 0.0～1.0の範囲の青成分
     * @param alpha 0.0(透明)～1.0(不透明)のアルファ値。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMaterialDiffuse(float red, float green, float blue,
                               float alpha )
            throws MmdFormatException;

    /**
     * 材質の反射光成分の通知を受け取る。
     * {@link #MATERIAL_LIST}ループの構成要素。
     * @param red 0.0～1.0の範囲の赤成分
     * @param green 0.0～1.0の範囲の緑成分
     * @param blue 0.0～1.0の範囲の青成分
     * @param shininess 光沢強度(1～15ぐらい)
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMaterialSpecular(float red, float green, float blue,
                                float shininess)
            throws MmdFormatException;

    /**
     * 材質の環境色成分の通知を受け取る。
     * {@link #MATERIAL_LIST}ループの構成要素。
     * @param red 0.0～1.0の範囲の赤成分
     * @param green 0.0～1.0の範囲の緑成分
     * @param blue 0.0～1.0の範囲の青成分
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMaterialAmbient(float red, float green, float blue)
            throws MmdFormatException;

    /**
     * シェーディング情報の通知を受け取る。
     * {@link #MATERIAL_LIST}ループの構成要素。
     * @param toonIdx トゥーンファイル番号。
     * 0ならtoon01.bmp。9ならtoon10.bmp。0xffならtoon0.bmp。
     * @param textureFile テクスチャファイル名。
     * 無ければ空文字。
     * @param sphereFile スフィアマップファイル名。
     * 無ければ空文字。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMaterialShading(int toonIdx,
                               String textureFile, String sphereFile )
            throws MmdFormatException;

    /**
     * 材質情報の通知を受け取る。
     * {@link #MATERIAL_LIST}ループの構成要素。
     * @param hasEdge エッジを表示するならtrue
     * @param vertexNum 面頂点数。
     * 3の倍数のはず。
     * 3で割ると積算で表される面IDの範囲を表す。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMaterialInfo(boolean hasEdge, int vertexNum)
            throws MmdFormatException;

}
