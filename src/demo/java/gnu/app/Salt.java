package gnu.app;

import gnu.app.x11.Application;
import gnu.x11.Input.*;
import gnu.x11.Window;
import gnu.x11.event.*;


/**
 * Send string to top-level windows. It sends to all top-level windows
 * matching res_name and res_class given. I use this all the time to send
 * common long strings to netscape.
 *
 * <p>You may find this emacs code helpful.
 * <pre>
 * <code>
 * (defun salt-id ()
 *   (interactive)
 *   (shell-command 
 *   (format "java gnu.app.Salt Navigator Netscape 29340002487578")))
 * </code>
 * </pre>
 * 
 * @see <a href="../../../etc/screenshot/gnu/app/Salt.help">
 * help output</a>
 */
public class Salt extends Application {
  public Salt (String [] args) {
    super (args);

    String res_name = option.string ("res-name",
      "resource name of windows", "");
    String res_class = option.string ("res-class",
      "resource class of windows", "");
    String message = option.string ("message",
      "message to be sent", "");
    
    about ("0.1", "send string to top-level windows",
      "Stephen Tse <stephent@sfu.ca>",
      "http://escher.sourceforge.net/");

    if (help_option) return;

    for (Window window : display.getDefaultRoot().tree ().children ()) {

      Window.WMClassHint class_hint = window.wmClassHint ();
      
      if (class_hint == null) continue;
      if (!class_hint.equals (res_name, res_class)) continue;
      
      if (window.getAttributes().overrideRedirect ()
        || window.getAttributes ().mapState ()
        != Window.MapState.VIEWABLE
	|| window.wmName () == null) continue;


      for (int i=0; i<message.length (); i++)
        send_key (window, message.charAt (i));

      // keep loop to salt all matched apps 
    }
    
    display.close ();
  }


  public void send_key (Window window, int keysym) {
    boolean capital = keysym >= 'A' && keysym <= 'Z';
    // keysym of corresponding small letter
    int small_keysym = !capital ? keysym : keysym + ('a' - 'A');

    KeyPress key_event = new KeyPress (display);
    key_event.setWindow (window);
    key_event.setDetail (display.getInput().keysymToKeycode (small_keysym));
    if (capital) key_event.setState (KeyMask.SHIFT_MASK.getCode());

    window.sendEvent (false, EventMask.NO_EVENT_MASK.getMask(), key_event);
  }


  public static void main (String [] args) {
    new Salt (args);
  }
}
