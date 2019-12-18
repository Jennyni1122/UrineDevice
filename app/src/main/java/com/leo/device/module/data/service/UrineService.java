package com.leo.device.module.data.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.Utils;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.leo.device.O;
import com.leo.device.bean.data.Urine;
import com.leo.device.bean.data.device.DeviceRequest;
import com.leo.device.bean.data.device.DeviceResponse;
import com.leo.device.model.local.AppDatabase;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Leo
 * @date 2019-05-28
 */
public class UrineService extends BaseService {
    Disposable disposable;
    private RxBluetooth rxBluetooth;

    @Override
    public void init() {
        release();
        rxBluetooth = new RxBluetooth(Utils.getApp());
        Observable
                .interval(0, 30, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        saveDataFromDevice();
                        saveDataFromTest();
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
        rxBluetooth = null;
    }

    public static List<Urine> getDataFromDevice(BluetoothSocket socket) {
        List<Urine> list = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        byte[] bytes = DeviceResponse.getByteArray();
        int length;
        List<Byte> byteList = new ArrayList<>();
        try {
            do {
                length = inputStream.read(bytes);
                for (int i = 0; i < length; i++) {
                    byteList.add(bytes[i]);
                }
            } while (length > 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LogUtils.i("回应：", byteList);

        while (byteList.size() > 0) {
            DeviceResponse response = new DeviceResponse(byteList);
            if (response.isVerify()) {
                list.add(responseToUrine(response));
            }
        }
        return list;
    }

    public static Urine responseToUrine(DeviceResponse response) {
        Urine urine = new Urine();
        urine.setDeviceId(response.getDeviceId());
        urine.setTimestamp(Calendar.getInstance().getTimeInMillis() - response.getInterval());
        urine.setUrineCapacity(getCapacity(response.getData()));
        urine.setPhoneNumber(O.getUser().getPhoneNumber());
        urine.setPhoneImei(PhoneUtils.getIMEI());
        urine.setBattery((int) response.getBattery());
        return urine;
    }

    private static int getCapacity(short[][] data) {
        // TODO: 2019/6/27 0027 计算尿量
        List<Integer> list = new ArrayList<>();
        int sum = 0;
        for (short[] channel : data) {
            sum = 0;
            for (short aShort : channel) {
                sum += aShort;
            }
            if (sum > 0) {
                list.add(sum);
            }
        }
        if (list.size() == 0) {
            return 0;
        }
        sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    public static Observable<BluetoothSocket> getBluetoothSocket(RxBluetooth rxBluetooth, BluetoothDevice device) {
        UUID uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
        return Observable
                .just(device)
                .flatMap(device1 -> rxBluetooth
                        .connectAsClient(device1, uuid)
                        .onErrorResumeNext(rxBluetooth.connectAsClient(device, 1))
                        .doOnError(throwable -> LogUtils.e("蓝牙连接错误", throwable))
                        .toObservable());
    }

    private void saveDataFromDevice() {
        if (O.getDevice() != null) {
            getBluetoothSocket(rxBluetooth, O.getDevice())
                    .flatMap(socket -> {
                        socket.connect();

                        OutputStream outputStream = socket.getOutputStream();
                        DeviceRequest request = new DeviceRequest();
                        request.setType(DeviceRequest.GET_DATA);
                        outputStream.write(request.toBytes());
                        outputStream.flush();

                        List<Urine> list = getDataFromDevice(socket);
                        AppDatabase.getInstance().urineDao().save(list);

                        request = new DeviceRequest();
                        request.setType(DeviceRequest.CLEAR);
                        outputStream.write(request.toBytes());
                        outputStream.flush();

                        socket.close();
                        return Observable.fromIterable(list);
                    })
                    .sorted((o1, o2) -> o1.getCreateDate().compareTo(o2.getCreateDate()))
                    .doOnNext(urine -> AppDatabase.getInstance().urineDao().save(urine))
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Urine>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Urine urine) {
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.e("===UrineService===", e);
                        }

                        @Override
                        public void onComplete() {
                            EventBus.getDefault().post(new UploadService.UploadCommand());
                        }
                    });
        }
    }

    private void saveDataFromTest() {
        Observable.just(new Urine())
                .doOnNext(urine -> {
                    urine.setTimestamp(Calendar.getInstance().getTimeInMillis());
                    urine.setDeviceId(O.getUser().getPhoneImei());
                    urine.setUrineCapacity(new Random().nextInt(100) + 10);
                    urine.setPhoneImei(O.getUser().getPhoneImei());
                    urine.setPhoneNumber(O.getUser().getPhoneNumber());
                })
                .doOnNext(urine -> AppDatabase.getInstance().urineDao().save(urine))
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Urine>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Urine urine) {
                        EventBus.getDefault().post(new UploadService.UploadCommand());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
