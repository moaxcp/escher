package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X gravity notify event. */
public final class GravityNotify extends Event {
  public static final int CODE = 24;

  public int event_window_id;
  public int window_id;

  public int x;
  public int y;

  
  public GravityNotify (Display display, ResponseInputStream in) {
    super (display, in);
    event_window_id = in.readInt32 ();
    window_id = in.readInt32 ();
    x = in.readInt16 ();
    y = in.readInt16 ();
    in.skip (16);
  }
}
