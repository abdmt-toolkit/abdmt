package com.judykong.abdmt;

import android.os.SystemClock;

import com.judykong.abdmt.AbdObserver.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AbilityModeler {

    private static AbilityModeler _abilityModeler = null;
    private ABDMT _abdmt;
    private TouchObserver _touchObserver;
    private GestureObserver _gestureObserver;
    private ActivityObserver _activityObserver;
    private AttentionObserver _attentionObserver;

    private double TREMOR_VARIABILITY_THRESHOLD = 50;
    private double STIFFNESS_DRIFT_THRESHOLD = 10;
    private double STIFFNESS_VARIABILITY_THRESHOLD = 100;

    private AbilityModeler() {
    }

    // Get instance
    public static AbilityModeler getInstance() {
        if (_abilityModeler == null) {
            _abilityModeler = new AbilityModeler();
        }
        return _abilityModeler;
    }

    // TOUCH abilities
    public boolean hasTremor() {
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        return _touchObserver.getTouchVariability() > TREMOR_VARIABILITY_THRESHOLD;
    }

    public boolean hasStiffness() {
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        return _touchObserver.getTouchDrift() > STIFFNESS_DRIFT_THRESHOLD && _touchObserver.getTouchVariability() < STIFFNESS_VARIABILITY_THRESHOLD;
    }

    // TODO
    private double getTremorLevel() {
        return 0;
    }
    private boolean hasSpasm() {
        return false;
    }
    private boolean hasPoorCoordination() {
        return false;
    }
    private double getSpasmLevel() {
        return 0;
    }
    private boolean hasFrequentMisses() {
        return false;
    }

    // ACTIVITY abilities
    public boolean isStill() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        return (_activityObserver.getCurrentActivity().name == ActivityEnum.STILL);
    }

    public boolean isWalking() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        return (_activityObserver.getCurrentActivity().name == ActivityEnum.WALKING);
    }

    public boolean isRunning() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        return (_activityObserver.getCurrentActivity().name == ActivityEnum.RUNNING);
    }

    public boolean isCycling() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        return (_activityObserver.getCurrentActivity().name == ActivityEnum.CYCLING);
    }

    public boolean isDriving() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        return (_activityObserver.getCurrentActivity().name == ActivityEnum.IN_VEHICLE);
    }

    // TOUCH + ACTIVITY abilities
    public boolean isWalkingWhileTouching() {
        if (!isWalking())
            return false;
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        Activity currActivity = _activityObserver.getCurrentActivity();
        ArrayList<ArrayList<TimePoint>> touches = _touchObserver.getTouchesMs(currActivity.startTime);
        return (touches.size() > 0);
    }

    public boolean isDrivingWhileTouching() {
        if (!isDriving())
            return false;
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        Activity currActivity = _activityObserver.getCurrentActivity();
        ArrayList<ArrayList<TimePoint>> touches = _touchObserver.getTouchesMs(currActivity.startTime);
        return (touches.size() > 0);
    }

    // Situated touch abilities

    public boolean hasWalkTremor() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        double sum = 0;
        double count = 0;
        for (Activity activity : _activityObserver.getActivities()) {
            if (activity.name == ActivityEnum.WALKING) {
                long startTime = activity.startTime;
                long endTime = Math.max(activity.endTime, SystemClock.uptimeMillis());
                ArrayList<ArrayList<TimePoint>> walkTouchSegment = _touchObserver.getTouchesMs(startTime, endTime);
                sum += _touchObserver.getTouchVariabilityMs(startTime, endTime) * walkTouchSegment.size();
                count += walkTouchSegment.size();
            }
        }
        Activity currActivity = _activityObserver.getCurrentActivity();
        if (currActivity.name == ActivityEnum.WALKING) {
            long startTime = currActivity.startTime;
            ArrayList<ArrayList<TimePoint>> walkTouchSegment = _touchObserver.getTouchesMs(startTime);
            sum += _touchObserver.getTouchVariabilityMs(startTime) * walkTouchSegment.size();
            count += walkTouchSegment.size();
        }
        return count == 0 ? false : sum / count > TREMOR_VARIABILITY_THRESHOLD;
    }

    public boolean hasDriveTremor() {
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        double sum = 0;
        double count = 0;
        for (Activity activity : _activityObserver.getActivities()) {
            if (activity.name == ActivityEnum.IN_VEHICLE) {
                long startTime = activity.startTime;
                long endTime = Math.max(activity.endTime, SystemClock.uptimeMillis());
                ArrayList<ArrayList<TimePoint>> walkTouchSegment = _touchObserver.getTouchesMs(startTime, endTime);
                sum += _touchObserver.getTouchVariabilityMs(startTime, endTime) * walkTouchSegment.size();
                count += walkTouchSegment.size();
            }
        }
        Activity currActivity = _activityObserver.getCurrentActivity();
        if (currActivity.name == ActivityEnum.IN_VEHICLE) {
            long startTime = currActivity.startTime;
            ArrayList<ArrayList<TimePoint>> walkTouchSegment = _touchObserver.getTouchesMs(startTime);
            sum += _touchObserver.getTouchVariabilityMs(startTime) * walkTouchSegment.size();
            count += walkTouchSegment.size();
        }
        return count == 0 ? false : sum / count > TREMOR_VARIABILITY_THRESHOLD;
    }

    // GESTURE + ACTIVITY abilities
    public boolean isWalkingWhileUsingGestures() {
        if (!isWalking())
            return false;
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _gestureObserver = ABDMT.getInstance().getGestureObserver();
        Activity currActivity = _activityObserver.getCurrentActivity();
        ArrayList<ArrayList<TimePoint>> gestures = _gestureObserver.getGesturesMs(currActivity.startTime);
        return (gestures.size() > 0);
    }

    public boolean isDrivingWhileUsingGestures() {
        if (!isDriving())
            return false;
        _activityObserver = ABDMT.getInstance().getActivityObserver();
        _gestureObserver = ABDMT.getInstance().getGestureObserver();
        Activity currActivity = _activityObserver.getCurrentActivity();
        ArrayList<ArrayList<TimePoint>> gestures = _gestureObserver.getGesturesMs(currActivity.startTime);
        return (gestures.size() > 0);
    }

    // ATTENTION
    public boolean isLooking() {
        _attentionObserver = AttentionObserver.getInstance();
        Attention currentAttention = _attentionObserver.getCurrentAttention();
        if (currentAttention == null) {
            return false;
        }
        return currentAttention.status == AttentionEnum.LOOKING;
    }

    public boolean isLookingWhileWalking() {
        if (!isLooking()) {
            return false;
        }
        if (!isWalking()) {
            return false;
        }
        return true;
    }

    public boolean isLookingWhileDriving() {
        if (!isLooking()) {
            return false;
        }
        if (!isDriving()) {
            return false;
        }
        return true;
    }

    boolean isLookingWhileTouching() {
        if (!isLooking()) {
            return false;
        }
        AttentionObserver _attentionObserver = ABDMT.getInstance().getAttentionObserver();
        _touchObserver = ABDMT.getInstance().getTouchObserver();
        long startTime = _attentionObserver.getCurrentAttention().startTime;
        /*
        ArrayList<Attention> records = _attentionObserver.getAttentionRecords();
        long startTime = 0;
        for (int i = records.size() - 1; i > 0; i--) {
            if (records.get(i).status == AttentionEnum.NOT_LOOKING) {
                startTime = records.get(i).endTime;
                break;
            }
        }
        */
        ArrayList<ArrayList<TimePoint>> touches = _touchObserver.getTouchesMs(startTime);
        return (touches.size() > 0);
    }

    boolean isLookingWhileUsingGestures() {
        if (!isLooking()) {
            return false;
        }
        AttentionObserver _attentionObserver = ABDMT.getInstance().getAttentionObserver();
        _gestureObserver = ABDMT.getInstance().getGestureObserver();
        long startTime = _attentionObserver.getCurrentAttention().startTime;
        /*
        ArrayList<Attention> records = _attentionObserver.getAttentionRecords();
        long startTime = 0;
        for (int i = records.size() - 1; i > 0; i--) {
            if (records.get(i).status == AttentionEnum.NOT_LOOKING) {
                startTime = records.get(i).endTime;
                break;
            }
        }
        */
        ArrayList<ArrayList<TimePoint>> gestures = _gestureObserver.getGesturesMs(startTime);
        return (gestures.size() > 0);
    }

    // TODO
    private boolean isFingerDown() {
        _abdmt = ABDMT.getInstance();
        return !_abdmt.isTouchPathRecorded();
    }

//    private boolean queryAbilities(ArrayList<AbilityFlags> flags) {
//        for (AbilityFlags flag : flags) {
//            switch (flag) {
//                default:
//                    break;
//            }
//        }
//        return true;
//    }
}
