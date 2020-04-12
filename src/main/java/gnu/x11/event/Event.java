package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.RequestOutputStream;
import gnu.x11.ResponseInputStream;

/**
 * The base class for all X events.
 */
public abstract class Event {

  private Display display;

  private EventCode code;

  private int detail;

  private int sequenceNumber;

  /**
   * Creates an event without reading. This is used in subclasses that
   * don't use the usual first 3 fields.
   */
  Event(Display display) {
    this.display = display;
  }

  /**
   * Reads the event from the input stream.
   */
  public Event(Display display, ResponseInputStream in) {
    this.display = display;
    code = EventCode.of(in.readInt8());
    detail = in.readInt8();
    sequenceNumber = in.readInt16();
  }


  public Event(Display disp, EventCode c) {
    display = disp;
    code = c;
  }

  /**
   * The display from which this event originated.
   */
  public Display getDisplay() {
    return display;
  }

  /**
   * The event code;
   */
  public EventCode getCode() {
    return code;
  }

  /**
   * Event-specific detail information.
   */
  public int getDetail() {
    return detail;
  }

  /**
   * The sequence number of the event.
   */
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setDisplay(Display display) {
    this.display = display;
  }

  public void setCode(EventCode code) {
    this.code = code;
  }

  public void setDetail(int detail) {
    this.detail = detail;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public EventCode code() {
    return code;
  }

  public int sequenceNumber() {
    return sequenceNumber;
  }

  public String toString() {
    String class_name = "#" + getClass().getName();
    return class_name + " " + code();
  }

  /**
   * Writes this event into a request. This is used in
   * {@link gnu.x11.Window#sendEvent(boolean, int, Event)}.
   *
   * @param o the output stream to write to
   */
  public void write(RequestOutputStream o) {
    o.writeInt8(code.getCode());
    o.writeInt8(detail);
    o.writeInt16(sequenceNumber); // Is this correct?

    // The remaining pieces must be written by the subclasses.
  }

}
