package pxl.be.clockie;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class RingtoneService extends Service {
    MediaPlayer mediaPlayer;
    String ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean alarmIsOn = intent.getExtras().getBoolean("alarmIsOn");

        if (alarmIsOn) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultTone = getApplicationContext().getResources().getString(R.string.pref_ringtone_default);
            ringtone = sharedPrefs.getString(getString(R.string.pref_ringtone_key),defaultTone);
            mediaPlayer = MediaPlayer.create(this, Uri.parse(ringtone));
            mediaPlayer.start();
        } else {
            mediaPlayer.release();
        }
        return START_NOT_STICKY;
    }
}
