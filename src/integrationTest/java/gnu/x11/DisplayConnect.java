package gnu.x11;

import com.github.moaxcp.xephyr.*;
import org.junit.jupiter.api.*;

import java.io.*;

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
    //todo this should throw an exception when it doesn't work
    //todo need to be able to connect using Unix Socket as well as http
    new Display(null, 1, 0);
  }
}
