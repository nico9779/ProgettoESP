package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/*
Questa classe descrive il database dell'applicazione.
Implementa il design pattern singleton.
 */

@Database(entities = {Alarm.class}, version = 1)
@TypeConverters(Converter.class)
public abstract class AppDatabase extends android.arch.persistence.room.RoomDatabase {

    //Istanza del database
    private static AppDatabase instance;

    public abstract AlarmDao alarmDao();

    /*Metodo che crea un'istanza del database o che permette di ottenere l'istanza del database nel caso
    sia già stata creata
     */
    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "MyDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
