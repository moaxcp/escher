package publicapi;

import gnu.x11.XAuthority.Family;
import java.io.*;
import java.util.Optional;
import mockit.*;
import org.junit.jupiter.api.Test;
import gnu.x11.XAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XAuthorityTest {

  @Injectable
  private DataInput in;

  @Test
  void constructor_fails_on_null_family() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> new XAuthority(null, "hostName".getBytes(), 0, "magic", new byte[1]));
    assertThat(exception).hasMessage("family is marked non-null but is null");
  }

  @Test
  void constructor_fails_on_null_address() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> new XAuthority(Family.LOCAL, null, 0, "magic", new byte[1]));
    assertThat(exception).hasMessage("address is marked non-null but is null");
  }

  @Test
  void constructor_fails_on_negative_displayNumber() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new XAuthority(Family.LOCAL, "host".getBytes(), -1, "magic", new byte[1]));
    assertThat(exception).hasMessage("displayNumber was \"-1\" expected >= 0.");
  }

  @Test
  void constructor_fails_on_null_protocolName() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> new XAuthority(Family.LOCAL, "host".getBytes(), 0, null, new byte[1]));
    assertThat(exception).hasMessage("protocolName is marked non-null but is null");
  }

  @Test
  void constructor_fails_on_blank_protocolName() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new XAuthority(Family.LOCAL, "host".getBytes(), 0, " ", new byte[1]));
    assertThat(exception).hasMessage("protocolName must not be blank.");
  }

  @Test
  void constructor_fails_on_null_protocolData() {
    NullPointerException exception = assertThrows(NullPointerException.class, () -> new XAuthority(Family.LOCAL, "host".getBytes(), 0, "magic", null));
    assertThat(exception).hasMessage("protocolData is marked non-null but is null");
  }

  @Test
  void constructor() {
    XAuthority xAuthority = new XAuthority(Family.LOCAL, "host".getBytes(), 0, "magic", new byte[]{1, 2, 3});
    assertThat(xAuthority.getFamily()).isEqualTo(Family.LOCAL);
    assertThat(xAuthority.getAddress()).isEqualTo("host".getBytes());
    assertThat(xAuthority.getDisplayNumber()).isEqualTo(0);
    assertThat(xAuthority.getProtocolName()).isEqualTo("magic");
    assertThat(xAuthority.getProtocolData()).isEqualTo(new byte[]{1, 2, 3});
  }

  @Test
  void read_empty_on_exception() throws IOException {
    new Expectations() {{
      in.readUnsignedShort(); result = new IOException();
    }};
    Optional<XAuthority> read = XAuthority.read(in);
    assertThat(read).isEmpty();
  }

  @Test
  void read() throws IOException {
    new Expectations() {{
      in.readUnsignedShort(); returns(256, 4, 3);
      in.readUTF(); returns("3", "magic");
      Delegate one = new Delegate() {
        public void readFully(byte[] bytes) {
          bytes[0] = 104;
          bytes[1] = 111;
          bytes[2] = 115;
          bytes[3] = 116;
        }
      };
      Delegate two = new Delegate() {
        public void readFully(byte[] bytes) {
          bytes[0] = 1;
          bytes[1] = 2;
          bytes[2] = 3;
        }
      };
      in.readFully((byte[]) any); result = new Delegate[] {one, two};
    }};
    Optional<XAuthority> read = XAuthority.read(in);
    assertThat(read).isPresent();
    XAuthority xAuthority = read.get();
    assertThat(xAuthority.getFamily()).isEqualTo(Family.LOCAL);
    assertThat(xAuthority.getAddress()).isEqualTo("host".getBytes());
    assertThat(xAuthority.getDisplayNumber()).isEqualTo(3);
    assertThat(xAuthority.getProtocolName()).isEqualTo("magic");
    assertThat(xAuthority.getProtocolData()).isEqualTo(new byte[]{1, 2, 3});
  }
}
