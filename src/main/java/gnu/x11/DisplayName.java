package gnu.x11;

import lombok.*;

import static gnu.util.Strings.*;

/**
 * X display name.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayName {
  private final String hostName;
  private final int displayNumber;
  private final int screenNumber;

  /**
   * Parses a DisplayName from the DISPLAY environment variable.
   * @return resulting DiplayName
   */
  public static DisplayName parse() {
    return parse(System.getenv("DISPLAY"));
  }

  /**
   * Parses a DisplayName from convention. The convention for a display name is
   * <code>hostName:displayNumber.screenNumber</code>. hostName and screenNumber are optional.
   * @param convention of <code>hostName:displayNumber.screenNumber</code>
   * @return resulting DisplayName
   */
  public static DisplayName parse(@NonNull String convention) {
    requiresNonBlank("convention", convention);
    int i = convention.indexOf(':');

    // case 1: convention = hostName
    if (i == -1) {
      return new DisplayName(convention, 0, 0);
    }

    String hostName = i == 0 ? null : convention.substring(0, i);
    int j = convention.indexOf('.', i);

    // case 2: convention = hostName:displayNumber
    if (j == -1) {
      int displayNumber = Integer.parseInt(convention.substring(i + 1));
      return new DisplayName(hostName, displayNumber, 0);
    }

    // case 3: convention = hostName:displayNumber.screenNumber
    int displayNumber = Integer.parseInt(convention.substring(i + 1, j));
    int screenNumber = Integer.parseInt(convention.substring(j + 1));
    return new DisplayName(hostName, displayNumber, screenNumber);
  }

  /**
   * Returns the string representation of this DisplayName.
   * @return
   */
  public String toString() {
    String h = hostName == null ? "" : hostName;
    return h + ":" + displayNumber + "." + screenNumber;
  }
}
