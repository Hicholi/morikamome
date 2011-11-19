/* 
 * PROJECT: NyARToolkit JOGL utilities.
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java version ARToolkit class library.
 * Copyright (C)2008 R.Iizuka
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp>
 * 
 */
package jp.nyatla.nyartoolkit.jogl.utils;

import jp.nyatla.nyartoolkit.core.*;
import jp.nyatla.nyartoolkit.core.param.NyARParam;

/**
 * NyARParamにOpenGL向け関数を追加したもの
 */
public class GLNyARParam extends NyARParam
{
	private double view_distance_min = 0.1;//#define VIEW_DISTANCE_MIN		0.1			// Objects closer to the camera than this will not be displayed.

	private double view_distance_max = 100.0;//#define VIEW_DISTANCE_MAX		100.0		// Objects further away from the camera than this will not be displayed.

	private double[] m_projection = null;

	public void setViewDistanceMin(double i_new_value)
	{
		m_projection = null;//キャッシュ済変数初期化
		view_distance_min = i_new_value;
	}

	public void setViewDistanceMax(double i_new_value)
	{
		m_projection = null;//キャッシュ済変数初期化
		view_distance_max = i_new_value;
	}

	/**
	 * void arglCameraFrustumRH(const ARParam *cparam, const double focalmin, const double focalmax, GLdouble m_projection[16])
	 * 関数の置き換え
	 * @param focalmin
	 * @param focalmax
	 * @return
	 */
	public double[] getCameraFrustumRH()
	{
		//既に値がキャッシュされていたらそれを使う
		if (m_projection != null) {
			return m_projection;
		}
		//無ければ計算
		m_projection = new double[16];
		NyARMat trans_mat = new NyARMat(3, 4);
		NyARMat icpara_mat = new NyARMat(3, 4);
		double[][] p = new double[3][3], q = new double[4][4];

		int i, j;

		final int width = this._screen_size.w;
		final int height = this._screen_size.h;

		this.getPerspectiveProjectionMatrix().decompMat(icpara_mat, trans_mat);

		double[][] icpara = icpara_mat.getArray();
		double[][] trans = trans_mat.getArray();
		for (i = 0; i < 4; i++) {
			icpara[1][i] = (height - 1) * (icpara[2][i]) - icpara[1][i];
		}

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				p[i][j] = icpara[i][j] / icpara[2][2];
			}
		}
		q[0][0] = (2.0 * p[0][0] / (width - 1));
		q[0][1] = (2.0 * p[0][1] / (width - 1));
		q[0][2] = -((2.0 * p[0][2] / (width - 1)) - 1.0);
		q[0][3] = 0.0;

		q[1][0] = 0.0;
		q[1][1] = -(2.0 * p[1][1] / (height - 1));
		q[1][2] = -((2.0 * p[1][2] / (height - 1)) - 1.0);
		q[1][3] = 0.0;

		q[2][0] = 0.0;
		q[2][1] = 0.0;
		q[2][2] = (view_distance_max + view_distance_min) / (view_distance_min - view_distance_max);
		q[2][3] = 2.0 * view_distance_max * view_distance_min / (view_distance_min - view_distance_max);

		q[3][0] = 0.0;
		q[3][1] = 0.0;
		q[3][2] = -1.0;
		q[3][3] = 0.0;

		for (i = 0; i < 4; i++) { // Row.
			// First 3 columns of the current row.
			for (j = 0; j < 3; j++) { // Column.
				m_projection[i + j * 4] = q[i][0] * trans[0][j] + q[i][1] * trans[1][j] + q[i][2] * trans[2][j];
			}
			// Fourth column of the current row.
			m_projection[i + 3 * 4] = q[i][0] * trans[0][3] + q[i][1] * trans[1][3] + q[i][2] * trans[2][3] + q[i][3];
		}
		return m_projection;
	}
}
