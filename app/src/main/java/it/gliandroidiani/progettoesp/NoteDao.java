package it.gliandroidiani.progettoesp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/*
Questa classe rappresenta il DAO per le note del database.
 */

@Dao
public interface NoteDao {

    //Metodo per ottenere la lista delle note presenti nel database in ordine decrescente di data
    @Query("SELECT * FROM note_table ORDER BY date DESC")
    LiveData<List<Note>> getAllNotes();

    //Metodo per ottenere direttamente la lista delle note nel database anzichè i livedata
    @Query("SELECT * FROM note_table ORDER BY date DESC")
    List<Note> getListNotes();

    //Metodo per eliminare tutte le note nel database
    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    //Metodo che cancella tutte le note con un determinato titolo
    @Query("DELETE FROM note_table WHERE title = :noteName")
    void deleteNoteName(String noteName);

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
