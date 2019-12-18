package com.leo.device.module.register;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import com.blankj.utilcode.util.LogUtils;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.User;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.net.RetrofitUtil;
import com.leo.device.module.login.LoginActivity;
import com.leo.device.module.main.MainActivity;
import com.leo.device.module.tutorial.TutorialActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * @author Leo
 */
public class RegisterActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new Box();
    }

    static class Box extends BaseActivityBox<Object> {


        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.etPhone)
        AppCompatEditText etPhone;
        @BindView(R.id.etCode)
        AppCompatEditText etCode;
        @BindView(R.id.bCapture)
        AppCompatButton bCapture;
        @BindView(R.id.etName)
        AppCompatEditText etName;
        @BindView(R.id.etAge)
        AppCompatEditText etAge;
        @BindView(R.id.ivMan)
        AppCompatImageView ivMan;
        @BindView(R.id.ivWoman)
        AppCompatImageView ivWoman;
        @BindView(R.id.bCommit)
        AppCompatButton bCommit;
        @BindView(R.id.tvToLogin)
        AppCompatTextView tvToLogin;
        @BindView(R.id.tvPhoneError)
        AppCompatTextView tvPhoneError;
        @BindView(R.id.tvCodeError)
        AppCompatTextView tvCodeError;
        @BindView(R.id.tvNameError)
        AppCompatTextView tvNameError;
        @BindView(R.id.tvAgeError)
        AppCompatTextView tvAgeError;
        @BindView(R.id.llStep1)
        LinearLayout llStep1;
        @BindView(R.id.llStep2)
        LinearLayout llStep2;
        @BindView(R.id.bNext)
        AppCompatButton bNext;
        @BindView(R.id.etHeight)
        AppCompatEditText etHeight;
        @BindView(R.id.tvHeightError)
        AppCompatTextView tvHeightError;
        @BindView(R.id.etWeight)
        AppCompatEditText etWeight;
        @BindView(R.id.tvWeightError)
        AppCompatTextView tvWeightError;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_register;
        }

        @Override
        public void bindView() {
            super.bindView();
            etPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 11) {
                        RetrofitUtil.getService()
                                .getMobileRegistered(etPhone.getText().toString())
                                .map(BaseResponse::getData)
                                .compose(F.ioToMain())
                                .subscribe(new Observer<Object>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Object registered) {
                                        if (Double.valueOf(registered.toString()).intValue() == 1) {
                                            tvPhoneError.setText("手机号已注册");
                                            bCommit.setEnabled(false);
                                            bCapture.setEnabled(false);
                                        } else {
                                            bCommit.setEnabled(true);
                                            bCapture.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast toast = Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 0, 150);
                                        toast.show();
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    } else {
                        tvPhoneError.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            etCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        tvCodeError.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            tvTitle.setText(R.string.registry);
            bCapture.setOnClickListener(v -> getCapture());
            ivMan.setOnClickListener(v -> {
                ivMan.setSelected(true);
                ivWoman.setSelected(false);
            });
            ivWoman.setOnClickListener(v -> {
                ivMan.setSelected(false);
                ivWoman.setSelected(true);
            });
            ivMan.performClick();
            bCommit.setOnClickListener(v -> register());
            tvToLogin.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            });

            bNext.setOnClickListener(view -> checkCapture());
        }

        @Override
        public void bindData(Object bean) {
        }

        boolean clicked = false;

        private void getCapture() {
            if (TextUtils.isEmpty(etPhone.getText())) {
                tvPhoneError.setText("手机号码为空");
                return;
            } else if (etPhone.getText().length() != 11) {
                tvPhoneError.setText("手机号码不合法");
                return;
            }
            if (clicked) {
                return;
            }
            clicked = true;
            if (TextUtils.isEmpty(etPhone.getText())) {
                return;
            }
            RetrofitUtil.getService()
                    .getRegisterCapture(etPhone.getText().toString())
                    .map(BaseResponse::getCode)
                    .filter(integer -> integer == 0)
                    .flatMap(integer -> Observable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS))
                    .map(aLong -> 60 - aLong)
                    .compose(F.ioToMain())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Long aLong) {
                            bCapture.setText(aLong.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            clicked = false;
                            bCapture.setText(R.string.get_verification_code);
                        }

                        @Override
                        public void onComplete() {
                            clicked = false;
                            bCapture.setText(R.string.get_verification_code);
                        }
                    });
        }

        private boolean checkStep1() {
            boolean flag = false;
            if (TextUtils.isEmpty(etPhone.getText())) {
                tvPhoneError.setText("手机号码为空");
                flag = true;
            }
            if (TextUtils.isEmpty(etCode.getText())) {
                tvCodeError.setText("验证码为空");
                flag = true;
            }
            return flag;
        }

        private boolean checkStep2() {
            boolean flag = false;
            if (TextUtils.isEmpty(etName.getText())) {
                tvNameError.setText("姓名为空");
                flag = true;
            }
            if (TextUtils.isEmpty(etAge.getText())) {
                tvAgeError.setText("年龄为空");
                flag = true;
            }
            if (TextUtils.isEmpty(etHeight.getText())) {
                tvHeightError.setText("身高为空");
                flag = true;
            }
            if (TextUtils.isEmpty(etWeight.getText())) {
                tvWeightError.setText("体重为空");
                flag = true;
            }
            return flag;
        }

        private void register() {
            if (checkStep2()) {
                return;
            }
            Observable.just(new User())
                    .doOnNext(user -> {
                        user.setName(etName.getText().toString());
                        user.setAge(Integer.parseInt(etAge.getText().toString()));
                        user.setGender(ivMan.isSelected() ? 1 : 0);
                        user.setPhoneNumber(etPhone.getText().toString());
                        user.setHeight(Integer.parseInt(etHeight.getText().toString()));
                        user.setWeight(Integer.parseInt(etWeight.getText().toString()));
                        user.setBmi();
                    })
                    .flatMap(s -> RetrofitUtil.getService().register(s))
                    // .filter(baseResponse -> baseResponse.getCode() == 0)
                    // .map(BaseResponse::getData)
                    .compose(F.ioToMain())
                    .subscribe(new Observer<BaseResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseResponse response) {
                            if (response.getCode() == 0) {
                                O.setUser((User) response.getData());
                                Intent intent1 = new Intent(getActivity(), TutorialActivity.class);
                                Intent intent2 = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivities(new Intent[]{intent2, intent1});
                                getActivity().finish();
                            } else {
                                Toast toast = Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 150);
                                toast.show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast toast = Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 150);
                            toast.show();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }

        private void checkCapture() {
            if (checkStep1()) {
                return;
            }
            Observable
                    .just(new User())
                    .doOnNext(user -> {
                        user.setCapture(etCode.getText().toString());
                        user.setPhoneNumber(etPhone.getText().toString());
                    })
                    .flatMap(user -> RetrofitUtil.getService().checkCapture(user))
                    .compose(F.ioToMain())
                    .subscribe(new Observer<BaseResponse<?>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseResponse<?> response) {
                            if (response.getCode() == 0) {
                                llStep1.setVisibility(View.GONE);
                                llStep2.setVisibility(View.VISIBLE);
                                bNext.setVisibility(View.GONE);
                                bCommit.setVisibility(View.VISIBLE);
                            } else {
                                Toast toast = Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 150);
                                toast.show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.e(e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }
}
