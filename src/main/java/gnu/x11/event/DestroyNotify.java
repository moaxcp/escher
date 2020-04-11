package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;
import lombok.*;


/** X destroy notify event. */
public final class DestroyNotify extends Event {

  @Getter private int eventWindowID;
  @Getter private int windowID;

  public DestroyNotify (Display display, ResponseInputStream in) {
    super(display, in);
    eventWindowID = in.readInt32();
    windowID = in.readInt32();
    in.skip(20);
  }
}
