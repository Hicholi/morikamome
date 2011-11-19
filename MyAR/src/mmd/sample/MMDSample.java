package mmd.sample;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
 
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLCanvas;

import jp.sourceforge.mikutoga.parser.MmdSource;
import jp.sourceforge.mikutoga.pmd.Material;
import jp.sourceforge.mikutoga.pmd.PmdModel;
import jp.sourceforge.mikutoga.pmd.Surface;
import jp.sourceforge.mikutoga.pmd.Vertex;
import jp.sourceforge.mikutoga.pmd.pmdloader.PmdLoader;

import com.sun.opengl.util.GLUT;
 
public class MMDSample implements GLEventListener {

    private GL gl;
    private GLUT glut;
    
    private PmdModel model;
    private int[] hTextures;
    private String filedir = "/Users/core_aoi/Downloads/imas_miku/imas_miku/";
    private String pmdfile = "imas_miku.pmd";
    
    public MMDSample() {
        Frame frame = new Frame("MMD Sample");
 
        // 3Dを描画するコンポーネント
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(this);
  
        frame.add(canvas);
        frame.setSize(1000, 1000);
 
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
 
        frame.setVisible(true);
    }
 
    public void init(GLAutoDrawable drawable) {
        // 初期化処理
        gl = drawable.getGL();
        glut = new GLUT();
        // モデルの読み込み
		try {
			File file = new File(filedir + pmdfile);
			FileInputStream is = new FileInputStream(file);
			PmdLoader loader = new PmdLoader(new MmdSource(is));
			model = loader.load();
			System.out.println(model.getSurfaceList().get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// テクスチャの初期化
		initTexture();
    }
 
    public void reshape(GLAutoDrawable drawable,
                        int x, int y, 
                        int width, int height) {
        // 描画領域変更処理
        float ratio = (float)height / (float)width;
        
        gl.glViewport(0, 0, width, height);
 
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(-1.0f, 1.0f, -ratio, ratio,
		             5.0f, 40.0f);
 
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -20.0f);
    }
 
    public void display(GLAutoDrawable drawable) {
        // 描画処理
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // 行列を保存
        gl.glPushMatrix();
        
        // 移動
        gl.glTranslated(0, -2, 0);
        // 縮小
        gl.glScaled(0.2, 0.2, 0.2);
        // 回転
        gl.glRotated(180, 0, 1, 0);

        // ミクの描画
//        drawMiku();
        
		// テクスチャテスト
        drawTexture();
        
        // test
//        drawTest();
 
        // 行列を元に戻す
        gl.glPopMatrix();
    }
 
    public void displayChanged(GLAutoDrawable drawable,
                               boolean modeChanged,
                               boolean deviceChanged) {}
 
    public static void main(String[] args) {
        new MMDSample();
    }
    
    private void drawMiku(){
        // ミクの描画
        gl.glBegin(GL.GL_TRIANGLES);
        for(int i=0; i<model.getSurfaceList().size(); i++){
        	Surface surface = model.getSurfaceList().get(i);
        	Vertex[] vertexs = {surface.getVertex1(),surface.getVertex2(),surface.getVertex3()};
        	for(Vertex v : vertexs){
        		gl.glVertex3d(v.getPosition().getXPos(),
        				v.getPosition().getYPos(), 
        				v.getPosition().getZPos());
        	}
        }
        gl.glEnd();
    }
    private void drawTest(){
    	// 後ろと前で描画する順番に依存するか実験
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3d(1, 0, 0);
		gl.glVertex3d(0,0,0);
		gl.glVertex3d(3,0,0);
		gl.glVertex3d(0,5,0);

        gl.glColor3d(0, 1, 0);
		gl.glVertex3d(0,0,5);
		gl.glVertex3d(5,0,5);
		gl.glVertex3d(0,3,5);
        gl.glEnd();
    	
    }
    private void drawTexture(){
        double size = model.getMaterialList().size();
        for(int i=0; i<size; i++){
        	if(hTextures[i] == -1)continue;
        	// material
        	Material material = model.getMaterialList().get(i);
        	// texture
            gl.glEnable(GL.GL_TEXTURE_2D | GL.GL_DEPTH_TEST);
            gl.glBindTexture(GL.GL_TEXTURE_2D, hTextures[i]);
            gl.glBegin(GL.GL_TRIANGLES);
        	
        	//gl.glColor4d(i/size, 0.5, 0.5, 0);
        	for(int j=0; j<material.getSurfaceList().size(); j++){
	        	Surface surface = material.getSurfaceList().get(j);
	        	Vertex[] vertexs = {surface.getVertex1(),surface.getVertex2(),surface.getVertex3()};
	        	for(Vertex v : vertexs){
	        		// 頂点の向きを指定
	        		gl.glNormal3d(v.getNormal().getXVal(), 
	        				v.getNormal().getYVal(), 
	        				v.getNormal().getZVal());
	        		// テクスチャの対応点
	        		gl.glTexCoord2d(v.getUVPosition().getXPos(), 
	        				v.getUVPosition().getYPos());
	        		// 描画空間上の頂点位置
	        		gl.glVertex3d(v.getPosition().getXPos(),
	        				v.getPosition().getYPos(), 
	        				v.getPosition().getZPos());
	        	}
        	}
            gl.glEnd();
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }
    private void initTexture(){
    	int size = model.getMaterialList().size();
    	hTextures = new int[size];
    	
    	// テクスチャを1枚生成する
    	gl.glGenTextures(size, hTextures, 0);
    	
    	gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
    	
    	for(int i=0; i<size; i++){
	    	try {
	    		// material
	    		Material m = model.getMaterialList().get(i);
		    	// イメージの作成
				System.out.println(m.getShadeInfo().getTextureFileName());
				System.out.println(m.getShadeInfo().getToonFileName());
				String filename = m.getShadeInfo().getTextureFileName();
				if(m.getShadeInfo().getTextureFileName().equals(""))
					filename = m.getShadeInfo().getToonFileName();
		    	BufferedImage image;
					image = ImageIO.read(new File(filedir + filename));
				// ピクセルを入れる配列の初期化
		    	int[] pixels;
		    	if(image.getType() == BufferedImage.TYPE_3BYTE_BGR)
		    		pixels = new int[image.getWidth()*image.getHeight()];
		    	else
		    		pixels = new int[image.getWidth()*image.getHeight()];
		    	// ピクセルの取得
		    	pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		    	// RGBA byte配列に変換
		    	byte[] rgba = new byte[image.getWidth()*image.getHeight()*4];
		    	for(int j=0,count=0; j<pixels.length; j++){
		    		rgba[count++] = (byte)((pixels[j] >> (8*2)) & 0xFF);
		    		rgba[count++] = (byte)((pixels[j] >> (8*1)) & 0xFF);
		    		rgba[count++] = (byte)((pixels[j] >> (8*0)) & 0xFF);
		    		rgba[count++] = (byte)128;
		    	}
		    	// バッファの作成
		    	ByteBuffer buffer = ByteBuffer.wrap(rgba); 
		    	
		    	// hTextures[i]の設定をここから行うという宣言
		    	gl.glBindTexture(GL.GL_TEXTURE_2D, hTextures[i]);
		    	
		    	// ピクセル形式の指定
		    	gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);

		    	// VRAMにイメージを転送
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA,
						image.getWidth(), image.getHeight(), 0, GL.GL_RGBA,
						GL.GL_UNSIGNED_BYTE, buffer);
				
				// テクスチャマッピング方式。とりあえず繰り返し
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
				// 補完フィルタ設定
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
				// ポリゴンカラーとの混色設定
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
				
				// hTextureの設定終了
				gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			} catch (IOException e) {
				System.out.println("image cannot load!");
				hTextures[i] = -1;
			}
    	}
    }
}