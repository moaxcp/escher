package gnu.x11.extension;


import gnu.x11.*;

public interface ErrorFactory {
  X11ServiceException build (gnu.x11.Display display, int code, int seqNumber, int bad,
                             int minorOpcode, int majorOpcode);
}
