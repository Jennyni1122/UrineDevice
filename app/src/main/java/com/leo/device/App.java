package com.leo.device;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.Utils;
import com.leo.device.model.local.AppDatabase;
import com.leo.device.module.data.service.DataService;
import com.xdandroid.hellodaemon.DaemonEnv;

/**
 * @author Leo
 * @date 2019-05-05
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        if (BuildConfig.DEBUG) {
            LogUtils.getConfig().setDir(PathUtils.getExternalAppFilesPath());
            LogUtils.getConfig().setLog2FileSwitch(true);
        }
        AppDatabase.init(this);
        DaemonEnv.initialize(this, DataService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
