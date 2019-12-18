package com.leo.device.module.about;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import butterknife.BindView;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;

public class AboutActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new AboutActivity.Box();
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.ivBack)
        AppCompatImageView ivBack;
        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.tvDeviceID)
        AppCompatTextView tvDeviceID;
        @BindView(R.id.tvVersion)
        AppCompatTextView tvVersion;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_about;
        }

        @Override
        public void bindView() {
            super.bindView();
            tvTitle.setText(R.string.about);
            ivBack.setOnClickListener(v -> getActivity().finish());
        }

        @Override
        public void bindData(Object bean) {
        }
    }
}
