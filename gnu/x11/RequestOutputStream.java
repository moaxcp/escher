package gnu.x11;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Used to create and manage requests to the X server.
 * 
 * @author Roman Kennke (roman@kennke.org)
 */
public class RequestOutputStream extends FilterOutputStream {

  /**
   * The default buffer size.
   */
  private static final int DEFAULT_BUFFER_SIZE = 255;

  /**
   * The request buffer. It always holds the current request. This can be
   * accessed directly for modifications, like when the current request
   * can be aggregated.
   */
  public byte [] buffer;

  /**
   * The current write index in the buffer. This always points to the next
   * free location inside the buffer.
   */
  public int index;

  /**
   * The request object. This is written to the stream when flushing.
   */
  public RequestObject request_object;

  public int seq_number;

  /**
   * Creates a new RequestOutputStream with the specified sink and a default
   * buffer size.
   *
   * @param sink the output stream to write to
   */
  RequestOutputStream (OutputStream sink) {
    this (sink, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Creates a new RequestOutputStream that writes to the specified output
   * stream and has a maximum request buffer size of <code>size</code>.
   *
   * @param sink the output stream to write to
   * @param size the buffer size
   */
  RequestOutputStream (OutputStream sink, int size) {
    super (sink);
    buffer = new byte [size];
    seq_number = 0;
  }

  /**
   * Changes the buffer size.
   *
   * @param size the new buffer size
   */
  public synchronized void set_buffer_size (int size) {
    // First flush all possibly pending request data.
    if (index > 0) {
      System.err.println("WARNING: Unflushed request data.");
      flush ();
    }
    buffer = new byte [size];
    index = 0;
  }

  /**
   * Begins a new request. This flushes all pending request data.
   *
   * @param opcode the opcode for the request
   * @param second_field the second field for the request
   * @param request_length the length of the request
   */
  public void begin_request (int opcode, int second_field,
                             int request_length) {

    assert Thread.holdsLock (this);

    // Send pending request.
    if (index > 0) {
      seq_number++;
      send ();
    }

    write_int8 (opcode);
    write_int8 (second_field);
    write_int16 (request_length);
  }

  /**
   * Sends the current request to the underlying stream, without necessarily
   * flushing the stream.
   */
  public void send () {
    assert Thread.holdsLock (this);

    if (request_object != null) {
      //System.err.println("request object: " + request_object);
      request_object.write (this);
      request_object = null;
    }
    if (index > 0) {
      // Possibly pad request.
      int pad = pad (index);
      if (pad != 0)
        skip (pad);
      try {
        out.write (buffer, 0, index);
      } catch (IOException ex) {
        handle_exception (ex);
      }
      index = 0;
      seq_number++;
    }
  }

  /**
   * Returns the opcode of the current request.
   *
   * @return the opcode of the current request
   */
  public int current_opcode () {
    return buffer [0];
  }

  /**
   * Sets the write index to <code>i</code>.
   *
   * @param i the write index to set
   */
  public void set_index (int i) {
    index = i;
  }

  /**
   * Writes one byte to the stream.
   *
   * @param v
   */
  public void write (int v) throws IOException {
    assert Thread.holdsLock (this);
    buffer [index] = (byte) v;
    index++;
  }

  /**
   * Writes the specified data to the stream.
   *
   * @param b the data to write
   */
  public void write (byte [] b) {
    assert Thread.holdsLock (this);
    System.arraycopy (b, 0, buffer, index, b.length);
    index += b.length;
  }

  /**
   * Writes the specified data to the stream.
   *
   * @param b the data to write
   * @param offs the start offset in the data array
   * @param len the length of the data to write
   */
  public void write (byte [] b, int offs, int len) {
    assert Thread.holdsLock (this);
    System.arraycopy (b, offs, buffer, index, len);
    index += len;
  }

  /**
   * Flushes all the pending request data to the underlying stream.
   */
  public synchronized void flush () {
    try {
      send ();
      out.flush ();
    } catch (IOException ex) {
      handle_exception (ex);
    }
  }

  public void write_bool (boolean b) {
    write_int8 (b ? 1 : 0);
  }

  /**
   * Writes an INT8 value to the stream.
   *
   * @param v the value to write
   */
  public void write_int8 (int v) {
    assert Thread.holdsLock (this);
    buffer [index] = (byte) (v);
    index++;
  }

  /**
   * Writes an INT16 value to the stream.
   *
   * @param v the value to write
   */
  public void write_int16 (int v) {
    assert Thread.holdsLock (this);
    buffer [index] = (byte) (v >> 8);
    index++;
    buffer [index] = (byte) v;
    index++;
  }

  /**
   * Writes an INT32 value to the stream.
   *
   * @param v the value to write
   */
  public void write_int32 (int v) {
    assert Thread.holdsLock (this);
    buffer [index] = (byte) (v >> 24);
    index++;
    buffer [index] = (byte) (v >> 16);
    index++;
    buffer [index] = (byte) (v >> 8);
    index++;
    buffer [index] = (byte) v;
    index++;
  }

  /**
   * Writes a STRING8 value to the stream.
   *
   * @param s the string to write
   */
  public void write_string8 (String s) {
    assert Thread.holdsLock (this);
    write (s.getBytes ());
  }

  /**
   * Writes a STRING16 to the stream.
   *
   * @param s the string to write
   */
  public void write_string16 (String s) {
    assert Thread.holdsLock (this);
    char [] chars = s.toCharArray();
    int len = chars.length;
    for (int i = 0; i < len; i++) {
      write_int16 (chars [i]);
    }
  }

  public void write_bytes (byte [] b) {
    assert Thread.holdsLock (this);
    write (b);
  }

  public long skip (long n) {
    assert Thread.holdsLock (this);
    index += n;
    return n;
  }

  /**
   * Skips p unused bytes, where p is pad(n). pad(n) is the number of
   * bytes that are needed to fill a block multiple of 4.
   *
   * @param n the number to be padded
   */
  public void write_pad (int n) {
    assert Thread.holdsLock (this);
    skip (pad (n));
  }

  /**
   * Returns the number of bytes that are needed to pad <code>n</code> bytes
   * to fill a multiple of four.
   *
   * @param n the number of bytes the pad
   *
   * @return the number of pad bytes needed
   */
  public static int pad (int n) {
    int pad = n % 4;
    if (pad > 0)
      pad = 4 - pad;
    return pad;
  }

  /**
   * Handles exceptions that my occur during IO operations.
   *
   * @param ex the exception to handle
   */
  private void handle_exception (Throwable ex) {
    ex.printStackTrace();
  }
}
