package ru.mefccplusstudios.shellulspu2;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import abs.parts.Bus;
import abs.parts.Day;
import abs.parts.Style;
import abs.parts.Time;
import abs.Data;
import abs.parts.interfaces.Eventable;
import arch.main.DataFill;

import pipes.core.Fluid;
import pipes.core.Pipe;
import pipes.core.Pipes;

public class MainActivity extends Activity implements Eventable {
    private Style style; //= new Style(this);
    private Time time; //= new Time(this);
    private Data data;// = new Data(this);

    private WeekDialog wpd;
    private GroupPickerDialog gpd;
    private SettingsDialog set;
    private ErrorDialog errdial;
    private TextClock tc;
    private Button group;
    private LinearLayout lload, content, rootp, rootl, weekp;
    private TextView daytv, weektv, nweek, monthtv, weekperiod, loadtv;
    private ProgressBar loadpb;
    private ScrollView scrollz;
    private ImageButton update, settings;

    private final Pipes piman = new Pipes();
    private final ArrayList<Day> days = new ArrayList<>();
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        style = new Style(this);
        time = new Time(this);
        data = new Data(this);
        Bus.connect(style, time, data, this);

        scrollz = findViewById(R.id.ScrollZ);
        lload = findViewById(R.id.llLoad);
        loadpb = findViewById(R.id.Progressable);
        loadtv = findViewById(R.id.textStatus);
        tc = findViewById(R.id.clock_id);
        update = findViewById(R.id.UPDATE);
        daytv = findViewById(R.id.dayTV);
        weektv = findViewById(R.id.weekTV);
        weekp = findViewById(R.id.beginDataPicker);
        nweek = findViewById(R.id.numberWeek);
        monthtv = findViewById(R.id.monthTV);
        weekperiod = findViewById(R.id.weekPeriodTV);
        group = findViewById(R.id.BtnGroup);
        content = findViewById(R.id.Parceable);
        rootp = findViewById(R.id.rootp);
        rootl = findViewById(R.id.rootrela);
        settings = findViewById(R.id.Settings);

        wpd = new WeekDialog(this);
        gpd = new GroupPickerDialog(this);
        set = new SettingsDialog(this);
        errdial = new ErrorDialog(this);

