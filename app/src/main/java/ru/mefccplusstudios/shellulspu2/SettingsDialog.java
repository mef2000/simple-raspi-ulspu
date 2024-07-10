package ru.mefccplusstudios.shellulspu2;

import static android.graphics.PorterDuff.Mode.SRC_ATOP;

import android.content.Context;
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

import abs.core.Bus;
import abs.core.PickerAdapter;
import abs.core.Window;


public class SettingsDialog extends Window {
    private final TextView tvc, tvf, samtv, copyf;
    private final SeekBar sb;
    private final Spinner spinka;
    private final PickerAdapter adapter;
    private final CheckBox chk, sbar;
    public SettingsDialog(Context context) {
        super(context);
        setWinTitle(context.getString(R.string.settitle));
        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.settings_layout, null);
        content.addView(v);

        tvc = v.findViewById(R.id.stvColor);
        tvf = v.findViewById(R.id.stvF);
        copyf = v.findViewById(R.id.copyF);
        samtv = v.findViewById(R.id.sampleF);
        sb = v.findViewById(R.id.seekF);
        spinka = v.findViewById(R.id.spinka);
        chk = v.findViewById(R.id.chk);
        sbar = v.findViewById(R.id.statbar);
        adapter = new PickerAdapter(context);
        adapter.addAll(context.getResources().getStringArray(R.array.themes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinka.setAdapter(adapter);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bus.style.FONT_SIZE_SP = progress;
                Bus.event(Bus.FONTS_CHANGED, null);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spinka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                if(selectedItemPosition != Bus.style.THEME_PARADIGM) {
                    Bus.style.THEME_PARADIGM = selectedItemPosition;
                    Bus.style.loadStyle(selectedItemPosition);
                    Bus.event(Bus.COLORS_CHANGED, null);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Bus.data.isDebugMode = isChecked;
            }
        });
        sbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Bus.data.isBarSupport = isChecked;
                Bus.event("ON_CHANGE_CONFIG", null);
            }
        });
    }
    @Override public void event(String tag, Object packet) {
        super.event(tag, packet);
        spinka.setSelection(Bus.style.THEME_PARADIGM);
        tvc.setTextColor(Bus.style.FONT_COLOR);
        tvf.setTextColor(Bus.style.FONT_COLOR);
        samtv.setTextColor(Bus.style.FONT_COLOR);
        copyf.setTextColor(Bus.style.FONT_COLOR);
        samtv.setText(Bus.style.FONT_SIZE_SP+" sp");
        tvc.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        tvf.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        samtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        copyf.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        sb.getProgressDrawable().setColorFilter(Bus.style.SEEKBAR_COLOR, SRC_ATOP);
        sb.getThumb().setColorFilter(Bus.style.SEEKBAR_COLOR, SRC_ATOP);
        chk.setTextColor(Bus.style.FONT_COLOR);
        chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        chk.setButtonTintList(ColorStateList.valueOf(Bus.style.SEEKBAR_COLOR));

        sbar.setTextColor(Bus.style.FONT_COLOR);
        sbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        sbar.setButtonTintList(ColorStateList.valueOf(Bus.style.SEEKBAR_COLOR));

        adapter.notifyDataSetChanged();
    }
    @Override public void show() {
        sb.setProgress(Bus.style.FONT_SIZE_SP);
        chk.setChecked(Bus.data.isDebugMode);
        sbar.setChecked(Bus.data.isBarSupport);
        super.show();
    }
}
