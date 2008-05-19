package gnu.x11.event;

import gnu.x11.Atom;
import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X property notify event. */
public final class PropertyNotify extends Event {
  public static final int CODE = 28;

  public static final int NEW_VALUE = 0;
  public static final int DELETED = 1;

  private int window_id;
  private int atom_id;
  private int time;
  private int state;

  public PropertyNotify (Display display, ResponseInputStream in) {
    super (display, in);
    window_id = in.read_int32 ();
    atom_id = in.read_int32 ();
    time = in.read_int32 ();
    state = in.read_int8 ();
    in.skip (15);
  }

  /**
   * @deprecated use {@link #getAtom(Display)} instead.
   * @param display
   * @return
   */
  @Deprecated
  public Atom atom (Display display) { 
    return (Atom) Atom.intern (display, atom_id, true);
  }
  
  public Atom getAtom(Display display) {
      
      return atom(display);
  }
  
  public int getAtomID() {
      
      return this.atom_id;
  }
  
  public int getWindowID() {
      
      return this.window_id;
  }
  
  public int getTime() {

      return this.time;
  }
  
  public int getState() {
      
      return this.state;
  }
}
