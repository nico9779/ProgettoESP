package it.gliandroidiani.progettoesp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static it.gliandroidiani.progettoesp.R.id.list_note_date;

/*
Questa classe estende Activity e permette di aggiungere o modificare delle note all'interno del
database indicando titolo della nota e descrizione.
 */
public class ActivityAddEditNote extends AppCompatActivity {

    //Variabili private della classe
    private EditText noteTitle;
    private EditText noteDescription;
    private Toolbar noteToolbar;
    private NoteRepository noteRepository;
    private TextView mNoteCreationTime;
    private long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        //Inizializzazione delle variabili
        noteTitle = findViewById(R.id.note_title);
        noteDescription = findViewById(R.id.note_description);
        mNoteCreationTime = findViewById(list_note_date);
        noteToolbar = findViewById(R.id.add_note_toolbar);
        noteRepository = new NoteRepository(getApplication());
        currentTime = System.currentTimeMillis();

        /*
          L'oggetto SimpleDateFormat mi permette di trasformare il tempo restituito da System.currentTimeMillis()
          in una stringa leggibile usando il pattern dd/MM/yyyy HH:mm:ss e quindi lo uso per inizializzare
          la textview mNoteCreationTime.
         */
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", getResources().getConfiguration().locale);
        formatter.setTimeZone(TimeZone.getDefault());
        mNoteCreationTime.setText(formatter.format(new Date(currentTime)));


        //Aggiungo alla toolbar l'icona per annullare una nota e imposto la action bar
        noteToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        setSupportActionBar(noteToolbar);

        /*
        Aggiungo un listener al tasto di annullamento della nota che mi permette di creare una finestra
        per accettare l'annullamento o rifiutarlo
         */
        noteToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditNote.this, R.style.AlertDialogStyle);
                if(noteToolbar.getTitle().equals(getResources().getString(R.string.add_note))){
                    builder.setTitle(getResources().getString(R.string.cancel_note));
                    builder.setMessage(getResources().getString(R.string.message_cancel_note));
                }
                else if(noteToolbar.getTitle().equals(getResources().getString(R.string.edit_note))){
                    builder.setTitle(getResources().getString(R.string.cancel_edit_note));
                    builder.setMessage(getResources().getString(R.string.message_cancel_edit_note));
                }
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.yes_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(noteToolbar.getTitle().equals(getResources().getString(R.string.add_note))){
                            Toast.makeText(ActivityAddEditNote.this, R.string.event_cancel_note, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(noteToolbar.getTitle().equals(getResources().getString(R.string.edit_note))){
                            Toast.makeText(ActivityAddEditNote.this, R.string.cancel_edit_note_toast, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.no_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        /*
        Nel caso in cui modifico una nota ricevo un'intent e estraggo da esso tutte le informazioni
        per ricostruire l'activity "ActivityAddEditNote".
         */
        Intent intent = getIntent();
        if(intent.hasExtra(NoteFragment.EXTRA_ID_NOTE)){
            noteToolbar.setTitle(getResources().getString(R.string.edit_note));
            noteTitle.setText(intent.getStringExtra(NoteFragment.EXTRA_TITLE_NOTE));
            noteDescription.setText(intent.getStringExtra(NoteFragment.EXTRA_DESCRIPTION_NOTE));
        }

        /*
        Nel caso in cui ci sia un cambiamento della configurazione a runtime recupero i valori
        salvati in savedInstanceState e ricostruisco le textview dell'activity e l'orario della
        creazione o modifica della nota
         */
        if(savedInstanceState!=null){
            currentTime = savedInstanceState.getLong("currentTime");//recupero  i dati salvati dopo un cambio stato
            String timeString = savedInstanceState.getString("timeString");
            if(timeString!=null) mNoteCreationTime.setText(timeString);
        }
    }

    //Metodo che salva o modifica una nota nel database
    private void saveNote(){

        //Determino i parametri della nota
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();

        //Controllo se l'utente ha inserito un titolo e una descrizione altrimenti lo segnalo con un Toast
        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, R.string.insert_title_and_description_note, Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        Controllo se devo creare l'allarme o modificarla e svolgo l'opzione corretta.
        Nel caso in cui devo modificare l'allarme l'intent che triggera l'activity contiene tra gli
        extra quello che rappresenta l'ID della nota altrimenti nel caso in cui devo aggiungere una
        nuova nota questo non accade.
         */
        if(!getIntent().hasExtra(NoteFragment.EXTRA_ID_NOTE)){
            Note note = new Note(title, currentTime, description);
            noteRepository.addNote(note);
            Toast.makeText(this, R.string.event_save_note, Toast.LENGTH_SHORT).show();
        }
        else {
            long id = getIntent().getLongExtra(NoteFragment.EXTRA_ID_NOTE, -1);
            //Verifico se la nota è presente nel database altrimenti c'è un errore
            if (id == -1)
                Toast.makeText(this, R.string.impossible_edit_note, Toast.LENGTH_SHORT).show();
            else {
                Note note = new Note(title, currentTime, description);
                note.setId(id);
                noteRepository.updateNote(note);
                Toast.makeText(this, R.string.event_edit_note, Toast.LENGTH_SHORT).show();
            }
        }

        //Terminata l'operazione di salvataggio o modifica ritorno alla main activity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //Metodo che aggiunge il menu alla toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    /* Metodo che mi permette di stabilire cosa fare quando clicco
    su un'icona del menu della toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        /*Cliccando sull'icona della spunta della toolbar viene eseguito il metodo saveNote che
          che mi permette di salvare una nota all'interno del database.
        */
        if(selected == R.id.save_note) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Metodo che mi permette di salvare lo stato dell'activity nel caso di modifiche della
    configurazione a runtime (come ad esempio girare lo schermo passando da portrait a landscape).
    Le variabili che vengono salvate sono l'orario di creazione o modifica della nota e la textview
    che contiene tale valore.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        long time = currentTime;
        String timeString = mNoteCreationTime.getText().toString();
        outState.putLong("currentTime", time); //per recuperare i dati quando giro lo schermo
        outState.putString("timeString", timeString);
        super.onSaveInstanceState(outState);
    }
}
