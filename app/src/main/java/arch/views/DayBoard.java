package arch.views;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import arch.main.DataFill;
import arch.main.Lesson;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;
import ru.mefccplusstudios.shellulspu2.TimeUtils;

public class DayBoard extends LinearLayout {
    private final MainActivity context;
    private final TextView day;
    private final Lesson lesson;
    private final HashMap<String, Stroken> strline = new HashMap<>();
    public DayBoard(MainActivity context, Lesson lesson) {
        super(context);
        this.context = context;
        this.lesson = lesson;
        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        day = new TextView(context);
        day.setTextColor(getResources().getColor(R.color.black));
        day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        day.setTypeface(Typeface.DEFAULT_BOLD);
        this.addView(day);
        if(lesson.dataz.size()>0) {
            Collections.sort(lesson.dataz);
            DataFill df = lesson.dataz.get(0);
            String dayweek = TimeUtils.getNamedDayOfWeekBy(df.YEAR, df.MONTH, df.DAY)+", "+TimeUtils.getNormalNumber(df.DAY)+"/"+TimeUtils.getNormalNumber(df.MONTH+1)+"/"+df.YEAR;
            day.setText(dayweek);
            for(int q=0; q<lesson.dataz.size(); q++) {
                DataFill tdf = lesson.dataz.get(q);
                String time_range = TimeUtils.getNormalNumber(tdf.START_HOUR)+":"+
                        TimeUtils.getNormalNumber(tdf.START_MINS)+" - "+
                        TimeUtils.getNormalNumber(tdf.END_HOUR)+":"+TimeUtils.getNormalNumber(tdf.END_MINS);
                Stroken cor;
                if(strline.containsKey(time_range)) cor = strline.get(time_range);
                else {
                    cor = new Stroken(context, time_range);
                    this.addView(cor);
                    strline.put(time_range, cor);
                }
                cor.add(tdf.CONTEXT);

            }
        }else day.setText("Ошибка данных");
    }
}
