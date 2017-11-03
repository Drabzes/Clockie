package pxl.be.clockie.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import pxl.be.clockie.DayOfTheWeek;
import pxl.be.clockie.data.AlarmContract;

public abstract class AlarmUtils {

    public static int findNextWeekDayToSet(List<DayOfTheWeek> days, Calendar calendar) {
        int today = CalendarUtils.getCurrentDayOfWeek();
        calendar.set(Calendar.DAY_OF_WEEK, today);
        int dayToSet = -1;
        int i = 0;
        while(dayToSet == -1 && i < days.size()) {
            int dayValue = days.get(i).getValue();
            if (dayValue == today) {
                if (calendar.getTimeInMillis() > CalendarUtils.getCurrentTimeInMillis()) {
                    dayToSet = dayValue;
                }
            } else if (dayValue > today) {
                dayToSet = dayValue;
            }
            i++;
        }
        if (dayToSet == -1) {
            dayToSet = days.get(0).getValue();
        }
        return dayToSet;
    }

    public static String[] getProjection(){
        String[] projection = new String[]{
                AlarmContract.AlarmEntry._ID,
                AlarmContract.AlarmEntry.COLUMN_LABEL,
                AlarmContract.AlarmEntry.COLUMN_TIME,
                AlarmContract.AlarmEntry.COLUMN_RAINTIME,
                AlarmContract.AlarmEntry.COLUMN_SONG,
                AlarmContract.AlarmEntry.COLUMN_SNOOZE,
                AlarmContract.AlarmEntry.COLUMN_ACTIVE,
                AlarmContract.AlarmEntry.COLUMN_MONDAY,
                AlarmContract.AlarmEntry.COLUMN_TUESDAY,
                AlarmContract.AlarmEntry.COLUMN_WEDNESDAY,
                AlarmContract.AlarmEntry.COLUMN_THURSDAY,
                AlarmContract.AlarmEntry.COLUMN_FRIDAY,
                AlarmContract.AlarmEntry.COLUMN_SATURDAY,
                AlarmContract.AlarmEntry.COLUMN_SUNDAY,
        };
        return projection;
    }

    public static Calendar getTimeCalendar(String timeString){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(timeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Nieuwe calendar want anders is datum fout (1 jan 1995) en dan gaat alarm altijd meteen af
        Calendar calendarRightDate = Calendar.getInstance();
        calendarRightDate.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendarRightDate.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendarRightDate.set(Calendar.SECOND, 0);
        calendarRightDate.set(Calendar.MILLISECOND, 0);

        return calendarRightDate;
    }

}
