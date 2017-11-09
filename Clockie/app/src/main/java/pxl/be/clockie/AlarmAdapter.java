package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;
import pxl.be.clockie.utils.AlarmUtils;
import pxl.be.clockie.utils.CalendarUtils;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
    private final Context context;
    private final List<Alarm> alarms;
    private TextView mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView;
    private TextView fridayTextView, saturdayTextView,  sundayTextView;

    AlarmAdapter(Context context, List<Alarm> alarms) {
        super(context, -1, alarms);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView timeTextView = (TextView) rowView.findViewById(R.id.time);
        TextView labelTextView = (TextView) rowView.findViewById(R.id.label);
        final Switch alarmSwitch = (Switch) rowView.findViewById(R.id.alarmSwitch);
        ImageView rainIcon = (ImageView) rowView.findViewById(R.id.rainIcon);
        TextView rainTimeTextView = (TextView) rowView.findViewById(R.id.clockRain);
        mondayTextView = (TextView) rowView.findViewById(R.id.mondayLabel);
        tuesdayTextView = (TextView) rowView.findViewById(R.id.tuesdayLabel);
        wednesdayTextView = (TextView) rowView.findViewById(R.id.wednesdayLabel);
        thursdayTextView = (TextView) rowView.findViewById(R.id.thursdayLabel);
        fridayTextView = (TextView) rowView.findViewById(R.id.fridayLabel);
        saturdayTextView = (TextView) rowView.findViewById(R.id.saturdayLabel);
        sundayTextView = (TextView) rowView.findViewById(R.id.sundayLabel);

        Alarm alarm = alarms.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarm.getTime().getTime()));
        labelTextView.setText(alarm.getLabel());
        alarmSwitch.setChecked(alarm.isActive());

        if (alarm.isCheckRain()) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
            String key = App.getAppContext().getResources().getString(R.string.pref_rainMinutes_key);
            int rainMinutes = sharedPrefs.getInt(key, 0);

            rainIcon.setVisibility(View.VISIBLE);
            Calendar rainTime = (Calendar) alarm.getTime().clone();
            rainTime.add(Calendar.MINUTE, rainMinutes);
            rainTimeTextView.setText(sdf.format(rainTime.getTime()));
        } else{
            rainIcon.setVisibility(View.GONE);
            rainTimeTextView.setVisibility(View.GONE);

        }

        setDayButtons(alarm);

        alarmSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Alarm alarm = alarms.get(position);
                alarm.setActive(alarmSwitch.isChecked());

                int isActive = alarm.isActive() ? 1 : 0;

                ContentResolver contentResolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(AlarmContract.AlarmEntry.COLUMN_ACTIVE, isActive);
                contentResolver.update(AlarmContract.AlarmEntry.CONTENT_URI, values, "_id='" + alarm.getId() + "'", null);

                if (alarm.isActive()) {
                    AlarmUtils.setAlarm(alarm);
                } else {
                    AlarmUtils.cancelAlarm(alarm.getId(), alarm.getDaysToSet().size() > 0);
                }
            }
        });
        return rowView;
    }

    private void setDayButtons(Alarm alarm) {
        int accentColor = ContextCompat.getColor(context, R.color.colorPrimary);
        if (alarm.getDays().get(DayOfTheWeek.MONDAY)) {
            mondayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.TUESDAY)) {
            tuesdayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.WEDNESDAY)) {
            wednesdayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.THURSDAY)) {
            thursdayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.FRIDAY)) {
            fridayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.SATURDAY)) {
            saturdayTextView.setTextColor(accentColor);
        }
        if (alarm.getDays().get(DayOfTheWeek.SUNDAY)) {
            sundayTextView.setTextColor(accentColor);
        }
    }
}
