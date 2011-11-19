/*
 * xml loader
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.xml;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import jp.sourceforge.mikutoga.corelib.I18nText;
import jp.sourceforge.mikutoga.corelib.ListUtil;
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
import jp.sourceforge.mikutoga.pmd.RigidBehaviorType;
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
import jp.sourceforge.mikutoga.xml.DomUtils;
import jp.sourceforge.mikutoga.xml.TogaXmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML形式でのモデルファイルを読み込む。
 */
public class Xml2PmdLoader {

    private final DocumentBuilder builder;

    private PmdModel model;

    private final Map<String, Integer> toonIdxMap =
            new HashMap<String, Integer>();
    private final Map<String, BoneInfo> boneMap =
            new HashMap<String, BoneInfo>();
    private final Map<String, Vertex> vertexMap =
            new HashMap<String, Vertex>();
    private final Map<String, List<Surface>> surfaceGroupMap =
            new HashMap<String, List<Surface>>();
    private final Map<String, RigidInfo> rigidMap =
            new HashMap<String, RigidInfo>();
    private final Map<String, RigidGroup> rigidGroupMap =
            new HashMap<String, RigidGroup>();


    /**
     * コンストラクタ。
     * @param builder ビルダ
     */
    public Xml2PmdLoader(DocumentBuilder builder){
        super();
        this.builder = builder;
        return;
    }

    /**
     * 要素からxsd:string型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return 文字列
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    private static String getStringAttr(Element elem, String attrName)
            throws TogaXmlException{
        return DomUtils.getStringAttr(elem, attrName);
    }

    /**
     * 要素からxsd:boolean型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return 真ならtrue
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    private static boolean getBooleanAttr(Element elem, String attrName)
            throws TogaXmlException{
        return DomUtils.getBooleanAttr(elem, attrName);
    }

    /**
     * 要素からxsd:integer型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return int値
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    private static int getIntegerAttr(Element elem, String attrName)
            throws TogaXmlException{
        return DomUtils.getIntegerAttr(elem, attrName);
    }

    /**
     * 要素からxsd:float型属性値を読み取る。
     * @param elem 要素
     * @param attrName 属性名
     * @return float値
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    private static float getFloatAttr(Element elem, String attrName)
            throws TogaXmlException{
        return DomUtils.getFloatAttr(elem, attrName);
    }

    /**
     * 要素から日本語Windows用ファイル名を属性値として読み取る。
     * 念のため文字U+00A5は文字U-005Cに変換される。
     * @param elem 要素
     * @param attrName 属性名
     * @return ファイル名
     * @throws TogaXmlException 属性値が見つからなかった。
     */
    private static String getSjisFileNameAttr(Element elem, String attrName)
            throws TogaXmlException{
        return DomUtils.getSjisFileNameAttr(elem, attrName);
    }

    /**
     * 指定された名前の子要素を1つだけ返す。
     * @param parent 親要素
     * @param tagName 子要素名
     * @return 子要素
     * @throws TogaXmlException 1つも見つからなかった
     */
    private static Element getChild(Element parent, String tagName)
            throws TogaXmlException{
        return DomUtils.getChild(parent, tagName);
    }

    /**
     * 親要素が指定された名前の子要素を持つか判定する。
     * @param parent 親要素
     * @param tagName 子要素名
     * @return 指定名の子要素が存在すればtrue
     */
    private static boolean hasChild(Element parent, String tagName){
        return DomUtils.hasChild(parent, tagName);
    }

    /**
     * 指定された名前の子要素のforeachを返す。
     * @param parent 親要素
     * @param childTag 子要素名
     * @return 子要素のforeach
     */
    private static Iterable<Element> eachChild(Element parent,
                                                    String childTag){
        return DomUtils.getEachChild(parent, childTag);
    }

    /**
     * グローバル名を取得する。
     * 元要素のname属性及びi18nNameタグを持つ子要素が検索対象
     * @param parent 元要素
     * @return グローバル名。なければnull
     */
    private static String getGlobalName(Element parent){
        NodeList nodeList = parent.getElementsByTagName("i18nName");
        int length = nodeList.getLength();
        for(int idx = 0; idx < length; idx++){
            Node i18nNameNode = nodeList.item(idx);
            Element i18nNameElem = (Element)i18nNameNode;
            String lang = i18nNameElem.getAttribute("lang");
            if(lang == null || lang.length() <= 0) continue;
            if(lang.equals("en")){
                String name = i18nNameElem.getAttribute("name");
                return name;
            }
        }
        return null;
    }