        weekp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                wpd.show();
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                gpd.show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)  { set.show(); }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                view.setEnabled(false); updateData(view);
            }
        });
        onGet.connect(onError);
        onGet.connect(onUpdateList);
        onGet.connect(onParser);
    }

    private final Pipe onGet = new Pipe("GET", Bus.ANY_BACK_THREAD) {
        private final HashMap<String, Pipe> connects = new HashMap<>();
        private Pipe next = null;
        @Override public void next(Pipe next) { this.next = next; }
        @Override public Pipe manager(String event) { return connects.get(event); }
        @Override public Fluid work(Fluid in) {
            String JSON="";
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            Fluid chain = new Fluid();
            try {
                URL url = null;
                if ("get_list".equals(in.EVENT))
                    url = new URL("https://raspi.ulspu.ru/json/dashboard/" + in.TRANSFER);
                else
                    url = new URL("https://raspi.ulspu.ru/json/dashboard/events?mode=" + in.EVENT + "&value=" + in.TRANSFER);

                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                JSON = buffer.toString();
                if (connection != null) connection.disconnect();
                if (reader != null) reader.close();

                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if (status.compareTo("ok") != 0) throw new JSONException("BAD_ANSWER_FROM_SERVER");

                if ("get_list".equals(in.EVENT)) {
                    JSONArray values = (JSONArray) root.get("rows");
                    if ("rooms".equals(in.TRANSFER)) {
                        Bus.data.ROOMS.clear();
                        for (int q = 0; q < values.length(); q++)
                            Bus.data.ROOMS.add(values.get(q).toString());
                    } else if ("groups".equals(in.TRANSFER)) {
                        Bus.data.GROUPS.clear();
                        for (int q = 0; q < values.length(); q++)
                            Bus.data.GROUPS.add(values.get(q).toString());
                    } else if ("teachers".equals(in.TRANSFER)) {
                        Bus.data.TEACHERS.clear();
                        for (int q = 0; q < values.length(); q++)
                            Bus.data.TEACHERS.add(values.get(q).toString());
                    }
                    //update lists
                    chain.TRANSFER = in.TRANSFER;
                    chain.EVENT = "onUpdateList";
                    chain.PIPE = manager(chain.EVENT);
                    return chain;
                }else {
                    JSONArray values = (JSONArray) root.get("data");
                    Bus.data.filler.clear();
                    Calendar cd = Calendar.getInstance();
                    for(int q=0; q<values.length(); q++) {
                        JSONObject joba = values.getJSONObject(q);
                        String title = joba.getString("title");
                        String start = joba.getString("start");
                        String end = joba.getString("end");
                        cd.setTime(Bus.time.sdf.parse(start.substring(0, start.length()-5)));
                        cd.add(Calendar.HOUR_OF_DAY, Bus.time.HOURS_STEP);
                        DataFill df = new DataFill();
                        df.CONTEXT = title;
                        df.YEAR = cd.get(Calendar.YEAR);
                        df.MONTH = cd.get(Calendar.MONTH);
                        df.DAY = cd.get(Calendar.DAY_OF_MONTH);
                        df.START_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                        df.START_MINS = cd.get(Calendar.MINUTE);
                        cd.setTime(Bus.time.sdf.parse(end.substring(0, end.length()-5)));
                        cd.add(Calendar.HOUR_OF_DAY, Bus.time.HOURS_STEP);
                        df.END_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                        df.END_MINS = cd.get(Calendar.MINUTE);
                        String TIME_ID = ""+df.YEAR;
                        if(df.MONTH<10) TIME_ID = TIME_ID+"0";
                        TIME_ID = TIME_ID+df.MONTH;

                        if(df.DAY<10) TIME_ID = TIME_ID+"0";
                        TIME_ID = TIME_ID+df.DAY;

                        df.TIME_ID = Integer.valueOf(TIME_ID);
                        Bus.data.addDataFill(df);
                    }
                    //render shedule
                    chain.TRANSFER = null;
                    chain.EVENT = "onParser";
                    chain.PIPE = manager(chain.EVENT);
                    return chain;
                }
            }catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                chain.TRANSFER = sw.toString();
                chain.EVENT = "onError";
                chain.PIPE = manager(chain.EVENT);
                return chain;
            }
        }

        @Override public void connect(Pipe connect) { connects.put(connect.PID, connect); }
        @Override public void stop(boolean isStop) {}
    };

    private final Pipe onParser = new Pipe("onParser", Bus.RUN_UI_THREAD) {
        @Override public void next(Pipe next) {}
        @Override public Pipe manager(String event) { return null;}

        @Override  public Fluid work(Fluid in) {
            content.removeAllViews();
            group.setEnabled(true);
            update.setEnabled(true);
            ArrayList<ArrayList<DataFill>> lessa = Bus.data.getLessonsByPeriod();
            if(lessa.size()>0) {
                //content filling
                for(int q=0; q<lessa.size(); q++) {
                    Day day = new Day(MainActivity.this);
                    Collections.sort(lessa.get(q));
                    day.setLessons(lessa.get(q));
                    content.addView(day);
                    days.add(day);
                }
                lload.setVisibility(View.GONE);
                scrollz.setVisibility(View.VISIBLE);
            }else{
                scrollz.setVisibility(View.GONE);
                lload.setVisibility(View.VISIBLE);
                loadpb.setVisibility(View.GONE);
                loadtv.setText("На выбранный период для текущих параметров нет данных\n(попробуйте обновить расписание)");
            }
            return null;
        }
        @Override public void connect(Pipe connect) {}
        @Override public void stop(boolean isStop) {}
    };
    private final Pipe onUpdateList = new Pipe("onUpdateList", Bus.RUN_UI_THREAD) {
        @Override public void next(Pipe next) {}
        @Override public Pipe manager(String event) { return null; }
        @Override public Fluid work(Fluid in) {
            if("groups".equals(in.TRANSFER)) gpd.checkReady(0);
            else if("teachers".equals(in.TRANSFER)) gpd.checkReady(1);
            else if("rooms".equals(in.TRANSFER)) gpd.checkReady(2);
            return null;
        }
        @Override public void connect(Pipe connect) {}
        @Override public void stop(boolean isStop) {}
    };
    private final Pipe onError = new Pipe("onError", Bus.RUN_UI_THREAD) {
        @Override public void next(Pipe next) {}
        @Override public Pipe manager(String event) { return null; }

        @Override public Fluid work(Fluid in) {
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            update.setEnabled(true);
            group.setEnabled(true);
            loadtv.setText("Упс! Кажется произошла ошибка. Попробуйте ещё раз, но позже");
            if(Bus.data.isDebugMode) errdial.show((String)in.TRANSFER);
            return null;
        }
        @Override public void connect(Pipe connect) {}
        @Override public void stop(boolean isStop) {}
    };

    private final  Fluid f = new Fluid();
    public void updateData(View lock) {
        if(Bus.data.SEARCH.compareTo("null")==0) {
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            group.setText(this.getString(R.string.group));
            loadtv.setText(this.getString(R.string.letstart));
            lock.setEnabled(true);
        }else {
            group.setEnabled(false);
            update.setEnabled(false);
            group.setText(Bus.data.SEARCH);
            scrollz.setVisibility(View.GONE);
            content.removeAllViews();
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.VISIBLE);
            loadtv.setText("Получение расписания...");

            System.out.println("Trying BOOT "+Bus.data.SEARCH);
            days.clear();
            f.PIPE = onGet;
            f.TRANSFER = Bus.data.SEARCH;
            f.EVENT = Bus.data.getListMode(Bus.data.FOCUS_TAB);
            //f.CHAIN = f;
            piman.open(f);
        }
    }
    @Override public void onStart() {
        super.onStart();
        Bus.connect(style, time, data, this);
        updateData(update);
    }

    @Override public void onResume() {
        super.onResume();
        tc.setFormat12Hour(null);
        tc.setFormat24Hour("HH:mm");

        weektv.setText(Bus.time.getDayName(Bus.time.TOTAL_YEAR, Bus.time.TOTAL_MONTH, Bus.time.TOTAL_DAY).toLowerCase());
        daytv.setText(Bus.time.packWithSlash(Bus.time.TOTAL_YEAR, Bus.time.TOTAL_MONTH, Bus.time.TOTAL_DAY));

        updateUI();
    }

    public void updateUI() {
        String month = Bus.time.getMonthName(Bus.data.MONTH_SBEGIN);
        nweek.setText(month.substring(0,1).toUpperCase());
        monthtv.setText(month.substring(1, month.length()).toLowerCase());
        weekperiod.setText(Bus.time.getSavedWeek().toString());
    }

    @Override public void onPause() {
        super.onPause();
        Bus.data.saveSettings();
    }
    @Override public void onStop() {
        Bus.disconnect();
        super.onStop();
    }

    @Override public void event(String tag, Object packet) {
        if(Bus.FONTS_CHANGED.equals(tag)) {
            loadtv.setTextColor(Bus.style.DISABLED_FONT_COLOR);
            loadtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            monthtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            weekperiod.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            nweek.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(2.5f*Bus.style.FONT_SIZE_SP));
            group.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            tc.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(2.5f*Bus.style.FONT_SIZE_SP));
            daytv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            weektv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);

            wpd.event(tag, packet);
            errdial.event(tag, packet);
            set.event(tag, packet);
            gpd.event(tag, packet);
            for(Day d: days) d.event(tag, packet);
        } else if(Bus.COLORS_CHANGED.equals(tag)) {
            loadtv.setTextColor(Bus.style.DISABLED_FONT_COLOR);
            rootp.setBackgroundColor(Bus.style.BACKGROUND_COLOR);
            rootl.getBackground().setColorFilter(Bus.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);
            loadpb.getIndeterminateDrawable().setColorFilter(Bus.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);

            wpd.event(tag, packet);
            errdial.event(tag, packet);
            set.event(tag, packet);
            gpd.event(tag, packet);
            for(Day d: days) d.event(tag, packet);
        }else if("RUN_IN_UI".equals(tag)) {
            this.runOnUiThread((Runnable) packet);
        }else if("LOAD_LIST".equals(tag)) {
            //f.CHAIN = f;
            f.TRANSFER = packet;
            f.EVENT = "get_list";
            f.PIPE = onGet;
            piman.open(f);
        }else if("FOCUS_TO_MONTH".equals(tag)) {
            updateUI();
            updateData(update);
        }else if("LOAD_FOR_DATA".equals(tag)) {
            updateUI();
            updateData(update);
        }
    }
}
