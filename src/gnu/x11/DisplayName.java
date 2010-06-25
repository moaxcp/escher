package gnu.x11;

/** X display name. */
class DisplayName {

    private String hostname;
    private int display_no;
    private int screen_no;

    public DisplayName(String displayName) {

        if (displayName == null)
            return;
        
        int i = displayName.indexOf(':');

        // case 1: display_name = hostname
        if (i == -1) {
            hostname = displayName;
            return;
        }

        hostname = displayName.substring(0, i);
        int j = displayName.indexOf('.', i);

        if (j == -1) {
            // case 2: display_name = hostname:display_no
            display_no = Integer.parseInt(displayName
                            .substring(i + 1, displayName.length()));
            return;
        }

        // case 3: display_name = hostname:display_no.screen_no
        display_no = Integer.parseInt(displayName.substring(i + 1, j));
        screen_no = Integer.parseInt(displayName
                        .substring(j + 1, displayName.length()));
    }

    public DisplayName(String hostname, int display_no, int screen_no) {

        this.hostname = hostname;
        this.display_no = display_no;
        this.screen_no = screen_no;
    }

    public String toString() {

        return hostname + ":" + display_no + "." + screen_no;
    }


    // Set and Gets
    
    public String getHostname() {
    
        return hostname;
    }

    
    public void setHostname(String hostname) {
    
        this.hostname = hostname;
    }

    
    public int getDisplay_no() {
    
        return display_no;
    }

    
    public void setDisplay_no(int displayNo) {
    
        display_no = displayNo;
    }

    
    public int getScreen_no() {
    
        return screen_no;
    }

    
    public void setScreen_no(int screenNo) {
    
        screen_no = screenNo;
    }
        
}