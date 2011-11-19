/*
 * pmd file parser
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import java.io.IOException;
import jp.sourceforge.mikutoga.parser.CommonParser;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.MmdSource;

/**
 * PMDモデルファイルのパーサ基本部。
 */
public class PmdParserBase extends CommonParser {

    /** 改行文字列 CR。 */
    protected static final String CR = "\r";       // 0x0d
    /** 改行文字列 LF。 */
    protected static final String LF = "\n";       // 0x0a
    /** 改行文字列 CRLF。 */
    protected static final String CRLF = CR + LF;  // 0x0d, 0x0a

    private static final String MAGIC = "Pmd";
    private static final int MAGIC_SZ = MAGIC.getBytes(CS_WIN31J).length;

    private static final int VERTEX_DATA_SZ      = 38;
    private static final int SURFACE_DATA_SZ     =  6;
    private static final int MATERIAL_DATA_SZ    = 70;
    private static final int BONE_DATA_SZ        = 39;
    private static final int MORPHVERTEX_DATA_SZ = 16;
    private static final int MORPHORDER_DATA_SZ  =  2;
    private static final int BONEGROUP_DATA_SZ   = 50;
    private static final int GROUPEDBONE_DATA_SZ =  3;


    private PmdBasicHandler basicHandler = null;
    private PmdShapeHandler shapeHandler = null;
    private PmdMaterialHandler materialHandler = null;
    private PmdBoneHandler boneHandler = null;
    private PmdMorphHandler morphHandler = null;

    private int boneCount      = -1;
    private int morphCount     = -1;
    private int boneGroupCount = -1;


    /**
     * コンストラクタ。
     * @param source 入力ソース
     */
    public PmdParserBase(MmdSource source){
        super(source);
        return;
    }

    /**
     * 文字列の最後がLF(0x0a)の場合削除する。
     * @param name 文字列
     * @return 末尾LFが削除された文字列
     */
    public static String chopLastLF(String name){
        String result;

        if(name.endsWith(LF)){
            result = name.substring(0, name.length() - 1);
        }else{
            result = name;
        }

        return result;
    }

    /**
     * シェーディング用ファイル情報から
     * テクスチャファイル名とスフィアマップファイル名を分離する。
     * @param shadingFile シェーディング用ファイル情報
     * @return [0]:テクスチャファイル名 [1]:スフィアマップファイル名。
     * 該当ファイル名が無い場合は空文字列。
     */
    public static String[] splitShadingFileInfo(String shadingFile){
        String[] result;

        result = shadingFile.split('\\'+"*", 2);
        assert result.length == 1 || result.length == 2;

        if(result.length == 1){
            String onlyFile = result[0];
            result = new String[2];
            result[0] = "";
            result[1] = "";
            if(onlyFile.endsWith(".sph") || onlyFile.endsWith(".spa")){
                result[1] = onlyFile;
            }else{
                result[0] = onlyFile;
            }
        }

        assert result.length == 2;

        return result;
    }

    /**
     * 基本情報通知ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setBasicHandler(PmdBasicHandler handler){
        this.basicHandler = handler;
        return;
    }

    /**
     * 形状情報通知ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setShapeHandler(PmdShapeHandler handler){
        this.shapeHandler = handler;
        return;
    }

    /**
     * 材質情報通知ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setMaterialHandler(PmdMaterialHandler handler){
        this.materialHandler = handler;
        return;
    }

    /**
     * ボーン情報通知ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setBoneHandler(PmdBoneHandler handler){
        this.boneHandler = handler;
        return;
    }

    /**
     * モーフ情報通知ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setMorphHandler(PmdMorphHandler handler){
        this.morphHandler = handler;
        return;
    }

    /**
     * パースによって得られたボーン数を返す。
     * @return ボーン数
     */
    protected int getBoneCount(){
        return this.boneCount;
    }

    /**
     * パースによって得られたモーフ数を返す。
     * @return モーフ数
     */
    protected int getMorphCount(){
        return this.morphCount;
    }

    /**
     * パースによって得られたボーングループ数を返す。
     * @return ボーングループ数
     */
    protected int getBoneGroupCount(){
        return this.boneGroupCount;
    }

