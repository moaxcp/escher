package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Rectangle;
import gnu.x11.ResponseInputStream;
import gnu.x11.Window;
import lombok.*;


/** X configure request event. */
public final class ConfigureRequest extends Event {
    
  @Getter private int parentWindowID;
  @Getter private int windowID;
  private int siblingID;

  private int x;
  private int y;
  private int width;
  private int height;

  private int borderWidth;
  private int valueMask;


  public ConfigureRequest (Display display, ResponseInputStream in) {
    super (display, in);
    parentWindowID = in.readInt32();
    windowID = in.readInt32();
    siblingID = in.readInt32();
    x = in.readInt16();
    y = in.readInt16();
    width = in.readInt16();
    height = in.readInt16();
    borderWidth = in.readInt16();
    valueMask = in.readInt16();
    in.skip(4);
  }


  public Window.Changes changes() {
    Window.Changes c = new Window.Changes ();

    c.stackMode (stackMode());
    c.sibling_id (siblingID());
    c.setX(x());
    c.setY(y());
    c.setWidth(width());
    c.setHeight(height());
    c.borderWidth(borderWidth());

    // since above function calls will change bitmask, 
    // read bitmask last
    c.setBitmask(bitmask());
    return c;
  }


  public Window.Changes.StackMode stackMode () {
    return Window.Changes.StackMode.getByCode(getDetail());
  }

  public int siblingID() {
    return siblingID;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  public int borderWidth() {
    return borderWidth;
  }

  public int bitmask() {
    return valueMask;
  }


  public Rectangle rectangle() {
    return new Rectangle (x (), y (), width (), height ());
  }
}
