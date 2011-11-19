/*
 * PMD shape information handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import jp.sourceforge.mikutoga.parser.LoopHandler;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.ParseStage;

/**
 * PMDモデルの各種形状(頂点、面)の通知用ハンドラ。
 * 0から始まる頂点ID順に頂点は出現する。
 * 0から始まる面ID順に面は出現する。
 */
public interface PmdShapeHandler extends LoopHandler {

    /**
     * モデル形状パースステージ。
     */
    class PmdShapeStage extends ParseStage{
        /** コンストラクタ。 */
        PmdShapeStage(){ super(); return; }
    }

    /** 頂点抽出ループ。 */
    PmdShapeStage VERTEX_LIST = new PmdShapeStage();
    /** 面抽出ループ。 */
    PmdShapeStage SURFACE_LIST = new PmdShapeStage();

    /**
     * 頂点の座標の通知を受け取る。
     * {@link #VERTEX_LIST}ループの構成要素
     * @param xPos X座標
     * @param yPos Y座標
     * @param zPos Z座標
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdVertexPosition(float xPos, float yPos, float zPos)
            throws MmdFormatException;

    /**
     * 頂点の法線情報の通知を受け取る。
     * {@link #VERTEX_LIST}ループの構成要素
     * ※単位ベクトル化必須？
     * @param xVec 法線ベクトルX成分
     * @param yVec 法線ベクトルY成分
     * @param zVec 法線ベクトルZ成分
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdVertexNormal(float xVec, float yVec, float zVec)
            throws MmdFormatException;

    /**
     * 頂点のUVマッピング情報の通知を受け取る。
     * (頂点UV)
     * {@link #VERTEX_LIST}ループの構成要素
     * @param uVal テクスチャのU座標
     * @param vVal テクスチャのV座標
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdVertexUV(float uVal, float vVal )
            throws MmdFormatException;

    /**
     * 頂点のボーン間ウェイトバランス情報の通知を受け取る。
     * {@link #VERTEX_LIST}ループの構成要素
     * @param boneId1 ボーンその1識別ID
     * @param boneId2 ボーンその2識別ID
     * @param weightForB1 ボーンその1への影響度。0(min)～100(max)
     * ボーンその2への影響度は100からの引き算で求める。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdVertexWeight(int boneId1, int boneId2, int weightForB1)
            throws MmdFormatException;

    /**
     * 頂点のエッジ表現情報の通知を受け取る。
     * 材質単位でのエッジ表現指定に優先される。
     * {@link #VERTEX_LIST}ループの構成要素
     * @param hideEdge エッジ無効ならtrue
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdVertexEdge(boolean hideEdge)
            throws MmdFormatException;

    /**
     * 3つの頂点から構成される面情報の通知を受け取る。
     * {@link #SURFACE_LIST}ループの構成要素
     * @param vertexId1 頂点IDその1
     * @param vertexId2 頂点IDその1
     * @param vertexId3 頂点IDその1
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdSurfaceTriangle(int vertexId1, int vertexId2, int vertexId3)
            throws MmdFormatException;

}
