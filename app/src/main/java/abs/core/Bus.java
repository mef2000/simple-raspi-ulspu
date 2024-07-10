package abs.core;

public final class Bus {
    public static Style style;
    public static Time time;
    public static Data data;

    private static Eventable GLOBAL;
    public static void connect(Style style, Time time, Data data, Eventable global) {
        Bus.style = style;
        Bus.time = time;
        Bus.data = data;
        GLOBAL = global;

        time.actual();
        data.loadSettings();

    }
    public static void disconnect() { //Fix Android memory leak... Broken link to Conxtext-ref objects, call in onStop/onDestroy
        style = null;
        time = null;
        data = null;
        GLOBAL = null;
    }
    public static void event(String tag, Object packet) {
        GLOBAL.event(tag, packet);
    }
    public static final String COLORS_CHANGED = "COLORS_SHEME_CHANGED";
    public static final String FONTS_CHANGED = "FONTS_SHEME_CHANGED";

    public static int ANY_BACK_THREAD = 0;
    public static int RUN_UI_THREAD = 1;
}
