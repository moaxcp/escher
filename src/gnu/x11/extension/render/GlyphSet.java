package gnu.x11.extension.render;

import gnu.x11.RequestOutputStream;


/** GlyphSet in RENDER. */
public class GlyphSet extends gnu.x11.Resource {
  public Render render;

 
  // render opcode 17 - create glyph set
  /**
   * @see <a href="XRenderCreateGlyphSet.html">XRenderCreateGlyphSet</a>
   */
  public GlyphSet (Render render, Picture.Format format) {
    super (render.display);
    this.render = render;

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 17, 3);
      o.write_int32 (id);
      o.write_int32 (format.id ());
      o.send ();
    }
  }


  // render opcode 18 - reference glyph set
  /**
   * @see <a href="XRenderReferenceGlyphSet.html">
   * XRenderReferenceGlyphSet</a>
   */
  public GlyphSet (GlyphSet src) {
    super (src.display);
    render = src.render;

    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 18, 3);
      o.write_int32 (id);
      o.write_int32 (src.id);
      o.send ();
    }
  }


  // render opcode 19 - free glyph set
  /**
   * @see <a href="XRenderFreeGlyphSet.html">XRenderFreeGlyphSet</a>
   */
  public void free () {
    RequestOutputStream o = display.out;
    synchronized (o) {
      o.begin_request (render.major_opcode, 19, 2);
      o.write_int32 (id);
      o.send ();
    }
  }
}
