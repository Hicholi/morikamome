/*
 * pmd-xml exporter
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.xml;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.corelib.SerialNumbered;
import jp.sourceforge.mikutoga.pmd.BoneGroup;
import jp.sourceforge.mikutoga.pmd.BoneInfo;
import jp.sourceforge.mikutoga.pmd.BoneType;
import jp.sourceforge.mikutoga.pmd.Deg3d;
import jp.sourceforge.mikutoga.pmd.DynamicsInfo;
import jp.sourceforge.mikutoga.pmd.IKChain;
import jp.sourceforge.mikutoga.pmd.JointInfo;
import jp.sourceforge.mikutoga.pmd.Material;
import jp.sourceforge.mikutoga.pmd.MorphPart;
import jp.sourceforge.mikutoga.pmd.MorphType;
import jp.sourceforge.mikutoga.pmd.MorphVertex;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.Pos2d;
import jp.sourceforge.mikutoga.pmd.Pos3d;
import jp.sourceforge.mikutoga.pmd.Rad3d;
import jp.sourceforge.mikutoga.pmd.RigidGroup;
import jp.sourceforge.mikutoga.pmd.RigidInfo;
import jp.sourceforge.mikutoga.pmd.RigidShape;
import jp.sourceforge.mikutoga.pmd.RigidShapeType;
import jp.sourceforge.mikutoga.pmd.ShadeInfo;
import jp.sourceforge.mikutoga.pmd.Surface;
import jp.sourceforge.mikutoga.pmd.ToonMap;
import jp.sourceforge.mikutoga.pmd.TripletRange;
import jp.sourceforge.mikutoga.pmd.Vec3d;
import jp.sourceforge.mikutoga.pmd.Vertex;
import jp.sourceforge.mikutoga.xml.BasicXmlExporter;
import jp.sourceforge.mikutoga.xml.XmlResourceResolver;

/**
 * XML形式でPMDモデルデータを出力する。
 */
public class PmdXmlExporter extends BasicXmlExporter{

    private static final String TOP_COMMENT =
            "  MikuMikuDance\n    model-data(*.pmd) on XML";
    private static final String SCHEMA_LOCATION =
            PmdXmlResources.NS_PMDXML + " " + PmdXmlResources.SCHEMA_PMDXML;

    /** 改行文字列 CR。 */
    private static final String CR = "\r";       // 0x0d
    /** 改行文字列 LF。 */
    private static final String LF = "\n";       // 0x0a
    /** 改行文字列 CRLF。 */
    private static final String CRLF = CR + LF;  // 0x0d, 0x0a

    private static final String PFX_SURFACEGROUP = "sg";
    private static final String PFX_TOONFILE = "tf";
    private static final String PFX_VERTEX = "vtx";
    private static final String PFX_BONE = "bn";
    private static final String PFX_RIGID = "rd";
    private static final String PFX_RIGIDGROUP = "rg";

    private static final String BONETYPE_COMMENT =
          "Bone types:\n"
        + "[0 : ROTATE      : Rotate       : 回転           :]\n"
        + "[1 : ROTMOV      : Rotate/Move  : 回転/移動      :]\n"
        + "[2 : IK          : IK           : IK             :]\n"
        + "[3 : UNKNOWN     : Unknown      : 不明           :]\n"
        + "[4 : UNDERIK     : Under IK     : IK影響下(回転) :]\n"
        + "[5 : UNDERROT    : Under rotate : 回転影響下     :]\n"
        + "[6 : IKCONNECTED : IK connected : IK接続先       :]\n"
        + "[7 : HIDDEN      : Hidden       : 非表示         :]\n"
        + "[8 : TWIST       : Twist        : 捩り           :]\n"
        + "[9 : LINKEDROT   : Linked Rotate: 回転連動       :]\n";

    private static final String MORPHTYPE_COMMENT =
          "Morph types:\n"
        + "[1 : EYEBROW : まゆ   ]\n"
        + "[2 : EYE     : 目     ]\n"
        + "[3 : LIP     : リップ ]\n"
        + "[4 : EXTRA   : その他 ]\n";

    private static final String RIGIDBEHAVIOR_COMMENT =
          "Rigid behavior types:\n"
        + "[0 : FOLLOWBONE    : ボーン追従       ]\n"
        + "[1 : ONLYDYNAMICS  : 物理演算         ]\n"
        + "[2 : BONEDDYNAMICS : ボーン位置合わせ ]\n";

    private String generator = "";

    /**
     * コンストラクタ。
     * 文字エンコーディングはUTF-8が用いられる。
     * @param stream 出力ストリーム
     */
    public PmdXmlExporter(OutputStream stream){
        super(stream);
        return;
    }

