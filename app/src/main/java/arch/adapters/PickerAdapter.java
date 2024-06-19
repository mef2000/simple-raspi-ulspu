package arch.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import arch.main.Kernel;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class PickerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final Kernel kernel;

    public PickerAdapter(MainActivity context) {
        super(context, R.layout.grou_item);
        this.context = context;
        this.kernel = context.kernel;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.grou_item, null);
        }
        String posi = getItem(position);
        TextView tv = v.findViewById(R.id.lwGroup);
        tv.setText(posi);//dr.BEGIN_DAY+"/"+(dr.BEGIN_MONTH+1)+" - "+dr.END_DAY+"/"+(dr.END_MONTH+1));
        tv.setTextColor(kernel.style.MAIN_FONT_COLOR);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);

        return v;
    }
}
