package ru.mefccplusstudios.shellulspu2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import abs.core.Window;
import abs.core.Bus;
import abs.core.PickerAdapter;

public class GroupPickerDialog extends Window {
    private final PickerAdapter pa;
    private final EditText search;
    private final ListView lv;

    private final Button[] tabs = new Button[3];
    public GroupPickerDialog(Context context) {
        super(context);
        setWinTitle(context.getString(R.string.retdata));

        LayoutInflater lif = LayoutInflater.from(context);
        View v = lif.inflate(R.layout.group_picker, null);
        content.addView(v);

        lv = v.findViewById(R.id.lview);
        search = v.findViewById(R.id.Searcher);
        tabs[0] = v.findViewById(R.id.tab1);
        tabs[1] = v.findViewById(R.id.tab2);
        tabs[2] = v.findViewById(R.id.tab3);
        //aa = new ArrayAdapter<>(context, R.layout.grou_item, R.id.lwGroup, new ArrayList<String>());
        pa = new PickerAdapter(context);
        lv.setAdapter(pa);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = search.getText().toString();
                pa.clear();
                switch(Bus.data.ACTIVED_TAB) {
                    case 0:
                        pa.addAll(Bus.data.GROUPS);
                        for(String s: Bus.data.GROUPS) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) pa.remove(s);
                        }
                        break;
                    case 1:
                        pa.addAll(Bus.data.TEACHERS);
                        for(String s: Bus.data.TEACHERS) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) pa.remove(s);
                        }
                        break;
                    case 2:
                        pa.addAll(Bus.data.ROOMS);
                        for(String s: Bus.data.ROOMS) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) pa.remove(s);
                        }
                        break;
                }
                pa.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GroupPickerDialog.this.dismiss();
                Bus.data.SEARCH = pa.getItem(position);
                Bus.data.FOCUS_TAB = Bus.data.ACTIVED_TAB;
                Bus.event("LOAD_FOR_DATA", null);
            }
        });
        tabs[0].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Bus.data.ACTIVED_TAB = 0;
                updateState();
                //checkReady(0);
            }
        });
        tabs[1].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Bus.data.ACTIVED_TAB = 1;
                updateState();
                //checkReady(1);
            }
        });
        tabs[2].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Bus.data.ACTIVED_TAB = 2;
                updateState();
                //checkReady(2);
            }
        });
    }
    @Override public void event(String tag, Object packet) {
        super.event(tag, packet);
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        search.setTextColor(Bus.style.FONT_COLOR);
        search.setHintTextColor(Bus.style.SUBFONT_COLOR);
        for(int q=0; q<3; q++) {
            tabs[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, Bus.style.FONT_SIZE_SP);
        }
        updateState();
    }

    @Override public void show() {
        super.show();
        updateState();
    }
    public void checkReady(int from) {
        tabs[from].setEnabled(true);
        if(from == Bus.data.ACTIVED_TAB) {
            System.out.println("SUCESS FROM: "+from);
            switch(Bus.data.ACTIVED_TAB) {
                case 0: updateGroups(); break;
                case 1: updatePrepods(); break;
                case 2: updateRooms(); break;
            }
        }
    }

    public void updateGroups() {
        pa.clear();
        pa.addAll(Bus.data.GROUPS);
        pa.notifyDataSetChanged();
        setWinTitle("Выберите группу"); //tvstat.setText("Выберите группу:");
    }
    public void updateRooms() {
        pa.clear();
        pa.addAll(Bus.data.ROOMS);
        pa.notifyDataSetChanged();
        setWinTitle("Выберите аудиторию");//tvstat.setText("Выберите аудиторию:");
    }
    public void updatePrepods() {
        pa.clear();
        pa.addAll(Bus.data.TEACHERS);
        pa.notifyDataSetChanged();
        setWinTitle("Выберите преподавателя");//tvstat.setText("Выберите преподавателя:");
    }
    public void updateState() {
        for(int q=0; q<3; q++) {
            if(q!=Bus.data.ACTIVED_TAB) {
                tabs[q].setTextColor(Bus.style.SUBFONT_COLOR);
                tabs[q].setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            }else {
                tabs[q].setTextColor(getContext().getResources().getColor(R.color.white));
                tabs[q].setBackgroundColor(Bus.style.MAIN_COLOR);
            }
        }
        checkReady(Bus.data.ACTIVED_TAB);
    }
}
