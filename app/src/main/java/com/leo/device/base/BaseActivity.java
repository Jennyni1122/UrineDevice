package com.leo.device.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.NetworkUtils;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.event.NetworkEvent;

/**
 * @author Leo90
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ViewBox<Object> viewBox;

    /**
     * 创建ViewBox
     *
     * @return ViewBox
     */
    protected abstract ViewBox<Object> createViewBox();

    protected ViewBox<Object> getViewBox() {
        return viewBox;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBox = createViewBox();
        if (getViewBox() instanceof BaseActivityBox) {
            ((BaseActivityBox) getViewBox()).setActivity(this);
        }
        setContentView(getViewBox().createView(getDelegate().findViewById(android.R.id.content)));
        getViewBox().bindView();
    }

    private final BroadcastReceiver networkWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getViewBox() instanceof BaseActivityBox) {
                ((BaseActivityBox) getViewBox()).onAnyEvent(new NetworkEvent(NetworkUtils.isConnected(), NetworkUtils.getNetworkType()));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkWatcher, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkWatcher);
    }
}
