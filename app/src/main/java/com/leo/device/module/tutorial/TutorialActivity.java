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
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import com.leo.device.F;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.module.connect.ConnectFragment;
import com.leo.device.module.edit.EditFragment;
import com.leo.device.module.notification.NotificationFragment;

/**
 * @author Leo
 */
public class TutorialActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, TutorialActivity.class);
        F.startActivity(context, intent);
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.vp)
        ViewPager vp;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_tutorial;
        }

        @Override
        public void bindView() {
            super.bindView();
            tvTitle.setText(getContext().getString(R.string.tutorial));
            vp.setAdapter(new FragmentStatePagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager()) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 1:
                            return new ConnectFragment();
                        case 2:
                            return new Turtorial2Fragment();
                        case 3:
                            return new NotificationFragment();
                        case 4:
                            return new EditFragment();
                        case 0:
                        default:
                            return new Turtorial1Fragment();
                    }
                }

                @Override
                public int getCount() {
                    return 5;
                }
            });
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    switch (position) {
                        case 1:
                            tvTitle.setText("设备连接");
                            break;
                        case 2:
                            tvTitle.setText("位置校准");
                            break;
                        case 3:
                            tvTitle.setText("通知设置");
                            break;
                        case 4:
                            tvTitle.setText("个人信息编辑");
                            break;
                        case 0:
                        default:
                            tvTitle.setText(getContext().getString(R.string.tutorial));
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
