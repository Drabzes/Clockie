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

public class ListFragment extends Fragment {
    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        Calendar time1 = Calendar.getInstance();
        Calendar time2 = Calendar.getInstance();
        Calendar time3 = Calendar.getInstance();

        time1.set(Calendar.HOUR_OF_DAY, 11);
        time1.set(Calendar.MINUTE, 52);
        time1.set(Calendar.SECOND, 0);

        time2.set(Calendar.HOUR_OF_DAY, 19);
        time2.set(Calendar.MINUTE, 52);
        time2.set(Calendar.SECOND, 0);


        time3.set(Calendar.HOUR_OF_DAY, 16);
        time3.set(Calendar.MINUTE, 10);
        time3.set(Calendar.SECOND, 0);

        List<Alarm> alarms = new ArrayList<Alarm>();
        alarms.add(new Alarm(1, time1, "test", true, "06:45", "testSong", true));
        alarms.add(new Alarm(2, time2, "test2", false, "07:45", "testSong2", false));
        alarms.add(new Alarm(3, time3, "test3", false, "", "testSong2", false));

        AlarmAdapter alarmAdapter = new AlarmAdapter(getActivity(), alarms);

        ListView listView = (ListView) view.findViewById(R.id.clockList);
        listView.setAdapter(alarmAdapter);

        return view;
    }
}
