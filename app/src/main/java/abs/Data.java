package abs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import abs.parts.Bus;
import arch.main.DataFill;
import ru.mefccplusstudios.shellulspu2.DateRange;

public class Data {
    private final Context context;
    private final SharedPreferences settings;

    public boolean isDebugMode = false;
    public int SAVED_YEAR, SAVED_MONTH, SAVED_WEEK, FOCUS_TAB, ACTIVED_TAB;
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
    public ArrayList<ArrayList<DataFill>> getLessonsByPeriod(DateRange dr) {
        ArrayList<ArrayList<DataFill>> tlessons = new ArrayList<>();
        int START_ID = Integer.valueOf(dr.BEGIN_YEAR + Bus.time.getNormalNumber(dr.BEGIN_MONTH)+Bus.time.getNormalNumber(dr.BEGIN_DAY));
        int END_ID = Integer.valueOf(dr.END_YEAR+ Bus.time.getNormalNumber(dr.END_MONTH)+Bus.time.getNormalNumber(dr.END_DAY));
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
        this.SEARCH = settings.getString("group", "null");
        this.SAVED_YEAR = settings.getInt("syear", Bus.time.TOTAL_YEAR);
        this.SAVED_MONTH= settings.getInt("smonth", Bus.time.TOTAL_MONTH);
        this.SAVED_WEEK= settings.getInt("sweek", 0);
        this.FOCUS_TAB = settings.getInt("atab", 0);
        Bus.style.THEME_PARADIGM = settings.getInt("themer", 0);
        Bus.style.loadStyle(Bus.style.THEME_PARADIGM);
        Bus.style.FONT_SIZE_SP = settings.getInt("fsize", 20);
        Bus.data.ACTIVED_TAB = FOCUS_TAB;
        Bus.style.loadStyle(Bus.style.THEME_PARADIGM);
    }
    public void saveSettings() {
        SharedPreferences.Editor setedit = settings.edit();
        setedit.putInt("syear", this.SAVED_YEAR);
        setedit.putInt("smonth", this.SAVED_MONTH);
        setedit.putInt("sweek", this.SAVED_WEEK);
        setedit.putInt("atab", this.FOCUS_TAB);
        setedit.putBoolean("debug_mode", this.isDebugMode);
        setedit.putString("group", this.SEARCH);
        setedit.putInt("themer", Bus.style.THEME_PARADIGM);
        setedit.putInt("fsize", Bus.style.FONT_SIZE_SP);
        setedit.commit();
    }
}
