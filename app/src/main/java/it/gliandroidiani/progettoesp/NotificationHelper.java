package it.gliandroidiani.progettoesp;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/*
Questa classe è di aiuto per la costruzione delle notifiche che vengono visualizzate ogni volta
che scatta un'allarme.
 */

public class NotificationHelper extends ContextWrapper {

    /*ID e nomi dei due canali utilizzati per costruire le notifiche.
      Uno è utilizzato nel caso l'utente richieda l'uso della suoneria e
      l'altro viceversa.
    */
    public static final String CHANNEL_ID_RINGTONE = "channelID_RINGTONE";
    public static final String CHANNEL_ID_NO_RINGTONE = "channelID_NO_RINGTONE";
    public static final String CHANNEL_NAME_RINGTONE = "progettoesp_RINGTONE";
    public static final String CHANNEL_NAME_NO_RINGTONE = "progettoesp_NO_RINGTONE";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        /*
        Nel caso in cui l'API level del dispositivo sia maggiore di 26 per creare le notifiche
        devo usare i canali
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    //Metodo che costruisce i due canali
    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel channelRingtone = new NotificationChannel(CHANNEL_ID_RINGTONE, CHANNEL_NAME_RINGTONE, NotificationManager.IMPORTANCE_HIGH);
        /*
        Nel canale "progettoesp_RINGTONE" imposto la suoneria che è la risorsa R.raw.alarmclocksound
         */
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.alarmclocksound);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channelRingtone.setSound(soundUri, audioAttributes);
        channelRingtone.setVibrationPattern(null);
        getManager().createNotificationChannel(channelRingtone);

        NotificationChannel channelNoRingtone = new NotificationChannel(CHANNEL_ID_NO_RINGTONE, CHANNEL_NAME_NO_RINGTONE, NotificationManager.IMPORTANCE_HIGH);
        channelNoRingtone.setSound(null, null);
        channelNoRingtone.setVibrationPattern(null);
        getManager().createNotificationChannel(channelNoRingtone);
    }

    //Creo un'istanza del notification manager per creare i canali nel caso in cui non esista o restituisco quella già esistente
    public NotificationManager getManager(){
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    //Questo metodo serve a costruire la notifica
    public NotificationCompat.Builder getChannelNotification(String title, long alarmID, boolean ringtone, boolean vibration){

        //Intent che serve ad accedere all'activity principale quando una notifica viene cliccata
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        /*Nel caso sia presente o meno la suoneria uso il canale corretto per la creazione della notifica.
          Nel caso in cui l'sdk version del dispositivo sia inferiore a oreo utilizzo i metodi
          appropriati per l'impostazione della suoneria.
         */
        if (ringtone)
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_RINGTONE);
        else
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_NO_RINGTONE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setVibrate(null);
            if(ringtone) {
                builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.alarmclocksound));
            }
            else builder.setSound(null);
        }
        builder.setContentTitle(title)
                .setContentText("Sveglia!!!")
                .setSmallIcon(R.drawable.ic_icon_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        /*
        Questo if mi permette di impostare la vibrazione nel caso sia richiesta e utilizzando un pattern
        di questo tipo: 2 secondi di vibrazione e 2 secondi di pausa per un totale di 10 secondi.
         */
        if(vibration) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(new long[] {0, 2000, 2000, 2000, 2000, 2000}, -1));
            } else {
                v.vibrate(new long[] {0, 2000, 2000, 2000, 2000, 2000}, -1);
            }
        }
        return builder;
    }
}
