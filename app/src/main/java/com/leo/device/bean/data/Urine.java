package com.leo.device.bean.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Leo
 * @date 2019-05-05
 */
@Entity(primaryKeys = {"timestamp", "deviceId"})
public class Urine {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private long timestamp;

    @NonNull
    private String deviceId;

    private int urineCapacity;

    private String createTime;

    @Ignore
    private Integer id;

    @Ignore
    private Integer battery;

    private String phoneImei;

    private String phoneNumber;

    public long getTimestamp() {
        return timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getUrineCapacity() {
        return urineCapacity;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getId() {
        return id;
    }

    public String getPhoneImei() {
        return phoneImei;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        setCreateTime();
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setUrineCapacity(int urineCapacity) {
        this.urineCapacity = urineCapacity;
    }

    public void setCreateTime() {
        if (timestamp > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            setCreateTime(format.format(calendar.getTime()));
        }
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setPhoneImei(String phoneImei) {
        this.phoneImei = phoneImei;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getCreateDate() {
        try {
            return format.parse(getCreateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }
}
