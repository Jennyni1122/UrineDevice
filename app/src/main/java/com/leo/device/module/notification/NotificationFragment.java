package com.leo.device.module.notification;

import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import butterknife.BindView;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseFragment;
import com.leo.device.base.viewbox.BaseFragmentBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.NotificationSetting;
import com.leo.device.bean.data.device.DeviceRequest;
import com.leo.device.module.data.service.UrineService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.io.OutputStream;

/**
 * @author Leo
 * @date 2019-05-09
 */
public class NotificationFragment extends BaseFragment {
    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseFragmentBox<Object> {

        NotificationSetting setting;
        private RxBluetooth rxBluetooth;

        @BindView(R.id.seeBar)
        SeekBar seekBar;
        @BindView(R.id.interv_sub)
        ImageView imageViewSub;
        @BindView(R.id.interval_add)
        ImageView imageViewAdd;
        @BindView(R.id.verticalStr)
        TextView verticalStr;
        @BindView(R.id.verticalStrLayout)
        LinearLayout verticalStrLayout;

        @BindView(R.id.sAll)
        SwitchCompat sAll;
        @BindView(R.id.sInterval)
        SwitchCompat sInterval;
        @BindView(R.id.llRadio)
        LinearLayout llRadio;
        @BindView(R.id.sBattery)
        SwitchCompat sBattery;
        @BindView(R.id.sUrinary)
        SwitchCompat sUrinary;
        @BindView(R.id.sChange)
        SwitchCompat sChange;

        @Override
        protected int getLayoutId() {
            return R.layout.fragment_notification;
        }

        @Override
        public void bindView() {
            super.bindView();
            bindData(O.getSetting());
            rxBluetooth = new RxBluetooth(getContext());
            sInterval.setOnClickListener(view -> {
                setting.setIntervalOpen(sInterval.isChecked());
                O.setSetting(setting);
                llRadio.setVisibility(sInterval.isChecked() ? View.VISIBLE : View.GONE);
                verticalStrLayout.setVisibility(sInterval.isChecked() ? View.VISIBLE : View.GONE);
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    setInterval(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            imageViewSub.setOnClickListener(view -> {
                seekBar.setProgress(seekBar.getProgress() - 1);
            });

            imageViewAdd.setOnClickListener(view -> {
                seekBar.setProgress(seekBar.getProgress() + 1);
            });

            sAll.setOnClickListener(view -> {
                //setting.setAll(isChecked);
                if (!sAll.isChecked()) {
                    sInterval.setChecked(false);
                    sInterval.setEnabled(false);
                    sBattery.setChecked(false);
                    sBattery.setEnabled(false);
                    sUrinary.setChecked(false);
                    sUrinary.setEnabled(false);
                    sChange.setChecked(false);
                    sChange.setEnabled(false);
                    llRadio.setVisibility(View.GONE);
                    verticalStrLayout.setVisibility(View.GONE);
                } else {
                    sInterval.setEnabled(true);
                    sBattery.setEnabled(true);
                    sUrinary.setEnabled(true);
                    sChange.setEnabled(true);
                    setting.setAll(true);
                    bindData(setting);
                }
            });

            sBattery.setOnClickListener(view -> {
                setting.setBattery(sBattery.isChecked());
                O.setSetting(setting);
            });
            sUrinary.setOnClickListener(view -> {
                setting.setUrinary(sUrinary.isChecked());
                O.setSetting(setting);
            });
            sChange.setOnClickListener(view -> {
                setting.setChange(sChange.isChecked());
                O.setSetting(setting);
            });
        }

        @Override
        public void bindData(Object bean) {
            setting = O.getSetting();
            if(setting == null) {
                setting = new NotificationSetting();
            }
            if (bean == null) {
                return;
            }
            Observable.just(bean)
                    .filter(o -> o instanceof NotificationSetting)
                    .cast(NotificationSetting.class)
                    .doOnNext(notificationSetting -> setting = notificationSetting)
                    .compose(F.ioToMain())
                    .subscribe(new Observer<NotificationSetting>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(NotificationSetting notificationSetting) {
                            llRadio.setVisibility(notificationSetting.isIntervalOpen() ? View.VISIBLE : View.GONE);
                            verticalStrLayout.setVisibility(notificationSetting.isIntervalOpen() ? View.VISIBLE : View.GONE);
                            seekBar.setProgress(notificationSetting.getInterval());
                            sAll.setChecked(notificationSetting.isAll());
                            sInterval.setChecked(notificationSetting.isIntervalOpen());
                            sBattery.setChecked(notificationSetting.isBattery());
                            sUrinary.setChecked(notificationSetting.isUrinary());
                            sChange.setChecked(notificationSetting.isChange());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        public void setInterval(int interval) {
            setting.setInterval(interval);
            verticalStr.setText(interval + "分钟");
            O.setSetting(setting);
            if (O.getDevice() != null) {
                UrineService.getBluetoothSocket(rxBluetooth, O.getDevice())
                        .doOnNext(bluetoothSocket -> {
                            bluetoothSocket.connect();

                            OutputStream outputStream = bluetoothSocket.getOutputStream();
                            DeviceRequest request = new DeviceRequest();
                            request.setType(DeviceRequest.SET_INTERVAL);
                            request.setParam(interval);
                            outputStream.write(request.toBytes());
                            outputStream.flush();

                            bluetoothSocket.close();
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<BluetoothSocket>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(BluetoothSocket socket) {
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
}
