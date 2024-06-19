package ru.mefccplusstudios.shellulspu2;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import arch.adapters.PickerAdapter;
import arch.main.Kernel;
import arch.views.DialogCore;

public class GroupPickerDialog extends DialogCore {
    //private final ArrayAdapter<String> aa;

    private final PickerAdapter pa;
    private final EditText search;
    private final ListView lv;

    private final Button[] tabs = new Button[3];
    public GroupPickerDialog(MainActivity context) {
        super(context);
        setDialogTitle("Загрузка данных...");

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
                if(word.compareTo("dev:on")==0) {
                    Toast.makeText(context, "Режим отладки включен", Toast.LENGTH_LONG).show();
                    kernel.isDebugMode = true;
                } else if(word.compareTo("dev:off")==0) {
                    Toast.makeText(context, "Режим отладки выключен", Toast.LENGTH_LONG).show();
                    kernel.isDebugMode = false;
                }
                pa.clear();
                switch(kernel.ACTIVED_TAB) {
                    case 0:
                        pa.addAll(kernel.groups);
                        for(String s: kernel.groups) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) pa.remove(s);
                        }
                        break;
                    case 1:
                        pa.addAll(kernel.prepods);
                        for(String s: kernel.prepods) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) pa.remove(s);
                        }
                        break;
                    case 2:
                        pa.addAll(kernel.auds);
                        for(String s: kernel.auds) {
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
                kernel.SAVED_PARAM = pa.getItem(position);
                kernel.FOCUS_TAB = kernel.ACTIVED_TAB;
                context.buildRaspiByParams();
            }
        });
        tabs[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kernel.ACTIVED_TAB = 0;
                updateState();
                //checkReady(0);
            }
        });
        tabs[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kernel.ACTIVED_TAB = 1;
                updateState();
                //checkReady(1);
            }
        });
        tabs[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kernel.ACTIVED_TAB = 2;
                updateState();
                //checkReady(2);
            }
        });
    }
    @Override public void styleHasBeenChanged() {
        super.styleHasBeenChanged();
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        search.setTextColor(kernel.style.MAIN_FONT_COLOR);
        search.setHintTextColor(kernel.style.DISABLED_FONT_COLOR);
        for(int q=0; q<3; q++) {
            tabs[q].setTextSize(TypedValue.COMPLEX_UNIT_SP, kernel.style.FONT_SIZE_SP);
        }
        updateState();
    }
    @Override public void show() {
        if(kernel.groups.size()<1) {
            tabs[0].setEnabled(false);
            context.loadGroupsList();
            setDialogTitle("Загрузка данных...");//tvstat.setText("Загрузка данных...");
        }
        if(kernel.auds.size()<1) {
            tabs[2].setEnabled(false);
            context.loadAudsList();
            setDialogTitle("Загрузка данных...");//tvstat.setText("Загрузка данных...");
        }
        if(kernel.prepods.size()<1) {
            tabs[1].setEnabled(false);
            context.loadPrepodsList();
            setDialogTitle("Загрузка данных...");// tvstat.setText("Загрузка данных...");
        }
        super.show();
    }
    public void checkReady(int from) {
        tabs[from].setEnabled(true);
        if(from==kernel.ACTIVED_TAB) {
            System.out.println("SUCESS FROM: "+from);
            switch(kernel.ACTIVED_TAB) {
                case 0: updateGroups(); break;
                case 1: updatePrepods(); break;
                case 2: updateRooms(); break;
            }
        }
    }
    public void updateGroups() {
        pa.clear();
        pa.addAll(kernel.groups);
        pa.notifyDataSetChanged();
        setDialogTitle("Выберите группу"); //tvstat.setText("Выберите группу:");
    }
    public void updateRooms() {
        pa.clear();
        pa.addAll(kernel.auds);
        pa.notifyDataSetChanged();
        setDialogTitle("Выберите аудиторию");//tvstat.setText("Выберите аудиторию:");
    }
    public void updatePrepods() {
        pa.clear();
        pa.addAll(kernel.prepods);
        pa.notifyDataSetChanged();
        setDialogTitle("Выберите преподавателя");//tvstat.setText("Выберите преподавателя:");
    }
    public void updateState() {
        for(int q=0; q<3; q++) {
            if(q!=kernel.ACTIVED_TAB) {
                tabs[q].setTextColor(kernel.style.DISABLED_FONT_COLOR);
                tabs[q].setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }else {
                tabs[q].setTextColor(context.getResources().getColor(R.color.white));
                tabs[q].setBackgroundColor(kernel.style.FIELD_COLOR);
            }
        }
        checkReady(kernel.ACTIVED_TAB);
    }
}
