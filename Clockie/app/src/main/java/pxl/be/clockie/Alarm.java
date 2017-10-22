package pxl.be.clockie;

import java.util.Calendar;

public class Alarm {
    private int id;
    private Calendar time;
    private String label;
    private boolean active;
    private String rainTime;
    private String song;
    private boolean snooze;

    public Alarm(int id, Calendar time, String label, boolean active, String rainTime, String song, boolean snooze) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.active = active;
        this.rainTime = rainTime;
        this.song = song;
        this.snooze = snooze;
    }

    public int getId() {
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

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public boolean isSnooze() {
        return snooze;
    }

    public void setSnooze(boolean snooze) {
        this.snooze = snooze;
    }
}
