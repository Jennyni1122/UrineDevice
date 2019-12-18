package com.leo.device.module.login;

import android.Manifest;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.BaseActivityBox;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.User;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.net.RetrofitUtil;
import com.leo.device.module.main.MainActivity;
import com.leo.device.module.register.RegisterActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * @author Leo
 */
public class LoginActivity extends BaseActivity {

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
        @BindView(R.id.bCommit)
        AppCompatButton bCommit;
        @BindView(R.id.tvToLogin)
        AppCompatTextView tvToLogin;
        @BindView(R.id.tvPhoneError)
        AppCompatTextView tvPhoneError;
        @BindView(R.id.tvCodeError)
        AppCompatTextView tvCodeError;

        RxPermissions rxPermissions;

        @Override
        protected int getLayoutId() {
            return R.layout.activity_login;
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
                    if (charSequence.length() > 0) {
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

            rxPermissions = new RxPermissions((FragmentActivity) getActivity());
            rxPermissions
                    .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .compose(F.ioToMain())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (aBoolean) {
                                if (O.getUser() != null) {
                                    toMain(false);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            tvTitle.setText(R.string.login);
            bCapture.setOnClickListener(v -> getCapture());
            bCommit.setOnClickListener(v -> login());
            tvToLogin.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            });
        }

        private boolean check() {
            boolean flag = false;
            if (TextUtils.isEmpty(etPhone.getText())) {
                tvPhoneError.setText("手机号码为空");
                flag = true;
            } else if (etPhone.getText().length() != 11) {
                tvPhoneError.setText("手机号不合法");
                flag = true;
            }
            if (TextUtils.isEmpty(etCode.getText())) {
                tvCodeError.setText("验证码为空");
                flag = true;
            }
            return flag;
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
                tvPhoneError.setText("手机号不合法");
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
                    .getLoginCapture(etPhone.getText().toString())
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

        private void login() {
            if (check()) {
                return;
            }
            Observable
                    .just(new User())
                    .doOnNext(user -> {
                        user.setPhoneNumber(etPhone.getText().toString());
                        user.setCapture(etCode.getText().toString());
                    })
                    .flatMap(user -> RetrofitUtil.getService().login(user))
                    //     .map(BaseResponse::getData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(BaseResponse response) {
                            if (response.getCode() == 0) {
                                O.setUser((User) response.getData());
                                toMain(O.getDevice() == null);
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

        private void toMain(boolean needConnect) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(MainActivity.NEED_CONNECT, needConnect);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }
}
