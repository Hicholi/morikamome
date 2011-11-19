/*
 * model exporter for pmd-file
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdexporter;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;
import jp.sourceforge.mikutoga.parser.pmd.PmdLimits;
import jp.sourceforge.mikutoga.pmd.BoneGroup;
import jp.sourceforge.mikutoga.pmd.BoneInfo;
import jp.sourceforge.mikutoga.pmd.BoneType;
import jp.sourceforge.mikutoga.pmd.IKChain;
import jp.sourceforge.mikutoga.pmd.Material;
import jp.sourceforge.mikutoga.pmd.MorphPart;
import jp.sourceforge.mikutoga.pmd.MorphType;
import jp.sourceforge.mikutoga.pmd.MorphVertex;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.Pos2d;
import jp.sourceforge.mikutoga.pmd.Pos3d;
import jp.sourceforge.mikutoga.pmd.ShadeInfo;
import jp.sourceforge.mikutoga.pmd.Surface;
import jp.sourceforge.mikutoga.pmd.Vec3d;
import jp.sourceforge.mikutoga.pmd.Vertex;

/**
 * PMDファイルのエクスポーター(拡張無し基本フォーマット)。
 * <p>
 * 英名対応以降のPMDファイルフォーマットを
 * 使いたくない場合はこのエクスポーターを用いて出力せよ。
 */
public class PmdExporterBase extends AbstractExporter{

    /** 前(親)ボーンが無い場合の便宜的なボーンID。 */
    public static final int NOPREVBONE_ID = 0xffff;
    /** 次(子)ボーンが無い場合の便宜的なボーンID。 */
    public static final int NONEXTBONE_ID = 0x0000;
    /** 影響元IKボーンが無い場合の便宜的なボーンID。 */
    public static final int NOIKBONE_ID = 0x0000;

    private static final String MAGIC = "Pmd";

    private static final byte[] NULLFILLER =
        { (byte)0x00 };
    private static final byte[] FDFILLER =
        { (byte)0x00, (byte)0xfd };
    private static final byte[] LFFILLER =
        { (byte)0x0a, (byte)0x00, (byte)0xfd };

    /** 改行文字列 CR。 */
    private static final String CR = "\r";       // 0x0d
    /** 改行文字列 LF。 */
    private static final String LF = "\n";       // 0x0a
    /** 改行文字列 CRLF。 */
    private static final String CRLF = CR + LF;  // 0x0d, 0x0a

    static{
        assert NOPREVBONE_ID > PmdLimits.MAX_BONE - 1;
    }

    /**
     * コンストラクタ。
     * @param stream 出力ストリーム
     * @throws NullPointerException 引数がnull
     */
    public PmdExporterBase(OutputStream stream)
            throws NullPointerException{
        super(stream);
        return;
    }

    /**
     * 改行文字の正規化を行う。
     * CR(0x0d)およびCRLF(0x0d0a)がLF(0x0a)へと正規化される。
     * @param text 文字列
     * @return 正規化の行われた文字列。
     */
    protected static String normalizeBreak(String text){
        String result = text;

        result = result.replace(CRLF, LF);
        result = result.replace(CR, LF);

        return result;
    }

    /**
     * 文字列を指定されたバイト長で出力する。
     * 文字列の改行記号はLF(0x0a)に正規化される。
     * エンコード結果がバイト長に満たない場合は
     * 1つの0x00及びそれに続く複数の0xfdがパディングされる。
     * @param text 文字列
     * @param maxByteLength バイト長指定
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException エンコード結果が
     * 指定バイト長をはみ出した。
     */
    protected void dumpText(String text, int maxByteLength)
            throws IOException, IllegalPmdTextException{
        dumpText(text, maxByteLength, FDFILLER);
        return;
    }

    /**
     * 文字列を指定されたバイト長で出力する。
     * 文字列の改行記号はLF(0x0a)に正規化される。
     * エンコード結果がバイト長に満たない場合は
     * fillerがパディングされる。
     * @param text 文字列
     * @param maxByteLength バイト超指定
     * @param filler 出力結果が足りない場合の詰め物。
     * それでも足りない場合は最後のbyte要素が繰り返し出力される。
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException エンコード結果が
     * 指定バイト長をはみ出した
     */
    protected void dumpText(String text, int maxByteLength, byte[] filler)
            throws IOException, IllegalPmdTextException{
        String normalized = normalizeBreak(text);
        int blen = dumpCharSequence(normalized);
        int remain = maxByteLength - blen;

        if(remain < 0) throw new IllegalPmdTextException("too long text");

        int fillerIdx = 0;
        while(remain > 0){
            if(fillerIdx >= filler.length){
                fillerIdx = filler.length - 1;
            }
            dumpByte(filler[fillerIdx]);
            fillerIdx++;
            remain--;
        }

        return;
    }

