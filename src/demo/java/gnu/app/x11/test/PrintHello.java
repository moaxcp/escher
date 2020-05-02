package gnu.app.x11.test;

import gnu.app.Application;
import gnu.x11.GC;
import gnu.x11.Window;
import gnu.x11.extension.Print;


/**
 * Hello World for X Print Service Extension.
 *
 * @see <a href="../../../../etc/screenshot/gnu/x11/test/PrintHello.help">
 * help output</a>
 *
 * @see <a href="../../../../etc/screenshot/gnu/x11/test/PrintHello.ps">
 * printer output</a>
 *
 * @see <a href="../../../../etc/screenshot/gnu/x11/test/PrintHello.output">
 * text output</a>
 */
public class PrintHello extends Application {
  private static final String FONT =
    "-*-*-*-*-*-*-*-180-300-300-*-*-iso8859-1";


  public PrintHello (String [] args) 
    throws gnu.x11.extension.NotFoundException {
    
    super (args);

    String printer_name = option.string ("printer", 
      "name of printer", "");

    about ("0.1", "print hello world",
      "Stephen Tse <stephent@sfu.ca>",
      "http://escher.sourceforge.net/");

    if (help_option) return;

    Print print = new Print (display);
    Print.Context context = print.createContext(printer_name);

    System.out.println (print);
    System.out.println (context);

    context.set_attributes (Print.Attributes.JOB_ATTRIBUTE_POOL,
      Print.Rule.ATTRIBUTE_MERGE,
      "*job-name: Hello world for Xprint");
    context.set ();
    print.startJob (Print.OutputMode.SPOOL);

    Window root = context.screen ();
    GC gc = root.getScreen().defaultGC();
    gc.setFont (new gnu.x11.Font (display, FONT));
    Window window = new Window (root, 100, 100, 100, 100);
    window.create ();

    print.startPage (window);
    window.map ();
    window.text (gc, 20, 30, "Hello World!");
    print.endPage ();

    print.endJob ();
    context.destroy ();
    display.close ();    
  }


  public static void main (String [] args) throws Exception { 
    new PrintHello (args);
  }
}
