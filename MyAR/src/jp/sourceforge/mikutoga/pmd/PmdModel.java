/*
 * PMD model
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import jp.sourceforge.mikutoga.corelib.I18nText;

/**
 * PMDモデルファイル一式に相当するもの。
 * 様々な基本構造のリストの集合から構成される。
 */
public class PmdModel {

    /** デフォルトのヘッダバージョン。 */
    public static final float DEF_HEADERVER = 1.0f;

    private float headerVersion = DEF_HEADERVER;

    private final I18nText modelName = new I18nText();
    private final I18nText description = new I18nText();

    private final List<Vertex> vertexList = new ArrayList<Vertex>();
    private final List<Surface> surfaceList = new ArrayList<Surface>();
    private final List<Material> materialList = new LinkedList<Material>();

    private final List<BoneInfo> boneList = new ArrayList<BoneInfo>();
    private final List<BoneGroup> boneGroupList = new ArrayList<BoneGroup>();

    private final List<IKChain> ikChainList = new ArrayList<IKChain>();

    private final Map<MorphType, List<MorphPart>> morphMap =
            new EnumMap<MorphType, List<MorphPart>>(MorphType.class);

    private final List<RigidInfo> rigidList = new ArrayList<RigidInfo>();
    private final List<RigidGroup> rigidGroupList =
            new ArrayList<RigidGroup>();

    private final List<JointInfo> jointList = new ArrayList<JointInfo>();

    private ToonMap toonMap = new ToonMap();

    /**
     * コンストラクタ。
     */
    public PmdModel(){
        super();

        assert this.vertexList instanceof RandomAccess;
        assert this.surfaceList instanceof RandomAccess;

        this.morphMap.put(MorphType.EYEBROW, new ArrayList<MorphPart>());
        this.morphMap.put(MorphType.EYE,     new ArrayList<MorphPart>());
        this.morphMap.put(MorphType.LIP,     new ArrayList<MorphPart>());
        this.morphMap.put(MorphType.EXTRA,   new ArrayList<MorphPart>());

        return;
    }

    /**
     * PMDファイルのヘッダバージョンを返す。
     * @return PMDファイルのヘッダバージョン
     */
    public float getHeaderVersion(){
        return this.headerVersion;
    }

    /**
     * PMDファイルのヘッダバージョンを設定する。
     * @param ver PMDファイルのヘッダバージョン
     */
    public void setHeaderVersion(float ver){
        this.headerVersion = ver;
        return;
    }

    /**
     * モデル名を返す。
     * @return モデル名
     */
    public I18nText getModelName(){
        return this.modelName;
    }

    /**
     * モデル説明文を返す。
     * 改行表現には{@literal \n}が用いられる
     * @return モデル説明文
     */
    public I18nText getDescription(){
        return this.description;
    }

    /**
     * 頂点リストを返す。
     * @return 頂点リスト。
     */
    public List<Vertex> getVertexList(){
        return this.vertexList;
    }

    /**
     * 面リストを返す。
     * @return 面リスト
     */
    public List<Surface> getSurfaceList(){
        return this.surfaceList;
    }

    /**
     * 素材リストを返す。
     * @return 素材リスト
     */
    public List<Material> getMaterialList(){
        return this.materialList;
    }

    /**
     * ボーンリストを返す。
     * @return ボーンリスト
     */
    public List<BoneInfo> getBoneList(){
        return this.boneList;
    }

    /**
     * ボーングループリストを返す。
     * @return ボーングループリスト
     */
    public List<BoneGroup> getBoneGroupList(){
        return this.boneGroupList;
    }

    /**
     * IKチェーンリストを返す。
     * @return IKチェーンリスト
     */
    public List<IKChain> getIKChainList(){
        return this.ikChainList;
    }

    /**
     * 種類別モーフリストのマップを返す。
     * @return 種類別モーフリストのマップ
     */
    public Map<MorphType, List<MorphPart>> getMorphMap(){
        return this.morphMap;
    }

    /**
     * 剛体リストを返す。
     * @return 剛体リスト
     */
    public List<RigidInfo> getRigidList(){
        return this.rigidList;
    }

    /**
     * 剛体グループリストを返す。
     * @return 剛体グループリスト。
     */
    public List<RigidGroup> getRigidGroupList(){
        return this.rigidGroupList;
    }

    /**
     * 剛体間ジョイントリストを返す。
     * @return 剛体間ジョイントリスト
     */
    public List<JointInfo> getJointList(){
        return this.jointList;
    }

    /**
     * トゥーンファイルマップを返す。
     * @return トゥーンファイルマップ
     */
    public ToonMap getToonMap(){
        return this.toonMap;
    }

    /**
     * トゥーンファイルマップを設定する。
     * 各素材のシェーディングで参照するトゥーンファイルマップも更新される。
     * @param map トゥーンファイルマップ
     */
    public void setToonMap(ToonMap map){
        this.toonMap = map;
        for(Material material : this.materialList){
            ShadeInfo info = material.getShadeInfo();
            info.setToonMap(this.toonMap);
        }
        return;
    }

