package com.leo.device.module.data.service;

import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.xdandroid.hellodaemon.AbsWorkService;
import com.xdandroid.hellodaemon.DaemonEnv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 * @date 2019-05-28
 */
public class DataService extends AbsWorkService {

    public static void startService() {
        shouldStopService = false;
        DaemonEnv.startServiceMayBind(DataService.class);
    }

    public static void stopService() {
        shouldStopService = true;
        cancelJobAlarmSub();
        DaemonEnv.startServiceMayBind(DataService.class);
    }

    private static boolean shouldStopService = false;
    private List<BaseService> serviceList = new ArrayList<>();

    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return shouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        startServices();
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopServices();
    }

    private void startServices() {
        serviceList.add(new UrineService());
        serviceList.add(new UploadService());
        for (BaseService service : serviceList) {
            service.init();
        }
    }

    private void stopServices() {
        for (BaseService service : serviceList) {
            service.release();
        }
        serviceList.clear();
    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return serviceList.size() > 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        stopServices();
    }
}