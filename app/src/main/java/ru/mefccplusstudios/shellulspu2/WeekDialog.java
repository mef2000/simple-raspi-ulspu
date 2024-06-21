package ru.mefccplusstudios.shellulspu2;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import abs.Window;
import abs.parts.Bus;
import abs.parts.interfaces.Eventable;

public class WeekDialog extends Window {
    private final Button next, undo;
    private final TextView tvdate;
    private final LinearLayout calendar;
    private final WeekLine wl;

    private int FOCUS_MONTH = 0, FOCUS_YEAR = 0;
    private final ArrayList<WeekLine> wls = new ArrayList<>();
    public WeekDialog(Context context) {
        super(context);
        setWinTitle(context.getString(R.string.selweek));

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
            @Override public void onClick(View view) {
                slideTime(1);
                tvdate.setText(Bus.time.getMonthName(FOCUS_MONTH)+" "+FOCUS_YEAR);
                prepareWeeks();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                slideTime(-1);
                tvdate.setText(Bus.time.getMonthName(FOCUS_MONTH)+" "+FOCUS_YEAR);
                prepareWeeks();
            }
        });

    }
    @Override public void event(String tag, Object packet) {
        super.event(tag, packet);
        tvdate.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        tvdate.setTextColor(Bus.style.MAIN_FONT_COLOR);
        for(int q=0; q<7; q++) {
            wl.days[q].setTextColor(Bus.style.DIALOG_HEADER_COLOR);
            wl.days[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        }
        wl.STRLINE.setBackgroundColor(Bus.style.FIELD_COLOR);
        wl.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(Bus.style.FONT_SIZE_SP*2.3f), getContext().getResources().getDisplayMetrics());
        wl.requestLayout();
    }

    @Override public void show() {
        tvdate.setText(Bus.time.getMonthName(FOCUS_MONTH)+" "+FOCUS_YEAR);
        prepareWeeks();
        super.show();
    }
    public void prepareWeeks() {
        for(WeekLine wlz: wls) wl.UNLOCK = true;
        calendar.removeAllViews();
        calendar.addView(wl);
        calendar.addView(wl.STRLINE);
        DateRange[] drs = Bus.time.getMonthSlice(FOCUS_YEAR, FOCUS_MONTH);
        for(int i=0; i<drs.length; i++) {
            WeekLine twl = getFreeWeek();
            twl.UNLOCK = false;
            twl.WEEK_ID = i;
            twl.fill(drs[i], i==0);
            calendar.addView(twl);
            calendar.addView(twl.STRLINE);
        }
    }
    public WeekLine getFreeWeek() {
        WeekLine twl = null;
        for(WeekLine wlz: wls) if(wlz.UNLOCK) { twl = wlz; break; }
        if(twl == null) { twl = new WeekLine(getContext()); wls.add(twl); }
        return twl;
    }
    public void slideTime(int amount) {
        if(FOCUS_YEAR ==0 ) FOCUS_YEAR = Bus.time.TOTAL_YEAR; FOCUS_MONTH = Bus.time.TOTAL_MONTH;
        FOCUS_MONTH += amount;
        if(FOCUS_MONTH>11) {
            FOCUS_YEAR++;
            FOCUS_MONTH -= 12;
        }else if(FOCUS_MONTH<0) {
            FOCUS_YEAR--;
            FOCUS_MONTH += 12;
        }
    }
    public void obtainChoose(WeekLine link) {
        Bus.data.SAVED_WEEK = link.WEEK_ID;
        Bus.data.SAVED_MONTH = FOCUS_MONTH;
        Bus.data.SAVED_YEAR = FOCUS_YEAR;
        dismiss();
        Bus.event("FOCUS_TO_MONTH", link);
    }

    private class WeekLine extends LinearLayout implements Eventable {
        private final TypedValue outValue = new TypedValue();
        public TextView days[] = new TextView[7];
        public int WEEK_ID = -1;
        public boolean UNLOCK = true;
        public final View STRLINE;

        public DateRange LINK;
        protected final WeekLine self = this;
        public WeekLine(Context context) {
            super(context);
            this.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            this.setBackgroundResource(outValue.resourceId);
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setClickable(true);
            this.setGravity(Gravity.CENTER_VERTICAL);
            this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            this.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(Bus.style.FONT_SIZE_SP*2.3f), getResources().getDisplayMetrics());
            this.requestLayout();
            for(int q=0; q<7; q++) {
                TextView tv = new TextView(this.getContext());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
                tv.setTextColor(Bus.style.MAIN_FONT_COLOR);
                tv.setWidth(0);
                tv.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
                tv.setGravity(Gravity.CENTER);
                days[q] = tv;
                this.addView(tv);
            }
            STRLINE = new View(context);
            STRLINE.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            STRLINE.setBackgroundColor(Bus.style.FIELD_COLOR);
            this.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    obtainChoose(self);
                }
            });


        }
        public void fill(DateRange dr, boolean isFirstWeek) {
            if(dr.BEGIN_DAY<dr.END_DAY) for(int q=0; q<7; q++) days[q].setText((dr.BEGIN_DAY+q)+"");
            else {
                int tick = dr.END_DAY;
                for(int q=6; q>-1; q--) {
                    if (isFirstWeek) {
                        if (tick > 0) days[q].setText(tick);
                        else days[q].setText("");
                    } else {
                        if(tick>0) days[q].setText("");
                        else days[q].setText(dr.BEGIN_DAY+q);
                    }
                    tick--;
                }
            }
            LINK = dr;
        }
        @Override public void event(String tag, Object packet) {
            for(int q=0; q<7; q++) {
                days[q].setTextColor(Bus.style.MAIN_FONT_COLOR);
                days[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            }
            STRLINE.setBackgroundColor(Bus.style.FIELD_COLOR);
        }
    }
}
