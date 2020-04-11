package gnu.x11;

import com.github.moaxcp.xephyr.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static gnu.x11.Display.*;
import static gnu.x11.DisplayName.*;

public class DisplayConnect {

  private XephyrRunner runner;

  @BeforeEach
  void setup() throws IOException {
    runner = XephyrRunner.builder()
        .ac(true)
        .br(true)
        .noreset(true)
        .arg(":1")
        .build();
    runner.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    runner.stop();
  }

  @Test
  void connect() {
    unixConnection(parse(":1"));
  }
}
