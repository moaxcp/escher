package gnu.x11.event;

import java.util.*;
import java.util.stream.*;

import static java.util.function.UnaryOperator.*;
import static java.util.stream.Collectors.*;

public enum EventMask {
  NO_EVENT_MASK(0),
  KEY_PRESS_MASK(1 << 0),
  KEY_RELEASE_MASK(1 << 1),
  BUTTON_PRESS_MASK(1 << 2),
  BUTTON_RELEASE_MASK(1 << 3),
  ENTER_WINDOW_MASK(1 << 4),
  LEAVE_WINDOW_MASK(1 << 5),
  POINTER_MOTION_MASK(1 << 6),
  POINTER_MOTION_HINT_MASK(1 << 7),
  BUTTON1_MOTION_MASK(1 << 8),
  BUTTON2_MOTION_MASK(1 << 9),
  BUTTON3_MOTION_MASK(1 << 10),
  BUTTON4_MOTION_MASK(1 << 11),
  BUTTON5_MOTION_MASK(1 << 12),
  BUTTON_MOTION_MASK(1 << 13),
  KEYMAP_STATE_MASK(1 << 14),
  EXPOSURE_MASK(1 << 15),
  VISIBILITY_CHANGE_MASK(1 << 16),
  STRUCTURE_NOTIFY_MASK(1 << 17),
  RESIZE_REDIRECT_MASK(1 << 18),
  SUBSTRUCTURE_NOTIFY_MASK(1 << 19),
  SUBSTRUCTURE_REDIRECT_MASK(1 << 20),
  FOCUS_CHANGE_MASK(1 << 21),
  PROPERTY_CHANGE_MASK(1 << 22),
  COLORMAP_CHANGE_MASK(1 << 23),
  OWNER_GRAB_BUTTON_MASK(1 << 24),
  LAST_MASK_INDEX(24);

  private static final Map<Integer, EventMask> fromMasks;

  static {
    fromMasks = Stream.of(values())
        .collect(toMap(EventMask::getMask, identity()));
  }

  private final int mask;

  EventMask(int mask) {
    this.mask = mask;
  }

  public static int maskOr(EventMask[] eventMasks) {
    int resultMask = 0;

    for (EventMask m : eventMasks) {
      resultMask = resultMask | m.getMask();
    }

    return resultMask;
  }

  public boolean is(int mask) {
    return this.mask == mask;
  }

  public int logicOr(EventMask em) {
    return this.getMask() | em.getMask();
  }

  public int getMask() {
    return mask;
  }

  public int logicOr(int i) {
    return this.getMask() | i;
  }
}