    /**
     * brタグで区切られた文字列内容(Mixed content)を改行付き文字列に変換する。
     * brタグはその出現回数だけ\nに変換される。
     * 生文字列コンテンツ中の\n,\rは削除される。
     * 改行文字以外のホワイトスペースは保持される。
     * @param parent br要素及び文字列コンテンツを含む要素
     * @return 変換された文字列
     */
    private static String getBRedContent(Element parent){
        StringBuilder result = new StringBuilder();

        for(Node node = parent.getFirstChild();
            node != null;
            node = node.getNextSibling() ){

            switch(node.getNodeType()){
            case Node.ELEMENT_NODE:
                Element elem = (Element) node;
                if("br".equals(elem.getTagName())){
                    result.append('\n');
                }
                break;
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                String content = node.getTextContent();
                content = content.replace("\r", "");
                content = content.replace("\n", "");
                result.append(content);
                break;
            default:
                break;
            }
        }

        return result.toString();
    }

    /**
     * 多言語名を取得する。
     * @param baseElement 元要素
     * @param text 多言語名
     * @throws TogaXmlException あるべき属性が存在しない。
     */
    private static void buildI18nName(Element baseElement, I18nText text)
            throws TogaXmlException{
        String primaryText;
        primaryText = getStringAttr(baseElement, "name");
        text.setPrimaryText(primaryText);

        for(Element i18nNameElem : eachChild(baseElement, "i18nName")){
            String lang = getStringAttr(i18nNameElem, "lang");
            String name = getStringAttr(i18nNameElem, "name");
            if("en".equals(lang)){
                text.setGlobalText(name);
            }else{
                text.setText(lang, text);
            }
        }

        return;
    }

    /**
     * XMLのパースを開始する。
     * @param source XML入力
     * @return モデルデータ
     * @throws SAXException 構文エラー
     * @throws IOException 入力エラー
     * @throws TogaXmlException 構文エラー
     */
    public PmdModel parse(InputSource source)
            throws SAXException, IOException, TogaXmlException{
        Document document = this.builder.parse(source);

        this.model = new PmdModel();

        Element pmdModelElem = document.getDocumentElement();

        buildBasicInfo(pmdModelElem);

        buildBoneList(pmdModelElem);
        buildVertexList(pmdModelElem);
        buildSurfaceList(pmdModelElem);

        buildToonMap(pmdModelElem);
        buildMaterialList(pmdModelElem);
        buildIkChainList(pmdModelElem);
        buildMorphList(pmdModelElem);
        buildBoneGroupList(pmdModelElem);

        buildRigidList(pmdModelElem);
        buildRigidGroupList(pmdModelElem);
        resolveThroughRigidGroup(pmdModelElem);

        buildJointList(pmdModelElem);

        return this.model;
    }

    private void buildBasicInfo(Element pmdModelElem)
            throws TogaXmlException{
        String primaryName = getStringAttr(pmdModelElem, "name");
        String globalName = getGlobalName(pmdModelElem);

        I18nText modelName = this.model.getModelName();
        modelName.setPrimaryText(primaryName);
        modelName.setGlobalText(globalName);

        String primaryDescription = null;
        String globalDescription = null;
        for(Element descriptionElem :
            eachChild(pmdModelElem, "description")){
            String descriptionText = getBRedContent(descriptionElem);
            if( ! descriptionElem.hasAttribute("lang") ){
                primaryDescription = descriptionText;
            }else{
                String lang = getStringAttr(descriptionElem, "lang");
                if(lang.equals("ja")){
                    primaryDescription = descriptionText;
                }else if(lang.equals("en")){
                    globalDescription = descriptionText;
                }
            }
        }

        I18nText description = this.model.getDescription();
        description.setPrimaryText(primaryDescription);
        description.setGlobalText(globalDescription);

        return;
    }

