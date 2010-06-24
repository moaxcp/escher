package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X create notify event. */
public final class CreateNotify extends Event {
  public static final int CODE = 16;


  public int parent_id;
  public int window_id;
  public int x;
  public int y;
  public int width;
  public int height;
  public int border_width;
  public boolean override_redirect;

  public CreateNotify (Display display, ResponseInputStream in) {
    super (display, in); 
    parent_id = in.readInt32 ();
    window_id = in.readInt32 ();
    x = in.readInt16 ();
    y = in.readInt16 ();
    width = in.readInt16 ();
    height = in.readInt16 ();
    border_width = in.readInt16 ();
    override_redirect = in.readBool ();
    in.skip (9);
  }

}
