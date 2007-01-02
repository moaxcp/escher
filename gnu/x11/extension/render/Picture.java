package gnu.x11.extension.render;

import gnu.x11.Atom;
import gnu.x11.Data;
import gnu.x11.Drawable;
import gnu.x11.GC;
import gnu.x11.Pixmap;
import gnu.x11.Rectangle;
import gnu.x11.RequestOutputStream;
import gnu.x11.ResponseInputStream;


/** Picture in RENDER. */
public class Picture extends gnu.x11.Resource {
  /**
   * Predefined picture.
   *
   * @see gnu.x11.Window#NONE
   */
  public static final Picture NONE = new Picture (0);


  public Render render;
  public Drawable drawable;


  /** Predefined. */
  public Picture (int id) { super (id); }


  /** ValueList for {@link Picture}. */
  public static class Attributes extends gnu.x11.ValueList {
    public final static Attributes EMPTY = new Attributes ();
    public Attributes () { super (13); }
    public void set_alpha_x_origin (int i) { set (2, i); }
    public void set_alpha_y_origin (int i) { set (3, i); }
    public void set_clip_x_origin (int i) { set (4, i); }
    public void set_clip_y_origin (int i) { set (5, i); }


    /** 
     * @param b default: false
     */
    public void set_repeat (boolean b) { set (0, b); }

    
    /** 
     * @param p possible:
     * {@link Picture#NONE} (default)
     */
    public void set_alpha_map (Picture p) { set (1, p.id); }


    /** 
     * @param p possible:
     * {@link Pixmap#NONE} (default)
     */
    public void set_clip_mask (Pixmap p) { set (6, p.id); }


    /** 
     * @param b default: true
     */
    public void set_graphics_exposures (boolean b) { set (7, b); }


    /** 
     * @param i valid:
     * {@link gnu.x11.GC.Values#CLIP_BY_CHILDREN} (default),
     * {@link gnu.x11.GC.Values#INCLUDE_INTERIORS}
     */
    public void set_subwindow_mode (int i) { set (8, i); }


    public static final int SHARP = 0;
    public static final int SMOOTH = 1;


    /** 
     * @param i valid:
     * {@link #SHARP},
     * {@link #SMOOTH} (default)
     */
    public void set_poly_edge (int i) { set (9, i); }

    
    public static final int PRECISE = 0;
    public static final int IMPRECISE = 1;

    
    /** 
     * @param i valid:
     * {@link #PRECISE} (default),
     * {@link #IMPRECISE}
     */
    public void set_poly_mode (int i) { set (10, i); }


    /** 
     * @param a possible:
     * {@link Atom#NONE} (default)
     */
    public void set_dither (Atom a) { set (11, a.id); }


    /** 
     * @param b default: false
     */
    public void set_component_alpha (boolean i) { set (12, i); }
  }
  

  /** RENDER picture format. */
  public static class Format {
    public static final int LENGTH = 28;

    // GLX uses *_BIT too
    public static final int ID_BIT = 1<<0;
    public static final int TYPE_BIT = 1<<1;
    public static final int DEPTH_BIT = 1<<2;
    public static final int RED_BIT = 1<<3;
    public static final int RED_MASK_BIT = 1<<4;
    public static final int GREEN_BIT = 1<<5;
    public static final int GREEN_MASK_BIT = 1<<6;
    public static final int BLUE_BIT = 1<<7;
    public static final int BLUE_MASK_BIT = 1<<8;
    public static final int ALPHA_BIT = 1<<9;
    public static final int ALPHA_MASK_BIT = 1<<10;
    public static final int COLORMAP_BIT = 1<<10;


    public int bitmask;
  

    private int id;
    private int type;
    private int depth;
    private Direct direct_format;
    private int colormap_id;

    public Format () {
      direct_format = new Direct();
    }


    public Format (ResponseInputStream i) {
      id = i.read_int32 ();
      type = i.read_int8 ();
      depth = i.read_int8 ();
      i.skip (2);
      direct_format = new Direct(i);
      colormap_id = i.read_int32 ();
    }   
  
  
    /** RENDER direct format. */
    public static class Direct {
      public static final int LENGTH = 16;
      public static final int TYPE = 1;
    

