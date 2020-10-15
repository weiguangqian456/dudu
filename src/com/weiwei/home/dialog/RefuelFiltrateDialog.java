package com.weiwei.home.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyco.dialog.widget.base.TopBaseDialog;
import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.home.adapter.RefuelFiltrateAdapter;
import com.weiwei.salemall.bean.RefuelFiltrate;
import com.weiwei.salemall.utils.DeviceUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefuelFiltrateDialog extends TopBaseDialog<RefuelFiltrateDialog> {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_choose_all)
    LinearLayout mLlChooseAll;

    private OnClickListener mOnClickListener;
    private List<RefuelFiltrate> mChooseAddressList;
    public RefuelFiltrateAdapter mAdapter = new RefuelFiltrateAdapter(mContext);
    private View mAnchorView;
    private boolean mIsAllChoose;

    public void setIsAllChoose(boolean isAllChoose) {
        this.mIsAllChoose = isAllChoose;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setData(List<RefuelFiltrate> chooseAddressList) {
        this.mChooseAddressList = chooseAddressList;
    }

    public RefuelFiltrateDialog(Context context, View anchorView) {
        super(context);
        mAnchorView = anchorView;
    }


    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_refuel_filtrate, null);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void setUiBeforShow() {
        mLlChooseAll.setVisibility(mIsAllChoose ? View.VISIBLE : View.GONE);
        mAdapter.setIsAllChoose(mIsAllChoose);
        mAdapter.setList(mChooseAddressList);
        mAdapter.notifyDataSetChanged();
    }


//    @OnClick({R.id.rtv_choose_else_address})
//    public void onViewClicked(View view) {
//        if (mOnClickListener != null) {
//            mOnClickListener.onClick(view);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        final Rect rect = new Rect();
        mAnchorView.getWindowVisibleDisplayFrame(rect);

        int[] location = new int[2];
        mAnchorView.getLocationOnScreen(location);

        int y = location[1] + mAnchorView.getHeight();

        lp.dimAmount = 0;
        lp.gravity = Gravity.TOP;
        lp.x = location[0];

        lp.y = y;

        mLlTop.setBackgroundColor(0x80000000);
        mLlTop.setGravity(Gravity.TOP);
        mLlTop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.getScreenHeight(mContext) - y - 1));

    }

    @OnClick({R.id.rtv_cancel, R.id.rtv_confirm})
    public void onViewClicked(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    public interface OnClickListener {
        void onClick(View view);
    }

}

