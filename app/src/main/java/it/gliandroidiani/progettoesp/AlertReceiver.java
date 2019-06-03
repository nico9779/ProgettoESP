package it.gliandroidiani.progettoesp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title");
        long alarmID = intent.getLongExtra("AlarmID", 0);
        boolean ringtone = intent.getBooleanExtra("Ringtone", false);
        boolean vibration = intent.getBooleanExtra("Vibration", false);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, alarmID, ringtone, vibration);
        notificationHelper.getManager().notify((int) alarmID, nb.build());
    }
}
