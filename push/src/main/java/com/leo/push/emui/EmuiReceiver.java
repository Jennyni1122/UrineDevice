package com.leo.push.emui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.huawei.hms.support.api.push.PushReceiver;
import com.leo.push.Push;

/**
 * @author Leo
 */
public class EmuiReceiver extends PushReceiver {
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Push.setToken(token);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction("com.leo.device.dataService");
        context.sendBroadcast(intent);
        return true;
    }
}
