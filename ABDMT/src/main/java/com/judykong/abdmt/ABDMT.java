package com.judykong.abdmt;

import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.tasks.Task;
import com.judykong.abdmt.AbdObserver.Activity;
import com.judykong.abdmt.AbdObserver.ActivityObserver;
import com.judykong.abdmt.AbdObserver.AttentionObserver;
import com.judykong.abdmt.AbdObserver.GestureObserver;
import com.judykong.abdmt.AbdObserver.ObserverFlags;
import com.judykong.abdmt.AbdObserver.TimePoint;
import com.judykong.abdmt.AbdObserver.TouchObserver;

import java.util.ArrayList;

public class ABDMT {

    private TouchObserver _touchObserver = null;
    private GestureObserver _gestureObserver = null;
    private ActivityObserver _activityObserver = null;
    private AttentionObserver _attentionObserver = null;
    private UIAdapter _uiadapter = null;
    private AbilityModeler _abilityModeler = null;
    private int _layoutId;

    private static ABDMT _abdmt = null;
    private ArrayList<TimePoint> _currPath;
    private Activity _currActivity;

    private boolean _loadHistoricalSessionData = false;
    private boolean _saveCurrentSessionData = false;

    // TESTING
    // public TextView _textView;

    private ABDMT() {
        // setLayout(layoutId);
        _touchObserver = TouchObserver.getInstance();
        _gestureObserver = GestureObserver.getInstance();
        _activityObserver = ActivityObserver.getInstance();
        _attentionObserver = AttentionObserver.getInstance();
        _uiadapter = UIAdapter.getInstance();
        _abilityModeler = AbilityModeler.getInstance();
    }

    // Returns the Toolkit singleton
    public static ABDMT getInstance() {
        if (_abdmt == null) {
            _abdmt = new ABDMT();
        }
        return _abdmt;
    }

    public TouchObserver getTouchObserver() {
        return _touchObserver;
    }

    public GestureObserver getGestureObserver() {
        return _gestureObserver;
    }

    public ActivityObserver getActivityObserver() {
        return _activityObserver;
    }

    public AttentionObserver getAttentionObserver() {
        return _attentionObserver;
    }

    public AbilityModeler getAbilityModeler() {
        return _abilityModeler;
    }

    public UIAdapter getUiAdapter() {
        return _uiadapter;
    }

    public boolean startObservers(int flags) {
        boolean result = true;
        if ((flags & ObserverFlags.TOUCH) != 0) {
            result &= _touchObserver.start();
        }
        if ((flags & ObserverFlags.GESTURE) != 0) {
            result &= _gestureObserver.start();
        }
        if ((flags & ObserverFlags.ACTIVITY) != 0) {
            result &= _activityObserver.start();
        }
        if ((flags & ObserverFlags.ATTENTION) != 0) {
            result &= _attentionObserver.start();
        }
        return result;
    }

    public boolean startObserver(int flag) {
        switch (flag) {
            case ObserverFlags.TOUCH:
                return _touchObserver.start();
            case ObserverFlags.GESTURE:
                return _gestureObserver.start();
            case ObserverFlags.ACTIVITY:
                return _activityObserver.start();
            case ObserverFlags.ATTENTION:
                return _attentionObserver.start();
            default:
                return false;
        }
    }

    public void stopObserver(int flag) {
        switch (flag) {
            case ObserverFlags.TOUCH:
                _touchObserver.stop();
                break;
            case ObserverFlags.GESTURE:
                _gestureObserver.stop();
                break;
            case ObserverFlags.ACTIVITY:
                _activityObserver.stop();
                break;
            case ObserverFlags.ATTENTION:
                _attentionObserver.stop();
            default:
                break;
        }
    }

    public void dataLoaderSettings(int flags) {
        if ((flags & DataLoaderFlags.LOAD_HISTORICAL_SESSION_DATA) != 0) {
            _loadHistoricalSessionData = true;
        }
        if ((flags & DataLoaderFlags.SAVE_CURRENT_SESSION_DATA) != 0) {
            _saveCurrentSessionData = true;
        }
    }

    public boolean loadDataOnResume() {
        return _loadHistoricalSessionData;
    }

    public boolean saveDataOnPause() {
        return _saveCurrentSessionData;
    }

    // Get and set layout id to record the current layout type
    /*
    public int getLayout() {
        return _layoutId;
    }

    public void setLayout(int layoutId) {
        _layoutId = layoutId;
    }
    */

    public boolean isTouchPathRecorded() {
        return (_currPath == null || _currPath.size() == 0);
    }

    // Add touch event
    public void add(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float major = event.getTouchMajor();
        float minor = event.getTouchMinor();
        double contactArea = Math.PI * major * minor * 4;
        double orientation = event.getOrientation();
        long time = event.getEventTime();
        TimePoint pt = new TimePoint(x, y, major, minor, contactArea, orientation, time);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _currPath = new ArrayList<TimePoint>();
                _currPath.add(pt);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getHistorySize(); i++) {
                    x = event.getHistoricalX(i);
                    y = event.getHistoricalY(i);
                    major = event.getHistoricalTouchMajor(i);
                    minor = event.getHistoricalTouchMinor(i);
                    contactArea = Math.PI * major * minor * 4;
                    orientation = event.getHistoricalOrientation(i);
                    time = event.getHistoricalEventTime(i);
                    pt = new TimePoint(x, y, major, minor, contactArea, orientation, time);
                    _currPath.add(pt);
                }
                break;
            case MotionEvent.ACTION_UP:
                _currPath.add(pt);
                // Note: Move these to onTouch on onGesture, when the event gets consumed
                /*
                _touch.add(_currPath);
                _gesture.add(_currPath);
                */
                break;
        } // central data storage in API
    }

    // Add activity
    public void add(ArrayList<ActivityTransitionEvent> detectedActivities) {
        for (ActivityTransitionEvent activity : detectedActivities) {
            int activityType = activity.getActivityType();
            int transitionType = activity.getTransitionType();
            if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                if (_currActivity != null && _currActivity.name == _activityObserver.toEnumActivityType(activityType)) {
                    continue;
                }
                if (_currActivity != null) {
                    _currActivity.endTime = SystemClock.uptimeMillis(); // Note that it should be activity.getElapsedRealTimeNanos() but doesn't match with TimePoint
                    _activityObserver.add(_currActivity);
                }
                _currActivity = new Activity(
                        _activityObserver.toEnumActivityType(activityType),
                        SystemClock.uptimeMillis(), // Note that it should be activity.getElapsedRealTimeNanos() but doesn't match with TimePoint
                        0);
                _activityObserver.setCurrentActivity(_currActivity);
            }
        }
    }

    public void recordTouch() {
        if (_touchObserver.isActive()) {
            _touchObserver.add(_currPath);
        }
        _currPath = new ArrayList<TimePoint>();
    }

    public void recordGesture() {
        if (_gestureObserver.isActive()) {
            _gestureObserver.add(_currPath);
        }
        _currPath = new ArrayList<TimePoint>();
    }
}