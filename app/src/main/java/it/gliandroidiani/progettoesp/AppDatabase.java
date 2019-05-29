package it.gliandroidiani.progettoesp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AppDatabase extends android.arch.persistence.room.RoomDatabase {

    private static AppDatabase instance;

    public abstract AlarmDao alarmDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "MyDatabase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private AlarmDao alarmDao;

        private PopulateDbAsyncTask(AppDatabase db){
            alarmDao = db.alarmDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            alarmDao.addAlarm(new Alarm("Titolo 1", 10, 10, true));
            alarmDao.addAlarm(new Alarm("Titolo 2", 20, 20, true));
            alarmDao.addAlarm(new Alarm("Titolo 3", 30, 30, true));
            return null;
        }
    }
}
