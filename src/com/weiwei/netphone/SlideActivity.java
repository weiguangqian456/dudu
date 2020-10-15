package com.weiwei.netphone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.login.VsStartActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.widgets.OnViewChangeListener;
import com.weiwei.base.widgets.ScrollLayout;
import com.hwtx.dududh.R;

/**
 * 
 * @Title: 滑屏
 * @Description:
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 石云升
 * @version: 1.0.0.0
 * @Date: 2014-8-29
 */
public class SlideActivity extends VsBaseActivity implements OnViewChangeListener, View.OnClickListener {
	/**
	 * This must be false for production. If true, turns on logging, test code,
	 * etc.
	 */

	private String TAG = "SlideActivity";
	/**
	 * 滚动layout
	 */
	private ScrollLayout mScrollLayout;
	private LinearLayout pointLLayout;
	private ImageView[] imgs;
	private int viewPageCount;
	private int currentItem;
	/**
	 * 是否从小C教你玩VS进入
	 */
	public boolean isCourse = false;

    private Button mGoTo1Btn, mGoTo2Btn;
    private boolean hasAccount = false;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取消状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vs_viewpager_image);
        initView();
        setDisEnableLeftSliding();

        hasAccount = VsUtil.checkHasAccount(mContext);
        hasAccount = !VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, false) && hasAccount;
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void initView() {
		/**
		 * 获取传递的参数
		 */
		Intent intent = getIntent();
		isCourse = intent.getBooleanExtra("isCourse", false);

		VsApplication.getInstance().addActivity(this);
		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mScrollLayout.SetOnViewChangeListener(this);
		viewPageCount = mScrollLayout.getChildCount();
		imgs = new ImageView[viewPageCount];
		for (int i = 0; i < viewPageCount; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

        mGoTo1Btn = (Button) findViewById(R.id.go_to_btn_11);
        mGoTo2Btn = (Button) findViewById(R.id.go_to_btn_12);

        mGoTo1Btn.setOnClickListener(this);
        mGoTo2Btn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void OnViewChange(int position, boolean jumpBol) {
		setcurrentPoint(position, jumpBol);
	}

	private void setcurrentPoint(int position, boolean jumpBol) {
		if (jumpBol) {
			if (position < 0 || position > viewPageCount - 1 || currentItem == position) {
				return;
			}
			if (position == 1) {
			} else if (position == 2) {
			}
			imgs[currentItem].setEnabled(true);
			imgs[position].setEnabled(false);
			currentItem = position;
		} else {
			if (isCourse) {
				finish();
				return;
			}
			if (hasAccount) {
				startActivity(SlideActivity.this, VsMainActivity.class);
			} else {
				startActivity(SlideActivity.this, VsMainActivity.class);
			}
			finish();
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_btn_11:
            case R.id.go_to_btn_12:
                if (hasAccount) {
                    startActivity(SlideActivity.this, VsMainActivity.class);
                } else {
                    startActivity(SlideActivity.this, VsMainActivity.class);
                }
                finish();
                break;
            default:
                break;
        }
    }
}
