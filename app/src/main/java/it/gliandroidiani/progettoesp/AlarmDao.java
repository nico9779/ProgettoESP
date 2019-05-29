package it.gliandroidiani.progettoesp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Query("SELECT * FROM alarm_table")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("DELETE FROM alarm_table")
    void deleteAllAlarms();

    @Insert
    void addAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);
}
