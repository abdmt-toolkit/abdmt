package com.judykong.abdmt.AbdObserver;

public abstract class AbdObserver {
    // public String NAME;
    public ObserverFlags observer;
    private boolean isActive = false;
    // abstract String getName();
    abstract int getSize();
    abstract boolean isEmpty();
    abstract void clear();

    public boolean start() {
        isActive = true;
        return true;
    }

    public void stop() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
}