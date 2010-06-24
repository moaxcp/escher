package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;

/** X unmap notify event. */
public final class UnmapNotify extends Event {
  public static final int CODE = 18;

  public int event_window_id;
  public int window_id;

  public boolean from_configure;

  /** Reading. */
  public UnmapNotify (Display display, ResponseInputStream in) {
    super (display, in);
    event_window_id = in.readInt32 ();
    window_id = in.readInt32 ();
    from_configure = in.readBool ();
    in.skip (19);
  }


  //-- reading
  public boolean from_configure () {
    return from_configure;
  }


}
