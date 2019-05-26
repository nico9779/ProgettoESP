package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.Database;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AppDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract AlarmDao alarmDao();
}
