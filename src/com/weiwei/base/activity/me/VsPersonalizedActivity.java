package com.weiwei.base.activity.me;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.more.VsSetingVoicePianoActivity;
import com.weiwei.base.common.VsUtil;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

public class VsPersonalizedActivity extends VsBaseActivity implements OnClickListener {
    private RelativeLayout pPianoSetLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_presonalized_layout);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getResources().getString(R.string.my_tv9));
        showLeftNavaBtn(R.drawable.icon_back);
        initView();
    }

    private void initView() {
        pPianoSetLayout = (RelativeLayout) findViewById(R.id.pre_piano_set);
        pPianoSetLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre_piano_set:
                startActivity(mContext, VsSetingVoicePianoActivity.class);
                break;
        }

    }

}
