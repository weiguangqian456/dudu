package com.weiwei.home.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseDefaultDialog;
import com.weiwei.home.base.BaseDefaultDialogNotV4;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/3/13.
 * @description: 提示弹窗
 */

public class HintMessageNotV4Dialog extends BaseDefaultDialogNotV4{

    @BindView(R.id.tv_massage)
    TextView tv_massage;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.fl_close)
    FrameLayout fl_close;

    private boolean isShowCancel = true;
    private String mMessage = "提示";
    private String mConfirmText;
    private OnDialogClickListener mConfirmlistener;

    public static HintMessageNotV4Dialog getInstance(){
        return new HintMessageNotV4Dialog();
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_hint;
    }

    @Override
    protected void initView() {
        tv_massage.setText(mMessage);
        tv_confirm.setText(mConfirmText);
        fl_close.setVisibility(isShowCancel ? View.VISIBLE : View.GONE);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConfirmlistener != null){
                    mConfirmlistener.onDialogClick(HintMessageNotV4Dialog.this);
                }else{
                    dismiss();
                }
            }
        });
        fl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnDialogClickListener{
        void onDialogClick(HintMessageNotV4Dialog dialog);
    }

    @Override
    protected Boolean isLoadAnimation() {
        return false;
    }

    public HintMessageNotV4Dialog setMessage(String text){
        mMessage = text;
        return this;
    }
}
