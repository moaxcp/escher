package gnu.x11.event;

import org.junit.jupiter.api.*;

import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

public class EventCodeTest {
  @Test
  void getEventByID() {
    for(EventCode code : EventCode.values()) {
      assertThat(EventCode.getEventByID(code.getCode())).isEqualTo(code);
    }
  }
}
