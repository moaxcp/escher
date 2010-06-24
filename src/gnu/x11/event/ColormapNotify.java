package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X colormap notify event. */
public final class ColormapNotify extends Event {
  public static final int CODE = 32;


  public int window_id;
  public int colormap_id;
  public boolean is_new;
  public int state;

  public ColormapNotify (Display display, ResponseInputStream in) {
    super (display, in);
    window_id = in.readInt32 ();
    colormap_id = in.readInt32 ();
    is_new = in.readBool ();
    state = in.readInt8 ();
    in.skip (18);
  }

}
