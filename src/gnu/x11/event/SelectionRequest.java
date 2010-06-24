package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X selection request event. */
public final class SelectionRequest extends Event {
  public static final int CODE = 30;

  public int time;
  public int owner_window_id;
  public int requestor_window_id;
  public int selection_atom_id;
  public int target_atom_id;
  public int property_atom_id;

  public SelectionRequest (Display display, ResponseInputStream in) {
    super (display, in);
    time = in.readInt32 ();
    owner_window_id = in.readInt32 ();
    requestor_window_id = in.readInt32 ();
    selection_atom_id = in.readInt32 ();
    target_atom_id = in.readInt32();
    property_atom_id = in.readInt32 ();
    in.skip (4);
  }
}
