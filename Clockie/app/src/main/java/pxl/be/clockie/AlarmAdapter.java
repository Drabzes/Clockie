package pxl.be.clockie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class AlarmAdapter extends ArrayAdapter<Alarm>{
    private final Context context;
    private final List<Alarm> alarms;
    @BindView(R.id.alarmSwitch) Switch alarmSwitch;
    private int pos;


    public AlarmAdapter(Context context, List<Alarm> alarms){
        super(context, -1, alarms);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_layout, parent, false);



        TextView timeTextView = (TextView) rowView.findViewById(R.id.time);
        TextView labelTextView = (TextView) rowView.findViewById(R.id.label);
        Switch alarmSwitch = (Switch) rowView.findViewById(R.id.alarmSwitch);

        this.pos = position;
        final Alarm alarm = alarms.get(position);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // do something, the isChecked will be
                // true if the switch is in the On position
                Log.i("alarm!!!",alarm.getTime().getTime().toString() );
                alarm.setActive(isChecked);
                Toast.makeText(context, "Switch is: " + alarm.isActive(), Toast.LENGTH_SHORT).show();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarms.get(position).getTime().getTime()));
        labelTextView.setText(alarms.get(position).getLabel());

        return rowView;
    }
}
