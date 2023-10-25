package arch.views;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import arch.main.Kernel;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class DialogCore extends Dialog {
    protected final MainActivity context;
    protected final Kernel kernel;
    protected final Button exit;
    protected final TextView tvtitle;
    protected final LinearLayout content, rootll;
    protected final View separat;
    public DialogCore(MainActivity context) {
        super(context);
        this.context = context;
        kernel = context.kernel;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.core_dialog);
        this.setCancelable(true);
        this.getWindow().getAttributes().windowAnimations = R.style.animationChooser;
        this.getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        tvtitle = findViewById(R.id.dialogTitle);
        exit = findViewById(R.id.dialogClose);
        content = findViewById(R.id.dialogContent);
        rootll = findViewById(R.id.rootll);
        separat = findViewById(R.id.separator);
        this.
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
    }
    public void styleHasBeenChanged() {
        tvtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        tvtitle.setTextColor(kernel.style.DIALOG_HEADER_COLOR);
        separat.setBackgroundColor(kernel.style.DIALOG_HEADER_COLOR);
       // exit.getBackground().setColorFilter(kernel.style.FIELD_COLOR, PorterDuff.Mode.SRC_ATOP);
       rootll.setBackgroundColor(kernel.style.DIALOG_COLOR);
       // rootll.getBackground().setColorFilter(kernel.style.DIALOG_COLOR, PorterDuff.Mode.SRC_ATOP);

    }
    public LinearLayout clearContent() {
        content.removeAllViews();
        return content;
    }
    public LinearLayout getContent() {
        return content;
    }
    public void setDialogTitle(String title) {
        tvtitle.setText(title);
    }
}
