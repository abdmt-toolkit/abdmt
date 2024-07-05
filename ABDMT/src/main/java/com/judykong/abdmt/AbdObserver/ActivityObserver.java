package com.judykong.abdmt.AbdObserver;

import android.os.SystemClock;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityObserver extends AbdObserver {

    // public static final String NAME = "Activity";
    public static final int observer = ObserverFlags.ACTIVITY;

    // public static final int IN_VEHICLE = 0;
    // public static final int STILL = 3;
    // public static final int WALKING = 7;

    // public static final String WALKING = "WALKING";
    // public static final String STILL = "STILL";
    // public static final String IN_VEHICLE = "IN_VEHICLE";

    private static ActivityObserver _activityObserver = null;
    private ArrayList<Activity> _activities;
    private Activity _currentActivity;
    // private Context _context;
    private ActivityChangeListener _activityListener;

    public interface ActivityChangeListener {
        default void onActivityChange(ActivityEnum previousActivityEnum, ActivityEnum currentActivityEnum) {}
        default void onStill() { }
        default void onWalk() { }
        default void onRun() { }
        default void onCycle() { }
        default void onDrive() { }
    }

    private ActivityObserver() {
        _activities = new ArrayList<Activity>();
    }

    // Returns the Touch singleton
    public static ActivityObserver getInstance() {
        if (_activityObserver == null) {
            _activityObserver = new ActivityObserver();
        }
        return _activityObserver;
    }

    public void setListener(ActivityChangeListener listener) {
        _activityListener = listener;
    }

    public ActivityChangeListener getListener() {
        return _activityListener;
    }

    // Returns the name of the module
//    public String getName() {
//        return NAME;
//    }

    // Returns the number of paths stored
    public int getSize() {
        return _activities.size();
    }
    public boolean isEmpty() { return _activities.size() == 0; }

    // Store a new path
    public void add(Activity activity) {
        _activities.add(activity);
    }

    // Delete all stored paths
    public void clear() {
        _activities = new ArrayList<Activity>();
    }

    // Returns the current Activity
    // Note: there must be one according to the Android ActivityRecognition logic
    public Activity getCurrentActivity() {
        return _currentActivity;
    }

    // Sets the current Activity
    public void setCurrentActivity(Activity activity) {
        _currentActivity = activity;
    }

    // Gets the duration of the current activity
    public long getCurrentActivityDuration() {
        if (_currentActivity == null) {
            return 0;
        }
        return SystemClock.uptimeMillis() - _currentActivity.startTime; // Note: same issue as ABDMTActivity, use different elapse time function
    }

    // Set context
//    public void setContext(Context context) {
//        _context = context;
//    }

    // Returns all activities;
    public ArrayList<Activity> getActivities() {
        return _activities;
    }

    // Load existing paths
    public void loadActivities(ArrayList<Activity> activities) {
        _activities = activities;
    }

    // Returns the most recent lastN activities;
    public ArrayList<Activity> getActivities(int lastN) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (int i = this.getSize() - 1; i >= this.getSize() - lastN; i--) {
            if (i >= 0) {
                activities.add(_activities.get(i));
            }
        }
        return activities;
    }

    // Returns the activities from startTime to endTime
    public ArrayList<Activity> getActivitiesMs(long startTime, long endTime) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                Activity activity = _activities.get(i);
                if (startTime <= activity.startTime && activity.endTime <= endTime) {
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    // Returns the activities from startTime to endTime
    public ArrayList<Activity> getActivitiesMs(long startTime) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0) {
                Activity activity = _activities.get(i);
                if (startTime <= activity.startTime) {
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    public long getActivityDuration(ActivityEnum activityType) {
        long sum = 0;
        for (Activity activity: _activities) {
            if (activity.name == activityType) {
                sum += activity.endTime - activity.startTime;
            }
        }
        return sum;
    }

    public long getActivityDuration(ActivityEnum activityType, int lastN) {
        int count = 0;
        long sum = 0;
        for (int i = this.getSize() - 1; i >= 0; i--) {
            if (i >= 0 && count < lastN) {
                Activity activity = _activities.get(i);
                if (activity.name == activityType) {
                    count ++;
                    sum += activity.endTime - activity.startTime;
                }
            }
        }
        return sum;
    }
    
    public boolean isWalking() {
        if (_currentActivity == null) {
            return false;
        }
        return _currentActivity.name == ActivityEnum.WALKING;
    }

    public boolean isStill() {
        if (_currentActivity == null) {
            return false;
        }
        return _currentActivity.name == ActivityEnum.STILL;
    }

    public boolean isRunning() {
        if (_currentActivity == null) {
            return false;
        }
        return _currentActivity.name == ActivityEnum.RUNNING;
    }

    public boolean isCycling() {
        if (_currentActivity == null) {
            return false;
        }
        return _currentActivity.name == ActivityEnum.CYCLING;
    }

    public boolean isDriving() {
        if (_currentActivity == null) {
            return false;
        }
        return _currentActivity.name == ActivityEnum.IN_VEHICLE;
    }

    // Returns the start time of the current Activity;
//    public long getStart(int activityType) {
//        return _currActivity.startTime;
//    }

    // Returns the end time of the current Activity;
//    public long getEnd(int activityType) {
//        return _currActivity.endTime;
//    }

    /******** Helper Functions ********/

    public ActivityEnum toEnumActivityType(int type) {
        switch (type) {
            case DetectedActivity.WALKING:
                return ActivityEnum.WALKING;
            case DetectedActivity.STILL:
                return ActivityEnum.STILL;
            case DetectedActivity.RUNNING:
                return ActivityEnum.RUNNING;
            case DetectedActivity.ON_BICYCLE:
                return ActivityEnum.CYCLING;
            case DetectedActivity.IN_VEHICLE:
                return ActivityEnum.IN_VEHICLE;
            default:
                return null;
        }
    }

    public String toStringActivityType(int type) {
        switch (type) {
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.ON_BICYCLE:
                return "CYCLING";
            case DetectedActivity.IN_VEHICLE:
                return "DRIVING";
            default:
                return "";
        }
    }

    public String toStringTransitionType(int type) {
        switch (type) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "Enter";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "Exit";
            default:
                return "";
        }
    }
}