    private void buildToonMap(Element pmdModelElem)
            throws TogaXmlException{
        ToonMap toonMap = this.model.getToonMap();

        Element toonMapElem = getChild(pmdModelElem, "toonMap");

        for(Element toonDefElem : eachChild(toonMapElem, "toonDef")){
            String toonFileId = getStringAttr(toonDefElem, "toonFileId");
            int toonIndex = getIntegerAttr(toonDefElem, "index");
            String toonFile = getSjisFileNameAttr(toonDefElem, "winFileName");

            toonMap.setIndexedToon(toonIndex, toonFile);
            this.toonIdxMap.put(toonFileId, toonIndex);
        }

        return;
    }

    private void buildBoneList(Element pmdModelElem)
            throws TogaXmlException{
        Element boneListElem = getChild(pmdModelElem, "boneList");

        List<BoneInfo> boneList = this.model.getBoneList();

        for(Element boneElem : eachChild(boneListElem, "bone")){
            BoneInfo boneInfo = new BoneInfo();
            boneList.add(boneInfo);

            I18nText boneName = boneInfo.getBoneName();
            buildI18nName(boneElem, boneName);

            String boneType = getStringAttr(boneElem, "type");
            BoneType type = BoneType.valueOf(boneType);
            boneInfo.setBoneType(type);

            String boneId = getStringAttr(boneElem, "boneId");
            this.boneMap.put(boneId, boneInfo);

            Element positionElem = getChild(boneElem, "position");
            float xPos = getFloatAttr(positionElem, "x");
            float yPos = getFloatAttr(positionElem, "y");
            float zPos = getFloatAttr(positionElem, "z");
            Pos3d position = boneInfo.getPosition();
            position.setXPos(xPos);
            position.setYPos(yPos);
            position.setZPos(zPos);
        }

        ListUtil.assignIndexedSerial(boneList);

        int serial = 0;
        for(Element boneElem : eachChild(boneListElem, "bone")){
            BoneInfo boneInfo = boneList.get(serial++);

            if(hasChild(boneElem, "ikBone")){
                Element ikBoneElem = getChild(boneElem, "ikBone");
                String ikBoneId = getStringAttr(ikBoneElem, "boneIdRef");
                BoneInfo ikBone = this.boneMap.get(ikBoneId);
                boneInfo.setIKBone(ikBone);
            }else if(hasChild(boneElem, "rotationRatio")){
                Element ikBoneElem = getChild(boneElem, "rotationRatio");
                int ratio = getIntegerAttr(ikBoneElem, "ratio");
                boneInfo.setRotationRatio(ratio);
            }

            Element boneChainElem = getChild(boneElem, "boneChain");
            if(boneChainElem.hasAttribute("prevBoneIdRef")){
                String prevId = getStringAttr(boneChainElem, "prevBoneIdRef");
                BoneInfo prevBone = this.boneMap.get(prevId);
                boneInfo.setPrevBone(prevBone);
            }
            if(boneChainElem.hasAttribute("nextBoneIdRef")){
                String nextId = getStringAttr(boneChainElem, "nextBoneIdRef");
                BoneInfo nextBone = this.boneMap.get(nextId);
                boneInfo.setNextBone(nextBone);
            }
        }

        return;
    }

