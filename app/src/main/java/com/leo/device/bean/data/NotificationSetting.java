package com.leo.device.bean.data;

/**
 * @author Leo
 * @date 2019-05-27
 */
public class NotificationSetting {
    private boolean all;
    private int interval;
    private boolean battery;
    private boolean urinary;
    private boolean change;
    private boolean intervalOpen;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isBattery() {
        return battery;
    }

    public void setBattery(boolean battery) {
        this.battery = battery;
    }

    public boolean isUrinary() {
        return urinary;
    }

    public void setUrinary(boolean urinary) {
        this.urinary = urinary;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public boolean isIntervalOpen() {
        return intervalOpen;
    }

    public void setIntervalOpen(boolean intervalOpen) {
        this.intervalOpen = intervalOpen;
    }
}