    /**
     * モデルデータをPMDファイル形式で出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdException モデルデータに不備が発見された
     */
    public void dumpPmdModel(PmdModel model)
            throws IOException, IllegalPmdException{
        dumpBasic(model);
        dumpVertexList(model);
        dumpSurfaceList(model);
        dumpMaterialList(model);
        dumpBoneList(model);
        dumpIKChainList(model);
        dumpMorphList(model);
        dumpMorphGroup(model);
        dumpBoneGroupList(model);

        return;
    }

    /**
     * モデル基本情報を出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException モデル名もしくは説明が長すぎる
     */
    private void dumpBasic(PmdModel model)
            throws IOException, IllegalPmdTextException{
        dumpCharSequence(MAGIC);
        float ver = model.getHeaderVersion();
        dumpFloat(ver);

        String modelName   = model.getModelName()  .getPrimaryText();
        String description = model.getDescription().getPrimaryText();

        dumpText(modelName, PmdLimits.MAXBYTES_MODELNAME);
        dumpText(description, PmdLimits.MAXBYTES_MODELDESC);

        flush();

        return;
    }

    /**
     * 頂点リストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     */
    private void dumpVertexList(PmdModel model)
            throws IOException{
        List<Vertex> vList = model.getVertexList();

        int vertexNum = vList.size();
        dumpInt(vertexNum);

        for(Vertex vertex : vList){
            dumpVertex(vertex);
        }

        flush();

        return;
    }

    /**
     * 個別の頂点データを出力する。
     * @param vertex 頂点
     * @throws IOException 出力エラー
     */
    private void dumpVertex(Vertex vertex)
            throws IOException{
        Pos3d position = vertex.getPosition();
        dumpPos3d(position);

        Vec3d normal = vertex.getNormal();
        dumpVec3d(normal);

        Pos2d uv = vertex.getUVPosition();
        dumpPos2d(uv);

        BoneInfo boneA = vertex.getBoneA();
        BoneInfo boneB = vertex.getBoneB();
        dumpSerialIdAsShort(boneA);
        dumpSerialIdAsShort(boneB);

        int weight = vertex.getWeightA();
        dumpByte((byte)weight);

        byte edgeFlag;
        boolean hasEdge = vertex.getEdgeAppearance();
        if(hasEdge) edgeFlag = 0x00;
        else        edgeFlag = 0x01;
        dumpByte(edgeFlag);

        return;
    }

    /**
     * 面リストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     */
    private void dumpSurfaceList(PmdModel model)
            throws IOException{
        int surfaceNum = 0;
        List<Material> materialList = model.getMaterialList();
        for(Material material : materialList){
            surfaceNum += material.getSurfaceList().size();
        }
        dumpInt(surfaceNum * 3);

        Vertex[] triangle = new Vertex[3];
        for(Material material : materialList){
            for(Surface surface : material){
                surface.getTriangle(triangle);
                dumpShort(triangle[0].getSerialNumber());
                dumpShort(triangle[1].getSerialNumber());
                dumpShort(triangle[2].getSerialNumber());
            }
        }

        flush();

        return;
    }

    /**
     * マテリアル素材リストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException シェーディングファイル情報が長すぎる
     */
    private void dumpMaterialList(PmdModel model)
            throws IOException, IllegalPmdTextException{
        List<Material> materialList = model.getMaterialList();

        int materialNum = materialList.size();
        dumpInt(materialNum);

        float[] rgba = new float[4];

        for(Material material : materialList){
            Color diffuse = material.getDiffuseColor();
            diffuse.getRGBComponents(rgba);
            dumpFloat(rgba[0]);
            dumpFloat(rgba[1]);
            dumpFloat(rgba[2]);
            dumpFloat(rgba[3]);

            float shininess = material.getShininess();
            dumpFloat(shininess);

            Color specular = material.getSpecularColor();
            specular.getRGBComponents(rgba);
            dumpFloat(rgba[0]);
            dumpFloat(rgba[1]);
            dumpFloat(rgba[2]);

            Color ambient = material.getAmbientColor();
            ambient.getRGBComponents(rgba);
            dumpFloat(rgba[0]);
            dumpFloat(rgba[1]);
            dumpFloat(rgba[2]);

            ShadeInfo shade = material.getShadeInfo();
            int toonIdx = shade.getToonIndex();
            dumpByte(toonIdx);

            byte edgeFlag;
            boolean showEdge = material.getEdgeAppearance();
            if(showEdge) edgeFlag = 0x01;
            else         edgeFlag = 0x00;
            dumpByte(edgeFlag);

            int surfaceNum = material.getSurfaceList().size();
            dumpInt(surfaceNum * 3);

            dumpShadeFileInfo(shade);
        }

        flush();

        return;
    }

