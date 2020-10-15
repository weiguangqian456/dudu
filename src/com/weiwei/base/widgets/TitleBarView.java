package com.weiwei.base.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.util.SystemMethod;

public class TitleBarView extends RelativeLayout {
    private Context mContext;
    private TextView btnLeft;
    private ImageButton btnRight;
    private Button btn_titleLeft;
    private Button btn_titleRight;
    private TextView tv_center;
    private LinearLayout common_constact;
    private TextView title_txt_right;
    private ImageView title_image_right;
    private TextView mms_unread;
    private RelativeLayout calllog_stop,rl_back;
    private ImageView calllog_image_up;
    private TextView dial_select_choose;
    private Button callBtn, contactBtn;

    public TitleBarView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.vs_common_title_bar, this);
        btnLeft = (TextView) findViewById(R.id.title_btn_left);
        btnRight = (ImageButton) findViewById(R.id.title_btn_right);
        btn_titleLeft = (Button) findViewById(R.id.constact_group);
        btn_titleRight = (Button) findViewById(R.id.constact_all);
        tv_center = (TextView) findViewById(R.id.title_txt);
        common_constact = (LinearLayout) findViewById(R.id.common_constact);
        title_image_right = (ImageView) findViewById(R.id.title_image_right);
        title_txt_right = (TextView) findViewById(R.id.title_txt_right);
        mms_unread = (TextView) findViewById(R.id.mms_unread);
        calllog_stop = (RelativeLayout) findViewById(R.id.calllog_stop);
        calllog_image_up = (ImageView) findViewById(R.id.calllog_image_up);
        dial_select_choose = (TextView) findViewById(R.id.dial_select_choose);
        callBtn = (Button) findViewById(R.id.btn_call_register);
        contactBtn = (Button) findViewById(R.id.btn_contacts);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
    }

    public void setCommonTitle(int LeftVisibility, int centerVisibility, int center1Visibilter, int rightVisibility) {
        btnLeft.setVisibility(LeftVisibility);
        btnRight.setVisibility(rightVisibility);
        tv_center.setVisibility(centerVisibility);
        common_constact.setVisibility(center1Visibilter);
    }

    public void setTextMms(int num, boolean show) {
        if (show) {
            if (num > 0) {
                mms_unread.setText(num + "");
                mms_unread.setVisibility(View.VISIBLE);
            } else {
                mms_unread.setVisibility(View.GONE);
            }
        } else {
            mms_unread.setVisibility(View.GONE);
        }
    }

    public void setBtnLeft(int icon, int txtRes) {
        Drawable img = mContext.getResources().getDrawable(icon);
        int height = SystemMethod.dip2px(mContext, 20);
        int width = img.getIntrinsicWidth() * height / img.getIntrinsicHeight();
        img.setBounds(0, 0, width, height);
        btnLeft.setText(txtRes);
        btnLeft.setCompoundDrawables(img, null, null, null);
    }

    public void setIMG(int icon) {
        calllog_image_up.setBackgroundResource(icon);
    }

    public ImageView getIMG() {
        return calllog_image_up;
    }

    public void setBtnLeft(int txtRes) {
        btnLeft.setText(txtRes);
    }

    public TextView getBtnLeft() {
        return btnLeft;
    }

    public void setBtnRight(int icon) {
        Drawable img = mContext.getResources().getDrawable(icon);
        int height = SystemMethod.dip2px(mContext, 30);
        int width = img.getIntrinsicWidth() * height / img.getIntrinsicHeight();
        img.setBounds(0, 0, width, height);
        //btnRight.setCompoundDrawables(img, null, null, null);
    }

    public void setTitleLeft(int resId) {
        btn_titleLeft.setText(resId);
    }

    public void setTitleRight(int resId) {
        btn_titleRight.setText(resId);
    }

//	public void settCalllogStop(int resId) {
//		calllog_stop.setT
//	}

    public void setTxtRight(int resId) {
        title_txt_right.setText(resId);
    }

    public void setTxtDialSelect(String string) {
        dial_select_choose.setText(string);
    }

    public void setTxtRightShow() {
        title_txt_right.setVisibility(View.VISIBLE);
    }

    public void setTxtRightHide() {
        title_txt_right.setVisibility(View.GONE);
    }

    public void setImageRightShow() {
        title_image_right.setVisibility(View.VISIBLE);
    }

    public void setImageRightHide() {
        title_image_right.setVisibility(View.GONE);
    }

    /**
     * 隐藏全部通话+未接来电选择行
     */
    public void setCalllogStopHide() {
        calllog_stop.setVisibility(GONE);
    }

    /**
     * 显示全部通话+未接来电选择行
     */
    public void setCalllogStopShow() {
        calllog_stop.setVisibility(VISIBLE);
    }

    /*
     * @SuppressLint("NewApi") public void setPopWindow(PopupWindow
	 * mPopWindow,TitleBarView titleBaarView){
	 * mPopWindow.setBackgroundDrawable(new
	 * ColorDrawable(Color.parseColor("#E9E9E9")));
	 * mPopWindow.showAsDropDown(titleBaarView, 0,-15);
	 * mPopWindow.setAnimationStyle(R.style.popwin_anim_style);
	 * mPopWindow.setFocusable(true); mPopWindow.setOutsideTouchable(true);
	 * mPopWindow.update();
	 * 
	 * setBtnRight(R.drawable.skin_conversation_title_right_btn_selected); }
	 */

    public void setTitleText(int txtRes) {
        tv_center.setText(txtRes);
    }

    public void setBtnLeftOnclickListener(OnClickListener listener) {
        btnLeft.setOnClickListener(listener);
    }

    public void setBtnRightOnclickListener(OnClickListener listener) {
        btnRight.setOnClickListener(listener);
    }

    public void setLayoutCalllogOnclickListener(OnClickListener listener) {
        calllog_stop.setOnClickListener(listener);
    }

    public void setLayoutBackOnclickListener(OnClickListener listener) {
        rl_back.setOnClickListener(listener);
    }

    public Button getTitleLeft() {
        return btn_titleLeft;
    }

    public Button getTitleRight() {
        return btn_titleRight;
    }

    public ImageButton getRightBtn() {
        return btnRight;
    }

    public TextView getTxtRight() {
        return title_txt_right;
    }

    public ImageView getImageRight() {
        return title_image_right;
    }

    public Button getCallBtn() {
        return callBtn;
    }

    public void setCallBtn(Button callBtn) {
        this.callBtn = callBtn;
    }

    public Button getContactBtn() {
        return contactBtn;
    }

    public void setContactBtn(Button contactBtn) {
        this.contactBtn = contactBtn;
    }

    public void destoryView() {
        btnLeft.setText("");
        //btnRight.setText(null);
        tv_center.setText(null);
    }
}
