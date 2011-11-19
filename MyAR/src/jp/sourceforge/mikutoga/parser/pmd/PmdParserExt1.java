/*
 * pmd parser extension 1
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser.pmd;

import java.io.IOException;
import jp.sourceforge.mikutoga.parser.MmdFormatException;
import jp.sourceforge.mikutoga.parser.MmdSource;

/**
 * PMDモデルファイルのパーサ拡張その1。
 * ※英名対応
 */
public class PmdParserExt1 extends PmdParserBase {

    private PmdEngHandler engHandler = null;
    private boolean hasEnglishInfo = true;

    /**
     * コンストラクタ。
     * @param source 入力ソース
     */
    public PmdParserExt1(MmdSource source){
        super(source);
        return;
    }

    /**
     * 英語ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setEngHandler(PmdEngHandler handler){
        this.engHandler = handler;
        return;
    }

    /**
     * {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @throws MmdFormatException {@inheritDoc}
     */
    @Override
    protected void parseBody()
            throws IOException, MmdFormatException{
        super.parseBody();

        if(hasMore()){
            parseEngHeader();
            if(this.hasEnglishInfo){
                parseEngBoneList();
                parseEngMorphName();
                parseEngBoneGroupName();
            }
        }

        return;
    }

    /**
     * PMDモデル英語基本情報のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseEngHeader()
            throws IOException, MmdFormatException{
        this.hasEnglishInfo = parseBoolean();

        if(this.engHandler != null){
            this.engHandler.pmdEngEnabled(this.hasEnglishInfo);
        }
        if( ! this.hasEnglishInfo ) return;

        String modelName =
                parseZeroTermString(PmdLimits.MAXBYTES_MODELNAME);
        String description =
                parseZeroTermString(PmdLimits.MAXBYTES_MODELDESC);
        description = description.replace(CRLF, LF);

        if(this.engHandler != null){
            this.engHandler.pmdEngModelInfo(modelName, description);
        }

        return;
    }

    /**
     * PMDモデル英語ボーン名のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseEngBoneList()
            throws IOException, MmdFormatException{
        int boneNum = getBoneCount();

        if(this.engHandler == null){
            skip(PmdLimits.MAXBYTES_BONENAME * boneNum);
            return;
        }

        this.engHandler.loopStart(PmdEngHandler.ENGBONE_LIST, boneNum);

        for(int ct = 0; ct < boneNum; ct++){
            String boneName =
                    parseZeroTermString(PmdLimits.MAXBYTES_BONENAME);
            this.engHandler.pmdEngBoneInfo(boneName);

            this.engHandler.loopNext(PmdEngHandler.ENGBONE_LIST);
        }

        this.engHandler.loopEnd(PmdEngHandler.ENGBONE_LIST);

        return;
    }

    /**
     * PMDモデル英語モーフ名のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseEngMorphName()
            throws IOException, MmdFormatException{
        int morphNum = getMorphCount() - 1;  // base は英名なし

        if(this.engHandler == null){
            skip(PmdLimits.MAXBYTES_MORPHNAME * morphNum);
            return;
        }

        this.engHandler.loopStart(PmdEngHandler.ENGMORPH_LIST, morphNum);

        for(int ct = 0; ct < morphNum; ct++){
            String morphName =
                    parseZeroTermString(PmdLimits.MAXBYTES_MORPHNAME);
            this.engHandler.pmdEngMorphInfo(morphName);

            this.engHandler.loopNext(PmdEngHandler.ENGMORPH_LIST);
        }

        this.engHandler.loopEnd(PmdEngHandler.ENGMORPH_LIST);

        return;
    }

    /**
     * PMDモデル英語ボーングループ名のパースと通知。
     * @throws IOException IOエラー
     * @throws MmdFormatException フォーマットエラー
     */
    private void parseEngBoneGroupName()
            throws IOException, MmdFormatException{
        int groupNum = getBoneGroupCount();

        if(this.engHandler == null){
            skip(PmdLimits.MAXBYTES_BONEGROUPNAME * groupNum);
            return;
        }

        this.engHandler.loopStart(PmdEngHandler.ENGBONEGROUP_LIST, groupNum);

        for(int ct = 0; ct < groupNum; ct++){
            String boneGroupName =
                    parseZeroTermString(PmdLimits.MAXBYTES_BONEGROUPNAME);
            this.engHandler.pmdEngBoneGroupInfo(boneGroupName);

            this.engHandler.loopNext(PmdEngHandler.ENGBONEGROUP_LIST);
        }

        this.engHandler.loopEnd(PmdEngHandler.ENGBONEGROUP_LIST);

        return;
    }

}
