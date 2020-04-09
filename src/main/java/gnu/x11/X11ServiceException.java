package gnu.x11;

import lombok.*;

/**
 * Represents an error response from X11. This is for responses from the server which indicate the request was
 * received and processed but something was incorrect.
 */
public class X11ServiceException extends X11ClientException {
  public X11ServiceException(@NonNull String message, @NonNull Throwable cause) {
    super(message, cause);
  }
}
