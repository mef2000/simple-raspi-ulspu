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
import arch.main.Kernel;

public class WeekPickerDialog extends Dialog {
    private final MainActivity context;
    private final Kernel kernel;
    private final Button next, undo, cancel;
    private final TextView tvdate;
    private final ListView lw;

    private final WeekAdapter wa;
    public WeekPickerDialog(MainActivity context) {
        super(context);
        this.context = context;
        this.kernel = context.kernel;
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
               kernel.time.getYMApplyBy(1);
               tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
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
                kernel.time.getYMApplyBy(-1);
                tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
                prepareRangers();
            }
        });
        wa = new WeekAdapter(context);
        lw.setAdapter(wa);

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                DateRange dr = wa.getItem(position);
                kernel.SAVED_WEEK = position;
                kernel.SAVED_MONTH = kernel.FOCUS_MONTH;
               kernel.SAVED_YEAR = kernel.FOCUS_YEAR;
               dismiss();
               context.updateUI();
               context.buildRaspiByRange();
                //TimeUtils.buildFromScratch(position);
            }
        });
    }
    @Override public void show() {
        tvdate.setText(kernel.time.getNamedMonth(kernel.FOCUS_MONTH)+" "+kernel.FOCUS_YEAR);
        //TimeUtils.pullDateRangers(TimeUtils.TOTAL_MONTH, TimeUtils.TOTAL_YEAR);
        prepareRangers();
        super.show();
    }
    public void prepareRangers() {
        kernel.time.pullDateRangers(kernel.FOCUS_MONTH, kernel.FOCUS_YEAR);
        wa.clear();
        wa.addAll(kernel.time.drangers);
        wa.notifyDataSetChanged();
    }
}
