package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.RequestOutputStream;
import gnu.x11.ResponseInputStream;
import gnu.x11.Window;

/**
 * X input-related event.
 */
public abstract class Input extends Event {

    private int time;

    private int root_window_id;

    private int event_window_id;

    private int child_window_id;

    private int root_x;

    private int root_y;

    private int event_x;

    private int event_y;

    private int state;

    private boolean same_screen;

    /**
     * Reads the event from the input stream.
     */
    public Input(Display display, ResponseInputStream in) {

        super(display, in);
        
        this.time = in.readInt32();
        this.root_window_id = in.readInt32();
        this.event_window_id = in.readInt32();
        this.child_window_id = in.readInt32();
        this.root_x = in.readInt16();
        this.root_y = in.readInt16();
        this.event_x = in.readInt16();
        this.event_y = in.readInt16();
        this.state = in.readInt16();
        this.same_screen = in.readBool();
        
        in.skip(1); // Unused.
    }

    public Input(Display display, int code) {

        super(display, code);
    }

    public int detail() {

        return detail;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {

        this.time = time;
    }

    /**
     * @return the time
     */
    public int getTime() {

        return time;
    }
    
    /**
     * @param root_window_id the root_window_id to set
     */
    public void setRootWindowID(int root_window_id) {

        this.root_window_id = root_window_id;
    }

    /**
     * @return the root_window_id
     */
    public int getRootWindowID() {

        return root_window_id;
    }

    /**
     * @param event_window_id the event_window_id to set
     */
    public void setEventWindowID(int event_window_id) {

        this.event_window_id = event_window_id;
    }

    /**
     * @return the event_window_id
     */
    public int getEventWindowID() {

        return event_window_id;
    }

    /**
     * @param same_screen the same_screen to set
     */
    public void setSameScreen(boolean same_screen) {

        this.same_screen = same_screen;
    }

    /**
     * @return the same_screen
     */
    public boolean isSameScreen() {

        return this.same_screen();
    }

    /**
     * @deprecated Use {@link #getRootID()} instead
     */
    public int root_id() {

        return getRootWindowID();
    }

    public int getRootID() {

        return root_id();
    }

    /**
     * @deprecated Use {@link #childID()} instead
     */
    public int child_id() {
        
        return child_window_id;
    }

    public int childID() {

        return child_id();
    }

    /**
     * @deprecated Use {@link #getRootX()} instead
     */
    public int root_x() {
        
        return root_x;
    }

    public int getRootX() {

        return root_x();
    }

    /**
     * @deprecated Use {@link #getRootY()} instead
     */
    public int root_y() {
        
        return root_y;
    }

    public int getRootY() {

        return root_y();
    }

    /**
     * @deprecated Use {@link #getEventX()} instead
     */
    public int event_x() {
        
        return event_x;
    }

    public int getEventX() {

        return event_x();
    }

    /**
     * @deprecated Use {@link #getEventY()} instead
     */
    public int event_y() {
        
        return event_y;
    }

    public int getEventY() {

        return event_y();
    }

    /**
     * @deprecated Use {@link #getState()} instead
     */
    public int state() {
        
        return state;
    }

    public int getState() {

        return state();
    }

    /**
     * @deprecated use {@link #isSameScreen()} instead
     */
    public boolean same_screen() {

        return this.same_screen;
    }

    /**
     * @deprecated Use {@link #getRoot()} instead
     */
    public Window root() {
        return (Window) Window.intern(display, getRootWindowID());
    }

    public Window getRoot() {

        return root();
    }

    /**
     * @deprecated Use {@link #getChild()} instead
     */
    public Window child() {
        
        return (Window) Window.intern(display, child_window_id);
    }

    public Window getChild() {

        return child();
    }

    /**
     * @deprecated Use {@link #setWindow(Window)} instead
     */
    public void set_window(Window w) {

        setEventWindowID(w.id);
    }

    public void setWindow(Window w) {

        set_window(w);
    }

    /**
     * @deprecated Use {@link #setDetail(int)} instead
     */
    public void set_detail(int d) {
        
        detail = d;
    }

    public void setDetail(int d) {

        set_detail(d);
    }

    /**
     * @deprecated Use {@link #setState(int)} instead
     */
    public void set_state(int s) {
        
        state = s;
    }

    public void setState(int s) {

        set_state(s);
    }

    public void write(RequestOutputStream o) {

        super.write(o);
        o.writeInt32(time);
        o.writeInt32(root_window_id);
        o.writeInt32(event_window_id);
        o.writeInt32(child_window_id);
        o.writeInt16(root_x);
        o.writeInt16(root_y);
        o.writeInt16(event_x);
        o.writeInt16(event_y);
        o.writeInt16(state);
        o.writeBool(same_screen);
        o.skip(1); // Unused.

    }
}
