package com.weiwei.home.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseDefaultDialog;
import com.weiwei.home.utils.TextDisposeUtils;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/4/11.
 * @description: 弹窗 - 支付
 */

public class PayDialog extends BaseDefaultDialog implements View.OnClickListener {

    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.rl_red_bag)
    RelativeLayout rl_red_bag;
    @BindView(R.id.rl_wx)
    RelativeLayout rl_wx;
    @BindView(R.id.rl_alipay)
    RelativeLayout rl_alipay;
    @BindView(R.id.cb_redbag)
    CheckBox cb_redbag;
    @BindView(R.id.cb_wx)
    CheckBox cb_wx;
    @BindView(R.id.cb_alipay)
    CheckBox cb_alipay;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.fl_close)
    FrameLayout fl_close;

    private String mCoupon = "0";
    private String mPayChannel = "0";
    private String mDiscountAmount = "0";
    private CheckBox[] checkBoxes = new CheckBox[3];

    private CheckChange mCheckChange;
    private View.OnClickListener mConfirmClickListener;

    public static PayDialog getInstance(){
        return new PayDialog();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_pay;
    }

    public PayDialog setCoupon(String coupon){
        mCoupon = coupon;
        return this;
    }

    public PayDialog setDiscountAmount(String mDiscountAmount){
        this.mDiscountAmount = mDiscountAmount;
        return this;
    }

    public PayDialog setConfirmClick(View.OnClickListener listener){
        this.mConfirmClickListener = listener;
        return this;
    }

    public PayDialog setOnCheckChange(CheckChange mCheckChange){
        this.mCheckChange = mCheckChange;
        return this;
    }

    public interface CheckChange{
        void onCheckChange(String channel);
    }

    @Override
    protected void initView() {
        StringBuilder price = TextDisposeUtils.getTotalAmount("￥",mDiscountAmount,mCoupon);
        tv_price.setText(price);
        checkBoxes[0] = cb_redbag;
        checkBoxes[1] = cb_wx;
        checkBoxes[2] = cb_alipay;
        boolean hasPrice = Float.parseFloat(mDiscountAmount) != 0;
        rl_wx.setVisibility(hasPrice ? View.VISIBLE : View.GONE);
        rl_alipay.setVisibility(hasPrice ? View.VISIBLE : View.GONE);
        fl_close.setOnClickListener(this);
        rl_red_bag.setOnClickListener(this);
        rl_wx.setOnClickListener(this);
        rl_alipay.setOnClickListener(this);
        tv_confirm.setOnClickListener(mConfirmClickListener);
    }

    private void toCheckChange(int index,String payChannel){
        this.mPayChannel = payChannel;
        if(mCheckChange != null){
            mCheckChange.onCheckChange(payChannel);
        }
        for(int i = 0; i < checkBoxes.length ; i ++){
            if(checkBoxes[i] != null){
                checkBoxes[i].setChecked( i == index );
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_red_bag:
                toCheckChange(0,"redpay");
                break;
            case R.id.rl_wx:
                toCheckChange(1,"wxpay");
                break;
            case R.id.rl_alipay:
                toCheckChange(2,"alipay");
                break;
            case R.id.fl_close:
                dismiss();
                break;
        }
    }
}
