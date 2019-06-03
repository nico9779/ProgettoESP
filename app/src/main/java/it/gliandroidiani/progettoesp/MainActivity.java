package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_ALARM_REQUEST = 1;
    public static final String LOG_MAIN_ACTIVITY = "MainActivity";

    private BottomNavigationView navigationView;
    private AlarmViewModel alarmViewModel;
    private boolean isConfigurationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.bottom_navigation_bar);
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        isConfigurationChanged = false;

        //Istanzio i fragment
        final AlarmFragment alarmFragment = new AlarmFragment();
        final NoteFragment noteFragment = new NoteFragment();

        //Listener per impostare i fragment in base alla selezione nel menu
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

        if(savedInstanceState != null) {
            isConfigurationChanged = true;
            int item_selected = savedInstanceState.getInt("item_selected");
            navigationView.setSelectedItemId(item_selected);
        }
        else {
            //Seleziono le sveglie come primo oggetto da mostrare nella UI
            navigationView.setSelectedItemId(R.id.alarm);
        }

        Intent intent = getIntent();
        addAlarmAssistant(intent);
    }

    //Metodo per rimpiazzare un fragment in activity_main.xml
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
            intent = new Intent(this, ActivityAddNote.class);
            startActivity(intent);
        }
    }

    //Metodo per gestire l'interazione con assistant
    private void addAlarmAssistant(Intent intent){
        Log.d(LOG_MAIN_ACTIVITY, String.valueOf(intent.getAction()));
        if(!isConfigurationChanged) {
            // verifico se l'intent che ricevo è triggerato dall'assistent per impostare una sveglia
            if (AlarmClock.ACTION_SET_ALARM.equals(intent.getAction())) {
                // verifico se ci sono gli extra che mi servono per impostare la sveglia
                if (intent.hasExtra(AlarmClock.EXTRA_HOUR) && intent.hasExtra(AlarmClock.EXTRA_MINUTES)) {
                    int hours = intent.getIntExtra(AlarmClock.EXTRA_HOUR, -1);
                    int minute = intent.getIntExtra(AlarmClock.EXTRA_MINUTES, -1);
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hours);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);
                    Alarm alarm;
                    if(intent.hasExtra(AlarmClock.EXTRA_MESSAGE)){
                        String title = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
                        alarm = new Alarm(title, hours, minute, true, false);
                    }
                    else alarm = new Alarm("Sveglia", hours, minute, true,false);
                    long alarmID = alarmViewModel.addAlarm(alarm);
                    startAlarm(c, alarmID, alarm);
                    Toast.makeText(this, R.string.event_save_alarm, Toast.LENGTH_SHORT).show();
                }
            }
            // verifico se l'intent che ricevo è triggerato dall'assistent per vedere tutte le mie sveglie
            else if (AlarmClock.ACTION_SHOW_ALARMS.equals(intent.getAction()))
                Log.d(LOG_MAIN_ACTIVITY, intent.getAction());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int item_selected = navigationView.getSelectedItemId();
        outState.putInt("item_selected", item_selected);
        super.onSaveInstanceState(outState);
    }

    private void startAlarm(Calendar c, long alarmID, Alarm alarm){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Title", alarm.getTitle());
        intent.putExtra("AlarmID", alarmID);
        intent.putExtra("Vibration", alarm.isVibration());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) alarmID, intent, 0);

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
