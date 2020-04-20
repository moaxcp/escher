package publicapi.display;

import gnu.x11.*;
import org.junit.*;
import org.junit.runner.*;
import org.newsclub.net.unix.*;
import org.powermock.core.classloader.annotations.*;
import org.powermock.modules.junit4.*;

import java.io.*;

import static gnu.x11.Display.*;
import static gnu.x11.DisplayName.*;
import static org.assertj.core.api.Assertions.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Display.class, AFUNIXSocket.class})
public class DisplayConnectPowerMock {
  @Test
  public void connectUnixSocket() throws Exception {
    AFUNIXSocket socket = mock(AFUNIXSocket.class);
    mockStatic(AFUNIXSocket.class);
    Display expected = mock(Display.class);
    File socketFile = new File("/tmp/.X11-unix/X0");
    AFUNIXSocketAddress address = mock(AFUNIXSocketAddress.class);

    whenNew(AFUNIXSocketAddress.class).withArguments(socketFile).thenReturn(address);
    whenNew(Display.class).withArguments(socket, null, 0, 0).thenReturn(expected);
    when(AFUNIXSocket.connectTo(address)).thenReturn(socket);

    Display display = connect(parse(":0.0"));

    assertThat(display).isSameAs((expected));
  }
}
