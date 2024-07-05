package com.judykong.abdmt.AbdObserver;

public class TimePoint {
    public float x;
    public float y;
    public float major;
    public float minor;
    public double contactArea; // Contact area as proportion of device screen size from 0 to 1
    public double orientation;
    public long time; // Time of point in milliseconds // Use SystemClock.uptimeMillis()

    public TimePoint(float x, float y, float major, float minor, double contactArea, double orientation, long time) {
        this.x = x;
        this.y = y;
        this.major = major;
        this.minor = minor;
        this.contactArea = contactArea;
        this.orientation = orientation;
        this.time = time;
    }
}