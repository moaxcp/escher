package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X leave notify event. */
public final class LeaveNotify extends Input {

  public LeaveNotify (Display display, ResponseInputStream in) {
    super (display, in);
  }
}
