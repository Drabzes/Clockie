package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
    private final Context context;
    private final List<Alarm> alarms;

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
        TextView rainTimeTextView = (TextView) rowView.findViewById(R.id.clockRain);
        TextView mondayTextView = (TextView) rowView.findViewById(R.id.mondayLabel);
        TextView tuesdayTextView = (TextView) rowView.findViewById(R.id.tuesdayLabel);
        TextView wednesdayTextView = (TextView) rowView.findViewById(R.id.wednesdayLabel);
        TextView thursdayTextView = (TextView) rowView.findViewById(R.id.thursdayLabel);
        TextView fridayTextView = (TextView) rowView.findViewById(R.id.fridayLabel);
        TextView saturdayTextView = (TextView) rowView.findViewById(R.id.saturdayLabel);
        TextView sundayTextView = (TextView) rowView.findViewById(R.id.sundayLabel);

        Alarm alarm = alarms.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarm.getTime().getTime()));
        labelTextView.setText(alarm.getLabel());
        alarmSwitch.setChecked(alarm.isActive());
        rainTimeTextView.setText(alarm.getRainTime());

        int accentColor = ContextCompat.getColor(context, R.color.colorPrimary);
        List<DayOfTheWeek> daysToSet = new ArrayList<>();
        if (alarm.getDays().get(DayOfTheWeek.MONDAY)) {
            mondayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.MONDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.TUESDAY)) {
            tuesdayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.TUESDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.WEDNESDAY)) {
            wednesdayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.WEDNESDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.THURSDAY)) {
            thursdayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.THURSDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.FRIDAY)) {
            fridayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.FRIDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.SATURDAY)) {
            saturdayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.SATURDAY);
        }
        if (alarm.getDays().get(DayOfTheWeek.SUNDAY)) {
            sundayTextView.setTextColor(accentColor);
            daysToSet.add(DayOfTheWeek.SUNDAY);
        }

        if (alarm.isActive()) {
            if (daysToSet.size() == 0) {
                Calendar calendar = Calendar.getInstance();
                int today = calendar.get(Calendar.DAY_OF_WEEK);

                if (alarm.getTime().getTimeInMillis() < System.currentTimeMillis()) {
                    alarm.getTime().set(Calendar.DAY_OF_WEEK, today + 1);
                } else {
                    alarm.getTime().set(Calendar.DAY_OF_WEEK, today);
                }
                setAlarm(alarm.getTime(), alarm.getId());
            }
            //            else {
//                for (DayOfTheWeek day : alarm.getDaysToSet()) {
//                    setRepeatingAlarm(alarm.getTime().getTimeInMillis(), alarm.getId(), day);
//                }
//            }
        }


        alarmSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Alarm alarm = alarms.get(position);
                alarm.setActive(alarmSwitch.isChecked());

                if (alarm.isActive()) {
                    setAlarm(alarm.getTime(), alarm.getId());
                } else {
                    cancelAlarm(alarm.getId());
                }
            }
        });
        return rowView;
    }

    private void cancelAlarm(long id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "alarm gecanceld", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(Calendar calendar, long id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode);

        long timeInMillis = calendar.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Date date = new Date(timeInMillis);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        Toast.makeText(context, "alarm is gezet: " + dateFormatted + ", voor dag: " + calendar.get(Calendar.DAY_OF_WEEK), Toast.LENGTH_SHORT).show();
    }

    private void setRepeatingAlarm(long timeInMillis, long id, DayOfTheWeek day) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int requestCode = (int) id;
//        PendingIntent pendingIntent = createPendingIntent(requestCode);
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, yourIntent);
//
//
//        Date date = new Date(timeInMillis);
//        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
//        String dateFormatted = formatter.format(date);
//        Toast.makeText(context, "alarm is gezet: " + dateFormatted, Toast.LENGTH_SHORT).show();

    }

    private PendingIntent createPendingIntent(long id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmIsOn", true);
        int requestCode = (int) id;
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
