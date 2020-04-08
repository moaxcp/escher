package com.github.moaxcp.xephyr;

import lombok.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class XephyrRunner {
  private final boolean br;
  private final boolean ac;
  private final boolean noreset;
  private final List<String> args;
  private Process process;

  @Builder
  public XephyrRunner(boolean br, boolean ac, boolean noreset, @Singular List<String> args) {
    this.br = br;
    this.ac = ac;
    this.noreset = noreset;
    this.args = args;
  }

  public void start() throws IOException {
    List<String> command = new ArrayList<>();
    command.add("Xephyr");
    if(br) {
      command.add("-br");
    }
    if(ac) {
      command.add("-ac");
    }
    if(noreset) {
      command.add("-noreset");
    }

    command.addAll(args);

    process = new ProcessBuilder(command.toArray(new String[command.size()]))
        .start();
  }

  public void stop() throws InterruptedException {
    process.destroy();
    process.waitFor(10000, TimeUnit.SECONDS);
    process.destroyForcibly();
    process.waitFor();
  }
}
