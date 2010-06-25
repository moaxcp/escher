
package gnu.x11;

import gnu.x11.event.Event;
import gnu.x11.extension.ErrorFactory;
import gnu.x11.extension.EventFactory;
import gnu.x11.extension.BigRequests;
import gnu.x11.extension.NotFoundException;
import gnu.x11.extension.XCMisc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

/** X server connection. */
public class Display {

    /* 
     * TODO: fix all the public fields to be private and replace them with
     * accessors
     */
    
    private static java.util.logging.Logger logger;
    static {

        logger = java.util.logging.Logger.getLogger("gnu.x11.Display");
        logger.setLevel(Level.ALL);
    }

    public static final int CURRENT_TIME = 0;

    // TODO better handling debug flag
    public static final boolean DEBUG = false;

    /**
     * The output stream.
     */
    private RequestOutputStream out;

    /**
     * The input stream.
     */
    private ResponseInputStream in;

    /**
     * The socket.
     */
    private Socket socket;

    /**
     * The hostname to this display.
     */
    private String hostname;

    /**
     * The display number.
     */
    private int displayNumber;

    /**
     *  The display input (Keyboard/mouse) 
     */
    private Input input;

    /**
     * Indicates if this display is connected or not.
     */
    private boolean connected;

    // Server information
    private int releaseNumber;

    private String vendor;

    private int maximumRequestLength;

    /** Contains an array view of the Screens supported by this Display. */
    private Screen[] screens;
    
    /**
     * Contains all the visual info mapped by the ID.
     */
    private HashMap<Integer, VisualInfo> visuals = new HashMap<Integer, VisualInfo>();
       
    public Pixmap.Format[] pixmapFormats;

    public int imageByteOrder;

    public int bitmapFormatBitOrder;

    public int bitmapFormatScanlineUnit;

    public int bitmapFormatScanlinePad;

    public int resourceBase;

    public int resourceMask;

    // Defaults
    public Color defaultBlack, defaultWhite;

    public Colormap defaultColormap;

    public int defaultDepth;

    public Pixmap.Format defaultPixmapFormat;

    private Window defaultRoot;

    public Screen defaultScreen;

    public int defaultScreenNumber;

    int minKeycode;

    int maxKeycode;

    /**
     * @see Screen#default_gc()
     */
    public GC defaultGC;

    // Resources
    public Hashtable<Integer, Resource> resources = new Hashtable<Integer, Resource>(257);

    public int resourceIndex;

    private Hashtable<Integer, Atom> atomIDs = new Hashtable<Integer, Atom>(257);

    private Hashtable<String, Atom> atoms = new Hashtable<String, Atom>(257);

    // XCMisc
    public XCMisc xcmisc;

    public boolean useXcmisc;

    public int xcmiscResourceBase;

    public int xcmiscResourceCount;

    // Extension
    public boolean bigRequestsPresent;

    public int extendedMaximumRequestLength;

    /**
     * Major opcodes 128 through 255 are reserved for extensions, totally 128.
     */
    public String[] extensionOpcodeStrings = new String[128];

    public String[][] extensionMinorOpcodeStrings = new String[128][];

    /**
     * Event codes 64 through 127 are reserved for extensiones, totally 64.
     */
    public EventFactory[] extensionEventFactories = new EventFactory[64];

    /**
     * Error codes 128 through 255 are reserved for extensiones, totally 128.
     */
    public ErrorFactory[] extensionErrorFactories = new ErrorFactory[128];

    /**
     * Open a connection to the display defined by the DISPLAY
     * environment variable. If this variable is not set, the
     * connection is opened on {@code localhost:0.0}.
     * 
     * #Display(String, int, int)
     * 
     * @throws EscherServerConnectionException
     */
    public Display() throws EscherServerConnectionException {

        this(new DisplayName(System.getenv("DISPLAY")));
    }

    /**
     * #Display(String, int, int)
     * 
     * @throws EscherServerConnectionException
     */
    public Display(DisplayName name) throws EscherServerConnectionException {

        this(name.hostname, name.display_no, name.screen_no);
    }

    /**
     * #Display(String, int, int)
     * 
     * @throws EscherServerConnectionException
     */
    public Display(String hostname, int display_no)
            throws EscherServerConnectionException {

        this(hostname, display_no, 0);
    }

