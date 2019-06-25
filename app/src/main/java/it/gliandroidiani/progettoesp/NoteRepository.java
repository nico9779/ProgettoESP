package it.gliandroidiani.progettoesp;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/*
Questa classe ha significato analogo a AlarmRepository
 */

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    //Metodo che restituisce i livedata
    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    //Metodo per cancellare tutte le note nel database
    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    //Metodo per aggiungere una nota
    public void addNote(Note note){
        new AddNoteAsyncTask(noteDao).execute(note);
    }

    //Metodo per aggiornare una nota
    public void updateNote(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    //Metodo per eliminare una nota
    public void deleteNote(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    /*
    Qui di seguito sono riportate tutte le classi che estendono AsyncTask e eseguono le query
    del dao in background.
     */

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }

    private static class AddNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private AddNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.addNote(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.updateNote(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.deleteNote(notes[0]);
            return null;
        }
    }
}