    private void buildVertexList(Element pmdModelElem)
            throws TogaXmlException{
        Element vertexListElem = getChild(pmdModelElem, "vertexList");

        List<Vertex> vertexList = this.model.getVertexList();

        for(Element vertexElem : eachChild(vertexListElem, "vertex")){
            Vertex vertex = new Vertex();
            vertexList.add(vertex);

            String vertexId = getStringAttr(vertexElem, "vtxId");
            this.vertexMap.put(vertexId, vertex);

            boolean showEdge = getBooleanAttr(vertexElem, "showEdge");
            vertex.setEdgeAppearance(showEdge);

            float xVal;
            float yVal;
            float zVal;

            Element positionElem = getChild(vertexElem, "position");
            xVal = getFloatAttr(positionElem, "x");
            yVal = getFloatAttr(positionElem, "y");
            zVal = getFloatAttr(positionElem, "z");
            Pos3d position = vertex.getPosition();
            position.setXPos(xVal);
            position.setYPos(yVal);
            position.setZPos(zVal);

            Element normalElem = getChild(vertexElem, "normal");
            xVal = getFloatAttr(normalElem, "x");
            yVal = getFloatAttr(normalElem, "y");
            zVal = getFloatAttr(normalElem, "z");
            Vec3d normal = vertex.getNormal();
            normal.setXVal(xVal);
            normal.setYVal(yVal);
            normal.setZVal(zVal);

            Element uvElem = getChild(vertexElem, "uvMap");
            float uVal = getFloatAttr(uvElem, "u");
            float vVal = getFloatAttr(uvElem, "v");
            Pos2d uv = vertex.getUVPosition();
            uv.setXPos(uVal);
            uv.setYPos(vVal);

            Element skinningElem = getChild(vertexElem, "skinning");
            String boneId1 = getStringAttr(skinningElem, "boneIdRef1");
            String boneId2 = getStringAttr(skinningElem, "boneIdRef2");
            int weight = getIntegerAttr(skinningElem, "weightBalance");
            BoneInfo boneA = this.boneMap.get(boneId1);
            BoneInfo boneB = this.boneMap.get(boneId2);
            vertex.setBonePair(boneA, boneB);
            vertex.setWeightA(weight);
        }

        ListUtil.assignIndexedSerial(vertexList);

        return;
    }

    private void buildSurfaceList(Element pmdModelElem)
            throws TogaXmlException{
        Element surfaceGroupListElem =
                getChild(pmdModelElem, "surfaceGroupList");

        for(Element surfaceGroupElem :
            eachChild(surfaceGroupListElem, "surfaceGroup") ){

            String groupId =
                    getStringAttr(surfaceGroupElem, "surfaceGroupId");
            List<Surface> surfaceList = buildSurface(surfaceGroupElem);

            this.surfaceGroupMap.put(groupId, surfaceList);
        }
    }

    private List<Surface> buildSurface(Element surfaceGroupElem)
            throws TogaXmlException{
        List<Surface> result = new ArrayList<Surface>();

        for(Element surfaceElem : eachChild(surfaceGroupElem, "surface")){
            Surface surface = new Surface();
            result.add(surface);

            String id1 = getStringAttr(surfaceElem, "vtxIdRef1");
            String id2 = getStringAttr(surfaceElem, "vtxIdRef2");
            String id3 = getStringAttr(surfaceElem, "vtxIdRef3");

            Vertex vertex1 = this.vertexMap.get(id1);
            Vertex vertex2 = this.vertexMap.get(id2);
            Vertex vertex3 = this.vertexMap.get(id3);

            surface.setTriangle(vertex1, vertex2, vertex3);
        }

        return result;
    }

