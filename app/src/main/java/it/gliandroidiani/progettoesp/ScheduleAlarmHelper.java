package it.gliandroidiani.progettoesp;

/*
Questa interfaccia dichiara il metodo che viene utilizzato dalle activity e dai fragment
per schedulare un allarme
 */

import java.util.Calendar;

public interface ScheduleAlarmHelper {

    void scheduleAlarm(long alarmID, Alarm alarm);
    Calendar createCalendar(int hours, int minute);
}
