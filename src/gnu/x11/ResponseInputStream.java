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
import gnu.x11.extension.EventFactory;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

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
    private LinkedList events = new LinkedList();

    /**
     * Creates a new ResponseInputStream.
     * 
     * @param source
     *                the stream to read from
     */
    ResponseInputStream(InputStream source, Display d) {

        super(source);
        this.display = d;
    }

    private Error build_extension_error(Display display, int code, int seq_no,
            int bad, int minor_opcode, int major_opcode) {

        gnu.x11.extension.ErrorFactory factory = display.extension_error_factories[code - 128];

        if (factory == null) {
            throw new java.lang.Error("Unsupported extension error: " + code);
        }

        return factory.build(display, code, seq_no, bad, minor_opcode,
                major_opcode);
    }

    private void handle_exception(Throwable ex) {

        ex.printStackTrace();
    }

    public void pad(int n) {

        assert Thread.holdsLock(this);

        int pad = n % 4;
        if (pad > 0) {
            pad = 4 - pad;
        }
        this.skip(pad);
    }

    /**
     * Pulls all pending events out of the queue.
     * 
     * @return all pending events
     */
    public List pull_all_events() {

        LinkedList l = new LinkedList(this.events);
        Event e = this.read_event_from_stream();
        while (e != null) {
            l.add(e);
        }
        return l;
    }

    public boolean read_bool() {

        assert Thread.holdsLock(this);

        boolean v = false;
        try {
            v = this.read() != 0;
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v;
    }

    /**
     * Reads an (unsigned) byte value from the underlying stream.
     * 
     * @return the byte value
     */
    public int read_byte() {

        assert Thread.holdsLock(this);

        int v = -1;
        try {
            v = this.read();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v & 0xff;
    }

    private Event read_core_event(int code) {

        Event ev = null;
        switch (code) {
        case 0:
            this.read_error();
            break;
        case 1:
            ev = null;
            break;
        case 2:
            ev = new KeyPress(this.display, this);
            break;
        case 3:
            ev = new KeyRelease(this.display, this);
            break;
        case 4:
            ev = new ButtonPress(this.display, this);
            break;
        case 5:
            ev = new ButtonRelease(this.display, this);
            break;
        case 6:
            ev = new MotionNotify(this.display, this);
            break;
        case 7:
            ev = new EnterNotify(this.display, this);
            break;
        case 8:
            ev = new LeaveNotify(this.display, this);
            break;
        case 9:
            ev = new FocusIn(this.display, this);
            break;
        case 10:
            ev = new FocusOut(this.display, this);
            break;
        case 11:
            ev = new KeymapNotify(this.display, this);
            break;
        case 12:
            ev = new Expose(this.display, this);
            break;
        case 13:
            ev = new GraphicsExpose(this.display, this);
            break;
        case 14:
            ev = new NoExposure(this.display, this);
            break;
        case 15:
            ev = new VisibilityNotify(this.display, this);
            break;
        case 16:
            ev = new CreateNotify(this.display, this);
            break;
        case 17:
            ev = new DestroyNotify(this.display, this);
            break;
        case 18:
            ev = new UnmapNotify(this.display, this);
            break;
        case 19:
            ev = new MapNotify(this.display, this);
            break;
        case 20:
            ev = new MapRequest(this.display, this);
            break;
        case 21:
            ev = new ReparentNotify(this.display, this);
            break;
        case 22:
            ev = new ConfigureNotify(this.display, this);
            break;
        case 23:
            ev = new ConfigureRequest(this.display, this);
            break;
        case 24:
            ev = new GravityNotify(this.display, this);
            break;
        case 25:
            ev = new ResizeRequest(this.display, this);
            break;
        case 26:
            ev = new CirculateNotify(this.display, this);
            break;
        case 27:
            ev = new CirculateRequest(this.display, this);
            break;
        case 28:
            ev = new PropertyNotify(this.display, this);
            break;
        case 29:
            ev = new SelectionClear(this.display, this);
            break;
        case 30:
            ev = new SelectionRequest(this.display, this);
            break;
        case 31:
            ev = new SelectionNotify(this.display, this);
            break;
        case 32:
            ev = new ColormapNotify(this.display, this);
            break;
        case 33:
            ev = new ClientMessage(this.display, this);
            break;
        case 34:
            ev = new MappingNotify(this.display, this);
            break;
        default:
            throw new java.lang.Error("Unsupported core event code: " + code);
        }
        return ev;
    }

    public void read_data(byte[] buf) {

        assert Thread.holdsLock(this);

        int len = buf.length;
        int offset = 0;
        this.read_data(buf, offset, len);
    }

    public void read_data(byte[] buf, int offset, int len) {

        assert Thread.holdsLock(this);

        try {
            while (len > 0) {
                int numread = this.in.read(buf, offset, len);
                if (numread < 0) {
                    throw new EOFException();
                }
                len -= numread;
                offset += numread;
            }
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
    }

    /**
     * Reads an X error from the stream.
     */
    private void read_error() {

        int reply = this.read_int8();
        assert reply == 0;
        int code = this.read_int8();
        int seq_no = this.read_int16();
        int bad_value = this.read_int32();
        int minor_opcode = this.read_int16();
        int major_opcode = this.read_int8();
        this.skip(21);
        if (code >= 128 && code <= 255) {
            throw this.build_extension_error(this.display, code, seq_no, bad_value,
                    minor_opcode, major_opcode);
        }

        gnu.x11.Error err = new gnu.x11.Error(this.display,
                gnu.x11.Error.ERROR_STRINGS[code], code, seq_no, bad_value,
                minor_opcode, major_opcode);
        throw err;
    }

    public Event read_event() {

        // Otherwise we read and return the first event from the stream.
        Event ev = null;
        do {
            // If there are any events already queued up, then return the first
            // event in the queue.
            if (this.events.size() > 0) {
                ev = (Event) this.events.removeFirst();
            } else {
                ev = this.read_event_from_stream();
            }

            // If this returned null, there's a reply in the response stream and
            // some other thread is waiting for it, or there is no event and we
            // keep
            // waiting for one...
            if (ev == null) {
                try {
                    Thread.sleep(40);
                } catch (Exception ex) {
                }
                // Thread.yield ();
            }

        } while (ev == null);
        // System.err.println("event: " + ev);
        return ev;
    }

    /**
     * Reads an event from the input stream of the connection. If there is a
     * reply waiting to be fetched, this returns <code>null</code>.
     * 
     * @return the next event from the stream
     */
    private synchronized Event read_event_from_stream() {

        int available = 0;
        try {
            available = this.in.available();
            // System.err.println("available: " + available);
        } catch (IOException ex) {
            this.handle_exception(ex);
        }

        if (available == 0) {
            return null;
        }

        // We want to look-ahead the first byte to determine the type of the
        // response.
        int code = -1;
        try {
            this.in.mark(1);
            code = this.read_int8();
            this.in.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // System.err.println("reading code: " + code + " masked: " + (code &
        // 0x7f));
        code = code & 0x7f; // Remove synthetic mask.
        Event ev = null;
        if (code >= 64 && code <= 127) {
            ev = this.read_extension_event(code);
        } else {
            ev = this.read_core_event(code);
        }
        return ev;
    }

    private Event read_extension_event(int code) {

        EventFactory fac = this.display.extension_event_factories[code - 64];
        if (fac == null) {
            throw new java.lang.Error("Unsuppored extension event: " + code);
        }
        return fac.build(this.display, this, code);
    }

    public float read_float32() {

        int bits = this.read_int32();
        float v = Float.intBitsToFloat(bits);
        return v;
    }

    public double read_float64() {

        long bits = this.read_int64();
        double v = Double.longBitsToDouble(bits);
        return v;
    }

    /**
     * Reads an INT16 value from the stream.
     * 
     * @return the value
     */
    public int read_int16() {

        assert Thread.holdsLock(this);

        int v = -1;
        try {
            v = (this.read() << 8) | this.read();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v;
    }

    /**
     * Reads an INT32 value from the stream.
     * 
     * @return the value
     */
    public int read_int32() {

        assert Thread.holdsLock(this);

        int v = -1;
        try {
            v = (this.read() << 24) | (this.read() << 16) | (this.read() << 8) | this.read();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v;
    }

    /**
     * Reads an INT32 value from the stream.
     * 
     * @return the value
     */
    public long read_int64() {

        assert Thread.holdsLock(this);

        long v = -1;
        try {
            v = (this.read() << 56) | (this.read() << 48) | (this.read() << 40)
                    | (this.read() << 32) | (this.read() << 24) | (this.read() << 16)
                    | (this.read() << 8) | this.read();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v;
    }

    public int read_int8() {

        assert Thread.holdsLock(this);

        int v = -1;
        try {
            v = this.read();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }
        return v;
    }

    /**
     * Flushes the currently pending request and starts reading the reply. The
     * specified sequence number is used to check the reply sequence number.
     * 
     * @param seq_no
     *                the sequence number of the request
     * 
     * @return the input stream for reading the reply
     */
    public void read_reply(RequestOutputStream out) {

        // When reading a reply, the calling thread must hold a lock on both
        // the input and the output stream, otherwise we might end up doing
        // nasty stuff.

        assert Thread.holdsLock(this);
        assert Thread.holdsLock(out);

        // Flush the current request.
        // DON'T use plain send() because this could trigger a round-trip check
        // which would mess up with the reply.
        out.send_impl();
        out.flush();

        int exp_seq_no = out.getSequenceNumber();

        // Fetch all events and errors that may come before the reply.
        int code = -1;
        do {
            try {
                this.mark(1);
                code = this.read_int8();
                this.reset();
            } catch (IOException ex) {
                this.handle_exception(ex);
            }
            if (code == 0) {
                this.read_error();
            } else if (code > 1) { // Event.
                Event ev = this.read_event_from_stream();
                if (ev != null) {
                    this.events.addLast(ev);
                }
            }// else // Reply or Exception.
        } while (code != 1);
        // Check reply header, especially make sure that the sequence codes
        // match.
        try {
            this.mark(4);
            int reply = this.read_int8();
            assert reply == 1 : "Reply code must be 1 but is: " + reply;
            this.skip(1);
            int seq_no = this.read_int16();
            assert (exp_seq_no == seq_no) : "expected sequence number: "
                    + exp_seq_no + " got sequence number: " + seq_no;
            this.reset();
        } catch (IOException ex) {
            this.handle_exception(ex);
        }

        // Now the calling thread can safely read the reply.
    }

    public String read_string8(int len) {

        assert Thread.holdsLock(this);

        byte[] buf = new byte[len];
        this.read_data(buf);
        String s = new String(buf);
        return s;
    }

    /**
     * Skips n bytes in the stream.
     * 
     * @param n
     *                the number of bytes to skip
     * 
     * @return the actual number of bytes skipped
     */
    @Override
    public long skip(long n) {

        assert Thread.holdsLock(this);

        long s = 0;
        try {
            while (s < n) {
                s += super.skip(n - s);
            }
        } catch (Exception ex) {
            this.handle_exception(ex);
        }
        return s;
    }
}
