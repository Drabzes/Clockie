package pxl.be.clockie;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;

public class ListFragment extends Fragment {
    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        List<Alarm> alarms = getAlarmsFromDatabase();

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

    private void seedDatabase() {
        ContentValues values = new ContentValues();
        ContentResolver resolver = getActivity().getContentResolver();

        values.put(AlarmContract.AlarmEntry.COLUMN_LABEL, "Werk alarm");
        values.put(AlarmContract.AlarmEntry.COLUMN_TIME, "06:05");
        values.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
        values.put(AlarmContract.AlarmEntry.COLUMN_SONG, "song");
        values.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_MONDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_TUESDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_WEDNESDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_THURSDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_FRIDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_SATURDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_SUNDAY, 0);
        resolver.insert(AlarmContract.AlarmEntry.CONTENT_URI, values);

        ContentValues values2 = new ContentValues();
        values2.put(AlarmContract.AlarmEntry.COLUMN_LABEL, "Weekend");
        values2.put(AlarmContract.AlarmEntry.COLUMN_TIME, "20:23");
        values2.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
        values2.put(AlarmContract.AlarmEntry.COLUMN_SONG, "song");
        values2.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, 0);
        values2.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_MONDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_TUESDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_WEDNESDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_THURSDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_FRIDAY, 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_SATURDAY, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_SUNDAY, 1);
        resolver.insert(AlarmContract.AlarmEntry.CONTENT_URI, values2);
    }

    private List<Alarm> getAlarmsFromDatabase() {
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
                AlarmContract.AlarmEntry.COLUMN_MONDAY,
                AlarmContract.AlarmEntry.COLUMN_TUESDAY,
                AlarmContract.AlarmEntry.COLUMN_WEDNESDAY,
                AlarmContract.AlarmEntry.COLUMN_THURSDAY,
                AlarmContract.AlarmEntry.COLUMN_FRIDAY,
                AlarmContract.AlarmEntry.COLUMN_SATURDAY,
                AlarmContract.AlarmEntry.COLUMN_SUNDAY,
        };

        Cursor cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.getCount() == 0) {
            //SEEDING = tijdeling!
            seedDatabase();
            cursor = contentResolver.query(AlarmContract.AlarmEntry.CONTENT_URI, projection, null, null, null);
        }

        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(0);
                String label = cursor.getString(1);
                String time = cursor.getString(2);
                String rainTime = cursor.getString(3);
                String song = cursor.getString(4);
                boolean snooze = cursor.getInt(5) == 1;
                boolean active = cursor.getInt(6) == 1;
                boolean monday = cursor.getInt(7) == 1;
                boolean tuesday = cursor.getInt(8) == 1;
                boolean wednesday = cursor.getInt(9) == 1;
                boolean thursday = cursor.getInt(10) == 1;
                boolean friday = cursor.getInt(11) == 1;
                boolean saturday = cursor.getInt(12) == 1;
                boolean sunday = cursor.getInt(13) == 1;
                HashMap<DayOfTheWeek, Boolean> days = new HashMap<>();
                days.put(DayOfTheWeek.MONDAY, monday);
                days.put(DayOfTheWeek.TUESDAY, tuesday);
                days.put(DayOfTheWeek.WEDNESDAY, wednesday);
                days.put(DayOfTheWeek.THURSDAY, thursday);
                days.put(DayOfTheWeek.FRIDAY, friday);
                days.put(DayOfTheWeek.SATURDAY, saturday);
                days.put(DayOfTheWeek.SUNDAY, sunday);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(format.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Alarm alarm = new Alarm(id, calendar, label, active, rainTime, song, snooze, days);

                alarms.add(alarm);

                cursor.moveToNext();
            }
        }
        return alarms;
    }
}

