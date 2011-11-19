import javax.media.opengl.*;

public class Particle {

  ///////////////// Constants /////////////////////////

  // Constants for indexing.
  public static final int X = 0;
  public static final int Y = 1;
  public static final int Z = 2;

  //////////////// Variables /////////////////////////

  private float lifetime = 100f;
  private float decay = 1f;
  // The pariticls resites inside a rectangle.
  private float size = 0.4f;

  private float pos[] = {0.0f, 0.0f, 0.0f};
  private float speed[] = {0.0f, 0.0f, 0.0f};

  ///////////////// Functions /////////////////////////

  Particle( float lifetime, float decay, float size )
    {
      if( lifetime != 0) { this.lifetime = lifetime; }
      if( decay != 0) { this.decay = decay; }
      if( size != 0) { this.size = size; }

      pos[X] = pos[Y] = pos[Z] = 0f;
    }

  public float getLifetime() { return lifetime; }

  public float getPosX() { return pos[X]; }
  public float getPosY() { return pos[Y]; }
  public float getPosZ() { return pos[Z]; }

  public float getSpeedX() { return speed[X]; }
  public float getSpeedY() { return speed[Y]; }
  public float getSpeedZ() { return speed[Z]; }

  public void setSpeed( float sx, float sy, float sz ) 
    { 
      speed[X] = sx;
      speed[Y] = sy;
      speed[Z] = sz;
    }

  public void incSpeedX( float ds ) { speed[X] += ds; }
  public void incSpeedY( float ds ) { speed[Y] += ds; }
  public void incSpeedZ( float ds ) { speed[Z] += ds; }

  public boolean isAlive() { return (lifetime > 0.0); }

  public void evolve()
    {
      lifetime -= decay;
      // Update locaton.
      for(int i=0; i<3; i++)
	pos[i] += speed[i];
    }

  public void draw( GL gl )
    { 
      final float halfSize = size / 2f;
      final float x = pos[X]-halfSize;
      final float y = pos[Y]-halfSize;
      final float xs = pos[X]+halfSize;
      final float ys = pos[Y]+halfSize;
      // Particle as small rectangle.
      gl.glBegin(GL.GL_QUADS); {
	gl.glTexCoord2f( 0f, 0f );
	gl.glVertex3f( x, y, pos[Z] );
	gl.glTexCoord2f( 1f, 0f );
	gl.glVertex3f( xs, y, pos[Z] );
	gl.glTexCoord2f( 1f, 1f );
	gl.glVertex3f( xs, ys, pos[Z] );
	gl.glTexCoord2f( 0f, 1f );
	gl.glVertex3f( x, ys, pos[Z] );
      } gl.glEnd();
    }

}

