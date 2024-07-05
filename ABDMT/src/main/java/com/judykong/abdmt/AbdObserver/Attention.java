package com.judykong.abdmt.AbdObserver;

public class Attention {

    public AttentionEnum status;
    public long startTime;
    public long endTime;

    public Attention(AttentionEnum status, long startTime) {
        this.status = status;
        this.startTime = startTime;
    }

}
