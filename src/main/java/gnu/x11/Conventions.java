package gnu.x11;

import java.io.File;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Conventions {
  public static File getXAuthorityFile() {
    String authFilename = System.getenv("XAUTHORITY");
    if (authFilename == null || authFilename.equals("")) {
      authFilename = System.getProperty("user.home") + File.separatorChar + ".Xauthority";
    }
    return new File(authFilename);
  }
}
