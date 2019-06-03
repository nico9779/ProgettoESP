package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class ActivityAddEditAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private AlarmViewModel alarmViewModel;
    Toolbar alarmToolbar;
    TextView time_picked;
    TextView ringtone_state;
    TextView vibration_state;
    EditText alarmTitle;
    SwitchCompat ringtone_switch;
    SwitchCompat vibration_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);

        time_picked = findViewById(R.id.time_picked);
        ringtone_state = findViewById(R.id.ringtone_state);
        ringtone_switch = findViewById(R.id.ringtone_switch);
        vibration_switch = findViewById(R.id.vibration_switch);
        vibration_state = findViewById(R.id.vibration_state);
        alarmTitle = findViewById(R.id.alarm_title);
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        alarmToolbar = findViewById(R.id.add_alarm_toolbar);
        setSupportActionBar(alarmToolbar);


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
        }

        if(savedInstanceState != null){
            String timePicked = savedInstanceState.getString("timePicked");
            if(timePicked != null) time_picked.setText(timePicked);
        }
    }

    private void saveAlarm(){
        String title = alarmTitle.getText().toString();
        String textTime = time_picked.getText().toString();
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

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if(!getIntent().hasExtra(AlarmFragment.EXTRA_ID)){
            Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration);
            long alarmID = alarmViewModel.addAlarm(alarm);
            startAlarm(c, alarmID, alarm);
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
        }
        else {
            long id = getIntent().getLongExtra(AlarmFragment.EXTRA_ID, -1);
            if (id == -1)
                Toast.makeText(this, "La sveglia non può essere modificata", Toast.LENGTH_SHORT).show();
            else {
                Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration);
                alarm.setId(id);
                alarmViewModel.updateAlarm(alarm);
                startAlarm(c, id, alarm);
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
        else if(selected == R.id.cancel_alarm) {
            Toast.makeText(this, R.string.event_cancel_alarm, Toast.LENGTH_SHORT).show();
            finish();
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

    private void startAlarm(Calendar c, long alarmID, Alarm alarm){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Title", alarm.getTitle());
        intent.putExtra("AlarmID", alarmID);
        intent.putExtra("Ringtone", alarm.isRingtone());
        intent.putExtra("Vibration", alarm.isVibration());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
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
        outState.putString("timePicked", timePicked);
        super.onSaveInstanceState(outState);
    }
}
