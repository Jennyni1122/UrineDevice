package com.leo.device;

import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.leo.device.base.viewbox.ViewBox;
import com.leo.device.bean.data.User;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Leo
 * @date 2019-05-07
 */
public class F {

    public static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return;
        } catch (Exception e) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> ObservableTransformer<T, T> ioToMain() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> ioToMainSingle() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> log() {
        return upstream -> upstream.doOnNext(t -> {
            if (BuildConfig.DEBUG) {
                LogUtils.i("===TAG===", GsonUtils.toJson(t));
            }
        });
    }

    public static void getAndSetUser(ViewBox viewBox) {
        Observable.just("")
                .map(s -> O.getUser())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(User user) {
                        viewBox.bindData(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public static String null2Empty(String str) {
        return str == null ? "" : str;
    }
}
