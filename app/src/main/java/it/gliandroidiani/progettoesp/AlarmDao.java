package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface AlarmDao {

    @Insert
    public void addAlarm(Alarm alarm);
}
