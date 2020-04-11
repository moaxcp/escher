package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;
import lombok.*;


/** X map notify event. */
public final class MapNotify extends Event {

  @Getter private int eventWindowID;
  @Getter private int windowID;
  @Getter private boolean overrideRedirect;

  public MapNotify (Display display, ResponseInputStream in) {
    super (display, in);
    eventWindowID = in.readInt32 ();
    windowID = in.readInt32 ();
    overrideRedirect = in.readBool ();
    in.skip (19);
  }
}
