package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmRepository alarmRepository = new AlarmRepository((Application) context.getApplicationContext());
            List<Alarm> alarms = alarmRepository.getListAlarms();

            for (Alarm alarm : alarms) {


                if (alarm.isActive()) {

                    long alarmID = alarm.getId();

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context, AlertReceiver.class);
                    i.putExtra("Title", alarm.getTitle());
                    i.putExtra("AlarmID", alarmID);
                    i.putExtra("Hours", alarm.getHours());
                    i.putExtra("Minute", alarm.getMinute());
                    i.putExtra("Ringtone", alarm.isRingtone());
                    i.putExtra("Vibration", alarm.isVibration());
                    i.putExtra("Repetition", alarm.getRepetitionType());
                    String repetition = alarm.getRepetitionType();
                    if (repetition.equals("Una sola volta")) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID * 6), i, PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                    if (repetition.equals("Giornalmente")) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID * 6), i, PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    } else {
                        for (int j = 0; j <= 6; j++) {
                            if (alarm.getRepetitionDays()[j]) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID * 6 + j), i, PendingIntent.FLAG_UPDATE_CURRENT);

                                Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

                                if (j != 6) {
                                    calendar.set(Calendar.DAY_OF_WEEK, j + 2);
                                } else {
                                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                }

                                if (calendar.before(Calendar.getInstance())) {
                                    calendar.add(Calendar.DATE, 7);
                                }

                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (AlarmManager.INTERVAL_DAY) * 7, pendingIntent);
                            }
                        }
                    }
                }
            }
        }
    }

    public Calendar createCalendar(int hours, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
