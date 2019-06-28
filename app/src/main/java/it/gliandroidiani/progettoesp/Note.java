package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
Questa classe descrive l'entità nota che appartiene al database.
I parametri che descrivono una nota sono il suo id che viene generato automaticamente,
il titolo e la descrizione e il giorno e l'ora on cui è stata presa
 */

@Entity(tableName = "note_table")
public class Note {

    public Note(String title, long mDateTime, String description)
    {
        this.title = title;
        this.mDateTime= mDateTime;
        this.description = description;
    }


//Parametri entità nota

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date")
    private long mDateTime;

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

    public long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(long dateTime)
    {
        mDateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //metodo che ritorna la data in un formato leggibile in stringa
    public String getDateTimeFormatted(Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , context.getResources().getConfiguration().locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(mDateTime));
    }
}
