import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.jogl.utils.GLNyARParam;
import jp.nyatla.nyartoolkit.jogl.utils.GLNyARSingleDetectMarker;
import jp.nyatla.nyartoolkit.qt.utils.QtNyARRaster_RGB;

import JautOGL.GLModel;

import com.sun.opengl.util.Animator;

@SuppressWarnings("serial")
public class MyGLCanvas extends GLCanvas implements GLEventListener {
	
	private final String CARCODE_FILE = "./Data/patt.tachikoma";
	private final String PARAM_FILE = "./Data/camera_para.dat";

	private GL _gl;
	private QtNyARRaster_RGB _raster;
	private GLNyARSingleDetectMarker _nya;
	private GLNyARParam _ar_param;
	
	/// protected TextureManager texture_manager;
	protected GLModel model1;
	protected GLModel model2;
	
	public MyGLCanvas() throws NyARException {
		
		addGLEventListener(this);
		setSize(MyAR.SCREEN_X, MyAR.SCREEN_Y);

		MyQtCameraCapture _qtCapture = new MyQtCameraCapture();
		_raster = _qtCapture.get_raster();
		_qtCapture.start();
		
	}
	
	// キューブを表示する
	public void drawCube() {
		
		int polyList = 0;
		float fSize = 0.5f;
		int f, i;
		float[][] cube_vertices = new float[][] { { 1.0f, 1.0f, 1.0f },
				{ 1.0f, -1.0f, 1.0f }, { -1.0f, -1.0f, 1.0f },
				{ -1.0f, 1.0f, 1.0f }, { 1.0f, 1.0f, -1.0f },
				{ 1.0f, -1.0f, -1.0f }, { -1.0f, -1.0f, -1.0f },
				{ -1.0f, 1.0f, -1.0f } };
		
		float[][] cube_vertex_colors = new float[][] { { 1.0f, 1.0f, 1.0f },
				{ 1.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 0.0f },
				{ 0.0f, 1.0f, 1.0f }, { 1.0f, 0.0f, 1.0f },
				{ 1.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 0.0f },
				{ 0.0f, 0.0f, 1.0f } };
		
		int cube_num_faces = 6;
		short[][] cube_faces = new short[][] { { 3, 2, 1, 0 }, { 2, 3, 7, 6 },
				{ 0, 1, 5, 4 }, { 3, 0, 4, 7 }, { 1, 2, 6, 5 }, { 4, 5, 6, 7 } };
		
		if (polyList == 0) {
			
			polyList = _gl.glGenLists(1);
			_gl.glNewList(polyList, GL.GL_COMPILE);
			_gl.glBegin(GL.GL_QUADS);
			
			for (f = 0; f < cube_num_faces; f++)
				
				for (i = 0; i < 4; i++) {
					
					_gl.glColor3f(cube_vertex_colors[cube_faces[f][i]][0],
							cube_vertex_colors[cube_faces[f][i]][1],
							cube_vertex_colors[cube_faces[f][i]][2]);
					_gl.glVertex3f(cube_vertices[cube_faces[f][i]][0] * fSize,
							cube_vertices[cube_faces[f][i]][1] * fSize,
							cube_vertices[cube_faces[f][i]][2] * fSize);
				
				}
			
				_gl.glEnd();
				_gl.glColor3f(0.0f, 0.0f, 0.0f);
			
				for (f = 0; f < cube_num_faces; f++) {
					
					_gl.glBegin(GL.GL_LINE_LOOP);

					for (i = 0; i < 4; i++)
						_gl.glVertex3f(cube_vertices[cube_faces[f][i]][0] * fSize,
							cube_vertices[cube_faces[f][i]][1] * fSize,
							cube_vertices[cube_faces[f][i]][2] * fSize);
					_gl.glEnd();
				}
			
				_gl.glEndList();
			}
		
			_gl.glPushMatrix(); // Save world coordinate system.
			_gl.glTranslatef(0.0f, 0.0f, 0.5f); // Place base of cube on marker
		
			// surface.
			_gl.glRotatef(0.0f, 0.0f, 0.0f, 1.0f); // Rotate about z axis.
			_gl.glDisable(GL.GL_LIGHTING); // Just use colours.
			_gl.glCallList(polyList); // Draw the cube.
			_gl.glPopMatrix(); // Restore world coordinate system.
		}
	
	// オブジェクトの表示
	public void draw( GL gl ) {
	 
		//CAR 1 COORDINATES
		float car1_x = 0f;
		float car1_y = 0f;
		float car1_z = 0f;
		
		//  //CAR 2 COORDINATES
		//  float car2_x = 0f;
		//  float car2_y = 0f;
		//  float car2_z = 0f;
	 
		float movement_angle = (float)Math.PI/2;
		// float movement_angle2=(float)Math.PI/2;

		//it draws a race-ground
		// draw_floor(gl);

		gl.glDisable(GL.GL_COLOR_MATERIAL);

		//*********positioning the car models*********
		//***car 1***
		gl.glPushMatrix();

		//gl.glTranslatef(car1_x, car1_y, car1_z);
		//gl.glScalef(0.0005F, 0.0005F, 0.0005F); //TOO BIG
		//gl.glRotatef(-90.0f, 0.0F, 1.0F, 0.0F);
		//gl.glRotatef((float)Math.toDegrees(movement_angle),0.0f,1.0f,0.0f);

		//draws the model
		model1.opengldraw(gl);

		gl.glPopMatrix();
		
		//          //***CAR2***
		//         gl.glPushMatrix();
		
		//         gl.glTranslated(car2_x, car2_y, car2_z);
		//         gl.glScalef(0.0005F, 0.0005F, 0.0005F); //TOO BIG
		//         gl.glRotatef(-90.0f, 0.0F, 1.0F, 0.0F);
		//         gl.glRotatef((float)Math.toDegrees(movement_angle2),0.0f,1.0f,0.0f);

		//         //draws the model
		//         model2.opengldraw(gl);
	}
	
