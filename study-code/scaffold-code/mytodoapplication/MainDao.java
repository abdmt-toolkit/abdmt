package com.example.mytodoapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {

    // Insert query
    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);

    // Delete
    @Delete
    void delete(MainData mainData);

    // Delete all
    @Delete
    void reset(List<MainData> mainData);

    // Update
    @Query("UPDATE Table_name SET text =:sText WHERE ID = :sID")
    void update(int sID,String sText);

    // Get all data
    @Query("SELECT * FROM table_name")
    List<MainData> getAll();
}