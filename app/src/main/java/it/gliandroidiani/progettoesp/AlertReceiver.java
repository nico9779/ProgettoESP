package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/*
Questa classe estende BroadcastReceiver e il metodo onReceive viene eseguito ogni volta che scatta
un'allarme.
 */

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Estraggo tutte le informazioni della sveglia dall'intent che ha triggerato il mio receiver
        String title = intent.getStringExtra("Title");
        long alarmID = intent.getLongExtra("AlarmID", -1);
        int hours = intent.getIntExtra("Hours", -1);
        int minute = intent.getIntExtra("Minute", -1);
        boolean ringtone = intent.getBooleanExtra("Ringtone", false);
        boolean vibration = intent.getBooleanExtra("Vibration", false);
        String repetition = intent.getStringExtra("Repetition");
        //Creo la notifica da visualizzare con la classe di supporto NotificationHelper
        if(alarmID != -1) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, alarmID, ringtone, vibration);
            notificationHelper.getManager().notify((int) alarmID, nb.build());
        }
        /*
        Nel caso in cui la sveglia deva squillare una sola volta viene aggiornata creando una nuova
        sveglia che contiene le stesse informazioni e inoltre lo stato della sveglia viene impostato
        automaticamente a false.
         */
        if(repetition.equals("Una sola volta") && alarmID != -1){
            AlarmRepository alarmRepository = new AlarmRepository((Application) context.getApplicationContext());
            Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, false, repetition);
            alarm.setId(alarmID);
            alarmRepository.updateAlarm(alarm);
        }
    }
}
