package com.leo.device.module.data.service;

import com.blankj.utilcode.util.SPUtils;
import com.leo.device.O;
import com.leo.device.bean.data.Urine;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.local.AppDatabase;
import com.leo.device.model.local.UrineDao;
import com.leo.device.model.net.RetrofitUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Leo
 * @date 2019-05-29
 */
public class UploadService extends BaseService {

    private static final String KEY = "UPLOAD_TIME";
    private long uploadTime = -1;
    private long lastUploadTime = -1;
    UrineDao dao;

    @Override
    public void init() {
        release();
        EventBus.getDefault().register(this);
        lastUploadTime = SPUtils.getInstance().getLong(KEY, -1);
        dao = AppDatabase.getInstance().urineDao();
        createUploadTask();
        createTimer();
    }

    ObservableEmitter<Object> uploadEmitter;

    private void createUploadTask() {
        Observable
                .create(emitter -> uploadEmitter = emitter)
                .map(object -> {
                    uploadTime = Calendar.getInstance().getTimeInMillis();
                    if (lastUploadTime > 0) {
                        return dao.getByDeviceAndTime(O.getUser().getPhoneImei(), lastUploadTime, uploadTime);
                    } else {
                        return dao.getAll();
                    }
                })
                .filter(urineList -> urineList.size() > 0)
                .doOnNext(urineList -> {
                    Urine lastUrine = null;
                    for (Urine urine : urineList) {
                        if (lastUrine != null && lastUrine.getTimestamp() > urine.getTimestamp()) {
                            continue;
                        }
                        lastUrine = urine;
                    }
                    EventBus.getDefault().post(lastUrine);
                })
                .flatMap(urineList -> RetrofitUtil.getService().uploadUrineList(urineList))
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == 0) {
                            lastUploadTime = uploadTime;
                            SPUtils.getInstance().put(KEY, lastUploadTime);
                            EventBus.getDefault().post(new Date(lastUploadTime));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    Disposable disposable;

    private void createTimer() {
        Observable
                .interval(0, 30, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        sendUploadCommand(new UploadCommand());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (uploadEmitter != null) {
            uploadEmitter.onComplete();
        }
        disposable = null;
        dao = null;
    }

    public static class UploadCommand {
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendUploadCommand(UploadCommand command) {
        if (uploadEmitter == null) {
            return;
        }
        uploadEmitter.onNext(command);
    }
}
