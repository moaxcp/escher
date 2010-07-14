package gnu.x11.extension;

import gnu.x11.Display;
import gnu.x11.Pixmap;
import gnu.x11.Rectangle;
import gnu.x11.RequestOutputStream;
import gnu.x11.ResponseInputStream;
import gnu.x11.Window;
import gnu.x11.event.Event;

/** 
 * Nonrectangular Window Extension. The specification can be found <a href= 
 * "http://escher.sourceforge.net/etc/specification/shape-library.ps.gz"
 * >here</a> (<a href=
 * "http://escher.sourceforge.net/etc/specification/shape-protocol.ps.gz"
 * >protocol</a>).
 */
public class Shape extends Extension implements EventFactory {
  public static final String [] MINOR_OPCODE_STRINGS = {
    "QueryVersion",             // 0
    "Rectangles",               // 1
    "Mask",                     // 2
    "Combine",                  // 3
    "Offset",                   // 4
    "QueryExtents",             // 5
    "SelectInput",              // 6
    "InputSelected",            // 7
    "GetRectangles"             // 8
  };
    

  public static final int CLIENT_MAJOR_VERSION = 1;
  public static final int CLIENT_MINOR_VERSION = 0;


  public int server_major_version, server_minor_version;


  // shape opcode 0 - query version
  /**
   * @see <a href="XShapeQueryExtension.html">XShapeQueryExtension</a>
   */
  public Shape (Display display) throws NotFoundException { 
    super (display, "SHAPE", MINOR_OPCODE_STRINGS, 0, 1);

    // check version before any other operations
    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 0, 1);
      ResponseInputStream i = display.getResponseInputStream();
      synchronized (i) {
        i.readReply (o);
        i.skip (8);
        server_major_version = i.readInt16 ();
        server_minor_version = i.readInt16 ();
        i.skip (20);
      }
    }
  }

  public static final int BOUNDING = 0;
  public static final int CLIP = 1;


  public static final int SET = 0;
  public static final int UNION = 1;
  public static final int INTERSECT = 2;
  public static final int SUBTRACT = 3;
  public static final int INVERT = 4;


  public static final int UN_SORTED = 0;
  public static final int Y_SORTED = 1;
  public static final int YX_SORTED = 2;
  public static final int YX_BANDED = 3;


  // shape opcode 1 - rectangles
  /**
   * @param dest_kind valid:
   * {@link #BOUNDING},
   * {@link #CLIP}
   * 
   * @param operation valid:
   * {@link #SET},
   * {@link #UNION},
   * {@link #INTERSECT},
   * {@link #SUBTRACT},
   * {@link #INVERT}
   * 
   * @param ordering valid: 
   * {@link #UN_SORTED},
   * {@link #Y_SORTED},
   * {@link #YX_SORTED},
   * {@link #YX_BANDED}
   * 
   * @see <a href="XShapeCombineRectangles.html">XShapeCombineRectangles</a>
   */
  public void combine_rectangles (Window dest, int dest_kind, int x_offset,
                                  int y_offset, Rectangle [] rectangles,
                                  int operation, int ordering) {

    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 1, 4 + 2 * rectangles.length);
      o.writeInt8 (operation);
      o.writeInt8 (dest_kind);
      o.writeInt8 (ordering);
      o.skip (1);
      o.writeInt32 (dest.getID());
      o.writeInt16 (x_offset);
      o.writeInt16 (y_offset);

      for (int i = 0; i < rectangles.length; i++) {
        o.writeInt16 (rectangles [i].getX());
        o.writeInt16 (rectangles [i].getY());
        o.writeInt16 (rectangles [i].getWidth());
        o.writeInt16 (rectangles [i].getHeight());
      }

      o.send ();
    }
  }


  // shape opcode 2 - mask
  /**
   * @param dest_kind valid:
   * {@link #BOUNDING},
   * {@link #CLIP}
   * 
   * @param operation valid:
   * {@link #SET},
   * {@link #UNION},
   * {@link #INTERSECT},
   * {@link #SUBTRACT},
   * {@link #INVERT}
   * 
   * @param src possible: {@link gnu.x11.Pixmap#NONE}
   * @see <a href="XShapeCombineMask.html">XShapeCombineMask</a>
   */
  public void combine_mask (Window dest, int dest_kind, int x_offset, 
                            int y_offset, Pixmap src, int operation) { 

    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 2, 5);
      o.writeInt8 (operation);
      o.writeInt8 (dest_kind);
      o.skip (2);
      o.writeInt32 (dest.getID());
      o.writeInt16 (x_offset);
      o.writeInt16 (y_offset);
      o.writeInt32 (src.getID());
      o.send ();
    }
  }


  // shape opcode 3 - combine
  /**
   * @param dest_kind valid:
   * {@link #BOUNDING},
   * {@link #CLIP}
   * 
   * @param src_kind valid: 
   * {@link #BOUNDING},
   * {@link #CLIP}
   * 
   * @param operation valid:
   * {@link #SET},
   * {@link #UNION},
   * {@link #INTERSECT},
   * {@link #SUBTRACT},
   * {@link #INVERT}
   * 
   * @see <a href="XShapeCombineShape.html">XShapeCombineShape</a>
   */
  public void combine_shape (Window dest, int dest_kind, int x_offset,
                             int y_offset, Window src, int src_kind, int operation) { 

    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 3, 5);
      o.writeInt8 (operation);
      o.writeInt8 (dest_kind);
      o.writeInt8 (src_kind);
      o.skip (1);
      o.writeInt32 (dest.getID());
      o.writeInt16 (x_offset);
      o.writeInt16 (y_offset);
      o.writeInt32 (src.getID());
      o.send ();
    }
  }


  // shape opcode 4 - offset
  /**
   * @see <a href="XShapeOffsetShape.html">XShapeOffsetShape</a>
   */
  public void offset_shape (Window dest, int dest_kind,
                            int x_offset, int y_offset) {

    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 4, 4);
      o.writeInt8 (dest_kind);
      o.skip (3);
      o.writeInt32 (dest.getID());
      o.writeInt16 (x_offset);
      o.writeInt16 (y_offset);
      o.send ();
    }
  }


  /** Reply of {@link #extents(Window)} */
  public static class ExtentsInfo {

    public boolean bounding_shaped;
    public boolean clip_shaped;
    public int bounding_x;
    public int bounding_y;
    public int bounding_width;
    public int bounding_height;
    public int clip_x;
    public int clip_y;
    public int clip_width;
    public int clip_height;
    
    ExtentsInfo (ResponseInputStream i) {
      bounding_shaped = i.readBool ();
      clip_shaped = i.readBool ();
      i.skip (2);
      bounding_x = i.readInt16 ();
      bounding_y = i.readInt16 ();
      bounding_width = i.readInt16 ();
      bounding_height = i.readInt16 ();
      clip_x = i.readInt16 ();
      clip_y = i.readInt16 ();
      clip_width = i.readInt16 ();
      clip_height = i.readInt16 ();
    }
  
    public Rectangle bounding () { 
      return new Rectangle (
        bounding_x, bounding_y,
        bounding_width, bounding_height);
    }
        
  
    public Rectangle clip () { 
      return new Rectangle (
        clip_x, clip_y,
        clip_width, clip_height);
    }
        
  
    public String toString () {
      String output = "#Extents ";
  
      if (bounding_shaped) output += "Bounding: " + bounding ();
      if (clip_shaped) output += "Clip: " + clip ();
  
      return output;
    }
  }
  
  
  // shape opcode 5 - query extents
  /**
   * @see <a href="XShapeQueryExtents.html">XShapeQueryExtents</a>
   */
  public ExtentsInfo extents (Window dest) {
    
    ExtentsInfo info;
    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 5, 2);
      o.writeInt32 (dest.getID());
      ResponseInputStream i = display.getResponseInputStream();
      synchronized (i) {
        i.readReply (o);
        i.skip (8);
        info = new ExtentsInfo (i);
        i.skip (4);
      }
    }
    return info;
  }


  // shape opcode 6 - select input
  /**
   * @see <a href="XShapeSelectInput.html">XShapeSelectInput</a>
   */
  public void select_input (Window dest, boolean enable) {

    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 6, 3);
      o.writeInt32 (dest.getID());
      o.writeBool (enable);
      o.send ();    
    }
  }


  // shape opcode 7 - input selected
  /**
   * @see <a href="XShapeInputSelected.html">XShapeInputSelected</a>
   */
  public boolean input_selected (Window dest) {
    boolean enabled;
    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 6, 2);
      o.writeInt32 (dest.getID());
      ResponseInputStream i = display.getResponseInputStream();
      synchronized (i) {
        i.readReply (o);
        i.skip (1);
        enabled = i.readBool ();
        i.skip (24);
      }
    }
    return enabled;
  }

  
  /** Reply of {@link #rectangles()} */
  public static class RectanglesInfo {

    /**
     * One of {@link #UN_SORTED}, {@link #Y_SORTED}, {@link #YX_SORTED},
     * {@link #YX_BANDED}
     */
    public int ordering;

    public Rectangle [] rectangles;

    RectanglesInfo (ResponseInputStream i) {
      ordering = i.readInt8 ();
      i.skip (6);
      int nrects = i.readInt32 ();
      rectangles = new Rectangle [nrects];
      i.skip (20);
      for (int j = 0; j < nrects; j++) {
        int x = i.readInt16 ();
        int y = i.readInt16 ();
        int w = i.readInt16 ();
        int h = i.readInt16 ();
        rectangles [j] = new Rectangle (x, y, w, h);
      }
    }

  }
  
  
  // shape opcode 8 - get rectangles
  /**
   * @see <a href="XShapeGetRectangles.html">XShapeGetRectangles</a>
   */
  public RectanglesInfo rectangles (Window window, int kind) {

    RectanglesInfo info;
    RequestOutputStream o = display.getResponseOutputStream();
    synchronized (o) {
      o.beginRequest (majorOpcode, 7, 3);
      o.writeInt32 (window.getID());
      o.writeInt8 (kind);
      ResponseInputStream i = display.getResponseInputStream();
      synchronized (i) {
        i.readReply (o);
        i.skip (1);
        info = new RectanglesInfo (i);
      }
    }
    return info;
  }


  /** SHAPE notify event. */
  public static class NotifyEvent extends Event {
    public static final int code = 0;

    public int window_id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int time;
    public boolean shaped;

    NotifyEvent (Display display, ResponseInputStream i) { 
      super (display, i);
      window_id = i.readInt32 ();
      x = i.readInt16 ();
      y = i.readInt16 ();
      width = i.readInt16 ();
      height = i.readInt16 ();
      time = i.readInt32 ();
      shaped = i.readBool ();
      i.skip (11);
    }

    public int x () { return x; }
    public int y () { return y; }
    public int width () { return width; }
    public int height () { return height; }
    public int time () { return time; }
    public boolean shaped () { return shaped; }

    /**
     * @return valid:
     * {@link #BOUNDING},
     * {@link #CLIP}
     */
    public int kind () { return detail; }


    public Rectangle rectangle () {
      return new Rectangle (x (), y (), width (), height ());
    }
  }


  public Event build (Display display, ResponseInputStream i, int code) {
    // only one extension event
    return new NotifyEvent (display, i);
  }


  public String moreString () {
    return "\n  client-version: " 
      + CLIENT_MAJOR_VERSION + "." + CLIENT_MINOR_VERSION
      + "\n  server-version: "
      + server_major_version + "." + server_minor_version;
  }
}
