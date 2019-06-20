package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/*
Questa classe estende BroadcastReceiver e il metodo onReceive viene eseguito ogni volta che viene
riavviato il dispositivo.
Ogni volta che viene riavviato il dispositivo gli allarmi che sono stati schedulati vengono persi
e devono essere schedulati nuovamente.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
        if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Recupero un'istanza del repository per ottenere la lista delle sveglie  presenti nel database
            AlarmRepository alarmRepository = new AlarmRepository((Application) context.getApplicationContext());
            List<Alarm> alarms = alarmRepository.getListAlarms();

            //Per ogni sveglia nel database se è attiva riavvio gli allarmi.
            for (Alarm alarm : alarms) {

                if (alarm.isActive()) {

                    long alarmID = alarm.getId();
                    ScheduleAlarmHelper.scheduleAlarm(context, alarmID, alarm);
                }
            }
        }
    }
}
