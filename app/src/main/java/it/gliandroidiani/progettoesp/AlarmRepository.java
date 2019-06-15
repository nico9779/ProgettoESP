package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        alarmDao = database.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms(){
        return allAlarms;
    }

    public List<Alarm> getListAlarms() {
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

    public void deleteAllAlarms(){
        new DeleteAllAlarmsAsyncTask(alarmDao).execute();
    }

    public long addAlarm(Alarm alarm) {
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

    public void updateAlarm(Alarm alarm){
        new UpdateAlarmAsyncTask(alarmDao).execute(alarm);
    }

    public void deleteAlarm(Alarm alarm){
        new DeleteAlarmAsyncTask(alarmDao).execute(alarm);
    }

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