    /**
     * このモデルがグローバル名を含むか判定する。
     * ボーン名、ボーングループ名、モーフ名、モデル説明文が判定対象。
     * @return グローバル名を持つならtrue
     */
    public boolean hasGlobalText(){
        if(this.modelName.hasGlobalText()) return true;
        if(this.description.hasGlobalText()) return true;

        for(BoneInfo bone : this.boneList){
            if(bone.getBoneName().hasGlobalText()) return true;
        }

        List<MorphType> typeList = new ArrayList<MorphType>();
        typeList.addAll(this.morphMap.keySet());
        for(MorphType type : typeList){
            List<MorphPart> partList = this.morphMap.get(type);
            for(MorphPart part : partList){
                if(part.getMorphName().hasGlobalText()) return true;
            }
        }

        for(BoneGroup group : this.boneGroupList){
            if(group.getGroupName().hasGlobalText()) return true;
        }

        return false;
    }

    /**
     * モーフで使われる全てのモーフ頂点のリストを返す。
     * モーフ間で重複する頂点はマージされる。
     * 頂点IDでソートされる。
     * <p>
     * 0から始まる通し番号がリナンバリングされる。
     * 通し番号は返されるモーフ頂点リストの添え字番号と一致する。
     * @return モーフに使われるモーフ頂点のリスト
     */
    public List<MorphVertex> mergeMorphVertex(){
        List<MorphVertex> result = new ArrayList<MorphVertex>();

        Set<Vertex> mergedVertexSet = new HashSet<Vertex>();
        for(MorphType type : this.morphMap.keySet()){
            if(type.isBase()) continue;
            List<MorphPart> partList = this.morphMap.get(type);
            if(partList == null) continue;
            for(MorphPart part : partList){
                for(MorphVertex morphVertex : part){
                    Vertex vertex = morphVertex.getBaseVertex();
                    if(mergedVertexSet.contains(vertex)) continue;
                    mergedVertexSet.add(vertex);
                    result.add(morphVertex);
                }
            }
        }

        Collections.sort(result, MorphVertex.VIDCOMPARATOR);
        for(int idx = 0; idx < result.size(); idx++){
            MorphVertex morphVertex = result.get(idx);
            morphVertex.setSerialNumber(idx);
        }

        Map<Vertex, MorphVertex> numberedMap =
                new HashMap<Vertex, MorphVertex>();
        for(MorphVertex morphVertex : result){
            numberedMap.put(morphVertex.getBaseVertex(), morphVertex);
        }

        for(MorphType type : this.morphMap.keySet()){
            if(type.isBase()) continue;
            List<MorphPart> partList = this.morphMap.get(type);
            if(partList == null) continue;
            for(MorphPart part : partList){
                for(MorphVertex morphVertex : part){
                    Vertex vertex = morphVertex.getBaseVertex();
                    MorphVertex numbered = numberedMap.get(vertex);
                    assert numbered != null;
                    morphVertex.setSerialNumber(numbered.getSerialNumber());
                }
            }
        }

        return result;
    }

    /**
     * 永続化可能な状態へトリミングする。
     * 各種オブジェクトの通し番号が変化する可能性がある。
     */
    public void trimming(){
        List<Surface> trimmedSurfaceList = trimmingSurfaceList();
        this.surfaceList.clear();
        this.surfaceList.addAll(trimmedSurfaceList);

        List<Vertex> trimmedVertexList = trimmingVertexList();
        this.vertexList.clear();
        this.vertexList.addAll(trimmedVertexList);

        return;
    }

    /**
     * 面リストをトリミングする。
     * 所属マテリアル順に再配置し、通し番号を割り振り直す。
     * 所属マテリアルの無い面はリストの末端に配置される。
     * 面リスト中のnullは削除され詰められる。
     * @return トリミングされた面リスト
     */
    private List<Surface> trimmingSurfaceList(){
        Set<Surface> materialedSurfaceSet = new HashSet<Surface>();
        for(Material material : this.materialList){
            if(material == null) continue;
            for(Surface surface : material){
                if(surface == null) continue;
                materialedSurfaceSet.add(surface);
            }
        }

        materialedSurfaceSet.removeAll(this.surfaceList);

        List<Surface> result = new ArrayList<Surface>();
        for(Surface surface : this.surfaceList){
            if(surface == null) continue;
            result.add(surface);
        }

        result.addAll(materialedSurfaceSet);

        int serialNum = 0;
        for(Surface surface : result){
            surface.setSerialNumber(serialNum);
            serialNum++;
        }

        return result;
    }

    /**
     * 頂点リストをトリミングする。
     * 通し番号を振り直す。
     * 所属面の無い頂点はリストの末端に配置される。
     * 頂点リスト中のnullは削除され詰められる。
     * @return トリミングされた頂点リスト
     */
    private List<Vertex> trimmingVertexList(){
        Set<Vertex> surfacedVertexSet = new HashSet<Vertex>();
        for(Surface surface : this.surfaceList){
            if(surface == null) continue;
            for(Vertex vertex : surface){
                surfacedVertexSet.add(vertex);
            }
        }

        surfacedVertexSet.removeAll(this.vertexList);

        List<Vertex> result = new ArrayList<Vertex>();
        for(Vertex vertex : this.vertexList){
            if(vertex == null) continue;
            result.add(vertex);
        }

        result.addAll(surfacedVertexSet);

        int serialNum = 0;
        for(Vertex vertex : result){
            vertex.setSerialNumber(serialNum);
            serialNum++;
        }

        return result;
    }

}
