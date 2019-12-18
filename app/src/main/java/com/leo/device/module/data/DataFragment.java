package com.leo.device.module.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.leo.device.base.BaseFragment;
import com.leo.device.base.viewbox.ViewBox;
import org.greenrobot.eventbus.EventBus;

/**
 * @author Leo
 * @date 2019-05-09
 */
public class DataFragment extends BaseFragment {
    @Override
    protected ViewBox<Object> createViewBox() {
        return new DataBox();
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

    private OrientationEventListener listener;
    private boolean isChange = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isChange = false;
        listener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation > 75 && orientation < 105) {
                    if (!isChange) {
                        isChange = true;
                        Intent intent = new Intent(getContext(), DataFullscreenActivity.class);
                        startActivity(intent);
                    }
                } else if (orientation > 255 && orientation < 285) {
                    if (!isChange) {
                        isChange = true;
                        Intent intent = new Intent(getContext(), DataFullscreenActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        isChange = false;
        if (hidden) {
            return;
        }
        listener.enable();
    }

    @Override
    public void onStop() {
        super.onStop();
        listener.disable();
    }

    private boolean hidden = false;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (hidden) {
            listener.disable();
        } else {
            listener.enable();
        }
    }
}
