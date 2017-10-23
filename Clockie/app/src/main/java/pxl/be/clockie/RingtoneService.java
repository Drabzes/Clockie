package pxl.be.clockie;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class RingtoneService extends Service {
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean alarmIsOn = intent.getExtras().getBoolean("alarmIsOn");

        if(alarmIsOn){
            mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
            mediaPlayer.start();

        } else{
            mediaPlayer.release();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "you stopped service", Toast.LENGTH_SHORT).show();
    }
}
