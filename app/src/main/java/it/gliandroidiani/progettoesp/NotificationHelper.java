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

    public static final String CHANNEL_ID = "channelID";
    public static final String CHANNEL_NAME = "progettoesp";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+getApplicationContext().getPackageName()+"/"+R.raw.alarmclocksound);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(soundUri, audioAttributes);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, long alarmID, boolean vibration){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        else
            builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(title)
                .setContentText("Sveglia!!!")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+getApplicationContext().getPackageName()+"/"+R.raw.alarmclocksound));
        }
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
