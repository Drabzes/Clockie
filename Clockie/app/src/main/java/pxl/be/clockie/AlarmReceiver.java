package pxl.be.clockie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;


public class AlarmReceiver extends BroadcastReceiver {
    private final static int NOTIFY_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        startRingtoneService(context, intent);
        if(intent.getExtras().getBoolean("alarmIsOn")) {
            showNotification(context, intent);
        }
    }

    private void startRingtoneService(Context context, Intent intent) {
        boolean alarmIsOn = intent.getExtras().getBoolean("alarmIsOn");
        Intent ringtoneIntent = new Intent(context, RingtoneService.class);
        ringtoneIntent.putExtra("alarmIsOn", alarmIsOn);
        context.startService(ringtoneIntent);
    }

    private void showNotification(Context context, Intent intent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.alarm_clock_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.alarm_clock_icon))
                .setContentTitle("Wekker gaat af")
                .setContentText("my message")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(true);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        0
                );
        notificationBuilder.setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }


}