      public int bitmask;

      private int red;
      private int red_mask;
      private int green;
      private int green_mask;
      private int blue;
      private int blue_mask;
      private int alpha;
      private int alpha_mask;

      Direct () {
        // Nothing to do here.
      }

      public Direct (ResponseInputStream i) {
        red = i.read_int16 ();
        red_mask = i.read_int16 ();
        green = i.read_int16 ();
        green_mask = i.read_int16 ();
        blue = i.read_int16 ();
        blue_mask = i.read_int16 ();
        alpha = i.read_int16 ();
        alpha_mask = i.read_int16 ();
      }
    
    
      //-- reading
    
      public int red () {
        return red;
      }

      public int red_mask () {
        return red_mask;
      }

      public int green () {
        return green;
      }

      public int green_mask () {
        return green_mask;
      }

      public int blue () {
        return blue;
      }

      public int blue_mask () {
        return blue_mask;
      }

      public int alpha () {
        return alpha;
      }

      public int alpha_mask () {
        return alpha_mask;
      }
    
    
      //-- writing

      public void set_red (int i) { 
        red = i;
        set (RED_BIT); 
      }


      public void set_red_mask (int i) {
        red_mask = i; 
        set (RED_MASK_BIT); 
      }

      public void set_green (int i) {
        green = i;
        set (GREEN_BIT); 
      }

      public void set_green_mask (int i) {
        green_mask = i; 
        set (GREEN_MASK_BIT);
      }

      public void set_blue (int i) {
        blue = i;
        set (BLUE_BIT); 
      }

      public void set_blue_mask (int i) {
        blue_mask = i;
        set (BLUE_MASK_BIT); 
      }

      public void set_alpha (int i) {
        alpha = i;
        set (ALPHA_BIT);
      }

      public void set_alpha_mask (int i) {
        alpha_mask = i;
        set (ALPHA_MASK_BIT);
      }

      public String toString () {
        return "#Direct"
          + "\n  red: " + red ()
          + "\n  red-mask: " + red_mask ()
          + "\n  green: " + green ()
          + "\n  green-mask: " + green_mask ()
          + "\n  blue: " + blue ()
          + "\n  blue-mask: " + blue_mask ()
          + "\n  alpha: " + alpha ()
          + "\n  alpha-mask: " + alpha_mask ();
      }


      private void set (int mask) { bitmask |= mask; }
    }


    //-- reading
  
    public int id () {
      return id;
    }

    public int depth () {
      return depth;
    }

    public int colormap_id () {
      return colormap_id;
    }
  
    
    /** 
     * @return valid:
     * {@link Direct#TYPE}
     */
    public int type () {
      return type;
    }
  
  
    public Direct direct_format () {
      return direct_format;
    }
  
  
    //-- writing

    public void clear () {
      bitmask = 0; 
      direct_format ().bitmask = 0;
    }


    public void set_id (int i) {
      id = i; 
      set (ID_BIT); 
    }


    public void set_depth (int i) {
      depth = i;
      set (DEPTH_BIT);
    }
  
    
    /**
     * @param i valid:
     * {@link Direct#TYPE}
     */
    public void set_type (int i) {
      type = i;
      set (TYPE_BIT); 
    }
  
  
    public boolean match (Format template) {
      if ((template.bitmask & ID_BIT) != 0 
        && template.id () != id ()) return false;
      if ((template.bitmask & TYPE_BIT) != 0 
        && template.type () != type ()) return false;
      if ((template.bitmask & DEPTH_BIT) != 0 
        && template.depth () != depth ()) return false;
      if ((template.bitmask & COLORMAP_BIT) != 0 
        && template.colormap_id () != colormap_id ()) return false;
  
      
      Direct df0 = direct_format ();
      Direct df1 = template.direct_format ();
  
      if ((df1.bitmask & RED_BIT) != 0
        && df0.red () != df1.red ()) return false;
      if ((df1.bitmask & RED_MASK_BIT) != 0
        && df0.red_mask () != df1.red_mask ()) return false;
      if ((df1.bitmask & GREEN_BIT) != 0 
        && df0.green () != df1.green ()) return false;
      if ((df1.bitmask & GREEN_MASK_BIT) != 0 &&
        df0.green_mask () != df1.green_mask ()) return false;
      if ((df1.bitmask & BLUE_BIT) != 0 &&
        df0.blue () != df1.blue ()) return false;
      if ((df1.bitmask & BLUE_MASK_BIT) != 0 &&
        df0.blue_mask () != df1.blue_mask ()) return false;
      if ((df1.bitmask & ALPHA_BIT) != 0 &&
        df0.alpha () != df1.alpha ()) return false;
      if ((df1.bitmask & ALPHA_MASK_BIT) != 0 &&
        df0.alpha_mask () != df1.alpha_mask ()) return false;
  
      return true;
    }
    
  
    public static final String [] TYPE_STRINGS = {"indexed", "direct"};
  
  
    public String toString () {
      return "#Format"
        + "\n  id: " + id ()
        + "\n  type: " + TYPE_STRINGS [type ()]
        + "\n  depth: " + depth ()
        + "\n#Format: " + direct_format ()
        + "\n  colormap-id: " + colormap_id ()
        + "\n  bitmask: " + bitmask;
    }


