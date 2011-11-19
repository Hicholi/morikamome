/*
 * loop handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

/**
 * ループ構造の通知用ハンドラ。
 * ステージ指定により、多重ネストループをもサポートする。
 */
public interface LoopHandler {

    /**
     * ループ構造開始の通知を受け取る。
     * 0回ループの場合も含め一度呼ばれる。
     * @param stage ループ種別
     * @param loops ループ回数。未知の場合は負の値。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void loopStart(ParseStage stage, int loops) throws MmdFormatException;

    /**
     * ループ構造の1イテレーション終了の通知を受け取る。
     * 1度しか回らないループでも呼ばれる。0回ループでは決して呼ばれない。
     * @param stage ループ種別
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void loopNext(ParseStage stage) throws MmdFormatException;

    /**
     * ループ構造終了の通知を受け取る。
     * 0回ループの場合も含め一度呼ばれる。
     * @param stage ループ種別
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void loopEnd(ParseStage stage) throws MmdFormatException;

}
