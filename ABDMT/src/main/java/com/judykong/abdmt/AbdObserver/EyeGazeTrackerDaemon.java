package com.judykong.abdmt.AbdObserver;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import android.content.Context;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class EyeGazeTrackerDaemon implements MultiProcessor.Factory<Face> {

    private Context context;
    public EyeGazeTrackerDaemon(Context context) { this.context = context; }

    @Override
    public Tracker<Face> create(Face face) {
        return new EyeGazeTracker(this.context);
    }
}
