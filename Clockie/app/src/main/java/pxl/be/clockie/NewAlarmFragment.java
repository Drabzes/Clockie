package pxl.be.clockie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

public class NewAlarmFragment extends Fragment{

    private TimePicker time;
    private EditText label;
    private EditText song;
    private Switch snooze;
    private Boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View addFragmentView = inflater.inflate(R.layout.new_alarm_fragment, container, false);

        time = (TimePicker) addFragmentView.findViewById(R.id.time);
        time.setIs24HourView(true);
        return addFragmentView;
    }
}
