package it.gliandroidiani.progettoesp;

import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

/*
Questa classe estende Activity e permette di aggiungere o modificare delle sveglie all'interno del
database indicando titolo della sveglia, ora, tipo della ripetizione ("Una sola volta",
"Giornalmente", "Giorni della settimana"), suoneria e vibrazione
 */
public class ActivityAddEditAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    //Variabili private della classe
    private AlarmViewModel alarmViewModel;
    private Toolbar alarmToolbar;
    private TextView time_picked;
    private TextView ringtone_state;
    private TextView vibration_state;
    private TextView repetition_picked;
    private EditText alarmTitle;
    private SwitchCompat ringtone_switch;
    private SwitchCompat vibration_switch;
    private String[] repetitionOptions;
    private String[] repetitionOptionsDays;
    private String[] repetitionOptionsDaysShort;
    private boolean[] repetitionOptionsDaysChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);

        //Inizializzazione delle variabili
        time_picked = findViewById(R.id.time_picked);
        ringtone_state = findViewById(R.id.ringtone_state);
        ringtone_switch = findViewById(R.id.ringtone_switch);
        vibration_switch = findViewById(R.id.vibration_switch);
        vibration_state = findViewById(R.id.vibration_state);
        alarmTitle = findViewById(R.id.alarm_title);
        RelativeLayout repetitionLayout = findViewById(R.id.repetition_layout);
        repetition_picked = findViewById(R.id.repetition_picked);

        repetitionOptions = getResources().getStringArray(R.array.repetition_options);
        repetitionOptionsDays = getResources().getStringArray(R.array.repetition_options_days);
        repetitionOptionsDaysShort = getResources().getStringArray(R.array.repetition_options_days_short);
        repetitionOptionsDaysChecked = new boolean[repetitionOptionsDays.length];

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        alarmToolbar = findViewById(R.id.add_alarm_toolbar);

        //Aggiungo alla toolbar l'icona per annullare una sveglia e imposto la action bar
        alarmToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        setSupportActionBar(alarmToolbar);

        /*
        Aggiungo un listener al tasto di annullamento della sveglia che mi permette di creare una finestra
        per accettare l'annullamento o rifiutarlo
         */
        alarmToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditAlarm.this, R.style.AlertDialogStyle);
                if(alarmToolbar.getTitle().equals(getResources().getString(R.string.add_alarm))){
                    builder.setTitle(getResources().getString(R.string.cancel_alarm));
                    builder.setMessage(getResources().getString(R.string.message_cancel_alarm));
                }
                else if(alarmToolbar.getTitle().equals(getResources().getString(R.string.edit_alarm))){
                    builder.setTitle(getResources().getString(R.string.cancel_edit_alarm));
                    builder.setMessage(getResources().getString(R.string.message_cancel_edit_alarm));
                }
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.yes_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(alarmToolbar.getTitle().equals(getResources().getString(R.string.add_alarm))){
                            Toast.makeText(ActivityAddEditAlarm.this, R.string.event_cancel_alarm, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(alarmToolbar.getTitle().equals(getResources().getString(R.string.edit_alarm))){
                            Toast.makeText(ActivityAddEditAlarm.this, R.string.cancel_edit_alarm_toast, Toast.LENGTH_SHORT).show();
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
        Creo un calendario per determinare l'ora attuale e aggiorno la textview che permette di
        scegliere l'orario
         */
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        updateTimeText(hours, minute);

        //Metodi che vengono invocati quando cambio lo stato degli switch
        ringtone_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ringtone_state.setText(R.string.on_label);
                else
                    ringtone_state.setText(R.string.off_label);
            }
        });

        vibration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    vibration_state.setText(R.string.on_label);
                else
                    vibration_state.setText(R.string.off_label);
            }
        });

        /*
        Nel caso in cui modifico una sveglia ricevo un'intent e estraggo da esso tutte le informazioni
        per ricostruire l'activity "ActivityAddEditAlarm".
        Inoltre nel caso in cui il tipo di ripetizione sia "Giorni della settimana" recupero il
        vettore di booleani che determinano i giorni in cui l'allarme deve essere ripetuta e imposto
        correttamente tali giorni nella textview
         */
        Intent intent = getIntent();
        if(intent.hasExtra(AlarmFragment.EXTRA_ID)){
            alarmToolbar.setTitle(getResources().getString(R.string.edit_alarm));
            alarmTitle.setText(intent.getStringExtra(AlarmFragment.EXTRA_TITLE));
            updateTimeText(intent.getIntExtra(AlarmFragment.EXTRA_HOURS, 0),intent.getIntExtra(AlarmFragment.EXTRA_MINUTE, 0));
            ringtone_switch.setChecked(intent.getBooleanExtra(AlarmFragment.EXTRA_RINGTONE, false));
            vibration_switch.setChecked(intent.getBooleanExtra(AlarmFragment.EXTRA_VIBRATION, false));
            String repetitionType = intent.getStringExtra(AlarmFragment.EXTRA_REPETITION_TYPE);
            if(repetitionType.equals("Una sola volta") || repetitionType.equals("Giornalmente")){
                repetition_picked.setText(repetitionType);
            }
            else {
                boolean[] repetitionDays = intent.getBooleanArrayExtra(AlarmFragment.EXTRA_REPETITION_DAYS);
                repetitionOptionsDaysChecked = repetitionDays;
                String item = "";
                for (int i = 0; i < repetitionDays.length; i++) {
                    if(repetitionDays[i]){
                        item = item + repetitionOptionsDaysShort[i] + " ";
                    }
                }
                repetition_picked.setText(item);
            }
        }

        /*
        Imposto un listener sul layout che contiene il tipo di ripetizione scelta in modo tale
            da creare una finestra attraverso la quale l'utente può fare la sua scelta.
         */
        repetitionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditAlarm.this, R.style.AlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.select_option));
                builder.setCancelable(false);

                /*
                Imposto nella finestra le tre tipologie di ripetizione
                 */
                builder.setItems(repetitionOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Nel caso in cui seleziono le prime due opzioni ("Una sola volta" e "Giornalmente")
                        aggiorno la textview altrimento creo un nuovo menu per selezionare i giorni della
                        settimana cui far ripetere l'allarme
                         */
                        if(which<2){
                            repetition_picked.setText(repetitionOptions[which]);
                        }
                        else{
                            dialog.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditAlarm.this, R.style.AlertDialogStyle);
                            builder.setTitle("Giorni della settimana");
                            builder.setCancelable(false);
                            builder.setMultiChoiceItems(repetitionOptionsDays, repetitionOptionsDaysChecked, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    /*
                                    Ogni volta che clicco su un'elemento della lista, tale elemento nel
                                    vettore repetitionOptionsDaysChecked viene messo automaticamente a true
                                     */
                                }
                            });
                            /*
                            Quando premo "OK" aggiorno la textview con i giorni scelti.
                            Se sono selezionati tutti i giorni allora la textview mostrerà
                            "Giornalmente" altrimenti se non ne è selezionato nessuno mostrerà
                            "Nessuna opzione"
                             */
                            builder.setPositiveButton(getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean isFalse = false;
                                    String item = "";
                                    for (int i = 0; i < repetitionOptionsDaysChecked.length; i++) {
                                        if(repetitionOptionsDaysChecked[i]){
                                            item = item + repetitionOptionsDaysShort[i] + " ";
                                        }
                                        else {
                                            isFalse = true;
                                        }
                                    }
                                    if(isFalse && !item.isEmpty()) {
                                        repetition_picked.setText(item);
                                    }
                                    else if(item.isEmpty()){
                                        repetition_picked.setText(R.string.no_repetition_option_selected);
                                    }
                                    else {
                                        repetition_picked.setText(repetitionOptions[1]);
                                    }
                                }
                            });
                            AlertDialog d = builder.create();
                            d.show();
                        }
                    }
                });

                builder.setNeutralButton(getResources().getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
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
        Nel caso in cui ci sia un cambiamento della configurazione a runtime recupero i valori
        salvati in savedInstanceState e ricostruisco le textview dell'activity
         */
        if(savedInstanceState != null){
            String timePicked = savedInstanceState.getString("timePicked");
            String repetitionType = savedInstanceState.getString("repetitionType");
            repetitionOptionsDaysChecked = savedInstanceState.getBooleanArray("repetitionDays");
            if(timePicked != null) time_picked.setText(timePicked);
            if(repetitionType != null) repetition_picked.setText(repetitionType);
        }
    }

    //Metodo che salva una sveglia nel database
    private void saveAlarm(){
        //Determino i parametri della sveglia
        String title = alarmTitle.getText().toString();
        String textTime = time_picked.getText().toString();
        String repetition = repetition_picked.getText().toString();
        int hours = 0;
        int minute = 0;

        //Faccio il parsing di textTime per determinare ora e minuti della sveglia
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(DateFormat.getTimeInstance(DateFormat.SHORT).parse(textTime));
            hours = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean ringtone = ringtone_switch.isChecked();
        boolean vibration = vibration_switch.isChecked();

        //Controllo se l'utente ha inserito un titolo e una ripetizione
        if(title.trim().isEmpty()) {
            Toast.makeText(this, R.string.insert_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if(repetition.equals(getResources().getString(R.string.no_repetition_option_selected))){
            Toast.makeText(this, R.string.choose_repetition, Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!repetition.equals("Una sola volta") && !repetition.equals("Giornalmente")){
            repetition = "Giorni della settimana";
        }

        /*
        Controllo se devo creare l'allarme o modificarla e svolgo l'opzione corretta
         */
        if(!getIntent().hasExtra(AlarmFragment.EXTRA_ID)){
            Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, true, repetition);
            alarm.setRepetitionDays(repetitionOptionsDaysChecked);
            long alarmID = alarmViewModel.addAlarm(alarm);
            ScheduleAlarmHelper.scheduleAlarm(this, alarmID, alarm);
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
        }
        else {
            long id = getIntent().getLongExtra(AlarmFragment.EXTRA_ID, -1);
            if (id == -1)
                Toast.makeText(this, R.string.impossible_edit_alarm, Toast.LENGTH_SHORT).show();
            else {
                boolean active = getIntent().getBooleanExtra(AlarmFragment.EXTRA_ACTIVE, false);
                Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, active, repetition);
                alarm.setId(id);
                alarm.setRepetitionDays(repetitionOptionsDaysChecked);
                alarmViewModel.updateAlarm(alarm);
                if(active) {
                    ScheduleAlarmHelper.scheduleAlarm(this, id, alarm);
                }
                Toast.makeText(this, R.string.event_edit_alarm, Toast.LENGTH_SHORT).show();
            }
        }

        //Terminata l'operazione di salvataggio o modifica ritorno alla main activity
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //Metodo che aggiunge il menu alla toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_alarm_menu, menu);
        return true;
    }

    /* Metodo che mi permette di stabilire cosa fare quando clicco
    su un'icona del menu della toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int selected = item.getItemId();
        /*Cliccando sull'icona della spunta della toolbar viene eseguito il metodo saveAlarm che
          che mi permette di salvare una sveglia all'interno del database.
        */
        if(selected == R.id.save_alarm) {
            saveAlarm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Metodo che viene eseguito quando l'utente seleziona un determinato orario nel timepickerFragment
    e che va ad aggiornare la textview mostrando quindi l'orario scelto.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateTimeText(hourOfDay, minute);
    }

    /*
    Metodo che crea il timepickerFragment quando viene cliccata la textview che contiene l'orario
    per scegliere l'orario da impostare alla sveglia.
     */
    public void setTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }

    /*
    Metodo che aggiorna la textview che mostra l'orario selezionato dall'utente.
     */
    private void updateTimeText(int hours, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        //DateFormat mi permette di ottenere l'orario in un formato che coincide con quello
        //impostato nel dispositivo ovvero 12 o 24 ore.
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        time_picked.setText(timeText);
    }

    /*
    Metodo che mi permette di salvare lo stato dell'activity nel caso di modifiche della
    configurazione a runtime (come ad esempio girare lo schermo passando da portrait a landscape).
    Le variabili che vengono salvate sono l'orario scelto dall'utente, il tipo della ripetizione e
    i giorni scelti come ripetizione.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String timePicked = time_picked.getText().toString();
        String repetitionType = repetition_picked.getText().toString();
        boolean[] repetitionDays = repetitionOptionsDaysChecked;
        outState.putString("timePicked", timePicked);
        outState.putString("repetitionType", repetitionType);
        outState.putBooleanArray("repetitionDays", repetitionDays);
        super.onSaveInstanceState(outState);
    }
}
