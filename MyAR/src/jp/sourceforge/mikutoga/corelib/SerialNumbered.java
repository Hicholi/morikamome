/*
 * serial-numbered interface
 *
 * License : The MIT License
 * Copyright(c) 2010 MikuToga Partners
 */

package jp.sourceforge.mikutoga.corelib;

import java.util.Comparator;

/**
 * 通し番号を持つオブジェクトの抽象化インタフェース。
 */
public interface SerialNumbered {

    /** 昇順での比較子。 */
    public Comparator<SerialNumbered> COMPARATOR = new SerialComparator();

    /**
     * 通し番号を設定する。
     * @param num 通し番号
     */
    void setSerialNumber(int num);

    /**
     * 通し番号を返す。
     * @return 通し番号
     */
    int getSerialNumber();

    /**
     * 通し番号による比較子Comparator。
     * 通し番号の昇順を定義づける。
     */
    public static class SerialComparator
            implements Comparator<SerialNumbered> {

        /**
         * コンストラクタ。
         */
        public SerialComparator(){
            super();
            return;
        }

        /**
         * {@inheritDoc}
         * @param o1 {@inheritDoc}
         * @param o2 {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public int compare(SerialNumbered o1, SerialNumbered o2){
            if(o1 == o2) return 0;
            if(o1 == null) return -1;
            if(o2 == null) return +1;

            int ser1 = o1.getSerialNumber();
            int ser2 = o2.getSerialNumber();

            return ser1 - ser2;
        }

    }

}
