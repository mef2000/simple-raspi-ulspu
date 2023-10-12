package arch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import arch.main.Kernel;
import ru.mefccplusstudios.shellulspu2.DateRange;
import ru.mefccplusstudios.shellulspu2.MainActivity;
import ru.mefccplusstudios.shellulspu2.R;

public class WeekAdapter extends ArrayAdapter<DateRange> {
    private final Context context;
    private final Kernel kernel;

    public WeekAdapter(MainActivity context) {
        super(context, R.layout.list_week);
        this.context = context;
        this.kernel = context.kernel;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.list_week, null);
        }
        DateRange dr = getItem(position);
        if(dr!=null) {
            TextView tv = v.findViewById(R.id.lwMain);
            tv.setText(kernel.time.buildFromScratch(position));//dr.BEGIN_DAY+"/"+(dr.BEGIN_MONTH+1)+" - "+dr.END_DAY+"/"+(dr.END_MONTH+1));
        }
        return v;
    }
}
