package it.gliandroidiani.progettoesp;

import android.arch.lifecycle.ViewModelProviders;
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

/*
Questa classe è la classe principale che ospita i due fragment "AlarmFragment" e "NoteFragment"
che consentono di visualizzare le sveglie e gli allarmi
 */
public class MainActivity extends AppCompatActivity {

    //Variabili per il debugging e per determinare il requestcode dell'activity chiamata
    public static final int ADD_ALARM_REQUEST = 1;
    private static final int ADD_NOTE_REQUEST = 3;
    public static final String LOG_MAIN_ACTIVITY = "MainActivity";

    //Variabili private
    private BottomNavigationView navigationView;
    private AlarmViewModel alarmViewModel;
    private NoteViewModel noteViewModel;
    private boolean isConfigurationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inizializzazione
        navigationView = findViewById(R.id.bottom_navigation_bar);
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
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

                    String title = "Sveglia"; //titolo di dafault se non dovess essere impostato dall'utente


                    if(intent.hasExtra(AlarmClock.EXTRA_MESSAGE)){
                        title = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
                    }

                    String repetitionType = "Una sola volta";//ripetizione di Default
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
                    long alarmID = alarmViewModel.addAlarm(alarm);
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
            if (NoteIntents.ACTION_CREATE_NOTE.equals(action)) {
                if(intent.hasExtra(Intent.EXTRA_TEXT)){
                    String title = "Nota";
                    String description = intent.getStringExtra(Intent.EXTRA_TEXT);
                    Note note = new Note(title, System.currentTimeMillis(), description);
                    noteViewModel.addNote(note);
                    navigationView.setSelectedItemId(R.id.note);
                    Toast.makeText(this, R.string.event_save_note, Toast.LENGTH_SHORT).show();
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
