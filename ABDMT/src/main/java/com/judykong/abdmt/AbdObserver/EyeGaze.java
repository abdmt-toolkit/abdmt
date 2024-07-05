package com.judykong.abdmt.AbdObserver;

public class EyeGaze {

    public EyeGazeEnum status;
    public long startTime;
    public long endTime;

    public EyeGaze(EyeGazeEnum status, long startTime) {
        this.status = status;
        this.startTime = startTime;
    }

}
