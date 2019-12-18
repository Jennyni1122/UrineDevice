package com.leo.device.module.connect;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import com.leo.device.R;
import com.leo.device.base.BaseAdapter;
import com.leo.device.base.viewbox.BaseRecyclerViewBox;

/**
 * @author Leo
 * @date 2019-06-24
 */
public class DeviceAdapter extends BaseAdapter<BluetoothDevice> {

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected BaseRecyclerViewBox<BluetoothDevice> createViewBox(int type) {
        return new ViewBox();
    }

    class ViewBox extends BaseRecyclerViewBox<BluetoothDevice> {
        @BindView(R.id.tvName)
        AppCompatTextView tvName;
        BluetoothDevice device;

        @Override
        protected int getLayoutId() {
            return R.layout.adapter_device;
        }

        @Override
        public void bindView() {
            super.bindView();
            getRootView().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(device);
                }
            });
        }

        @Override
        public void bindData(BluetoothDevice bean) {
            device = bean;
            tvName.setText(TextUtils.isEmpty(bean.getName()) ? bean.getAddress() : bean.getName());
        }

        private void sendPassword(String password) {
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device);
    }
}
