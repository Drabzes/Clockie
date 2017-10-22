package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AlarmAdapter extends ArrayAdapter<Alarm>{
    private final Context context;
    private final List<Alarm> alarms;
    private Switch alarmSwitch;

    public AlarmAdapter(Context context, List<Alarm> alarms){
        super(context, -1, alarms);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView timeTextView = (TextView) rowView.findViewById(R.id.time);
        TextView labelTextView = (TextView) rowView.findViewById(R.id.label);
        alarmSwitch = (Switch) rowView.findViewById(R.id.alarmSwitch);
        Alarm alarm = alarms.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarm.getTime().getTime()));
        labelTextView.setText(alarm.getLabel());
        alarmSwitch.setChecked(alarm.isActive());

        alarmSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                boolean newValue = !alarmSwitch.isChecked();
                alarmSwitch.setChecked(newValue);
                Alarm alarm = alarms.get(position);
                alarm.setActive(alarmSwitch.isChecked());

                if(alarm.isActive()){
                    setAlarm(alarm.getTime().getTimeInMillis(), alarm.getId());
                } else{
                    cancelAlarm(alarm.getId());
                }
            }
        });
        return rowView;
    }

    public void cancelAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(requestCode);
        alarmManager.cancel(pendingIntent);
    }

    public void setAlarm(long timeInMillis, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(requestCode);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(context, "alarm is gezet", Toast.LENGTH_SHORT).show();

    }

    private PendingIntent createPendingIntent(int requestCode){
        Intent intent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
    }
}
