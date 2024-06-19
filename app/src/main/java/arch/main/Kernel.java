package arch.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import ru.mefccplusstudios.shellulspu2.DateRange;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class Kernel {
    protected final Kernel kernel = this;
    protected MainActivity context;
    public String SAVED_PARAM = "nullable";
    public String ERROR_MSG;

    public int SAVED_YEAR, SAVED_MONTH, SAVED_WEEK,
            FOCUS_TAB, ACTIVED_TAB, FOCUS_YEAR, FOCUS_MONTH;
    public boolean isDebugMode = false;
    public SharedPreferences settings = null;
    public Handler bus = new Handler() {
        @Override public void handleMessage(Message msg) {
            RuntimeEvent re = (RuntimeEvent) msg.obj;
            if(re!=null) re.postWorkMainUI();
        }
    };
    public ArrayList<String> groups = new ArrayList<>();
    public ArrayList<String> auds = new ArrayList<>();
    public ArrayList<String> prepods = new ArrayList<>();

    private final int HOURS_STEP = 4;
    public void init(MainActivity context) {
        this.context = context;
        settings = context.getSharedPreferences("kon_games_global", MODE_PRIVATE);
    }
    public void loadSettings() {
        this.isDebugMode = settings.getBoolean("debug_mode", false);
        this.SAVED_PARAM = settings.getString("group", "nullable");
        this.SAVED_YEAR = settings.getInt("syear", kernel.time.TOTAL_YEAR);
        this.SAVED_MONTH= settings.getInt("smonth", kernel.time.TOTAL_MONTH);
        this.SAVED_WEEK= settings.getInt("sweek", 0);
        this.FOCUS_TAB = settings.getInt("atab", 0);
        this.style.THEME_PARADIGM = settings.getInt("themer", 0);
        this.style.loadStyle(this.style.THEME_PARADIGM);
        this.style.FONT_SIZE_SP = settings.getInt("fsize", 20);
        this.ACTIVED_TAB = FOCUS_TAB;
        this.style.loadStyle(this.style.THEME_PARADIGM);
    }
    public void saveSettings() {
        SharedPreferences.Editor setedit = settings.edit();
        setedit.putInt("syear", this.SAVED_YEAR);
        setedit.putInt("smonth", this.SAVED_MONTH);
        setedit.putInt("sweek", this.SAVED_WEEK);
        setedit.putInt("atab", this.FOCUS_TAB);
        setedit.putBoolean("debug_mode", this.isDebugMode);
        setedit.putString("group", this.SAVED_PARAM);
        setedit.putInt("themer", this.style.THEME_PARADIGM);
        setedit.putInt("fsize", this.style.FONT_SIZE_SP);
        setedit.commit();
    }
    public Time time = new Time();
    public AsyncJob async = new AsyncJob();
    public Data data = new Data();
    public Style style = new Style();

    public class Time {
        private Calendar dyncd = Calendar.getInstance();
        public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        public int TOTAL_YEAR, TOTAL_MONTH, TOTAL_DAY, TOTAL_HOURS, TOTAL_MINS, TOTAL_SECS;
        public final int HOURS_STEP = 4;
        public final ArrayList<DateRange> drangers = new ArrayList<>();
        public void init() {
            Calendar finalcd = Calendar.getInstance();
            TOTAL_YEAR = finalcd.get(Calendar.YEAR);
            TOTAL_MONTH = finalcd.get(Calendar.MONTH);
            TOTAL_DAY = finalcd.get(Calendar.DAY_OF_MONTH);
            TOTAL_HOURS = finalcd.get(Calendar.HOUR_OF_DAY);
            TOTAL_MINS = finalcd.get(Calendar.MINUTE);
            TOTAL_SECS = finalcd.get(Calendar.SECOND);
            FOCUS_YEAR = TOTAL_YEAR; FOCUS_MONTH = TOTAL_MONTH;
        }
        public String getNamedDayOfWeekBy(int year, int month, int day) {
            if(dyncd==null) return "TU_ERROR";
            String nday = "NOT_ATTRIBUTE";
            dyncd.set(year, month, day);
            switch(dyncd.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    nday = "Понедельник";
                    break;
                case Calendar.TUESDAY:
                    nday = "Вторник";
                    break;
                case Calendar.WEDNESDAY:
                    nday = "Среда";
                    break;
                case Calendar.THURSDAY:
                    nday = "Четверг";
                    break;
                case Calendar.FRIDAY:
                    nday = "Пятница";
                    break;
                case Calendar.SATURDAY:
                    nday = "Суббота";
                    break;
                case Calendar.SUNDAY:
                    nday = "Воскресенье";
                    break;
            }
            return nday;
        }
        public String getStyledData(int year, int month, int day) {
            String sa = getNormalNumber(day);
            sa = sa+"/"+getNormalNumber(month);
            sa = sa+"/"+year;
            return sa;
        }
        public String getNormalNumber(int number) {
            if(number<10) return "0"+number;
            else return  ""+number;
        }
        public String getNamedMonth(int month) {
            String data = "N/A";
            switch(month) {
                case Calendar.JANUARY: data = "Январь"; break;
                case Calendar.FEBRUARY: data = "Февраль"; break;
                case Calendar.MARCH: data = "Март"; break;
                case Calendar.MAY: data = "Май"; break;
                case Calendar.APRIL: data = "Апрель"; break;
                case Calendar.JUNE: data = "Июнь"; break;
                case Calendar.JULY: data = "Июль"; break;
                case Calendar.SEPTEMBER: data = "Сентябрь"; break;
                case Calendar.OCTOBER: data = "Октябрь"; break;
                case Calendar.NOVEMBER: data = "Ноябрь"; break;
                case Calendar.DECEMBER: data = "Декабрь"; break;
                case Calendar.AUGUST: data = "Август"; break;
            }
            return data;
        }
        public void pullDateRangers(int MONTH, int YEAR) {
            drangers.clear();
            // dyncd.clear();
            dyncd.set(YEAR, MONTH, 1);
            int startday = dyncd.get(Calendar.DAY_OF_WEEK);
            if(startday==Calendar.SUNDAY) startday = 6;
            else startday -= 2;
            dyncd.add(Calendar.DATE, -startday);
            int PREVIOSLY_MONTH = MONTH;
            while(true) {
                DateRange dr = new DateRange();
                dr.BEGIN_DAY = dyncd.get(Calendar.DAY_OF_MONTH);
                dr.BEGIN_MONTH = dyncd.get(Calendar.MONTH);
                dr.BEGIN_YEAR = dyncd.get(Calendar.YEAR);
                dyncd.add(Calendar.DATE, 6);
                dr.END_DAY = dyncd.get(Calendar.DAY_OF_MONTH);
                dr.END_MONTH = dyncd.get(Calendar.MONTH);
                dr.END_YEAR = dyncd.get(Calendar.YEAR);
                dyncd.add(Calendar.DATE, 1);
                drangers.add(dr);
                if(dr.END_MONTH!=PREVIOSLY_MONTH) break;
                PREVIOSLY_MONTH = dr.END_MONTH;
            }
        }
        public String buildFromSaved() {
            if(drangers.size()>kernel.SAVED_WEEK) {
                DateRange dr = kernel.time.drangers.get(SAVED_WEEK);
                return getNormalNumber(dr.BEGIN_DAY)+"."+getNormalNumber(dr.BEGIN_MONTH+1)
                        +" - "+getNormalNumber(dr.END_DAY)+"."+getNormalNumber(dr.END_MONTH+1);

            }else return "internal_error";
        }
        public String buildFromScratch(int position) {
            if(drangers.size()>position) {
                DateRange dr = drangers.get(position);
                return getNormalNumber(dr.BEGIN_DAY)+"."+getNormalNumber(dr.BEGIN_MONTH+1)
                        +" - "+getNormalNumber(dr.END_DAY)+"."+getNormalNumber(dr.END_MONTH+1);
            }else return "internal_error";
        }
        public void getYMApplyBy(int month_amount) {
            if(FOCUS_YEAR == 0) { FOCUS_YEAR = TOTAL_YEAR; FOCUS_MONTH = TOTAL_MONTH;}
            dyncd.set(FOCUS_YEAR, FOCUS_MONTH, 1);
            dyncd.add(Calendar.MONTH, month_amount);
            FOCUS_YEAR = dyncd.get(Calendar.YEAR);
            FOCUS_MONTH = dyncd.get(Calendar.MONTH);
        }
    }
    public class AsyncJob {
        private RuntimeEvent onErrorAction;
        private ConcurrentHashMap<String, RuntimeEvent> pool = new ConcurrentHashMap<>();

        public void init(RuntimeEvent onErrorAction) {
            this.onErrorAction = onErrorAction;
        }
        public void startAsync(RuntimeEvent rte, String ID_EVENT) {
            if(pool.containsKey(ID_EVENT)) {
                RuntimeEvent dead = pool.get(ID_EVENT);
                try {
                    System.out.println("MUST_BE_KILL_ASYNC: "+ID_EVENT);
                    dead.kill();
                }catch(Exception e) { e.printStackTrace();}
            }
            if(rte!=null) {
                rte.preWorkMainUI();
                Thread live = new Thread(new Runnable() {
                    Message msg = new Message();
                    @Override
                    public void run() {
                        try {
                            rte.asyncWork();
                            msg.obj = rte;
                        }catch(Exception e) {
                            //e.printStackTrace();
                            msg.obj = onErrorAction;
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            kernel.ERROR_MSG = sw.toString();
                        }
                        kernel.bus.sendMessage(msg);
                    }
                });
                pool.put(ID_EVENT, rte);
                live.start();
            }else System.out.println("ASYNC_WRONG_RT_EVENT: null --> skipping.");
        }

    }

    public class Data {
        public SortedMap<Integer, Lesson> filler = new TreeMap<>();
        public void insertDataFill(DataFill df) {
            Lesson less;
            if(filler.containsKey(df.TIME_ID)) {
                less = filler.get(df.TIME_ID);
            }else{
                less = new Lesson();
                filler.put(df.TIME_ID, less);
            }
            less.dataz.add(df);
        }
        public ArrayList<Lesson> getLessonsByPeriod(DateRange dr) {
            ArrayList<Lesson> tlessons = new ArrayList<>();
            int START_ID = Integer.valueOf(dr.BEGIN_YEAR+ kernel.time.getNormalNumber(dr.BEGIN_MONTH)+kernel.time.getNormalNumber(dr.BEGIN_DAY));
            int END_ID = Integer.valueOf(dr.END_YEAR+ kernel.time.getNormalNumber(dr.END_MONTH)+kernel.time.getNormalNumber(dr.END_DAY));
            Map<Integer, Lesson> submap = filler.subMap(START_ID, END_ID+1);
            tlessons.addAll(submap.values());
            return tlessons;
        }
    }
    public class Style {
        public int THEME_PARADIGM;
        public int BACKGROUND_COLOR;
        public int DIALOG_COLOR;
        public int DIALOG_HEADER_COLOR;
        public int FIELD_COLOR;
        public int MAIN_FONT_COLOR;
        public int DISABLED_FONT_COLOR;
        public int FOREGROUND_FONT_COLOR;
        public int SEEKBAR_COLOR;
        public int FONT_SIZE_SP;

        public void loadStyle(int stl) {
            switch(stl) {
                case 0:
                    BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                    FIELD_COLOR = context.getResources().getColor(R.color.orange);
                    SEEKBAR_COLOR = context.getResources().getColor(R.color.orange);
                    FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                    MAIN_FONT_COLOR = context.getResources().getColor(R.color.black);
                    DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                    DIALOG_COLOR = context.getResources().getColor(R.color.white);
                    DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.orange);
                    break;
                case 1:
                    SEEKBAR_COLOR = context.getResources().getColor(R.color.phiol);
                    BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                    FIELD_COLOR = context.getResources().getColor(R.color.phiol);
                    FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                    MAIN_FONT_COLOR = context.getResources().getColor(R.color.black);
                    DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                    DIALOG_COLOR = context.getResources().getColor(R.color.white);
                    DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.phiol);
                    break;
                case 2:
                    SEEKBAR_COLOR = context.getResources().getColor(R.color.grey_ultra);
                    BACKGROUND_COLOR = context.getResources().getColor(R.color.grey);
                    FIELD_COLOR = context.getResources().getColor(R.color.grey_light);
                    FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                    MAIN_FONT_COLOR = context.getResources().getColor(R.color.white);
                    DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_ultra);
                    DIALOG_COLOR = context.getResources().getColor(R.color.grey_light);
                    DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.white);
                    break;
            }
        }
    }
}
