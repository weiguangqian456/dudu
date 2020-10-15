package com.weiwei.home.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.byl.datepicker.wheelview.OnWheelScrollListener;
import com.byl.datepicker.wheelview.WheelView;
import com.byl.datepicker.wheelview.adapter.NumericWheelAdapter;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.home.base.BaseDefaultDialogNotV4;
import com.weiwei.home.test.ValidClickListener;

import java.util.Calendar;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/4/16.
 * @description: 日期选择器
 */

public class DateSelectDialog extends BaseDefaultDialogNotV4 {

    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;

    @BindView(R.id.wv_year)
    WheelView wv_year;
    @BindView(R.id.wv_month)
    WheelView wv_month;
    @BindView(R.id.wv_day)
    WheelView wv_day;
    @BindView(R.id.wv_hour)
    WheelView wv_hour;
    @BindView(R.id.wv_minute)
    WheelView wv_minute;
    @BindView(R.id.wv_sec)
    WheelView wv_sec;

    private int curYear;
    private int curMonth;
    private int[] timeInt;

    public static DateSelectDialog getInstance(){
        return new DateSelectDialog();
    }

    public DateSelectDialog setInitTime(String startTime) {
        timeInt = new int[6];
        timeInt[0] = Integer.valueOf(startTime.substring(0, 4));
        timeInt[1] = Integer.valueOf(startTime.substring(4, 6));
        timeInt[2] = Integer.valueOf(startTime.substring(6, 8));
        timeInt[3] = Integer.valueOf(startTime.substring(8, 10));
        timeInt[4] = Integer.valueOf(startTime.substring(10, 12));
        timeInt[5] = Integer.valueOf(startTime.substring(12, 14));
        return this;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_date_select;
    }

    @Override
    protected void initView() {
        tv_confirm.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                Intent intent = new Intent(DfineAction.ACTION_GETTIME);
                String time = tv_date.getText().toString();
                intent.putExtra("time", time);
                mContext.sendBroadcast(intent);
                dismiss();
            }
        });
        initDate();
    }

    private void initDate(){
        Calendar calendar=Calendar.getInstance();
        curYear  = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH)+1;
        initWheelView(wv_year ,"年",curYear-100,curYear,"");
        initWheelView(wv_month,"月",1,12,"%02d");
        initWheelView(wv_day  ,"日",1,getDay(curYear, curMonth),"%02d");
        wv_year.setCurrentItem(timeInt[0]-curYear);
        wv_month.setCurrentItem(timeInt[1]-1);
        wv_day.setCurrentItem(timeInt[2]-1);
    }

    private void initWheelView(WheelView wheelView,String label,int min,int max,String format){
        NumericWheelAdapter numericWheelAdapter1;
        if (TextUtils.isEmpty(format)){
            numericWheelAdapter1 = new NumericWheelAdapter(mContext,min, max);
        }else{
            numericWheelAdapter1 = new NumericWheelAdapter(mContext,min, max,format);
        }
        numericWheelAdapter1.setLabel(label);
        wheelView.setViewAdapter(numericWheelAdapter1);
        wheelView.setCyclic(true);
        wheelView.addScrollingListener(scrollListener);
    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = wv_year.getCurrentItem() + curYear;
            int n_month = wv_month.getCurrentItem() + 1;
            initDay(n_year,n_month);
            String birthday = String.valueOf((wv_year.getCurrentItem() + curYear - 100)) + "-"
                    + ((wv_month.getCurrentItem() + 1) < 10 ? "0" + (wv_month.getCurrentItem() + 1): (wv_month.getCurrentItem() + 1)) + "-"
                    + (((wv_day.getCurrentItem() + 1) < 10) ? "0" + (wv_day.getCurrentItem() + 1) : (wv_day.getCurrentItem() + 1));
            tv_date.setText(birthday);
        }
    };

    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        wv_day.setViewAdapter(numericWheelAdapter);
    }

    private int getDay(int year, int month) {
        int day;
        boolean flag;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }
}
