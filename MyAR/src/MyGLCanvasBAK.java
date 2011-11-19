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

import com.sun.opengl.util.Animator;

@SuppressWarnings("serial")
public class MyGLCanvasBAK extends GLCanvas implements GLEventListener {
 private final String CARCODE_FILE = "./Data/patt.hiro";
 private final String PARAM_FILE = "./Data/camera_para.dat";

 private GL _gl;
 private QtNyARRaster_RGB _raster;
 private GLNyARSingleDetectMarker _nya;
 private GLNyARParam _ar_param;

 public MyGLCanvasBAK() throws NyARException {
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

 public void display(GLAutoDrawable drawable) {
  try {
   if (!_raster.hasData()) {
    return;
   }
   _gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

   boolean is_marker_exist;
   synchronized (_raster) {
    is_marker_exist = _nya.detectMarkerLite(_raster, 100);
    {
     NyARIntSize rsize = _raster.getSize();
     IntBuffer params = IntBuffer.allocate(4);
     _gl.glDisable(GL.GL_TEXTURE_2D);
     _gl.glGetIntegerv(GL.GL_VIEWPORT, params);
     _gl.glPixelZoom(
       1f * ((float) (params.get(2)) / (float) rsize.w),
       -1f * ((float) (params.get(3)) / (float) rsize.h));
     _gl.glWindowPos2f(0.0f, (float) rsize.h);
     ByteBuffer buf = ByteBuffer.wrap(_raster.get_Ref_buf());
     _gl.glDrawPixels(rsize.w, rsize.h, GL.GL_RGB,
       GL.GL_UNSIGNED_BYTE, buf);
    }
   }
   if (is_marker_exist) {

    _gl.glMatrixMode(GL.GL_PROJECTION);
    _gl.glLoadMatrixd(_ar_param.getCameraFrustumRH(), 0);
    _gl.glMatrixMode(GL.GL_MODELVIEW);
    _gl.glLoadIdentity();
    _gl.glLoadMatrixd(_nya.getCameraViewRH(), 0);

    drawCube();
   }
   Thread.sleep(1);
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
   boolean deviceChanged) {
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
   ar_code.loadARPattFromFile(CARCODE_FILE);
   
   Animator _animator = new Animator(drawable);
   _animator.start();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public void reshape(GLAutoDrawable drawable, int x, int y, int width,
   int height) {
  _gl.glViewport(0, 0, width, height);
  _gl.glMatrixMode(GL.GL_PROJECTION);
  _gl.glLoadIdentity();
  _gl.glMatrixMode(GL.GL_MODELVIEW);
  _gl.glLoadIdentity();
 }
}