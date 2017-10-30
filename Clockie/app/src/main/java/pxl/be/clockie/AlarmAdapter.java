package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AlarmAdapter extends ArrayAdapter<Alarm>{
    private final Context context;
    private final List<Alarm> alarms;

    AlarmAdapter(Context context, List<Alarm> alarms){
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
        final Switch alarmSwitch = (Switch) rowView.findViewById(R.id.alarmSwitch);
        Alarm alarm = alarms.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarm.getTime().getTime()));
        labelTextView.setText(alarm.getLabel());
        alarmSwitch.setChecked(alarm.isActive());

        if(alarm.isActive()) {
            setAlarm(alarm.getTime().getTimeInMillis(), alarm.getId());
        }

        alarmSwitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
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

    private void cancelAlarm(long id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "alarm gecanceld", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(long timeInMillis, long id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

        Date date = new Date(timeInMillis);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        Toast.makeText(context, "alarm is gezet: " + dateFormatted, Toast.LENGTH_SHORT).show();

    }

    private PendingIntent createPendingIntent(long id){
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmIsOn", true);
        int requestCode = (int) id;
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
