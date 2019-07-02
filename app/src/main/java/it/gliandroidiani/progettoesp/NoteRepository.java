package it.gliandroidiani.progettoesp;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
Questa classe ha significato analogo a AlarmRepository
 */

class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    NoteRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    //Metodo che restituisce i livedata
    LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    //Metodo per cancellare tutte le note nel database
    void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    //Metodo per aggiungere una nota
    void addNote(Note note){
        new AddNoteAsyncTask(noteDao).execute(note);
    }

    //Metodo per aggiornare una nota
    void updateNote(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    //Metodo per eliminare una nota
    void deleteNote(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    //Metodo per eliminare una nota con un dato titolo
    void deleteNoteName(String name){
        new DeleteNoteNameAsyncTask(noteDao).execute(name);
    }

    /*
    Metodo che restituisce direttamente la lista delle note anzichè i livedata.
    Utilizza il metodo get della classe AsyncTask opportunamente gestito in presenza di eccezioni
    che permette di recuperare i dati processati e restituiti all'interno del metodo doInBackground.
     */
    List<Note> getListNotes() {
        GetListNotesAsyncTask asyncTask = new GetListNotesAsyncTask(noteDao);
        List<Note> notes = new ArrayList<>();
        try {
            notes = asyncTask.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notes;
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

    private static class DeleteNoteNameAsyncTask extends AsyncTask<String, Void, Void>{

        private NoteDao noteDao;

        private DeleteNoteNameAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            noteDao.deleteNoteName(strings[0]);
            return null;
        }
    }

    private static class GetListNotesAsyncTask extends AsyncTask<Void, Void, List<Note>>{

        private NoteDao noteDao;

        private GetListNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected List<Note> doInBackground(Void... voids) {
            return noteDao.getListNotes();
        }
    }
}
