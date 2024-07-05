package com.judykong.abdmt.AbdObserver;

import android.os.SystemClock;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.sql.Array;
import java.util.ArrayList;

public class AttentionObserver extends AbdObserver {

    public static final int observer = ObserverFlags.ATTENTION;

    private static AttentionObserver _attentionObserver = null;
    private ArrayList<Attention> _attentionRecords;
    private Attention _currentAttention = null;
    private AttentionEnum _currentAttentionEnum = null;
    private AttentionChangeListener _attentionListener;

    public interface AttentionChangeListener {
        default void onAttentionChange(AttentionEnum attentionEnum) { }
        default void onLookingAtScreen() { }
        default void onLookingAwayFromScreen() { }
    }

    private AttentionObserver() {
        _attentionRecords = new ArrayList<Attention>();
        // _eyeGazeRecords = new ArrayList<EyeGaze>();
    }

    // Returns the Touch singleton
    public static AttentionObserver getInstance() {
        if (_attentionObserver == null) {
            _attentionObserver = new AttentionObserver();
        }
        return _attentionObserver;
    }

    public void setListener(AttentionChangeListener listener) {
        _attentionListener = listener;
    }

    public AttentionChangeListener getListener() {
        return _attentionListener;
    }

    // Returns the number of paths stored
    public int getSize() {
        return _attentionRecords.size();
    }
    public boolean isEmpty() {
        return _attentionRecords.size() == 0;
    }

    // Store a new gaze
    public void add(Attention event) {
        _attentionRecords.add(event);
    }

    // Delete all stored paths
    public void clear() {
        _attentionRecords = new ArrayList<Attention>();
    }

    // Returns the current
    // Note: there must be one according to the Android ActivityRecognition logic
    public Attention getCurrentAttention() {
        return _currentAttention;
    }

    // Sets the current
    public void setCurrentAttention(Attention event) {
        _currentAttention = event;
    }

    public void loadAttention(ArrayList<Attention> attentionRecords) {
        _attentionRecords = attentionRecords;
    }

    // Gets the duration of the current gaze
    public long getCurrentAttentionDuration() {
        if (_attentionRecords == null) {
            return 0;
        }
        return SystemClock.uptimeMillis() - _currentAttention.startTime;
    }

    // Returns all
    public ArrayList<Attention> getAttentionRecords() {
        return _attentionRecords;
    }

}
