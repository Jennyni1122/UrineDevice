package com.leo.device;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.leo.device.bean.data.NotificationSetting;
import com.leo.device.bean.data.User;

/**
 * @author Leo
 * @date 2019-05-24
 */
public class O {
    private static User user;

    public static User getUser() {
        if (user == null) {
            String json = SPUtils.getInstance().getString("USER");
            if (!TextUtils.isEmpty(json)) {
                user = GsonUtils.fromJson(json, User.class);
                user.setPhoneImei(PhoneUtils.getIMEI());
            }
        }
        return user;
    }

    public static void setUser(User user) {
        if (user == null) {
            SPUtils.getInstance().remove("USER");
        } else {
            user.setPhoneImei(PhoneUtils.getIMEI());
            SPUtils.getInstance().put("USER", GsonUtils.toJson(user));
        }
        O.user = user;
    }

    private static NotificationSetting setting;

    public static NotificationSetting getSetting() {
        if (setting == null) {
            String json = SPUtils.getInstance().getString("SETTING");
            if (!TextUtils.isEmpty(json)) {
                setting = GsonUtils.fromJson(json, NotificationSetting.class);
            }
        }
        return setting;
    }

    public static void setSetting(NotificationSetting setting) {
        if (setting == null) {
            SPUtils.getInstance().remove("SETTING");
        } else {
            SPUtils.getInstance().put("SETTING", GsonUtils.toJson(setting));
        }
        O.setting = setting;
    }

    private static BluetoothDevice device;

    public static BluetoothDevice getDevice() {
        if (device == null) {
            String json = SPUtils.getInstance().getString("DEVICE");
            if (!TextUtils.isEmpty(json)) {
                device = GsonUtils.fromJson(json, BluetoothDevice.class);
            }
        }
        return device;
    }

    public static void setDevice(BluetoothDevice device) {
        if (device == null) {
            SPUtils.getInstance().remove("DEVICE");
        } else {
            SPUtils.getInstance().put("DEVICE", GsonUtils.toJson(device));
        }
        O.device = device;
    }
}
