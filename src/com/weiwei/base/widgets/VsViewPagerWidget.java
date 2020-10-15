package com.weiwei.base.widgets;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:通话记录滑动提示
 * @Description:
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-8-30
 */
public class VsViewPagerWidget extends LinearLayout implements OnPageChangeListener {
    private static final String TAG = VsViewPagerWidget.class.getSimpleName();
    /**
	 * ViewPager
	 */
	private ViewPager viewPager;

	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;

	/**
	 * 装ImageView数组
	 */
	private ImageView[] mImageViews;

	/**
	 * 图片资源id
	 */
	private int[] imgIdArray;
	/**
	 * viewpager Item
	 */
	private View[] viewPagerItems;
	/**
	 * 提示大文字
	 */
	private TextView vs_viewpager_textBig;
	/**
	 * 提示小文字
	 */
	private TextView vs_viewpager_textSmall;

	/**
	 * 导入按钮
	 */
	private Button vs_calllog_btn;
	/**
	 * 视图内容
	 */
	private ViewGroup group;
    /**
     * 当系统禁止了读取通话记录权限时，提示文字
     */
    private TextView mCallLogLoadTipTv;

    /**
	 * 
	 * 构造方法
	 * 
	 * @param context
	 */
	public VsViewPagerWidget(Context context, Handler handler) {
		super(context);
//		initView(context);
	}

	/**
	 * 
	 * 构造方法
	 * 
	 * @param context
	 * @param set
	 */
	public VsViewPagerWidget(Context context, AttributeSet set) {
		super(context, set);
//		initView(context);
	}

	private void initView(Context context) {
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.vs_viewpage_layout, this, true);
		group = (ViewGroup) findViewById(R.id.vs_viewGrouplayout);
		viewPager = (ViewPager) findViewById(R.id.vs_viewPagerlayout);
		vs_viewpager_textBig = (TextView) findViewById(R.id.vs_viewpager_textBig);
		vs_viewpager_textSmall = (TextView) findViewById(R.id.vs_viewpager_textSmall);
		vs_calllog_btn = (Button) findViewById(R.id.vs_calllog_btn);
        mCallLogLoadTipTv = (TextView) findViewById(R.id.vs_call_log_load_tip_tv);

		LayoutInflater inflater = LayoutInflater.from(context);
		// 载入图片资源ID
//		imgIdArray = new int[] { R.drawable.drawable_guide_call_0, R.drawable.drawable_guide_call_1,
//				R.drawable.drawable_guide_call_2, R.drawable.drawable_guide_call_3 };

		// 将点点加入到ViewGroup中
		tips = new ImageView[imgIdArray.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.drawable_guide_call_selected);
			} else {
				tips[i].setBackgroundResource(R.drawable.drawable_guide_call_normal);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			group.addView(imageView, layoutParams);
		}
		viewPagerItems = new View[imgIdArray.length];
		// 将图片装载到数组中
		mImageViews = new ImageView[imgIdArray.length];
		for (int i = 0; i < mImageViews.length; i++) {
			View viewItem = inflater.inflate(R.layout.vs_viewpager_item_layout, null);
			viewPagerItems[i] = viewItem;
			ImageView imageView = (ImageView) viewPagerItems[i].findViewById(R.id.item_image);
			mImageViews[i] = imageView;
			imageView.setImageResource(imgIdArray[i]);
		}
		// 设置Adapter
		viewPager.setAdapter(new MyAdapter());
		// 设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(this);
		// 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
		viewPager.setCurrentItem((mImageViews.length) * 100);

	}

    public void checkLoadCalllogPermission(){
        if (isDisenableLoadCalllog()) {
            VsUserConfig.setData(getContext(), VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, false);
            getVs_calllog_btn().setVisibility(View.VISIBLE);
            getVs_calllog_btn().setText(R.string.vs_calllog_inlog);
        } else {
            mCallLogLoadTipTv.setVisibility(GONE);
        }
    }

    private boolean isDisenableLoadCalllog() {
//        boolean ret = insertCallLog("18668421927", "10", "", "1");
//        if (ret){
//
//            return true;
//        } else {
//            return false;
//        }
        return true;
    }

    private boolean insertCallLog(String number,String duration, String type, String news)
    {
        // TODO Auto-generated method stub
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE,type);//未接
        values.put(CallLog.Calls.NEW, news);//0已看1未看

        Uri iRet = getContext().getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);

        if (null == iRet){
            CustomLog.i(TAG, "insertCallLog(), return failed...");
            return false;
        } else {
            CustomLog.i(TAG, "insertCallLog(), return uri is " + iRet.toString());
            return true;
        }
    }
    /**
	 * 
	 * @Title:viewPager适配器
	 * @Description:
	 * @Copyright: Copyright (c) 2014
	 * 
	 * @author: 李志
	 * @version: 1.0.0.0
	 * @Date: 2014-8-30
	 */
	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// ((ViewPager)container).removeView(mImageViews[position %
			// mImageViews.length]);
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			try {
				((ViewPager) container).addView(viewPagerItems[position % mImageViews.length], 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return viewPagerItems[position % mImageViews.length];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0 % mImageViews.length);
		setText(arg0 % mImageViews.length);
	}

	/**
	 * 设置文本内容
	 * 
	 * @param index
	 */
	public void setText(int index) {
		switch (index) {
		case 0:
			vs_viewpager_textBig.setText(R.string.vs_viewpager_big_hint1);
			vs_viewpager_textSmall.setText(R.string.vs_viewpager_small_hint1);
			break;
		case 1:
			vs_viewpager_textBig.setText(R.string.vs_viewpager_big_hint2);
			vs_viewpager_textSmall.setText(R.string.vs_viewpager_small_hint2);
			break;
		case 2:
			vs_viewpager_textBig.setText(R.string.vs_viewpager_big_hint3);
			vs_viewpager_textSmall.setText(R.string.vs_viewpager_small_hint3);
			break;
		case 3:
			vs_viewpager_textBig.setText(R.string.vs_viewpager_big_hint4);
			vs_viewpager_textSmall.setText(R.string.vs_viewpager_small_hint4);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.drawable_guide_call_selected);
			} else {
				tips[i].setBackgroundResource(R.drawable.drawable_guide_call_normal);
			}
		}
	}

	/**
	 * 获取vs_calllog_btn
	 * 
	 * @return vs_calllog_btn
	 */

	public Button getVs_calllog_btn() {
		return vs_calllog_btn;
	}

	/**
	 * 设置vs_calllog_btn
	 * 
	 * @param vs_calllog_btn
	 */
	public void setVs_calllog_btn(Button vs_calllog_btn) {
		this.vs_calllog_btn = vs_calllog_btn;
	}

	/**
	 * 获取group
	 * 
	 * @return group
	 */

	public ViewGroup getGroup() {
		return group;
	}

	/**
	 * 设置group
	 * 
	 * @param group
	 */
	public void setGroup(ViewGroup group) {
		this.group = group;
	}

	/**
	 * 获取viewPager
	 * 
	 * @return viewPager
	 */

	public ViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * 设置viewPager
	 * 
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
	}

}