    private void buildMaterialList(Element pmdModelElem)
            throws TogaXmlException{
        Element materialListElem =
                getChild(pmdModelElem, "materialList");

        List<Surface> surfaceList = this.model.getSurfaceList();
        List<Material> materialList = this.model.getMaterialList();

        for(Element materialElem : eachChild(materialListElem, "material")){
            Material material = new Material();
            materialList.add(material);

            material.getShadeInfo().setToonMap(this.model.getToonMap());

            String surfaceGroupId =
                    getStringAttr(materialElem, "surfaceGroupIdRef");
            List<Surface> surfaceGroup =
                    this.surfaceGroupMap.get(surfaceGroupId);
            surfaceList.addAll(surfaceGroup);
            material.getSurfaceList().addAll(surfaceGroup);

            boolean hasEdge = getBooleanAttr(materialElem, "showEdge");
            material.setEdgeAppearance(hasEdge);

            ShadeInfo shadeInfo = material.getShadeInfo();

            int toonIdx;
            if(hasChild(materialElem, "toon")){
                Element toonElem = getChild(materialElem, "toon");
                String toonId = getStringAttr(toonElem, "toonFileIdRef");
                toonIdx = this.toonIdxMap.get(toonId);
            }else{
                toonIdx = 255;
            }
            shadeInfo.setToonIndex(toonIdx);

            if(hasChild(materialElem, "textureFile")){
                Element textureFileElem =
                        getChild(materialElem, "textureFile");
                String textureFile =
                        getSjisFileNameAttr(textureFileElem, "winFileName");
                shadeInfo.setTextureFileName(textureFile);
            }

            if(hasChild(materialElem, "spheremapFile")){
                Element spheremapFileElem =
                        getChild(materialElem, "spheremapFile");
                String spheremapFile =
                        getSjisFileNameAttr(spheremapFileElem, "winFileName");
                shadeInfo.setSpheremapFileName(spheremapFile);
            }

            float red;
            float green;
            float blue;

            Element diffuseElem = getChild(materialElem, "diffuse");
            red   = getFloatAttr(diffuseElem, "r");
            green = getFloatAttr(diffuseElem, "g");
            blue  = getFloatAttr(diffuseElem, "b");
            float alpha = getFloatAttr(diffuseElem, "alpha");
            Color diffuse = new Color(red, green, blue, alpha);
            material.setDiffuseColor(diffuse);

            Element specularElem = getChild(materialElem, "specular");
            red   = getFloatAttr(specularElem, "r");
            green = getFloatAttr(specularElem, "g");
            blue  = getFloatAttr(specularElem, "b");
            float shininess = getFloatAttr(specularElem, "shininess");
            Color specular = new Color(red, green, blue);
            material.setSpecularColor(specular);
            material.setShininess(shininess);

            Element ambientElem = getChild(materialElem, "ambient");
            red   = getFloatAttr(ambientElem, "r");
            green = getFloatAttr(ambientElem, "g");
            blue  = getFloatAttr(ambientElem, "b");
            Color ambient = new Color(red, green, blue);
            material.setAmbientColor(ambient);
        }

        return;
    }

    private void buildIkChainList(Element pmdModelElem)
            throws TogaXmlException{
        Element ikChainListElem =
                getChild(pmdModelElem, "ikChainList");

        List<IKChain> ikChainList = this.model.getIKChainList();

        for(Element ikChainElem : eachChild(ikChainListElem, "ikChain")){
            IKChain ikChain = new IKChain();
            ikChainList.add(ikChain);

            String ikBoneIdRef = getStringAttr(ikChainElem, "ikBoneIdRef");
            int rucursiveDepth =
                    getIntegerAttr(ikChainElem, "recursiveDepth");
            float weight = getFloatAttr(ikChainElem, "weight");

            BoneInfo ikBone = this.boneMap.get(ikBoneIdRef);
            ikChain.setIkBone(ikBone);
            ikChain.setIKDepth(rucursiveDepth);
            ikChain.setIKWeight(weight);

            List<BoneInfo> chainList = ikChain.getChainedBoneList();

            for(Element orderElem : eachChild(ikChainElem, "chainOrder")){
                String boneIdRef = getStringAttr(orderElem, "boneIdRef");
                BoneInfo chaindBone = this.boneMap.get(boneIdRef);
                chainList.add(chaindBone);
            }
        }

        return;
    }

    private void buildMorphList(Element pmdModelElem)
            throws TogaXmlException{
        Element morphListElem =
                getChild(pmdModelElem, "morphList");

        Map<MorphType, List<MorphPart>> morphMap = this.model.getMorphMap();

        for(Element morphElem : eachChild(morphListElem, "morph")){
            MorphPart morphPart = new MorphPart();

            I18nText name = morphPart.getMorphName();
            buildI18nName(morphElem, name);

            String type = getStringAttr(morphElem, "type");
            MorphType morphType = MorphType.valueOf(type);
            morphPart.setMorphType(morphType);

            List<MorphVertex> morphVertexList =
                    morphPart.getMorphVertexList();

            for(Element morphVertexElem
                    : eachChild(morphElem, "morphVertex")){
                String vtxIdRef = getStringAttr(morphVertexElem, "vtxIdRef");
                Vertex baseVertex = this.vertexMap.get(vtxIdRef);
                float xOff = getFloatAttr(morphVertexElem, "xOff");
                float yOff = getFloatAttr(morphVertexElem, "yOff");
                float zOff = getFloatAttr(morphVertexElem, "zOff");

                MorphVertex morphVertex = new MorphVertex();
                morphVertex.setBaseVertex(baseVertex);
                Pos3d position = morphVertex.getOffset();
                position.setXPos(xOff);
                position.setYPos(yOff);
                position.setZPos(zOff);

                morphVertexList.add(morphVertex);
            }

            morphMap.get(morphType).add(morphPart);
        }

        List<MorphPart> serialList = new LinkedList<MorphPart>();
        MorphPart baseDummy = new MorphPart();
        serialList.add(baseDummy);
        for(MorphPart part : morphMap.get(MorphType.EYEBROW)){
            serialList.add(part);
        }
        for(MorphPart part : morphMap.get(MorphType.EYE)){
            serialList.add(part);
        }
        for(MorphPart part : morphMap.get(MorphType.LIP)){
            serialList.add(part);
        }
        for(MorphPart part : morphMap.get(MorphType.EXTRA)){
            serialList.add(part);
        }
        ListUtil.assignIndexedSerial(serialList);

        return;
    }

