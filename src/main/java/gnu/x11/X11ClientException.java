package gnu.x11;

import lombok.*;

/**
 * Base exception class for any errors that occur when sending request to X11 or receiving results from X11.
 */
public class X11ClientException extends RuntimeException {
  public X11ClientException(@NonNull Throwable cause) {
    super(cause);
  }

  public X11ClientException(@NonNull String message, @NonNull Throwable cause) {
    super(message, cause);
  }

  public X11ClientException(@NonNull String message) {
    super(message);
  }
}
