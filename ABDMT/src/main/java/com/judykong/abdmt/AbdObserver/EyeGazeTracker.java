package com.judykong.abdmt.AbdObserver;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.judykong.abdmt.ABDMT;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EyeGazeTracker extends Tracker<Face> {

    private final float THRESHOLD = 0.75f;
    private ABDMT _abdmt;
    private Context context;

    private AttentionObserver _attentionObserver;

    public EyeGazeTracker(Context context) {
        this.context = context;
        _abdmt = ABDMT.getInstance();
        _attentionObserver = AttentionObserver.getInstance();
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        Attention prev;
        if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
            // Log.i(TAG, "onUpdate: Open Eyes Detected");
            prev = _attentionObserver.getCurrentAttention();
            // Skip the update if only eyes are blinking
            if (prev != null && prev.status == AttentionEnum.LOOKING) {
                return;
            }
            // Add to eye gaze records
            if (prev != null) {
                prev.endTime = SystemClock.uptimeMillis();
                _attentionObserver.add(prev);
            }
            Attention curr = new Attention(AttentionEnum.LOOKING, SystemClock.uptimeMillis());
            _attentionObserver.setCurrentAttention(curr);
        } else {
            // Log.i(TAG, "onUpdate: Close Eyes Detected");
            prev = _attentionObserver.getCurrentAttention();
            // Skip the update if only eyes are blinking
            if (prev != null && prev.status == AttentionEnum.LOOKING) {
                return;
            }
            // Add to eye gaze records
            if (prev != null) {
                prev.endTime = SystemClock.uptimeMillis();
                _attentionObserver.add(prev);
            }
            Attention curr = new Attention(AttentionEnum.LOOKING, SystemClock.uptimeMillis());
            _attentionObserver.setCurrentAttention(curr);
        }
        if (prev == null || (prev != null && prev.status == AttentionEnum.NOT_LOOKING)) {
            AttentionObserver.AttentionChangeListener listener = _attentionObserver.getListener();
            if (listener != null) {
                listener.onAttentionChange(AttentionEnum.LOOKING);
                listener.onLookingAtScreen();
            }
        }
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);
        // Log.i(TAG, "onUpdate: Face Not Detected!");
        Attention prev = _attentionObserver.getCurrentAttention();
        // Skip the update if no status change
        if (prev != null && prev.status == AttentionEnum.NOT_LOOKING) {
            return;
        }
        // Add to eye gaze records
        if (prev != null) {
            prev.endTime = SystemClock.uptimeMillis();
            _attentionObserver.add(prev);
        }
        Attention curr = new Attention(AttentionEnum.NOT_LOOKING, SystemClock.uptimeMillis());
        _attentionObserver.setCurrentAttention(curr);
        if (prev != null && prev.status != AttentionEnum.NOT_LOOKING) {
            AttentionObserver.AttentionChangeListener listener = _attentionObserver.getListener();
            if (listener != null) {
                listener.onAttentionChange(AttentionEnum.NOT_LOOKING);
                listener.onLookingAwayFromScreen();
            }
        }
    }

    @Override
    public void onDone() {
        super.onDone();
    }
}
