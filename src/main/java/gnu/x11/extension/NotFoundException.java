package gnu.x11.extension;


import gnu.x11.*;

public class NotFoundException extends X11ClientException {
  public NotFoundException(String s) { super(s); }
}
