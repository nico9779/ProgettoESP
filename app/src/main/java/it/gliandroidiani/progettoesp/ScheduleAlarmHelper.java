package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

public class ScheduleAlarmHelper {

    public static void scheduleAlarm(Context context, long alarmID, Alarm alarm){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("Title", alarm.getTitle());
        intent.putExtra("AlarmID", alarmID);
        intent.putExtra("Hours", alarm.getHours());
        intent.putExtra("Minute", alarm.getMinute());
        intent.putExtra("Ringtone", alarm.isRingtone());
        intent.putExtra("Vibration", alarm.isVibration());
        intent.putExtra("Repetition", alarm.getRepetitionType());
        String repetition = alarm.getRepetitionType();
        if(repetition.equals("Una sola volta")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        if(repetition.equals("Giornalmente")){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID*6), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            for (int i = 0; i <= 6; i++) {
                if(alarm.getRepetitionDays()[i]){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarmID*6+i), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar calendar = createCalendar(alarm.getHours(), alarm.getMinute());

                    if(i!=6) {
                        calendar.set(Calendar.DAY_OF_WEEK, i+2);
                    }
                    else{
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    }

                    if(calendar.before(Calendar.getInstance())){
                        calendar.add(Calendar.DATE, 7);
                    }

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (AlarmManager.INTERVAL_DAY)*7,pendingIntent);
                }
            }
        }
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);

        if(alarm.getRepetitionType().equals("Una sola volta") || alarm.getRepetitionType().equals("Giornalmente")) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarm.getId() * 6), intent, 0);
            alarmManager.cancel(pendingIntent);
        }
        else {
            for (int i = 0; i <= 6; i++) {
                if(alarm.getRepetitionDays()[i]){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarm.getId() * 6+i), intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }
    }

    public static void cancelAllAlarm(Context context, AlarmViewModel alarmViewModel) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        List<Alarm> alarms = alarmViewModel.getAllAlarms().getValue();
        for(Alarm alarm:alarms) {
            if(alarm.getRepetitionType().equals("Una sola volta") || alarm.getRepetitionType().equals("Giornalmente")) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarm.getId() * 6), intent, 0);
                alarmManager.cancel(pendingIntent);
            }
            else {
                for (int i = 0; i <= 6; i++) {
                    if(alarm.getRepetitionDays()[i]){
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (alarm.getId() * 6+i), intent, 0);
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        }
    }

    private static Calendar createCalendar(int hours, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
