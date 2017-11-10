package pxl.be.clockie.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pxl.be.clockie.Alarm;
import pxl.be.clockie.AlarmReceiver;
import pxl.be.clockie.App;
import pxl.be.clockie.DayOfTheWeek;
import pxl.be.clockie.R;
import pxl.be.clockie.data.AlarmContract;

public abstract class AlarmUtils {

    public static List<Alarm> getAlarmsFromDatabase() {
        List<Alarm> alarms = new ArrayList<>();
        ContentResolver contentResolver = App.getAppContext().getContentResolver();
        String[] projection = AlarmUtils.getProjection();
        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(0);
                String label = cursor.getString(1);
                String time = cursor.getString(2);
                boolean checkRain = cursor.getInt(3) == 1;
                String city = cursor.getString(4);
                boolean active = cursor.getInt(6) == 1;
                boolean monday = cursor.getInt(7) == 1;
                boolean tuesday = cursor.getInt(8) == 1;
                boolean wednesday = cursor.getInt(9) == 1;
                boolean thursday = cursor.getInt(10) == 1;
                boolean friday = cursor.getInt(11) == 1;
                boolean saturday = cursor.getInt(12) == 1;
                boolean sunday = cursor.getInt(13) == 1;
                HashMap<DayOfTheWeek, Boolean> days = new HashMap<>();
                days.put(DayOfTheWeek.SUNDAY, sunday);
                days.put(DayOfTheWeek.MONDAY, monday);
                days.put(DayOfTheWeek.TUESDAY, tuesday);
                days.put(DayOfTheWeek.WEDNESDAY, wednesday);
                days.put(DayOfTheWeek.THURSDAY, thursday);
                days.put(DayOfTheWeek.FRIDAY, friday);
                days.put(DayOfTheWeek.SATURDAY, saturday);

                Calendar calendar = AlarmUtils.getTimeCalendar(time);

                Alarm alarm = new Alarm(id, calendar, label, active, checkRain, city, days);
                alarms.add(alarm);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return alarms;
    }

    public static int findNextWeekDayToSet(List<DayOfTheWeek> days, Calendar calendar) {
        int today = CalendarUtils.getCurrentDayOfWeek();
        calendar.set(Calendar.DAY_OF_WEEK, today);
        int dayToSet = -1;
        int i = 0;
        while (dayToSet == -1 && i < days.size()) {
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

    public static String[] getProjection() {
        String[] projection = new String[]{
                AlarmContract.AlarmEntry._ID,
                AlarmContract.AlarmEntry.COLUMN_LABEL,
                AlarmContract.AlarmEntry.COLUMN_TIME,
                AlarmContract.AlarmEntry.COLUMN_CHECKRAIN,
                AlarmContract.AlarmEntry.COLUMN_CITY,
                AlarmContract.AlarmEntry.COLUMN_WEATHER,
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

    public static Calendar getTimeCalendar(String timeString) {
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

    public static void setAlarm(Alarm alarm) {
        if (alarm.getWeather() != null && alarm.getWeather().toLowerCase().contains("rain")) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
            String key = App.getAppContext().getResources().getString(R.string.pref_rainMinutes_key);
            int rainMinutes = sharedPrefs.getInt(key, 0);
            alarm.getTime().add(Calendar.MINUTE, rainMinutes);
        }
        alarm = setAlarmDay(alarm);
        scheduleAlarm(alarm.getTime(), alarm.getId(), alarm.getDaysToSet().size() > 0, alarm.getLabel());
    }

    private static Alarm setAlarmDay(Alarm alarm) {
        List<DayOfTheWeek> days = alarm.getDaysToSet();
        if (days.size() == 0) {
            if (alarm.getTime().getTimeInMillis() < CalendarUtils.getCurrentTimeInMillis()) {
                alarm.getTime().add(Calendar.DAY_OF_WEEK, 1);
            }
        } else {
            int day = AlarmUtils.findNextWeekDayToSet(days, alarm.getTime());
            alarm.getTime().set(Calendar.DAY_OF_WEEK, day);
            if (day < CalendarUtils.getCurrentDayOfWeek()) {
                alarm.getTime().add(Calendar.DAY_OF_YEAR, 7);
            }
        }
        return alarm;
    }

    private static void scheduleAlarm(Calendar calendar, long id, boolean isRepeat, String alarmLabel) {
        AlarmManager alarmManager = (AlarmManager) App.getAppContext().getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode, isRepeat, alarmLabel);

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);

        Toast.makeText(App.getAppContext(), "alarm gezet: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + "; "
                + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH), Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarm(long id, boolean isRepeat) {
        AlarmManager alarmManager = (AlarmManager) App.getAppContext().getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode, isRepeat, "");
        alarmManager.cancel(pendingIntent);
        Toast.makeText(App.getAppContext(), "alarm gecanceld", Toast.LENGTH_SHORT).show();
    }

    private static PendingIntent createPendingIntent(long id, boolean isRepeat, String alarmLabel) {
        Intent intent = new Intent(App.getAppContext(), AlarmReceiver.class);
        intent.putExtra("alarmIsOn", true);
        intent.putExtra("alarmId", id);
        intent.putExtra("isRepeat", isRepeat);
        intent.putExtra("label", alarmLabel);
        int requestCode = (int) id;
        return PendingIntent.getBroadcast(App.getAppContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
