package gnu.app;

import gnu.x11.Display;
import gnu.x11.DisplayName;

import static gnu.x11.DisplayName.parse;

/**
 * X application.
 *
 * <p>A basic X application is expected to extend this class and call the
 * constructor with its command-line arguments. See <a
 * href="../../../gnu/x11/test/Hello.java"><code>gnu/x11/test/Hello.java</code></a>
 * for an example.
 *
 * <p>To run an X application, the name of the X server to be connected to
 * must be given. By default, the value is fetched from the environment
 * variable $DISPLAY. Users can override this by specifying --display
 * option. Simply <code>java gnu.app.x11.test.Hello</code> will display a
 * window in the default X server.
 *
 * <p>Users can also specify --send-mode option. For instance, <code>java
 * gnu.app.x11.test.Hello --send-mode sync</code> will display the same
 * window but the protocol requests are sent synchronously. See {@link
 * Connection#sendMode} and {@link Connection#SEND_MODE_STRINGS} for more
 * info.
 */
public class Application {
  /**
   * X server connection.
   *
   * <p>Note that this variable (X connection) should not be used before
   * calling {@link #about} since it is possible that users specify
   * <code>"--help"</code> option and no connection is made at all.
   */
  public Display display;
  protected boolean help_option, exit_now;
  protected Option option;



  protected Application (String [] args) {
    this (args, new Option (args));
    String env = System.getenv("DISPLAY");
    DisplayName display_name = option.display_name ("display",
        "X server to connect to", parse(env));

    if (help_option) return;
    display = display_name.connect();
  }


  /** 
   * Allow subclass to override {@link #option}.
   */
  protected Application (String [] args, Option option) {
    this.option = option;
    help_option = option.flag ("help", 
      "display this help screen and exit");
  }


  /**
   * #about(String, String, String, String, String)
   */
  protected void about (String version, String description,
    String author, String url) {
    
    about (version, description, author, url, "");
  }


  /**
   * Check if print "about" and possible options and then exit. This method
   * should be called only once after all options are specified.
   */
  protected void about (String version, String description,
    String author, String url, String extra) {

    if (help_option) option.about (this, version, description, author, url,
      "[OPTION]...", extra);

    if (option.invalid () || help_option) exit ();
  }


  protected void exit () {
    exit_now = true;
  }
}
