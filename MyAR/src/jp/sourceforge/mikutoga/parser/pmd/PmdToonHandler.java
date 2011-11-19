/*
 * PMD toon texture file information handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import jp.sourceforge.mikutoga.parser.LoopHandler;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.ParseStage;

/**
 * PMDモデルの独自トゥーンテクスチャファイル名の通知用ハンドラ。
 */
public interface PmdToonHandler extends LoopHandler {

    /**
     * 独自トゥーンテクスチャファイル名パースステージ。
     */
    class PmdToonStage extends ParseStage{
        /** コンストラクタ。 */
        PmdToonStage(){ super(); return; }
    }

    /** トゥーンテクスチャファイル名抽出ループ。 */
    PmdToonStage TOON_LIST = new PmdToonStage();

    /**
     * 独自トゥーンテクスチャファイル名の通知を受け取る。
     * {@link #TOON_LIST}ループの構成要素
     * @param toonName 独自トゥーンテクスチャファイル名
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdToonFileInfo(String toonName) throws MmdFormatException;

}
