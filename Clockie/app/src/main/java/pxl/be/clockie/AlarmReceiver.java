package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;
import pxl.be.clockie.utils.AlarmUtils;
import pxl.be.clockie.utils.CalendarUtils;


public class AlarmReceiver extends BroadcastReceiver {
    private final static int NOTIFY_ID = 1;
    private ContentResolver contentResolver;
    private Context context;
    private Intent intent;
    private long alarmId;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        if (intent.getExtras().getBoolean("alarmIsOn")) {
            MainActivity.setVisibilityStopButton(View.VISIBLE);
            showNotification(context);

            alarmId = intent.getExtras().getLong("alarmId");
            contentResolver = context.getContentResolver();

            if (intent.getExtras().getBoolean("isRepeat")) {
                setNewAlarm();
            } else {
                setAlarmInactive();
            }
        } else{
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NOTIFY_ID);
            MainActivity.setVisibilityStopButton(View.GONE);
        }
        startRingtoneService(context, intent);
    }

    private void setAlarmInactive() {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 0);
        contentResolver.update(AlarmContract.AlarmEntry.CONTENT_URI, values, "_id='" + alarmId + "'", null);
    }

    private void setNewAlarm() {
        String[] projection = new String[]{
                AlarmContract.AlarmEntry.COLUMN_TIME,
                AlarmContract.AlarmEntry.COLUMN_MONDAY,
                AlarmContract.AlarmEntry.COLUMN_TUESDAY,
                AlarmContract.AlarmEntry.COLUMN_WEDNESDAY,
                AlarmContract.AlarmEntry.COLUMN_THURSDAY,
                AlarmContract.AlarmEntry.COLUMN_FRIDAY,
                AlarmContract.AlarmEntry.COLUMN_SATURDAY,
                AlarmContract.AlarmEntry.COLUMN_SUNDAY,
        };
        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, AlarmContract.AlarmEntry._ID + " = ?", new String[]{String.valueOf(alarmId)}, null);
        cursor.moveToFirst();
        List<DayOfTheWeek> days = new ArrayList<>();
        if (cursor.getInt(1) == 1) {
            days.add(DayOfTheWeek.MONDAY);
        }
        if (cursor.getInt(2) == 1) {
            days.add(DayOfTheWeek.TUESDAY);
        }
        if (cursor.getInt(3) == 1) {
            days.add(DayOfTheWeek.WEDNESDAY);
        }
        if (cursor.getInt(4) == 1) {
            days.add(DayOfTheWeek.THURSDAY);
        }
        if (cursor.getInt(5) == 1) {
            days.add(DayOfTheWeek.FRIDAY);
        }
        if (cursor.getInt(6) == 1) {
            days.add(DayOfTheWeek.SATURDAY);
        }
        if (cursor.getInt(7) == 1) {
            days.add(DayOfTheWeek.SUNDAY);
        }
        String time = cursor.getString(0);
        cursor.close();

        Calendar calendar = AlarmUtils.getTimeCalendar(time);
        int nextAlarmDay = AlarmUtils.findNextWeekDayToSet(days, calendar);

        calendar.set(Calendar.DAY_OF_WEEK, nextAlarmDay);
        if (nextAlarmDay < CalendarUtils.getCurrentDayOfWeek()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) alarmId;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),pendingIntent),pendingIntent);

        Toast.makeText(context, "alarm gezet: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + "; "
                + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH), Toast.LENGTH_SHORT).show();
    }

    private void startRingtoneService(Context context, Intent intent) {
        boolean alarmIsOn = intent.getExtras().getBoolean("alarmIsOn");
        Intent ringtoneIntent = new Intent(context, RingtoneService.class);
        ringtoneIntent.putExtra("alarmIsOn", alarmIsOn);
        context.startService(ringtoneIntent);
    }

    private void showNotification(Context context) {
        String label = intent.getExtras().getString("label");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.alarm_clock_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.alarm_clock_icon))
                .setContentTitle("Wekker!")
                .setContentText(label)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);

        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.putExtra("alarmIsOn", false);

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, (int)alarmId, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.addAction(R.drawable.stop_button, "Stop wekker", stopPendingIntent);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
