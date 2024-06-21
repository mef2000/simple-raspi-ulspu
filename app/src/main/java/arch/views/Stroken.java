package arch.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import abs.parts.Bus;
import abs.parts.interfaces.Eventable;
import ru.mefccplusstudios.shellulspu2.R;

public class Stroken extends LinearLayout implements Eventable {
    private final LinearLayout content;
    private final TextView tv;
    private final View horv, vertv;
    private final ArrayList<TextView> tpool = new ArrayList<>();
    private final ArrayList<TextView> dpool = new ArrayList<>();
    public Stroken(Context context, String time_range) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.stroken_layout, null);
        content = v.findViewById(R.id.conentLL);
        tv = v.findViewById(R.id.idTime);
        horv = v.findViewById(R.id.horV);
        vertv = v.findViewById(R.id.verV);
        horv.setBackgroundColor(Bus.style.FIELD_COLOR);
        vertv.setBackgroundColor(Bus.style.FIELD_COLOR);
        tv.setText(time_range);
        tv.setTextColor(Bus.style.MAIN_FONT_COLOR);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        this.addView(v);
    }
    public void merge(String lesson) {
        TextView title = new TextView(getContext()), desc = new TextView(getContext());
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        desc.setTextColor(Bus.style.DISABLED_FONT_COLOR);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        desc.setLayoutParams(params);

        content.addView(title); content.addView(desc);
        tpool.add(title); dpool.add(desc);
        try {
            title.setText(lesson.substring(0, lesson.indexOf(" -")));
            desc.setText(lesson.substring(lesson.indexOf(" - "), lesson.length()));
        }catch(Exception e) { e.printStackTrace(); title.setText(lesson); System.out.println("Stroken::merge::Wrong syntax: "+lesson);}
    }

    @Override public void event(String tag, Object packet) {
        if(Bus.COLORS_CHANGED.equals(tag)) {
            horv.setBackgroundColor(Bus.style.FIELD_COLOR);
            vertv.setBackgroundColor(Bus.style.FIELD_COLOR);
            tv.setTextColor(Bus.style.MAIN_FONT_COLOR);
            for(TextView v: tpool) v.setTextColor(Bus.style.MAIN_FONT_COLOR);
            for(TextView v: dpool) v.setTextColor(Bus.style.DISABLED_FONT_COLOR);
        }else if(Bus.FONTS_CHANGED.equals(tag)) {
            for(TextView v: tpool) {
                v.setTextColor(Bus.style.MAIN_FONT_COLOR);
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            }
            for(TextView v: dpool) {
                v.setTextColor(Bus.style.MAIN_FONT_COLOR);
                v.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            }
            tv.setTextColor(Bus.style.MAIN_FONT_COLOR);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        }
    }
}
