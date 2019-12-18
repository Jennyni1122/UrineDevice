package com.leo.device.module.connect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.github.ivbaranov.rxbluetooth.predicates.BtPredicate;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.device.DeviceRequest;
import com.leo.device.module.connect.view.RadarView;
import com.leo.device.module.data.service.UrineService;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Leo
 */
public class ConnectActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            getViewBox().bindData(null);
        }
    }

    public static class Box extends BaseActivityBox<Object> {
        UUID uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");

        @BindView(R.id.srl)
        SmartRefreshLayout srl;
        @BindView(R.id.rv)
        RecyclerView rv;
        @BindView(R.id.radar)
        RadarView radar;
        private DeviceAdapter adapter;

        private RxPermissions rxPermissions;
        private RxBluetooth rxBluetooth;
        private CompositeDisposable compositeDisposable = new CompositeDisposable();

        Disposable disposable;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_connect;
        }

        @Override
        public void bindView() {
            super.bindView();
            try {
                rxPermissions = new RxPermissions((FragmentActivity) ActivityUtils.getTopActivity());
            } catch (Exception e) {
                LogUtils.e(e);
            }
            rxBluetooth = new RxBluetooth(getContext());

            compositeDisposable.add(rxBluetooth.observeDevices()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bluetoothDevice -> adapter.add(bluetoothDevice)));

            compositeDisposable.add(rxBluetooth.observeDiscovery()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .filter(BtPredicate.in(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
                    .subscribe(action -> adapter.clear()));

            compositeDisposable.add(rxBluetooth.observeDiscovery()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .filter(BtPredicate.in(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                    .subscribe(action -> {
                        srl.finishRefresh();
                        radar.stop();
                    }));

            adapter = new DeviceAdapter();
            adapter.setOnItemClickListener(device -> {
                if (disposable != null) {
                    disposable.dispose();
                }
                UrineService.getBluetoothSocket(rxBluetooth, device)
                        .doOnNext(bluetoothSocket -> {
                            bluetoothSocket.connect();
                            OutputStream outputStream = bluetoothSocket.getOutputStream();
                            DeviceRequest request = new DeviceRequest();
                            request.setType(DeviceRequest.SET_BLUERAY);
                            request.setParam(1);
                            outputStream.write(request.toBytes());
                            outputStream.flush();

                            request.setType(DeviceRequest.SET_INTERVAL);
                            request.setParam(10);
                            outputStream.write(request.toBytes());
                            outputStream.flush();
                        })
                        .doOnNext(BluetoothSocket::close)
                        .doOnNext(bluetoothSocket -> O.setDevice(device))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<BluetoothSocket>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(BluetoothSocket bluetoothSocket) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.e(e);
                                disposable = null;
                            }

                            @Override
                            public void onComplete() {
                                getActivity().finish();
                                disposable = null;
                            }
                        });
            });
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(adapter);
            srl.setEnableRefresh(false);
            srl.setEnableLoadMore(false);
            srl.setOnRefreshListener(refreshLayout -> getDevices());
            radar.setOnClickListener(v -> {
                if (radar.isScanning()) {
                    return;
                }
                getDevices();
                radar.start();
            });
            //radar.start();
            //getDevices();
        }

        @Override
        public void bindData(Object bean) {
            compositeDisposable.dispose();
        }

        private void getDevices() {
            if (rxPermissions == null) {
                try {
                    rxPermissions = new RxPermissions((FragmentActivity) ActivityUtils.getTopActivity());
                } catch (Exception e) {
                    LogUtils.e(e);
                    return;
                }
            }
            rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter(Boolean::booleanValue)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            rxBluetooth.startDiscovery();
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
}
