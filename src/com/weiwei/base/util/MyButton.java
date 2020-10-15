package com.weiwei.base.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class MyButton extends Button{
	private static final String TAG = "CountDownButton";
    private Context mContext;
    private int countDownTime = 60;//Ĭ����Ҫ����ʱ��ʱ������
    private String countDownText ;//����ʱ֮���������ʾ
    private String endString = "";
    private String startString = "";
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int seconds = -1;
    private String countDownIngFormat ;//���ڵ���ʱʱҪ��ʾ�ĸ�ʽ
     
    /**����ʱ״̬**/
    private boolean isCountDown = false;
     
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 0:
                if (seconds <= 0) {
                    stopCountDown();
                    setText(endString);
                }else{
                	countDownIngFormat = startString +"(%d)s" ;
                    setText(String.format(countDownIngFormat, seconds));
                }
                break;
            default:
                break;
            }
        }
    };
     
     
    public MyButton(Context context) {
        super(context);
        init(context,null);
    }
     
    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
     
    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }
     
    private void init(Context context,AttributeSet attrs) {
        this.mContext = context;
        

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=countDownOnClickListener){
                    countDownOnClickListener.onClickListener();
                }
            }
        });
    }
     
    /**
     * ��ʼ����ʱ
     */
    public void startCountDown(String startString,String endString){
    	this.startString = startString;
    	this.endString = endString;
        setEnabled(false);
        seconds = 5;
        isCountDown = true;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                seconds--;
                Message msg = Message.obtain();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        };
        if (seconds > 0) {
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    /**
     * ֹͣ����ʱ
     */
    public void stopCountDown(){
        if(isCountDown){
            seconds = -1;
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
            setEnabled(true); 
            setText(endString);
            isCountDown = false;
        }
    }
     
    /**
     * �ж��Ƿ��ǵ���ʱ״̬
     */
    public boolean isCountDown(){
        return isCountDown;
    }
     
    private CountDownOnClickListener countDownOnClickListener;
    /**
     * ���ð�ť����
     * @param countDownOnClickListener
     */
    public void setCountDownOnClickListener(CountDownOnClickListener countDownOnClickListener){
        this.countDownOnClickListener = countDownOnClickListener;
    }
     
    public interface CountDownOnClickListener{
        void onClickListener();
    }
}