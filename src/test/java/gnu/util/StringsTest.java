package gnu.util;

import org.junit.jupiter.api.*;

import static gnu.util.Strings.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class StringsTest {
  @Test
  void requiresNonNull_null_name_fails() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> requiresNonBlank(null, "value"));
    assertThat(exception).hasMessage("name is marked non-null but is null");
  }

  @Test
  void requiresNonNull_null_value_fails() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> requiresNonBlank("variable", null));
    assertThat(exception).hasMessage("value is marked non-null but is null");
  }
  @Test
  void requiresNonNull_empty_name_fails() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requiresNonBlank("", "value"));
    assertThat(exception).hasMessage("name must not be blank.");
  }

  @Test
  void requiresNonNull_empty_value_fails() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requiresNonBlank("variable", ""));
    assertThat(exception).hasMessage("variable must not be blank.");
  }
}
