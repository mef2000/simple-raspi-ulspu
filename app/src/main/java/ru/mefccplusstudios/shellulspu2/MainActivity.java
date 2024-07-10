package ru.mefccplusstudios.shellulspu2;

import android.app.Activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
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

import javax.net.ssl.HttpsURLConnection;

import abs.core.Bus;
import abs.core.Data;
import abs.core.DataFill;
import abs.core.Day;
import abs.core.Eventable;
import abs.core.Style;
import abs.core.Time;

public class MainActivity extends Activity implements Eventable {
    private Style style;
    private Time time;
    private Data data;

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
    private final ArrayList<Day> days = new ArrayList<>();

    public MainActivity() {
    }

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
        settings.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view)  { set.show(); }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                gpd.show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                view.setEnabled(false);
                updateList();
                updateShedule();
            }
        });
    }
    private final Runnable load_list = new Runnable() {
        @Override public void run() {
            try{
                Bus.data.GROUPS.clear();
                Bus.data.TEACHERS.clear();
                Bus.data.ROOMS.clear();
                parseArray(getFile("https://raspi.ulspu.ru/json/dashboard/groups"), Bus.data.GROUPS);
                parseArray(getFile("https://raspi.ulspu.ru/json/dashboard/teachers"), Bus.data.TEACHERS);
                parseArray(getFile("https://raspi.ulspu.ru/json/dashboard/rooms"), Bus.data.ROOMS);
                runOnUiThread(update_list);
            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                ERROR_MSG = sw.toString();
                runOnUiThread(error);
            }
        }
    };
    private final Runnable update_sh = new Runnable() {
        @Override public void run() {
            try{
                Bus.data.filler.clear();
                JSONObject root = new JSONObject(
                        getFile("https://raspi.ulspu.ru/json/dashboard/events?mode=" +Bus.data.getListMode(Bus.data.FOCUS_TAB)+ "&value=" +Bus.data.SEARCH));
                String status = root.get("status").toString();
                if (status.compareTo("ok") != 0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("data");
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
                runOnUiThread(make_sh);
            }catch(Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                ERROR_MSG = sw.toString();
                runOnUiThread(error);
            }
        }
    };
    private String ERROR_MSG;
    private final Runnable update_list = new Runnable() { @Override public void run() { gpd.updateState(); }};
    private final Runnable make_sh = new Runnable() { @Override public void run() { makeShedule(); }};
    private final Runnable error = new Runnable() { @Override public void run() {
        scrollz.setVisibility(View.GONE);
        lload.setVisibility(View.VISIBLE);
        loadpb.setVisibility(View.GONE);
        update.setEnabled(true);
        group.setEnabled(true);
        loadtv.setText("Упс! Кажется произошла ошибка. Попробуйте ещё раз, но позже");
        if(Bus.data.isDebugMode) errdial.show(ERROR_MSG);
    }};
    public void parseArray(String JSON, ArrayList<String> fill) throws Exception {
        JSONObject root = new JSONObject(JSON);
        String status = root.get("status").toString();
        if (status.compareTo("ok") != 0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
        JSONArray values = (JSONArray) root.get("rows");
        for (int q = 0; q < values.length(); q++) {
            String id = values.get(q).toString();
            if(!fill.contains(id)) fill.add(id);
        }
    }
    public String getFile(String link) throws Exception {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        URL url = new URL(link);
        connection = (HttpsURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        if (connection != null) connection.disconnect();
        if (reader != null) reader.close();
        return buffer.toString();
    }
    public void updateUI() {
        String month = Bus.time.getMonthName(Bus.data.MONTH_SBEGIN);
        nweek.setText(month.substring(0,1).toUpperCase());
        monthtv.setText(month.substring(1, month.length()).toLowerCase());
        weekperiod.setText(Bus.time.getSavedWeek().toString());
    }
    public void makeShedule() {
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
    }
    public void updateList() { new Thread(load_list).start(); }
    public void updateShedule() {
        if(Bus.data.SEARCH.compareTo("null")==0) {
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            group.setText(this.getString(R.string.group));
            loadtv.setText(this.getString(R.string.letstart));
            update.setEnabled(true);
        }else {
            group.setEnabled(false);
            update.setEnabled(false);
            group.setText(Bus.data.SEARCH);
            scrollz.setVisibility(View.GONE);
            content.removeAllViews();
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.VISIBLE);
            loadtv.setText("Получение расписания...");
            System.out.println("Trying BOOT " + Bus.data.SEARCH);
            days.clear();
            new Thread(update_sh).start();
        }
    }
    @Override public void onStart() {
        super.onStart();
        Bus.connect(style, time, data, this);
        Bus.event(Bus.COLORS_CHANGED, null);
        Bus.event(Bus.FONTS_CHANGED, null);
        Bus.event("ON_CHANGE_CONFIG", null);
        updateList();
        updateShedule();
    }

    @Override public void onResume() {
        super.onResume();
        tc.setFormat12Hour(null);
        tc.setFormat24Hour("HH:mm");
        weektv.setText(Bus.time.getDayName(Bus.time.TOTAL_YEAR, Bus.time.TOTAL_MONTH, Bus.time.TOTAL_DAY).toLowerCase());
        daytv.setText(Bus.time.packWithSlash(Bus.time.TOTAL_YEAR, Bus.time.TOTAL_MONTH, Bus.time.TOTAL_DAY));
        updateUI();
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
            loadtv.setTextColor(Bus.style.SUBFONT_COLOR);
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
            loadtv.setTextColor(Bus.style.SUBFONT_COLOR);
            rootp.setBackgroundColor(Bus.style.BACKGROUND_COLOR);
            rootl.getBackground().setColorFilter(Bus.style.MAIN_COLOR, PorterDuff.Mode.SRC_ATOP);
            loadpb.getIndeterminateDrawable().setColorFilter(Bus.style.MAIN_COLOR, PorterDuff.Mode.SRC_ATOP);
            wpd.event(tag, packet);
            errdial.event(tag, packet);
            set.event(tag, packet);
            gpd.event(tag, packet);
            getWindow().getDecorView().setBackgroundColor(Bus.style.MAIN_COLOR);
            for(Day d: days) d.event(tag, packet);
        }else if("LOAD_FOR_DATA".equals(tag)) updateShedule();
        else if("FOCUS_TO_MONTH".equals(tag)) {
            updateUI();
            makeShedule();
        } else if("ON_CHANGE_CONFIG".equals(tag)) {
            if(Bus.data.isBarSupport) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}

