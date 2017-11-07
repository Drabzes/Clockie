package pxl.be.clockie;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pxl.be.clockie.data.AlarmContract;

public class WeatherChecker extends AsyncTask<List<Alarm>, Void, List<Alarm>> {


    @Override
    protected List<Alarm> doInBackground(List<Alarm>... alarms) {
        List<Alarm> resultAlarms = new ArrayList<>();
        try {
            for (Alarm alarm : alarms[0]) {
                String city = alarm.getCity();
//                http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=232fe333ebaa17ccbd1e6c1fdfa3f790
                String example = "http://api.openweathermap.org/data/2.5/weather?q=Diepenbeek&APPID=232fe333ebaa17ccbd1e6c1fdfa3f790";
                URL UrlExample = new URL(example);

                String JSONString = APIGetRequest(UrlExample);

                Weather weather = convertJsonStringToWeather(JSONString);
                alarm.setWeather(weather.getMain());
                alarm.setLabel("test");

                Log.e("in WeatherChecker", alarm.getWeather());
                resultAlarms.add(alarm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return resultAlarms;
        }
    }

    @Override
    protected void onPostExecute(List<Alarm> resultAlarms) {
        for (Alarm alarm : resultAlarms) {
            Toast.makeText(App.getAppContext(), alarm.getWeather(), Toast.LENGTH_SHORT).show();
            ContentResolver contentResolver = App.getAppContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(AlarmContract.AlarmEntry.COLUMN_WEATHER, alarm.getWeather());
            values.put(AlarmContract.AlarmEntry.COLUMN_LABEL, alarm.getLabel());
            contentResolver.update(AlarmContract.AlarmEntry.CONTENT_URI, values, "_id='" + alarm.getId() + "'", null);
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
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
        } finally {
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

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
