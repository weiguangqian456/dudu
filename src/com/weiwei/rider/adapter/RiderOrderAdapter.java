package com.weiwei.rider.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hwtx.dududh.R;
import com.weiwei.rider.activity.RiderOrderDetailActivity;
import com.weiwei.rider.bean.Order;
import com.weiwei.rider.utils.LocationUtils;
import com.weiwei.salemall.utils.ToastUtils;

import java.util.List;

public class RiderOrderAdapter extends BaseQuickAdapter<Order,BaseViewHolder>{
    private String riderId;
    private SeekBar seekBar;
    public RiderOrderAdapter( @Nullable List<Order> data,String riderId) {
        super(R.layout.rider_order_item, data);
        this.riderId = riderId;
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        seekBar = helper.getView(R.id.seek_bar);
        seekBar.setProgress(0);
        helper.setText(R.id.tv_time,item.getDelivery())
                .setText(R.id.tv_distance,item.getDistance()+"米")
                .setText(R.id.tv_city,item.getAddress())
                .setText(R.id.tv_detail,item.getStoreAddress());
        if(item.getRiderOrderStatus().equals("0")) {
            helper.setText(R.id.tv_intro,"右滑确认抢单");
            helper.getView(R.id.btn_confirm).setVisibility(View.GONE);
            helper.getView(R.id.seek_bar).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_cancel).setVisibility(View.GONE);
            helper.getView(R.id.tv_call_phone).setVisibility(View.GONE);
        }else if(item.getRiderOrderStatus().equals("1")) {
            helper.setText(R.id.tv_intro,"右滑确认拣货");
            helper.getView(R.id.btn_confirm).setVisibility(View.GONE);
            helper.getView(R.id.seek_bar).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_cancel).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_call_phone).setVisibility(View.GONE);
        }else if(item.getRiderOrderStatus().equals("2")) {
            helper.setText(R.id.tv_intro,"右滑确认配送完成");
            helper.getView(R.id.btn_confirm).setVisibility(View.GONE);
            helper.getView(R.id.seek_bar).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_cancel).setVisibility(View.GONE);
            helper.getView(R.id.tv_call_phone).setVisibility(View.VISIBLE);
        }else if(item.getRiderOrderStatus().equals("3")) {
            helper.getView(R.id.btn_confirm).setVisibility(View.VISIBLE);
            helper.getView(R.id.seek_bar).setVisibility(View.GONE);
            helper.setText(R.id.btn_confirm,"配送已完成");
            helper.setBackgroundRes(R.id.btn_confirm, R.drawable.rider_order_gray_button).setTextColor(R.id.btn_confirm,mContext.getResources().getColor(R.color.White));
            helper.getView(R.id.rl_distance).setVisibility(View.GONE);
            helper.getView(R.id.tv_cancel).setVisibility(View.GONE);
            helper.getView(R.id.tv_call_phone).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.btn_confirm).setVisibility(View.GONE);
            helper.getView(R.id.seek_bar).setVisibility(View.VISIBLE);
            helper.getView(R.id.rl_distance).setVisibility(View.VISIBLE);
            helper.getView(R.id.tv_cancel).setVisibility(View.GONE);
            helper.getView(R.id.tv_call_phone).setVisibility(View.GONE);
        }
        helper.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, RiderOrderDetailActivity.class);
            intent.putExtra("id",item.getId());
            intent.putExtra("riderId",riderId);
            intent.putExtra("riderOrderStatus",item.getRiderOrderStatus());
            mContext.startActivity(intent);
        });
        if(item.getRiderOrderStatus().equals("1") || item.getRiderOrderStatus().equals("2")) {
            helper.getView(R.id.tv_nav).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.tv_nav).setVisibility(View.GONE);
        }
        helper.getView(R.id.tv_nav).setOnClickListener(view -> {
            String[] split;
            if(item.getRiderOrderStatus().equals("1")) {
                split = item.getStorLongitudeLatitude().split(",");
            }else {
                split = item.getLongitudeLatitude().split(",");
            }
            if(!TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1])) {
                LocationUtils.getInstance().showMapPop(mContext,split[0],split[1]);
            }else {
                ToastUtils.show(mContext,"经纬度不存在");
            }

        });
        helper.getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnCancelClickListener != null) {
                    mOnCancelClickListener.onCancelItemClicked(item);
                }
            }
        });
        helper.getView(R.id.tv_call_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnCallPhoneClickListener != null) {
                    mOnCallPhoneClickListener.onItemCallPhone(item.getPhone());
                }
            }
        });
       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               if(mOnSeekBarChangeListener != null && i == 100) {
                   mOnSeekBarChangeListener.onProgressChanged(item);
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
           }
       });
    }

    public void seekBarProgress() {
        seekBar.setProgress(0);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    public interface OnSeekBarChangeListener {
       void onProgressChanged(Order order);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mOnSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = mOnSeekBarChangeListener;
    }

    private OnCancelClickListener mOnCancelClickListener;

    public interface OnCancelClickListener {
        void onCancelItemClicked(Order order);
    }

    public void setOnCancelClickListener(OnCancelClickListener mOnCancelClickListener) {
        this.mOnCancelClickListener = mOnCancelClickListener;
    }

    private OnCallPhoneClickListener mOnCallPhoneClickListener;

    public interface OnCallPhoneClickListener {
        void onItemCallPhone(String phone);
    }

    public void setOnCallPhoneClickListener(OnCallPhoneClickListener mOnCallPhoneClickListener) {
        this.mOnCallPhoneClickListener = mOnCallPhoneClickListener;
    }
}
