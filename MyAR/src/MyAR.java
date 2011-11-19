import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.GLUT;

import jp.nyatla.nyartoolkit.NyARException;

@SuppressWarnings("serial")
public class MyAR extends Frame {
	
	public final static int SCREEN_X = 320;
	public final static int SCREEN_Y = 240;
	public MyAR() {
		setTitle("MyAR");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		setVisible(true);
		setSize(SCREEN_X, SCREEN_Y);

		try {
			
			add(new MyGLCanvas());
			
		} catch (NyARException e) {
			
			e.printStackTrace();
			
		}
	}
	
	public static void main(String[] args) {
		
		new MyAR();
		
	}
}