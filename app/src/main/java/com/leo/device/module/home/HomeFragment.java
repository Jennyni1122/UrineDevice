package com.leo.device.module.home;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import butterknife.BindView;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseFragment;
import com.leo.device.base.viewbox.BaseFragmentBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.Urine;
import com.leo.device.bean.data.User;
import com.leo.device.bean.data.device.DeviceRequest;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.local.AppDatabase;
import com.leo.device.model.net.RetrofitUtil;
import com.leo.device.module.data.DataPresenter;
import com.leo.device.module.data.service.UploadService;
import com.leo.device.module.data.service.UrineService;
import com.leo.device.ui.VerticalSeekBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.itangqi.waveloadingview.WaveLoadingView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author Leo
 * @date 2019-05-09
 */
public class HomeFragment extends BaseFragment {
    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    @Override
    public void onResume() {
        super.onResume();
        F.getAndSetUser(getViewBox());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(getViewBox());
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(getViewBox());
        super.onDestroyView();
    }

    static class Box extends BaseFragmentBox<Object> {

        @BindView(R.id.wlv)
        WaveLoadingView wlv;
        @BindView(R.id.ivSwipe)
        AppCompatImageView ivSwipe;
        @BindView(R.id.ml)
        MotionLayout ml;
        @BindView(R.id.vsb)
        VerticalSeekBar vsb;
        @BindView(R.id.tv)
        AppCompatTextView tv;
        @BindView(R.id.ivBg)
        AppCompatImageView ivBg;
        @BindView(R.id.tvUrinary)
        AppCompatTextView tvUrinary;
        @BindView(R.id.srl)
        SmartRefreshLayout srl;
        @BindView(R.id.tvBattery)
        AppCompatTextView tvBattery;
        @BindView(R.id.ivSignal)
        AppCompatImageView ivSignal;

        RxBluetooth bluetooth;

        @Override
        protected int getLayoutId() {
            return R.layout.fragment_home;
        }

        @Override
        public void bindView() {
            super.bindView();
            bluetooth = new RxBluetooth(getContext());
            vsb.getProgressDrawable().setAlpha(0);
            wlv.setProgressValue(79);
            wlv.setCenterTitle(wlv.getProgressValue() + "%");
            wlv.setCenterTitleColor(Color.rgb(255, 134, 21));
            wlv.setOnClickListener(view -> {
                if (wlv.getCenterTitle().contains("ml")) {
                    wlv.setCenterTitle(Math.round(wlv.getProgressValue()) + "%");
                } else {
                    wlv.setCenterTitle(Math.round(1f * wlv.getProgressValue() / 100 * O.getUser().getMaxThreshold()) + "ml");
                }

            });
            vsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    ml.setProgress(1f * progress / seekBar.getMax());
//                    setThreshold(progress * 6);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            vsb.setEnabled(false);

            srl.setEnableLoadMore(false);
            srl.setOnRefreshListener(refreshLayout -> getData());
        }

        private boolean checkDevice() {
            // TODO: 2019/12/16 0016 检测是否连接设备
            return false;
        }

        private void getData() {
            // TODO: 2019/12/12 0012 获取数据，默认使用保存的设备信息
            Single.just(1)
                    .map(integer -> O.getDevice() != null && checkDevice())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(Boolean connect) {
                            if (connect) {
                                getDataFromDevice();
                            } else {
                                srl.finishRefresh();
                                ToastUtils.showLong("未连接设备");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            srl.finishRefresh();
                        }
                    });
        }