	// 画面描画時に呼ばれる処理
	public void display(GLAutoDrawable drawable) {
		
		try {
		
			if (!_raster.hasData()) {
				
				return;
			
			}
		
			_gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			boolean is_marker_exist;
			
			// スレッドの排他制御
			synchronized (_raster) {
				
				// 8bitRGBを表現できるラスタとしきい値を渡す
				is_marker_exist = _nya.detectMarkerLite(_raster, 150);
			
				{
					NyARIntSize rsize = _raster.getSize();
					IntBuffer params = IntBuffer.allocate(4);
					_gl.glDisable(GL.GL_TEXTURE_2D);
					_gl.glGetIntegerv(GL.GL_VIEWPORT, params);
					_gl.glPixelZoom(
						1f * ((float) (params.get(2)) / (float) rsize.w),
						-1f * ((float) (params.get(3)) / (float) rsize.h)
					);
					_gl.glWindowPos2f(0.0f, (float) rsize.h);
					ByteBuffer buf = ByteBuffer.wrap(_raster.get_Ref_buf());
					_gl.glDrawPixels(rsize.w, rsize.h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buf);
				}
			}
			
			// マーカーを検出したら
			if (is_marker_exist) {
			
				_gl.glMatrixMode(GL.GL_PROJECTION);
				_gl.glLoadMatrixd(_ar_param.getCameraFrustumRH(), 0);
				_gl.glMatrixMode(GL.GL_MODELVIEW);
				_gl.glLoadIdentity();
				_gl.glLoadMatrixd(_nya.getCameraViewRH(), 0);

				// キューブを描画
				//drawCube();
			 
				// オブジェクトを描画
				draw(_gl);
			}
		
			Thread.sleep(1);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		
	}

	public void init(GLAutoDrawable drawable) {
		
		try {
			// GLオブジェクトの作成
			_gl = drawable.getGL();
			_gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

			_ar_param = new GLNyARParam();
			NyARCode ar_code = new NyARCode(16, 16);
			_ar_param.loadARParamFromFile(PARAM_FILE);
			_ar_param.changeScreenSize(320, 240);
			_nya = new GLNyARSingleDetectMarker(_ar_param, ar_code, 80.0);
			_nya.setContinueMode(false);
			
			// pattファイルの読み込み
			ar_code.loadARPattFromFile(CARCODE_FILE);
   
			// ModelLoaderOBJ ここから
			System.out.println("ModelLoaderOBJ init() start..."); //ddd

			///       texture_manager = TextureManager.getInstance( gl, glu );
			_gl.glEnable( GL.GL_TEXTURE_2D );
			_gl.glShadeModel( GL.GL_SMOOTH );

			//Paths of the 3D models.
			//String path1 = "models/formula.obj";
			//String path1 = "models/heli.obj";
			//String path1 = "models/barbell.obj";
			String path1 = "/Users/core_aoi/wing2.obj";
			////String path2 = "models/formula.obj";

			//here the needed textures for the race-ground are loaded
			try{
				///texture_manager.createManagedTexture
				///("road", "textures/roads/road.jpg", 
				///GL.GL_TEXTURE_2D, GL.GL_RGB, GL.GL_RGB, GL.GL_LINEAR,
				///GL.GL_LINEAR, true, true );
                // ...
                //in the same way we load each texture is needed...
                //...

                //a file input stream reads the data and stores them in 
                //a buffer reader for each 3D model
                FileInputStream r_path1 = new FileInputStream(path1);
                BufferedReader b_read1 = new BufferedReader(new InputStreamReader(r_path1));
                model1 = new GLModel(b_read1, true, "/Users/core_aoi/wing2.mtl", _gl);
                r_path1.close();
                b_read1.close();

                //FileInputStream r_path2 = new FileInputStream(path2);
                //BufferedReader b_read2 =
                //new BufferedReader(new InputStreamReader(r_path2));
                //model2 = new GLModel(b_read2, true, "models/formula.mtl", gl);
                //r_path2.close();
                //b_read2.close();
			} catch( Exception e ) {
				System.out.println("LOADING ERROR" +  e);
			}

			System.out.println("ModelLoaderOBJ init() done"); //ddd
   
			// ModelLoaderOBJ ここまで
   
			Animator _animator = new Animator(drawable);
			_animator.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 画面サイズ変更時に呼ばれる処理
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
		System.out.println("x: " + x + " y: " + y + " width: " + width + " height: " + height);
		_gl.glViewport(0, 0, width, height);
		_gl.glMatrixMode(GL.GL_PROJECTION);
		_gl.glLoadIdentity();
		_gl.glMatrixMode(GL.GL_MODELVIEW);
		_gl.glLoadIdentity();
		
	}
}