package com.leo.device.module.data;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.leo.device.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * @author Leo
 * @date 2019-05-24
 */
public class DataContract {
    interface View extends BaseView {
        void showUrineData(List<Entry> values);

        void showWaterData(List<BarEntry> values);
    }

    interface Presenter {
        void getData(Date date);

        void getUrineData(Date date);

        void getWaterData(Date date);

        void addWater(int capacity, long time);
    }
}
