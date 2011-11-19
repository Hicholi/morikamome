/*
 * PMD morph information handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import jp.sourceforge.mikutoga.parser.LoopHandler;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.ParseStage;

/**
 * PMDモデルのモーフ情報の通知用ハンドラ。
 */
public interface PmdMorphHandler extends LoopHandler {

    /**
     * モーフ情報パースステージ。
     */
    class PmdMorphStage extends ParseStage{
        /** コンストラクタ。 */
        PmdMorphStage(){ super(); return; }
    }

    /** モーフ抽出ループ。 */
    PmdMorphStage MORPH_LIST = new PmdMorphStage();

    /** モーフ頂点抽出ループ。 */
    PmdMorphStage MORPHVERTEX_LIST = new PmdMorphStage();

    /** モーフ出現順抽出ループ。 */
    PmdMorphStage MORPHORDER_LIST = new PmdMorphStage();

    /**
     * モーフ情報の通知を受け取る。
     * {@link #MORPH_LIST}ループの構成要素
     * @param morphName モーフ名
     * @param morphType モーフ種別。
     * <ul>
     * <li>0:base
     * <li>1:まゆ
     * <li>2:目
     * <li>3:リップ
     * <li>4:その他
     * </ul>
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMorphInfo(String morphName, byte morphType)
            throws MmdFormatException;

    /**
     * モーフ形状の通知を受け取る。
     * {@link #MORPH_LIST}ループの下位{@link #MORPHVERTEX_LIST}の構成要素
     * @param serialId base型の場合は頂点ID、それ以外はモーフ頂点ID
     * @param xPos base型の場合はX座標、それ以外はX軸変位
     * @param yPos base型の場合はY座標、それ以外はY軸変位
     * @param zPos base型の場合はZ座標、それ以外はZ軸変位
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMorphVertexInfo(int serialId,
                               float xPos, float yPos, float zPos)
            throws MmdFormatException;

    /**
     * 各モーフ種別内のGUI表示順の通知を受け取る。
     * {@link #MORPHORDER_LIST}ループの構成要素
     * @param morphId モーフ通し番号。同一種別内の大小関係のみ意味がある。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdMorphOrderInfo(int morphId) throws MmdFormatException;

}
