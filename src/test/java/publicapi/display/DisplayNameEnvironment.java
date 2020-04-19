package publicapi.display;

import gnu.x11.*;
import org.junit.*;
import org.junit.runner.*;
import org.powermock.core.classloader.annotations.*;
import org.powermock.modules.junit4.*;

import static gnu.x11.DisplayName.*;
import static org.assertj.core.api.Assertions.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { DisplayName.class })
public class DisplayNameEnvironment {
  @Test
  public void parse_environment() {
    mockStatic(System.class);
    when(System.getenv("DISPLAY")).thenReturn(":1.2");
    DisplayName name = parse();
    assertThat(name.getHostName()).isNull();
    assertThat(name.getDisplayNumber()).isEqualTo(1);
    assertThat(name.getScreenNumber()).isEqualTo(2);
  }
}
