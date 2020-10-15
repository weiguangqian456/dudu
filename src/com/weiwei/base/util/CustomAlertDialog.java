package com.weiwei.base.util;

import java.util.ArrayList;

import org.miscwidgets.interpolator.EasingType.Type;
import org.miscwidgets.interpolator.ElasticInterpolator;

import com.hwtx.dududh.R;
import com.weiwei.base.util.Panel.OnPanelListener;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.Visibility;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class CustomAlertDialog extends Dialog implements OnPanelListener {
	private Window mWindow;
	private Context context;
	private TextView mAlertDialogTitleTextView;
	private Panel bottomPanel;
	// 所有按钮名字
	private ArrayList<String> button_name;
	// 按钮的父布局
	private LinearLayout alert_dialog_ll;
	// 所有按钮的点击事件
	private ArrayList<View.OnClickListener> click_listener;
	private Button btn;
	private Resources resource;
	private ImageView alert_dialog_title_line;
	/*
	 * author :黄文武 修改时间:14/09/29
	 */
	// 所有按钮控件(TextView做)
	private TextView tv_message1;
	// 分隔线
	private View line1;

	/**
	 * 初始化数据
	 * 
	 * @param context
	 *            上下文
	 * @param button_name
	 *            所有按钮名字
	 * @param click_listener
	 *            所有按钮的点击事件
	 */
	public CustomAlertDialog(Context context, ArrayList<String> button_name,
			ArrayList<View.OnClickListener> click_listener) {
		super(context, R.style.SystemNoticeDialog);
		this.context = context;
		this.button_name = button_name;
		this.click_listener = click_listener;
		resource = context.getResources();
		initView();
		setViewSizeAndLocation();
	}

	private void initView() {
		// 加载布局文件
		Panel panel;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.vs_alert_dialog, null);
		bottomPanel = panel = (Panel) view.findViewById(R.id.alert_dialog);
		mAlertDialogTitleTextView = (TextView) view.findViewById(R.id.alert_dialog_title);
		alert_dialog_title_line = (ImageView) view.findViewById(R.id.alert_dialog_title_line);
		alert_dialog_ll = (LinearLayout) view.findViewById(R.id.alert_dialog_ll);
		/*
		 * alert_dialog_ll = (LinearLayout) view.findViewById(R.id.alert_dialog_ll); alert_dialog_ll.removeAllViews();
		 */
		/*
		 * LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (48.5 *
		 * GlobalVariables.density)); int margins = (int) (32 * GlobalVariables.density); int marginTop = (int) (5*
		 * GlobalVariables.density);
		 */
		tv_message1 = (TextView) view.findViewById(R.id.tv_message1);
		line1 = view.findViewById(R.id.line1);
		for (int i = 0; i < button_name.size(); i++) {
			if (i == 0) {
				tv_message1.setText(button_name.get(i));
				if (click_listener!=null&&click_listener.size() > i) {
					tv_message1.setOnClickListener(click_listener.get(i));
				}
				tv_message1.setVisibility(View.VISIBLE);
				if (i == button_name.size() - 2) {
					tv_message1.setBackgroundResource(R.drawable.vs_whilte_btn_bottom_shape_selecter);
					line1.setVisibility(View.INVISIBLE);
				} else {
					line1.setVisibility(View.VISIBLE);
				}
			} else if (i == button_name.size() - 2) {
				View itemview = View.inflate(context, R.layout.vs_alertdialog_item, null);
				TextView tv_message_item = (TextView) itemview.findViewById(R.id.tv_message_item);
				View line_item = itemview.findViewById(R.id.line_item);
				line_item.setVisibility(view.GONE);
				tv_message_item.setBackgroundResource(R.drawable.vs_whilte_btn_bottom_shape_selecter);
				tv_message_item.setText(button_name.get(i));
				if (click_listener!=null&&click_listener.size() > i) {
					tv_message_item.setOnClickListener(click_listener.get(i));
				}
				alert_dialog_ll.addView(itemview);
			} else if (i == button_name.size() - 1) {
				View itemview = View.inflate(context, R.layout.vs_alertdialog_btn, null);
				Button tv_message_btn = (Button) itemview.findViewById(R.id.btn_wait);
				tv_message_btn.setText(button_name.get(i));
				if (click_listener!=null&&click_listener.size() > i&&click_listener.size()-1==i) {
					tv_message_btn.setOnClickListener(click_listener.get(i));
				}else {
					tv_message_btn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CustomAlertDialog.this.dismiss();
						}
					});
				}
				alert_dialog_ll.addView(itemview);
				break;
			} else {
				View itemview = View.inflate(context, R.layout.vs_alertdialog_item, null);
				TextView tv_message_item = (TextView) itemview.findViewById(R.id.tv_message_item);
				tv_message_item.setText(button_name.get(i));
				if (click_listener!=null&&click_listener.size() > i) {				
					tv_message_item.setOnClickListener(click_listener.get(i));
				}
				alert_dialog_ll.addView(itemview);
			}
			/*
			 * btn = new Button(context); btn.setText(button_name.get(i)); btn.setGravity(Gravity.CENTER);
			 * btn.setTextColor(resource.getColor(R.color.vs_gree));
			 * btn.setBackgroundDrawable(resource.getDrawable(R.drawable.vs_edit_border_shape_bag_selecter));
			 * btn.setTextSize(18); if (i == button_name.size() - 1) { params.setMargins(margins, marginTop, margins,
			 * marginTop); } else { params.setMargins(margins, marginTop, margins, 0); }
			 * btn.setOnClickListener(click_listener.get(i)); btn.setLayoutParams(params); alert_dialog_ll.addView(btn);
			 * btn = null;
			 */
		}
		panel.setOnPanelListener(this);
		panel.setInterpolator(new ElasticInterpolator(Type.OUT, 1.0f, 0.3f));
		// dialog添加视图
		this.setContentView(view);
		// 设置触摸对话框意外的地方取消对话框
		this.setCanceledOnTouchOutside(true);
	}

	/**
	 * 设置视图的大小和显示位置
	 */
	@SuppressWarnings("deprecation")
	private void setViewSizeAndLocation() {
		mWindow = this.getWindow();
		mWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.width = mWindow.getWindowManager().getDefaultDisplay().getWidth(); // 宽度
		mWindow.setAttributes(lp);
	}

	public void setTitle(String title) {
		mAlertDialogTitleTextView.setText(title);
	}

	public void setTitle(int titleId) {
		mAlertDialogTitleTextView.setText(titleId);
	}

	public void setTitltVisibility(boolean bol) {
		if (bol) {
			mAlertDialogTitleTextView.setVisibility(View.VISIBLE);
			alert_dialog_title_line.setVisibility(View.VISIBLE);
		} else {
			mAlertDialogTitleTextView.setVisibility(View.GONE);
			alert_dialog_title_line.setVisibility(View.GONE);
		}
	}

	@Override
	public void show() {
		super.show();
		if (bottomPanel != null) {
			bottomPanel.buttonOnTouch(context);
		}
	}

	@Override
	public void onPanelClosed(Panel panel) {
	}

	@Override
	public void onPanelOpened(Panel panel) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
