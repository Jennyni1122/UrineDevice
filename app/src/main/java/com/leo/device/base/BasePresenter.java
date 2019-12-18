package com.leo.device.base;

/**
 * @author Leo
 * @date 2019-04-11
 */
public abstract class BasePresenter<V extends BaseView> {

    private V view;

    public V getView() {
        return view;
    }

    public BasePresenter(V view) {
        this.view = view;
    }
}