    /**
     * シェーディングファイル情報を出力する。
     * @param shade シェーディング情報
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException ファイル名が長すぎる
     */
    private void dumpShadeFileInfo(ShadeInfo shade)
            throws IOException, IllegalPmdTextException{
        String textureFile   = shade.getTextureFileName();
        String spheremapFile = shade.getSpheremapFileName();

        StringBuilder text = new StringBuilder();
        if(textureFile != null) text.append(textureFile);
        if(spheremapFile != null && spheremapFile.length() > 0){
            text.append('*')
                  .append(spheremapFile);
        }

        byte[] filler;
        if(text.length() <= 0) filler = NULLFILLER;
        else                   filler = FDFILLER;

        dumpText(text.toString(),
                 PmdLimits.MAXBYTES_TEXTUREFILENAME,
                 filler );

        return;
    }

    /**
     * ボーンリストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException ボーン名が長すぎる
     */
    private void dumpBoneList(PmdModel model)
            throws IOException, IllegalPmdTextException{
        List<BoneInfo> boneList = model.getBoneList();

        int boneNum = boneList.size();
        dumpShort(boneNum);

        for(BoneInfo bone : boneList){
            dumpBone(bone);
        }

        flush();

        return;
    }

    /**
     * 個別のボーン情報を出力する。
     * @param bone ボーン情報
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException ボーン名が長すぎる
     */
    private void dumpBone(BoneInfo bone)
            throws IOException, IllegalPmdTextException{
        String boneName = bone.getBoneName().getPrimaryText();
        dumpText(boneName, PmdLimits.MAXBYTES_BONENAME);

        BoneInfo prev = bone.getPrevBone();
        if(prev != null) dumpSerialIdAsShort(prev);
        else             dumpShort(NOPREVBONE_ID);

        BoneInfo next = bone.getNextBone();
        if(next != null) dumpSerialIdAsShort(next);
        else             dumpShort(NONEXTBONE_ID);

        BoneType type = bone.getBoneType();
        dumpByte(type.encode());

        if(type == BoneType.LINKEDROT){
            int ratio = bone.getRotationRatio();
            dumpShort(ratio);
        }else{
            BoneInfo ik = bone.getIKBone();
            if(ik != null) dumpSerialIdAsShort(ik);
            else           dumpShort(NOIKBONE_ID);
        }

        Pos3d position = bone.getPosition();
        dumpPos3d(position);

        return;
    }

    /**
     * IKチェーンリストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     */
    private void dumpIKChainList(PmdModel model)
            throws IOException{
        List<IKChain> ikChainList = model.getIKChainList();

        int ikNum = ikChainList.size();
        dumpShort(ikNum);

        for(IKChain chain : ikChainList){
            dumpIKChain(chain);
        }

        flush();

        return;
    }

    /**
     * IKチェーンを出力する。
     * @param chain IKチェーン
     * @throws IOException 出力エラー
     */
    // TODO ボーンリストから自動抽出できる情報ではないのか？
    private void dumpIKChain(IKChain chain)
            throws IOException{
        BoneInfo ikBone = chain.getIkBone();
        dumpSerialIdAsShort(ikBone);

        List<BoneInfo> boneList = chain.getChainedBoneList();

        BoneInfo bone1st = boneList.get(0);
        dumpSerialIdAsShort(bone1st);

        int boneNum = boneList.size();
        dumpByte(boneNum - 1);

        int depth = chain.getIKDepth();
        float weight = chain.getIKWeight();

        dumpShort(depth);
        dumpFloat(weight);

        for(int idx = 1; idx < boneNum; idx++){ // リストの2番目以降全て
            BoneInfo bone = boneList.get(idx);
            dumpSerialIdAsShort(bone);
        }

        return;
    }

