package gnu.x11.extension.glx;

import gnu.x11.RequestOutputStream;


/** GLX pixmap. */
public class GLXPixmap extends gnu.x11.Resource implements GLXDrawable {
  public GLX glx;


  // glx opcode 13 - create glx pixmap
  /**
   * @see <a href="glXCreateGLXPixmap.html">glXCreateGLXPixmap</a>
   */
  public GLXPixmap (GLX glx, int screen_no, VisualConfig visual, 
    gnu.x11.Pixmap pixmap) {

    super (glx.display);
    this.glx = glx;
    
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (glx.major_opcode, 13, 5);
      o.write_int32 (screen_no);
      o.write_int32 (visual.visual_id ());
      o.write_int32 (pixmap.id);
      o.write_int32 (id);
      o.send ();
    }
  } 


  // glx opcode 15 - destroy glx pixmap
  /**
   * @see <a href="glXDestroyContext.html">glXDestroyContext</a>
   */
  public void destroy () {
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (glx.major_opcode, 15, 2);
      o.write_int32 (id);
      o.send ();
    }
  }    

  public int id () {
    return id;
  }
}
