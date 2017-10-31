package pxl.be.clockie;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;

public class DetailFragment extends Fragment {
    private TextView hiddenId;
    private TimePicker timePicker;
    private EditText nameField;
    private EditText songField;
    private Switch snoozeSwitch;
    private Button saveButton, cancelButton;
    private FloatingActionButton deleteButton;
    private ToggleButton mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    private HashMap<DayOfTheWeek, Boolean> days;
    private Alarm alarm;
    private ContentResolver contentResolver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.detail_fragment, container, false);

        hiddenId = (TextView) fragmentView.findViewById(R.id.hiddenId);
        timePicker = (TimePicker) fragmentView.findViewById(R.id.time);
        timePicker.setIs24HourView(true);
        nameField = (EditText) fragmentView.findViewById(R.id.label);
        songField = (EditText) fragmentView.findViewById(R.id.song);
        snoozeSwitch = (Switch) fragmentView.findViewById(R.id.switchSnooze);
        mondayButton = (ToggleButton) fragmentView.findViewById(R.id.monday);
        tuesdayButton = (ToggleButton) fragmentView.findViewById(R.id.tuesday);
        wednesdayButton = (ToggleButton) fragmentView.findViewById(R.id.wednesday);
        thursdayButton = (ToggleButton) fragmentView.findViewById(R.id.thursday);
        fridayButton = (ToggleButton) fragmentView.findViewById(R.id.friday);
        saturdayButton = (ToggleButton) fragmentView.findViewById(R.id.saturday);
        sundayButton = (ToggleButton) fragmentView.findViewById(R.id.sunday);
        saveButton = (Button) fragmentView.findViewById(R.id.buttonSave);
        cancelButton = (Button) fragmentView.findViewById(R.id.buttonCancel);
        deleteButton = (FloatingActionButton) fragmentView.findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(hiddenId.getText().toString());
                contentResolver = getActivity().getContentResolver();
                ContentValues values = putValues();

                if (isExistingAlarm(id)) {
                    contentResolver.update(AlarmContract.AlarmEntry.CONTENT_URI, values, "_id='" + id + "'", null);

                } else{
                    contentResolver.insert(AlarmContract.AlarmEntry.CONTENT_URI, values);
                }

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return fragmentView;
    }

    private ContentValues putValues() {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.COLUMN_LABEL, nameField.getText().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            values.put(AlarmContract.AlarmEntry.COLUMN_TIME, timePicker.getHour() + ":" + timePicker.getMinute());
        } else{
            values.put(AlarmContract.AlarmEntry.COLUMN_TIME, timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
        }
        values.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
        values.put(AlarmContract.AlarmEntry.COLUMN_SONG, songField.getText().toString());
        values.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, snoozeSwitch.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 1);
        values.put(AlarmContract.AlarmEntry.COLUMN_MONDAY, mondayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_TUESDAY, tuesdayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_WEDNESDAY, wednesdayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_THURSDAY, thursdayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_FRIDAY, fridayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_SATURDAY, saturdayButton.isChecked() ? 1 : 0);
        values.put(AlarmContract.AlarmEntry.COLUMN_SUNDAY, sundayButton.isChecked() ? 1 : 0);
        return values;
    }

    private boolean isExistingAlarm(long id) {
        Cursor cursor = contentResolver.query(
                AlarmContract.AlarmEntry.CONTENT_URI,
                new String[]{AlarmContract.AlarmEntry._ID},
                AlarmContract.AlarmEntry._ID + " = ?",
                new String[]{String.valueOf(id)}, null);
        if (cursor.moveToFirst()) {
            cursor.close();

            return true;
        } else {
            cursor.close();
            return false;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void setAllFields(int position) {
        List<Alarm> alarms = new ArrayList<>();

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
        cursor.moveToFirst();
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

        Alarm alarm = alarms.get(position);
        hiddenId.setText(String.valueOf(alarm.getId()));
        timePicker.setHour(alarm.getTime().get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(alarm.getTime().get(Calendar.MINUTE));
        nameField.setText(alarm.getLabel());
        songField.setText(alarm.getSong());
        snoozeSwitch.setChecked(alarm.isSnooze());
        mondayButton.setChecked(alarm.getDays().get(DayOfTheWeek.MONDAY));
        tuesdayButton.setChecked(alarm.getDays().get(DayOfTheWeek.TUESDAY));
        wednesdayButton.setChecked(alarm.getDays().get(DayOfTheWeek.WEDNESDAY));
        thursdayButton.setChecked(alarm.getDays().get(DayOfTheWeek.THURSDAY));
        fridayButton.setChecked(alarm.getDays().get(DayOfTheWeek.FRIDAY));
        saturdayButton.setChecked(alarm.getDays().get(DayOfTheWeek.SATURDAY));
        sundayButton.setChecked(alarm.getDays().get(DayOfTheWeek.SUNDAY));
    }
}

