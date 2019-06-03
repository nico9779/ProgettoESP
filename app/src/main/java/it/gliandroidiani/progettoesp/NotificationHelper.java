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

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ID_RINGTONE = "channelID_RINGTONE";
    public static final String CHANNEL_ID_NO_RINGTONE = "channelID_NO_RINGTONE";
    public static final String CHANNEL_NAME_RINGTONE = "progettoesp_RINGTONE";
    public static final String CHANNEL_NAME_NO_RINGTONE = "progettoesp_NO_RINGTONE";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel channelRingtone = new NotificationChannel(CHANNEL_ID_RINGTONE, CHANNEL_NAME_RINGTONE, NotificationManager.IMPORTANCE_HIGH);
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

    public NotificationManager getManager(){
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, long alarmID, boolean ringtone, boolean vibration){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ringtone)
                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_RINGTONE);
            else
                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_NO_RINGTONE);
        }
        else {
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setVibrate(null);
            if(ringtone) {
                builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.alarmclocksound));
            }
            else builder.setSound(null);
        }
        builder.setContentTitle(title)
                .setContentText("Sveglia!!!")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
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
