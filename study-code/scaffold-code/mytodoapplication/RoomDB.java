package com.example.mytodoapplication;

// Add database entities
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MainData.class}, version = 1, exportSchema = false)

public abstract class RoomDB extends RoomDatabase {

    // Create database instance
    private static RoomDB database;

    // Define DB name
    private static String DATABASE_NAME = "database";

    public synchronized static RoomDB getInstance(Context context){
        if (database == null) {
            // When database is null, initialize database
            database= Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    // Create Dao
    public abstract MainDao mainDao();

}