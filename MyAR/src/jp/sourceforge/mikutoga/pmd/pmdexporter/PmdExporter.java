/*
 * model exporter for pmd-file(up to date)
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.pmd.pmdexporter;

import java.io.OutputStream;

/**
 * 最新仕様のPMDファイルエクスポーター。
 * 将来のリリースにおいて、
 * 常に最新のPMDモデルファイルフォーマットに対応したエクスポーターの
 * 別名であることが保証される。つもり。
 */
public class PmdExporter extends PmdExporterExt3{

    /**
     * コンストラクタ。
     * @param stream 出力ストリーム
     * @throws NullPointerException 引数がnull
     */
    public PmdExporter(OutputStream stream) throws NullPointerException{
        super(stream);
        return;
    }

}
