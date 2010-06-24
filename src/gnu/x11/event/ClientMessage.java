package gnu.x11.event;

import gnu.x11.Atom;
import gnu.x11.Display;
import gnu.x11.ResponseInputStream;


/** X client message event. */
public final class ClientMessage extends Event {
  public static final int CODE = 33;


  public int window_id;
  public int type_atom_id;
  public byte[] data;

  /** Reading. */
  public ClientMessage (Display display, ResponseInputStream in) {
    super (display, in); 
    window_id = in.readInt32 ();
    type_atom_id = in.readInt32 ();
    data = new byte[20];
    in.readData(data);
  }


  //-- reading

  public int format () {
    return detail;
  }

  public int type_id () {
    return type_atom_id;
  }

  public int wm_data () {
      return  ((((int) data [0]) & 0xff) << 24 |
               (((int) data [1]) & 0xff) << 16 |
               (((int) data [2]) & 0xff) << 8  |
               (((int) data [3]) & 0xff));
  }

  public int wm_time () {
    return ((((int) data [4]) & 0xff) << 24 |
            (((int) data [5]) & 0xff) << 16 |
            (((int) data [6]) & 0xff) << 8  |
            (((int) data [7]) & 0xff));
  }


  public boolean delete_window () {
    Atom wm_protocols = (Atom) Atom.intern (display, "WM_PROTOCOLS");
    Atom wm_delete_window = (Atom) Atom.intern (display,
      "WM_DELETE_WINDOW");

    return format () == 32
      && type () == wm_protocols
      && wm_data () == wm_delete_window.getId();
  }


  public Atom type () { 
    return (Atom) Atom.intern (display, type_id (), true); 
  }


}
