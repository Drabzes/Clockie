package pxl.be.clockie;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Titaanje-Laptop on 07/11/2017.
 */
public class WeatherChecker extends AsyncTask<Alarm, Void, Alarm> {
    @Override
    protected Alarm doInBackground(Alarm... alarms) {
        try {
            String example = "http://api.openweathermap.org/data/2.5/weather?q=Diepenbeek&APPID=232fe333ebaa17ccbd1e6c1fdfa3f790";
            URL UrlExample = new URL(example);

            String JSONString = APIGetRequest(UrlExample);

            Weather weather = convertJsonStringToWeather(JSONString);

            if (weather.main.equals("Clear")) {
                alarms[0].setLabel("JoepiGiel");
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return alarms[0];
        }
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
