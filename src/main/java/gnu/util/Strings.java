package gnu.util;

import lombok.*;
import lombok.experimental.*;

@UtilityClass
public class Strings {
  public static String requiresNonBlank(@NonNull String name, @NonNull String value) {
    if(name.trim().isEmpty()) {
      throw new IllegalArgumentException("name must not be blank.");
    }
    if(value.trim().isEmpty()) {
      throw new IllegalArgumentException(String.format("%s must not be blank.", name));
    }
    return value;
  }
}
