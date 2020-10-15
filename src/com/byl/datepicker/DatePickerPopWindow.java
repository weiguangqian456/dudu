package com.byl.datepicker;

import java.util.Calendar;

import com.byl.datepicker.wheelview.OnWheelScrollListener;
import com.byl.datepicker.wheelview.WheelView;
import com.byl.datepicker.wheelview.adapter.NumericWheelAdapter;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.DfineAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DatePickerPopWindow extends PopupWindow{
	private Context context;
	private String startTime;
	private int curYear,curMonth;
	private LayoutInflater mInflater;
	private View dateView;
	private WheelView yearView;
	private WheelView monthView;
	private WheelView dayView;
	private WheelView hourView;
	private WheelView minView;
	private WheelView secView;
	private TextView showtime;
	private Button ok_btn;
	private int[] timeInt;
	private String birthday = "";
	public DatePickerPopWindow(Context context,String startTime){
		this.context=context;
		this.startTime=startTime;
		setStartTime();
		initWindow();
	}
	
	private void setStartTime() {
		// TODO Auto-generated method stub
		timeInt=new int[6];
		timeInt[0]=Integer.valueOf(startTime.substring(0, 4));
		timeInt[1]=Integer.valueOf(startTime.substring(4, 6));
		timeInt[2]=Integer.valueOf(startTime.substring(6, 8));
		timeInt[3]=Integer.valueOf(startTime.substring(8, 10));
		timeInt[4]=Integer.valueOf(startTime.substring(10, 12));
		timeInt[5]=Integer.valueOf(startTime.substring(12, 14));
	}
	private void initWindow() {
		// TODO Auto-generated method stub
		mInflater=LayoutInflater.from(context);
		dateView=mInflater.inflate(R.layout.wheel_date_picker, null);
		yearView=(WheelView) dateView.findViewById(R.id.year);
		monthView=(WheelView) dateView.findViewById(R.id.month);
		dayView=(WheelView) dateView.findViewById(R.id.day);
		hourView=(WheelView) dateView.findViewById(R.id.time);
		minView=(WheelView) dateView.findViewById(R.id.minitue);
		secView=(WheelView) dateView.findViewById(R.id.sec);
		secView.setVisibility(View.GONE);
		showtime = (TextView)dateView.findViewById(R.id.showtime);
		ok_btn = (Button)dateView.findViewById(R.id.ok_btn);
		final Intent intent = new Intent(DfineAction.ACTION_GETTIME);
		
		ok_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String time = showtime.getText().toString();
				intent.putExtra("time", time);
				dateView.getContext().sendBroadcast(intent);
				DatePickerPopWindow.this.dismiss();
			}
		});
		initWheel();
	}
	private void initWheel() {
		// TODO Auto-generated method stub
		Calendar calendar=Calendar.getInstance();
		curYear=calendar.get(Calendar.YEAR);
		curMonth=calendar.get(Calendar.MONTH)+1;
		
		NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(context,curYear-100, curYear); 
		numericWheelAdapter1.setLabel("年");
		yearView.setViewAdapter(numericWheelAdapter1);
		yearView.setCyclic(true);
		yearView.addScrollingListener(scrollListener);
		
		NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(context,1, 12, "%02d"); 
		numericWheelAdapter2.setLabel("月");
		monthView.setViewAdapter(numericWheelAdapter2);
		monthView.setCyclic(true);
		monthView.addScrollingListener(scrollListener);
		
		NumericWheelAdapter numericWheelAdapter3=new NumericWheelAdapter(context,1, getDay(curYear, curMonth), "%02d");
		numericWheelAdapter3.setLabel("日");
		dayView.setViewAdapter(numericWheelAdapter3);
		dayView.setCyclic(true);
		dayView.addScrollingListener(scrollListener);
		
		NumericWheelAdapter numericWheelAdapter4=new NumericWheelAdapter(context,0, 23, "%02d"); 
		numericWheelAdapter4.setLabel("?");
		hourView.setViewAdapter(numericWheelAdapter4);
		hourView.setCyclic(true);
		hourView.addScrollingListener(scrollListener);
		
		NumericWheelAdapter numericWheelAdapter5=new NumericWheelAdapter(context,0, 59, "%02d"); 
		numericWheelAdapter5.setLabel("日");
		minView.setViewAdapter(numericWheelAdapter5);
		minView.setCyclic(true);
		minView.addScrollingListener(scrollListener);
		
		yearView.setCurrentItem(timeInt[0]-curYear);
		monthView.setCurrentItem(timeInt[1]-1);
		dayView.setCurrentItem(timeInt[2]-1);
		hourView.setCurrentItem(timeInt[3]);
		minView.setCurrentItem(timeInt[4]);
		yearView.setVisibleItems(7);//???????????
		monthView.setVisibleItems(7);
		dayView.setVisibleItems(7);
		hourView.setVisibleItems(7);
		minView.setVisibleItems(7);
		setContentView(dateView);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
		setBackgroundDrawable(dw);
		setFocusable(true);
	}
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = yearView.getCurrentItem() + curYear;//??
			int n_month = monthView.getCurrentItem() + 1;//??
			
			initDay(n_year,n_month);
			
			 birthday=new StringBuilder().append((yearView.getCurrentItem()+curYear-100)).append("-").append((monthView.getCurrentItem() + 1) < 10 ? "0" + (monthView.getCurrentItem() + 1) : (monthView.getCurrentItem() + 1)).append("-").append(((dayView.getCurrentItem()+1) < 10) ? "0" + (dayView.getCurrentItem()+1) : (dayView.getCurrentItem()+1)).toString();
//			birthday+="	"+hourView.getCurrentItem()+":"+minView.getCurrentItem();
//			Toast.makeText(context, birthday, Toast.LENGTH_SHORT).show();
			showtime.setText(birthday);
		}
	};
	
	
	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel("日");
		dayView.setViewAdapter(numericWheelAdapter);
	}
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
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
