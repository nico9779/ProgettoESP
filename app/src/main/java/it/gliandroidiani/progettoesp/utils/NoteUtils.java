package it.gliandroidiani.progettoesp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteUtils {

    public static String dateFromLong(long time){

        DateFormat format=  new SimpleDateFormat("EEE dd MMM  yyyy' at ' hh:mm aaa", Locale.ITALIAN);
        return format.format(new Date(time));

    }
}
