package it.gliandroidiani.progettoesp;

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.actions.NoteIntents;

import java.util.ArrayList;
import java.util.List;

/*
Questa classe è la classe principale che ospita i due fragment "AlarmFragment" e "NoteFragment"
che consentono di visualizzare le sveglie e le note
 */
public class MainActivity extends AppCompatActivity {

    //Variabili per il debugging e per determinare il requestcode dell'activity chiamata
    public static final int ADD_ALARM_REQUEST = 1;
    private static final int ADD_NOTE_REQUEST = 3;
    public static final String LOG_MAIN_ACTIVITY = "MainActivity";

    //Variabili private
    private BottomNavigationView navigationView;
    private AlarmRepository alarmRepository;
    private NoteRepository noteRepository;
    private boolean isConfigurationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inizializzazione
        navigationView = findViewById(R.id.bottom_navigation_bar);
        alarmRepository = new AlarmRepository(getApplication());
        noteRepository = new NoteRepository(getApplication());
        isConfigurationChanged = false;

        //Istanzio i fragment
        final AlarmFragment alarmFragment = new AlarmFragment();
        final NoteFragment noteFragment = new NoteFragment();

        //Listener per impostare i fragment in base alla selezione nel bottom navigation menu
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.alarm:
                        setFragment(alarmFragment);
                        return true;
                    case R.id.note:
                        setFragment(noteFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        /*
        Nel caso in cui la configurazione cambi a runtime recupero il fragment che stavo mostrando
        da savedInstanceState
         */
        if(savedInstanceState != null) {
            isConfigurationChanged = true;
            int item_selected = savedInstanceState.getInt("item_selected");
            navigationView.setSelectedItemId(item_selected);
        }
        else {
            //Seleziono le sveglie come primo oggetto da mostrare nella UI
            navigationView.setSelectedItemId(R.id.alarm);
        }

        /*
        Ogni volta che ricevo un'intent eseguo uno o nessuno dei due metodi ovvero quello
        per aggiungere le sveglie con assistant o quello per aggiungere le note con assistant
         */
        Intent intent = getIntent();
        addAlarmAssistant(intent);
        addNoteAssistant(intent);
    }

    //Metodo per rimpiazzare un fragment nella main activity
    private void setFragment(Fragment fragment)
    {
        if(fragment!=null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }
    }

    //Metodo per chiamare l'activity corretta per aggiungere una sveglia o una nota
    public void addElement(View view) {
        int selected = navigationView.getSelectedItemId();
        Intent intent;
        if(selected==R.id.alarm)
        {
            intent = new Intent(this, ActivityAddEditAlarm.class);
            startActivityForResult(intent, ADD_ALARM_REQUEST);
        }
        else if(selected==R.id.note){
            intent = new Intent(this, ActivityAddEditNote.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        }
    }

    //Metodo per gestire l'interazione con assistant per aggiungere le sveglie
    private void addAlarmAssistant(Intent intent){
        String action = intent.getAction();
        /*
        Inizialmente verifico che non ci sia stato un cambio di configurazione.
        Questo si fa perchè nel caso in cui si aggiunga una sveglia con assistant l'activity riceve
        un'intent e se cambia la configurazione a runtime si continua a ricevere tale intent dato
        che l'activity viene distrutta e ricreata e viene eseguito nuovamente il codice in onCreate.
         */
        if(!isConfigurationChanged) {
            // verifico se l'intent che ricevo è triggerato dall'assistent per impostare una sveglia
            if (AlarmClock.ACTION_SET_ALARM.equals(action)) {
                // verifico se ci sono gli extra che mi servono per impostare la sveglia
                if (intent.hasExtra(AlarmClock.EXTRA_HOUR) && intent.hasExtra(AlarmClock.EXTRA_MINUTES)) {
                    int hours = intent.getIntExtra(AlarmClock.EXTRA_HOUR, -1);
                    int minute = intent.getIntExtra(AlarmClock.EXTRA_MINUTES, -1);

                    String title = "Sveglia"; //titolo di default se non dovesse essere impostato dall'utente


                    if(intent.hasExtra(AlarmClock.EXTRA_MESSAGE)){
                        title = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
                    }

                    String repetitionType = "Una sola volta"; //ripetizione di default
                    boolean[] repetitionDays = new boolean[7];

                    /*
                    Nel caso in cui sia presente l'extra_days lo recupero dall'intent e imposto
                    correttamente i valori di repetitionType e repetitionDays
                    */
                    if(intent.hasExtra(AlarmClock.EXTRA_DAYS)){
                        ArrayList<Integer> repetitionDaysArrayList = intent.getIntegerArrayListExtra(AlarmClock.EXTRA_DAYS);
                        if(repetitionDaysArrayList.size() == 7)
                            repetitionType = "Giornalmente";
                        else {
                            repetitionType = "Giorni della settimana";
                            for (int i = 0; i <= 6; i++) {
                                if(repetitionDaysArrayList.contains(i+2))
                                    repetitionDays[i] = true;
                            }
                            if(repetitionDaysArrayList.contains(1))
                                repetitionDays[6] = true;
                        }
                    }
                    //Creo la nuova sveglia e la aggiungo nel database
                    Alarm alarm = new Alarm(title, hours, minute, true,true, true, repetitionType);
                    if(repetitionType.equals("Giorni della settimana")) {
                        alarm.setRepetitionDays(repetitionDays);
                    }
                    long alarmID = alarmRepository.addAlarm(alarm);
                    ScheduleAlarmHelper.scheduleAlarm(this, alarmID, alarm);
                    Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, R.string.impossible_add_alarm, Toast.LENGTH_SHORT).show();
                }
            }
            // verifico se l'intent che ricevo è triggerato dall'assistent per vedere tutte le mie sveglie
            else if (AlarmClock.ACTION_SHOW_ALARMS.equals(action))
                Log.d(LOG_MAIN_ACTIVITY, action);
        }
    }

