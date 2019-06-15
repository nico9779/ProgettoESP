package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import java.util.Calendar;

public class ActivityAddEditAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, ScheduleAlarmHelper {

    private AlarmViewModel alarmViewModel;
    Toolbar alarmToolbar;
    TextView time_picked;
    TextView ringtone_state;
    TextView vibration_state;
    TextView repetition_picked;
    EditText alarmTitle;
    SwitchCompat ringtone_switch;
    SwitchCompat vibration_switch;
    RelativeLayout repetitionLayout;
    String[] repetitionOptions;
    String[] repetitionOptionsDays;
    String[] repetitionOptionsDaysShort;
    boolean[] repetitionOptionsDaysChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);

        time_picked = findViewById(R.id.time_picked);
        ringtone_state = findViewById(R.id.ringtone_state);
        ringtone_switch = findViewById(R.id.ringtone_switch);
        vibration_switch = findViewById(R.id.vibration_switch);
        vibration_state = findViewById(R.id.vibration_state);
        alarmTitle = findViewById(R.id.alarm_title);
        repetitionLayout = findViewById(R.id.repetition_layout);
        repetition_picked = findViewById(R.id.repetition_picked);

        repetitionOptions = getResources().getStringArray(R.array.repetition_options);
        repetitionOptionsDays = getResources().getStringArray(R.array.repetition_options_days);
        repetitionOptionsDaysShort = getResources().getStringArray(R.array.repetition_options_days_short);
        repetitionOptionsDaysChecked = new boolean[repetitionOptionsDays.length];

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        alarmToolbar = findViewById(R.id.add_alarm_toolbar);
        alarmToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp));
        setSupportActionBar(alarmToolbar);
        alarmToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditAlarm.this, R.style.AlertDialogStyle);
                if(alarmToolbar.getTitle().equals("Aggiungi una sveglia")){
                    builder.setTitle("Annulla sveglia");
                    builder.setMessage("Sei sicuro di voler annullare questa sveglia?");
                }
                else if(alarmToolbar.getTitle().equals("Modifica sveglia")){
                    builder.setTitle("Annulla modifica");
                    builder.setMessage("Sei sicuro di voler annullare questa modifica?");
                }
                builder.setCancelable(false);
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(alarmToolbar.getTitle().equals("Aggiungi una sveglia")){
                            Toast.makeText(ActivityAddEditAlarm.this, R.string.event_cancel_alarm, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(alarmToolbar.getTitle().equals("Modifica sveglia")){
                            Toast.makeText(ActivityAddEditAlarm.this, "Modifica annullata", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        updateTimeText(hours, minute);

        ringtone_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ringtone_state.setText(R.string.on_label);
                else
                    ringtone_state.setText(R.string.off_label);
            }
        });

        //Metodo che viene invocato quando cambio lo stato dello switch
        vibration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    vibration_state.setText(R.string.on_label);
                else
                    vibration_state.setText(R.string.off_label);
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra(AlarmFragment.EXTRA_ID)){
            alarmToolbar.setTitle("Modifica sveglia");
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

        repetitionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddEditAlarm.this, R.style.AlertDialogStyle);
                builder.setTitle("Scegli un'opzione");
                builder.setCancelable(false);

                builder.setItems(repetitionOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                                    // do nothing
                                }
                            });
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

                builder.setNeutralButton("ANNULLA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if(savedInstanceState != null){
            String timePicked = savedInstanceState.getString("timePicked");
            String repetitionType = savedInstanceState.getString("repetitionType");
            repetitionOptionsDaysChecked = savedInstanceState.getBooleanArray("repetitionDays");
            if(timePicked != null) time_picked.setText(timePicked);
            if(repetitionType != null) repetition_picked.setText(repetitionType);
        }
    }

    private void saveAlarm(){
        String title = alarmTitle.getText().toString();
        String textTime = time_picked.getText().toString();
        String repetitionType = repetition_picked.getText().toString();
        String repetition;
        int hours = 0;
        int minute = 0;
        for (int i = 0; i < textTime.length(); i++) {
            if(textTime.charAt(i) == ':'){
                hours = Integer.valueOf(textTime.substring(0,i));
                minute = Integer.valueOf(textTime.substring(i+1));
                break;
            }
        }
        boolean ringtone = ringtone_switch.isChecked();
        boolean vibration = vibration_switch.isChecked();

        if(title.trim().isEmpty()) {
            Toast.makeText(this, "Inserisci un titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(repetitionType.equals(getResources().getString(R.string.no_repetition_option_selected))){
            Toast.makeText(this, "Scegli una ripetizione", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!repetitionType.equals("Una sola volta") && !repetitionType.equals("Giornalmente")){
            repetition = "Giorni della settimana";
        }
        else repetition = repetitionType;

        if(!getIntent().hasExtra(AlarmFragment.EXTRA_ID)){
            Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, true, repetition);
            alarm.setRepetitionDays(repetitionOptionsDaysChecked);
            long alarmID = alarmViewModel.addAlarm(alarm);
            scheduleAlarm(alarmID, alarm);
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
        }
        else {
            long id = getIntent().getLongExtra(AlarmFragment.EXTRA_ID, -1);
            if (id == -1)
                Toast.makeText(this, "La sveglia non può essere modificata", Toast.LENGTH_SHORT).show();
            else {
                boolean active = getIntent().getBooleanExtra(AlarmFragment.EXTRA_ACTIVE, false);
                Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, active, repetition);
                alarm.setId(id);
                alarm.setRepetitionDays(repetitionOptionsDaysChecked);
                alarmViewModel.updateAlarm(alarm);
                if(active) {
                    scheduleAlarm(id, alarm);
                }
                Toast.makeText(this, "Sveglia modificata", Toast.LENGTH_SHORT).show();
            }
        }

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
        if(selected == R.id.save_alarm) {
            saveAlarm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateTimeText(hourOfDay, minute);
    }

    public void setTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }

    @Override
    public void scheduleAlarm(long alarmID, Alarm alarm){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Title", alarm.getTitle());
        intent.putExtra("AlarmID", alarmID);
        intent.putExtra("Hours", alarm.getHours());
        intent.putExtra("Minute", alarm.getMinute());
        intent.putExtra("Ringtone", alarm.isRingtone());
        intent.putExtra("Vibration", alarm.isVibration());
        intent.putExtra("Repetition", alarm.getRepetitionType());
        String repetition = alarm.getRepetitionType();
        if(repetition.equals("Una sola volta")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        if(repetition.equals("Giornalmente")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            for (int i = 0; i <= 6; i++) {
                if(alarm.getRepetitionDays()[i]){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) (alarmID*6+i), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

                    if(i!=6) {
                        calendar.set(Calendar.DAY_OF_WEEK, i+2);
                    }
                    else{
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    }

                    if(calendar.before(Calendar.getInstance())){
                        calendar.add(Calendar.DATE, 7);
                    }

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (AlarmManager.INTERVAL_DAY)*7,pendingIntent);
                }
            }
        }
    }

    private void updateTimeText(int hours, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        time_picked.setText(timeText);
    }

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

    public Calendar createCalendar(int hours, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
