package pxl.be.clockie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;
import pxl.be.clockie.utils.AlarmUtils;
import pxl.be.clockie.utils.CalendarUtils;

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

        Toast.makeText(context, "weatherChecker", Toast.LENGTH_SHORT).show();
        new weatherChecker().execute(alarm);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeTextView.setText(sdf.format(alarm.getTime().getTime()));
        labelTextView.setText(alarm.getLabel());
        alarmSwitch.setChecked(alarm.isActive());
        rainTimeTextView.setText(alarm.getRainTime());

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

        if (alarm.isActive()) {
            alarm = setAlarmDay(alarm);
            setAlarm(alarm.getTime(), alarm.getId(), alarm.getDaysToSet().size()>0, alarm.getLabel());
        }

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
                    alarm = setAlarmDay(alarm);
                    setAlarm(alarm.getTime(), alarm.getId(), alarm.getDaysToSet().size()>0, alarm.getLabel());
                } else {
                    cancelAlarm(alarm.getId(), alarm.getDaysToSet().size()>0);
                }
            }
        });
        return rowView;
    }

    private Alarm setAlarmDay(Alarm alarm) {
        List<DayOfTheWeek> days = alarm.getDaysToSet();
        if (days.size() == 0) {
            if (alarm.getTime().getTimeInMillis() < CalendarUtils.getCurrentTimeInMillis()) {
                alarm.getTime().add(Calendar.DAY_OF_WEEK, 1);
            }
        } else {
            int day = AlarmUtils.findNextWeekDayToSet(days, alarm.getTime());
            alarm.getTime().set(Calendar.DAY_OF_WEEK, day);
            if (day < CalendarUtils.getCurrentDayOfWeek()) {
                alarm.getTime().add(Calendar.DAY_OF_YEAR, 7);
            }
        }
        return alarm;
    }

    private void cancelAlarm(long id, boolean isRepeat) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode, isRepeat, "");
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "alarm gecanceld", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(Calendar calendar, long id, boolean isRepeat, String alarmLabel) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) id;
        PendingIntent pendingIntent = createPendingIntent(requestCode, isRepeat, alarmLabel);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(context, "alarm gezet: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + "; "
                + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH), Toast.LENGTH_SHORT).show();
    }

    private PendingIntent createPendingIntent(long id, boolean isRepeat, String alarmLabel) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmIsOn", true);
        intent.putExtra("alarmId", id);
        intent.putExtra("isRepeat", isRepeat);
        intent.putExtra("label", alarmLabel);
        int requestCode = (int) id;
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static class weatherChecker extends AsyncTask<Alarm, Void, Alarm> {
        @Override
        protected Alarm doInBackground(Alarm... alarms) {
            try {
                String example = "http://api.openweathermap.org/data/2.5/weather?q=Diepenbeek&APPID=232fe333ebaa17ccbd1e6c1fdfa3f790";
                URL UrlExample = new URL (example);

                String JSONString = APIGetRequest(UrlExample);

                Weather weather = convertJsonStringToWeather(JSONString);

                if (weather.main.equals("Clear")) {
                    alarms[0].setLabel("JoepiGiel");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Alarm result) {
            //Hier functie aanroepen op alarm in het geheugen te steken
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private static String APIGetRequest(URL url) throws MalformedURLException {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            return e.getMessage();
        }
        return "";
    }

    private static Weather convertJsonStringToWeather(String data) {
        Weather weather = new Weather();
        try {
            JSONObject jObj = new JSONObject(data);
            JSONArray jArr = jObj.getJSONArray("weather");
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.setId(getInt("id", JSONWeather));
            weather.setDescription(getString("description", JSONWeather));
            weather.setMain(getString("main", JSONWeather));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return weather;
        }
    }

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException, JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
