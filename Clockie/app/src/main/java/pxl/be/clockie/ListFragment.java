package pxl.be.clockie;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pxl.be.clockie.data.AlarmContract;
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

        alarms = getAlarmsFromDatabase();

        List<Alarm> activeAlarms = getActiveAlarms();
        checkWeather(activeAlarms);

        //STAAT NU IN WEATHERCHECKER -> IS DAT OK? -> wordt dus om het half uur ofzo uitgevoerd
//        for (Alarm activeAlarm : activeAlarms) {
//            AlarmUtils.setAlarm(activeAlarm);
//        }

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

    private List<Alarm> getAlarmsFromDatabase() {
        List<Alarm> alarms = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projection = AlarmUtils.getProjection();
        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(0);
                String label = cursor.getString(1);
                String time = cursor.getString(2);
                boolean checkRain = cursor.getInt(3) == 1;
                String city = cursor.getString(4);
                String weather = cursor.getString(5);
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

                Alarm alarm = new Alarm(id, calendar, label, active, "0:0", city, days);
                alarms.add(alarm);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return alarms;
    }

    public void checkWeather(List<Alarm> alarms){
        final List<Alarm> alarmsToCheck = alarms;
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                new WeatherChecker().execute(alarmsToCheck);
            }
        }, 0, 1*60*1000);
    }

    public List<Alarm> getActiveAlarms(){
        List<Alarm> activeAlarms = new ArrayList<>();

        for(Alarm alarm:alarms){
            if(alarm.isActive()){
                activeAlarms.add(alarm);
            }
        }
        return activeAlarms;
    }
}

