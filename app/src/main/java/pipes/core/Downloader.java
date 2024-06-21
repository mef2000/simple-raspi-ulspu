package pipes.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import abs.parts.Bus;
import arch.main.DataFill;

public class Downloader extends Pipe {
    private boolean isKilled = false;
    private final HashMap<String, Pipe> pipes = new HashMap<>();
    public Downloader() {
        super("DOWNLOADER", Bus.ANY_BACK_THREAD);
    }

    @Override
    public Fluid work(Fluid in) {
        String JSON="";
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        Fluid next = new Fluid();
        try {
            URL url = null;

            if ("get_list".equals(in.EVENT)) url = new URL("https://raspi.ulspu.ru/json/dashboard/"+in.TRANSFER);
            else url = new URL("https://raspi.ulspu.ru/json/dashboard/events?mode="+in.EVENT+"&value="+in.TRANSFER);

            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                if(isKilled) break;
                buffer.append(line).append("\n");
            }
            JSON = buffer.toString();
            if (connection != null) connection.disconnect();
            if (reader != null) reader.close();

            if(isKilled) {
                next.CHAIN = next;
                next.TRANSFER = "stop by Holder (Pipes man)";
                next.PIPE = pipes.get("onStop");
                next.EVENT = "onStop";
                return next;
            }
            JSONObject root = new JSONObject(JSON);
            String status = root.get("status").toString();
            if(status.compareTo("ok") != 0) throw new JSONException("BAD_ANSWER_FROM_SERVER");

            if ("get_list".equals(in.EVENT)) {
                JSONArray values = (JSONArray) root.get("rows");
                if("rooms".equals(in.TRANSFER)) {
                    Bus.data.ROOMS.clear();
                    for(int q=0; q<values.length(); q++) Bus.data.ROOMS.add(values.get(q).toString());
                }else if("groups".equals(in.TRANSFER)) {
                    Bus.data.GROUPS.clear();
                    for(int q=0; q<values.length(); q++) Bus.data.GROUPS.add(values.get(q).toString());
                }else if("teachers".equals(in.TRANSFER)) {
                    Bus.data.TEACHERS.clear();
                    for(int q=0; q<values.length(); q++) Bus.data.TEACHERS.add(values.get(q).toString());
                }
                next.TRANSFER = in.TRANSFER;
                next.CHAIN = next;
                next.EVENT = "onUpdateList";
                next.PIPE = pipes.get("onUpdateList");
            }else {
                JSONArray values = (JSONArray) root.get("data");
                Bus.data.filler.clear();
                Calendar cd = Calendar.getInstance();
                for(int q=0; q<values.length(); q++) {
                    if(isKilled) break;
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
                next.TRANSFER = root;
                next.EVENT = in.EVENT;
                next.CHAIN = next;
                next.PIPE = pipes.get("onParser");
            }
            return next;
        }catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            next.TRANSFER = sw.toString();
            next.PIPE = pipes.get("onError");
            next.EVENT = "onError";
            next.CHAIN = next;
            return next;
        }
    }

    @Override public void connect(String trigger, Pipe connect) { pipes.put(trigger, connect); }
    @Override public void stop() { isKilled = true; }
}
