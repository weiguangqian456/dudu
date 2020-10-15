package com.weiwei.base.activity.more;

import java.io.RandomAccessFile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.widgets.NoticeDialog;
import com.weiwei.lock.LockPatternUtils;
import com.hwtx.dududh.R;




public class KcUnlockedActivity extends VsBaseActivity implements OnClickListener  {

	private ImageView set_unlocked_img;
	private RelativeLayout re_unlocked_id;
	private boolean unlockedSwitch = false;
	private Context mContext = this;
	private NoticeDialog.Builder builder = new NoticeDialog.Builder(mContext); 
	 Boolean boolean_reslut;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unlocked_setting);
		
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		mTitleTextView.setText(getResources().getString(R.string.vs_unlocked_pwd_str));
		 init();
		
		 VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}
	
		private void init() {
			 boolean_reslut = if_reslut();
			set_unlocked_img = (ImageView) findViewById(R.id.set_unlocked_img);//手势密码开关
			unlockedSwitch = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_UNLOCKED_BTN, false);
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(LockPatternUtils.sLockPatternFilename,"rwd");
				if (raf.length() == 0) {
					set_unlocked_img.setImageResource(R.drawable.vs_switch_close);
					re_unlocked_id.setVisibility(View.GONE);
				}else{
					set_unlocked_img.setImageResource(R.drawable.vs_switch_open);
					re_unlocked_id.setVisibility(View.VISIBLE);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			set_unlocked_img.setOnClickListener(this);
			
			
			re_unlocked_id = (RelativeLayout) findViewById(R.id.re_unlocked_id);
			re_unlocked_id.setOnClickListener(this);
			
		}


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.set_unlocked_img: {

				NoticeDialog dialog = builder.create();
			    dialog.show();
			    builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
//		  
		            	 if (builder.getBoolean_pass()) {
		            		 boolean_reslut = if_reslut();
//		            		 unlockedSwitch = !unlockedSwitch;
//		            	set_unlocked_img.setImageResource(unlockedSwitch ? R.drawable.vs_switch_open : R.drawable.vs_switch_close);
		     				if (!boolean_reslut) {
		     							Intent intent = new Intent(KcUnlockedActivity.this,CreateGesturePasswordActivity.class);
		     							startActivityForResult(intent, 1);
			
		     				}else{
		     					re_unlocked_id.setVisibility(View.GONE);
		     					new LockPatternUtils(getApplicationContext()).clearLock();
		     					set_unlocked_img.setImageResource(R.drawable.vs_switch_close);
		     				}
		     			    }else{
		     			    	Toast.makeText(mContext, "密码错误",Toast.LENGTH_SHORT ).show();
		     			    }	
		            	
		            }
		        });
			   
				break;
				}
			case R.id.re_unlocked_id:{
//				new LockPatternUtils(getApplicationContext()).clearLock();
				Intent intent = new Intent(this,CreateGesturePasswordActivity.class);
				mContext.startActivity(intent);
			}
			}
		}

		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			Boolean boolean2 = if_reslut();
			if (!boolean2) {
				set_unlocked_img.setImageResource(R.drawable.vs_switch_close);
				re_unlocked_id.setVisibility(View.GONE);
		}else{
			re_unlocked_id.setVisibility(View.VISIBLE);
//			new LockPatternUtils(getApplicationContext()).clearLock();
			set_unlocked_img.setImageResource(R.drawable.vs_switch_open);
		}
		}

		@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
			
			System.out.println("onRestart()");
		}
		
		private Boolean if_reslut(){
			RandomAccessFile raf;
			Boolean boolean1 = false;
			try {
				raf = new RandomAccessFile(LockPatternUtils.sLockPatternFilename,"rwd");
				if (raf.length() == 0) {
					boolean1 = false;

				}else{
					boolean1 = true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return boolean1;
			
}
	
}
