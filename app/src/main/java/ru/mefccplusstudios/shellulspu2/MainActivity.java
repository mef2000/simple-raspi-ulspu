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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import abs.parts.Bus;
import abs.parts.Day;
import abs.parts.Style;
import abs.parts.Time;
import abs.Data;
import abs.parts.interfaces.Eventable;
import arch.main.DataFill;
import pipes.core.Downloader;
import pipes.core.Fluid;
import pipes.core.Holder;
import pipes.core.Pipe;

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

    private final Holder piman = new Holder();
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
        get.connect("onError", onerror);
        get.connect("onUpdateList", onUpdateList);
        get.connect("onParser", onParser);
    }
    private final Pipe onParser = new Pipe("RASPIMAN", Bus.RUN_UI_THREAD) {

        @Override  public Fluid work(Fluid in) {
            content.removeAllViews();
            group.setEnabled(true);
            update.setEnabled(true);
            DateRange dr = Bus.time.getSavedWeek();
            ArrayList<ArrayList<DataFill>> lessa = Bus.data.getLessonsByPeriod(dr);
            if(lessa.size()>0) {
                //content filling
                for(int q=0; q<lessa.size(); q++) {
                    Day day = new Day(MainActivity.this);
                    Collections.sort(lessa.get(q));
                    day.setLessons(lessa.get(q));
                    content.addView(day);
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
        @Override public void connect(String trigger, Pipe connect) {}
        @Override public void stop() {}
    };
    private final Pipe onUpdateList = new Pipe("UPDLMAN", Bus.RUN_UI_THREAD) {
        @Override public Fluid work(Fluid in) {
            if("groups".equals(in.TRANSFER)) gpd.checkReady(0);
            else if("teachers".equals(in.TRANSFER)) gpd.checkReady(1);
            else if("rooms".equals(in.TRANSFER)) gpd.checkReady(2);
            return null;
        }
        @Override public void connect(String trigger, Pipe connect) {}
        @Override public void stop() {}
    };
    private final Pipe onerror = new Pipe("ERRMAN", Bus.RUN_UI_THREAD) {
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
        @Override public void connect(String trigger, Pipe connect) {}
        @Override public void stop() {}
    };
    private final Downloader get = new Downloader();
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

            f.PIPE = get;
            f.TRANSFER = Bus.data.SEARCH;
            f.EVENT = Bus.data.getListMode(Bus.data.FOCUS_TAB);
            f.CHAIN = f;
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
        String month = Bus.time.getMonthName(Bus.data.SAVED_MONTH);
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
        } else if(Bus.COLORS_CHANGED.equals(tag)) {
            loadtv.setTextColor(Bus.style.DISABLED_FONT_COLOR);
            rootp.setBackgroundColor(Bus.style.BACKGROUND_COLOR);
            rootl.getBackground().setColorFilter(Bus.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);
            loadpb.getIndeterminateDrawable().setColorFilter(Bus.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);

            wpd.event(tag, packet);
            errdial.event(tag, packet);
            set.event(tag, packet);
            gpd.event(tag, packet);
        }else if("RUN_IN_UI".equals(tag)) this.runOnUiThread((Runnable) packet);
        else if("LOAD_LIST".equals(tag)) {
            f.CHAIN = f;
            f.TRANSFER = packet;
            f.EVENT = "onLoadList";
            f.PIPE = get;
            piman.open(f);
        }
    }
}
