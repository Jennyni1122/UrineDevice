package com.leo.device.module.tutorial;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.leo.device.F;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.module.connect.ConnectFragment;

import butterknife.BindView;

public class NavigationActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new NavigationActivity.Box();
    }

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        F.startActivity(context, intent);
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.ivTutorial)
        AppCompatImageView ivTutorial;
        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.vp)
        ViewPager vp;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_nagivation;
        }

        @Override
        public void bindView() {
            super.bindView();
            tvTitle.setText("连接设备");
            ivTutorial.setOnClickListener(v -> getActivity().finish());
            vp.setAdapter(new FragmentStatePagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager()) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 1:
                            return new Turtorial2Fragment();
                        case 0:
                        default:
                            return new ConnectFragment();
                    }
                }

                @Override
                public int getCount() {
                    return 2;
                }
            });
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    switch (position) {
                        case 1:
                            tvTitle.setText("位置校准");
                            break;
                        case 0:
                        default:
                            tvTitle.setText("连接设备");
                            break;
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        @Override
        public void bindData(Object bean) {
        }
    }
}
