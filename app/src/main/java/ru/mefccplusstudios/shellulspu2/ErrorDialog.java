package ru.mefccplusstudios.shellulspu2;

import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import arch.main.Data;

public class ErrorDialog extends Dialog  {
    private final MainActivity context;
    private final TextView tverror;
    public ErrorDialog(MainActivity context) {
        super(context);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.error_layout);
        this.setCancelable(true);
        this.getWindow().getAttributes().windowAnimations = R.style.animationChooser;
        this.getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        tverror = findViewById(R.id.errorD);
    }
    @Override public void show() {
        tverror.setText(Data.error_stack);
        super.show();

    }
}
