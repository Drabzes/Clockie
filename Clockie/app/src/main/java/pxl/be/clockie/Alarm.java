package pxl.be.clockie;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Alarm {
    private long id;
    private Calendar time;
    private String label;
    private boolean active;
    private String rainTime;
    private String city;
    private HashMap<DayOfTheWeek, Boolean> days;
    private String weather;

    public Alarm(long id, Calendar time, String label, boolean active, String rainTime, String city, HashMap<DayOfTheWeek, Boolean> days) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.active = active;
        this.rainTime = rainTime;
        this.city = city;
        this.days = days;
        this.weather = null;
    }

    public long getId() {
        return id;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRainTime() {
        return rainTime;
    }

    public void setRainTime(String rainTime) {
        this.rainTime = rainTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String song) {
        this.city = city;
    }

    public HashMap<DayOfTheWeek, Boolean> getDays() {
        return days;
    }

    public void setDays(HashMap<DayOfTheWeek, Boolean> days) {
        this.days = days;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public List<DayOfTheWeek> getDaysToSet(){
        List<DayOfTheWeek> daysToSet = new ArrayList<>();
        for (Map.Entry<DayOfTheWeek,Boolean> pair : days.entrySet()){
            if(pair.getValue()) {
                daysToSet.add(pair.getKey());
            }
        }
        Collections.sort(daysToSet, new Comparator<DayOfTheWeek>() {
            @Override
            public int compare(DayOfTheWeek d1, DayOfTheWeek d2) {
                return d1.getValue() - d2.getValue();
            }
        });

        return daysToSet;
    }
}
