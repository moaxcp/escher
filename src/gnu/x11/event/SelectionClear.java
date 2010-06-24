package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X selection clear event. */
public final class SelectionClear extends Event {
  public static final int CODE = 29;


  public int time;
  public int owner_id;
  public int selection_atom_id;

  public SelectionClear (Display display, ResponseInputStream in) {
    super (display, in);
    time = in.readInt32 ();
    owner_id = in.readInt32 ();
    selection_atom_id = in.readInt32 ();
    in.skip (16);
  }
}
