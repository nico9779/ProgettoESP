package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "alarm_table")
public class Alarm {

    public Alarm(String title, int hours, int minute, boolean ringtone, boolean vibration, boolean active, String repetitionType) {
        this.title = title;
        this.hours = hours;
        this.minute = minute;
        this.ringtone = ringtone;
        this.vibration = vibration;
        this.active = active;
        this.repetitionType = repetitionType;
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

    @ColumnInfo(name = "active")
    private boolean active;

    @ColumnInfo(name = "repetition_type")
    private String repetitionType;

    @ColumnInfo(name = "repetition_days")
    private boolean[] repetitionDays;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRepetitionType() {
        return repetitionType;
    }

    public void setRepetitionType(String repetitionType) {
        this.repetitionType = repetitionType;
    }

    public boolean[] getRepetitionDays() {
        return repetitionDays;
    }

    public void setRepetitionDays(boolean[] repetitionDays) {
        this.repetitionDays = repetitionDays;
    }
}
