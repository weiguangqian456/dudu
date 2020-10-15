package com.weiwei.base.activity.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.qrcode.encoding.EncodingHandler;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description: 二维码界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-10-27
 */
public class VsQRCodeActivity extends VsBaseActivity {
	/**
	 * 提示语句
	 */
	private TextView vs_qrcode_tv1;
	/**
	 * 二维码
	 */
	private ImageView vs_qrcode_imageview;
	/**
	 * logo
	 */
	private Bitmap foreground = null;
	/**
	 * 二维码图片
	 */
	private Bitmap qrCodeBitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_qrcode_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		mTitleTextView.setText(R.string.vs_qrcode_title);
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		initView();
		VsApplication.getInstance().addActivity(this);
	}

	/**
	 * 初始化
	 */
	public void initView() {
		vs_qrcode_tv1 = (TextView) findViewById(R.id.vs_qrcode_tv1);
		vs_qrcode_imageview = (ImageView) findViewById(R.id.vs_qrcode_imageview);
		// 设置文本内容
		vs_qrcode_tv1.setText(Html.fromHtml("<B><font color=#272727>"
				+ getResources().getString(R.string.vs_qrcode_hint1) + "</font></B>"));
		vs_qrcode_tv1.append(Html.fromHtml("<font color=#595959>" + getResources().getString(R.string.vs_qrcode_hint2)
				+ "</font>"));
		//生成二维码
         new myAsyncTask().execute();
	}
     /**
      * 
      * @Title:Android客户端
      * @Description: 生成二维码异步线程
      * @Copyright: Copyright (c) 2014
      * 
      * @author: 李志
      * @version: 1.0.0.0
      * @Date: 2014-10-31
      */
	class myAsyncTask extends AsyncTask<String, Integer, Bitmap> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (GlobalVariables.width == 0) {
				VsUtil.setDensityWH(mContext);
			}

			try {
				int width = (int) (1 * GlobalVariables.width);
				String str = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_DIECODE_SHARE_CONTENT);
				if (str == null || "".equals(str)) {
					str = "wap.weishuo.cn";
				}
				foreground = BitmapFactory.decodeResource(getResources(), R.drawable.vs_logo_qrcode);
				qrCodeBitmap = EncodingHandler.createQRCode(str, width, foreground);
				return qrCodeBitmap;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				vs_qrcode_imageview.setImageBitmap(qrCodeBitmap);
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 释放资源
		if (foreground != null) {
			foreground.recycle();
			foreground=null;
		}
		if (qrCodeBitmap != null) {
			qrCodeBitmap.recycle();
			qrCodeBitmap=null;
		}
	}
}