    /**
     * モーフリストを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException モーフ名が長すぎる
     */
    private void dumpMorphList(PmdModel model)
            throws IOException, IllegalPmdTextException{
        Map<MorphType, List<MorphPart>> morphMap = model.getMorphMap();
        Set<MorphType> typeSet = morphMap.keySet();
        List<MorphVertex> mergedMorphVertexList = model.mergeMorphVertex();

        int totalMorphPart = 0;
        for(MorphType type : typeSet){
            List<MorphPart> partList = morphMap.get(type);
            if(partList == null) continue;
            totalMorphPart += partList.size();
        }

        if(totalMorphPart <= 0){
            dumpShort(0);
            return;
        }else{
            totalMorphPart++;  // baseの分
            dumpShort(totalMorphPart);
        }

        dumpText("base", PmdLimits.MAXBYTES_MORPHNAME);
        int totalVertex = mergedMorphVertexList.size();
        dumpInt(totalVertex);
        dumpByte(MorphType.BASE.encode());
        for(MorphVertex morphVertex : mergedMorphVertexList){
            Vertex baseVertex = morphVertex.getBaseVertex();
            dumpInt(baseVertex.getSerialNumber());
            dumpPos3d(baseVertex.getPosition());
        }

        for(MorphType type : typeSet){
            List<MorphPart> partList = morphMap.get(type);
            if(partList == null) continue;
            for(MorphPart part : partList){
                dumpText(part.getMorphName().getPrimaryText(),
                         PmdLimits.MAXBYTES_MORPHNAME );
                List<MorphVertex> morphVertexList = part.getMorphVertexList();
                dumpInt(morphVertexList.size());
                dumpByte(part.getMorphType().encode());

                for(MorphVertex morphVertex : morphVertexList){
                    dumpInt(morphVertex.getSerialNumber());
                    dumpPos3d(morphVertex.getOffset());
                }
            }
        }

        flush();

        return;
    }

    /**
     * モーフグループを出力する。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     */
    private void dumpMorphGroup(PmdModel model)
            throws IOException{
        Map<MorphType, List<MorphPart>> morphMap = model.getMorphMap();
        Set<MorphType> typeSet = morphMap.keySet();

        int totalMorph = 0;
        for(MorphType type : typeSet){
            List<MorphPart> partList = morphMap.get(type);
            if(partList == null) continue;
            totalMorph += partList.size();
        }
        dumpByte(totalMorph);

        List<MorphType> typeList = new LinkedList<MorphType>();
        for(MorphType type : typeSet){
            assert ! type.isBase();
            typeList.add(type);
        }
        Collections.reverse(typeList);  // 一応本家と互換性を

        for(MorphType type : typeList){
            List<MorphPart> partList = morphMap.get(type);
            if(partList == null) continue;
            for(MorphPart part : partList){
                dumpSerialIdAsShort(part);
            }
        }

        flush();

        return;
    }

    /**
     * ボーングループリストを出力する。
     * デフォルトボーングループ内訳は出力されない。
     * @param model モデルデータ
     * @throws IOException 出力エラー
     * @throws IllegalPmdTextException ボーングループ名が長すぎる
     */
    private void dumpBoneGroupList(PmdModel model)
            throws IOException, IllegalPmdTextException{
        List<BoneGroup> groupList = model.getBoneGroupList();
        int groupNum = groupList.size();
        dumpByte(groupNum - 1);

        int dispBoneNum = 0;
        for(BoneGroup group : groupList){
            if(group.isDefaultBoneGroup()) continue;
            dumpText(group.getGroupName().getPrimaryText(),
                     PmdLimits.MAXBYTES_BONEGROUPNAME, LFFILLER );
            dispBoneNum += group.getBoneList().size();
        }
        dumpInt(dispBoneNum);

        for(BoneGroup group : groupList){
            if(group.isDefaultBoneGroup()) continue;
            for(BoneInfo bone : group){
                dumpSerialIdAsShort(bone);
                int groupId = group.getSerialNumber();
                dumpByte(groupId);
            }
        }

        flush();

        return;
    }

    /**
     * 各種通し番号をshort値で出力する。
     * short値に収まらない上位ビットは捨てられる。
     * @param obj 番号づけられたオブジェクト
     * @throws IOException 出力エラー
     */
    protected void dumpSerialIdAsShort(SerialNumbered obj)
            throws IOException{
        int serialId = obj.getSerialNumber();
        dumpShort(serialId);
        return;
    }

    /**
     * 2次元位置情報を出力する。
     * @param position 2次元位置情報
     * @throws IOException 出力エラー
     */
    protected void dumpPos2d(Pos2d position) throws IOException{
        float xPos = position.getXPos();
        float yPos = position.getYPos();

        dumpFloat(xPos);
        dumpFloat(yPos);

        return;
    }

    /**
     * 3次元位置情報を出力する。
     * @param position 3次元位置情報
     * @throws IOException 出力エラー
     */
    protected void dumpPos3d(Pos3d position) throws IOException{
        float xPos = position.getXPos();
        float yPos = position.getYPos();
        float zPos = position.getZPos();

        dumpFloat(xPos);
        dumpFloat(yPos);
        dumpFloat(zPos);

        return;
    }

    /**
     * 3次元ベクトル情報を出力する。
     * @param vector 3次元ベクトル
     * @throws IOException 出力エラー
     */
    protected void dumpVec3d(Vec3d vector) throws IOException{
        float xVal = vector.getXVal();
        float yVal = vector.getYVal();
        float zVal = vector.getZVal();

        dumpFloat(xVal);
        dumpFloat(yVal);
        dumpFloat(zVal);

        return;
    }

}
