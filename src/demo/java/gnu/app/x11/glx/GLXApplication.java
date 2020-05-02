package gnu.app.x11.glx;

import gnu.x11.*;
import gnu.x11.event.*;
import gnu.x11.extension.glx.*;

import java.lang.*;
import java.lang.Error;


/** OpenGL application. */
public abstract class GLXApplication extends gnu.app.Application {
  protected static final int EVENT_BIT = 1<< EventMask.LAST_MASK_INDEX.getMask()+1;
  protected static final int DELETE_BIT = 1<< EventMask.LAST_MASK_INDEX.getMask()+2;

  protected static final int BUTTON_PRESS_BIT = EventMask.BUTTON_PRESS_MASK.getMask();
  protected static final int BUTTON_MOTION_BIT = EventMask.BUTTON_MOTION_MASK.getMask();
  protected static final int BUTTON1_MOTION_BIT = EventMask.BUTTON1_MOTION_MASK.getMask();
  protected static final int BUTTON2_MOTION_BIT = EventMask.BUTTON2_MOTION_MASK.getMask();
  protected static final int BUTTON3_MOTION_BIT = EventMask.BUTTON3_MOTION_MASK.getMask();
  protected static final int BUTTON4_MOTION_BIT = EventMask.BUTTON4_MOTION_MASK.getMask();
  protected static final int BUTTON5_MOTION_BIT = EventMask.BUTTON5_MOTION_MASK.getMask();
  protected static final int KEYBOARD_BIT = EventMask.KEY_PRESS_MASK.getMask();
  protected static final int POINTER_MOTION_BIT = EventMask.POINTER_MOTION_MASK.getMask();
  protected static final int RESIZE_BIT = EventMask.STRUCTURE_NOTIFY_MASK.getMask();

  private static final int ANY_BUTTON_MOTION_BITS = BUTTON_MOTION_BIT 
    | BUTTON1_MOTION_BIT 
    | BUTTON2_MOTION_BIT
    | BUTTON3_MOTION_BIT 
    | BUTTON4_MOTION_BIT 
    | BUTTON5_MOTION_BIT;

  private static final int MOTION_BITS = ANY_BUTTON_MOTION_BITS
    | POINTER_MOTION_BIT;


  protected int event_mask;
  protected Event event;
  protected GL gl;
  protected GLU glu;
  protected GLUT glut;
  protected GLX glx;
  protected boolean leave_display_open;
  protected VisualConfig visual_config;
  protected Window window;
  protected boolean window_dirty;


  protected GLXApplication(String [] args, int event_mask) {
    super (args);
    this.event_mask = event_mask;

    if (help_option) return;
    visual_config = new VisualConfig ();

    try {
      glx = new GLX (display);
    } catch (gnu.x11.extension.NotFoundException e) {
      throw new IllegalStateException(e);
    }
  }


  abstract protected void handle_expose ();
  protected void mark_window_dirty () { window_dirty = true; }


  /**
   * Throw when application declares to override some event (via
   * `event_mask' in constructor) but forgets to override the corresponding
   * evetn handler.
   */
  private void error () {
    throw new Error ("Un-overridden");
  }


  protected void handle_button (int key, int state, int x, int y) { 
    error (); 
  }


  protected boolean handle_delete () { 
    error (); 
    return false; 
  }


  protected boolean handle_event (Event event) { 
    error (); 
    return false; 
  }


  protected void handle_keyboard (int key, int state, int x, int y) { 
    error (); 
  }


  protected void handle_motion (int state, int x, int y) { 
    error (); 
  }


  protected void handle_resize (int width, int height) { 
    error (); 
  }


