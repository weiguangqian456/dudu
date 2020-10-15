package com.weiwei.home.fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.home.base.BaseDefaultDialog;
import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/3/27.
 * @description: 更新弹窗
 */

public class UpDataDialog extends BaseDefaultDialog {

    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.fl_close)
    FrameLayout fl_close;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;

    private String version = "1.0.0";
    private String content = "APP优化";
    private UpDataListener listener;
    private boolean isMandatory = false;

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_updata;
    }

    @Override
    protected Boolean isLoadAnimation() {
        return false;
    }

    public static UpDataDialog getInstance() {
        return new UpDataDialog();
    }

    public UpDataDialog setVersion(String version){
        if(!TextUtils.isEmpty(version)){
            this.version = version;
        }
        return this;
    }

    public UpDataDialog isUpMandatory(boolean isMandatory){
        this.isMandatory =isMandatory;
        return this;
    }

    public UpDataDialog setContent(String content){
        this.content = content;
        return this;
    }

    @Override
    protected void initView() {
        setCancelable(false);
        String v = "更新版本: " + version;
        tv_version.setText(v);
        tv_content.setText(content);
        if(isMandatory) fl_close.setVisibility(View.GONE);
        fl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){  listener.onUpDataClick(); }
                dismiss();
            }
        });
    }

    public UpDataDialog setUpDataListener(UpDataListener listener){
        this.listener = listener;
        return this;
    }

    public interface UpDataListener{
        void onUpDataClick();
    }
}
