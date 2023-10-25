package ru.mefccplusstudios.shellulspu2;

import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import arch.adapters.WeekAdapter;
import arch.views.DialogCore;

public class WeekDialog extends DialogCore {
    private final Button next, undo;
    private final TextView tvdate;
    private final LinearLayout calendar;
    private final WeekLine wl;
    public WeekDialog(MainActivity context) {
        super(context);
        setDialogTitle("Выберите неделю");

        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.data_picker, null);
        content.addView(v);

        next = v.findViewById(R.id.nextBtn);
        undo = v.findViewById(R.id.undoBtn);
        tvdate = v.findViewById(R.id.tvDATE);
        calendar = v.findViewById(R.id.calendar);

        wl = new WeekLine(context);
        wl.days[0].setText("пн");
        wl.days[1].setText("вт");
        wl.days[2].setText("ср");
        wl.days[3].setText("чт");
        wl.days[4].setText("пт");
        wl.days[5].setText("сб");
        wl.days[6].setText("вс");
        wl.setClickable(false);
        calendar.addView(wl);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kernel.time.getYMApplyBy(1);
                tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
                prepareRangers();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kernel.time.getYMApplyBy(-1);
                tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
                prepareRangers();
            }
        });

    }
    @Override public void styleHasBeenChanged() {
        super.styleHasBeenChanged();
        for(int q=0; q<7; q++) {
            wl.days[q].setTextColor(kernel.style.DIALOG_HEADER_COLOR);
            wl.days[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        }
        tvdate.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        tvdate.setTextColor(kernel.style.MAIN_FONT_COLOR);
        wl.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(kernel.style.FONT_SIZE_SP*2.3f), context.getResources().getDisplayMetrics());
        wl.requestLayout();
       // rootll.getBackground().setColorFilter(kernel.style.DIALOG_COLOR, PorterDuff.Mode.SRC_ATOP);
    }
    @Override public void show() {
        tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
        //TimeUtils.pullDateRangers(TimeUtils.TOTAL_MONTH, TimeUtils.TOTAL_YEAR);
        prepareRangers();
        super.show();
    }
    public void obtainChoose(int id) {
        DateRange dr = kernel.time.drangers.get(id);
        kernel.SAVED_WEEK = id;
        kernel.SAVED_MONTH = kernel.FOCUS_MONTH;
        kernel.SAVED_YEAR = kernel.FOCUS_YEAR;
        dismiss();
        context.updateUI();
        context.buildRaspiByRange();
    }
    public void inflateWeeks() {
        calendar.removeAllViews();
        calendar.addView(wl);
        View str = new View(this.context);
        str.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        str.setBackgroundColor(kernel.style.FIELD_COLOR);//this.context.getResources().getColor(R.color.dark_std_main));
        calendar.addView(str);
        for(int q=0; q<kernel.time.drangers.size(); q++) {
            WeekLine wtmp = new WeekLine(context);
            wtmp.WEEK_ID = q;
            DateRange dr = kernel.time.drangers.get(q);
            if(dr.BEGIN_DAY<dr.END_DAY) for(int e=0; e<7; e++) {
                wtmp.days[e].setTextColor(kernel.style.MAIN_FONT_COLOR);
                wtmp.days[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
                wtmp.days[e].setText(""+(int)(dr.BEGIN_DAY+e));
            }else{
                int end = dr.END_DAY;
                int medium = 7;
                while(end>0) {
                    wtmp.days[medium-1].setText(""+end);
                    if(q==0) wtmp.days[medium-1].setTextColor(kernel.style.MAIN_FONT_COLOR);
                    else wtmp.days[medium-1].setTextColor(kernel.style.DISABLED_FONT_COLOR);
                    end--;
                    medium--;
                }
                for(int z=0; z<medium; z++) {
                    wtmp.days[z].setText(""+(int)(dr.BEGIN_DAY+z));
                    if(q!=0) wtmp.days[z].setTextColor(kernel.style.MAIN_FONT_COLOR);
                    else wtmp.days[z].setTextColor(kernel.style.DISABLED_FONT_COLOR);
                }
            }
            calendar.addView(wtmp);
            View stmp = new View(this.context);
            stmp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            stmp.setBackgroundColor(kernel.style.FIELD_COLOR);
            calendar.addView(stmp);
        }
    }


    public void prepareRangers() {
        kernel.time.pullDateRangers(kernel.FOCUS_MONTH, kernel.FOCUS_YEAR);
        inflateWeeks();
    }
    private class WeekLine extends LinearLayout {
        private final MainActivity context;
        private final TypedValue outValue = new TypedValue();
        public TextView days[] = new TextView[7];
        public int WEEK_ID = -1;
        public WeekLine(MainActivity context) {
            super(context);
            this.context = context;
            this.context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            this.setBackgroundResource(outValue.resourceId);
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setClickable(true);
            this.setGravity(Gravity.CENTER_VERTICAL);
            this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            this.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(kernel.style.FONT_SIZE_SP*2.3f), getResources().getDisplayMetrics());
            this.requestLayout();
            for(int q=0; q<7; q++) {
                TextView tv = new TextView(this.context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
                tv.setTextColor(getResources().getColor(R.color.std_main));
                tv.setWidth(0);
                tv.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
                tv.setGravity(Gravity.CENTER);
                days[q] = tv;
                this.addView(tv);
            }
            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    obtainChoose(WEEK_ID);
                }
            });


        }

    }
}
