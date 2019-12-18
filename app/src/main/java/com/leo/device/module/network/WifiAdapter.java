package com.leo.device.module.network;

import android.net.wifi.ScanResult;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import com.blankj.utilcode.util.LogUtils;
import com.kongzue.dialog.v2.InputDialog;
import com.leo.device.R;
import com.leo.device.base.BaseAdapter;
import com.leo.device.base.viewbox.BaseRecyclerViewBox;

/**
 * @author Leo
 * @date 2019-05-21
 */
public class WifiAdapter extends BaseAdapter<ScanResult> {
    @Override
    protected BaseRecyclerViewBox<ScanResult> createViewBox(int type) {
        return new ViewBox();
    }

    public static class ViewBox extends BaseRecyclerViewBox<ScanResult> {
        @BindView(R.id.tvName)
        AppCompatTextView tvName;
        @BindView(R.id.ivLock)
        AppCompatImageView ivLock;
        ScanResult scanResult;

        @Override
        protected int getLayoutId() {
            return R.layout.adapter_wifi;
        }

        @Override
        public void bindView() {
            super.bindView();
            getRootView().setOnClickListener(v -> InputDialog.show(getContext(), getContext().getString(R.string.password), "", (dialog, inputText) -> {
                sendPassword(inputText);
                dialog.cancel();
            }));
        }

        @Override
        public void bindData(ScanResult bean) {
            scanResult = bean;
            tvName.setText(bean.SSID);
        }

        private void sendPassword(String password) {
            // TODO: 2019/6/29 0029 向蓝牙发送wifi数据
            LogUtils.i(scanResult.SSID, password);
        }
    }
}
