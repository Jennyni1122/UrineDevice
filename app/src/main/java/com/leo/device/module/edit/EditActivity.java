package com.leo.device.module.edit;

import android.text.TextUtils;
import android.widget.EditText;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import com.blankj.utilcode.util.ToastUtils;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.User;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.net.RetrofitUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Leo
 */
public class EditActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseActivityBox<Object> {

        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.etName)
        AppCompatEditText etName;
        @BindView(R.id.etAge)
        AppCompatEditText etAge;
        @BindView(R.id.ivMan)
        AppCompatImageView ivMan;
        @BindView(R.id.ivWoman)
        AppCompatImageView ivWoman;
        @BindView(R.id.etThreshold)
        AppCompatEditText etThreshold;
        @BindView(R.id.bCommit)
        AppCompatButton bCommit;
        @BindView(R.id.etHeight)
        AppCompatEditText etHeight;
        @BindView(R.id.etWeight)
        AppCompatEditText etWeight;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_edit;
        }

        @Override
        public void bindView() {
            super.bindView();
            tvTitle.setText(getContext().getString(R.string.editing_information));
            ivMan.setOnClickListener(v -> {
                ivMan.setSelected(true);
                ivWoman.setSelected(false);
            });
            ivWoman.setOnClickListener(v -> {
                ivMan.setSelected(false);
                ivWoman.setSelected(true);
            });
            Observable.just("")
                    .map(s -> O.getUser())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(User user) {
                            bindData(user);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
            bCommit.setOnClickListener(v -> commit());
        }

        @Override
        public void bindData(Object bean) {
            Observable.just(bean)
                    .filter(o -> o instanceof User)
                    .cast(User.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(User user) {
                            etName.setText(user.getName());
                            etAge.setText(String.valueOf(user.getAge()));
                            if (user.getSex() == 0) {
                                ivWoman.setSelected(true);
                                ivMan.setSelected(false);
                            } else {
                                ivMan.setSelected(true);
                                ivWoman.setSelected(false);
                            }
                            etThreshold.setText(String.valueOf(user.getThreshold()));
                            etHeight.setText(String.valueOf(user.getHeight()));
                            etWeight.setText(String.valueOf(user.getWeight()));
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }

        private void commit() {
            if (!checkEditText(etName)) {
                return;
            }
            if (!checkEditText(etAge)) {
                return;
            }
            if (!TextUtils.isDigitsOnly(etAge.getText())) {
                return;
            }
            if (!checkEditText(etHeight)) {
                return;
            }
            if (!checkEditText(etWeight)) {
                return;
            }
            if (!checkEditText(etThreshold)) {
                return;
            }
            if (!TextUtils.isDigitsOnly(etThreshold.getText())) {
                return;
            }
            if (Integer.parseInt(etThreshold.getText().toString()) > User.getMaxThreshold(Integer.parseInt(etAge.getText().toString()), ivMan.isSelected() ? 1 : 0)) {
                ToastUtils.showShort("最大值为" + User.getMaxThreshold(Integer.parseInt(etAge.getText().toString()), ivMan.isSelected() ? 1 : 0));
                return;
            }
            Observable
                    .just(O.getUser())
                    .doOnNext(user -> {
                        user.setName(etName.getText().toString());
                        user.setAge(Integer.parseInt(etAge.getText().toString()));
                        user.setGender(ivMan.isSelected() ? 1 : 0);
                        user.setThreshold(Integer.parseInt(etThreshold.getText().toString()));
                        user.setHeight(Integer.parseInt(etHeight.getText().toString()));
                        user.setWeight(Integer.parseInt(etWeight.getText().toString()));
                        user.setBmi();
                    })
                    .flatMap(user -> RetrofitUtil.getService().update(user))
                    .filter(baseResponse -> baseResponse.getCode() == 0)
                    .map(BaseResponse::getData)
                    .compose(F.ioToMain())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(User user) {
                            O.setUser(user);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }

        public boolean checkEditText(EditText editText) {
            if (editText == null) {
                return false;
            }
            if (TextUtils.isEmpty(editText.getText())) {
                ToastUtils.showShort("请填写" + editText.getHint());
                return false;
            }
            return true;
        }
    }
}
