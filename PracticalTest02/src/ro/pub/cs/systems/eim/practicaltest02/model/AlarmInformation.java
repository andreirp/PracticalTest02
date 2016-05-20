package ro.pub.cs.systems.eim.practicaltest02.model;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

public class AlarmInformation {

    private String hour;
    private String minute;
    private boolean isActive;

    public AlarmInformation() {
        this.hour = null;
        this.minute = null;
        this.isActive = false;
    }

    public AlarmInformation(
            String hour,
            String minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return hour;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return ": " + hour + ": " + minute + "\n\r";
    }

}
