package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "alarm_table")
public class Alarm {

    public Alarm(String title, int hours, int minute, boolean ringtone, boolean vibration) {
        this.title = title;
        this.hours = hours;
        this.minute = minute;
        this.ringtone = ringtone;
        this.vibration = vibration;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "hours")
    private int hours;

    @ColumnInfo(name = "minute")
    private int minute;

    @ColumnInfo(name = "ringtone")
    private boolean ringtone;

    @ColumnInfo(name = "vibration")
    private boolean vibration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isRingtone() {
        return ringtone;
    }

    public void setRingtone(boolean ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }
}
