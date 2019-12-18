package com.leo.device.module.data;

import android.text.TextUtils;
import android.text.format.DateUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.leo.device.F;
import com.leo.device.O;
import com.leo.device.base.BasePresenter;
import com.leo.device.bean.data.Urine;
import com.leo.device.bean.data.Water;
import com.leo.device.bean.response.BaseResponse;
import com.leo.device.model.net.RetrofitUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Leo
 * @date 2019-05-24
 */
public class DataPresenter extends BasePresenter<DataContract.View> implements DataContract.Presenter {

    private static final SimpleDateFormat START = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.CHINA);
    private static final SimpleDateFormat END = new SimpleDateFormat("yyyy-MM-dd 23:59:59", Locale.CHINA);

    public DataPresenter(DataContract.View view) {
        super(view);
    }

    private Date lastDate;

    @Override
    public void getData(Date date) {
        lastDate = date;
        getUrineData(date);
        getWaterData(date);
    }

    private static Map<String, String> getRequestMap(Date date) {
        Map<String, String> map = new HashMap<>(4);
        map.put("phoneImei", F.null2Empty(O.getUser().getPhoneImei()));
        map.put("phoneNumber", O.getUser().getPhoneNumber());
        map.put("startTime", START.format(date));
        map.put("endTime", END.format(date));
        return map;
    }

    @Override
    public void getUrineData(Date date) {
        getUrine(date)
                .map(urine -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(urine.getCreateDate());
                    float x = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    float y = urine.getUrineCapacity();
                    return new Entry(x, y);
                })
                .toList()
                .compose(F.ioToMainSingle())
                .subscribe(new SingleObserver<List<Entry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Entry> barEntries) {
                        getView().showUrineData(barEntries);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("===DataPresenter===", e);
                    }
                });
    }

    public static Observable<Urine> getUrine(Date date) {
        return Observable
                .just(getRequestMap(date))
                .flatMap(map -> RetrofitUtil.getService().getUrineList(map))
                .doOnNext(listBaseResponse -> LogUtils.i("===DataPresenter===", GsonUtils.toJson(listBaseResponse)))
                .map(BaseResponse::getData)
                .flatMap(Observable::fromIterable)
                .filter(urine -> urine.getCreateDate() != null);
    }

    @Override
    public void getWaterData(Date date) {
        Observable
                .just(getRequestMap(date))
                .flatMap(map -> RetrofitUtil.getService().getWaterList(map))
                .map(BaseResponse::getData)
                .flatMap(Observable::fromIterable)
                .filter(water -> water.getCreateDate() != null)
                .map(water -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(water.getCreateDate());
                    float x = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    float y = water.getCapacity();
                    return new BarEntry(x, y);
                })
                .toList()
                .compose(F.ioToMainSingle())
                .subscribe(new SingleObserver<List<BarEntry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<BarEntry> barEntries) {
                        getView().showWaterData(barEntries);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("===DataPresenter===", e);
                    }
                });
    }

    @Override
    public void addWater(int capacity, long time) {
        Observable
                .just(capacity)
                .map(integer -> {
                    Water water = new Water();
                    water.setCapacity(integer);
                    water.setTimestamp(time);
                    water.setPhoneImei(O.getUser().getPhoneImei());
                    water.setPhoneNumber(O.getUser().getPhoneNumber());
                    return water;
                })
                .toList()
                .toObservable()
                .flatMap(waterList -> RetrofitUtil.getService().uploadWaterList(waterList))
                .map(BaseResponse::getCode)
                .filter(integer -> integer == 0 && lastDate != null && DateUtils.isToday(lastDate.getTime()))
                .doOnNext(integer -> getWaterData(lastDate))
                .compose(F.ioToMain())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("===DataPresenter===", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
