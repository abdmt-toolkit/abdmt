package com.judykong.abdmt;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ABDMTData.class}, version = 1, exportSchema = false)

public abstract class ABDMTDB extends RoomDatabase {

    private static ABDMTDB databse;

    private static String DATABASE_NAME="abdmt-database";

    public synchronized static ABDMTDB getInstance(Context context){
        if (databse == null) {
            databse= Room.databaseBuilder(context.getApplicationContext(), ABDMTDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return databse;
    }

    public abstract ABDMTDao mainDao();

}