package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/*
Questa classe è analoga a AlarmViewModel
 */

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    void addNote(Note note){
        repository.addNote(note);
    }

    void updateNote(Note note){
        repository.updateNote(note);
    }

    void deleteNote(Note note){
        repository.deleteNote(note);
    }

    void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }
}
