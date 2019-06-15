package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title");
        long alarmID = intent.getLongExtra("AlarmID", 0);
        int hours = intent.getIntExtra("Hours", 0);
        int minute = intent.getIntExtra("Minute", 0);
        boolean ringtone = intent.getBooleanExtra("Ringtone", false);
        boolean vibration = intent.getBooleanExtra("Vibration", false);
        String repetition = intent.getStringExtra("Repetition");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, alarmID, ringtone, vibration);
        notificationHelper.getManager().notify((int) alarmID, nb.build());
        if(repetition.equals("Una sola volta")){
            AlarmRepository alarmRepository = new AlarmRepository((Application) context.getApplicationContext());
            Alarm alarm = new Alarm(title, hours, minute, ringtone, vibration, false, repetition);
            alarm.setId(alarmID);
            alarm.setRepetitionDays(new boolean[] {false, false, false, false, false, false, false});
            alarmRepository.updateAlarm(alarm);
        }
    }
}
