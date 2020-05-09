package gnu.app;

import com.github.moaxcp.xephyr.*;
import gnu.app.displayhack.*;
import gnu.app.glxdemo.*;
import org.junit.jupiter.api.*;

import java.io.*;

public class GLXMain {

  private static final String[] args = new String[]{"--display", ":1"};

  private XephyrRunner runner;

  @BeforeEach
  void setup() throws IOException {
    runner = XephyrRunner.builder()
        .ac(true)
        .br(true)
        .noreset(true)
        .iglx(true)
        .glamor(true)
        .enableExtension("GLX")
        .arg(":1")
        .build();
    runner.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    runner.stop();
  }

  @Test
  void abgr() {
    ABGR.main(args);
  }
}
