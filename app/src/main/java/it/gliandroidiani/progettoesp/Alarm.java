package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/*
Questa classe descrive l'entità sveglia che appartiene al database.
I parametri che descrivono una sveglia sono il suo id che viene generato automaticamente,
il titolo, l'ora suddivisa in ore e minuti, la presenza o meno della suoneria e della vibrazione
rappresentati da un valore booleano, il valore booleano active che descrive se la sveglia è attiva
oppure no (se attiva allora significa che può squillare), una stringa che descrive il tipo di ripetizione
adottato ("Una sola volta", "Giornalmente", "Giorni della settimana") e infine un vettore di booleani
che rappresentano i giorni in cui la sveglia deve squillare nel caso in cui la ripetizione scelta sia
"Giorni della settimana"
 */

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
        /*
        Nel caso in cui il tipo di ripetizione sia "Una sola volta" o "Giornalmente" inizializzo il
        vettore repetitionDays tutto a false in quanto questo vettore serve a indicare i giorni in
        cui la sveglia deve essere ripetuta e viene usato solo nel caso in cui il tipo di ripetizione
        sia "Giorni della settimana"
         */
        if(repetitionType.equals("Una sola volta") || repetitionType.equals("Giornalmente")){
            this.repetitionDays = new boolean[] {false, false, false, false, false, false, false};
        }
    }

    //Parametri entità allarme

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

    //Metodi Getter and Setter

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
