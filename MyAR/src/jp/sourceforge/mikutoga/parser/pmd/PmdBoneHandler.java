/*
 * PMD bone information handler
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import jp.sourceforge.mikutoga.parser.LoopHandler;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.ParseStage;

/**
 * PMDモデルの各種ボーン情報の通知用ハンドラ。
 * ボーン定義の出現順と、0から始まるボーンIDは対応する。
 */
public interface PmdBoneHandler extends LoopHandler {

    /**
     * ボーン情報パースステージ。
     */
    class PmdBoneStage extends ParseStage{
        /** コンストラクタ。 */
        PmdBoneStage(){ super(); return; }
    }

    /** ボーン定義抽出ループ。 */
    PmdBoneStage BONE_LIST = new PmdBoneStage();

    /** IKリスト抽出ループ。 */
    PmdBoneStage IK_LIST = new PmdBoneStage();

    /** IKチェーンリスト抽出ループ。 */
    PmdBoneStage IKCHAIN_LIST = new PmdBoneStage();

    /** ボーングループ名抽出ループ。 */
    PmdBoneStage BONEGROUP_LIST = new PmdBoneStage();

    /** ボーングループ内訳抽出ループ。 */
    PmdBoneStage GROUPEDBONE_LIST = new PmdBoneStage();

    /**
     * ボーン定義情報の通知を受け取る。
     * {@link #BONE_LIST}ループの構成要素。
     * @param boneName ボーン名
     * @param boneKind ボーン種別。
     * <ul>
     * <li>0:回転
     * <li>1:回転/移動
     * <li>2:IK
     * <li>3:不明
     * <li>4:IK影響下(回転)
     * <li>5:回転影響下
     * <li>6:IK接続先
     * <li>7:非表示
     * <li>8:捩り
     * <li>9:回転連動
     * </ul>
     * ※8,9はMMD4.0から？
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdBoneInfo(String boneName, byte boneKind)
            throws MmdFormatException;

    /**
     * ボーン間接続情報の通知を受け取る。
     * {@link #BONE_LIST}ループの構成要素。
     * @param parentId 親(前)ボーンID。無い場合は0xffff。
     * @param tailId 子(次)ボーンID。末端の場合は0。
     * 捩りボーンの場合は軸方向のボーンID、
     * 回転連動ボーンの場合は影響元ボーンID
     * @param ikId 影響IKボーンID。未指定の場合は0。
     * ※回転連動では影響度(0-100)、負や100以上もOK!
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdBoneLink(int parentId, int tailId, int ikId)
            throws MmdFormatException;

    /**
     * ボーン位置情報の通知を受け取る。
     * {@link #BONE_LIST}ループの構成要素。
     * @param xPos X座標
     * @param yPos Y座標
     * @param zPos Z座標
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdBonePosition(float xPos, float yPos, float zPos)
            throws MmdFormatException;

    /**
     * IKボーン情報の通知を受け取る。
     * {@link #IK_LIST}ループの構成要素。
     * @param boneId IKボーンID
     * @param targetId IKボーンが最初に接続するIK接続先ボーンID
     * @param depth 再帰演算の深さ
     * @param weight 制限角度強度
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdIKInfo(int boneId, int targetId, int depth, float weight)
            throws MmdFormatException;

    /**
     * IKチェイン要素の通知を受け取る。
     * {@link #IK_LIST}ループの下位{@link #IKCHAIN_LIST}ループの構成要素。
     * @param childId IK影響下ボーンID
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdIKChainInfo(int childId)
            throws MmdFormatException;

    /**
     * ボーングループ名定義の通知を受け取る。
     * {@link #BONEGROUP_LIST}ループの構成要素。
     * @param groupName ボーングループ名。末尾のLF(0x0a)は削除される。
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdBoneGroupInfo(String groupName) throws MmdFormatException;

    /**
     * ボーングループ内訳の通知を受け取る。
     * {@link #GROUPEDBONE_LIST}ループの構成要素。
     * @param boneId グループに所属するボーンのID
     * @param groupId ボーンが所属するボーングループIDに1を足した数
     * @throws MmdFormatException 不正フォーマットによる
     * パース処理の中断をパーサに指示
     */
    void pmdGroupedBoneInfo(int boneId, int groupId)
            throws MmdFormatException;

}
