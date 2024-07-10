package abs.core;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.mefccplusstudios.shellulspu2.R;

public class Window extends Dialog implements Eventable {
    protected final Button exit;
    protected final TextView tvtitle;
    protected final LinearLayout content, rootll;
    protected final View separat;
    public Window(Context context) {
        super(context);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
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
        exit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
            }
        });
    }
    public LinearLayout getContent() {
        return content;
    }
    public void setWinTitle(String win_title) { tvtitle.setText(win_title); }

    @Override public void event(String tag, Object packet) {
        if(Bus.COLORS_CHANGED.equals(tag)) {
            rootll.setBackgroundColor(Bus.style.BACKGROUND_COLOR);
            tvtitle.setTextColor(Bus.style.MAIN_COLOR);
            separat.setBackgroundColor(Bus.style.MAIN_COLOR);
        }else if(Bus.FONTS_CHANGED.equals(tag)) {
            tvtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
            tvtitle.setTextColor(Bus.style.MAIN_COLOR);
        }
    }
}
