package it.gliandroidiani.progettoesp;

import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ActivityAddAlarm extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Toolbar alarmToolbar;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        alarmToolbar = findViewById(R.id.alarm_toolbar);
        setSupportActionBar(alarmToolbar);

        time = findViewById(R.id.time);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        time.setText(hour+":"+minute);
    }

    //Metodo che aggiunge il menu alla toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_alarm_menu, menu);
        return true;
    }

    /* Metodo che mi permette di stabilire cosa fare quando clicco
    su un'icona del menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int selected = item.getItemId();
        if(selected == R.id.save_alarm) {
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
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