    private void buildBoneGroupList(Element pmdModelElem)
            throws TogaXmlException{
        Element boneGroupListElem =
                getChild(pmdModelElem, "boneGroupList");

        List<BoneGroup> boneGroupList = this.model.getBoneGroupList();
        BoneGroup defaultGroup = new BoneGroup();
        boneGroupList.add(defaultGroup);

        for(Element boneGroupElem
                : eachChild(boneGroupListElem, "boneGroup")){
            BoneGroup group = new BoneGroup();
            boneGroupList.add(group);

            I18nText name = group.getGroupName();
            buildI18nName(boneGroupElem, name);

            for(Element boneGroupMemberElem
                    : eachChild(boneGroupElem, "boneGroupMember")){
                String boneIdRef =
                        getStringAttr(boneGroupMemberElem, "boneIdRef");
                BoneInfo bone = this.boneMap.get(boneIdRef);
                group.getBoneList().add(bone);
            }
        }

        ListUtil.assignIndexedSerial(boneGroupList);

        return;
    }

    private void buildRigidList(Element pmdModelElem)
            throws TogaXmlException{
        Element rigidListElem =
                getChild(pmdModelElem, "rigidList");

        List<RigidInfo> rigidList = this.model.getRigidList();

        for(Element rigidElem : eachChild(rigidListElem, "rigid")){
            RigidInfo rigid = new RigidInfo();
            rigidList.add(rigid);

            I18nText name = rigid.getRigidName();
            buildI18nName(rigidElem, name);

            String behavior = getStringAttr(rigidElem, "behavior");
            RigidBehaviorType type = RigidBehaviorType.valueOf(behavior);
            rigid.setBehaviorType(type);

            String rigidId = getStringAttr(rigidElem, "rigidId");
            this.rigidMap.put(rigidId, rigid);

            if(hasChild(rigidElem, "linkedBone")){
                Element linkedBoneElem = getChild(rigidElem, "linkedBone");
                String boneIdRef = getStringAttr(linkedBoneElem, "boneIdRef");
                BoneInfo linkedBone = this.boneMap.get(boneIdRef);
                rigid.setLinkedBone(linkedBone);
            }

            RigidShape rigidShape = rigid.getRigidShape();
            if(hasChild(rigidElem, "rigidShapeSphere")){
                Element shapeElem =
                        getChild(rigidElem, "rigidShapeSphere");
                float radius = getFloatAttr(shapeElem, "radius");
                rigidShape.setShapeType(RigidShapeType.SPHERE);
                rigidShape.setRadius(radius);
            }
            if(hasChild(rigidElem, "rigidShapeBox")){
                Element shapeElem =
                        getChild(rigidElem, "rigidShapeBox");
                float width  = getFloatAttr(shapeElem, "width");
                float height = getFloatAttr(shapeElem, "height");
                float depth  = getFloatAttr(shapeElem, "depth");
                rigidShape.setShapeType(RigidShapeType.BOX);
                rigidShape.setWidth(width);
                rigidShape.setHeight(height);
                rigidShape.setDepth(depth);
            }
            if(hasChild(rigidElem, "rigidShapeCapsule")){
                Element shapeElem =
                        getChild(rigidElem, "rigidShapeCapsule");
                float height = getFloatAttr(shapeElem, "height");
                float radius = getFloatAttr(shapeElem, "radius");
                rigidShape.setShapeType(RigidShapeType.CAPSULE);
                rigidShape.setHeight(height);
                rigidShape.setRadius(radius);
            }

            float xVal;
            float yVal;
            float zVal;

            Element positionElem = getChild(rigidElem, "position");
            xVal = getFloatAttr(positionElem, "x");
            yVal = getFloatAttr(positionElem, "y");
            zVal = getFloatAttr(positionElem, "z");
            Pos3d position = rigid.getPosition();
            position.setXPos(xVal);
            position.setYPos(yVal);
            position.setZPos(zVal);

            Element radRotationElem = getChild(rigidElem, "radRotation");
            xVal = getFloatAttr(radRotationElem, "xRad");
            yVal = getFloatAttr(radRotationElem, "yRad");
            zVal = getFloatAttr(radRotationElem, "zRad");
            Rad3d rotation = rigid.getRotation();
            rotation.setXRad(xVal);
            rotation.setYRad(yVal);
            rotation.setZRad(zVal);

            Element dynamicsElem = getChild(rigidElem, "dynamics");
            float mass = getFloatAttr(dynamicsElem, "mass");
            float dampingPosition =
                    getFloatAttr(dynamicsElem, "dampingPosition");
            float dampingRotation =
                    getFloatAttr(dynamicsElem, "dampingRotation");
            float restitution = getFloatAttr(dynamicsElem, "restitution");
            float friction = getFloatAttr(dynamicsElem, "friction");
            DynamicsInfo dynamics = rigid.getDynamicsInfo();
            dynamics.setMass(mass);
            dynamics.setDampingPosition(dampingPosition);
            dynamics.setDampingRotation(dampingRotation);
            dynamics.setRestitution(restitution);
            dynamics.setFriction(friction);
        }

        ListUtil.assignIndexedSerial(rigidList);

        return;
    }

