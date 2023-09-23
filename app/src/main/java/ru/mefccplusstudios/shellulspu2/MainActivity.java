package ru.mefccplusstudios.shellulspu2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

import arch.main.Async;
import arch.main.Data;
import arch.main.DataFill;
import arch.main.Lesson;
import arch.main.RuntimeEvent;
import arch.views.DayBoard;

public class MainActivity extends Activity {
    private SharedPreferences sp;

    private TextClock tc;
    private ImageButton update;
    private TextView daytv, weektv, nweek, monthtv, weekperiod;

    private ErrorDialog errdial;
    private WeekPickerDialog wpd;
    private GroupPickerDialog gpd;
    private LinearLayout weekp;

    private Button group;
    public Handler uiManager;


    private RuntimeEvent rerror;

    private ScrollView scrollz;
    private ProgressBar loadpb;
    private TextView loadtv;
    private LinearLayout lload, content;

    private RuntimeEvent load_groups, load_prepod, load_aud, levent_group;
    //private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       wpd = new WeekPickerDialog(this);
        gpd = new GroupPickerDialog(this);
        errdial = new ErrorDialog(this);
        sp = getSharedPreferences("kon_games_global", MODE_PRIVATE);
        setContentView(R.layout.main_layout);

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
        weekp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               wpd.show();// dpg.show();
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpd.show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildRaspiByGroups();
            }
        });
        uiManager = new Handler() {
            @Override public void handleMessage(Message msg) {
                RuntimeEvent re = (RuntimeEvent) msg.obj;
                if(re!=null) re.postWorkMainUI();
            }
        };

        rerror = new RuntimeEvent() {
            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                scrollz.setVisibility(View.GONE);
                lload.setVisibility(View.VISIBLE);
                loadpb.setVisibility(View.GONE);
                update.setEnabled(true);
                group.setEnabled(true);
                loadtv.setText("Упс! Кажется произошла ошибка. Попробуйте ещё раз, но позже");
                if(Data.isDebugMode) errdial.show();
            }
        };
        createRuntimes();
        //loadGroupsList();
       // loadAudsList();
        //tc.set
    }
    @Override public void onStart() {
        super.onStart();
        Data.isDebugMode = sp.getBoolean("debug_mode", false);
        Data.SAVED_GROUP = sp.getString("group", "nullable");
        Data.PACTIVED_TAB = sp.getInt("atab", 0);
        Data.ACTIVED_TAB = Data.PACTIVED_TAB;
        TimeUtils.init();
        TimeUtils.SAVED_YEAR = sp.getInt("syear", TimeUtils.TOTAL_YEAR);
        TimeUtils.SAVED_MONTH= sp.getInt("smonth", TimeUtils.TOTAL_MONTH);
        TimeUtils.SAVED_WEEK= sp.getInt("sweek", 0);
        TimeUtils.FOCUS_YEAR = TimeUtils.SAVED_YEAR;
        TimeUtils.FOCUS_MONTH = TimeUtils.SAVED_MONTH;
        TimeUtils.pullDateRangers(TimeUtils.SAVED_MONTH, TimeUtils.SAVED_YEAR);
        Async.init(uiManager, rerror);
        gpd.updateState();
        buildRaspiByGroups();
        //buildRaspiByGroups();
        //TimeUtils.pullDateRangers(TimeUtils.SAVED_MONTH, TimeUtils.SAVED_YEAR);
        //wpd.show();
    }

    @Override public void onResume() {
        super.onResume();
        tc.setFormat12Hour(null);
        tc.setFormat24Hour("HH:mm");
        weektv.setText(TimeUtils.getNamedCurrentDayOfWeek().toLowerCase());
        daytv.setText(TimeUtils.getCurrentData());
        updateUI();
        //nweek.setText(""+TimeUtils.getNumberWeekOfYear());
    }
    @Override public void onPause() {
        super.onPause();
        Data.groups.clear();
        Data.auds.clear();
        Data.filler.clear();
        Data.prepods.clear();
        SharedPreferences.Editor setedit = sp.edit();
        setedit.putInt("syear", TimeUtils.SAVED_YEAR);
        setedit.putInt("smonth", TimeUtils.SAVED_MONTH);
        setedit.putInt("sweek", TimeUtils.SAVED_WEEK);
        setedit.putInt("atab", Data.ACTIVED_TAB);
        setedit.putBoolean("debug_mode", Data.isDebugMode);
        setedit.putString("group", Data.SAVED_GROUP);
        setedit.commit();
    }
    public void updateUI() {
        String month = TimeUtils.getNamedMonth(TimeUtils.SAVED_MONTH);
        nweek.setText(month.substring(0,1).toUpperCase());
        monthtv.setText(month.substring(1, month.length()).toLowerCase());
        weekperiod.setText(TimeUtils.buildFromSaved());
       // buildRaspiByGroups();
        //System.out.println("RANGE_CHANGED");
    }
    public void createRuntimes() {
        load_aud = new RuntimeEvent() {
            @Override
            public void asyncWork() throws Exception {
                super.asyncWork();
                String JSON="";
                HttpsURLConnection connection = null;
                BufferedReader reader = null;
                URL url = new URL("https://raspi.ulspu.ru/json/dashboard/rooms");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                Data.auds.clear();
                for(int q=0; q<values.length(); q++) {
                    Data.auds.add(values.get(q).toString());
                }
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(2);//gpd.updateRooms();
                //lload.setVisibility(View.GONE);
               // scrollz.setVisibility(View.VISIBLE);
                //gpd.updateGroups();
            }

            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
               // scrollz.setVisibility(View.GONE);
               // lload.setVisibility(View.VISIBLE);
               // loadpb.setVisibility(View.VISIBLE);
               // loadtv.setText("Загружаем данные...");
            }
        };
        load_prepod = new RuntimeEvent() {
            @Override
            public void asyncWork() throws Exception {
                super.asyncWork();
                String JSON="";
                HttpsURLConnection connection = null;
                BufferedReader reader = null;
                URL url = new URL("https://raspi.ulspu.ru/json/dashboard/teachers");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                Data.prepods.clear();
                for(int q=0; q<values.length(); q++) {
                    Data.prepods.add(values.get(q).toString());
                }
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(1);
                //gpd.updatePrepods();
                //lload.setVisibility(View.GONE);
                // scrollz.setVisibility(View.VISIBLE);
                //gpd.updateGroups();
            }

            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
                // scrollz.setVisibility(View.GONE);
                // lload.setVisibility(View.VISIBLE);
                // loadpb.setVisibility(View.VISIBLE);
                // loadtv.setText("Загружаем данные...");
            }
        };
        levent_group = new RuntimeEvent() {
            @Override
            public void asyncWork() throws Exception {
                super.asyncWork();
                String JSON="";
                HttpsURLConnection connection = null;
                BufferedReader reader = null;

                String mode = "group";
                switch(Data.ACTIVED_TAB){
                    case 0: mode = "group"; break;
                    case 1: mode = "teacher"; break;
                    case 2: mode = "room"; break;
                }
                URL url = new URL("https://raspi.ulspu.ru/json/dashboard/events?mode="+mode+"&value="+Data.SAVED_GROUP);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("data");
                Data.filler.clear();
                Calendar cd = Calendar.getInstance();
                for(int q=0; q<values.length(); q++) {
                    JSONObject joba = values.getJSONObject(q);
                    String title = joba.getString("title");
                    String start = joba.getString("start");
                    String end = joba.getString("end");
                    cd.setTime(TimeUtils.sdf.parse(start.substring(0, start.length()-5)));
                    cd.add(Calendar.HOUR_OF_DAY, TimeUtils.HOURS_STEP);
                    DataFill df = new DataFill();
                    df.CONTEXT = title;
                    df.YEAR = cd.get(Calendar.YEAR);
                    df.MONTH = cd.get(Calendar.MONTH);
                    df.DAY = cd.get(Calendar.DAY_OF_MONTH);
                    df.START_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                    df.START_MINS = cd.get(Calendar.MINUTE);
                    cd.setTime(TimeUtils.sdf.parse(end.substring(0, end.length()-5)));
                    cd.add(Calendar.HOUR_OF_DAY, TimeUtils.HOURS_STEP);
                    df.END_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                    df.END_MINS = cd.get(Calendar.MINUTE);
                    String TIME_ID = ""+df.YEAR;
                    if(df.MONTH<10) TIME_ID = TIME_ID+"0";
                    TIME_ID = TIME_ID+df.MONTH;

                    if(df.DAY<10) TIME_ID = TIME_ID+"0";
                    TIME_ID = TIME_ID+df.DAY;

                    df.TIME_ID = Integer.valueOf(TIME_ID);
                    Data.insertDataFill(df);
                }
                //Data.auds.clear();
                //for(int q=0; q<values.length(); q++) {
                    //Data.auds.add(values.get(q).toString());
                //}
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                buildRaspiByRange();
                //gpd.updateGroups();
            }

            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
                scrollz.setVisibility(View.GONE);
                content.removeAllViews();
                lload.setVisibility(View.VISIBLE);
                loadpb.setVisibility(View.VISIBLE);
                loadtv.setText("Получение расписания...");
            }
        };

        load_groups = new RuntimeEvent() {
            @Override
            public void asyncWork() throws Exception {
                super.asyncWork();
                String JSON="";
                HttpsURLConnection connection = null;
                BufferedReader reader = null;
                URL url = new URL("https://raspi.ulspu.ru/json/dashboard/groups");
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                Data.groups.clear();
                for(int q=0; q<values.length(); q++) {
                    Data.groups.add(values.get(q).toString());
                }
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(0);
                //gpd.updateGroups();
               // lload.setVisibility(View.GONE);
               // scrollz.setVisibility(View.VISIBLE);
            }

            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
              //  scrollz.setVisibility(View.GONE);
               // lload.setVisibility(View.VISIBLE);
              //  loadpb.setVisibility(View.VISIBLE);
              //  loadtv.setText("Загружаем данные...");
            }
        };
    }
    public void loadGroupsList() {
        Async.startAsync(load_groups, "grouplist");
    }
    public void loadAudsList() {
        Async.startAsync(load_aud, "audz");
    }
    public void loadPrepodsList() {
        Async.startAsync(load_prepod, "prepodz");
    }
    public void buildRaspiByGroups() {
        if(Data.SAVED_GROUP.compareTo("nullable")==0) {
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            group.setText("ГРУППА");
            loadtv.setText("Давайте начнем! Выберите группу и неделю.");
        }else {
            group.setEnabled(false);
            update.setEnabled(false);
            group.setText(Data.SAVED_GROUP);
            System.out.println("Trying BOOT "+Data.SAVED_GROUP);
            Async.startAsync(levent_group, "grouper");
        }
    }
    public void buildRaspiByRange() {
        //if((Data.groups.size()==0)||(Data.auds.size()==0)||(Data.prepods.size()==0)) return;
        content.removeAllViews();
        group.setEnabled(true);
        update.setEnabled(true);
        DateRange dr = TimeUtils.drangers.get(TimeUtils.SAVED_WEEK);
        ArrayList<Lesson> lessa = Data.getLessonsByPeriod(dr);
        if(lessa.size()>0) {
            //content filling
            for(int q=0; q<lessa.size(); q++)
            content.addView(new DayBoard(this, lessa.get(q)));
            lload.setVisibility(View.GONE);
            scrollz.setVisibility(View.VISIBLE);
        }else{
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            loadtv.setText("На выбранный период для текущих параметров нет данных\n(попробуйте обновить расписание)");
        }
    }
}
