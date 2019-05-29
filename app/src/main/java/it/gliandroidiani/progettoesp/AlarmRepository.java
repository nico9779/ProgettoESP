package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

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

    public void deleteAllAlarms(){
        new DeleteAllAlarmsAsyncTask(alarmDao).execute();
    }

    public void addAlarm(Alarm alarm){
        new AddAlarmAsyncTask(alarmDao).execute(alarm);
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

    private static class AddAlarmAsyncTask extends AsyncTask<Alarm, Void, Void>{

        private AlarmDao alarmDao;

        private AddAlarmAsyncTask(AlarmDao alarmDao){
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Alarm... alarms) {
            alarmDao.addAlarm(alarms[0]);
            return null;
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
