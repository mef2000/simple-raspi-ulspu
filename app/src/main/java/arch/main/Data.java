package arch.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.mefccplusstudios.shellulspu2.DateRange;
import ru.mefccplusstudios.shellulspu2.TimeUtils;

public class Data {
 //   public static final int INET_ERROR = 0;
//    public static final int UNKNOWN_ERROR = -1;

 //   public static final int OK_GROUPS_GET = 1;
 //   public static final int ERROR_GROUPS_GET = -2;

 //   public static final int OK_LOADS_GET = 2;
//    public static final int ERROR_LOADS_GET = -3;

    public static ArrayList<String> groups = new ArrayList<>();
    public static ArrayList<String> auds = new ArrayList<>();
    public static ArrayList<String> prepods = new ArrayList<>();
    //public static ArrayList<DataFill> filler = new ArrayList<DataFill>();
    public static SortedMap<Integer, Lesson> filler = new TreeMap<>();

    public static boolean isDebugMode = false;
    public static String error_stack;
    public static String SAVED_GROUP;
    public static int ACTIVED_TAB = 0;
    public static int PACTIVED_TAB = 0;
    public static void insertDataFill(DataFill df) {
        Lesson less;
        if(filler.containsKey(df.TIME_ID)) {
            less = filler.get(df.TIME_ID);
        }else{
           less = new Lesson();
           filler.put(df.TIME_ID, less);
        }
        less.dataz.add(df);
    }
    public static ArrayList<Lesson> getLessonsByPeriod(DateRange dr) {
        ArrayList<Lesson> tlessons = new ArrayList<>();
        int START_ID = Integer.valueOf(dr.BEGIN_YEAR+ TimeUtils.getNormalNumber(dr.BEGIN_MONTH)+TimeUtils.getNormalNumber(dr.BEGIN_DAY));
        int END_ID = Integer.valueOf(dr.END_YEAR+ TimeUtils.getNormalNumber(dr.END_MONTH)+TimeUtils.getNormalNumber(dr.END_DAY));
        Map<Integer, Lesson> submap = filler.subMap(START_ID, END_ID+1);
        tlessons.addAll(submap.values());
        return tlessons;
    }
}
