package ru.mefccplusstudios.shellulspu2;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
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

import arch.main.Data;

public class GroupPickerDialog extends Dialog {
    private final MainActivity context;
    private final ArrayAdapter<String> aa;
    private final EditText search;
    private final ListView lv;
    private final TextView tvstat;

    private final Button[] tabs = new Button[3];
    public GroupPickerDialog(MainActivity context) {
        super(context);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.group_picker);
        this.setCancelable(true);
        this.getWindow().getAttributes().windowAnimations = R.style.animationChooser;
        this.getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        lv = findViewById(R.id.lview);
        search = findViewById(R.id.Searcher);
        tvstat = findViewById(R.id.statusGroup);
        tabs[0] = findViewById(R.id.tab1);
        tabs[1] = findViewById(R.id.tab2);
        tabs[2] = findViewById(R.id.tab3);
        aa = new ArrayAdapter<>(context, R.layout.grou_item, R.id.lwGroup, new ArrayList<String>());
        lv.setAdapter(aa);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = search.getText().toString();
                if(word.compareTo("dev:on")==0) {
                    Toast.makeText(context, "Режим отладки включен", Toast.LENGTH_LONG).show();
                    Data.isDebugMode = true;
                } else if(word.compareTo("dev:off")==0) {
                    Toast.makeText(context, "Режим отладки выключен", Toast.LENGTH_LONG).show();
                    Data.isDebugMode = false;
                }
                aa.clear();
                switch(Data.PACTIVED_TAB) {
                    case 0:
                        aa.addAll(Data.groups);
                        for(String s: Data.groups) {
                            if(!s.startsWith(search.getText().toString())) aa.remove(s);
                        }
                        break;
                    case 1:
                        aa.addAll(Data.prepods);
                        for(String s: Data.prepods) {
                            if(!s.contains(search.getText().toString())) aa.remove(s);
                        }
                        break;
                    case 2:
                        aa.addAll(Data.auds);
                        for(String s: Data.auds) {
                            if(!s.startsWith(search.getText().toString())) aa.remove(s);
                        }
                        break;
                }
                aa.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GroupPickerDialog.this.dismiss();
                Data.SAVED_GROUP = aa.getItem(position);
                Data.ACTIVED_TAB = Data.PACTIVED_TAB;
                context.buildRaspiByGroups();
            }
        });
        tabs[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.PACTIVED_TAB = 0;
                updateState();
                checkReady(0);
            }
        });
        tabs[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.PACTIVED_TAB = 1;
                updateState();
                checkReady(1);
            }
        });
        tabs[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.PACTIVED_TAB = 2;
                updateState();
                checkReady(2);
            }
        });
    }
    @Override public void show() {
        if(Data.groups.size()<1) {
            tabs[0].setEnabled(false);
            context.loadGroupsList();
            tvstat.setText("Загрузка данных...");
        }
        if(Data.auds.size()<1) {
            tabs[2].setEnabled(false);
            context.loadAudsList();
            tvstat.setText("Загрузка данных...");
        }
        if(Data.prepods.size()<1) {
            tabs[1].setEnabled(false);
            context.loadPrepodsList();
            tvstat.setText("Загрузка данных...");
        }
        super.show();
    }
    public void checkReady(int from) {
        tabs[from].setEnabled(true);
        if(from==Data.PACTIVED_TAB) {
            System.out.println("SUCESS FROM: "+from);
            switch(Data.PACTIVED_TAB) {
                case 0: updateGroups(); break;
                case 1: updatePrepods(); break;
                case 2: updateRooms(); break;
            }
        }
    }
    public void updateGroups() {
        aa.clear();
        aa.addAll(Data.groups);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите группу:");
    }
    public void updateRooms() {
        aa.clear();
        aa.addAll(Data.auds);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите аудиторию:");
    }
    public void updatePrepods() {
        aa.clear();
        aa.addAll(Data.prepods);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите преподавателя:");
    }
    public void updateState() {
        for(int q=0; q<3; q++) {
            if(q!=Data.PACTIVED_TAB) {
                tabs[q].setTextColor(context.getResources().getColor(R.color.std_main));
                tabs[q].setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }else {
                tabs[q].setTextColor(context.getResources().getColor(R.color.white));
                tabs[q].setBackgroundColor(context.getResources().getColor(R.color.std_main));
            }
        }
    }
}
