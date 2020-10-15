package com.weiwei.base.activity.recharge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.weiwei.base.common.VsUtil;
import com.hwtx.dududh.R;
/**
 * 
 * @Title:Android客户端
 * @Description:充值卡列表组件 
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsRechargeCardWidget extends LinearLayout implements OnClickListener {
	/**
	 * contenxt
	 */
	private Context context;
	/**
	 * 提交成功与否提示图标
	 */
	private ImageView cardlist_widget_hint_imageview;
	/**
	 * 编辑按钮
	 */
	private ImageButton cardlist_widget_edit;
	/**
	 * 卡号
	 */
	private EditText vs_recharge_card_number_edit;
	/**
	 * 密码
	 */
	private EditText vs_recharge_card_password_edit;
	/**
	 * 编辑状态标志
	 */
	private boolean eidtFlag = false;

	/**
	 * 构造方法
	 */
	public VsRechargeCardWidget(Context context) {
		super(context);
		this.context = context;
		// 初始化视图
		initView();
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param set
	 */
	public VsRechargeCardWidget(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
		// 初始化视图
		initView();
	}

	/**
	 * 初始化视图
	 */
	public void initView() {
		eidtFlag = false;
		// 导入布局文件
		LayoutInflater.from(context).inflate(R.layout.vs_recharge_cardlist_widget, this, true);
		// 获取控件对象
		cardlist_widget_hint_imageview = (ImageView) findViewById(R.id.cardlist_widget_hint_imageview);
		cardlist_widget_edit = (ImageButton) findViewById(R.id.cardlist_widget_edit);
		vs_recharge_card_number_edit = (EditText) findViewById(R.id.vs_recharge_card_number_edit);
		vs_recharge_card_password_edit = (EditText) findViewById(R.id.vs_recharge_card_password_edit);
		// 设置监听事件
		cardlist_widget_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		// TODO Auto-generated method stub
		if (!eidtFlag) {
			vs_recharge_card_number_edit.setEnabled(true);
			vs_recharge_card_password_edit.setEnabled(true);
			vs_recharge_card_number_edit.setSelection(vs_recharge_card_number_edit.getText().length());
			vs_recharge_card_number_edit.setBackgroundResource(R.drawable.vs_edit_border_shape_bag_selecter);
			vs_recharge_card_password_edit.setBackgroundResource(R.drawable.vs_edit_border_shape_bag_selecter);
			eidtFlag = true;
		} else {
			vs_recharge_card_number_edit.setEnabled(false);
			vs_recharge_card_password_edit.setEnabled(false);
			vs_recharge_card_number_edit.setBackgroundResource(R.color.lucency);
			vs_recharge_card_password_edit.setBackgroundResource(R.color.lucency);
			eidtFlag = false;
		}
	}

	/**
	 * @return the cardlist_widget_hint_imageview
	 */
	public ImageView getCardlist_widget_hint_imageview() {
		return cardlist_widget_hint_imageview;
	}

	/**
	 * @param cardlist_widget_hint_imageview the cardlist_widget_hint_imageview to set
	 */
	public void setCardlist_widget_hint_imageview(ImageView cardlist_widget_hint_imageview) {
		this.cardlist_widget_hint_imageview = cardlist_widget_hint_imageview;
	}

	/**
	 * @return the cardlist_widget_edit
	 */
	public ImageButton getCardlist_widget_edit() {
		return cardlist_widget_edit;
	}

	/**
	 * @param cardlist_widget_edit the cardlist_widget_edit to set
	 */
	public void setCardlist_widget_edit(ImageButton cardlist_widget_edit) {
		this.cardlist_widget_edit = cardlist_widget_edit;
	}

	/**
	 * @return the vs_recharge_card_number_edit
	 */
	public EditText getKc_recharge_card_number_edit() {
		return vs_recharge_card_number_edit;
	}

	/**
	 * @param vs_recharge_card_number_edit the vs_recharge_card_number_edit to set
	 */
	public void setKc_recharge_card_number_edit(EditText vs_recharge_card_number_edit) {
		this.vs_recharge_card_number_edit = vs_recharge_card_number_edit;
	}

	/**
	 * @return the vs_recharge_card_password_edit
	 */
	public EditText getKc_recharge_card_password_edit() {
		return vs_recharge_card_password_edit;
	}

	/**
	 * @param vs_recharge_card_password_edit the vs_recharge_card_password_edit to set
	 */
	public void setKc_recharge_card_password_edit(EditText vs_recharge_card_password_edit) {
		this.vs_recharge_card_password_edit = vs_recharge_card_password_edit;
	}
	
	
}
