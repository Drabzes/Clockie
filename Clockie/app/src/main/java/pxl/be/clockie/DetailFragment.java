package pxl.be.clockie;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;

public class DetailFragment extends Fragment {
    private TextView hiddenId;
    private TimePicker timePicker;
    private EditText nameField;
    private EditText songField;
    private Switch snoozeSwitch;
    private Button saveButton, cancelButton;
    private ToggleButton mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
//    private HashMap<WeekDays, Boolean> repeatDays;

    private Alarm alarm;

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(hiddenId.getText().toString());

                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        new String[]{AlarmContract.AlarmEntry._ID},
                        AlarmContract.AlarmEntry._ID + " = ?",
                        new String[]{String.valueOf(id)}, null);

                if (cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(AlarmContract.AlarmEntry.COLUMN_LABEL, nameField.getText().toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        values.put(AlarmContract.AlarmEntry.COLUMN_TIME, timePicker.getHour() + ":" + timePicker.getMinute());
                    }
                    values.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "06:20");
                    values.put(AlarmContract.AlarmEntry.COLUMN_SONG, songField.getText().toString());
                    values.put(AlarmContract.AlarmEntry.COLUMN_SNOOZE, snoozeSwitch.isChecked());
                    values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, 0);
                    contentResolver.update(AlarmContract.AlarmEntry.CONTENT_URI, values, "_id='" + id + "'", null);
                    Toast.makeText(getContext(), "Alarm opgeslagen", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
                cursor.close();

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

        Alarm alarm = alarms.get(position);
        hiddenId.setText( String.valueOf(alarm.getId()));
        timePicker.setHour(alarm.getTime().get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(alarm.getTime().get(Calendar.MINUTE));
        nameField.setText(alarm.getLabel());
        songField.setText(alarm.getSong());
        snoozeSwitch.setChecked(alarm.isSnooze());
    }
}

