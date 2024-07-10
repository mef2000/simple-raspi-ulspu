package abs.core;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Day extends LinearLayout implements Eventable {
    private final TextView day;
    private final GradientDrawable gdraw;
    private final HashMap<String, Stroken> strline = new HashMap<>();
    public Day(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setPadding(Bus.style.MAJOR_PADDING, Bus.style.MAJOR_PADDING, Bus.style.MAJOR_PADDING, Bus.style.MAJOR_PADDING);

        gdraw = new GradientDrawable();
        gdraw.setShape(GradientDrawable.RECTANGLE);
        gdraw.setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()), Bus.style.MAIN_COLOR);
        gdraw.setCornerRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()));
        this.setBackground(gdraw);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, Bus.style.MAJOR_PADDING);
        this.setLayoutParams(lp);

        day = new TextView(context);
        day.setTextColor(Bus.style.FONT_COLOR);
        day.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        day.setTypeface(Typeface.DEFAULT_BOLD);
    }
    public void setLessons(ArrayList<DataFill> data) {
        this.removeAllViews();
        strline.clear();
        this.setVisibility(GONE);
        if(data.isEmpty()) return;

        this.addView(day);
        for(DataFill tdf: data) {
            String time_range = Bus.time.getNormalNumber(tdf.START_HOUR)+":"+
                    Bus.time.getNormalNumber(tdf.START_MINS)+" - "+
                    Bus.time.getNormalNumber(tdf.END_HOUR)+":"+Bus.time.getNormalNumber(tdf.END_MINS);
            Stroken str = strline.get(time_range);
            if(str == null) { str = new Stroken(this.getContext(), time_range); strline.put(time_range, str); this.addView(str);}
            str.merge(tdf.CONTEXT);
        }
        DataFill df = data.get(0);
        String dayw = Bus.time.getDayName(df.YEAR, df.MONTH, df.DAY)+", "+Bus.time.packWithSlash(df.YEAR, df.MONTH, df.DAY);
        day.setText(dayw);
        this.setVisibility(VISIBLE);
    }

    @Override public void event(String tag, Object packet) {
        if(Bus.COLORS_CHANGED.equals(tag)) {
            day.setTextColor(Bus.style.FONT_COLOR);
            gdraw.setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics()), Bus.style.MAIN_COLOR);
            //gdraw.setColor(Bus.style.MAIN_COLOR);
        }else if(Bus.FONTS_CHANGED.equals(tag)) {
            day.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            day.setTextColor(Bus.style.FONT_COLOR);
        }
        for(Map.Entry<String, Stroken> str : strline.entrySet()) str.getValue().event(tag, packet);
    }
}
