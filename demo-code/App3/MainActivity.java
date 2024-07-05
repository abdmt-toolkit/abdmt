package com.judykong.cyclingnotification;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;
public class MainActivity extends ABDMTActivity
        implements View.OnClickListener{
    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private GestureObserver _gestureObserver;
    private ActivityObserver _activityObserver;
    private AbilityModeler _abilityModeler;
    private UIAdapter _uiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        setContext(this);
        setRoot(findViewById(R.id.root));
        _abdmt = ABDMT.getInstance();
        _touchObserver = _abdmt.getTouchObserver();
        _gestureObserver = _abdmt.getGestureObserver();
        _activityObserver = _abdmt.getActivityObserver();
        _abilityModeler = _abdmt.getAbilityModeler();
        _uiAdapter = _abdmt.getUiAdapter();
        _abdmt.startObserver(ObserverFlags.TOUCH);
        _abdmt.startObserver(ObserverFlags.GESTURE);
        _abdmt.startObserver(ObserverFlags.ACTIVITY);
        _uiAdapter.registerWidgets(getRoot());
        setTags();
        _uiAdapter.showWidgetsWithTag("small");
        setActivityChangeListener(new ActivityChangeListener() {
            @Override
            public void onWalk() {
                _uiAdapter.showWidgetsWithTag("large");
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            _uiAdapter.resizeWidgets(1.03);
            _uiAdapter.resizeFonts(1.03);
        }
        return super.onTouchEvent(event);
    }
    private void setTags() {
        _uiAdapter.setTag(findViewById(R.id.smallView), "small");
        _uiAdapter.setTag(findViewById(R.id.smallViewTitleText), "small");
        _uiAdapter.setTag(findViewById(R.id.smallViewContentText), "small");
        _uiAdapter.setTag(findViewById(R.id.btnCancel), "large");
        _uiAdapter.setTag(findViewById(R.id.btnIgnore), "large");
        _uiAdapter.setTag(findViewById(R.id.btnReply), "large");
        _uiAdapter.setTag(findViewById(R.id.largeView), "large");
        _uiAdapter.setTag(findViewById(R.id.largeViewTitleText), "large");
        _uiAdapter.setTag(findViewById(R.id.largeViewContentText), "large");
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        _uiAdapter.showWidgetsWithTag(" ");
    }
}