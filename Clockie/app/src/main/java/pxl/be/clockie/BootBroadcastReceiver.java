package pxl.be.clockie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pxl.be.clockie.utils.AlarmUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        List<Alarm> alarms = AlarmUtils.getAlarmsFromDatabase();
        final List<Alarm> alarmsToCheckWeather = new ArrayList<>();
        for(Alarm alarm : alarms){
            if(alarm.isActive()){
                if(alarm.isCheckRain()){
                    alarmsToCheckWeather.add(alarm);
                } else{
                    AlarmUtils.setAlarm(alarm);
                }
            }
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                new WeatherChecker().execute(alarmsToCheckWeather);
            }
        }, 0, 30*60*1000);

    }
}
