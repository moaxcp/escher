package publicapi.display;

import org.junit.jupiter.api.*;

import static gnu.x11.Display.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class DisplayConnect {
  @Test
  void connect_fails_with_null_display() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> connect(null));
    assertThat(exception).hasMessage("name is marked non-null but is null");
  }
}
