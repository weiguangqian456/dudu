package com.weiwei.home.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseDefaultDialog;
import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description: 签到完成
 */

public class SignAccomplishDialog extends BaseDefaultDialog {

    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.tv_get)
    TextView tv_get;

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sign_accomplish;
    }

    @Override
    protected void initView() {
        if(getArguments() != null){
            String value = getArguments().getString("value");
            String str = "恭喜获得" + value + "积分";
            tv_get.setText(str);
        }
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
