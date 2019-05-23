package it.gliandroidiani.progettoesp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ActivityAddAlarm extends AppCompatActivity {

    Toolbar alarmToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        alarmToolbar = findViewById(R.id.alarm_toolbar);
        setSupportActionBar(alarmToolbar);
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
            Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_LONG).show();
            return true;
        }
        else if(selected == R.id.cancel_alarm) {
            Toast.makeText(this, R.string.event_cancel_alarm, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
