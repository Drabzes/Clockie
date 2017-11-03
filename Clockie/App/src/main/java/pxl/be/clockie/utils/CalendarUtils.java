package pxl.be.clockie.utils;

import java.util.Calendar;

public abstract class CalendarUtils {
    public static long getCurrentTimeInMillis(){
        return Calendar.getInstance().getTimeInMillis();
    }

    public static int getCurrentDayOfWeek(){
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }
}
