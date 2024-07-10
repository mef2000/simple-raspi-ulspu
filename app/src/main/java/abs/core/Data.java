package abs.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class Data {
    private final Context context;
    private final SharedPreferences settings;

    public boolean isDebugMode = false;
    public boolean isBarSupport = true;

    public int YEAR_SBEGIN, YEAR_SEND, MONTH_SBEGIN, MONTH_SEND, DAY_SBEGIN, DAY_SEND, FOCUS_TAB, ACTIVED_TAB;
    public String SEARCH = "null";

    public ArrayList<String> GROUPS = new ArrayList<>();
    public ArrayList<String> ROOMS = new ArrayList<>();
    public ArrayList<String> TEACHERS = new ArrayList<>();
    public SortedMap<Integer, ArrayList<DataFill>> filler = new TreeMap<>();
    public Data(Context context) {
        this.context = context;
        settings = context.getSharedPreferences("kon_games_global", Context.MODE_PRIVATE);
    }
    public void addDataFill(DataFill df) {
        ArrayList<DataFill> less;
        if(filler.containsKey(df.TIME_ID)) {
            less = filler.get(df.TIME_ID);
        }else{
            less = new ArrayList<>();
            filler.put(df.TIME_ID, less);
        }
        less.add(df);
    }
    public ArrayList<ArrayList<DataFill>> getLessonsByPeriod() {
        ArrayList<ArrayList<DataFill>> tlessons = new ArrayList<>();
        int START_ID = Integer.valueOf(Bus.data.YEAR_SBEGIN + Bus.time.getNormalNumber(Bus.data.MONTH_SBEGIN)+Bus.time.getNormalNumber(Bus.data.DAY_SBEGIN));
        int END_ID = Integer.valueOf(Bus.data.YEAR_SEND + Bus.time.getNormalNumber(Bus.data.MONTH_SEND)+Bus.time.getNormalNumber(Bus.data.DAY_SEND));
        Map<Integer, ArrayList<DataFill>> submap = filler.subMap(START_ID, END_ID+1);
        tlessons.addAll(submap.values());
        return tlessons;
    }
    public String getListMode(int mode) {
        switch(mode){
            default: return "group";
            case 1: return "teacher";
            case 2: return "room";
        }
    }
    public void loadSettings() {
        this.isDebugMode = settings.getBoolean("debug_mode", false);
        this.isBarSupport = settings.getBoolean("bar_mode", true);
        this.SEARCH = settings.getString("group", "null");
        this.YEAR_SBEGIN = settings.getInt("sbyear", Bus.time.TOTAL_YEAR);
        this.YEAR_SEND = settings.getInt("seyear", Bus.time.TOTAL_YEAR);
        this.MONTH_SBEGIN = settings.getInt("sbmonth", Bus.time.TOTAL_MONTH);
        this.MONTH_SEND = settings.getInt("semonth", Bus.time.TOTAL_MONTH);
        this.DAY_SBEGIN = settings.getInt("sbday", Bus.time.TOTAL_DAY);
        this.DAY_SEND = settings.getInt("seday", Bus.time.TOTAL_DAY);

        this.FOCUS_TAB = settings.getInt("atab", 0);
        Bus.style.THEME_PARADIGM = settings.getInt("themer", 0);
        Bus.style.loadStyle(Bus.style.THEME_PARADIGM);
        Bus.style.FONT_SIZE_SP = settings.getInt("fsize", 20);
        Bus.data.ACTIVED_TAB = FOCUS_TAB;
        Bus.style.loadStyle(Bus.style.THEME_PARADIGM);
    }
    public void saveSettings() {
        SharedPreferences.Editor setedit = settings.edit();

        setedit.putInt("sbyear", YEAR_SBEGIN);
        setedit.putInt("seyear", YEAR_SEND);
        setedit.putInt("sbmonth", MONTH_SBEGIN);
        setedit.putInt("semonth", MONTH_SEND);
        setedit.putInt("sbday", DAY_SBEGIN);
        setedit.putInt("seday", DAY_SEND);

        setedit.putInt("atab", this.FOCUS_TAB);
        setedit.putBoolean("debug_mode", this.isDebugMode);
        setedit.putBoolean("bar_mode", this.isBarSupport);
        setedit.putString("group", this.SEARCH);
        setedit.putInt("themer", Bus.style.THEME_PARADIGM);
        setedit.putInt("fsize", Bus.style.FONT_SIZE_SP);
        setedit.commit();
    }
}
