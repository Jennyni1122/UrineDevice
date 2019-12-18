package com.leo.device.module.network;

import android.Manifest;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.thanosfisherman.wifiutils.WifiUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Leo
 */
public class NetworkActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.ivBack)
        AppCompatImageView ivBack;
        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.sChange)
        SwitchCompat sChange;
        @BindView(R.id.rv)
        RecyclerView rv;
        WifiAdapter adapter;
        RxPermissions rxPermissions;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_network;
        }

        @Override
        public void bindView() {
            super.bindView();
            rxPermissions = new RxPermissions((FragmentActivity) getActivity());
            tvTitle.setText(R.string.networking);
            ivBack.setOnClickListener(v -> getActivity().finish());
            adapter = new WifiAdapter();
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.setAdapter(adapter);
            sChange.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    rv.setVisibility(View.VISIBLE);
                    getWifi();
                } else {
                    rv.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void bindData(Object bean) {
        }

        private void getWifi() {
            rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter(Boolean::booleanValue)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            WifiUtils.withContext(getContext()).enableWifi(isSuccess -> {
                                if (isSuccess) {
                                    WifiUtils.withContext(getContext())
                                            .scanWifi(scanResults -> adapter.replace(scanResults))
                                            .start();
                                }
                            });
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
