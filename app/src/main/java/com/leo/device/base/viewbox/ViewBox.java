package com.leo.device.base.viewbox;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Leo90
 */
public interface ViewBox<B> {
    /**
     * 创建View
     *
     * @param root 父容器
     * @return 创建后的View
     */
    View createView(ViewGroup root);

    /**
     * 绑定View
     */
    void bindView();

    /**
     * 绑定数据
     *
     * @param bean 数据
     */
    void bindData(B bean);
}
