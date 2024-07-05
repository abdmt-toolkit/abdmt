package com.example.adaptivereading;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
public class AttentionTrackerDaemon implements MultiProcessor.Factory<Face> {
    private Context context;
    private AppCompatActivity activity;
    public AttentionTrackerDaemon(Context context, AppCompatActivity activity) {
        this.context = context;
        this.activity = activity;
    }
    @Override
    public Tracker<Face> create(Face face) {
        return new AttentionTracker(this.context, this.activity);
    }
}