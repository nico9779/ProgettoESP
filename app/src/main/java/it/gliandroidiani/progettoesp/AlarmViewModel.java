package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/*
Questa classe estende AndroidViewModel che è una sottoclasse di ViewModel ed è in
grado di gestire i livedata, è associata a un'activity o un fragment, ha un proprio ciclo di vita
ed è in grado di sopravvivere a cambi di configurazioni.
La classe AlarmFragment che fornisce i dati all'interfaccia utente e svolge operazioni su di essi
li recupera da questa classe che inoltre implementa i metodi del dao recuperandoli dal repository.
 */
public class AlarmViewModel extends AndroidViewModel {

    private AlarmRepository repository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    long addAlarm(Alarm alarm){
        return repository.addAlarm(alarm);
    }

    void updateAlarm(Alarm alarm){
        repository.updateAlarm(alarm);
    }

    void deleteAlarm(Alarm alarm){
        repository.deleteAlarm(alarm);
    }

    void deleteAllAlarms(){
        repository.deleteAllAlarms();
    }

    LiveData<List<Alarm>> getAllAlarms(){
        return allAlarms;
    }
}
