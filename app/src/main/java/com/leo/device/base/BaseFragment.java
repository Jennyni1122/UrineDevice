package com.leo.device.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.leo.device.base.viewbox.BaseFragmentBox;
import com.leo.device.base.viewbox.ViewBox;

/**
 * @author Leo90
 */
public abstract class BaseFragment extends Fragment {

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBox = createViewBox();
        if (getViewBox() instanceof BaseFragmentBox) {
            ((BaseFragmentBox) getViewBox()).setFragment(this);
        }
        View view = getViewBox().createView(container);
        getViewBox().bindView();
        return view;
    }
}
