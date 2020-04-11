package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;
import lombok.*;


/** X map request event. */
public final class MapRequest extends Event {

  @Getter private int parentWindowID;
  @Getter private int windowID;

  public MapRequest (Display display, ResponseInputStream in) {
    super (display, in);
    parentWindowID = in.readInt32();
    windowID = in.readInt32();
    in.skip(20);
  }
}
