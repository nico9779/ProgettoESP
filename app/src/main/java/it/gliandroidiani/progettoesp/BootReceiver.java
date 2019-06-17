package it.gliandroidiani.progettoesp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {
        if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmRepository alarmRepository = new AlarmRepository((Application) context.getApplicationContext());
            List<Alarm> alarms = alarmRepository.getListAlarms();

            for (Alarm alarm : alarms) {

                if (alarm.isActive()) {

                    long alarmID = alarm.getId();
                    ScheduleAlarmHelper.scheduleAlarm(context, alarmID, alarm);
                }
            }
        }
    }
}
