package com.leo.push.miui;

import android.content.Context;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * @author Leo
 * @date 2019-06-16
 */
public class MiuiPush {
    private static final String APP_ID = "2882303761518026661";
    private static final String APP_KEY = "5951802625661";

    public static void init(Context context) {
        MiPushClient.registerPush(context, APP_ID, APP_KEY);
    }
}
