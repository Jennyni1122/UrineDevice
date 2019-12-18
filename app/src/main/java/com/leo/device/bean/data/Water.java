package com.leo.device.bean.data;

import androidx.room.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Leo
 * @date 2019-05-05
 */
@Entity(primaryKeys = {"timestamp"})
public class Water {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private long timestamp;

    private int capacity;

    private String createTime;

    private Integer id;

    private String phoneImei;

    private String phoneNumber;

    public long getTimestamp() {
        return timestamp;
    }

    public int getCapacity() {
        return capacity;
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

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCreateTime() {
        if (timestamp > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            createTime = format.format(calendar.getTime());
        }
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
}
