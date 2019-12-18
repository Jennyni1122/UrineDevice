package com.leo.device.module.tutorial;

import com.leo.device.R;
import com.leo.device.base.BaseFragment;
import com.leo.device.base.viewbox.BaseFragmentBox;
import com.leo.device.base.viewbox.ViewBox;

/**
 * @author Leo
 * @date 2019-05-13
 */
public class Turtorial2Fragment extends BaseFragment {
    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseFragmentBox<Object> {

        @Override
        protected int getLayoutId() {
            return R.layout.layout_tutorial_2;
        }

        @Override
        public void bindData(Object bean) {
        }
    }
}
