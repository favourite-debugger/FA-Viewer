package com.example.hello;

import static android.content.res.Resources.getSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CanvasActivity extends AppCompatActivity {

    boolean Animate = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);

        List<String> alphabet = new ArrayList<>();
        ArrayList<String> finalStates = new ArrayList<>();
        ArrayList<Map<String, String>> state_info;
        int num_of_states, startState;

        Intent intent = getIntent();
        num_of_states =  intent.getIntExtra(MainActivity.NUM_OF_STATES, 1);
        startState = intent.getIntExtra(MainActivity.strStartState, 0);

        state_info = (ArrayList<Map<String, String>>) intent.getSerializableExtra(MainActivity.STATE_INFO);
        alphabet = (List<String>) intent.getSerializableExtra(MainActivity.ALPHABET);
        finalStates = (ArrayList<String>) intent.getSerializableExtra(MainActivity.strFinalStates);


        setContentView(R.layout.activity_main2);
        FrameLayout main = (FrameLayout) findViewById(R.id.canvas_layout);

        // ADD BUTTONS AND TEXT FIELD TO CANVAS
         //EDIT TEXT FOR USER TO ENTER INPUT STRING
        EditText editTxt_enterString = new EditText(this);
        editTxt_enterString.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editTxt_enterString.setHintTextColor(Color.BLACK);
        editTxt_enterString.setTextColor(Color.BLACK);
       // editTxt_enterString.setBackgroundColor(Color.parseColor("#e31078"));
        editTxt_enterString.setHint("Enter String");


        TextView textView_inputString = new TextView(this);
        textView_inputString.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        textView_inputString.setTextSize(40);
        //textView_inputString.setBackgroundColor(Color.parseColor("#e31078"));
        textView_inputString.setTextColor(Color.parseColor("#000000"));
        textView_inputString.setText("Enter String", TextView.BufferType.SPANNABLE);

        Button btn_startAnimation = new Button(this);
        btn_startAnimation.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        btn_startAnimation.setText("Start Animation");

        Button btn_instructions = new Button(this);
        btn_instructions.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        btn_instructions.setText("Instructions");


 // horizontal linear layout to hold text for start state and state state view text
        LinearLayout horizontal1 = new LinearLayout(this);
        horizontal1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        horizontal1.setOrientation(LinearLayout.HORIZONTAL);
      //  horizontal1.setBackgroundColor(Color.parseColor("#e31078"));
        horizontal1.addView(editTxt_enterString);
        horizontal1.addView(textView_inputString);

        // USE VERTICAL LAYOUT TO ADD THEM ONE BELOW THE OTHER
        LinearLayout vertical = new LinearLayout(this);
        vertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        vertical.setOrientation(LinearLayout.VERTICAL);
        vertical.addView(horizontal1);

        LinearLayout horizontal2 = new LinearLayout(this);
        horizontal2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        horizontal2.setOrientation(LinearLayout.HORIZONTAL);
        horizontal2.addView(btn_instructions);
        horizontal2.addView(btn_startAnimation);
        //horizontal2.addView(btn_startAnimation);
        vertical.addView(horizontal2);

        main.addView(new MyView(this, num_of_states, state_info, alphabet, startState, finalStates, textView_inputString));
        main.addView(vertical);

        List<String> finalAlphabet = alphabet;

        btn_startAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean string_valid = true;
                String[] string = editTxt_enterString.getText().toString().replaceAll(" ", "").split("");
                String user_input = editTxt_enterString.getText().toString().replaceAll(" ", "");
                //finalStates.clear();
                for(int i = 0; i < user_input.length(); i++){
                    if(!finalAlphabet.contains(String.valueOf(user_input.charAt(i)))){
                        string_valid = false;
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Input String", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }

                }

                if(string_valid){
//                    btn_startAnimation.setVisibility(View.VISIBLE);
                    Animate = true;
                    textView_inputString.setText(editTxt_enterString.getText().toString());

                }
            }
        });

        btn_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CanvasActivity.this, InstructionsPage.class);
                startActivity(intent);
            }
        });


    }

    @SuppressLint("AppCompatCustomView")
    public class MyView extends ImageView{

        private int num_of_states, startState;

        private float x_pos= 0, y_pos= 0;
//        private int init_x = 300, init_y = -100;
//        private PointF pointF = new PointF(300, 100);
        private float stateHeight, stateWidth;
        private Paint paint;


        private PointF[] pointFS;

        private boolean touched = false;
        private Drawable[] states;
        private String[] colors;

        private ArrayList<Map<String, String>> stateInfo = new ArrayList<Map<String,String>>();
        private List<String> Alphabet;
        private ArrayList<String> finalStates;

        boolean animate = false;
        TextView userString;

        int count = 1;
        int times = 0;
        int current_index, last_index;
        int string_index = 0;

        public MyView(Context context, int n, ArrayList<Map<String, String>> arrayList, List<String> alphabet, int SState, ArrayList<String> fStates, TextView textView) {
            super(context);
            num_of_states = n;
            startState = SState;
            finalStates = fStates;
            stateInfo = arrayList;
            Alphabet = alphabet;

            pointFS = new PointF[num_of_states];
            spawnStatesRandomly();
            states = new Drawable[num_of_states];


            userString = textView;

            colors = new String[num_of_states];
            resetColors();


            last_index = startState;
            current_index = startState;

            Drawable state = getResources().getDrawable(R.drawable.state);
            stateHeight = state.getIntrinsicHeight();
            stateWidth = state.getIntrinsicWidth();

            if(num_of_states > 0){
                for(int i = 0; i < num_of_states; i++){
                    states[i] = getResources().getDrawable(R.drawable.state);
                    states[i].setColorFilter(Color.parseColor(colors[i]), PorterDuff.Mode.MULTIPLY);
                }
            }

            paint = new Paint();

        }

        private void resetColors(){
            for(int i = 0; i < num_of_states; i++){
                colors[i] = "#FFFFFF";
            }

            colors[startState] = "#6A006A";
            for(int i = 0; i < finalStates.size(); i++){
                colors[Integer.parseInt(finalStates.get(i))] = "#32E600";
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint); //background

            paint.setColor(Color.YELLOW);
            paint.setTextSize(100);
            if(num_of_states > 0){
                for(int i = 0; i < num_of_states; i++){
                    states[i].setColorFilter(Color.parseColor(colors[i]), PorterDuff.Mode.MULTIPLY);
                    states[i].setBounds((int) (pointFS[i].x-(stateWidth/2)), (int) (pointFS[i].y-(stateWidth/2)), (int)(pointFS[i].x+(stateWidth/2)), (int) (pointFS[i].y+(stateWidth/2)));
                    states[i].draw(canvas);
                    canvas.drawText("q"+Integer.toString(i), pointFS[i].x-50, pointFS[i].y+10, paint); //state name e.g q0, q1
                }
            }


            paint.setStrokeWidth(5);
            paint.setColor(Color.BLUE);
            int toDraw;
            for(int i = 0; i < stateInfo.size(); i++){
                for(int j = 0; j < Alphabet.size(); j++){
                    toDraw = Integer.parseInt(String.valueOf(stateInfo.get(i).get(Alphabet.get(j)).charAt(1)));
                    drawArrow(canvas, pointFS[i],  pointFS[toDraw], paint, Alphabet.get(j), last_index, current_index);
                }
            }

            if(Animate){
                String orig_string = userString.getText().toString().replaceAll(" ", "");
                if(times < orig_string.length()){
                    current_index = Integer.parseInt(String.valueOf(stateInfo.get(last_index).get(String.valueOf(orig_string.charAt(string_index))).charAt(1)));
                    colors[current_index] = "#FF0000";
                    Spannable s = (Spannable)userString.getText();
                    s.setSpan(new ForegroundColorSpan(Color.RED), string_index, string_index+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (count % 200 == 0){ //after counting 200 move to next state
                        userString.setText(orig_string, TextView.BufferType.SPANNABLE);
                        resetColors();
                        last_index = current_index;
                        string_index++;
                        times++;
                    }

                }
                else{
                    if(!finalStates.contains(Integer.toString(current_index))){
                        Toast toast = Toast.makeText(getApplicationContext(), "STRING NOT ACCEPTED", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "STRING ACCEPTED", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    Animate = false;
                    current_index = startState;
                    string_index = 0;
                    count = 1;
                    times = 0;
                    last_index = startState;
                }
                count ++;

            }

            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // point of touch
            x_pos = event.getX();
            y_pos = event.getY();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    for(int i = 0; i < num_of_states; i++){
                        if(is_nearby(pointFS[i], x_pos, y_pos)){
                            pointFS[i].set(x_pos, y_pos);
                            i = num_of_states; //end loop (idea is to move only one state per time even if 2 states are at the same position)
                        }
                    }
                    invalidate();
                    break;
            }
            return true;
        }



        private int getRandomValue(int min, int max)  //get random value between in range (min, max)
        {
            return (int)(Math.random()*(max-min+1)+min);
        }

        private void spawnStatesRandomly(){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
            if(num_of_states > 0) {
                for (int s = 0; s < num_of_states; s++) {
                    pointFS[s] = new PointF();
                    pointFS[s].x = getRandomValue(200, width - 500);
                    pointFS[s].y = getRandomValue(200, height - 500);
                }
            }
        }

        private boolean is_nearby(PointF p, float x, float y){  // check a point is nearby another point
            double distance = Math.sqrt(Math.pow(p.x-x, 2.0) + Math.pow(p.y-y, 2.0));
            if(distance < 100.0){ //state is close to touch
                return true;
            }
            return false;
        }

        // from http://www.java2s.com/example/android/graphics/draw-arrow-on-canvas.html  // although modified
        private void drawArrow(Canvas canvas, PointF point1,
                                     PointF point2, Paint paint, String stateName, int last_index, int curr_index) {
            float x1, x2;
            Path path = new Path();
            paint.setTextSize(50);
            paint.setStyle(Paint.Style.STROKE);

            if(Animate && point1.x == pointFS[last_index].x && point1.y == pointFS[last_index].y && point2.x == pointFS[curr_index].x && point2.y == pointFS[curr_index].y){
                paint.setColor(Color.RED);
            }

            float y1 = point1.y;
            float y2 = point2.y;

            int xoffset = 0;
            int yoffset = 0;

            if(point1.x == point2.x && point1.y  == point2.y){ //pointing to self
                path.moveTo(point1.x+(stateWidth/2), point1.y);
                path.cubicTo(point1.x+(stateWidth/4), point1.y + stateHeight + (stateHeight/2), point1.x-((stateWidth/2)+(stateWidth/4)) , point1.y+stateHeight+(stateHeight/2), point1.x-(stateWidth/2), point1.y);
                canvas.drawPath(path, paint);
                canvas.drawTextOnPath(stateName, path, 160, 0, paint);

                double t = 0.5F;
                double x = ((1-t)*(1-t)*(1-t)*(point1.x+(stateWidth/2))) + (3*(1-t)*(1-t)*t*(point1.x+(stateWidth/4))) + (3*(1-t)*t*t*(point1.x-((stateWidth/2)+(stateWidth/4))) + t*t*t*(point1.x-(stateWidth/4)));
                double y = ((1-t)*(1-t)*(1-t)*(point1.y)) + (3*(1-t)*(1-t)*t*(point1.y + stateHeight + (stateHeight/2))) + (3*(1-t)*t*t*(point1.y+stateHeight + (stateHeight/2)) + t*t*t*(point1.y));

                x1 = (float) x;
                y2 = (float) y;
                y1 = (float) y;
                x2 = (float) x;
            }

            else if(point1.x > point2.x) { //point 1 is rightwards of point 2
                x1 = point1.x - stateWidth/2;
                x2 = point2.x + stateWidth/2;

                path.moveTo(x1, y1);
                if(y1 > y2){
                    path.quadTo(x1-stateWidth, y1 - stateHeight , x2, y2); //((y1-y2)/2)/2  x2 + ((x1-x2)/2)

                    double t = 0.6F;
                    double x = ((1 - t) * (1 - t) * x1) + (2 * (1 - t) * t * (x1-stateWidth)) + (t * t * x2);
                    double y = ((1 - t) * (1 - t) * y1) + (2 * (1 - t) * t * (y1-stateHeight)) + (t * t * y2);
                    x2 = (float) x;
                    y2 = (float) y;
                }
                else{
                    path.quadTo(x1-stateWidth, y2 -stateHeight, x2, y2); //((y2-y1)/2)/2   x2 + ((x1-x2)/2)
                    double t = 0.6F;
                    double x = ((1 - t) * (1 - t) * x1) + (2 * (1 - t) * t * (x1-stateWidth)) + (t * t * x2);
                    double y = ((1 - t) * (1 - t) * y1) + (2 * (1 - t) * t * (y2-stateHeight)) + (t * t * y2);
                    x2 = (float) x;
                    y2 = (float) y;
                }

                canvas.drawPath(path, paint);
                canvas.drawTextOnPath(stateName, path, 90, 0, paint);

            }
            else{ //point 1 is leftwards of point 2

                if(point1.y < (point2.y-stateHeight)){
                    y1 = point1.y + stateHeight/2;
                    y2 = point2.y- stateHeight/2;
                    x1 = point1.x;
                    x2 = point2.x;
                }
                else if(point1.y > (point2.y + stateHeight)){
                    y1 = point1.y;
                    y2 = point2.y+ stateHeight/2;
                    x1 = point1.x+ stateWidth/2;
                    x2 = point2.x;
                }
                else {
                    x1 = point1.x + stateWidth/2;
                    x2 = point2.x - stateWidth/2;

                }

                xoffset = (int) ((x2 - x1)/4);
                yoffset = (int) ((y2 - y1)/4);
                canvas.drawLine(x1, y1, x2, y2, paint);
                drawLetter(canvas, new PointF(x1,y1), new PointF(x2, y2), paint, stateName);
            }

            x2  = x2 - xoffset;
            y2 = y2 - yoffset;
            float dx = (x2 - x1);
            float dy = (y2 - y1);
            float rad = (float) Math.atan2(dy, dx);

            //draw arrowhead
            canvas.drawLine(x2, y2,//from   w w  w .ja v  a2 s.c om
                    (float) (x2 + Math.cos(rad + Math.PI * 0.75) * 20),
                    (float) (y2 + Math.sin(rad + Math.PI * 0.75) * 20),
                    paint);
            canvas.drawLine(x2, y2,
                    (float) (x2 + Math.cos(rad - Math.PI * 0.75) * 20),
                    (float) (y2 + Math.sin(rad - Math.PI * 0.75) * 20),
                    paint);

            paint.setColor(Color.BLUE);
        }

        private void drawLetter(Canvas canvas, PointF point1, PointF point2, Paint paint, String text){
            float x_pos, y_pos;
            if(point1.x > point2.x){ //if point 1 is to the right of point 2
                x_pos = point2.x + ((point1.x - point2.x)/2);
            }
            else{
                x_pos = point1.x + ((point2.x - point1.x)/2);
            }

            if(point1.y > point2.y){ //if point 1 is lower than point 2
                y_pos = point2.y + ((point1.y - point2.y)/2);
            }
            else{
                y_pos = point1.y + ((point2.y - point1.y)/2);
            }

            canvas.drawText(text, x_pos, y_pos, paint);

        }

    }

}

