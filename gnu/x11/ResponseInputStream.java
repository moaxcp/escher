package gnu.x11;

import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.CirculateNotify;
import gnu.x11.event.CirculateRequest;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ColormapNotify;
import gnu.x11.event.ConfigureNotify;
import gnu.x11.event.ConfigureRequest;
import gnu.x11.event.CreateNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.EnterNotify;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.FocusIn;
import gnu.x11.event.FocusOut;
import gnu.x11.event.GraphicsExpose;
import gnu.x11.event.GravityNotify;
import gnu.x11.event.KeyPress;
import gnu.x11.event.KeyRelease;
import gnu.x11.event.KeymapNotify;
import gnu.x11.event.LeaveNotify;
import gnu.x11.event.MapNotify;
import gnu.x11.event.MapRequest;
import gnu.x11.event.MappingNotify;
import gnu.x11.event.MotionNotify;
import gnu.x11.event.NoExposure;
import gnu.x11.event.PropertyNotify;
import gnu.x11.event.ReparentNotify;
import gnu.x11.event.ResizeRequest;
import gnu.x11.event.SelectionClear;
import gnu.x11.event.SelectionNotify;
import gnu.x11.event.SelectionRequest;
import gnu.x11.event.UnmapNotify;
import gnu.x11.event.VisibilityNotify;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Reads response data from the X server.
 * 
 * @author Roman Kennke (roman@kennke.org)
 */
public class ResponseInputStream extends FilterInputStream {

  /**
   * The display to which this input stream is associated.
   */
  private Display display;

  /**
   * Events that have already been read from the stream but not fetched.
   */
  private LinkedList events = new LinkedList ();

  /**
   * Creates a new ResponseInputStream.
   *
   * @param source the stream to read from
   */
  ResponseInputStream (InputStream source, Display d) {
    super (source);
    display = d;
  }

  /**
   * Skips n bytes in the stream.
   *
   * @param n the number of bytes to skip
   *
   * @return the actual number of bytes skipped
   */
  public long skip (long n) {
    assert Thread.holdsLock (this);

    long s = -1;
    try {
      s = super.skip (n);
    } catch (Exception ex) {
      handle_exception (ex);
    }
    return s;
  }

  public int read_int8 () {
    assert Thread.holdsLock (this);

    int v = -1;
    try {
      v = read ();
    } catch (IOException ex) {
      handle_exception (ex);
    }
    return v;
  }

  /**
   * Reads an INT16 value from the stream.
   *
   * @return the value
   */
  public int read_int16 () {
    assert Thread.holdsLock (this);

    int v = -1;
    try {
      v = (read () << 8) | read ();
    } catch (IOException ex) {
      handle_exception (ex);
    }
    return v;
  }

  /**
   * Reads an INT32 value from the stream.
   *
   * @return the value
   */
  public int read_int32 () {
    assert Thread.holdsLock (this);

    int v = -1;
    try {
      v = (read () << 24) | (read () << 16) | (read () << 8) | read ();
    } catch (IOException ex) {
      handle_exception (ex);
    }
    return v;
  }

  public String read_string8 (int len) {
    assert Thread.holdsLock (this);

    byte [] buf = new byte [len];
    read_data (buf);
    String s = new String (buf);
    return s;
  }

  public void pad (int n) {
    assert Thread.holdsLock (this);

    int pad = n % 4;
    if (pad > 0)
      pad = 4 - pad;
    skip (pad);
  }

  public boolean read_bool () {
    assert Thread.holdsLock (this);

    boolean v = false;
    try {
      v = read () != 0;
    } catch (IOException ex) {
      handle_exception (ex);
    }
    return v;
  }

  /**
   * Reads an (unsigned) byte value from the underlying stream.
   *
   * @return the byte value
   */
  public int read_byte () {
    assert Thread.holdsLock (this);

    int v = -1;
    try {
      v = read ();
    } catch (IOException ex) {
      handle_exception (ex);
    }
    return v & 0xff;
  }

  public void read_data (byte [] buf, int offset, int len) {
    assert Thread.holdsLock (this);

    try {
      while (len > 0) {
        int numread = in.read (buf, offset, len);
        if (numread < 0)
          throw new EOFException ();
        len -= numread;
        offset += numread;
      }
    } catch (IOException ex) {
      handle_exception (ex);
    }
  }

