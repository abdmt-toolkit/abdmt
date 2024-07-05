package com.judykong.abdmt;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

import java.util.List;

@Dao
public interface ABDMTDao {

    @Insert(onConflict = REPLACE)
    void insert(ABDMTData mainData);

    @Delete
    void delete(ABDMTData mainData);

    @Delete
    void reset(List<ABDMTData> mainData);

    // @Query("UPDATE abdmt_table SET text =:sText WHERE ID = :sID")
    // void update(int sID, String sText);

    @Query("UPDATE abdmt_table SET touches =:touchText")
    void updateTouch(String touchText);

    @Query("UPDATE abdmt_table SET gestures =:gestureText")
    void updateGesture(String gestureText);

    @Query("UPDATE abdmt_table SET activities =:activityText")
    void updateActivity(String activityText);

    @Query("UPDATE abdmt_table SET attention =:attentionText")
    void updateAttention(String attentionText);

    @Query("SELECT * FROM abdmt_table")
    List<ABDMTData> getAll();
}