    /**
     * PMDファイルのパースを開始する。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    public void parsePmd()
            throws IOException, MmdFormatException {
        if(this.basicHandler != null){
            this.basicHandler.pmdParseStart();
        }

        parseBody();

        boolean hasMoreData = hasMore();
        if(this.basicHandler != null){
            this.basicHandler.pmdParseEnd(hasMoreData);
        }

        return;
    }

    /**
     * PMDファイル本体のパースを開始する。
     * パーサを拡張する場合はこのメソッドをオーバーライドする。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    protected void parseBody() throws IOException, MmdFormatException{
        parsePmdHeader();

        parseVertexList();
        parseSurfaceList();
        parseMaterialList();
        parseBoneList();
        parseIKList();
        parseMorphList();
        parseMorphOrderList();
        parseBoneGroupList();
        parseGroupedBoneList();

        return;
    }

    /**
     * PMDファイルヘッダ部のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parsePmdHeader() throws IOException, MmdFormatException{
        String magic = parseZeroTermString(MAGIC_SZ);
        if( ! magic.equals(MAGIC) ){
            throw new MmdFormatException("unrecognized magic data");
        }

        float ver = parseFloat();
        String modelName   =
                parseZeroTermString(PmdLimits.MAXBYTES_MODELNAME);
        String description =
                parseZeroTermString(PmdLimits.MAXBYTES_MODELDESC);
        description = description.replace(CRLF, LF);

        if(this.basicHandler != null){
            this.basicHandler.pmdHeaderInfo(ver);
            this.basicHandler.pmdModelInfo(modelName, description);
        }

        return;
    }

    /**
     * 頂点情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseVertexList() throws IOException, MmdFormatException{
        int vertexNum = parseInteger();

        if(this.shapeHandler == null){
            skip(VERTEX_DATA_SZ * vertexNum);
            return;
        }

        this.shapeHandler.loopStart(PmdShapeHandler.VERTEX_LIST, vertexNum);

        for(int ct = 0; ct < vertexNum; ct++){
            float xPos = parseFloat();
            float yPos = parseFloat();
            float zPos = parseFloat();
            this.shapeHandler.pmdVertexPosition(xPos, yPos, zPos);

            float xVec = parseFloat();
            float yVec = parseFloat();
            float zVec = parseFloat();
            this.shapeHandler.pmdVertexNormal(xVec, yVec, zVec);

            float uVal = parseFloat();
            float vVal = parseFloat();
            this.shapeHandler.pmdVertexUV(uVal, vVal);

            int boneId1 = parseUShortAsInteger();
            int boneId2 = parseUShortAsInteger();
            int weightForB1 = parseUByteAsInteger();
            this.shapeHandler.pmdVertexWeight(boneId1, boneId2, weightForB1);

            boolean hideEdge = parseBoolean();
            this.shapeHandler.pmdVertexEdge(hideEdge);

            this.shapeHandler.loopNext(PmdShapeHandler.VERTEX_LIST);
        }

        this.shapeHandler.loopEnd(PmdShapeHandler.VERTEX_LIST);

        return;
    }

    /**
     * 面情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseSurfaceList() throws IOException, MmdFormatException{
        int vertexNum = parseInteger();
        if(vertexNum % 3 != 0) throw new MmdFormatException();
        int surfaceNum = vertexNum / 3;

        if(this.shapeHandler == null){
            skip(SURFACE_DATA_SZ * surfaceNum);
            return;
        }

        this.shapeHandler.loopStart(PmdShapeHandler.SURFACE_LIST, surfaceNum);

        for(int ct = 0; ct < surfaceNum; ct++){
            int vertexId1 = parseUShortAsInteger();
            int vertexId2 = parseUShortAsInteger();
            int vertexId3 = parseUShortAsInteger();
            this.shapeHandler.pmdSurfaceTriangle(vertexId1,
                                                 vertexId2,
                                                 vertexId3 );
            this.shapeHandler.loopNext(PmdShapeHandler.SURFACE_LIST);
        }

        this.shapeHandler.loopEnd(PmdShapeHandler.SURFACE_LIST);

        return;
    }

    /**
     * 材質情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseMaterialList() throws IOException, MmdFormatException{
        int materialNum = parseInteger();

        if(this.materialHandler == null){
            skip(MATERIAL_DATA_SZ * materialNum);
            return;
        }

        this.materialHandler.loopStart(PmdMaterialHandler.MATERIAL_LIST,
                                       materialNum );

        for(int ct = 0; ct < materialNum; ct++){
            float red;
            float green;
            float blue;

            red   = parseFloat();
            green = parseFloat();
            blue  = parseFloat();
            float alpha = parseFloat();
            this.materialHandler.pmdMaterialDiffuse(red, green, blue, alpha);

            float shininess = parseFloat();
            red   = parseFloat();
            green = parseFloat();
            blue  = parseFloat();
            this.materialHandler.pmdMaterialSpecular(red, green, blue,
                                                     shininess);

            red   = parseFloat();
            green = parseFloat();
            blue  = parseFloat();
            this.materialHandler.pmdMaterialAmbient(red, green, blue);

            int toonidx = parseUByteAsInteger();
            boolean hasEdge = parseBoolean();
            int surfaceCount = parseInteger();
            String shadingFile =
                    parseZeroTermString(PmdLimits.MAXBYTES_TEXTUREFILENAME);
            String[] splitted = splitShadingFileInfo(shadingFile);
            String textureFile = splitted[0];
            String sphereFile = splitted[1];

            this.materialHandler.pmdMaterialShading(toonidx,
                                                    textureFile, sphereFile );
            this.materialHandler.pmdMaterialInfo(hasEdge, surfaceCount);

            this.materialHandler.loopNext(PmdMaterialHandler.MATERIAL_LIST);
        }

        this.materialHandler.loopEnd(PmdMaterialHandler.MATERIAL_LIST);

        return;
    }

    /**
     * ボーン情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseBoneList() throws IOException, MmdFormatException{
        this.boneCount = parseUShortAsInteger();

        if(this.boneHandler == null){
            skip(BONE_DATA_SZ * this.boneCount);
            return;
        }

        this.boneHandler.loopStart(PmdBoneHandler.BONE_LIST, this.boneCount);

        for(int ct = 0; ct < this.boneCount; ct++){
            String boneName =
                    parseZeroTermString(PmdLimits.MAXBYTES_BONENAME);
            int parentId = parseUShortAsInteger();
            int tailId = parseUShortAsInteger();
            byte boneKind = parseByte();
            int ikId = parseUShortAsInteger();

            this.boneHandler.pmdBoneInfo(boneName, boneKind);
            this.boneHandler.pmdBoneLink(parentId, tailId, ikId);

            float xPos = parseFloat();
            float yPos = parseFloat();
            float zPos = parseFloat();

            this.boneHandler.pmdBonePosition(xPos, yPos, zPos);

            this.boneHandler.loopNext(PmdBoneHandler.BONE_LIST);
        }

        this.boneHandler.loopEnd(PmdBoneHandler.BONE_LIST);

        return;
    }

    /**
     * IKリスト情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseIKList() throws IOException, MmdFormatException{
        int ikCount = parseUShortAsInteger();

        if(this.boneHandler != null){
            this.boneHandler.loopStart(PmdBoneHandler.IK_LIST, ikCount);
        }

        for(int ct = 0; ct < ikCount; ct++){
            int boneId = parseUShortAsInteger();
            int targetId = parseUShortAsInteger();
            int chainLength = parseUByteAsInteger();
            int depth = parseUShortAsInteger();
            float weight = parseFloat();

            parseIKChainList(chainLength);

            if(this.boneHandler != null){
                this.boneHandler.pmdIKInfo(boneId, targetId, depth, weight);
                this.boneHandler.loopNext(PmdBoneHandler.IK_LIST);
            }
        }

        if(this.boneHandler != null){
            this.boneHandler.loopEnd(PmdBoneHandler.IK_LIST);
        }

        return;
    }

    /**
     * IKチェーン情報のパースと通知。
     * @param chainLength チェーン長
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseIKChainList(int chainLength)
            throws IOException, MmdFormatException{
        if(this.boneHandler != null){
            this.boneHandler.loopStart(PmdBoneHandler.IKCHAIN_LIST,
                                       chainLength);
        }

        for(int ct = 0; ct < chainLength; ct++){
            int childId = parseUShortAsInteger();
            if(this.boneHandler != null){
                this.boneHandler.pmdIKChainInfo(childId);
                this.boneHandler.loopNext(PmdBoneHandler.IKCHAIN_LIST);
            }
        }

        if(this.boneHandler != null){
            this.boneHandler.loopEnd(PmdBoneHandler.IKCHAIN_LIST);
        }

        return;
    }

    /**
     * モーフ情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseMorphList() throws IOException, MmdFormatException{
        this.morphCount = parseUShortAsInteger();

        if(this.morphHandler != null){
            this.morphHandler.loopStart(PmdMorphHandler.MORPH_LIST,
                                        this.morphCount );
        }

        for(int ct = 0; ct < this.morphCount; ct++){
            String morphName =
                    parseZeroTermString(PmdLimits.MAXBYTES_MORPHNAME);
            int vertexCount = parseInteger();
            byte morphType = parseByte();

            if(this.morphHandler != null){
                this.morphHandler.pmdMorphInfo(morphName, morphType);
            }

            parseMorphVertexList(vertexCount);

            if(this.morphHandler != null){
                this.morphHandler.loopNext(PmdMorphHandler.MORPH_LIST);
            }
        }

        if(this.morphHandler != null){
            this.morphHandler.loopEnd(PmdMorphHandler.MORPH_LIST);
        }

        return;
    }

    /**
     * モーフ形状のパースと通知。
     * @param vertexCount 頂点数
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseMorphVertexList(int vertexCount)
            throws IOException, MmdFormatException{
        if(this.morphHandler == null){
            skip(MORPHVERTEX_DATA_SZ * vertexCount);
            return;
        }

        this.morphHandler.loopStart(PmdMorphHandler.MORPHVERTEX_LIST,
                                    vertexCount );

        for(int ct = 0; ct < vertexCount; ct++){
            int vertexId = parseInteger();
            float xPos = parseFloat();
            float yPos = parseFloat();
            float zPos = parseFloat();
            this.morphHandler.pmdMorphVertexInfo(vertexId, xPos, yPos, zPos);
            this.morphHandler.loopNext(PmdMorphHandler.MORPHVERTEX_LIST);
        }

        this.morphHandler.loopEnd(PmdMorphHandler.MORPHVERTEX_LIST);

        return;
    }

    /**
     * モーフGUI表示順のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseMorphOrderList()
            throws IOException, MmdFormatException{
        int morphOrderCount = parseUByteAsInteger();

        if(this.morphHandler == null){
            skip(MORPHORDER_DATA_SZ * morphOrderCount);
            return;
        }

        this.morphHandler.loopStart(PmdMorphHandler.MORPHORDER_LIST,
                                    morphOrderCount );

        for(int ct = 0; ct < morphOrderCount; ct++){
            int morphId = parseUShortAsInteger();
            this.morphHandler.pmdMorphOrderInfo(morphId);

            this.morphHandler.loopNext(PmdMorphHandler.MORPHORDER_LIST);
        }

        this.morphHandler.loopEnd(PmdMorphHandler.MORPHORDER_LIST);

        return;
    }

    /**
     * ボーングループ名のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseBoneGroupList()
            throws IOException, MmdFormatException{
        this.boneGroupCount = parseUByteAsInteger();

        if(this.boneHandler == null){
            skip(BONEGROUP_DATA_SZ * this.boneGroupCount);
            return;
        }

        this.boneHandler.loopStart(PmdBoneHandler.BONEGROUP_LIST,
                                   this.boneGroupCount);

        for(int ct = 0; ct < this.boneGroupCount; ct++){
            String groupName =
                    parseZeroTermString(PmdLimits.MAXBYTES_BONEGROUPNAME);
            groupName = chopLastLF(groupName);
            this.boneHandler.pmdBoneGroupInfo(groupName);

            this.boneHandler.loopNext(PmdBoneHandler.BONEGROUP_LIST);
        }

        this.boneHandler.loopEnd(PmdBoneHandler.BONEGROUP_LIST);

        return;
    }

    /**
     * ボーングループ内訳のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseGroupedBoneList()
            throws IOException, MmdFormatException{
        int groupedBoneCount = parseInteger();

        if(this.boneHandler == null){
            skip(GROUPEDBONE_DATA_SZ * groupedBoneCount);
            return;
        }

        this.boneHandler.loopStart(PmdBoneHandler.GROUPEDBONE_LIST,
                                   groupedBoneCount);

        for(int ct = 0; ct < groupedBoneCount; ct++){
            int boneId = parseUShortAsInteger();
            int groupId = parseUByteAsInteger();
            this.boneHandler.pmdGroupedBoneInfo(boneId, groupId);

            this.boneHandler.loopNext(PmdBoneHandler.GROUPEDBONE_LIST);
        }

        this.boneHandler.loopEnd(PmdBoneHandler.GROUPEDBONE_LIST);

        return;
    }

}
