/*
 * parse-processing stage
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.parser;

/**
 * パース処理の進行ステージ種別を表す。
 * ループ構造の識別に用いられる。
 */
public class ParseStage {

    private final String name;

    /**
     * コンストラクタ。
     * 進行ステージ名は空文字列が指定される。
     */
    public ParseStage(){
        this("");
        return;
    }

    /**
     * コンストラクタ。
     * @param name 進行ステージ名
     */
    public ParseStage(String name){
        super();
        this.name = name;
        return;
    }

    /**
     * {@inheritDoc}
     * 進行ステージ名を返す。
     * @return {@inheritDoc} 進行ステージ名
     */
    @Override
    public String toString(){
        return this.name;
    }

}
