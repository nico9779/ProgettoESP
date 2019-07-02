package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
Questa classe si interpone tra il dao e viewmodel e implementa tutti i metodi del database che
poi vengono utilizzati nella classe AlarmViewModel che estende ViewModel.
Il metodo del dao che restituisce i livedata (getAllAlarms) non ha bisogno di essere implementato
in quanto room fornisce a sua volta un'implementazione che viene eseguita in background.
Gli altri metodi devono essere implementati in modo che vengano eseguiti in background usando
la classe AsyncTask perchè Room non ammette l'esecuzioni di query nel main thread.
 */

class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> allAlarms;

    AlarmRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        alarmDao = database.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    //Metodo che restituisce i livedata
    LiveData<List<Alarm>> getAllAlarms(){
        return allAlarms;
    }

    /*
    Metodo che restituisce direttamente la lista di sveglie.
    Utilizza il metodo get della classe AsyncTask opportunamente gestito in presenza di eccezioni
    che permette di recuperare i dati processati e restituiti all'interno del metodo doInBackground.
     */
    List<Alarm> getListAlarms() {
        GetListAlarmsAsyncTask asyncTask = new GetListAlarmsAsyncTask(alarmDao);
        List<Alarm> alarms = new ArrayList<>();
        try {
            alarms = asyncTask.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return alarms;
    }

    //Metodo per cancellare tutte le sveglie nel database
    void deleteAllAlarms(){
        new DeleteAllAlarmsAsyncTask(alarmDao).execute();
    }

    /*
    Metodo che aggiunge una sveglia nel database.
    Il metodo get viene utilizzato per ottenere l'ID della sveglia appena inserita.
     */
    long addAlarm(Alarm alarm) {
        AddAlarmAsyncTask asyncTask = new AddAlarmAsyncTask(alarmDao);
        long id_alarm = 0;
        try {
            id_alarm = asyncTask.execute(alarm).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id_alarm;
    }

    //Metodo per aggiornare una sveglia
    void updateAlarm(Alarm alarm){
        new UpdateAlarmAsyncTask(alarmDao).execute(alarm);
    }

    //Metodo per eliminare una sveglia
    void deleteAlarm(Alarm alarm){
        new DeleteAlarmAsyncTask(alarmDao).execute(alarm);
    }

    /*
    Qui di seguito sono riportate tutte le classi che estendono AsyncTask e eseguono le query
    del dao in background.
     */

    private static class DeleteAllAlarmsAsyncTask extends AsyncTask<Void, Void, Void>{

        private AlarmDao alarmDao;

        private DeleteAllAlarmsAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            alarmDao.deleteAllAlarms();
            return null;
        }
    }

    private static class AddAlarmAsyncTask extends AsyncTask<Alarm, Void, Long>{

        private AlarmDao alarmDao;

        private AddAlarmAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected Long doInBackground(Alarm... alarms) {
            return alarmDao.addAlarm(alarms[0]);
        }
    }

    private static class GetListAlarmsAsyncTask extends AsyncTask<Void, Void, List<Alarm>>{

        private AlarmDao alarmDao;

        private GetListAlarmsAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            return alarmDao.getListAlarms();
        }
    }

    private static class UpdateAlarmAsyncTask extends AsyncTask<Alarm, Void, Void>{

        private AlarmDao alarmDao;

        private UpdateAlarmAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Alarm... alarms) {
            alarmDao.updateAlarm(alarms[0]);
            return null;
        }
    }

    private static class DeleteAlarmAsyncTask extends AsyncTask<Alarm, Void, Void>{

        private AlarmDao alarmDao;

        private DeleteAlarmAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Alarm... alarms) {
            alarmDao.deleteAlarm(alarms[0]);
            return null;
        }
    }
}
