package abs.parts;

import android.content.Context;
import android.util.TypedValue;

import ru.mefccplusstudios.shellulspu2.R;

public class Style {
    public int THEME_PARADIGM;
    public int BACKGROUND_COLOR;
    public int DIALOG_COLOR;
    public int DIALOG_HEADER_COLOR;
    public int FIELD_COLOR;
    public int MAIN_FONT_COLOR;
    public int DISABLED_FONT_COLOR;
    public int FOREGROUND_FONT_COLOR;
    public int SEEKBAR_COLOR;
    public int FONT_SIZE_SP;

    public final int MAJOR_PADDING;
    private final Context context;
    public Style(Context context) {
        this.context = context;
        MAJOR_PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
    }
    public void loadStyle(int stl) {
        switch(stl) {
            case 0:
                BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                FIELD_COLOR = context.getResources().getColor(R.color.orange);
                SEEKBAR_COLOR = context.getResources().getColor(R.color.orange);
                FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                MAIN_FONT_COLOR = context.getResources().getColor(R.color.black);
                DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                DIALOG_COLOR = context.getResources().getColor(R.color.white);
                DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.orange);
                break;
            case 1:
                SEEKBAR_COLOR = context.getResources().getColor(R.color.phiol);
                BACKGROUND_COLOR = context.getResources().getColor(R.color.white);
                FIELD_COLOR = context.getResources().getColor(R.color.phiol);
                FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                MAIN_FONT_COLOR = context.getResources().getColor(R.color.black);
                DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_dedark);
                DIALOG_COLOR = context.getResources().getColor(R.color.white);
                DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.phiol);
                break;
            case 2:
                SEEKBAR_COLOR = context.getResources().getColor(R.color.grey_ultra);
                BACKGROUND_COLOR = context.getResources().getColor(R.color.grey);
                FIELD_COLOR = context.getResources().getColor(R.color.grey_light);
                FOREGROUND_FONT_COLOR = context.getResources().getColor(R.color.white);
                MAIN_FONT_COLOR = context.getResources().getColor(R.color.white);
                DISABLED_FONT_COLOR = context.getResources().getColor(R.color.grey_ultra);
                DIALOG_COLOR = context.getResources().getColor(R.color.grey_light);
                DIALOG_HEADER_COLOR = context.getResources().getColor(R.color.white);
                break;
        }
    }
}
