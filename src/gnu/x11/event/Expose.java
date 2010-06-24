package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X expose event. */
public final class Expose extends Event {
  public static final int CODE = 12;

  public int window_id;

  public int x;
  public int y;
  public int width;
  public int height;

  public int count;

  public Expose (Display display, ResponseInputStream in) {
    super (display, in);
    window_id = in.readInt32 ();
    x = in.readInt16 ();
    y = in.readInt16 ();
    width = in.readInt16 ();
    height = in.readInt16 ();
    count = in.readInt16 ();
    in.skip (14); // Unused.
  }

  public int x () {
    return x;
  }

  public int y () {
    return y;
  }

  public int width () {
    return width;
  }

  public int height () {
    return height;
  }

  public int count () {
    return count;
  }

}
