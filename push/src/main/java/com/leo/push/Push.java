package com.leo.push;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import com.leo.push.emui.EmuiPush;
import com.leo.push.miui.MiuiPush;

/**
 * @author Leo
 * @date 2019-06-09
 */
public class Push {

    private static TokenHandler handler;

    public static void init(Activity activity, TokenHandler handler) {
        Push.handler = handler;
        String manufacturer = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(manufacturer)) {
            switch (manufacturer.toLowerCase()) {
                case "xiaomi":
                    MiuiPush.init(activity);
                    break;
                case "huawei":
                    EmuiPush.init(activity);
                default:
                    MiuiPush.init(activity);
                    break;
            }
        }
    }

    public static void setToken(String token) {
        if (handler != null) {
            handler.onResult(token);
        }
    }
}
