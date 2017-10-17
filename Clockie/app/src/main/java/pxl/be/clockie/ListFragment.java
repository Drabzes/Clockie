package pxl.be.clockie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ListFragment extends Fragment{
    public ListFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        Calendar time1 = Calendar.getInstance();
        Calendar time2 = Calendar.getInstance();

        time1.set(Calendar.HOUR,6);
        time1.set(Calendar.MINUTE,33);

        time2.set(Calendar.HOUR,2);
        time2.set(Calendar.MINUTE,55);

        Alarm alarm1 = new Alarm(time1, "test", true, "06:45", "testSong", true);
        Alarm alarm2 = new Alarm(time2, "test2", false, "07:45", "testSong2", false);

        List<Alarm> alarms = new ArrayList<Alarm>();
        alarms.add(alarm1);
        alarms.add(alarm2);

        AlarmAdapter alarmAdapter = new AlarmAdapter(getActivity(), alarms);

        ListView listView = (ListView)view.findViewById(R.id.clockList);
        listView.setAdapter(alarmAdapter);

        return view;
    }
}
