package com.judykong.walktap_notoolkit;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    public class TimePoint {
        public float x;
        public float y;
        public long time;
        public TimePoint(float x, float y, long time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }
    public class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            _touches.add(_currPath);
            _currPath = new ArrayList<TimePoint>();
            return true;
        }
    }
    private double TREMOR_VARIABILITY_THRESHOLD = 20;
    private static final String _password = "1680";
    private TextView _textView;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12;
    private ArrayList<ArrayList<TimePoint>> _touches = new ArrayList<ArrayList<TimePoint>>();
    private ArrayList<TimePoint> _currPath = new ArrayList<TimePoint>();
    private GestureDetectorCompat mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        registerButtons();
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        btn9 = findViewById(R.id.button9);
        btn10 = findViewById(R.id.button10);
        btn11 = findViewById(R.id.button11);
        btn12 = findViewById(R.id.button12);
        mDetector = new GestureDetectorCompat(this, new CustomGestureListener());
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long time = event.getEventTime();
        TimePoint pt = new TimePoint(x, y, time);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _currPath = new ArrayList<TimePoint>();
                _currPath.add(pt);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getHistorySize(); i++) {
                    x = event.getHistoricalX(i);
                    y = event.getHistoricalY(i);
                    time = event.getHistoricalEventTime(i);
                    pt = new TimePoint(x, y, time);
                    _currPath.add(pt);
                }
                break;
            case MotionEvent.ACTION_UP:
                _currPath.add(pt);
                break;
        }
        this.mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && _currPath.size() > 0) {
            _touches.add(_currPath);
            _currPath = new ArrayList<TimePoint>();
        }
        boolean hasTremor = getTouchVariability() > TREMOR_VARIABILITY_THRESHOLD;
        if (hasTremor) {
            double touchExtent = getTouchExtent();
            float factor;
            float bound = (float) touchExtent * 4;
            ViewGroup.LayoutParams params;
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn1.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn2.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn3.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn4.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn5.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn6.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn7.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn8.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn9.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn10.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn11.setLayoutParams(params);
            params = btn1.getLayoutParams();
            factor = Math.max(Math.max(bound / btn1.getWidth(), bound / btn1.getHeight()), 1);
            params.height = (int) (btn1.getWidth() * factor);
            btn12.setLayoutParams(params);
        }
        return super.dispatchTouchEvent(event);
    }
    public double getTouchVariability() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchVariability(tch);
        }
        return sum / n;
    }
    public double getTouchExtent() {
        if (_touches.size() == 0) { return 0; }
        double sum = 0;
        int n = this._touches.size();
        for (ArrayList<TimePoint> tch: this._touches) {
            sum += calculateTouchExtent(tch);
        }
        return sum / n;
    }
    private double distance(TimePoint start, TimePoint end) {
        return Math.sqrt(
                (start.x - end.x) * (start.x - end.x) + (start.y - end.y) * (start.y - end.y));
    }
    public double calculateTouchVariability(ArrayList<TimePoint> tch) {
        double sum = 0;
        for (int idx = 0; idx < tch.size() - 1; idx++) {
            sum += distance(tch.get(idx), tch.get(idx + 1));
        }
        return sum;
    }
    private double calculateTouchExtent(ArrayList<TimePoint> tch) {
        double maxDist = 0;
        for (TimePoint pt1: tch) {
            for (TimePoint pt2: tch) {
                double dist = distance(pt1, pt2);
                maxDist = Math.max(dist, maxDist);
            }
        }
        return maxDist;
    }
    @Override
    public void onClick(View view)
    {
        String text = (String) _textView.getText();
        if (text.equals("Enter Password") || text.equals("Wrong Password!")) {
            text = "";
        }
        switch (view.getId()) {
            case R.id.button1:
                _textView.setText(text + "1");
                break;
            case R.id.button2:
                _textView.setText(text + "2");
                break;
            case R.id.button3:
                _textView.setText(text + "3");
                break;
            case R.id.button4:
                _textView.setText(text + "4");
                break;
            case R.id.button5:
                _textView.setText(text + "5");
                break;
            case R.id.button6:
                _textView.setText(text + "6");
                break;
            case R.id.button7:
                _textView.setText(text + "7");
                break;
            case R.id.button8:
                _textView.setText(text + "8");
                break;
            case R.id.button9:
                _textView.setText(text + "9");
                break;
            case R.id.button10:
                if (text.length() > 0) {
                    _textView.setText(text.substring(0, text.length() - 1));
                }
                break;
            case R.id.button11:
                _textView.setText(text + "0");
                break;
            case R.id.button12:
                if (text.equals(_password)) {
                    startActivity(new Intent(MainActivity.this, Success.class));
                } else {
                    _textView.setText("Wrong Password!");
                }
                break;
            default:
                break;
        }
    }
    public void registerButtons() {
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(this);
        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(this);
        Button button9 = (Button) findViewById(R.id.button9);
        button9.setOnClickListener(this);
        Button button10 = (Button) findViewById(R.id.button10);
        button10.setOnClickListener(this);
        Button button11 = (Button) findViewById(R.id.button11);
        button11.setOnClickListener(this);
        Button button12 = (Button) findViewById(R.id.button12);
        button12.setOnClickListener(this);
        _textView = findViewById(R.id.textView);
    }
}