    private void set (int mask) { bitmask |= mask; }
  }


  // render opcode 4 - create picture
  /**
   * @see <a href="XRenderCreatePicture.html">XRenderCreatePicture</a>
   * @see Render#create_picture(Drawable, Picture.Format, 
   * Picture.Attributes)
   */
  public Picture (Render render, Drawable drawable, Format format, 
    Attributes attr) {
    
    super (render.display);
    this.render = render;

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 4, 5 + attr.count ());
      o.write_int32 (id);
      o.write_int32 (drawable.id);
      o.write_int32 (format.id ());
      o.write_int32 (attr.bitmask);
      attr.write (o);
      o.send ();
    }
  }


  // render opcode 5 - change picture
  /**
   * @see <a href="XRenderChangePicture.html">XRenderChangePicture</a>
   */
  public void change (Attributes attr) {
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 5, 5+attr.count ());
      o.write_int32 (id);
      o.write_int32 (attr.bitmask);
      attr.write (o);
      o.send ();    
    }
  }


  // render opcode 6 - set picture clip rectangles
  /**
   * @see <a href="XRenderSetPictureClipRectangles.html">
   * XRenderSetPictureClipRectangles</a>
   */
  public void set_clip_rectangles (int x_origin, int y_origin,
    Rectangle [] rectangles) {

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 7, 3 + 2 * rectangles.length);
      o.write_int32 (id);

      for (int i = 0; i < rectangles.length; i++) {
        o.write_int16 (rectangles [i].x);
        o.write_int16 (rectangles [i].y);
        o.write_int16 (rectangles [i].width);
        o.write_int16 (rectangles [i].height);
      }
      o.send ();
    }
  }


  // render opcode 7 - free picture
  /**
   * @see <a href="XRenderFreePicture.html">XRenderFreePicture</a>
   */
  public void free () {
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 7, 2);
      o.write_int32 (id);
      o.send ();
    }
  }


  // render opcode 8 - scale
  public void scale (int color_scale, int alpha_scale, 
    Picture src, int src_x, int src_y, 
    int dst_x, int dst_y, int width, int height) {

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 9, 8);
      o.write_int32 (src.id);
      o.write_int32 (id);
      o.write_int32 (color_scale);
      o.write_int32 (alpha_scale);
      o.write_int16 (src_x);
      o.write_int16 (src_y);
      o.write_int16 (dst_x);
      o.write_int16 (dst_y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.send ();
    }
  }


  // render opcode 26 - fill rectangles
  /**
   * @see <a href="XRenderFillRectangle.html">XRenderFillRectangle</a>
   */
  public void fill_rectangle (int op, Color color, int x, int y, 
    int width, int height) {

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 26, 7);
      o.write_int8 (op);
      o.skip (3);
      o.write_int32 (id);
      o.write_int16 (x);
      o.write_int16 (y);
      o.write_int16 (width);
      o.write_int16 (height);
      o.write_int16 (color.red);
      o.write_int16 (color.green);
      o.write_int16 (color.blue);
      o.write_int16 (color.alpha);
      o.send ();
    }
  }
}
