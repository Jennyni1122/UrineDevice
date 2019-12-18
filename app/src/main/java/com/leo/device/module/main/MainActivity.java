package com.leo.device.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.net.RetrofitUtil;
import com.leo.device.module.connect.ConnectActivity;
import com.leo.device.module.data.DataFragment;
import com.leo.device.module.data.service.DataService;
import com.leo.device.module.edit.EditActivity;
import com.leo.device.module.home.HomeFragment;
import com.leo.device.module.person.PersonFragment;
import com.leo.device.module.tutorial.NavigationActivity;
import com.leo.push.Push;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Leo
 */
public class MainActivity extends BaseActivity {

    public static final String NEED_CONNECT = "NeedConnect";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra(NEED_CONNECT, false)) {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
        }
        Push.init(this, token -> {
            O.getUser().setAppToken(token);
            O.getUser().setType();
            O.setUser(O.getUser());
            LogUtils.i("===MainActivity===", GsonUtils.toJson(O.getUser()));
            RetrofitUtil.getService()
                    .saveToken(O.getUser())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<BaseResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseResponse baseResponse) {
                            LogUtils.i("===MainActivity===", GsonUtils.toJson(baseResponse));
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.ivTutorial)
        AppCompatImageView ivTutorial;
        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;

        @BindView(R.id.ivEdit)
        AppCompatImageView ivEdit;
        @BindView(R.id.flContent)
        FrameLayout flContent;
        @BindView(R.id.bnb)
        BottomNavigationBar bnb;
        Fragment[] fragments;
        String[] titles;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_main;
        }

        @Override
        public void bindView() {
            super.bindView();
            initTitle();
            initFragment();
            DataService.startService();
        }

        private void initTitle() {
            ivTutorial.setOnClickListener(v -> NavigationActivity.toActivity(getContext()));
            ivEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditActivity.class);
                getContext().startActivity(intent);
            });
        }

        private void initFragment() {
            Fragment homeFragment = new HomeFragment();
            Fragment timeFragment = new DataFragment();
//            Fragment bookFragment = new NotificationFragment();
            Fragment personFragment = new PersonFragment();
            fragments = new Fragment[]{homeFragment, timeFragment, personFragment};
            titles = new String[]{getContext().getString(R.string.home_title), getContext().getString(R.string.data_title), getContext().getString(R.string.person_title)};
            tvTitle.setText(titles[0]);
            FragmentUtils.add(getActivity().getSupportFragmentManager(), fragments, R.id.flContent, 0);
            bnb.setMode(BottomNavigationBar.MODE_FIXED);
            bnb.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                @Override
                public void onTabSelected(int position) {
                    FragmentUtils.show(fragments[position]);
                    tvTitle.setText(titles[position]);
                    if (position == titles.length - 1) {
                        ivTutorial.setVisibility(View.GONE);
                        ivEdit.setVisibility(View.VISIBLE);
                    } else {
                        ivEdit.setVisibility(View.GONE);
                        ivTutorial.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTabUnselected(int position) {
                    FragmentUtils.hide(fragments[position]);
                }

                @Override
                public void onTabReselected(int position) {
                }
            });
            bnb
                    .addItem(new BottomNavigationItem(R.drawable.ic_home, getContext().getString(R.string.home)))
                    .addItem(new BottomNavigationItem(R.drawable.ic_data, getContext().getString(R.string.data)))
                    .addItem(new BottomNavigationItem(R.drawable.ic_person, getContext().getString(R.string.person)))
                    .setFirstSelectedPosition(0)
                    .initialise();
        }

        @Override
        public void bindData(Object bean) {
        }

        @Override
        public FragmentActivity getActivity() {
            return (FragmentActivity) super.getActivity();
        }

    }
}
