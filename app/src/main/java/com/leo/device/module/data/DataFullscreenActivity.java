package com.leo.device.module.data;

import android.os.Bundle;
import android.view.OrientationEventListener;
import androidx.annotation.Nullable;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.leo.device.R;
import com.leo.device.base.BaseActivity;
import com.leo.device.base.viewbox.ViewBox;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author Leo90
 */
public class DataFullscreenActivity extends BaseActivity {

    @Override
    protected ViewBox<Object> createViewBox() {
        return new DataBox() {
            @Override
            protected int getLayoutId() {
                return R.layout.activity_data_fullscreen;
            }

            @Override
            protected void initChart(BarLineChartBase<CombinedData> chartBase) {
                chartBase.getLegend().setEnabled(false);

                XAxis xAxis = chartBase.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(1440);
                xAxis.setAxisMinimum(0);
                xAxis.setLabelCount(13, true);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int minutesOfDay = (int) value;
                        int hour = minutesOfDay / 60;
                        int minute = minutesOfDay % 60;
                        return String.format(Locale.getDefault(), TIME_FORMAT, hour, minute);
                    }
                });

                chartBase.setDragEnabled(true);

                // 是否可以缩放 x和y轴
                chartBase.setScaleEnabled(false);
                //是否可以缩放 仅x轴
                chartBase.setScaleXEnabled(true);
                //是否可以缩放 仅y轴
                chartBase.setScaleYEnabled(false);

                //设置x轴和y轴能否同时缩放
                chartBase.setPinchZoom(false);
                //设置是否可以通过双击屏幕放大图表
                chartBase.setDoubleTapToZoomEnabled(false);

                chartBase.getAxisRight().setEnabled(false);
                YAxis yAxis = chartBase.getAxisLeft();
                yAxis.setAxisMinimum(0);

                Description description = new Description();
                description.setText("");
                chartBase.setDescription(description);

                CombinedData data = new CombinedData();
                data.setData(getLineData());
                data.setData(getBarData(null));
                chartBase.setData(data);
            }
        };
    }

    private OrientationEventListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation > 345 || orientation < 15) {
                    finish();
                } else if (orientation > 165 && orientation < 195) {
                    finish();
                }
            }
        };

        if (listener.canDetectOrientation()) {
            listener.enable();
        } else {
            listener.disable();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.disable();
    }


}
