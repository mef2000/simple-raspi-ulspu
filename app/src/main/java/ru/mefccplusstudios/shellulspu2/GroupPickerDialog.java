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
import arch.main.Kernel;

public class GroupPickerDialog extends Dialog {
    private final MainActivity context;
    private final Kernel kernel;
    private final ArrayAdapter<String> aa;
    private final EditText search;
    private final ListView lv;
    private final TextView tvstat;

    private final Button[] tabs = new Button[3];
    public GroupPickerDialog(MainActivity context) {
        super(context);
        this.context = context;
        this.kernel = context.kernel;
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
                    kernel.isDebugMode = true;
                } else if(word.compareTo("dev:off")==0) {
                    Toast.makeText(context, "Режим отладки выключен", Toast.LENGTH_LONG).show();
                    kernel.isDebugMode = false;
                }
                aa.clear();
                switch(kernel.ACTIVED_TAB) {
                    case 0:
                        aa.addAll(kernel.groups);
                        for(String s: kernel.groups) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) aa.remove(s);
                        }
                        break;
                    case 1:
                        aa.addAll(kernel.prepods);
                        for(String s: kernel.prepods) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) aa.remove(s);
                        }
                        break;
                    case 2:
                        aa.addAll(kernel.auds);
                        for(String s: kernel.auds) {
                            if(!s.toUpperCase().contains(search.getText().toString().toUpperCase())) aa.remove(s);
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
                kernel.SAVED_PARAM = aa.getItem(position);
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
    @Override public void show() {
        if(kernel.groups.size()<1) {
            tabs[0].setEnabled(false);
            context.loadGroupsList();
            tvstat.setText("Загрузка данных...");
        }
        if(kernel.auds.size()<1) {
            tabs[2].setEnabled(false);
            context.loadAudsList();
            tvstat.setText("Загрузка данных...");
        }
        if(kernel.prepods.size()<1) {
            tabs[1].setEnabled(false);
            context.loadPrepodsList();
            tvstat.setText("Загрузка данных...");
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
        aa.clear();
        aa.addAll(kernel.groups);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите группу:");
    }
    public void updateRooms() {
        aa.clear();
        aa.addAll(kernel.auds);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите аудиторию:");
    }
    public void updatePrepods() {
        aa.clear();
        aa.addAll(kernel.prepods);
        aa.notifyDataSetChanged();
        tvstat.setText("Выберите преподавателя:");
    }
    public void updateState() {
        for(int q=0; q<3; q++) {
            if(q!=kernel.ACTIVED_TAB) {
                tabs[q].setTextColor(context.getResources().getColor(R.color.std_main));
                tabs[q].setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }else {
                tabs[q].setTextColor(context.getResources().getColor(R.color.white));
                tabs[q].setBackgroundColor(context.getResources().getColor(R.color.std_main));
            }
        }
        checkReady(kernel.ACTIVED_TAB);
    }
}
