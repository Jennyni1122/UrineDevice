package com.leo.device.module.data;

import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.kongzue.dialog.v2.CustomDialog;
import com.leo.device.R;
import com.leo.device.base.viewbox.BaseFragmentBox;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;

/**
 * @author Leo
 * @date 2019-05-24
 */
public class DataBox extends BaseFragmentBox<Object> implements DataContract.View {

    private static final String DATE_FORMAT = "%04d / %02d / %02d";
    protected static final String TIME_FORMAT = "%02d:%02d";

    @BindView(R.id.bDate)
    AppCompatButton bDate;
    @BindView(R.id.bToday)
    AppCompatButton bToday;
    @BindView(R.id.dp)
    DatePicker dp;
    @BindView(R.id.lineChart)
    CombinedChart chart;
    LineDataSet lineDataSet;
    BarDataSet barDataSet;
    @BindView(R.id.bAdd)
    AppCompatButton bAdd;

    DataPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data;
    }

    @Override
    public void bindView() {
        super.bindView();
        presenter = new DataPresenter(this);
        initDatePicker();
        bDate.setOnClickListener(v -> dp.setVisibility(View.VISIBLE));
        bToday.setOnClickListener(v -> initDatePicker());
        initChart(chart);
        bAdd.setOnClickListener(v -> showInputDialog());
    }

    private void showInputDialog() {
        CustomDialog.build(getActivity(), R.layout.dialog_water_intake, new CustomDialog.BindView() {
            @Override
            public void onBind(CustomDialog dialog, View rootView) {
                EditText editText = rootView.findViewById(R.id.et);
                TimePicker timePicker = rootView.findViewById(R.id.tp);

                View bPositive = rootView.findViewById(R.id.bPositive);
                bPositive.setOnClickListener(v -> {
                    if (TextUtils.isDigitsOnly(editText.getText())) {
                        Calendar now = Calendar.getInstance();
                        now.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        now.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        presenter.addWater(Integer.parseInt(editText.getText().toString()), now.getTimeInMillis());
                        dialog.doDismiss();
                    }
                });
                View bNegative = rootView.findViewById(R.id.bNegative);
                bNegative.setOnClickListener(v -> dialog.doDismiss());
            }
        }).showDialog();
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
            dp.setVisibility(View.GONE);
            bDate.setText(String.format(Locale.getDefault(), DATE_FORMAT, year, monthOfYear + 1, dayOfMonth));
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            presenter.getData(c.getTime());
        });
        bDate.setText(String.format(Locale.getDefault(), DATE_FORMAT, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        dp.setVisibility(View.GONE);
        presenter.getData(new Date());
    }

    protected void initChart(BarLineChartBase<CombinedData> chartBase) {
        chartBase.getLegend().setEnabled(false);

        XAxis xAxis = chartBase.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(1440);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(6, true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int minutesOfDay = (int) value;
                int hour = minutesOfDay / 60;
                int minute = minutesOfDay % 60;
                return String.format(Locale.getDefault(), TIME_FORMAT, hour, minute);
            }
        });
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        chartBase.setVisibleXRange(0, 60);
        chartBase.moveViewToX(hour * 60);
        chartBase.setDragEnabled(true);

        // 是否可以缩放 x和y轴
        chartBase.setScaleEnabled(false);
        //是否可以缩放 仅x轴
        chartBase.setScaleXEnabled(false);
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

    public LineData getLineData() {
        // 创建一个数据集,并给它一个类型
        lineDataSet = new LineDataSet(new ArrayList<>(), "");

        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawCircleHole(false);

        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        return lineData;
    }

    /**
     * 设置柱状图绘制数据
     *
     * @return
     */
    public BarData getBarData(List<BarEntry> values) {
        BarData barData = new BarData();
        if (values == null) {
            return barData;
        }
        for (BarEntry value : values) {
            // 创建一个数据集,并给它一个类型
            BarDataSet barDataSet = new BarDataSet(new ArrayList<>(), "");
            barDataSet.setBarBorderWidth(0);
            barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary2));
            barDataSet.addEntry(value);
            barData.addDataSet(barDataSet);
        }
        return barData;
    }

    @Override
    public void bindData(Object bean) {
    }

    @Override
    public void showUrineData(List<Entry> values) {
        if (lineDataSet == null) {
            return;
        }
        lineDataSet.setValues(values);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        //刷新
        chart.invalidate();
    }

    @Override
    public void showWaterData(List<BarEntry> values) {
        chart.getData().setData(getBarData(values));
        chart.notifyDataSetChanged();
        //刷新
        chart.invalidate();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Date date) {
        presenter.getUrineData(date);
    }
}
