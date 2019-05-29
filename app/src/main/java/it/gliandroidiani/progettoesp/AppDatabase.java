package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.Context;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AppDatabase extends android.arch.persistence.room.RoomDatabase {

    private static AppDatabase instance;

    public abstract AlarmDao alarmDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "MyDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
