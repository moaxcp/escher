package publicapi.display;

import gnu.x11.DisplayName;
import org.junit.jupiter.api.*;

import static gnu.x11.DisplayName.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class DisplayNameTest {
  @Test
  void parse_null_fails() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> parse(null));
    assertThat(exception).hasMessage("convention is marked non-null but is null");
  }

  @Test
  void parse_empty_fails() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parse(""));
    assertThat(exception).hasMessage("convention must not be blank.");
  }

  @Test
  void parse_hostName() {
    gnu.x11.DisplayName name = parse("hostName");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(0);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo("hostName:0.0");
  }

  @Test
  void parse_hostName_displayNumber() {
    gnu.x11.DisplayName name = parse("hostName:2");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo("hostName:2.0");
  }

  @Test
  void parse_hostName_displayNumber_screenNumber() {
    gnu.x11.DisplayName name = parse("hostName:2.1");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(1);
    assertThat(name.toString()).isEqualTo("hostName:2.1");
  }

  @Test
  void parse_displayNumber() {
    gnu.x11.DisplayName name = parse(":2");
    assertThat(name.getHostName()).isNull();
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo(":2.0");
  }

  @Test
  void parse_displayNumber_screenNumber() {
    DisplayName name = parse(":2.1");
    assertThat(name.getHostName()).isNull();
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(1);
    assertThat(name.toString()).isEqualTo(":2.1");
  }
}