        private void getDataFromDevice() {
            UrineService.getBluetoothSocket(bluetooth, O.getDevice())
                    .doOnNext(socket -> {
                        socket.connect();

                        OutputStream outputStream = socket.getOutputStream();
                        DeviceRequest request = new DeviceRequest();
                        request.setType(DeviceRequest.CHECK_AND_GET_DATA);
                        outputStream.write(request.toBytes());
                        outputStream.flush();

                        List<Urine> list = UrineService.getDataFromDevice(socket);
                        AppDatabase.getInstance().urineDao().save(list);

                        request = new DeviceRequest();
                        request.setType(DeviceRequest.CLEAR);
                        outputStream.write(request.toBytes());
                        outputStream.flush();

                        socket.close();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BluetoothSocket>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BluetoothSocket socket) {
                            EventBus.getDefault().post(new UploadService.UploadCommand());
                        }

                        @Override
                        public void onError(Throwable e) {
                            getDataFromNet();
                        }

                        @Override
                        public void onComplete() {
                            srl.finishRefresh();
                        }
                    });
        }

        private void getDataFromNet() {
            DataPresenter.getUrine(new Date())
                    .sorted((o1, o2) -> o1.getCreateDate().compareTo(o2.getCreateDate()))
                    .lastOrError()
                    .subscribeOn(Schedulers.io())
                    .subscribe(new SingleObserver<Urine>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(Urine urine) {
                            LogUtils.i("===HomeFragment===", GsonUtils.toJson(urine));
                            EventBus.getDefault().post(urine);
                            srl.finishRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            srl.finishRefresh();
                            LogUtils.e("===HomeFragment===", e);
                        }
                    });
        }

        @Override
        public void bindData(Object bean) {
            Observable.just(bean)
                    .filter(o -> o instanceof User)
                    .cast(User.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(User user) {
                            Glide
                                    .with(getContext())
                                    .asBitmap()
                                    .load(user.getSex() == 0 ? R.mipmap.bg_women : R.mipmap.bg_man)
                                    .into(ivBg);
                            vsb.setMax(user.getMaxThreshold());
                            vsb.setProgress(user.getThreshold());
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

        private void setThreshold(int threshold) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
                disposable = null;
            }
            Observable
                    .just(threshold)
                    .map(integer -> O.getUser().setThreshold(integer))
                    .doOnNext(O::setUser)
                    .flatMap(user -> RetrofitUtil.getService().update(user))
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BaseResponse<User>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(BaseResponse<User> userBaseResponse) {
                        }

                        @Override
                        public void onError(Throwable e) {
                            disposable = null;
                        }

                        @Override
                        public void onComplete() {
                            disposable = null;
                        }
                    });
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(Urine urine) {
            checkUrine(urine);
            tvUrinary.setText(String.format(getContext().getString(R.string.urinary_volume), urine.getUrineCapacity()));
            wlv.setProgressValue((int) (1f * urine.getUrineCapacity() / O.getUser().getMaxThreshold() * 100));
            wlv.setCenterTitle(Math.round(1f * urine.getUrineCapacity() / O.getUser().getMaxThreshold() * 100) + "%");
        }

        private void checkUrine(Urine urine) {
            // TODO: 2019/6/30 0030 检测数据是否异常
            if (urine.getUrineCapacity() > O.getUser().getMaxThreshold()) {
                // TODO: 2019/6/30 0030 尿量超出阈值
            }
            if (urine.getBattery() != null && urine.getBattery() > 5) {
                // TODO: 2019/6/30 0030 检查设备电量
                tvBattery.setText(urine.getBattery() + "%");
            }
            setSignal(urine);
        }

        private static final int[] arrayBattery = new int[]{R.mipmap.ic_signal_1, R.mipmap.ic_signal_2, R.mipmap.ic_signal_3, R.mipmap.ic_signal_4, R.mipmap.ic_signal_5};

        private void setSignal(Urine urine) {
            // TODO: 2019/12/1 0001 设置信号通道
            if (urine == null) {
                return;
            }
            if (urine.getBattery() == null) {
                return;
            }
            int signal = urine.getBattery();
            if (signal >= 80) {
                ivSignal.setImageResource(arrayBattery[4]);
            } else if (signal >= 60) {
                ivSignal.setImageResource(arrayBattery[3]);
            } else if (signal >= 40) {
                ivSignal.setImageResource(arrayBattery[2]);
            } else if (signal >= 20) {
                ivSignal.setImageResource(arrayBattery[1]);
            } else {
                ivSignal.setImageResource(arrayBattery[0]);
            }
        }
    }
}
