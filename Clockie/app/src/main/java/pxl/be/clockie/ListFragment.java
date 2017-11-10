package pxl.be.clockie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pxl.be.clockie.utils.AlarmUtils;

public class ListFragment extends Fragment {
    private Timer timer;
    private List<Alarm> alarms;

    public ListFragment() {
        timer = new Timer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        alarms = AlarmUtils.getAlarmsFromDatabase();

        List<Alarm> alarmsToCheckWeather = getAlarmsToCheck();
        checkWeather(alarmsToCheckWeather);
        setActiveAlarms();

        AlarmAdapter alarmAdapter = new AlarmAdapter(getActivity(), alarms);

        ListView listView = (ListView) view.findViewById(R.id.clockList);
        listView.setAdapter(alarmAdapter);

        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailListener detailListener = (DetailListener) getActivity();
                detailListener.setFields(position);
            }
        });

        return view;
    }


    private void checkWeather(List<Alarm> alarms){
        final List<Alarm> alarmsToCheck = alarms;
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                new WeatherChecker().execute(alarmsToCheck);
            }
        }, 0, 30*60*1000);
    }

    private List<Alarm> getAlarmsToCheck(){
        List<Alarm> alarmsToCheck = new ArrayList<>();

        for(Alarm alarm:alarms){
            if(alarm.isActive() && alarm.isCheckRain()){
                alarmsToCheck.add(alarm);
            }
        }
        return alarmsToCheck;
    }

    private List<Alarm> setActiveAlarms(){
        List<Alarm> activeAlarms = new ArrayList<>();

        for(Alarm alarm:alarms){
            if(alarm.isActive()){
                AlarmUtils.setAlarm(alarm);
            }
        }
        return activeAlarms;
    }
}

