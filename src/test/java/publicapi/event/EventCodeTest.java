package publicapi.event;

import gnu.x11.event.*;
import org.junit.jupiter.api.*;

import static gnu.x11.event.EventCode.*;
import static org.assertj.core.api.Assertions.*;

public class EventCodeTest {
  @Test
  void of_with_each_code() {
    for(EventCode code : EventCode.values()) {
      assertThat(EventCode.of(code.getCode())).isEqualTo(code);
    }
  }

  @Test
  void of_LAST_EVENT() {
    assertThat(EventCode.of(1000)).isEqualTo(LAST_EVENT);
  }

  @Test
  void and() {
    assertThat(KEY_PRESS.and(KEY_PRESS.getCode())).isEqualTo(KEY_PRESS);
  }
}