    /**
     * Generatorメタ情報を設定する。
     * @param generatorArg Generatorメタ情報
     * @throws NullPointerException 引数がnull
     */
    public void setGenerator(String generatorArg)
            throws NullPointerException{
        if(generatorArg == null) throw new NullPointerException();
        this.generator = generatorArg;
        return;
    }

    /**
     * 任意の文字列がBasicLatin文字のみから構成されるか判定する。
     * @param seq 文字列
     * @return null、長さ0もしくはBasicLatin文字のみから構成されるならtrue
     */
    public static boolean hasOnlyBasicLatin(CharSequence seq){
        if(seq == null) return true;
        int length = seq.length();
        for(int pos = 0; pos < length; pos++){
            char ch = seq.charAt(pos);
            if(ch > 0x007f) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public PmdXmlExporter ind() throws IOException{
        super.ind();
        return this;
    }

    /**
     * 文字参照によるエスケープを補佐するためのコメントを出力する。
     * @param seq 文字列
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putUnescapedComment(CharSequence seq)
            throws IOException{
        if( ! isBasicLatinOnlyOut() ) return this;
        if(hasOnlyBasicLatin(seq)) return this;
        put(' ').putLineComment(seq);
        return this;
    }

    /**
     * 多言語化された各種識別名を出力する。
     * プライマリ名は出力対象外。
     * @param text 多言語文字列
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putI18nName(I18nText text) throws IOException{
        for(String lang639 : text.lang639CodeList()){
            if(lang639.equals(I18nText.CODE639_PRIMARY)) continue;
            String name = text.getText(lang639);
            ind().put("<i18nName ");
            putAttr("lang", lang639).put(' ');
            putAttr("name", name);
            put(" />");
            putUnescapedComment(name);
            ln();
        }
        return this;
    }

    /**
     * 番号付けされたID(IDREF)属性を出力する。
     * @param attrName 属性名
     * @param prefix IDプレフィクス
     * @param num 番号
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putNumberedIdAttr(CharSequence attrName,
                                                 CharSequence prefix,
                                                 int num )
            throws IOException{
        put(attrName).put("=\"");
        put(prefix).put(num);
        put('"');
        return this;
    }

    /**
     * 番号付けされたID(IDREF)属性を出力する。
     * @param attrName 属性名
     * @param prefix IDプレフィクス
     * @param numbered 番号付けされたオブジェクト
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putNumberedIdAttr(CharSequence attrName,
                                                 CharSequence prefix,
                                                 SerialNumbered numbered )
            throws IOException{
        putNumberedIdAttr(attrName, prefix, numbered.getSerialNumber());
        return this;
    }

    /**
     * 位置情報を出力する。
     * @param position 位置情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putPosition(Pos3d position) throws IOException{
        put("<position ");
        putFloatAttr("x", position.getXPos()).put(' ');
        putFloatAttr("y", position.getYPos()).put(' ');
        putFloatAttr("z", position.getZPos()).put(' ');
        put("/>");
        return this;
    }

    /**
     * 姿勢情報(ラジアン)を出力する。
     * @param rotation 姿勢情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putRadRotation(Rad3d rotation)
            throws IOException{
        put("<radRotation ");
        putFloatAttr("xRad", rotation.getXRad()).put(' ');
        putFloatAttr("yRad", rotation.getYRad()).put(' ');
        putFloatAttr("zRad", rotation.getZRad()).put(' ');
        put("/>");
        return this;
    }

    /**
     * 多言語識別名属性のローカルな名前をコメント出力する。
     * @param name 多言語識別名
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putLocalNameComment(I18nText name)
            throws IOException{
        String localName = name.getText();
        ind().putLineComment(localName);
        return this;
    }

    /**
     * 多言語識別名属性のプライマリな名前を出力する。
     * @param attrName 属性名
     * @param name 多言語識別名
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected PmdXmlExporter putPrimaryNameAttr(CharSequence attrName,
                                                   I18nText name)
            throws IOException{
        String primaryName = name.getPrimaryText();
        putAttr(attrName, primaryName);
        return this;
    }

    /**
     * PMDモデルデータをXML形式で出力する。
     * @param model PMDモデルデータ
     * @throws IOException 出力エラー
     */
    public void putPmdModel(PmdModel model) throws IOException{
        ind().put("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").ln(2);

        ind().putBlockComment(TOP_COMMENT).ln(2);

        /*
        ind().put("<!DOCTYPE pmdModel").ln();
        ind().put(" SYSTEM \"")
             .put(PmdXmlResources.DTD_PMDXML)
             .put("\" >")
             .ln(3);
         */

        I18nText modelName = model.getModelName();
        ind().putLocalNameComment(modelName).ln();
        ind().put("<pmdModel").ln();
        pushNest();
        ind().putAttr("xmlns", PmdXmlResources.NS_PMDXML).ln();
        ind().putAttr("xmlns:xsi", XmlResourceResolver.NS_XSD).ln();
        ind().putAttr("xsi:schemaLocation", SCHEMA_LOCATION).ln();
        ind().putAttr("schemaVersion", PmdXmlResources.VER_PMDXML).ln(2);
        ind().putPrimaryNameAttr("name", modelName).ln();
        popNest();
        put(">").ln(2);

        putModelInfo(model).flush();
        putMetaInfo(model).flush();
        putMaterialList(model).flush();
        putToonMap(model).flush();
        putBoneList(model).flush();
        putBoneGroupList(model).flush();
        putIKChainList(model).flush();
        putMorphList(model).flush();
        putRigidList(model).flush();
        putRigidGroupList(model).flush();
        putJointList(model).flush();
        putSurfaceGroupList(model).flush();
        putVertexList(model).flush();

        ind().put("</pmdModel>").ln(2);
        ind().put("<!-- EOF -->").ln();

        return;
    }

    /**
     * モデル基本情報を出力する。
     * @param model モデル情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putModelInfo(PmdModel model)
            throws IOException{
        I18nText modelName = model.getModelName();
        putI18nName(modelName);
        ln();

        I18nText description = model.getDescription();
        for(String lang639 : description.lang639CodeList()){
            String descText = description.getText(lang639);
            putDescription(lang639, descText);
            ln();
        }

        return this;
    }

    /**
     * モデル詳細テキストを出力する。
     * @param lang639 言語コード
     * @param content 詳細内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putDescription(CharSequence lang639,
                                              CharSequence content)
            throws IOException{
        String text = content.toString();
        text = text.replace(CRLF, LF);
        text = text.replace(CR,   LF);

        ind().put("<description");
        if( ! I18nText.CODE639_PRIMARY.equals(lang639) ){
            put(" ");
            putAttr("lang", lang639);
        }
        put(">").ln();

        putBRedContent(text);

        ln();
        ind().put("</description>").ln();

        if( ! hasOnlyBasicLatin(text) && isBasicLatinOnlyOut() ){
            putBlockComment(text);
        }

        return this;
    }

    /**
     * break要素を含む要素内容を出力する。
     * 必要に応じてXML定義済み実体文字が割り振られた文字、
     * コントロールコード、および非BasicLatin文字がエスケープされる。
     * \nはbrタグに変換される。
     * @param content 内容
     * @return this本体
     * @throws IOException 出力エラー
     */
    protected BasicXmlExporter putBRedContent(CharSequence content)
            throws IOException{
        int length = content.length();

        for(int pos = 0; pos < length; pos++){
            char ch = content.charAt(pos);
            if(ch == '\n'){
                put("<br/>").ln();
            }else if(Character.isISOControl(ch)){
                putCharRef2Hex(ch);
            }else if( ! isBasicLatin(ch) && isBasicLatinOnlyOut()){
                putCharRef4Hex(ch);
            }else{
                switch(ch){
                case '&':  put("&amp;");  break;
                case '<':  put("&lt;");   break;
                case '>':  put("&gt;");   break;
                case '"':  put("&quot;"); break;
                case '\'': put("&apos;"); break;
                default:   put(ch);       break;
                }
            }
        }

        return this;
    }

    /**
     * 各種メタ情報を出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putMetaInfo(PmdModel model) throws IOException{
        ind().put("<license>").ln();
        ind().put("</license>").ln(2);

        ind().put("<credits>").ln();
        ind().put("</credits>").ln(2);

        ind().put("<meta ");
        putAttr("name", "generator").put(' ')
                                    .putAttr("content", this.generator);
        put(" />").ln();
        ind().put("<meta ");
        putAttr("name", "siteURL").put(' ').putAttr("content", "");
        put(" />").ln();
        ind().put("<meta ");
        putAttr("name", "imageURL").put(' ').putAttr("content", "");
        put(" />").ln(2);

        return this;
    }

    /**
     * マテリアル素材一覧を出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putMaterialList(PmdModel model)
            throws IOException{
        ind().put("<materialList>").ln(2);
        pushNest();

        int ct = 0;
        for(Material material : model.getMaterialList()){
            putMaterial(material, ct++);
        }

        popNest();
        ind().put("</materialList>").ln(2);

        return this;
    }

    /**
     * マテリアル素材情報を出力する。
     * @param material マテリアル素材
     * @param no マテリアル通し番号
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putMaterial(Material material, int no)
            throws IOException{
        String bool;
        if(material.getEdgeAppearance()) bool = "true";
        else                             bool = "false";
        I18nText name = material.getMaterialName();
        String primary = name.getPrimaryText();
        String local = name.getText();

        if(local != null && local.length() > 0){
            ind().putLineComment(local).ln();
        }
        ind().put("<material ");
        if(primary != null && primary.length() > 0){
            putAttr("name", primary).put(' ');
        }

        putAttr("showEdge", bool);
        put(" ");
        putNumberedIdAttr("surfaceGroupIdRef", PFX_SURFACEGROUP, no);
        put('>').ln();
        pushNest();

        putI18nName(name);

        float[] rgba = new float[4];

        Color diffuse = material.getDiffuseColor();
        diffuse.getRGBComponents(rgba);
        ind().put("<diffuse ");
        putFloatAttr("r", rgba[0]).put(' ');
        putFloatAttr("g", rgba[1]).put(' ');
        putFloatAttr("b", rgba[2]).put(' ');
        putFloatAttr("alpha", rgba[3]).put(' ');
        put("/>").ln();

        Color specular = material.getSpecularColor();
        specular.getRGBComponents(rgba);
        float shininess = material.getShininess();
        ind().put("<specular ");
        putFloatAttr("r", rgba[0]).put(' ');
        putFloatAttr("g", rgba[1]).put(' ');
        putFloatAttr("b", rgba[2]).put(' ');
        putFloatAttr("shininess", shininess).put(' ');
        put("/>").ln();

        Color ambient = material.getAmbientColor();
        ambient.getRGBComponents(rgba);
        ind().put("<ambient ");
        putFloatAttr("r", rgba[0]).put(' ');
        putFloatAttr("g", rgba[1]).put(' ');
        putFloatAttr("b", rgba[2]).put(' ');
        put("/>").ln();

        ShadeInfo shade = material.getShadeInfo();
        String textureFileName = shade.getTextureFileName();
        String spheremapFileName = shade.getSpheremapFileName();

        if(shade.isValidToonIndex()){
            ind().put("<toon ");
            int toonIdx = shade.getToonIndex();
            putNumberedIdAttr("toonFileIdRef", PFX_TOONFILE, toonIdx);
            put(" />");
            String toonFileName = shade.getToonFileName();
            if(toonFileName != null && toonFileName.length() > 0){
                put(' ').putLineComment(toonFileName);
            }
            ln();
        }

        if(textureFileName != null && textureFileName.length() > 0){
            ind().put("<textureFile ");
            putAttr("winFileName", textureFileName);
            put(" />").ln();
        }

        if(spheremapFileName != null && spheremapFileName.length() > 0){
            ind().put("<spheremapFile ");
            putAttr("winFileName", spheremapFileName);
            put(" />").ln();
        }

        popNest();
        ind().put("</material>").ln(2);

        return this;
    }

    /**
     * トゥーンファイルマッピング情報を出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putToonMap(PmdModel model)
            throws IOException{
        ind().put("<toonMap>").ln();
        pushNest();

        ToonMap map = model.getToonMap();
        for(int index = 0; index <= 9; index++){
            ind().putToon(map, index).ln();
        }

        popNest();
        ind().put("</toonMap>").ln(2);
        return this;
    }

    /**
     * 個別のトゥーンファイル情報を出力する。
     * @param map トゥーンマップ
     * @param index インデックス値
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putToon(ToonMap map, int index)
            throws IOException{
        put("<toonDef ");
        putNumberedIdAttr("toonFileId", PFX_TOONFILE, index).put(' ');
        putIntAttr("index", index).put(' ');
        String toonFile = map.getIndexedToon(index);
        putAttr("winFileName", toonFile);
        put(" />");
        putUnescapedComment(toonFile);
        return this;
    }

    /**
     * サーフェイスグループリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putSurfaceGroupList(PmdModel model)
            throws IOException{
        ind().put("<surfaceGroupList>").ln(2);
        pushNest();

        int ct = 0;
        for(Material material : model.getMaterialList()){
            List<Surface> surfaceList = material.getSurfaceList();
            putSurfaceList(surfaceList, ct++);
        }

        popNest();
        ind().put("</surfaceGroupList>").ln(2);

        return this;
    }

    /**
     * 個別のサーフェイスグループを出力する。
     * @param surfaceList サーフェイスのリスト
     * @param index グループインデックス
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putSurfaceList(List<Surface> surfaceList,
                                              int index)
            throws IOException{
        ind().put("<surfaceGroup ");
        putNumberedIdAttr("surfaceGroupId", PFX_SURFACEGROUP, index);
        put(">").ln();
        pushNest();

        for(Surface surface : surfaceList){
            putSurface(surface);
        }

        popNest();
        ind().put("</surfaceGroup>").ln(2);

        return this;
    }

    /**
     * 個別のサーフェイスを出力する。
     * @param surface サーフェイス
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putSurface(Surface surface)
            throws IOException{
        ind().put("<surface ");

        Vertex vertex1 = surface.getVertex1();
        Vertex vertex2 = surface.getVertex2();
        Vertex vertex3 = surface.getVertex3();

        putNumberedIdAttr("vtxIdRef1", PFX_VERTEX, vertex1).put(' ');
        putNumberedIdAttr("vtxIdRef2", PFX_VERTEX, vertex2).put(' ');
        putNumberedIdAttr("vtxIdRef3", PFX_VERTEX, vertex3).put(' ');

        put("/>").ln();
        return this;
    }

    /**
     * 頂点リストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putVertexList(PmdModel model)
            throws IOException{
        ind().put("<vertexList>").ln(2);
        pushNest();

        for(Vertex vertex : model.getVertexList()){
            putVertex(vertex);
        }

        popNest();
        ind().put("</vertexList>").ln(2);

        return this;
    }

    /**
     * 個別の頂点情報を出力する。
     * @param vertex 頂点
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putVertex(Vertex vertex)
            throws IOException{
        String bool;
        if(vertex.getEdgeAppearance()) bool = "true";
        else                           bool = "false";

        ind().put("<vertex ");
        putNumberedIdAttr("vtxId", PFX_VERTEX, vertex).put(' ');
        putAttr("showEdge", bool);
        put(">").ln();
        pushNest();

        Pos3d position = vertex.getPosition();
        ind().putPosition(position).ln();

        Vec3d normal = vertex.getNormal();
        ind().put("<normal ");
        putFloatAttr("x", normal.getXVal()).put(' ');
        putFloatAttr("y", normal.getYVal()).put(' ');
        putFloatAttr("z", normal.getZVal()).put(' ');
        put("/>").ln();

        Pos2d uvPos = vertex.getUVPosition();
        ind().put("<uvMap ");
        putFloatAttr("u", uvPos.getXPos()).put(' ');
        putFloatAttr("v", uvPos.getYPos()).put(' ');
        put("/>").ln();

        BoneInfo boneA = vertex.getBoneA();
        BoneInfo boneB = vertex.getBoneB();
        int weight = vertex.getWeightA();
        ind().put("<skinning ");
        putNumberedIdAttr("boneIdRef1", PFX_BONE, boneA).put(' ');
        putNumberedIdAttr("boneIdRef2", PFX_BONE, boneB).put(' ');
        putIntAttr("weightBalance", weight).put(' ');
        put("/>").ln();

        popNest();
        ind().put("</vertex>").ln(2);

        return this;
    }

    /**
     * ボーンリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putBoneList(PmdModel model)
            throws IOException{
        ind().put("<boneList>").ln(2);
        pushNest();

        putBlockComment(BONETYPE_COMMENT).ln();

        for(BoneInfo bone : model.getBoneList()){
            putBone(bone);
        }

        popNest();
        ind().put("</boneList>").ln(2);

        return this;
    }

    /**
     * 個別のボーン情報を出力する。
     * @param bone　ボーン情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putBone(BoneInfo bone)
            throws IOException{
        I18nText i18nName = bone.getBoneName();
        BoneType type = bone.getBoneType();

        putLocalNameComment(i18nName).putLineComment(type.getGuiName()).ln();
        ind().put("<bone ");
        putPrimaryNameAttr("name", i18nName).put(' ');
        putNumberedIdAttr("boneId", PFX_BONE, bone).put(' ');
        putAttr("type", type.name());
        put(">").ln();
        pushNest();

        putI18nName(i18nName);

        Pos3d position = bone.getPosition();
        ind().putPosition(position).ln();

        BoneInfo ikBone = bone.getIKBone();
        if(bone.getBoneType() == BoneType.LINKEDROT){
            ind().put("<rotationRatio ");
            putIntAttr("ratio", bone.getRotationRatio());
            put(" />").ln();
        }else if(ikBone != null){
            ind().put("<ikBone ");
            putNumberedIdAttr("boneIdRef", PFX_BONE, ikBone);
            put(" /> ");
            String ikBoneName = "Ref:" + ikBone.getBoneName().getText();
            putLineComment(ikBoneName);
            ln();
        }

        StringBuilder chainComment = new StringBuilder();
        ind().put("<boneChain");
        BoneInfo prev = bone.getPrevBone();
        BoneInfo next = bone.getNextBone();
        if(prev != null){
            put(' ');
            putNumberedIdAttr("prevBoneIdRef", PFX_BONE, prev);
            chainComment.append('[')
                        .append(prev.getBoneName().getPrimaryText())
                        .append(']')
                        .append("=> #");
        }
        if(next != null){
            put(' ');
            putNumberedIdAttr("nextBoneIdRef", PFX_BONE, next);
            if(chainComment.length() <= 0) chainComment.append("#");
            chainComment.append(" =>")
                        .append('[')
                        .append(next.getBoneName().getPrimaryText())
                        .append(']');
        }
        put(" />").ln();
        ind().putLineComment(chainComment).ln();

        popNest();
        ind().put("</bone>").ln(2);

        return this;
    }

    /**
     * ボーングループリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putBoneGroupList(PmdModel model)
            throws IOException{
        ind().put("<boneGroupList>").ln(2);
        pushNest();

        for(BoneGroup group : model.getBoneGroupList()){
            if(group.isDefaultBoneGroup()) continue;
            putBoneGroup(group);
        }

        popNest();
        ind().put("</boneGroupList>").ln(2);

        return this;
    }

    /**
     * 個別のボーングループ情報を出力する。
     * @param group ボーングループ情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putBoneGroup(BoneGroup group)
            throws IOException{
        I18nText i18nName = group.getGroupName();

        putLocalNameComment(i18nName).ln();
        ind().put("<boneGroup ");
        putPrimaryNameAttr("name", i18nName);
        put(">").ln();
        pushNest();

        putI18nName(i18nName);

        for(BoneInfo bone : group){
            ind().put("<boneGroupMember ");
            putNumberedIdAttr("boneIdRef", PFX_BONE, bone);
            put(" /> ");
            String boneName = "Ref:" + bone.getBoneName().getText();
            putLineComment(boneName).ln();
        }

        popNest();
        ind().put("</boneGroup>").ln(2);

        return this;
    }

    /**
     * IKチェーンリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putIKChainList(PmdModel model)
            throws IOException{
        ind().put("<ikChainList>").ln(2);
        pushNest();

        for(IKChain chain : model.getIKChainList()){
            putIKChain(chain);
        }

        popNest();
        ind().put("</ikChainList>").ln(2);

        return this;
    }

    /**
     * 個別のIKチェーン情報を出力する。
     * @param chain チェーン情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putIKChain(IKChain chain)
            throws IOException{
        int depth = chain.getIKDepth();
        float weight = chain.getIKWeight();
        BoneInfo ikBone = chain.getIkBone();

        ind().putLineComment("Ref:" + ikBone.getBoneName().getText()).ln();
        ind().put("<ikChain ");
        putNumberedIdAttr("ikBoneIdRef", PFX_BONE, ikBone).put(' ');
        putIntAttr("recursiveDepth", depth).put(' ');
        putFloatAttr("weight", weight);
        put("> ").ln();
        pushNest();

        for(BoneInfo bone : chain){
            ind().put("<chainOrder ");
            putNumberedIdAttr("boneIdRef", PFX_BONE, bone);
            put(" /> ");
            putLineComment("Ref:" + bone.getBoneName().getText());
            ln();
        }

        popNest();
        ind().put("</ikChain>").ln(2);

        return this;
    }

    /**
     * モーフリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putMorphList(PmdModel model)
            throws IOException{
        ind().put("<morphList>").ln(2);
        pushNest();

        putBlockComment(MORPHTYPE_COMMENT).ln();

        Map<MorphType, List<MorphPart>> morphMap = model.getMorphMap();
        for(MorphType type : MorphType.values()){
            if(type == MorphType.BASE) continue;
            List<MorphPart> partList = morphMap.get(type);
            if(partList == null) continue;
            for(MorphPart part : partList){
                putMorphPart(part);
            }
        }

        popNest();
        ind().put("</morphList>").ln(2);

        return this;
    }

    /**
     * 個別のモーフ情報を出力する。
     * @param part モーフ情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putMorphPart(MorphPart part)
            throws IOException{
        I18nText i18nName = part.getMorphName();
        String primary = i18nName.getPrimaryText();

        ind().put("<morph ");
        putAttr("name", primary).put(' ');
        putAttr("type", part.getMorphType().name());
        put(">");
        putUnescapedComment(primary);
        ln();
        pushNest();

        putI18nName(i18nName);

        for(MorphVertex mvertex : part){
            Pos3d offset = mvertex.getOffset();
            Vertex base = mvertex.getBaseVertex();

            ind().put("<morphVertex ");
            putNumberedIdAttr("vtxIdRef", PFX_VERTEX, base).put(' ');
            putFloatAttr("xOff", offset.getXPos()).put(' ');
            putFloatAttr("yOff", offset.getYPos()).put(' ');
            putFloatAttr("zOff", offset.getZPos()).put(' ');
            put("/>");
            ln();
        }

        popNest();
        ind().put("</morph>").ln(2);

        return this;
    }

    /**
     * 剛体リストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putRigidList(PmdModel model)
            throws IOException{
        ind().put("<rigidList>").ln(2);
        pushNest();

        putBlockComment(RIGIDBEHAVIOR_COMMENT).ln();

        for(RigidInfo rigid : model.getRigidList()){
            putRigid(rigid);
        }

        popNest();
        ind().put("</rigidList>").ln(2);

        return this;
    }

    /**
     * 個別の剛体情報を出力する。
     * @param rigid 剛体情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putRigid(RigidInfo rigid)
            throws IOException{
        BoneInfo linkedBone = rigid.getLinkedBone();
        I18nText i18nName = rigid.getRigidName();
        String primary = i18nName.getPrimaryText();

        putLocalNameComment(i18nName).ln();
        ind().put("<rigid ");
        putAttr("name", primary).put(' ');
        putNumberedIdAttr("rigidId", PFX_RIGID, rigid).put(' ');
        putAttr("behavior", rigid.getBehaviorType().name());
        put(">").ln();
        pushNest();

        putI18nName(i18nName);

        if(linkedBone != null){
            ind().put("<linkedBone ");
            putNumberedIdAttr("boneIdRef", PFX_BONE, linkedBone);
            put(" /> ");
            putLineComment("Ref:" + linkedBone.getBoneName().getText());
            ln(2);
        }

        RigidShape shape = rigid.getRigidShape();
        putRigidShape(shape);

        Pos3d position = rigid.getPosition();
        ind().putPosition(position).ln();

        Rad3d rotation = rigid.getRotation();
        ind().putRadRotation(rotation).ln();

        DynamicsInfo dynamics = rigid.getDynamicsInfo();
        putDynamics(dynamics).ln();

        for(RigidGroup group : rigid.getThroughGroupColl()){
            ind().put("<throughRigidGroup ");
            putNumberedIdAttr("rigidGroupIdRef",
                              PFX_RIGIDGROUP,
                              group.getSerialNumber() + 1).put(' ');
            put(" />").ln();
        }

        popNest();
        ind().put("</rigid>").ln(2);

        return this;
    }

    /**
     * 剛体形状を出力する。
     * @param shape 剛体形状
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putRigidShape(RigidShape shape)
            throws IOException{
        RigidShapeType type = shape.getShapeType();

        switch(type){
        case BOX:
            ind().put("<rigidShapeBox ");
            putFloatAttr("width", shape.getWidth()).put(' ');
            putFloatAttr("height", shape.getHeight()).put(' ');
            putFloatAttr("depth", shape.getDepth()).put(' ');
            break;
        case SPHERE:
            ind().put("<rigidShapeSphere ");
            putFloatAttr("radius", shape.getRadius()).put(' ');
            break;
        case CAPSULE:
            ind().put("<rigidShapeCapsule ");
            putFloatAttr("height", shape.getHeight()).put(' ');
            putFloatAttr("radius", shape.getRadius()).put(' ');
            break;
        default:
            assert false;
            throw new AssertionError();
        }

        put("/>").ln();

        return this;
    }

    /**
     * 力学設定を出力する。
     * @param dynamics 力学設定
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putDynamics(DynamicsInfo dynamics)
            throws IOException{
        ind().put("<dynamics").ln();
        pushNest();
        ind().putFloatAttr("mass", dynamics.getMass()).ln();
        ind().putFloatAttr("dampingPosition",
                dynamics.getDampingPosition()).ln();
        ind().putFloatAttr("dampingRotation",
                dynamics.getDampingRotation()).ln();
        ind().putFloatAttr("restitution", dynamics.getRestitution()).ln();
        ind().putFloatAttr("friction", dynamics.getFriction()).ln();
        popNest();
        ind().put("/>").ln();

        return this;
    }

    /**
     * 剛体グループリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putRigidGroupList(PmdModel model)
            throws IOException{
        ind().put("<rigidGroupList>").ln(2);
        pushNest();

        for(RigidGroup group : model.getRigidGroupList()){
            ind().put("<rigidGroup ");
            putNumberedIdAttr("rigidGroupId",
                              PFX_RIGIDGROUP,
                              group.getSerialNumber() + 1);
            List<RigidInfo> rigidList = group.getRigidList();
            if(rigidList.size() <= 0){
                put(" />").ln(2);
                continue;
            }
            put(">").ln();
            pushNest();

            for(RigidInfo rigid : rigidList){
                ind().put("<rigidGroupMember ");
                putNumberedIdAttr("rigidIdRef", PFX_RIGID, rigid).put(' ');
                put("/>");
                put(' ');
                putLineComment("Ref:" + rigid.getRigidName().getText());
                ln();
            }

            popNest();
            ind().put("</rigidGroup>").ln(2);
        }

        popNest();
        ind().put("</rigidGroupList>").ln(2);

        return this;
    }

    /**
     * ジョイントリストを出力する。
     * @param model モデルデータ
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putJointList(PmdModel model)
            throws IOException{
        ind().put("<jointList>").ln(2);
        pushNest();

        for(JointInfo joint : model.getJointList()){
            putJoint(joint);
        }

        popNest();
        ind().put("</jointList>").ln(2);

        return this;
    }

    /**
     * 個別のジョイント情報を出力する。
     * @param joint ジョイント情報
     * @return this本体
     * @throws IOException 出力エラー
     */
    private PmdXmlExporter putJoint(JointInfo joint)
            throws IOException{
        I18nText i18nName = joint.getJointName();

        putLocalNameComment(i18nName).ln();
        ind().put("<joint ");
        putPrimaryNameAttr("name", i18nName);
        put(">").ln();
        pushNest();

        putI18nName(i18nName);

        RigidInfo rigidA = joint.getRigidA();
        RigidInfo rigidB = joint.getRigidB();
        ind().put("<jointedRigidPair ");
        putNumberedIdAttr("rigidIdRef1", PFX_RIGID, rigidA).put(' ');
        putNumberedIdAttr("rigidIdRef2", PFX_RIGID, rigidB).put(' ');
        put("/>").ln();
        ind();
        putLineComment("[" + rigidA.getRigidName().getText() + "]"
                + " <=> [" + rigidB.getRigidName().getText() + "]");
        ln(2);

        Pos3d position = joint.getPosition();
        ind().putPosition(position).ln();

        TripletRange posRange = joint.getPositionRange();
        ind().put("<limitPosition").ln();
        pushNest();
        ind();
        putFloatAttr("xFrom", posRange.getXFrom()).put(' ');
        putFloatAttr("xTo",   posRange.getXTo()).ln();
        ind();
        putFloatAttr("yFrom", posRange.getYFrom()).put(' ');
        putFloatAttr("yTo",   posRange.getYTo()).ln();
        ind();
        putFloatAttr("zFrom", posRange.getZFrom()).put(' ');
        putFloatAttr("zTo",   posRange.getZTo()).ln();
        popNest();
        ind().put("/>").ln(2);

        Rad3d rotation = joint.getRotation();
        ind().putRadRotation(rotation).ln();
        TripletRange rotRange = joint.getRotationRange();
        ind().put("<limitRotation").ln();
        pushNest();
        ind();
        putFloatAttr("xFrom", rotRange.getXFrom()).put(' ');
        putFloatAttr("xTo",   rotRange.getXTo()).ln();
        ind();
        putFloatAttr("yFrom", rotRange.getYFrom()).put(' ');
        putFloatAttr("yTo",   rotRange.getYTo()).ln();
        ind();
        putFloatAttr("zFrom", rotRange.getZFrom()).put(' ');
        putFloatAttr("zTo",   rotRange.getZTo()).ln();
        popNest();
        ind().put("/>").ln(2);

        Pos3d elaPosition = joint.getElasticPosition();
        ind().put("<elasticPosition ");
        putFloatAttr("x", elaPosition.getXPos()).put(' ');
        putFloatAttr("y", elaPosition.getYPos()).put(' ');
        putFloatAttr("z", elaPosition.getZPos()).put(' ');
        put("/>").ln();

        Deg3d elaRotation = joint.getElasticRotation();
        ind().put("<elasticRotation ");
        putFloatAttr("xDeg", elaRotation.getXDeg()).put(' ');
        putFloatAttr("yDeg", elaRotation.getYDeg()).put(' ');
        putFloatAttr("zDeg", elaRotation.getZDeg()).put(' ');
        put("/>").ln(2);

        popNest();
        ind().put("</joint>").ln(2);

        return this;
    }

}
