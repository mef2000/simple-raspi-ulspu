package ru.mefccplusstudios.shellulspu2;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import abs.core.Window;
import abs.core.Bus;

public class ErrorDialog extends Window {
    private final TextView tverror;
    public ErrorDialog(Context context) {
        super(context);
        setWinTitle(context.getString(R.string.rterror));
        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.error_layout, null);
        content.addView(v);
        tverror = v.findViewById(R.id.errorD);
    }
    public void show(String message) {
        tverror.setText(message); super.show();
    }
    @Override public void event(String tag, Object packet) {
        super.event(tag, packet);
        tverror.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        tverror.setTextColor(Bus.style.FONT_COLOR);
    }
}