    /**
     * Sets up a display using a connection over the specified <code>socket</code>.
     * This should be used when there is a need to use non-TCP sockets, like
     * connecting to an X server via Unix domain sockets. You need to provide an
     * implementation for this kind of socket though.
     * 
     * @param socket
     *            the socket to use for that connection
     * @param hostname
     *            the hostname to connect to
     * @param display_no
     *            the display number
     * @param screen_no
     *            the screen number
     * @throws EscherServerConnectionException
     */
    public Display(Socket socket, String hostname,
                   int display_no, int screen_no)
            throws EscherServerConnectionException {

        defaultScreenNumber = screen_no;
        this.hostname = hostname;
        this.displayNumber = display_no;
        this.socket = socket;
        init_streams();
        init();
    }

    /**
     * @throws EscherServerConnectionException
     * @see <a href="XOpenDisplay.html">XOpenDisplay</a>
     */
    public Display(String hostname, int display_no, int screen_no)
            throws EscherServerConnectionException {
        
        defaultScreenNumber = screen_no;
        this.displayNumber = display_no;
        this.hostname = hostname;
        try {
            socket = new Socket(hostname, 6000 + display_no);
        } catch (IOException ex) {
            handle_exception(ex);
        }
        init_streams();
        init();
    }

    /**
     * Performs client connection to the XServer, initialising all
     * the internal datastructures.
     * 
     * @throws EscherServerConnectionException
     */
    private void init() throws EscherServerConnectionException {

        // authorization protocol
        XAuthority xauth = get_authority();

        byte[] auth_name;
        byte[] auth_data;
        if (xauth != null) {
            auth_name = xauth.protocol_name;
            auth_data = xauth.protocol_data;
        } else {
            // In case the X authority couldn't be established...
            auth_name = new byte[0];
            auth_data = new byte[0];
        }

        RequestOutputStream o = out;
        synchronized (o) {
            o.writeInt8('B');
            o.writeInt8(0); // Unused.
            o.writeInt16(11);// major version
            o.writeInt16(0);// minor version
            o.writeInt16(auth_name.length);
            o.writeInt16(auth_data.length);
            o.writeInt16(0); // Unused.
            o.writeBytes(auth_name);
            o.writePad(auth_name.length);
            o.writeBytes(auth_data);
            o.writePad(auth_data.length);
            o.flush();
            ResponseInputStream i = in;
            synchronized (i) {
                // Don't do read_reply() here, this is not needed and
                // doesn't work during connection setup.
                connected = true;
                init_server_info(i);
            }
        }

        maximumRequestLength = out.setBufferSize(maximumRequestLength);
        init_keyboard_mapping();
        init_defaults();
        init_big_request_extension();
    }