    private void buildRigidGroupList(Element pmdModelElem)
            throws TogaXmlException{
        Element rigidGroupListElem =
                getChild(pmdModelElem, "rigidGroupList");

        List<RigidGroup> groupList = this.model.getRigidGroupList();

        for(Element rigidGroupElem
                : eachChild(rigidGroupListElem, "rigidGroup")){
            RigidGroup rigidGroup = new RigidGroup();
            groupList.add(rigidGroup);

            String rigidGroupId =
                    getStringAttr(rigidGroupElem, "rigidGroupId");
            this.rigidGroupMap.put(rigidGroupId, rigidGroup);

            for(Element memberElem
                    : eachChild(rigidGroupElem, "rigidGroupMember")){
                String rigidIdRef = getStringAttr(memberElem, "rigidIdRef");
                RigidInfo rigid = this.rigidMap.get(rigidIdRef);
                rigidGroup.getRigidList().add(rigid);
                rigid.setRigidGroup(rigidGroup);
            }
        }

        while(groupList.size() < 16){
            RigidGroup rigidGroup = new RigidGroup();
            groupList.add(rigidGroup);
        }

        ListUtil.assignIndexedSerial(groupList);

        return;
    }

    private void resolveThroughRigidGroup(Element pmdModelElem)
            throws TogaXmlException{
        Element rigidListElem =
                getChild(pmdModelElem, "rigidList");

        List<RigidInfo> rigidList = this.model.getRigidList();

        int serialNum = 0;
        for(Element rigidElem : eachChild(rigidListElem, "rigid")){
            RigidInfo rigid = rigidList.get(serialNum++);
            for(Element groupElem
                    : eachChild(rigidElem, "throughRigidGroup")){
                String groupId = getStringAttr(groupElem, "rigidGroupIdRef");
                RigidGroup group = this.rigidGroupMap.get(groupId);
                rigid.getThroughGroupColl().add(group);
            }
        }

        return;
    }

