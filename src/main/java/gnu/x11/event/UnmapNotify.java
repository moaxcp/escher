package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;
import lombok.*;

/** X unmap notify event. */
@Getter
public final class UnmapNotify extends Event {

  private int eventWindowID;
  private int windowID;
  private boolean fromConfigure;

  /** Reading. */
  public UnmapNotify (Display display, ResponseInputStream in) {
    super(display, in);
    eventWindowID = in.readInt32();
    windowID = in.readInt32();
    fromConfigure = in.readBool();
    in.skip(19);
  }
}
