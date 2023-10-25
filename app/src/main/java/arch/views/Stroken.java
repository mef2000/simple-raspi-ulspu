package arch.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import arch.main.Kernel;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class Stroken extends LinearLayout {
    private final Kernel kernel;
    private final MainActivity context;
    private final LayoutInflater inflater;
    private final LinearLayout content;
    private final TextView tv;
    private final View horv, vertv;
    public Stroken(MainActivity context, String time_range) {
        super(context);
        this.context = context;
        this.kernel = context.kernel;
        inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.stroken_layout, null);
        content = v.findViewById(R.id.conentLL);
        tv = v.findViewById(R.id.idTime);
        horv = v.findViewById(R.id.horV);
        vertv = v.findViewById(R.id.verV);
        horv.setBackgroundColor(kernel.style.FIELD_COLOR);
        vertv.setBackgroundColor(kernel.style.FIELD_COLOR);
        tv.setText(time_range);
        tv.setTextColor(kernel.style.MAIN_FONT_COLOR);//getResources().getColor(R.color.black));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        this.addView(v);
    }
    public void add(String datacontent) {
        TextView tmp = new TextView(context);
        tmp.setTextColor(kernel.style.MAIN_FONT_COLOR);//getResources().getColor(R.color.black));
        tmp.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        tmp.setTypeface(Typeface.DEFAULT_BOLD);
        tmp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        try{
            //String[] words = datacontent.split(" - ");
            tmp.setText(datacontent.substring(0, datacontent.indexOf(" -")));
            content.addView(tmp);
            TextView another = new TextView(context);
            another.setTextColor(kernel.style.DISABLED_FONT_COLOR);//getResources().getColor(R.color.black));
            another.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
            another.setText(datacontent.substring(datacontent.indexOf(" - "), datacontent.length()));

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            another.setLayoutParams(params);
            content.addView(another);
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("WRONG_SYNTAX: "+datacontent);
            tmp.setText(datacontent);
            content.addView(tmp);
        }
      //  View v = new View(context);
       // v.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      //          (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics())));
       // v.setBackgroundColor(getResources().getColor(R.color.std_main));
       // content.addView(v);

    }
}
