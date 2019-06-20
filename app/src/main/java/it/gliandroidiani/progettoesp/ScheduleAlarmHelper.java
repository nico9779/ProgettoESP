package it.gliandroidiani.progettoesp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

/*
Questa classe contiene solamente metodi statici e serve a schedulare e cancellare
gli allarmi in base al tempo selezionato nel momento della creazione della sveglia
e al tipo di ripetizione scelto
 */

public class ScheduleAlarmHelper {


    /*
    Metodo che serve alla schedulazione di un'allarme per una data sveglia.
    L'intent che viene creato contiene tutte le informazioni riguardante a una data sveglia che
    servono alla creazione delle notifiche.
    Il tipo di allarmi che vengono impostate in questo metodo dipendono dal tipo di ripetizione
    che presenta la sveglia.
    I pendingintent hanno bisogno di un requestcode che deve essere unico.
    Questo viene risolto associando a tutti i pendingintent un codice che è pari all'alarmID*6
    in modo tale che per ogni sveglia che imposto ci sia spazio per 6 possibili pendingintent.
    Tutti questi pendingintent potrebbero essere riempiti nel caso in cui la ripetizione coinvolga
    esattamente 6 giorni della settimana.
    Nei casi in cui la ripetizione sia del tipo "Una sola volta" o "Giornalmente" i pendingintent usati
    saranno solamente uno.
     */
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

            /*Nel caso in cui imposto una sveglia che presenta un'orario precedente a quello attuale
              evito che venga triggerata subito aggiungendo al calendario un giorno in più a quello attuale
             */
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


    //Metodo per cancellare gli allarmi associati a una data sveglia
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

    //Metodo per cancellare tutti gli allarmi associati a tutte le sveglie presenti nel database
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

    //Metodo di supporto che permette di creare un calendario con una data ora e minuti
    private static Calendar createCalendar(int hours, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
