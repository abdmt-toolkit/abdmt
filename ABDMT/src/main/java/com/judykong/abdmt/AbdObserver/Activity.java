package com.judykong.abdmt.AbdObserver;

public class Activity {
    public ActivityEnum name;
    public long startTime;
    public long endTime;

    public Activity(ActivityEnum name, long startTime, long endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}