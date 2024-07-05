package com.example.adaptivereading;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
public class AttentionTracker extends Tracker<Face> {
    private final float THRESHOLD = 0.75f;
    private Context context;
    private AppCompatActivity activity;
    private boolean isLooking;
    private float originalBrightness;
    private Window _window = null;
    public AttentionTracker(Context context, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
        this.isLooking = false;
    }
    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        boolean prev = this.isLooking;
        if (prev) {
            return;
        }
        if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
            this.isLooking = true;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _window = activity.getWindow();
                    WindowManager.LayoutParams layout = _window.getAttributes();
                    originalBrightness = layout.screenBrightness;
                    layout.screenBrightness = 1f;
                    _window.setAttributes(layout);
                }
            });
        }
    }
    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);
        boolean prev = this.isLooking;
        if (!prev) {
            return;
        }
        this.isLooking = false;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _window = activity.getWindow();
                WindowManager.LayoutParams layout = _window.getAttributes();
                layout.screenBrightness = originalBrightness;
                _window.setAttributes(layout);
            }
        });
    }
    @Override
    public void onDone() {
        super.onDone();
    }
}