package publicapi.display;

import gnu.x11.DisplayName;
import gnu.x11.*;
import java.io.*;
import java.net.*;
import mockit.*;
import org.junit.jupiter.api.*;
import org.newsclub.net.unix.*;

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
  void parse_negative_displayNumber_fails() {
    X11ClientException exception = assertThrows(X11ClientException.class, () -> parse(":-1"));
    assertThat(exception).hasMessage("expected displayNumber > 0 but was \"-1\".");
  }

  @Test
  void parse_negative_screenNumber_fails() {
    X11ClientException exception = assertThrows(X11ClientException.class, () -> parse(":1.-1"));
    assertThat(exception).hasMessage("expected screenNumber > 0 but was \"-1\".");
  }

  @Test
  void parse_hostName() {
    gnu.x11.DisplayName name = parse("hostName");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(0);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo("hostName:0.0");
    assertThat(name.getSocketFile()).isNull();
  }

  @Test
  void parse_hostName_displayNumber() {
    gnu.x11.DisplayName name = parse("hostName:2");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo("hostName:2.0");
    assertThat(name.getSocketFile()).isNull();
  }

  @Test
  void parse_hostName_displayNumber_screenNumber() {
    gnu.x11.DisplayName name = parse("hostName:2.1");
    assertThat(name.getHostName()).isEqualTo("hostName");
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(1);
    assertThat(name.toString()).isEqualTo("hostName:2.1");
    assertThat(name.getSocketFile()).isNull();
  }

  @Test
  void parse_displayNumber() {
    gnu.x11.DisplayName name = parse(":2");
    assertThat(name.getHostName()).isNull();
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(0);
    assertThat(name.toString()).isEqualTo(":2.0");
    assertThat(name.getSocketFile()).isEqualTo(new File("/tmp/.X11-unix/X2"));
  }

  @Test
  void parse_displayNumber_screenNumber() {
    DisplayName name = parse(":2.1");
    assertThat(name.getHostName()).isNull();
    assertThat(name.getDisplayNumber()).isEqualTo(2);
    assertThat(name.getScreenNumber()).isEqualTo(1);
    assertThat(name.toString()).isEqualTo(":2.1");
    assertThat(name.getSocketFile()).isEqualTo(new File("/tmp/.X11-unix/X2"));
  }

  @Test
  void connect_unix_exception_creating() throws IOException {
    IOException cause = new IOException("expected");
    new MockUp<AFUNIXSocketAddress>() {
      @Mock
      public void $init(File socketFile) throws IOException {
        throw cause;
      }
    };
    X11ClientException result = assertThrows(X11ClientException.class, () -> parse(":0").connect());
    assertThat(result).hasMessage("Failed to create connection to \":0.0\".");
    assertThat(result).hasCause(cause);
  }

  @Test
  void connect_unix_socket(@Mocked Display display, @Mocked AFUNIXSocket socket, @Mocked AFUNIXSocketAddress address) throws IOException {
    parse(":2.1").connect();
    new Verifications() {{
      AFUNIXSocket.connectTo((AFUNIXSocketAddress) any);
      new Display((Socket) any, 2, 1);
    }};
  }

  @Test
  void connect_tcp_socket_unknown_host(@Mocked InetAddress address) throws UnknownHostException {
    UnknownHostException cause = new UnknownHostException("expected");
    new Expectations() {{
      InetAddress.getByName("hostName"); result = cause;
    }};
    X11ClientException exception = assertThrows(X11ClientException.class, () -> parse("hostName:2.1").connect());
    assertThat(exception).hasMessage("Failed to create connection to \"hostName:2.1\".");
    assertThat(exception).hasCause(cause);
  }

  @Test
  void connect_tcp_socket_hostname(@Mocked Display display, @Mocked Socket socket, @Mocked InetAddress address) throws IOException {
    parse("hostName:2.1").connect();
    new Verifications() {{
      InetAddress.getByName("hostName");
      new Display((Socket) any, 2, 1);
    }};
  }

  @Test
  void connect_tcp_socket_localhost(@Mocked Display display, @Mocked Socket socket, @Mocked InetAddress address) throws IOException {
    parse("hostName:2.1").withHostName(null).connect();
    new Verifications() {{
      InetAddress.getLocalHost();
      new Display((Socket) any, 2, 1);
    }};
  }
}
