package com.leo.device.module.connect;

import com.leo.device.base.BaseFragment;
import com.leo.device.base.viewbox.ViewBox;

/**
 * @author Leo
 * @date 2019/12/11 0011 10:46
 */
public class ConnectFragment extends BaseFragment {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new ConnectActivity.Box();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() == null || getActivity().isFinishing()) {
            getViewBox().bindData(null);
        }
    }
}
