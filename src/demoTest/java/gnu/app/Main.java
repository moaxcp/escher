package gnu.app;

import com.github.moaxcp.xephyr.*;
import gnu.app.displayhack.*;
import org.junit.jupiter.api.*;

import java.io.*;

public class Main {

  private static final String[] args = new String[]{"--display", ":1"};

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
  void speedy() {
    Speedy.main(args);
  }

  @Test
  void winOp() {
    WinOp.main(args);
  }

  @Test
  void deco() {
    Deco.main(args);
  }

  @Test
  void munch() {
    Munch.main(args);
  }

  @Test
  void rorshach() {
    Rorschach.main(args);
  }

  @Test
  void squiral() {
    Squiral.main(args);
  }

  @Test
  void sprites() throws Exception {
    Sprites.main(args);
  }
}
