package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X graphics expose event. */
public final class GraphicsExpose extends Event {
  public static final int CODE = 13;


  public int drawable_id;

  public int x;
  public int y;
  public int width;
  public int height;

  public int minor_opcode;
  public int count;
  public int major_opcode;

  
  public GraphicsExpose (Display display, ResponseInputStream in) {

    super (display, in);
    drawable_id = in.readInt32 ();
    x = in.readInt16 ();
    y = in.readInt16 ();
    width = in.readInt16 ();
    height = in.readInt16 ();
    minor_opcode = in.readInt16 ();
    count = in.readInt16 ();
    major_opcode = in.readInt8 ();
    in.skip (11);

  }
}
