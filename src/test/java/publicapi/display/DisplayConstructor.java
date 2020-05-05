package publicapi.display;

import gnu.x11.Display;
import gnu.x11.X11ClientException;
import java.net.*;
import mockit.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DisplayConstructor {
  @Test
  void constructor_null_socket() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> new Display(null, 0, 0));
    assertThat(exception).hasMessage("socket is marked non-null but is null");
  }

  @Test
  void constructor_localhost_UnknownHost(@Injectable Socket socket) throws UnknownHostException {
    UnknownHostException cause = new UnknownHostException();
    new Expectations() {{
      socket.getInetAddress().getHostName(); result = "localhost";
    }};
    new MockUp<InetAddress>() {
      @Mock
      InetAddress getLocalHost() throws UnknownHostException {
        throw cause;
      }
    };
    X11ClientException exception = assertThrows(X11ClientException.class, () -> new Display(socket, 0, 0));
    assertThat(exception).hasCause(cause);
  }
}