    //Metodo per gestire l'interazione con assistant per aggiungere le note
    private void addNoteAssistant(Intent intent){
        String action = intent.getAction();
        if(!isConfigurationChanged) {
            // verifico se l'intent che ricevo è triggerato dall'assistent per impostare una nota
            if (NoteIntents.ACTION_CREATE_NOTE.equals(action)) {
                // verifico se ci sono gli extra che mi servono per impostare la nota
                if(intent.hasExtra(Intent.EXTRA_TEXT)){
                    // recupero il testo della nota dagli extra dell'intent
                    String testoNota = intent.getStringExtra(Intent.EXTRA_TEXT).toLowerCase();
                    /*
                    If che eseguo nel caso in cui voglio cancellare tutte le sveglie
                     */
                    if(testoNota.equals("cancella tutte le sveglie")){
                        //Recupero la lista delle sveglie dal database
                        List<Alarm> alarms = alarmRepository.getListAlarms();
                        //Se ci sono sveglie nel database le elimino tutte
                        if(!alarms.isEmpty()) {
                            alarmRepository.deleteAllAlarms();
                            for (Alarm alarm : alarms) {
                                //Disattivo tutti gli allarmi
                                ScheduleAlarmHelper.cancelAlarm(this, alarm);
                            }
                        Toast.makeText(this, R.string.deleted_all_alarms, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, R.string.no_alarms_to_delete, Toast.LENGTH_SHORT).show();
                        }
                    }
                    /*
                    If che eseguo nel caso in cui voglio cancellare tutte le note
                     */
                    else if(testoNota.equals("cancella tutte le note")){
                        //Recupero la lista delle note dal database
                        List<Note> notes = noteRepository.getListNotes();
                        navigationView.setSelectedItemId(R.id.note);
                        //Se ci sono note nel database le elimino tutte
                        if(!notes.isEmpty()) {
                            noteRepository.deleteAllNotes();
                            Toast.makeText(this, R.string.deleted_all_notes, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, R.string.no_notes_to_delete, Toast.LENGTH_SHORT).show();
                        }

                    }
                    /*
                    Nel caso in cui voglio cancellare delle sveglie con un determinato titolo estraggo
                    tale titolo dal testo della nota
                     */
                    else if(testoNota.startsWith("cancella sveglie con titolo") && !testoNota.trim().equals("cancella sveglie con titolo")){
                        String title = testoNota.substring(28,29).toUpperCase()+testoNota.substring(29);
                        //Booleano che verifica se ci sono sveglie presenti nel database con quel dato titolo
                        boolean noAlarms = true;
                        //Recupero la lista delle sveglie dal database
                        List<Alarm> alarms = alarmRepository.getListAlarms();
                        for (Alarm alarm : alarms) {
                            // Se c'è una sveglia nel database con quel titolo disattivo gli allarmi ad essa associati
                            if (alarm.getTitle().equals(title)) {
                                noAlarms = false;
                                ScheduleAlarmHelper.cancelAlarm(this, alarm);
                            }
                        }
                        /*
                        Se non esiste nessun allarme con quel titolo lo notifico con un toast
                        altrimenti elimino le sveglie con quel determinato titolo
                         */
                        if(noAlarms)
                            Toast.makeText(this, "Nessuna sveglia "+title+" da cancellare", Toast.LENGTH_SHORT).show();
                        else {
                            alarmRepository.deleteAlarmName(title);
                            Toast.makeText(this, "Sveglie con titolo " + title + " cancellate", Toast.LENGTH_SHORT).show();
                        }
                    }
                    /*
                    Nel caso in cui voglio cancellare delle note con un determinato titolo estraggo
                    tale titolo dal testo della nota
                     */
                    else if(testoNota.startsWith("cancella note con titolo") && !testoNota.trim().equals("cancella note con titolo")){
                        String title = testoNota.substring(25,26).toUpperCase()+testoNota.substring(26);
                        //Booleano che verifica se ci sono note presenti nel database con quel dato titolo
                        boolean noNotes = true;
                        //Recupero la lista delle note dal database
                        List<Note> notes = noteRepository.getListNotes();
                        for(Note note: notes){
                            //Verifico se esistono note con quel dato titolo
                            if(note.getTitle().equals(title)){
                                noNotes = false;
                            }
                        }
                        navigationView.setSelectedItemId(R.id.note);
                        /*
                        Se non esiste nessuna nota con quel titolo lo notifico con un toast
                        altrimenti elimino le note con quel determinato titolo
                         */
                        if(noNotes)
                            Toast.makeText(this, "Nessuna nota " + title + " da cancellare", Toast.LENGTH_SHORT).show();
                        else {
                            noteRepository.deleteNoteName(title);
                            Toast.makeText(this, "Note con titolo " + title + " cancellate", Toast.LENGTH_SHORT).show();
                        }
                    }
                    /*
                    In tutti gli altri casi aggiungo una nuova nota nel database che ha come
                    descrizione il testo della nota
                     */
                    else {
                        String title = "Nota";  //Titolo di default
                        //Creo la nuova nota e la aggiungo nel database
                        Note note = new Note(title, System.currentTimeMillis(), testoNota);
                        noteRepository.addNote(note);
                        //Visualizzo le note nella main activity
                        navigationView.setSelectedItemId(R.id.note);
                        Toast.makeText(this, R.string.event_save_note, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, R.string.impossible_add_note, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*
    Metodo per salvare lo stato del fragment visualizzato dall'activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int item_selected = navigationView.getSelectedItemId();
        outState.putInt("item_selected", item_selected);
        super.onSaveInstanceState(outState);
    }
}
