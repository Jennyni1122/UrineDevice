package com.leo.device.base.viewbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.AdaptScreenUtils;

/**
 * @author Leo90
 */
public abstract class BaseViewBox<B> implements ViewBox<B> {
    private View rootView;

    public View getRootView() {
        return rootView;
    }

    public Context getContext() {
        return getRootView().getContext();
    }

    @Override
    public View createView(ViewGroup root) {
        if (root != null) {
            rootView = LayoutInflater.from(root.getContext()).inflate(getLayoutId(), root, false);
            AdaptScreenUtils.adaptWidth(getContext().getResources(), 750);
        }
        return rootView;
    }

    /**
     * 获取布局id
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    @Override
    public void bindView() {
        if (getRootView() != null) {
            ButterKnife.bind(this, getRootView());
        }
    }
}
