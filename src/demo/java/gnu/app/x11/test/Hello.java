package gnu.app.x11.test;

import gnu.app.x11.*;
import gnu.x11.*;
import gnu.x11.event.*;


/** 
 * Hello World.
 *
 * <p>This program covers the basic elements of a primitive X
 * application. It intensionally does not base on {@link Graphics}.
 *
 * @see <a href="../../../../etc/screenshot/gnu/x11/test/Hello.gif">
 * screenshot</a>
 * 
 * @see <a href="../../../../etc/screenshot/gnu/x11/test/Hello.help">
 * help output</a>
 * 
 * @see Hello2
 */
public class Hello extends Application {
  public Hello (String [] args) {
    super (args);

    about ("0.1", "hello world",
      "Stephen Tse <stephent@sfu.ca>",
      "http://escher.sourceforge.net/",
      "\nTo quit, press 'q', 'Q', ESCAPE, or any button.");
   
    if (help_option) return;

    WindowAttributes win_attr = new WindowAttributes();
    win_attr.setBackground (display.getDefaultWhite());
    win_attr.setBorder (display.getDefaultBlack());
    win_attr.setEventMask (EventMask.BUTTON_PRESS_MASK.logicOr(EventMask.EXPOSURE_MASK.logicOr(EventMask.KEY_PRESS_MASK)));
    Window window = new Window (display.getDefaultRoot(), 10, 10,
      100, 50, 5, win_attr);
    
    window.setWM (this, "main");
    window.setWMDeleteWindow();
    window.map ();
    display.flush ();

    while (!exit_now) {
      Event event = display.nextEvent ();

      switch (event.getCode()) {
        case BUTTON_PRESS:
        exit ();
        break;

      case CLIENT_MESSAGE:
        if (((ClientMessage) event).deleteWindow()) exit ();
        break;

      case EXPOSE:
        if (((Expose) event).count () == 0) {
          window.text (display.getDefaultGC(), 20, 30, "Hello World!");
          display.flush ();
        }
        break;
	
      case KEY_PRESS: {
        KeyPress e = (KeyPress) event;
	
        int keycode = e.detail ();
        int keystate = e.state ();
        int keysym = display.getInput().keycodeToKeysym (keycode, keystate);

        if (keysym == 'q' || keysym == 'Q' 
          || keysym == gnu.x11.keysym.Misc.ESCAPE) exit ();
        break;
      }      
      }
    }

    display.close ();
  }


  public static void main (String [] args) {
    new Hello (args);
  }
}
