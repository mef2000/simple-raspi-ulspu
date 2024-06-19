package ru.mefccplusstudios.shellulspu2;

import static android.graphics.PorterDuff.Mode.SRC_ATOP;

import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import arch.adapters.PickerAdapter;
import arch.views.DialogCore;

public class SettingsDialog extends DialogCore {
    private final TextView tvc, tvf, samtv;
    private final SeekBar sb;
    private final Spinner spinka;
    private final PickerAdapter adapter;
    private final CheckBox chk;
    public SettingsDialog(MainActivity context) {
        super(context);
        setDialogTitle("Настройки");
        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.settings_layout, null);
        content.addView(v);

        tvc = v.findViewById(R.id.stvColor);
        tvf = v.findViewById(R.id.stvF);
        samtv = v.findViewById(R.id.sampleF);
        sb = v.findViewById(R.id.seekF);
        spinka = v.findViewById(R.id.spinka);
        chk = v.findViewById(R.id.chk);
        adapter = new PickerAdapter(context);
        adapter.addAll(context.getResources().getStringArray(R.array.themes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinka.setAdapter(adapter);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kernel.style.FONT_SIZE_SP = progress;
                context.styleHasBeenChanged();
                //samtv.setText(progress+" sp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spinka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                if(selectedItemPosition!=kernel.style.THEME_PARADIGM) {
                    kernel.style.THEME_PARADIGM = selectedItemPosition;
                    kernel.style.loadStyle(selectedItemPosition);
                    context.styleHasBeenChanged();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kernel.isDebugMode = isChecked;
            }
        });
    }
    @Override public void styleHasBeenChanged() {
        super.styleHasBeenChanged();
        spinka.setSelection(kernel.style.THEME_PARADIGM);
        tvc.setTextColor(kernel.style.MAIN_FONT_COLOR);
        tvf.setTextColor(kernel.style.MAIN_FONT_COLOR);
        samtv.setTextColor(kernel.style.MAIN_FONT_COLOR);
        samtv.setText(kernel.style.FONT_SIZE_SP+" sp");
        tvc.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        tvf.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        samtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        sb.getProgressDrawable().setColorFilter(kernel.style.SEEKBAR_COLOR, SRC_ATOP);
        sb.getThumb().setColorFilter(kernel.style.SEEKBAR_COLOR, SRC_ATOP);//.setFsetBackgroundColor(kernel.style.FIELD_COLOR);
        chk.setTextColor(kernel.style.MAIN_FONT_COLOR);
        chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        chk.setButtonTintList(ColorStateList.valueOf(kernel.style.SEEKBAR_COLOR));
        adapter.notifyDataSetChanged();
    }
    @Override public void show() {
        sb.setProgress(kernel.style.FONT_SIZE_SP);
        chk.setChecked(kernel.isDebugMode);
        super.show();
    }
}
