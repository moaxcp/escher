package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.RequestOutputStream;
import gnu.x11.ResponseInputStream;
import lombok.*;

/**
 * The base class for all X events.
 */
@Getter
public abstract class Event {

  private Display display;

  private EventCode code;

  @Setter private int detail; //todo temp for demo

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

  public String toString() {
    String class_name = "#" + getClass().getName();
    return class_name + " " + getCode();
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
