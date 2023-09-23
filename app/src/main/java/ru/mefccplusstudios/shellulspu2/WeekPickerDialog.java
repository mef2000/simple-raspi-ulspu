package ru.mefccplusstudios.shellulspu2;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import arch.adapters.WeekAdapter;

public class WeekPickerDialog extends Dialog {
    private final MainActivity context;
    private final Button next, undo, cancel;
    private final TextView tvdate;
    private final ListView lw;

    private final WeekAdapter wa;
    public WeekPickerDialog(MainActivity context) {
        super(context);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.data_picker);
        this.setCancelable(true);
        this.getWindow().getAttributes().windowAnimations = R.style.animationChooser;
        this.getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        next = findViewById(R.id.nextBtn);
        undo = findViewById(R.id.undoBtn);
        tvdate = findViewById(R.id.tvDATE);
        cancel = findViewById(R.id.cancelBtn);
        lw = findViewById(R.id.weeksLV);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TimeUtils.getYMApplyBy(1);
               tvdate.setText(TimeUtils.getNamedMonth(TimeUtils.FOCUS_MONTH)+" "+TimeUtils.FOCUS_YEAR);
               prepareRangers();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeUtils.getYMApplyBy(-1);
                tvdate.setText(TimeUtils.getNamedMonth(TimeUtils.FOCUS_MONTH)+" "+TimeUtils.FOCUS_YEAR);
                prepareRangers();
            }
        });
        wa = new WeekAdapter(context);
        lw.setAdapter(wa);

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                DateRange dr = wa.getItem(position);
                TimeUtils.SAVED_WEEK = position;
                TimeUtils.SAVED_MONTH = TimeUtils.FOCUS_MONTH;
               //if(position==0) TimeUtils.SAVED_MONTH = dr.END_MONTH;
               //else TimeUtils.SAVED_MONTH = dr.BEGIN_MONTH;
               TimeUtils.SAVED_YEAR = TimeUtils.FOCUS_YEAR;
               dismiss();
               context.updateUI();
               context.buildRaspiByRange();
                //TimeUtils.buildFromScratch(position);
            }
        });
    }
    @Override public void show() {
        tvdate.setText(TimeUtils.getNamedMonth(TimeUtils.FOCUS_MONTH)+" "+TimeUtils.FOCUS_YEAR);
        //TimeUtils.pullDateRangers(TimeUtils.TOTAL_MONTH, TimeUtils.TOTAL_YEAR);
        prepareRangers();
        super.show();
    }
    public void prepareRangers() {
        TimeUtils.pullDateRangers(TimeUtils.FOCUS_MONTH, TimeUtils.FOCUS_YEAR);
        wa.clear();
        wa.addAll(TimeUtils.drangers);
        wa.notifyDataSetChanged();
    }
}
