package abs.core;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.mefccplusstudios.shellulspu2.R;

public class PickerAdapter extends ArrayAdapter<String> {
    public PickerAdapter(Context context) {
        super(context, R.layout.grou_item);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.grou_item, null);
        }
        String posi = getItem(position);
        TextView tv = v.findViewById(R.id.lwGroup);
        tv.setText(posi);
        tv.setTextColor(Bus.style.FONT_COLOR);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);

        return v;
    }
}