    private void buildJointList(Element pmdModelElem)
            throws TogaXmlException{
        Element jointListElem =
                getChild(pmdModelElem, "jointList");

        List<JointInfo> jointList = this.model.getJointList();

        for(Element jointElem : eachChild(jointListElem, "joint")){
            JointInfo joint = new JointInfo();
            jointList.add(joint);

            I18nText name = joint.getJointName();
            buildI18nName(jointElem, name);

            Element rigidPairElem = getChild(jointElem, "jointedRigidPair");
            String rigidIdRef1 = getStringAttr(rigidPairElem, "rigidIdRef1");
            String rigidIdRef2 = getStringAttr(rigidPairElem, "rigidIdRef2");
            RigidInfo rigid1 = this.rigidMap.get(rigidIdRef1);
            RigidInfo rigid2 = this.rigidMap.get(rigidIdRef2);
            joint.setRigidPair(rigid1, rigid2);

            float xVal;
            float yVal;
            float zVal;
            float xFrom;
            float xTo;
            float yFrom;
            float yTo;
            float zFrom;
            float zTo;

            Pos3d position = joint.getPosition();
            Element positionElem = getChild(jointElem, "position");
            xVal = getFloatAttr(positionElem, "x");
            yVal = getFloatAttr(positionElem, "y");
            zVal = getFloatAttr(positionElem, "z");
            position.setXPos(xVal);
            position.setYPos(yVal);
            position.setZPos(zVal);

            TripletRange limitPosition = joint.getPositionRange();
            Element limitPositionElem = getChild(jointElem, "limitPosition");
            xFrom = getFloatAttr(limitPositionElem, "xFrom");
            xTo   = getFloatAttr(limitPositionElem, "xTo");
            yFrom = getFloatAttr(limitPositionElem, "yFrom");
            yTo   = getFloatAttr(limitPositionElem, "yTo");
            zFrom = getFloatAttr(limitPositionElem, "zFrom");
            zTo   = getFloatAttr(limitPositionElem, "zTo");
            limitPosition.setXRange(xFrom, xTo);
            limitPosition.setYRange(yFrom, yTo);
            limitPosition.setZRange(zFrom, zTo);

            Rad3d rotation = joint.getRotation();
            Element rotationElem = getChild(jointElem, "radRotation");
            xVal = getFloatAttr(rotationElem, "xRad");
            yVal = getFloatAttr(rotationElem, "yRad");
            zVal = getFloatAttr(rotationElem, "zRad");
            rotation.setXRad(xVal);
            rotation.setYRad(yVal);
            rotation.setZRad(zVal);

            TripletRange limitRotation = joint.getRotationRange();
            Element limitRotationElem = getChild(jointElem, "limitRotation");
            xFrom = getFloatAttr(limitRotationElem, "xFrom");
            xTo   = getFloatAttr(limitRotationElem, "xTo");
            yFrom = getFloatAttr(limitRotationElem, "yFrom");
            yTo   = getFloatAttr(limitRotationElem, "yTo");
            zFrom = getFloatAttr(limitRotationElem, "zFrom");
            zTo   = getFloatAttr(limitRotationElem, "zTo");
            limitRotation.setXRange(xFrom, xTo);
            limitRotation.setYRange(yFrom, yTo);
            limitRotation.setZRange(zFrom, zTo);

            Pos3d elasticPosition = joint.getElasticPosition();
            Element elasticPositionElem =
                    getChild(jointElem, "elasticPosition");
            xVal = getFloatAttr(elasticPositionElem, "x");
            yVal = getFloatAttr(elasticPositionElem, "y");
            zVal = getFloatAttr(elasticPositionElem, "z");
            elasticPosition.setXPos(xVal);
            elasticPosition.setYPos(yVal);
            elasticPosition.setZPos(zVal);

            Deg3d elasticRotation = joint.getElasticRotation();
            Element elasticRotationElem =
                    getChild(jointElem, "elasticRotation");
            xVal = getFloatAttr(elasticRotationElem, "xDeg");
            yVal = getFloatAttr(elasticRotationElem, "yDeg");
            zVal = getFloatAttr(elasticRotationElem, "zDeg");
            elasticRotation.setXDeg(xVal);
            elasticRotation.setYDeg(yVal);
            elasticRotation.setZDeg(zVal);
        }

        return;
    }

}
