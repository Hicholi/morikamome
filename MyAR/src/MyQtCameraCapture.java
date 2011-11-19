import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.qt.utils.QtCameraCapture;
import jp.nyatla.nyartoolkit.qt.utils.QtCaptureListener;
import jp.nyatla.nyartoolkit.qt.utils.QtNyARRaster_RGB;

public class MyQtCameraCapture extends QtCameraCapture implements
  QtCaptureListener {
 private QtNyARRaster_RGB _raster;

 public MyQtCameraCapture() throws NyARException {
  super(MyAR.SCREEN_X, MyAR.SCREEN_Y, 30f);
  setCaptureListener(this);

  _raster = new QtNyARRaster_RGB(MyAR.SCREEN_X, MyAR.SCREEN_Y);
 }

 public void onUpdateBuffer(byte[] pixels) {
  try {
   _raster.setBuffer(pixels);
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public QtNyARRaster_RGB get_raster() {
  return _raster;
 }
}