package publicapi.display;

import gnu.x11.*;
import java.io.*;
import java.net.*;
import java.util.Optional;
import jdk.nashorn.internal.ir.annotations.Ignore;
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

  @Test
  @Ignore
  void constructor(@Injectable Socket socket, @Injectable InetAddress inetAddress, @Injectable XAuthority xAuthority, @Injectable InputStream in, @Injectable OutputStream out) throws IOException {
    new Expectations() {{
      socket.getInetAddress().getHostName(); result = "localhost";
      socket.getInputStream(); result = in;
      socket.getOutputStream(); result = out;
      inetAddress.getHostName(); result = "n1";
      xAuthority.getProtocolName(); result = "MIT-MAGIC-COOKIE-1";
      xAuthority.getProtocolData(); result = new byte[] { 1, 2, 3 };
    }};
    new MockUp<InetAddress>() {
      @Mock
      InetAddress getLocalHost() {
        return inetAddress;
      }
    };
    new MockUp<XAuthority>() {
      @Mock
      Optional<XAuthority> getAuthority(String hostName) {
        return Optional.of(xAuthority);
      }
    };

    new Display(socket, 1, 2);
  }
}
