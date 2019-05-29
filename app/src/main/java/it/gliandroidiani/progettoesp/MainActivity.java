package it.gliandroidiani.progettoesp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_ALARM_REQUEST = 1;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.bottom_navigation_bar);

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

        //Seleziono le sveglie come primo oggetto da mostrare nella UI
        navigationView.setSelectedItemId(R.id.alarm);
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
}
