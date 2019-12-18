package com.leo.device.bean.request;

import android.text.TextUtils;
import androidx.collection.ArrayMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * @author Leo
 * @date 2019-05-22
 */
public class Request {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);

    private Long startTimestamp;
    private Long endTimestamp;
    private String phoneNumber;
    private String phoneImei;

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneImei(String phoneImei) {
        this.phoneImei = phoneImei;
    }

    private Map<String, String> toMap() {
        Map<String, String> map = new ArrayMap<>();
        if (!TextUtils.isEmpty(phoneImei)) {
            map.put("phoneImei", phoneImei);
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            map.put("phoneNumber", phoneNumber);
        }
        if (startTimestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTimestamp);
            map.put("startTime", format.format(calendar.getTime()));
        }
        if (endTimestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTimestamp);
            map.put("endTime", format.format(calendar.getTime()));
        }
        return map;
    }
}
