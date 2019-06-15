package it.gliandroidiani.progettoesp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/*
Questa classe rappresenta il DAO per le sveglie del database.
 */

@Dao
public interface AlarmDao {

    //Metodo per ottenere la lista delle sveglie presenti nel database
    @Query("SELECT * FROM alarm_table")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarm_table")
    List<Alarm> getListAlarms();

    //Metodo per eliminare tutte le sveglie nel database
    @Query("DELETE FROM alarm_table")
    void deleteAllAlarms();

    //Inserimento sveglia
    @Insert
    long addAlarm(Alarm alarm);

    //Aggiornamento sveglia
    @Update
    void updateAlarm(Alarm alarm);

    //Eliminazione sveglia
    @Delete
    void deleteAlarm(Alarm alarm);
}
