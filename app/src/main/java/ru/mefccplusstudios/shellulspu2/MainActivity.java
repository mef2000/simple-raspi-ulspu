package ru.mefccplusstudios.shellulspu2;

import android.app.Activity;
import android.os.Bundle;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import arch.main.DataFill;
import arch.main.Kernel;
import arch.main.Lesson;
import arch.main.RuntimeEvent;
import arch.views.DayBoard;

public class MainActivity extends Activity {
    public final Kernel kernel = new Kernel();

    private TextClock tc;
    private ImageButton update;
    private TextView daytv, weektv, nweek, monthtv, weekperiod;

    private ErrorDialog errdial;
    private WeekPickerDialog wpd;
    private GroupPickerDialog gpd;
    private LinearLayout weekp;

    private Button group;
    private RuntimeEvent rerror;

    private ScrollView scrollz;
    private ProgressBar loadpb;
    private TextView loadtv;
    private LinearLayout lload, content;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kernel.settings = getSharedPreferences("kon_games_global", MODE_PRIVATE);
        wpd = new WeekPickerDialog(this);
        gpd = new GroupPickerDialog(this);
        errdial = new ErrorDialog(this);

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
               wpd.show();
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
                buildRaspiByParams();
            }
        });

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
                if(kernel.isDebugMode) errdial.show();
            }
        };
    }
    @Override public void onStart() {
        super.onStart();
        kernel.loadSettings();
        kernel.time.init();
        kernel.time.pullDateRangers(kernel.SAVED_MONTH, kernel.SAVED_YEAR);
        kernel.async.init(rerror);
        gpd.updateState();
        buildRaspiByParams();
    }

    @Override public void onResume() {
        super.onResume();
        tc.setFormat12Hour(null);
        tc.setFormat24Hour("HH:mm");
        weektv.setText(kernel.time.getNamedDayOfWeekBy(kernel.time.TOTAL_YEAR, kernel.time.TOTAL_MONTH, kernel.time.TOTAL_DAY).toLowerCase());
        daytv.setText(kernel.time.getStyledData(
                kernel.time.TOTAL_YEAR, kernel.time.TOTAL_MONTH, kernel.time.TOTAL_DAY));
        updateUI();
    }
    @Override public void onPause() {
        super.onPause();
        kernel.saveSettings();
    }
    public void updateUI() {
        String month =kernel.time.getNamedMonth(kernel.SAVED_MONTH);
        nweek.setText(month.substring(0,1).toUpperCase());
        monthtv.setText(month.substring(1, month.length()).toLowerCase());
        weekperiod.setText(kernel.time.buildFromSaved());
    }

    public RuntimeEvent newDataByParamLoader() {
        RuntimeEvent re = new RuntimeEvent() {
            @Override
            public void asyncWork() throws Exception {
                super.asyncWork();
                String JSON="";
                HttpsURLConnection connection = null;
                BufferedReader reader = null;

                String mode = "group";
                switch(kernel.ACTIVED_TAB){
                    case 0: mode = "group"; break;
                    case 1: mode = "teacher"; break;
                    case 2: mode = "room"; break;
                }
                URL url = new URL("https://raspi.ulspu.ru/json/dashboard/events?mode="+mode+"&value="+kernel.SAVED_PARAM);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    if(isKilled) break;
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                if(isKilled) return;
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("data");
                if(isKilled) return;
                kernel.data.filler.clear();
                Calendar cd = Calendar.getInstance();
                for(int q=0; q<values.length(); q++) {
                    if(isKilled) break;
                    JSONObject joba = values.getJSONObject(q);
                    String title = joba.getString("title");
                    String start = joba.getString("start");
                    String end = joba.getString("end");
                    cd.setTime(kernel.time.sdf.parse(start.substring(0, start.length()-5)));
                    cd.add(Calendar.HOUR_OF_DAY, kernel.time.HOURS_STEP);
                    DataFill df = new DataFill();
                    df.CONTEXT = title;
                    df.YEAR = cd.get(Calendar.YEAR);
                    df.MONTH = cd.get(Calendar.MONTH);
                    df.DAY = cd.get(Calendar.DAY_OF_MONTH);
                    df.START_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                    df.START_MINS = cd.get(Calendar.MINUTE);
                    cd.setTime(kernel.time.sdf.parse(end.substring(0, end.length()-5)));
                    cd.add(Calendar.HOUR_OF_DAY, kernel.time.HOURS_STEP);
                    df.END_HOUR = cd.get(Calendar.HOUR_OF_DAY);
                    df.END_MINS = cd.get(Calendar.MINUTE);
                    String TIME_ID = ""+df.YEAR;
                    if(df.MONTH<10) TIME_ID = TIME_ID+"0";
                    TIME_ID = TIME_ID+df.MONTH;

                    if(df.DAY<10) TIME_ID = TIME_ID+"0";
                    TIME_ID = TIME_ID+df.DAY;

                    df.TIME_ID = Integer.valueOf(TIME_ID);
                    kernel.data.insertDataFill(df);
                }
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                if(isKilled) return;
                buildRaspiByRange();
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
        return re;
    }
    public RuntimeEvent newPrepodsListLoader() {
        RuntimeEvent re = new RuntimeEvent() {
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
                    if(isKilled) break;
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                if(isKilled) return;
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                if(isKilled) return;
                kernel.prepods.clear();
                for(int q=0; q<values.length(); q++) {
                    kernel.prepods.add(values.get(q).toString());
                }
            }
            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(1);
            }
            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
            }
        };
        return re;
    }
    public RuntimeEvent newAudsListLoader() {
        RuntimeEvent re = new RuntimeEvent() {
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
                    if(isKilled) break;
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                if(isKilled) return;
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                if(isKilled) return;
                kernel.auds.clear();
                for(int q=0; q<values.length(); q++) {
                    kernel.auds.add(values.get(q).toString());
                }
            }

            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(2);
            }

            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
            }
        };
        return re;
    }
    public RuntimeEvent newGroupsListLoader() {
        RuntimeEvent re = new RuntimeEvent() {
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
                    if(isKilled) break;
                    buffer.append(line+"\n");
                }
                JSON = buffer.toString();
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
                if(isKilled) return;
                JSONObject root = new JSONObject(JSON);
                String status = root.get("status").toString();
                if(status.compareTo("ok")!=0) throw new JSONException("BAD_ANSWER_FROM_SERVER");
                JSONArray values = (JSONArray) root.get("rows");
                if(isKilled) return;
                kernel.groups.clear();
                for(int q=0; q<values.length(); q++) {
                    kernel.groups.add(values.get(q).toString());
                }
            }
            @Override
            public void postWorkMainUI() {
                super.postWorkMainUI();
                gpd.checkReady(0);
            }
            @Override
            public void preWorkMainUI() {
                super.preWorkMainUI();
            }
        };
        return re;
    }


    public void loadGroupsList() {
        kernel.async.startAsync(newGroupsListLoader(), "grouplist");
    }
    public void loadAudsList() {
        kernel.async.startAsync(newAudsListLoader(), "audz");
    }
    public void loadPrepodsList() {
        kernel.async.startAsync(newPrepodsListLoader(), "prepodz");
    }

    public void buildRaspiByParams() {
        if(kernel.SAVED_PARAM.compareTo("nullable")==0) {
            scrollz.setVisibility(View.GONE);
            lload.setVisibility(View.VISIBLE);
            loadpb.setVisibility(View.GONE);
            group.setText("ГРУППА");
            loadtv.setText("Давайте начнем! Выберите группу и неделю.");
        }else {
            group.setEnabled(false);
            update.setEnabled(false);
            group.setText(kernel.SAVED_PARAM);
            System.out.println("Trying BOOT "+kernel.SAVED_PARAM);
            kernel.async.startAsync(newDataByParamLoader(), "grouper");
        }
    }
    public void buildRaspiByRange() {
        //if((Data.groups.size()==0)||(Data.auds.size()==0)||(Data.prepods.size()==0)) return;
        content.removeAllViews();
        group.setEnabled(true);
        update.setEnabled(true);
        DateRange dr = kernel.time.drangers.get(kernel.SAVED_WEEK);
        ArrayList<Lesson> lessa = kernel.data.getLessonsByPeriod(dr);
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
