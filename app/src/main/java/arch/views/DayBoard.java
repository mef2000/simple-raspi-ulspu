package arch.views;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import arch.main.DataFill;
import arch.main.Kernel;
import arch.main.Lesson;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class DayBoard extends LinearLayout {
    private final Kernel kernel;
    private final MainActivity context;
    private final TextView day;
    private final Lesson lesson;
    private final HashMap<String, Stroken> strline = new HashMap<>();
    public DayBoard(MainActivity context, Lesson lesson) {
        super(context);
        kernel = context.kernel;
        this.context = context;
        this.lesson = lesson;
        this.setOrientation(LinearLayout.VERTICAL);
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
       // this.setBackgroundColor(kernel.style.BACKGROUND_COLOR);


        GradientDrawable gdraw = new GradientDrawable();
        gdraw.setShape(GradientDrawable.RECTANGLE);
        gdraw.setStroke((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()), kernel.style.FIELD_COLOR);
        gdraw.setCornerRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()));
        this.setBackground(gdraw);

        //this.getBackground().setColorFilter(kernel.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);


        this.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, padding);
        this.setLayoutParams(lp);
        day = new TextView(context);
        day.setTextColor(kernel.style.MAIN_FONT_COLOR);//getResources().getColor(R.color.black));
        day.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        day.setTypeface(Typeface.DEFAULT_BOLD);



        this.addView(day);
        if(lesson.dataz.size()>0) {
            Collections.sort(lesson.dataz);
            DataFill df = lesson.dataz.get(0);
            String dayweek = kernel.time.getNamedDayOfWeekBy(df.YEAR, df.MONTH, df.DAY)+", "+kernel.time.getStyledData(df.YEAR, df.MONTH+1, df.DAY);
            day.setText(dayweek);
            for(int q=0; q<lesson.dataz.size(); q++) {
                DataFill tdf = lesson.dataz.get(q);
                String time_range = kernel.time.getNormalNumber(tdf.START_HOUR)+":"+
                        kernel.time.getNormalNumber(tdf.START_MINS)+" - "+
                        kernel.time.getNormalNumber(tdf.END_HOUR)+":"+kernel.time.getNormalNumber(tdf.END_MINS);
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
