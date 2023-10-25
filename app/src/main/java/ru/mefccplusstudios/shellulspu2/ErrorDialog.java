package ru.mefccplusstudios.shellulspu2;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import arch.views.DialogCore;

public class ErrorDialog extends DialogCore {
    private final TextView tverror;
    public ErrorDialog(MainActivity context) {
        super(context);
        setDialogTitle("Ошибка выполнения");
        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.error_layout, null);
        content.addView(v);
        tverror = v.findViewById(R.id.errorD);
    }
    @Override public void show() {
        tverror.setText(kernel.ERROR_MSG);
        super.show();

    }
    @Override public void styleHasBeenChanged() {
        super.styleHasBeenChanged();
        tverror.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        tverror.setTextColor(kernel.style.MAIN_FONT_COLOR);
    }
}
