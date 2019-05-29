package it.gliandroidiani.progettoesp;

import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
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

import java.util.Calendar;

public class ActivityAddEditAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_ID = "it.gliandroidiani.progettoesp.EXTRA_ID";
    public static final String EXTRA_TITLE = "it.gliandroidiani.progettoesp.EXTRA_TITLE";
    public static final String EXTRA_HOURS = "it.gliandroidiani.progettoesp.EXTRA_HOURS";
    public static final String EXTRA_MINUTE = "it.gliandroidiani.progettoesp.EXTRA_MINUTE";
    public static final String EXTRA_VIBRATION = "it.gliandroidiani.progettoesp.EXTRA_VIBRATION";

    private AlarmViewModel alarmViewModel;
    Toolbar alarmToolbar;
    TextView time;
    TextView vibration_state;
    EditText alarmTitle;
    SwitchCompat vibration_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        time = findViewById(R.id.time);
        vibration_switch = findViewById(R.id.vibration_switch);
        vibration_state = findViewById(R.id.vibration_state);
        alarmTitle = findViewById(R.id.alarm_title);
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        alarmToolbar = findViewById(R.id.add_alarm_toolbar);
        setSupportActionBar(alarmToolbar);


        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        time.setText(hour+":"+minute);

        //Metodo che viene invocato quando cambio lo stato dello switch
        vibration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    vibration_state.setText(R.string.vibration_on);
                else
                    vibration_state.setText(R.string.vibration_off);
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            alarmToolbar.setTitle("Modifica sveglia");
            alarmTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            time.setText(intent.getIntExtra(EXTRA_HOURS, 0)+":"+intent.getIntExtra(EXTRA_MINUTE, 0));
            vibration_switch.setChecked(intent.getBooleanExtra(EXTRA_VIBRATION, false));
        }
    }

    private void saveAlarm(){
        String title = alarmTitle.getText().toString();
        String textTime = time.getText().toString();
        int hours = 0;
        int minute = 0;
        for (int i = 0; i < textTime.length(); i++) {
            if(textTime.charAt(i) == ':'){
                hours = Integer.valueOf(textTime.substring(0,i));
                minute = Integer.valueOf(textTime.substring(i+1));
                break;
            }
        }
        boolean vibration = vibration_switch.isChecked();

        if(title.trim().isEmpty()) {
            Toast.makeText(this, "Inserisci un titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!getIntent().hasExtra(EXTRA_ID)){
            Alarm alarm = new Alarm(title, hours, minute, vibration);
            alarmViewModel.addAlarm(alarm);
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
        }
        else {
            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id == -1)
                Toast.makeText(this, "La sveglia non può essere modificata", Toast.LENGTH_SHORT).show();
            else {
                Alarm alarm = new Alarm(title, hours, minute, vibration);
                alarm.setId(id);
                alarmViewModel.updateAlarm(alarm);
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
        TextView textView = findViewById(R.id.time);
        textView.setText(hourOfDay+":"+minute);
    }

    public void setTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }
}
