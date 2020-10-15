package com.weiwei.home.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.hwtx.dududh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PromptDialog extends BaseDialog<PromptDialog> {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_ok)
    TextView mTvOk;

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    private String mTitle = "";
    private String mContent = "";
    private String btnText = "好的";

    public PromptDialog(Context context) {
        super(context);
    }

    public PromptDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public PromptDialog setContent(String content) {
        this.mContent = content;
        return this;
    }

    public PromptDialog setBtnText(String btnText) {
        this.btnText = btnText;
        return this;
    }

    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_invoice, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void setUiBeforShow() {
        mTvTitle.setText(mTitle);
        mTvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? View.GONE : View.VISIBLE);
        mTvContent.setText(mContent);
        mTvOk.setText(btnText);
    }


    @OnClick({R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_ok:
                dismiss();
                break;
        }
    }

    public interface OnClickListener {
        void onClick(View view);
    }

}

