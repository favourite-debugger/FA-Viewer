package com.example.hello;

import static com.example.hello.R.id.btn_ins;
import static com.example.hello.R.id.editTxtAlphabet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String NUM_OF_STATES = "com.example.hello.NUM_OF_STATES";
    public static final String STATE_INFO = "com.example.hello.STATE_INFO";
    public static final String ALPHABET = "com.example.hello.ALPHABET";
    public static final String MAP = "com.example.hello.MAP";
    public static final String strStartState = "com.example.hello.START_STATE";
    public static final String strFinalStates = "com.example.hello.FINAL_STATES";

    ///////////////// DECLARE VARIABLES ///////////////////////////////////////////

    private final int total_possible_states = 10;  //number of states allowed by application
    private int numOfStates = 0;                   //number of states chosen by user (initialised to zero)
    private int startState = -1; //default to invalid start state

    // create Integer type arraylist
    ArrayList<String> finalStates = new ArrayList<>();
    List<String> Alphabet = new ArrayList<>();


    private int total_rows;

    private LinearLayout matrix;
    private ScrollView scrollView;
    private Spinner spinner_select_num_states;
    private Spinner[] spinners; //all the spinners used in summary table

    //for horizontal linear layouts(serving as rows in summary table)
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
    );

    //for views
    private LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1
    );

    // FOR STORING INFORMATION ABOUT THE FINITE AUTOMATA - MAPS STATE TO STATE (EG q0 -> q1 under input 'a') although the input is not stored in this
    // arraylist we follow order of the ALPHABET (eg a,b) as entered by the user to determine what input symbol is determining the transition
    private ArrayList<Map<String, String>> stateInfo = new ArrayList<Map<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_ins).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Example.class);
                startActivity(intent);
            }
        });

        spinner_select_num_states = findViewById(R.id.spinner_num_of_states); //spinner
        String[] possible_states = new String[total_possible_states];
        for(int s = 0; s < possible_states.length;s++){
            possible_states[s] = Integer.toString(s+1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("Select number of states from this dropdown");
        adapter.addAll(possible_states);
        spinner_select_num_states.setAdapter(adapter);


        matrix = findViewById(R.id.matrix); //linearlayout on top of scroll view

       // EditText editTxt_num_of_states = findViewById(R.id.editTxtNumOfStates);
        EditText editTxt_alphabet = findViewById(R.id.editTxtAlphabet);
        EditText editTxt_final_states = findViewById(R.id.editTxtFinalState);
        EditText editTxt_start_state = findViewById(R.id.editTxtStartState);


        Button btn_save = findViewById(R.id.btn_save); //enter_fa
        Button btn_cancel = findViewById(R.id.btn_cancel);
        Button btn_save_table = findViewById(R.id.btn_save_table);
        Button btn_canvas = findViewById(R.id.btn_canvas);

        btn_save.setOnClickListener(new View.OnClickListener() {  // button to save FA definition
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {


                numOfStates = 0;
                startState = -1;

                if(spinner_select_num_states.getSelectedItem().toString().chars().allMatch(Character::isDigit)){
                    numOfStates = Integer.parseInt(spinner_select_num_states.getSelectedItem().toString());
                }

                Alphabet = Arrays.asList(editTxt_alphabet.getText().toString().replaceAll(" ", "").split(","));
                if(!editTxt_start_state.getText().toString().equals("")){
                    startState = Integer.parseInt(editTxt_start_state.getText().toString());
                }

                String final_state = editTxt_final_states.getText().toString();
                finalStates.clear();
                for(int i = 0; i < final_state.length(); i++){
                    finalStates.add(String.valueOf(final_state.charAt(i)));
                }
                if(is_valid_FA_input()){
                    total_rows = Alphabet.size() * numOfStates;

                    //present views should be invisible
                    spinner_select_num_states.setVisibility(View.GONE);
                    editTxt_alphabet.setVisibility(View.GONE);
                    editTxt_final_states.setVisibility(View.GONE);
                    editTxt_start_state.setVisibility(View.GONE);
                    findViewById(btn_ins).setVisibility(View.GONE);


                    create_summary_table();
                    btn_save.setVisibility(View.GONE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_canvas.setVisibility(View.VISIBLE);

                }


            }
        });

        scrollView = findViewById(R.id.scrollview);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_canvas.setVisibility(View.GONE);
                matrix.removeViews(4, total_rows+1); // remove transition table basically

                spinner_select_num_states.setVisibility(View.VISIBLE);
                editTxt_alphabet.setVisibility(View.VISIBLE);
                editTxt_final_states.setVisibility(View.VISIBLE);
                editTxt_start_state.setVisibility(View.VISIBLE);

                btn_cancel.setVisibility(View.GONE);
                btn_save_table.setVisibility(View.GONE);
                btn_save.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_ins).setVisibility(View.VISIBLE);

            }
        });


        btn_canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populate_stateInfo_Map();

                if(numOfStates == 0){ // just in case
                    numOfStates = 1;
                }

                Intent intent = new Intent(MainActivity.this, CanvasActivity.class);
                intent.putExtra(NUM_OF_STATES, numOfStates);
                intent.putExtra(strStartState, startState);
                intent.putExtra(strFinalStates, (Serializable) finalStates);
                intent.putExtra(STATE_INFO, stateInfo);
                intent.putExtra(ALPHABET, (Serializable) Alphabet);
                startActivity(intent);
            }
        });
    }

    protected Boolean is_valid_FA_input(){
        if(startState >= total_possible_states || startState < 0 || startState >= numOfStates){
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Start State or number of states", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        if(Alphabet.size() < 2){
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid entry for alphabet", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        for(int i = 0; i < Alphabet.size(); i++){
            if(Alphabet.get(i).length()>1){
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Alphabet Entry", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        }

        if(finalStates.size() < 1){
            Toast toast = Toast.makeText(getApplicationContext(), "Enter empty fields", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        for(int i = 0; i < finalStates.size(); i++){
//            Toast toast1 = Toast.makeText(getApplicationContext(), String.valueOf(finalStates.get(i)), Toast.LENGTH_LONG);
//            toast1.show();
            if(finalStates.get(i).length()>1 || Integer.parseInt(finalStates.get(i)) >= numOfStates){  //change this if number of possible states increases from 10
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Final States Entry", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        }

        return true;
    }

    protected void create_summary_table(){  // create transition table
        TextView textView1 = new TextView(MainActivity.this);
        textView1.setTextColor(Color.WHITE);
        textView1.setText("Current State");
        textView1.setHeight(100);
        textView1.setLayoutParams(params2);

        TextView textView2 = new TextView(MainActivity.this);
        textView2.setTextColor(Color.WHITE);
        textView2.setText("Input Symbol");
        textView1.setHeight(100);
        textView2.setLayoutParams(params2);

        TextView textView3 = new TextView(MainActivity.this);
        textView3.setTextColor(Color.WHITE);
        textView3.setText("Resulting State");
        textView3.setHeight(100);
        textView3.setLayoutParams(params2);


        LinearLayout l1 = new LinearLayout(MainActivity.this);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        l1.setLayoutParams(params);
        l1.setBackgroundColor(Color.parseColor("blue"));
        l1.addView(textView1);
        l1.addView(textView2);
        l1.addView(textView3);

        matrix.addView(l1);

        // arrays to hold dynamically created layouts(rows), textviews, editTexts
        TextView[] views = new TextView[total_rows*2];
        LinearLayout[] ls = new LinearLayout[total_rows];
        EditText[] edits = new EditText[total_rows*2];
        spinners = new Spinner[total_rows*2];

        List<String> states = new ArrayList<String>();
        for(int s = 0; s < numOfStates; s++){
            states.add("q"+Integer.toString(s));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, states);

        //integer values to control indexing into the above arrays
        int view_index = 0;
        int row_index = 0;
        int spinner_index = 0;

        //for all states populate summary table with alphabets and input EditTexts
        for(int i = 0; i < numOfStates; i++){
            for(int j = 0; j < Alphabet.size(); j++){
                ls[row_index] = new LinearLayout(MainActivity.this);
                ls[row_index].setOrientation(LinearLayout.HORIZONTAL);
                ls[row_index].setLayoutParams(params);

                views[view_index] = new TextView(MainActivity.this);
                views[view_index].setLayoutParams(params2);
                views[view_index].setHeight(100);
                views[view_index].setText("q"+Integer.toString(i));

                ls[row_index].addView(views[view_index]);
                view_index++;

                views[view_index] = new TextView(MainActivity.this);
                views[view_index].setLayoutParams(params2);
                views[view_index].setText(Alphabet.get(j));

                ls[row_index].addView(views[view_index]);
                view_index++;

                spinners[spinner_index] = new Spinner(MainActivity.this);
                spinners[spinner_index].setPadding(0,0,0,0);
                spinners[spinner_index].setAdapter(dataAdapter);
                int finalSpinner_index = spinner_index;
                spinners[spinner_index].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinners[finalSpinner_index].setBackgroundColor(Color.parseColor("red"));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                ls[row_index].addView(spinners[spinner_index]);
                spinner_index++;

                matrix.addView(ls[row_index]);
                row_index++;

            }

        }
    }

    protected void populate_stateInfo_Map(){  // from transition table and FA definition; populate the state information map
        stateInfo.clear();
        int index = 0; //for iteratively moving through spinners
        for(int i = 0; i < numOfStates; i++){
            stateInfo.add(new HashMap<String,String>());
            for (String s : Alphabet) {
                stateInfo.get(i).put(s, spinners[index].getSelectedItem().toString());
                index++;
            }
        }
    }


}