    /**
     * This method returns a list of visual information that match the
     * specified attributes given in the visual information
     * template.
     * 
     * The list can be empty if no visual match the given template.
     * 
     * If template is null, it returns a list of all the visual information
     * for the given screen, behaving like {@link #getVisualInfo()}.
     * 
     * 
     * @param screenNo
     * @param template
     * @return
     */
    public List<VisualInfo> getVisualInfo(VisualInfo template,
                                          int visualInfoMask) {
        
        List<VisualInfo> visuals = new ArrayList<VisualInfo>();
        
        // A visual is contained into a Depth object
        // and this is contained into the Screen object.
        // Xlib get the list of visuals by first checking if the user
        // query for a specific screen, and then lopping through all the depth
        // for each sreeen.
        
        Screen [] screens = null;
        if ((visualInfoMask & VisualInfoMask.VisualScreenMask) != 0) {
            
            screens = new Screen[1];
            screens[0] = template.getScreen();
        } else {
           
            screens = getScreens();
        }
        
        for (Screen screen : screens) {
            for (Depth depth : screen.getDepths()) {
                
                // Roman, this is why I hate the sun coding conventions
                // of 4 space tabs
                if (((visualInfoMask & VisualInfoMask.VisualDepthMask) != 0)
                        && (template.getDepth() == depth.getDepth()))
                    continue;

                for (VisualInfo visual : depth.getVisuals()) {
                    
                    if (((visualInfoMask & VisualInfoMask.VisualIDMask) != 0)
                            && (template.getID() != visual.getID())) 
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualClassMask) != 0)
                            && (!template.getVisualClass().
                                    equals(visual.getVisualClass())))
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualRedMaskMask)
                                != 0)
                            && (template.getRedMask() != visual.getRedMask()))
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualGreenMaskMask)
                                != 0)
                            && (template.getGreenMask()
                                    != visual.getGreenMask()))
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualBlueMaskMask)
                                != 0)
                            && (template.getBlueMask() != visual.getBlueMask()))
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualBlueMaskMask)
                                != 0)
                            && (template.getBlueMask() != visual.getBlueMask()))
                        continue;
                    
                    if (((visualInfoMask & VisualInfoMask.VisualBlueMaskMask)
                                != 0)
                            && (template.getBlueMask() != visual.getBlueMask()))
                        continue;
                
                    if (((visualInfoMask & 
                            VisualInfoMask.VisualColormapSizeMask)
                                != 0)
                            && (template.getColormapEntries()
                                    != visual.getColormapEntries()))
                      continue;
                  
                    if (((visualInfoMask & VisualInfoMask.VisualBitsPerRGBMask)
                                != 0)
                           && (template.getBitsPerRGBValue()
                                   != visual.getBitsPerRGBValue()))
                      continue;
                  
                  visuals.add(visual);
                }
            }
        }
        
        return visuals;
    }

    public Screen[] getScreens() {

        return this.screens;
    }

    /**
     * Returns a list of all the Visuals supported by this Display.
     * @return
     */
    public List<VisualInfo> getVisualInfo() {
        
        return this.getVisualInfo(null, VisualInfoMask.VisualNoMask);
    }
    
    /**
     * Returns a list of all the Visuals supported by this Display that
     * match this template.
     * 
     * The default mask is {@code VisualInfoMask.VisualNoMask}.
     */
    public List<VisualInfo> getVisualInfo(VisualInfo template) {
        
        return this.getVisualInfo(template, template.getVisualInfoMask());
    }
    
    /**
     * Return a single Visual associated to the given id. This is
     * an optimized version of {@link #getVisualInfo(VisualInfo, int)},
     * when the id is known in advance. This method may return {@code null},
     * as opposed to the other {@code getVisualInfo()}, which always
     * return at least a List with no entries.
     */
    public VisualInfo getVisualInfo(int id) {
        
        return this.visuals.get(id);
    }
    
    /**
     * Return the default VisualInfo of the given screen.
     */
    public VisualInfo getDefaultVisual(Screen screen) {
     
        return this.getVisualInfo(screen.root_visual_id());
    }
    
    /**
     * Return the default VisualInfo of the default screen.
     */
    public VisualInfo getDefaultVisual() {
     
        return this.getVisualInfo(this.defaultScreen.root_visual_id());
    }
    
    // opcode 23 - get selection owner
    /**
     * @see <a href="XGetSelectionOwner.html">XGetSelectionOwner</a>
     */
    public Window selection_owner(Atom selection) {

        RequestOutputStream o = out;
        int owner_id = -1;
        synchronized (o) {
            o.beginRequest(23, 0, 2);
            o.writeInt32(selection.getID());
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(8);
                owner_id = i.readInt32();
                i.skip(20);
            }
        }
        return (Window) Window.intern(this, owner_id);
    }

    // opcode 36 - grab server
    public synchronized void grab_server() {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(36, 0, 1);
            o.send();
        }
    }

    // opcode 37 - ungrab server
    public void ungrab_server() {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(37, 0, 1);
            o.send();
        }
    }

    // opcode 49 - list fonts
    /**
     * @return valid: {@link Enum#next()} of type {@link Font},
     *         {@link Enum#next_string()}
     * @see <a href="XListFonts.html">XListFonts</a>
     */
    public Font[] fonts(String pattern, int max_name_count) {

        int n = pattern.length();
        int p = RequestOutputStream.pad(n);

        RequestOutputStream o = out;
        Font[] fonts = null;
        synchronized (o) {
            o.beginRequest(49, 0, 2 + (n + p) / 4);
            o.writeInt16(max_name_count);
            o.writeInt16(n);
            o.writeString8(pattern);
            o.skip(p);

            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(4);
                int len = i.readInt32() * 4; // Number of bytes for the reply.
                int num_strings = i.readInt16();
                i.skip(22);
                fonts = new Font[num_strings];
                for (int j = 0; j < num_strings; j++) {
                    int strlen = i.readInt8();
                    String str = i.readString8(strlen);
                    len -= strlen + 1;
                    fonts[j] = new Font(this, str);
                }
                i.skip(len); // Pad the remaining bytes.
            }
        }
        return fonts;
    }

    // opcode 50 - list fonts with info
    /**
     * @see <a href="XListFontsWithInfo.html">XListFontsWithInfo</a>
     */
    public Data fonts_with_info(String pattern, int max_name_count) {

        // FIXME: Implement.
        return null;
    }

    // opcode 51 - set font path
    /**
     * @see <a href="XSetFontPath.html">XSetFontPath</a>
     */
    public void set_font_path(int count, String[] path) {

        int n = 0;
        for (int i = 0; i < path.length; i++) {
            n += path.length + 1;
        }
        int p = RequestOutputStream.pad(n);

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(51, 0, 2 + (n + p) / 4);
            o.writeInt16(path.length);
            o.skip(2);
            for (int i = 0; i < path.length; i++) {
                o.writeInt8(path[i].length());
                o.writeString8(path[i]);
            }
            o.skip(p);
            o.send();
        }
    }

    // opcode 52 - get font path
    /**
     * Returns the current search path for fonts.
     * 
     * @return the current search path for fonts
     * @see #set_font_path(int, String[])
     * @see <a href="XGetFontPath.html">XGetFontPath</a>
     */
    public String[] font_path() {

        RequestOutputStream o = out;
        String[] path;
        synchronized (o) {
            o.beginRequest(52, 0, 1);
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(4);
                int reply_length = i.readInt32() * 4;
                int num_strings = i.readInt16();
                i.skip(22);
                path = new String[num_strings];
                int bytes_read = 0;
                for (int j = 0; j < num_strings; j++) {
                    int num_chars = i.readInt8();
                    path[j] = i.readString8(num_chars);
                    bytes_read += num_chars + 1;
                }
                i.skip(reply_length - bytes_read);
            }
        }
        return path;
    }

    /**
     * Information about an X extension.
     * 
     * @see Display#query_extension .
     */
    public static class ExtensionInfo {

        private boolean present;

        private int major_opcode;

        private int first_event;

        private int first_error;

        ExtensionInfo(ResponseInputStream in) {

            present = in.readBool();
            major_opcode = in.readInt8();
            first_event = in.readInt8();
            first_error = in.readInt8();
            // System.err.println("first error: " + first_error);
            // Thread.dumpStack ();
        }

        public boolean present() {

            return present;
        }

        public int major_opcode() {

            return major_opcode;
        }

        public int first_event() {

            return first_event;
        }

        public int first_error() {

            return first_error;
        }
    }

    // opcode 98 - query extension
    /**
     * Determines if the named extension is present. If so, the major opcode for
     * the extension is returned, if it has one. Otherwise zero is returned. Any
     * minor opcode or the request formats are specific to the extension. If the
     * extension involves additional event types, the base event type code is
     * returned. Otherwise zero is returned. The format of the events is specific
     * to the extension. If the extension involves additional error codes, the
     * base error code is returned. The format of additional data in the errors is
     * specific to the extension. The name should use ISO-Latin1 encoding, and
     * uppercase and lowercase do matter.
     * 
     * @param name
     *            the name of the extension to query
     * @return
     * @see <a href="XQueryExtension.html">XQueryExtension</a>
     */
    public ExtensionInfo query_extension(String name) {

        int n = name.length();
        int p = RequestOutputStream.pad(n);

        ExtensionInfo info;
        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(98, 0, 2 + (n + p) / 4);
            o.writeInt16(n);
            o.skip(2);
            o.writeString8(name);
            o.skip(p);
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(8);
                info = new ExtensionInfo(i);
                i.skip(20);
            }
        }
        return info;
    }

    // opcode 99 - list extensions
    /**
     * Returns a list of all extensions supported by the server.
     * 
     * @return a list of all extensions supported by the server
     * @see <a href="XListExtensions.html">XListExtensions</a>
     */
    public String[] extensions() {

        String[] exts;
        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(99, 9, 1);
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(1);
                int num_strs = i.readInt8();
                i.skip(2);
                int reply_length = i.readInt32() * 4;
                exts = new String[num_strs];
                i.skip(24);
                int bytes_read = 0;
                for (int j = 0; j < num_strs; j++) {
                    int len = i.readInt8();
                    exts[j] = i.readString8(len);
                    bytes_read += len + 1;
                }
                i.skip(reply_length - bytes_read);
            }
        }
        return exts;
    }

    // opcode 104 - bell
    /**
     * Rings the bell on the keyboard at a volume relative to the base volume of
     * the keyboard, if possible. Percent can range from -100 to +100 inclusive
     * (or a Value error results). The volume at which the bell is rung when
     * percent is nonnegative is: base - [(base * percent) / 100] + percent When
     * percent is negative, it is: base + [(base * percent) / 100]
     * 
     * @param volume,
     *            see above
     * @see <a href="XBell.html">XBell</a>
     */
    public void bell(int percent) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(104, percent, 1);
            o.send();
        }
    }

    /* FIXME: use enums */
    public static final int NO = 0;
    public static final int YES = 1;
    public static final int DEFAULT = 2;
    public static final String[] SCREEN_SAVER_STRINGS = {
                    "no", "yes", "default"
    };

    // opcode 107 - set screen saver
    /**
     * @param prefer_blanking
     *            valid: {@link #NO}, {@link #YES}, {@link #DEFAULT}
     * @param allow_exposures
     *            valid: {@link #NO}, {@link #YES}, {@link #DEFAULT}
     * @see <a href="XSetScreenSaver.html">XSetScreenSaver</a>
     */
    public void set_screen_saver(int timeout, int interval,
                                 int prefer_blanking, int allow_exposures) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(107, 0, 3);
            o.writeInt16(timeout);
            o.writeInt16(interval);
            o.writeInt8(prefer_blanking);
            o.writeInt8(allow_exposures);
            o.skip(2);
            o.send();
        }
    }

    /**
     * Informations about the screensaver.
     * 
     * @see {@link Display#get_screen_saver()}.
     */
    public static class ScreenSaverInfo {

        private int timeout;

        private int interval;

        private boolean prefer_blanking;

        private boolean allow_exposures;

        ScreenSaverInfo(ResponseInputStream in) {

            timeout = in.readInt16();
            interval = in.readInt16();
            prefer_blanking = in.readBool();
            allow_exposures = in.readBool();
        }

        public int timeout() {

            return timeout;
        }

        public int interval() {

            return interval;
        }

        public boolean prefer_blanking() {

            return prefer_blanking;
        }

        public boolean allow_exposures() {

            return allow_exposures;
        }

        public String toString() {

            return "#ScreenSaverReply" + "\n  timeout: " + timeout()
                            + "\n  interval: " + interval()
                            + "\n  prefer-blanking: " + prefer_blanking()
                            + "\n  allow-exposures: " + allow_exposures();
        }
    }

    // opcode 108 - get screen saver
    /**
     * Returns the screensaver control values.
     * 
     * @return the screensaver control values
     * @see <a href="XGetScreenSaver.html">XGetScreenSaver</a>
     */
    public ScreenSaverInfo screen_saver() {

        ScreenSaverInfo info;
        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(108, 0, 1);
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(8);
                info = new ScreenSaverInfo(i);
                i.skip(18);
            }
        }
        return info;
    }

    public static final int INSERT = 0;

    public static final int DELETE = 1;

    // opcode 109 - change hosts
    /**
     * @param mode
     *            valid: {@link #INSERT}, {@link #DELETE}
     * @see <a href="XAddHost.html">XAddHost</a>
     * @see <a href="XRemoveHost.html">XRemoveHost</a>
     */
    public void change_hosts(int mode, int family, byte[] host) {

        int n = host.length;
        int p = RequestOutputStream.pad(n);

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(109, mode, 2 + (n + p) / 4);
            o.writeInt8(family);
            o.skip(1);
            o.writeInt16(n);
            o.writeBytes(host);
            o.skip(p);
            o.send();
        }
    }

    /**
     * Information about a host.
     * 
     * @see Display#list_hosts()
     */
    public static class Host {

        public static final int INTERNET = 0;

        public static final int DECNET = 1;

        public static final int CHAOS = 2;

        public int family;

        public byte[] address;

        /**
         * Reads one Host instance from a ResponseInputStream.
         * 
         * @param in
         *            the input stream to read from
         */
        Host(ResponseInputStream in) {

            family = in.readInt8();
            in.skip(1);
            int add_len = in.readInt16();
            address = new byte[add_len];
            in.readData(address);
            in.pad(add_len);
        }
    }

    /**
     * Hosts currently on the access control list and whether use of the list at
     * connection setup is currently enabled or disabled.
     * 
     * @see Display#list_hosts
     */
    public static class HostsInfo {

        public boolean mode;

        Host[] hosts;

        HostsInfo(ResponseInputStream in) {

            mode = in.readBool();
            in.skip(6);
            int num_hosts = in.readInt16();
            in.skip(22);
            hosts = new Host[num_hosts];
            for (int i = 0; i < num_hosts; i++)
                hosts[i] = new Host(in);
        }

    }

    // opcode 110 - list hosts
    /**
     * Returns the hosts currently on the access control list and whether use of
     * the list at connection setup is currently enabled or disabled.
     * 
     * @see <a href="XListHosts.html">XListHosts</a>
     */
    public HostsInfo list_hosts() {

        HostsInfo info;
        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(110, 0, 1);
            ResponseInputStream i = in;
            synchronized (i) {
                i.readReply(o);
                i.skip(1);
                info = new HostsInfo(i);
            }
        }
        return info;
    }

    public static final int ENABLE = 0;

    public static final int DISABLE = 1;

    // opcode 111 - set access control
    /**
     * @param mode
     *            valid: {@link #ENABLE}, {@link #DISABLE}
     * @see <a href="XSetAccessControl.html">XSetAccessControl</a>
     */
    public void set_access_control(int mode) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(111, mode, 1);
            o.send();
        }
    }

    // opcode 113 - kill client
    /**
     * @see <a href="XKillClient.html">XKillClient</a>
     */
    public void kill_client(Resource resource) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(113, 0, 2);
            o.writeInt32(resource.id);
            o.send();
        }
    }

    public static final int DESTROY = 0;

    public static final int RETAIN_PERMANENT = 1;

    public static final int RETAIN_TEMPORARY = 2;

    // opcode 112 - set close down mode
    /**
     * @param mode
     *            valid: {@link #DESTROY}, {@link #RETAIN_PERMANENT},
     *            {@link #RETAIN_TEMPORARY}
     * @see <a href="XSetCloseDownMode.html">XSetCloseDownMode</a>
     */
    public void set_close_down_mode(int mode) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(112, mode, 1);
            o.send();
        }
    }

    public static final int ACTIVATE = 0;

    public static final int RESET = 1;

    // opcode 115 - force screen saver
    /**
     * @param mode
     *            valid: {@link #ACTIVATE}, {@link #RESET}
     * @see <a href="XForceScreenSaver.html">XForceScreenSaver</a>
     */
    public void force_screen_saver(int mode) {

        RequestOutputStream o = out;
        synchronized (o) {
            o.beginRequest(115, mode, 1);
            o.send();
        }
    }

    /**
     * From XC-MISC extension specification: When an X client connects to an X
     * server, it receives a fixed range of resource IDs to use to identify the
     * client's resources inside the X server. Xlib hands these out sequentially
     * as needed. When it overruns the end of the range, an IDChoice protocol
     * error results. Long running clients, or clients that use resource IDs at
     * a rapid rate, may encounter this circumstance. When it happens, there are
     * usually many resource IDs available, but Xlib doesn't know about them.
     * One approach to solving this problem would be to have Xlib notice when a
     * resource is freed and recycle its ID for future use. This strategy runs
     * into difficulties because sometimes freeing one resource causes others to
     * be freed (for example, when a window is destroyed, so are its children).
     * To do a complete job, Xlib would have to maintain a large amount of state
     * that currently resides only in the server (the entire window tree in the
     * above example). Even if a less comprehensive strategy was adopted, such
     * as recycling only those IDs that Xlib can identify without maintaining
     * additional state, the additional bookkeeping at resource creation and
     * destruction time would likely introduce unacceptable overhead. To avoid
     * the problems listed above, the server's complete knowledge of all
     * resource IDs in use by a client is leveraged. This extension provides two
     * ways for Xlib to query the server for available resource IDs. Xlib can
     * use these extension requests behind the scenes when it has exhausted its
     * current pool of resource IDs.
     */
    public int allocate_id(Object object) {

        /*
         * If XC-MISC is present, we use it. Otherwise, we fall back to allocate X
         * resource ID sequentially to the end without recycling ID (just as xlib
         * does). Sample values: resource base: 0x04000000 or
         * 00000100000000000000000000000000b resource mask: 0x003FFFFF or
         * 00000000001111111111111111111111b
         */

        if (!useXcmisc)
            // check if basic allocation fails
            useXcmisc = (resourceIndex + 1 & ~resourceMask) != 0;

        if (!useXcmisc) {
            int id = resourceIndex++ | resourceBase;
            resources.put(new Integer(id), object);
            return id;
        }

        if (xcmisc == null) {
            try {
                xcmisc = new XCMisc(this);
            } catch (NotFoundException e) {
                throw new RuntimeException("Failed to allocate new resource id");
            }
        }

        if (xcmiscResourceCount == 0) {
            // first time, or used up
            gnu.x11.extension.XCMisc.XIDRange rr = xcmisc.xid_range();
            xcmiscResourceBase = rr.start_id;
            xcmiscResourceCount = rr.count;
        }

        // give out in descending order
        xcmiscResourceCount--;
        return xcmiscResourceBase + xcmiscResourceCount;
    }

    /**
     * @see <a href="XCloseDisplay.html">XCloseDisplay</a>
     */
    public void close() {

        // FIXME: Implement more sensible shutdown.
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            handle_exception(ex);
        }
        connected = false;
    }

    /**
     * From Big Requests extension specification: It is desirable for core Xlib,
     * and other extensions, to use this extension internally when necessary. It
     * is also desirable to make the use of this extension as transparent as
     * possible to the X client. For example, if enabling of the extension were
     * delayed until the first time it was needed, an application that used
     * XNextRequest to determine the sequence number of a request would no longer
     * get the correct sequence number. As such, XOpenDisplay will determine if
     * the extension is supported by the server and, if it is, enable
     * extended-length encodings. The core Xlib functions XDrawLines, XDrawArcs,
     * XFillPolygon, XChangeProperty, XSetClipRectangles, and XSetRegion are
     * required to use extended-length encodings when necessary, if supported by
     * the server. Use of extended-length encodings in other core Xlib functions
     * (XDrawPoints, XDrawRectangles, XDrawSegments, XFillArcs, XFillRectangles,
     * XPutImage) is permitted but not required; an Xlib implementation may choose
     * to split the data across multiple smaller requests instead.
     */
    public void init_big_request_extension() {

        try {
            BigRequests big = new BigRequests(this);
            bigRequestsPresent = true;
            extendedMaximumRequestLength = big.enable();

        } catch (NotFoundException e) {
            bigRequestsPresent = false;
        }
    }

    public void init_defaults() {

        defaultScreen = screens[defaultScreenNumber];
        defaultRoot = defaultScreen.root(); // before init default_gc
        defaultDepth = defaultScreen.root_depth;
        defaultColormap = defaultScreen.default_colormap();
        defaultGC = defaultScreen.default_gc();
        defaultBlack = new Color(defaultScreen.black_pixel);
        defaultWhite = new Color(defaultScreen.white_pixel);

        for (int i = pixmapFormats.length - 1; i >= 0; i--) {
            if (pixmapFormats[i].getDepth() == defaultDepth) {
                defaultPixmapFormat = pixmapFormats[i];
                break;
            }
        }
    }

    /**
     * Reads the server information after connection setup. The information is
     * read from the connection's ResponseInputStream.
     * 
     * @throws EscherServerConnectionException
     */
    private void init_server_info(ResponseInputStream i) throws EscherServerConnectionException {

        int accepted = i.readInt8();
        boolean connectionFailed = false;
        StringBuilder debugMessage = new StringBuilder();
        switch (accepted) {
        
        case 0:
            debugMessage.append("Connection to the XServer failed.\n");
            debugMessage.append("Try to set DISPLAY variable or to give " +
                                "proper\n");
            debugMessage.append("permissions (eg. edit .Xauthority or " +
                                "run \"xhost +\")\n");
            debugMessage.append("NOTE: leaving \"xhost +\" and allowing " +
                                "remote tcp\n");
            debugMessage.append("connections to the XServer can be a " +
                                "potential\n");
            debugMessage.append("security risk.\n");

            connectionFailed = true;

            break;
        
        case 1:
            logger.warning("more auth data not yet implemented");
            break;

        default:
            logger.warning("init_server_info::Unknown server reply!");
            break;
        }

        int failedLength = 0;
        if (connectionFailed) {
            // length of reason
            failedLength = i.readByte();

        } else {
            // Unused.
            i.skip(1);
        }

        i.skip(2); // protocol-major-version.
        i.skip(2); // protocol-minor-version.
        i.skip(2); // Length.

        // try to print some more (maybe not so) meaningful messages to
        // understand the failure
        if (connectionFailed) {

            if (DEBUG) {

                String codes = (failedLength > 1 ? "codes" : "code");

                debugMessage.append("XServer returned " + failedLength +
                                    " error ");
                debugMessage.append(codes);

                int reason = 0;
                for (int n = 0; n < failedLength; n++) {
                    reason = i.readInt32();

                    debugMessage.append("XServer returned error code: " +
                                        reason + "\n");
                }

                debugMessage.append("XServer are allowed to use non " +
                                     "standard errors codes\n");
                debugMessage.append("Please, consult the manual of your " +
                                    "XServer\n" +
                                    "to map the error codes to human " +
                                    "readable values.\n");
            }

            logger.severe(debugMessage.toString());

            throw new EscherServerConnectionException("Connection to the " +
                                                      "XServer failed.");
        }

        releaseNumber = i.readInt32();
        resourceBase = i.readInt32();
        resourceMask = i.readInt32();
        i.skip(4); // motion-buffer-size.

        int vendor_length = i.readInt16();
        maximumRequestLength = i.readInt16();
        extendedMaximumRequestLength = maximumRequestLength;
        
        int screen_count = i.readInt8();
        int pixmap_format_count = i.readInt8();

        imageByteOrder = i.readInt8();
        
        bitmapFormatBitOrder = i.readInt8();
        bitmapFormatScanlineUnit = i.readInt8();
        bitmapFormatScanlinePad = i.readInt8();

        minKeycode = i.readInt8();
        maxKeycode = i.readInt8();
        i.skip(4); // Unused.

        vendor = i.readString8(vendor_length);
        i.pad(vendor_length);

        /* ***** FORMAT ***** */
        pixmapFormats = new Pixmap.Format[pixmap_format_count];
        for (int j = 0; j < pixmap_format_count; j++) {
            pixmapFormats[j] = new Pixmap.Format(i);
        }

        /* ***** SCREEN ***** */
        if (defaultScreenNumber < 0 || defaultScreenNumber >= screen_count)
            throw new RuntimeException("Invalid screen number (screen-count "
                            + screen_count + "): " + defaultScreenNumber);

        screens = new Screen[screen_count];
        for (int j = 0; j < screen_count; j++) {
            screens[j] = new Screen(i, this);
        }

    }

    /**
     * Initializes the keyboard mapping.
     */
    private void init_keyboard_mapping() {

        input = new Input(this, minKeycode, maxKeycode);
        input.keyboard_mapping();
    }

    public Event next_event() {

        return in.readEvent();
    }

    public String toString() {

        return "#Display" + "\n  default-screen-number: " + defaultScreenNumber
                        + "\n  vendor: " + vendor + "\n  release-number: "
                        + releaseNumber + "\n  maximum-request-length: "
                        + maximumRequestLength;
    }

    /**
     * Fetches the XAuthority that matches this display.
     * 
     * @return the XAuthority that matches this display
     */
    private XAuthority get_authority() {

        XAuthority[] auths = XAuthority.get_authorities();

        // Fetch hostname.
        if (hostname == null || hostname.equals("")
                        || hostname.equals("localhost")) {
            // Translate localhost hostnames to the real hostname of this host.
            try {
                InetAddress local = InetAddress.getLocalHost();
                hostname = local.getHostName();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }

        // Fetch display no.
        String display_no_str = String.valueOf(displayNumber);

        // Find the XAuthority that matches the hostname and display no.
        XAuthority found = null;
        for (int i = 0; i < auths.length; i++) {
            XAuthority auth = auths[i];
            try {
                if (auth.hostname != null
                                && auth.display.equals(display_no_str)
                                && InetAddress
                                                .getByName(auth.hostname)
                                                .equals(
                                                        InetAddress
                                                                        .getByName(hostname))) {
                    found = auth;
                    break;
                }
            } catch (UnknownHostException ex) {
                System.err.println("warning unknown host :" + auth.hostname);
            }
        }
        return found;
    }

    public void check_error() {

        // `XSync' function in `xc/lib/X11/Sync.c' uses the same technique.
        try {
            input.input_focus();

        } catch (Error e) {
            /*
             * When an X error occurs, Java throws an `gnu.x11.Error' exception, the
             * normal execution order is disrupted; the reply of `input_focus()'
             * resides in network buffer while nobody wants it. In case someone
             * (`gnu.x11.test.Shape') catches the error and continues to work, we
             * should discard the input focus reply (by clearing the socket input
             * stream). TODO Should I be careful not to clear other packets after
             * the reply of input focus? Some event may come after that?
             */
            try {
                in.skip(in.available());
            } catch (IOException ie) {
                throw new java.lang.Error(
                                          "Failed to clear socket input stream: "
                                                          + ie);
            }

            throw e;
        }
    }

    /**
     * Initializes the input and output streams.
     */
    private void init_streams() {

        String _debug = System.getProperty("escher.debug_streams", null);

        try {
            // TODO: Evaluate if we gain performance by using BufferedOutputStream
            // here.
            OutputStream o = socket.getOutputStream();
            //BufferedOutputStream buf_out = new BufferedOutputStream (o, 512);
            if (_debug != null)
                out = new DebugRequestOutputStream(o, this);
            else
                out = new RequestOutputStream(o, this);

            // Create buffered response input stream.
            InputStream sock_in = socket.getInputStream();
            // Buffer space for 4 response messages. More are hardly needed I'd
            // think.
            BufferedInputStream buf_in = new BufferedInputStream(sock_in, 128);
            in = new ResponseInputStream(buf_in, this);
            
        } catch (IOException ex) {
            handle_exception(ex);
        }
    }

    public void flush() {

        synchronized (out) {
            out.send();
            out.flush();
        }
    }

   
    public Window getRootWindow() {

        return defaultRoot;
    }
    
    private void handle_exception(Throwable ex) {

        ex.printStackTrace();
    }
    
    synchronized void addVisual(VisualInfo xVisual) {
        
        this.visuals.put(xVisual.getID(), xVisual);
    }
    
    synchronized void addAtom(int id, Atom atom) {
        this.atomIDs.put(id, atom);
    }
    
    synchronized void addAtom(String name, Atom atom) {
        this.atoms.put(name, atom);
    }
    
    synchronized Atom getAtom(int id) {
        return atomIDs.get(id);
    }
    
    synchronized Atom getAtom(String name) {
        return atomIDs.get(name);
    }
    
}
