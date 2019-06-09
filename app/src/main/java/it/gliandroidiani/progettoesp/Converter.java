package it.gliandroidiani.progettoesp;

import android.arch.persistence.room.TypeConverter;

public class Converter {
    @TypeConverter
    public boolean[] gettingArrayFromString(String string){
        boolean[] booleanArray = new boolean[7];
        String[] array = string.split(",");
        for (int i = 0; i < array.length; i++) {
            booleanArray[i] = Boolean.parseBoolean(array[i]);
        }
        return booleanArray;
    }

    @TypeConverter
    public String writingStringFromArray(boolean[] booleanArray){
        String s = "";
        for (boolean value : booleanArray) {
            s = s + value + ",";
        }
        return s;
    }
}
