package gnu.x11;


/** X fontable. */
public abstract class Fontable extends Resource {
  /** Predefined. */
  public Fontable (int id) {
    super (id);
  }


  /** Create. */
  public Fontable (Display display) {
    super (display);
  }


  /** Intern. */
  public Fontable (Display display, int id) {
    super (display, id);
  }


  /** Reply of {@link #info()}. */
  public static class FontReply extends Data {
    public FontReply (Data data) { super (data); }
  
  
    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;
  
  
    public int min_char_or_byte2 () { return read2 (40); }
    public int max_char_or_byte2 () { return read2 (42); }
    public int default_char () { return read2 (44); }
    public int direction () { return read1 (48); }
    public int min_byte1 () { return read1 (49); }
    public int max_byte1 () { return read1 (50); }
    public boolean all_chars_exist () { return read_boolean (51); }
    public int ascent () { return read2 (52); }
    public int descent () { return read2 (54); }
  }
  
  
  // opcode 47 - query font  
  /**
   * @see <a href="XQueryFont.html">XQueryFont</a>
   */
  public FontReply info () {
    Request request = new Request (display, 47, 2);
    request.write4 (id);

    return new FontReply (display.read_reply (request));
  }

  
  /** Reply of {@link #text_extent(String)}. */
  public static class TextExtentReply extends Data {
    public TextExtentReply (Data data) { super (data); }
  }
  
  
  // opcode 48 - query text extents  
  /**
   * @see <a href="XQueryTextExtents.html">XQueryTextExtents</a>
   */
  public TextExtentReply text_extent (String s) {
    Request request = new Request (display, 48, s.length () % 2 == 1,
      2+Data.unit (s));

    request.write4 (id);
    request.write1 (s);
    return new TextExtentReply (display.read_reply (request));
  }
}
