/*
 * building toon information
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdloader;

import jp.sourceforge.mikutoga.parser.ParseStage;
import jp.sourceforge.mikutoga.parser.pmd.PmdLimits;
import jp.sourceforge.mikutoga.parser.pmd.PmdToonHandler;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.ToonMap;

/**
 * トゥーン関係の通知をパーサから受け取る。
 */
class ToonBuilder implements PmdToonHandler {

    private final PmdModel model;

    private ToonMap toonMap;
    private int index;

    /**
     * コンストラクタ。
     * @param model モデル
     */
    ToonBuilder(PmdModel model){
        this.model = model;
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     * @param loops {@inheritDoc}
     */
    @Override
    public void loopStart(ParseStage stage, int loops){
        assert stage == PmdToonHandler.TOON_LIST;
        assert loops == PmdLimits.TOON_FIXEDNUM;

        this.toonMap = new ToonMap();
        this.index = 0;

        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopNext(ParseStage stage){
        assert stage == PmdToonHandler.TOON_LIST;
        this.index++;
        return;
    }

    /**
     * {@inheritDoc}
     * @param stage {@inheritDoc}
     */
    @Override
    public void loopEnd(ParseStage stage){
        assert stage == PmdToonHandler.TOON_LIST;
        this.model.setToonMap(this.toonMap);
        return;
    }

    /**
     * {@inheritDoc}
     * @param toonFileName {@inheritDoc}
     */
    @Override
    public void pmdToonFileInfo(String toonFileName){
        this.toonMap.setIndexedToon(this.index, toonFileName);
        return;
    }

}
