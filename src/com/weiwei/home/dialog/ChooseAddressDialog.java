package com.weiwei.home.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.ChooseAddressAdapter;
import com.weiwei.salemall.bean.AddressEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseAddressDialog extends BottomBaseDialog<ChooseAddressDialog> {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private OnClickListener mOnClickListener;
    private List<AddressEntity> mChooseAddressList;
    public ChooseAddressAdapter mAdapter = new ChooseAddressAdapter(mContext);

    public void setOnClickListener(ChooseAddressDialog.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }


    public void setData(List<AddressEntity> chooseAddressList) {
        this.mChooseAddressList = chooseAddressList;
    }

    public ChooseAddressDialog(Context context) {
        super(context);
    }


    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose_address, null);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void setUiBeforShow() {
        mAdapter.setList(mChooseAddressList);
        mAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.iv_choose_address_close, R.id.rtv_choose_else_address})
    public void onViewClicked(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    public interface OnClickListener {
        void onClick(View view);
    }

}

