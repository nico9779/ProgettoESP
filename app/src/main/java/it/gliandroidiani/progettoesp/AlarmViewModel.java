package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class AlarmViewModel extends AndroidViewModel {

    private AlarmRepository repository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    public long addAlarm(Alarm alarm){
        return repository.addAlarm(alarm);
    }

    public void updateAlarm(Alarm alarm){
        repository.updateAlarm(alarm);
    }

    public void deleteAlarm(Alarm alarm){
        repository.deleteAlarm(alarm);
    }

    public void deleteAllAlarms(){
        repository.deleteAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms(){
        return allAlarms;
    }
}