  protected void init_window (int width, int height) {
    visual_config = glx.visualConfig(display.getDefaultScreenNumber(), visual_config, true);
    int vid = visual_config.visual_id ();
    gl = glx.create_context (vid, display.getDefaultScreenNumber(), GL.NONE0);

    // FIXME share colormap
    Colormap colormap = new Colormap (display.getDefaultRoot(), vid,
                                      Colormap.NONE);
    
    WindowAttributes attr = new WindowAttributes ();
    attr.setColormap (colormap);

    // TODO use depth of x visual config instead of
    // `visual_config.buffer_size'?
    int depth = visual_config.buffer_size ();

    int more = EventMask.EXPOSURE_MASK.getMask() | EventMask.KEY_PRESS_MASK.getMask(); // compulsory

    /* Bugs? Whenever button motion events are selected, it is required to
     * select button press event as well. 
     */
     if ((event_mask & ANY_BUTTON_MOTION_BITS) != 0)
       more |= EventMask.BUTTON_PRESS_MASK.getMask();
     attr.setEventMask(event_mask | more);

    window = new Window (display.getDefaultRoot(), 10, 10, width, height);
    window.create (5, depth, Window.WinClass.INPUT_OUTPUT, vid, attr);

    window.setWM(this, "main");
    window.setWMDeleteWindow();

    gl.make_current (window);
    glu = new GLU (gl);
    glut = new GLUT (glu);
  }


  protected void about (String version, String description,
    String author, String url, String extra) {

    super.about (version, description, author, url, 
      extra + "\nTo quit, press ESCAPE.\n");
  }


  private void dispatch_button_press () {
    if ((event_mask & BUTTON_PRESS_BIT) == 0) return;
      
    ButtonPress e = (ButtonPress) event;
    int button = e.detail ();
    int state = e.state ();      
    handle_button (button, state, e.getEventX(), e.getEventY());
  }


  private void dispatch_client_message () {
    if (!((ClientMessage) event).deleteWindow()) return;
    if ((event_mask & DELETE_BIT) != 0 && handle_delete ()) return;
    exit ();
  }


  private void dispatch_configure_notify () {
    if ((event_mask & RESIZE_BIT) == 0) return;
    ConfigureNotify e = (ConfigureNotify) event;
      
    if (window.resized (e.rectangle ()))
      handle_resize (e.width (), e.height ());      
    window.setGeometryCache(e.rectangle ());
  }


  private void dispatch_event () {
    event = display.nextEvent();
    if ((event_mask & EVENT_BIT) != 0 && handle_event (event)) return;

    switch (event.getCode()) {
      case BUTTON_PRESS: dispatch_button_press (); break;
      case CLIENT_MESSAGE: dispatch_client_message (); break;
      case CONFIGURE_NOTIFY: dispatch_configure_notify (); break;
      case EXPOSE: dispatch_expose (); break;
      case KEY_PRESS: dispatch_key_press (); break;
      case MOTION_NOTIFY: dispatch_motion_notify (); break;
    }
  }


  private void dispatch_expose () {
    if (((Expose) event).count () == 0) handle_expose ();
  }


  private void dispatch_key_press () {
    KeyPress e = (KeyPress) event;
    int keycode = e.detail ();
    int keystate = e.state ();
    int keysym = display.getInput().keycodeToKeysym(keycode, keystate);

    if ((event_mask & KEYBOARD_BIT) != 0)
      handle_keyboard (keysym, keystate, e.getEventX (), e.getEventY ());

    if (keysym == gnu.x11.keysym.Misc.ESCAPE) exit ();
  }


  private void dispatch_motion_notify () {
    if ((event_mask & MOTION_BITS) == 0) return;

    MotionNotify e = (MotionNotify) event;
    int state = e.state ();      
    handle_motion (state, e.getEventY (), e.getEventY ());
  }


  protected void exec () {
    if (help_option) return;

    window.map ();
    if ((event_mask & RESIZE_BIT) != 0)
      handle_resize (window.width, window.height);

    while (!exit_now) {
      if (window_dirty) {
        window_dirty = false;
        handle_expose ();
      }
     
      // `exit_now' may have become true during `handle_expose'
      if (!exit_now) dispatch_event ();
    }

    if (!leave_display_open) display.close ();
  }
}
