package com.leo.device.base.viewbox;

import android.app.Activity;
import android.widget.Toast;
import com.leo.device.bean.event.NetworkEvent;

/**
 * @author Leo90
 */
public abstract class BaseActivityBox<B> extends BaseViewBox<B> {
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void bindView() {
        super.bindView();
    }

    public void onAnyEvent(Object event) {
        if (event instanceof NetworkEvent) {
            if (((NetworkEvent) event).isConnected()) {
                return;
            }
            Toast.makeText(getActivity(), "当前网络异常， 请确认网络连接状态后重试", Toast.LENGTH_LONG).show();
        }
    }
}
