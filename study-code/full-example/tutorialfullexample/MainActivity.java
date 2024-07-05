package com.example.tutorialstartercode;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;

public class MainActivity extends ABDMTActivity // TODO #0: Change this to ABDMTActivity
        implements View.OnClickListener {

    // TODO #1: Add ABD-MT required instance variables here
    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private GestureObserver _gestureObserver;
    private ActivityObserver _activityObserver;
    private AttentionObserver _attentionObserver;
    private AbilityModeler _abilityModeler;
    private UIAdapter _uiAdapter;

    private TextView _textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Android app initialization
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // TODO #2: Add ABD-MT initialization here
        setContext(this);
        setRoot(findViewById(R.id.root1));
        _abdmt = ABDMT.getInstance();
        _touchObserver = _abdmt.getTouchObserver();
        _gestureObserver = _abdmt.getGestureObserver();
        _activityObserver = _abdmt.getActivityObserver();
        _attentionObserver = _abdmt.getAttentionObserver();
        _abilityModeler = _abdmt.getAbilityModeler();
        _uiAdapter = _abdmt.getUiAdapter();
        _abdmt.startObserver(ObserverFlags.TOUCH);
        _abdmt.startObserver(ObserverFlags.GESTURE);
        _abdmt.startObserver(ObserverFlags.ACTIVITY);
        _abdmt.startObserver(ObserverFlags.ATTENTION);
        _uiAdapter.registerWidgets(getRoot());

        // You can add other initialization you need here
        addButtonOnClickListeners();
    }

    @Override
    // Example Code: Adjusting widget sizes to be larger if a user has tremor
    // This is called whenever your finger land on, move around, or lift from the screen
    public boolean dispatchTouchEvent(MotionEvent event) {
        // TODO #3: Uncomment the line below and run the app
        // _textView.setText("Last touch duration = " + _touchObserver.getTouchDuration(1) + "ms");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.btnTouchDuration:
                // TODO #4: Display the average duration of all historical touches when this button is clicked
                // Hint: Check out methods from the Touch Observer
                double touchDuration = _touchObserver.getTouchDuration();
                _textView.setText("Average touch duration = " + touchDuration + "ms");
                break;
            case R.id.btnTouchVariability:
                // TODO #5: Display the average variability of all historical touches when this button is clicked
                // Hint: Check out methods from the Touch Observer
                double touchVariability = _touchObserver.getTouchVariability();
                _textView.setText("Average touch variability = " + touchVariability + "px");
                break;
            case R.id.btnHasTremor:
                // TODO #6: Display text indicating whether the user has touch tremor
                // Hint: Check out methods from the Ability Modeler
                boolean hasTremor = _abilityModeler.hasTremor();
                if (hasTremor) {
                    _textView.setText("This user has tremor");
                } else {
                    _textView.setText("This user doesn't have tremor");
                }
                break;
            case R.id.btnEnlarge:
                // TODO #7: Increase all the button sizes by 20% when this button is clicked
                // Hint: Check out methods from the UI Adapter
                _textView.setText("Enlarging all buttons");
                _uiAdapter.resizeWidgets(1.2);
                break;
            case R.id.btnNext:
                // We will skip this part
                // startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
            default:
                break;
        }
    }

    public void addButtonOnClickListeners() {
        Button btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        Button btnDuration = (Button) findViewById(R.id.btnTouchDuration);
        btnDuration.setOnClickListener(this);
        Button btnVariability = (Button) findViewById(R.id.btnTouchVariability);
        btnVariability.setOnClickListener(this);
        Button btnHasTremor = (Button) findViewById(R.id.btnHasTremor);
        btnHasTremor.setOnClickListener(this);
        Button btnEnlarge = (Button) findViewById(R.id.btnEnlarge);
        btnEnlarge.setOnClickListener(this);
        _textView = findViewById(R.id.textView1);
    }
}