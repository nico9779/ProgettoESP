package it.gliandroidiani.progettoesp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/*
Questa classe rappresenta il DAO (data acess object) per le note del database.
 */

@Dao
public interface NoteDao {

    //Metodo per ottenere la lista delle note presenti nel database
    @Query("SELECT * FROM note_table ORDER BY date DESC")
    LiveData<List<Note>> getAllNotes();

    //Metodo per eliminare tutte le note nel database
    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    //Inserimento nota
    @Insert
    void addNote(Note note);

    //Aggiornamento nota
    @Update
    void updateNote(Note note);

    //Eliminazione nota
    @Delete
    void deleteNote(Note note);
}
