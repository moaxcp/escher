

package gnu.x11;

import java.io.OutputStream;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class DebugRequestOutputStream extends RequestOutputStream {

  private static final String CLASS_NAME =
    DebugRequestOutputStream.class.getName ();

  private static final Logger logger;
  static {
    logger =
      java.util.logging.Logger.getLogger ("gnu.x11.DebugRequestOutputStream");

    logger.setLevel (Level.ALL);
    Handler h = new ConsoleHandler ();
    h.setLevel (Level.FINEST);
    logger.addHandler (h);

    try {
      // unless explicitly asked to do otherwise, we set the send_mode to
      // SYNCHRONOUS for debugging.
      String sendMode = System.getProperty ("escher.send_mode", "ROUND_TRIP");
      System.setProperty ("escher.send_mode", sendMode);

    }
    catch (SecurityException e) {
      // ok, not allowed to get/set sendMode...
    }
  }

  @Override public void begin_request (int opcode, int second_field,
                                       int request_length) {
    // begin_request will increment the sequence number, but what we get here
    // is still the not yet update sequence number.
    int sequenceNumber = getSequenceNumber() + 1;
    String message = "-----> BEGIN NEW REQUEST [opcode: " + opcode
                     + " | seq_number: "
                     + sequenceNumber
                     + " | second field: " + second_field
                     + " | request length: " + request_length + " ] <-----";

    logger.logp (Level.FINEST, CLASS_NAME, "begin_request", message);
    
    super.begin_request (opcode, second_field, request_length);
  }

  @Override public synchronized void flush () {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber  + "]";

    logger.logp (Level.FINEST, CLASS_NAME, "flush", message);

    super.flush ();
  }

  @Override public int get_int32 (int index) {
    int sequenceNumber = getSequenceNumber();
    String message = "[get_int32 - opcode: " + super.opcode ()
                     + " | seq_number: " + sequenceNumber
                     + " | index: " + index + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "get_int32", message);

    return super.get_int32 (index);
  }

  @Override public void increase_length (int i) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | i: " + i + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "increase_length", message);

    super.increase_length (i);
  }

  @Override void send_impl () {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " ]";
    
    logger.logp (Level.FINEST, CLASS_NAME, "send_impl", message);

    super.send_impl ();
  }

  /*
     * (non-Javadoc)
     * @see gnu.x11.RequestOutputStream#set_buffer_size(int)
     */
  @Override public synchronized int set_buffer_size (int size) {
    int sequenceNumber = getSequenceNumber();    
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | size: " + size + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "set_buffer_size", message);

    return super.set_buffer_size (size);
  }

  @Override public void set_index (int i) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | i: " + i + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "set_index", message);

    super.set_index (i);
  }

  @Override public long skip (long n) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | n: " + n + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "skip", message);

    return super.skip (n);
  }

  @Override public void update_length () {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "update_length", message);

    super.update_length ();
  }

  @Override public void write_bool (boolean b) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_bool", message);

    super.write_bool (b);
  }

  @Override public void write_bytes (byte[] b) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | byte: " + b + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_bytes", message);

    super.write_bytes (b);

  }

  @Override public void write_double (double d) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | double: " + d + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_double", message);

    super.write_double (d);
  }

  @Override public void write_float (float f) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | float: " + f + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_float", message);

    super.write_float (f);

  }

  @Override public void write_int16 (int v) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | int16: " + v + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_int16", message);

    super.write_int16 (v);
  }

  @Override public void write_int32 (int v) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | int32: " + v + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_int32", message);

    super.write_int32 (v);
  }

  @Override public void write_int8 (int v) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | int8: " + v + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_int8", message);

    super.write_int8 (v);
  }

  @Override public void write_pad (int n) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | n: " + n + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_pad", message);

    super.write_pad (n);
  }

  @Override public void write_string16 (String s) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | String16: " + s + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_string16", message);

    super.write_string16 (s);
  }

  @Override public void write_string8 (String s) {
    int sequenceNumber = getSequenceNumber();
    String message = "[opcode: " + super.opcode () + " | seq_number: "
                     + sequenceNumber + " | String8: " + s + " ]";

    logger.logp (Level.FINEST, CLASS_NAME, "write_string8", message);

    super.write_string8 (s);
  }

  public DebugRequestOutputStream (OutputStream sink, Display d) {
    super (sink, d);
  }

  public DebugRequestOutputStream (OutputStream sink, int size, Display d) {
    super (sink, size, d);
  }

}
