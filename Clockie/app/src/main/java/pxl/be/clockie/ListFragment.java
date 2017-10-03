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
import java.util.List;

public class ListFragment extends Fragment{
    public ListFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        Alarm alarm1 = new Alarm("07:00", "test", true, "06:45", "testSong", true);
        Alarm alarm2 = new Alarm("07:30", "test2", false, "07:45", "testSong2", false);

        List<Alarm> alarms = new ArrayList<Alarm>();
        alarms.add(alarm1);
        alarms.add(alarm2);

        AlarmAdapter alarmAdapter = new AlarmAdapter(getActivity(), alarms);

        ListView listView = (ListView)view.findViewById(R.id.clockList);
        listView.setAdapter(alarmAdapter);

        return view;
    }
}
