package abs.core;

import android.content.Context;
import android.util.TypedValue;

import ru.mefccplusstudios.shellulspu2.R;

public class Style {
    /*
    public int THEME_PARADIGM;
    public int BACKGROUND_COLOR;
    public int DIALOG_COLOR;
    public int DIALOG_HEADER_COLOR;
    public int FIELD_COLOR;
    public int MAIN_FONT_COLOR;
    public int DISABLED_FONT_COLOR;
    public int FOREGROUND_FONT_COLOR;
    public int SEEKBAR_COLOR;

    */
    public final int MAJOR_PADDING;
    public int THEME_PARADIGM;
    public int MAIN_COLOR;
    public int SEEKBAR_COLOR;
    public int FONT_COLOR;
    public int SUBFONT_COLOR;
    public int BACKGROUND_COLOR;
    public int FONT_SIZE_SP;
    private final Context context;
    public Style(Context context) {
        this.context = context;
        MAJOR_PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
    }
    public void loadStyle(int stl) {
        switch(stl) {
            case 0:
                MAIN_COLOR = context.getResources().getColor(R.color.orange);
                SEEKBAR_COLOR = MAIN_COLOR;
                FONT_COLOR = context.getResources().getColor(R.color.black);
                SUBFONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                break;
            case 1:
                MAIN_COLOR = context.getResources().getColor(R.color.phiol);
                SEEKBAR_COLOR = MAIN_COLOR;
                FONT_COLOR = context.getResources().getColor(R.color.black);
                SUBFONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                break;
            case 2:
                MAIN_COLOR = context.getResources().getColor(R.color.grey_light);
                SEEKBAR_COLOR = context.getResources().getColor(R.color.grey_ultra);;
                FONT_COLOR = context.getResources().getColor(R.color.white);
                SUBFONT_COLOR = context.getResources().getColor(R.color.grey_ultra);
                BACKGROUND_COLOR = context.getResources().getColor(R.color.grey);
                break;
        }
    }
}
