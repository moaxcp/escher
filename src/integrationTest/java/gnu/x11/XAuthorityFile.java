package gnu.x11;

import gnu.x11.XAuthority.Family;
import java.io.File;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XAuthorityFile {
  @Test
  void readFile() throws DecoderException, UnknownHostException {
    List<XAuthority> authorities = XAuthority.getAuthorities(new File(getClass().getClassLoader().getResource(".Xauthority").getFile()));
    assertThat(authorities).hasSize(2);
    XAuthority first = authorities.get(0);
    assertThat(first.getFamily()).isEqualTo(Family.LOCAL);
    assertThat(new String(first.getAddress(), StandardCharsets.UTF_8)).isEqualTo("n1");
    assertThat(first.getDisplayNumber()).isEqualTo(0);
    assertThat(first.getProtocolName()).isEqualTo("MIT-MAGIC-COOKIE-1");
    assertThat(first.getProtocolData()).isEqualTo(Hex.decodeHex("e58717c9a5a6cb908954e38540f3eabf"));

    XAuthority second = authorities.get(1);
    assertThat(second.getFamily()).isEqualTo(Family.INTERNET);
    assertThat(second.getAddress()).isEqualTo(new byte[]{127, 0, 1, 1});
    assertThat(second.getDisplayNumber()).isEqualTo(2);
    assertThat(second.getProtocolName()).isEqualTo("MIT-MAGIC-COOKIE-1");
    assertThat(second.getProtocolData()).isEqualTo(Hex.decodeHex("7580c734c37f7c7e0d206b90008ad47f"));
  }
}
