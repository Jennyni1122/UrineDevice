package com.leo.device.module.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.LogUtils;

/**
 * @author Leo
 */
public class DataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("===DataReceiver===", "onReceive");
        DataService.startService();
    }
}
