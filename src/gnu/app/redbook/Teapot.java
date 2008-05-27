package gnu.app.redbook;

import gnu.x11.extension.glx.GL;
import gnu.x11.extension.glx.GLUT;


/**
 * Utility for teapot. Modified from <code>teapot.c</code>.
 */
class Teapot {
  private int display_list;
  private GL gl;
  private GLUT glut;


  Teapot (GLUT glut, double scale) {
    this.glut = glut;
    gl = glut.gl;

    display_list = gl.genLists (1);
    gl.newList (display_list, GL.COMPILE);
    glut.solid_teapot (scale);
    gl.endList ();
  } 


  void draw (float x, float y, float z,
    float ambr, float ambg, float ambb,
    float diffr, float diffg, float diffb,
    float specr, float specg, float specb, float shine) {

    float [] data = new float [4];
    data [3] = 1.0f;

    gl.pushMatrix ();
    gl.translatef (x, y, z);

    data [0] = ambr;
    data [1] = ambg;
    data [2] = ambb;
    gl.materialfv (GL.FRONT, GL.AMBIENT, data);

    data [0] = diffr;
    data [1] = diffg;
    data [2] = diffb;
    gl.materialfv (GL.FRONT, GL.DIFFUSE, data);

    data [0] = specr;
    data [1] = specg;
    data [2] = specb;
    gl.materialfv (GL.FRONT, GL.SPECULAR, data);

    gl.materialf (GL.FRONT, GL.SHININESS, shine*128.0f);
    gl.callList (display_list);
    gl.popMatrix ();
  }
}