  public void read_data (byte [] buf) {
    assert Thread.holdsLock (this);

    int len = buf.length;
    int offset = 0;
    read_data (buf, offset, len);
  }

  private void handle_exception (Throwable ex) {
    ex.printStackTrace();
  }
  public Event read_event () {

    assert Thread.holdsLock (this);

    // If there are any events already queued up, then return the first
    // event in the queue.
    if (events.size () > 0) {
      Event event = (Event) events.poll ();
      return event;
    }

    // Otherwise we read and return the first event from the stream.
    Event ev = null;
    do {
      ev = read_event_from_stream ();
      // If this returned null, there's a reply in the response stream and
      // some other thread is waiting for it.
      if (ev == null)
        Thread.yield ();
    } while (ev == null);
    return ev;
  }

  /**
   * Reads an event from the input stream of the connection. If there is
   * a reply waiting to be fetched, this returns <code>null</code>.
   *
   * @return the next event from the stream
   */
  private Event read_event_from_stream () {

    assert Thread.holdsLock (this);

      // We want to look-ahead the first byte to determine the type of the
      // response.
      int code = -1;
      try {
        in.mark (1);
        code = read_int8 ();
        in.reset ();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      Event ev = null;
      switch (code) {
      case 0:
        read_error ();
        break;
      case 1:
        ev = null;
        break;
      case 2:
        ev = new KeyPress (display, this);
        break;
      case 3:
        ev = new KeyRelease (display, this);
        break;
      case 4:
        ev = new ButtonPress (display, this);
        break;
      case 5:
        ev = new ButtonRelease (display, this);
        break;
      case 6:
        ev = new MotionNotify (display, this);
        break;
      case 7:
        ev = new EnterNotify (display, this);
        break;
      case 8:
        ev = new LeaveNotify (display, this);
        break;
      case 9:
        ev = new FocusIn (display, this);
        break;
      case 10:
        ev = new FocusOut (display, this);
        break;
      case 11:
        ev = new KeymapNotify (display, this);
        break;
      case 12:
        ev = new Expose (display, this);
        break;
      case 13:
        ev = new GraphicsExpose (display, this);
        break;
      case 14:
        ev = new NoExposure (display, this);
        break;
      case 15:
        ev = new VisibilityNotify (display, this);
        break;
      case 16:
        ev = new CreateNotify (display, this);
        break;
      case 17:
        ev = new DestroyNotify (display, this);
        break;
      case 18:
        ev = new UnmapNotify (display, this);
        break;
      case 19:
        ev = new MapNotify (display, this);
        break;
      case 20:
        ev = new MapRequest (display, this);
        break;
      case 21:
        ev = new ReparentNotify (display, this);
        break;
      case 22:
        ev = new ConfigureNotify (display, this);
        break;
      case 23: 
        ev = new ConfigureRequest (display, this);
        break;
      case 24:
        ev = new GravityNotify (display, this);
        break;
      case 25:
        ev = new ResizeRequest (display, this);
        break;
      case 26:
        ev = new CirculateNotify (display, this);
        break;
      case 27:
        ev = new CirculateRequest (display, this);
        break;
      case 28:
        ev = new PropertyNotify (display, this);
        break;
      case 29:
        ev = new SelectionClear (display, this);
        break;
      case 30:
        ev = new SelectionRequest (display, this);
        break;
      case 31:
        ev = new SelectionNotify (display, this);
        break;
      case 32:
        ev = new ColormapNotify (display, this);
        break;
      case 33:
        ev = new ClientMessage (display, this);
        break;
      case 34:
        ev = new MappingNotify (display, this);
        break;
      }
      return ev;
  }

  /**
   * Flushes the currently pending request and starts reading the reply. The specified sequence
   * number is used to check the reply sequence number.
   *
   * @param seq_no the sequence number of the request
   *
   * @return the input stream for reading the reply
   */
  public void read_reply (RequestOutputStream out) {

    // When reading a reply, the calling thread must hold a lock on both
    // the input and the output stream, otherwise we might end up doing
    // nasty stuff.

    assert Thread.holdsLock (this);
    assert Thread.holdsLock (out);

    // Flush the current request.
    out.flush();

    // Fetch all events that may come before the reply.
    Event ev = null;
    do {
      ev = read_event_from_stream ();
      if (ev != null)
        events.offer (ev);
    } while (ev != null);

  }


  /**
   * Reads an X error from the stream.
   */
  private void read_error () {
    
  }

}