package com.judykong.abdmt;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "abdmt_table")
public class ABDMTData implements Serializable {

    @PrimaryKey(autoGenerate = true)

    private int ID;

    @ColumnInfo(name = "text")

    private String text;

    @ColumnInfo(name = "touches")

    private String touches;

    @ColumnInfo(name = "gestures")

    private String gestures;

    @ColumnInfo(name = "activities")

    private String activities;

    @ColumnInfo(name = "attention")

    private String attention;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTouches() {
        return touches;
    }

    public void setTouches(String touches) { this.touches = touches; }

    public String getGestures() {
        return gestures;
    }

    public void setGestures(String gestures) { this.gestures = gestures; }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) { this.activities = activities; }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) { this.attention = attention; }
}
