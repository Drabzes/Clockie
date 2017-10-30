package pxl.be.clockie;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;

public class ListFragment extends Fragment {
    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

//        Calendar calendar = Calendar.getInstance();
//        time1.set(Calendar.HOUR_OF_DAY, 16);
//        time1.set(Calendar.MINUTE, 40);
//        time1.set(Calendar.SECOND, 0);
//        time1.set(Calendar.MILLISECOND, 0);


        List<Alarm> alarms = new ArrayList<Alarm>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projection = new String[]{
                AlarmContract.AlarmEntry._ID,
                AlarmContract.AlarmEntry.COLUMN_LABEL,
                AlarmContract.AlarmEntry.COLUMN_TIME,
                AlarmContract.AlarmEntry.COLUMN_RAINTIME,
                AlarmContract.AlarmEntry.COLUMN_SONG,
                AlarmContract.AlarmEntry.COLUMN_SNOOZE,
                AlarmContract.AlarmEntry.COLUMN_ACTIVE,
        };

        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.getCount() == 0) {
            //SEEDING = tijdeling!
            seedDatabase();
            cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);
        }

        if(cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(0);
                String label = cursor.getString(1);
                String time = cursor.getString(2);
                String rainTime = cursor.getString(3);
                String song = cursor.getString(4);
                boolean snooze = cursor.getInt(5) == 1;
                boolean active = cursor.getInt(6) == 1;

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(format.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Alarm alarm = new Alarm(id, calendar, label, active, rainTime, song, snooze);
                alarms.add(alarm);

                cursor.moveToNext();
            }
        }
        AlarmAdapter alarmAdapter = new AlarmAdapter(getActivity(), alarms);

        ListView listView = (ListView) view.findViewById(R.id.clockList);
        listView.setAdapter(alarmAdapter);

        return view;
    }

    private void seedDatabase() {
        ContentValues values = new ContentValues();
        ContentResolver resolver = getActivity().getContentResolver();

        values.put(AlarmContract.AlarmEntry.COLUMN_LABEL, "Werk alarm");
        values.put(AlarmContract.AlarmEntry.COLUMN_TIME, "06:05");
        values.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
        values.put(AlarmContract.AlarmEntry.COLUMN_SONG, "song");
        values.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 0);
        resolver.insert(AlarmContract.AlarmEntry.CONTENT_URI, values);

        ContentValues values2 = new ContentValues();
        values2.put(AlarmContract.AlarmEntry.COLUMN_LABEL, "Weekend");
        values2.put(AlarmContract.AlarmEntry.COLUMN_TIME, "20:23");
        values2.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
        values2.put(AlarmContract.AlarmEntry.COLUMN_SONG, "song");
        values2.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, 0);
        values2.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 1);
        resolver.insert(AlarmContract.AlarmEntry.CONTENT_URI, values2);
    }
}

