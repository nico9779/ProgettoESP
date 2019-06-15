package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.TypeConverter;

/*
Questa classe viene utilizzata dal database per memorizzare l'array di booleani
repetitionDays all'interno delle istanze dell'entità sveglia trasformandolo
in una stringa che è un formato comprensibile dal database Room
 */

public class Converter {

    //Metodo che converte l'array di booleani in stringa
    @TypeConverter
    public boolean[] gettingArrayFromString(String string){
        boolean[] booleanArray = new boolean[7];
        String[] array = string.split(",");
        for (int i = 0; i < array.length; i++) {
            booleanArray[i] = Boolean.parseBoolean(array[i]);
        }
        return booleanArray;
    }


    //Metodo che converte una stringa in array di booleani
    @TypeConverter
    public String writingStringFromArray(boolean[] booleanArray){
        String s = "";
        for (boolean value : booleanArray) {
            s = s + value + ",";
        }
        return s;
    }
}
