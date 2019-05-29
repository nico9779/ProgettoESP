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

public class ActivityAddAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

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

        alarmToolbar = findViewById(R.id.alarm_toolbar);
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
    }

    private void saveAlarm(){
        String title = alarmTitle.getText().toString();
        String textTime = time.getText().toString();
        int i = 0;
        for (; textTime.charAt(i) != ':' ; i++);
        int hours = Integer.valueOf(textTime.substring(0,i));
        int minute = Integer.valueOf(textTime.substring(i+1));
        boolean vibration = vibration_switch.isChecked();

        if(title.trim().isEmpty()) {
            Toast.makeText(this, "Inserisci un titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        Alarm alarm = new Alarm(title, hours, minute, vibration);
        alarmViewModel.addAlarm(alarm);

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
