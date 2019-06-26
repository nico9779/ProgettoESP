package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/*
Questa classe descrive l'entità nota che appartiene al database.
I parametri che descrivono una nota sono il suo id che viene generato automaticamente,
il titolo e la descrizione
 */

@Entity(tableName = "note_table")
public class Note {

    public Note(String title, String description){
        this.title = title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
