package com.weiwei.salemall.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.activity.AddressAddActivity;
import com.weiwei.salemall.activity.AddressListActivity;
import com.weiwei.salemall.bean.AddressEntity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.widget.SDAdaptiveTextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

/**
 * @author Created by EDZ on 2018/5/23.
 *         地址管理
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    private List<AddressEntity> recordsBeanList;
    private String select;
    private static final String TAG = "AddressAdapter";

    private AddressAdapter.OnItemClickListener mOnItemClickListener;
    private AddressAdapter.OnItemCheckedListener onItemCheckedListener;

    public AddressAdapter(AddressListActivity context, List<AddressEntity> recordsBeanList, String select) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
        this.select = select;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_address_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //强制关闭复用
        holder.setIsRecyclable(false);
        final AddressEntity entity = recordsBeanList.get(position);
        String town = entity.getTown();
        holder.tv_name.setText(entity.getName());
        holder.tv_phone.setText(entity.getPhone());
        String addressDetail = "";
        if (!StringUtils.isEmpty(town)) {
            addressDetail = entity.getProvince() + entity.getCity() + entity.getArea() + entity.getTown() + entity.getAddress();
        } else {
            addressDetail = entity.getProvince() + entity.getCity() + entity.getArea() + entity.getAddress();
        }
        final String finalAddressDetail = addressDetail;
        holder.tv_address.post(new Runnable() {
            @Override
            public void run() {
                holder.tv_address.setAdaptiveText(finalAddressDetail);
            }
        });

        if (entity.getIsDefault() == 1) {
            holder.cb_address_default.setChecked(true);
            holder.tv_default.setTextColor(context.getResources().getColor(R.color.orange));
        } else {
            holder.cb_address_default.setChecked(false);
            holder.tv_default.setTextColor(context.getResources().getColor(R.color.nodeColor));
        }

        //删除
        holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).
                        setMessage("确定要删除此地址吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAddress(entity.getId());
                        recordsBeanList.remove(position);
                        notifyDataSetChanged();
                        if (recordsBeanList == null || recordsBeanList.size() == 0) {     //地址数据为空
                            MessageEvent event = new MessageEvent();
                            event.setMessage("0");
                            EventBus.getDefault().post(event);
                        }
                    }
                }).setNegativeButton("再看看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });

        //编辑
        holder.ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddressAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", entity.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.ll_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select != null && select.equals("1")) {
                    Intent intent = new Intent();
                    //传回名字、电话、详细地址
                    intent.putExtra("name", entity.getName());
                    intent.putExtra("phone", entity.getPhone());
                    String detailAddress = entity.getProvince() + entity.getCity() + entity.getArea() + entity.getAddress();
                    intent.putExtra("detaiaddress", detailAddress);
                    intent.putExtra("addressId", entity.getId());
                    ((AddressListActivity) context).setResult(RESULT_OK, intent);
                    ((AddressListActivity) context).finish();
                }
            }
        });


        holder.cb_address_default.setTag(position);
        holder.cb_address_default.setOnClickListener(this);

        holder.cb_address_default.setTag(position);
        holder.cb_address_default.setOnCheckedChangeListener(this);
    }


    /**
     * 删除地址
     *
     * @param id
     */
    private void deleteAddress(String id) {
        ApiService api = RetrofitClient.getInstance(context).Api();
        retrofit2.Call<ResultEntity> call = api.deleteDelivery(id);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Log.e(TAG, "删除地址msg===>" + result.getMsg());
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_phone;
        private SDAdaptiveTextView tv_address;
        private LinearLayout ll_edit;
        private LinearLayout ll_delete;
        private CheckBox cb_address_default;
        private LinearLayout ll_view;
        private TextView tv_default;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_address = (SDAdaptiveTextView) itemView.findViewById(R.id.tv_address_detail);
            ll_edit = (LinearLayout) itemView.findViewById(R.id.ll_edit);
            ll_delete = (LinearLayout) itemView.findViewById(R.id.ll_delete);
            cb_address_default = (CheckBox) itemView.findViewById(R.id.cb_address_default);
            ll_view = (LinearLayout) itemView.findViewById(R.id.ll_view);
            tv_default = (TextView) itemView.findViewById(R.id.tv_default);
        }
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    //点击事件的接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(AddressAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (onItemCheckedListener != null) {
                //注意这里使用getTag方法获取position
                onItemCheckedListener.onItemChecked(buttonView, (Integer) buttonView.getTag());
            }
        } else {
            if (onItemCheckedListener != null) {
                //注意这里使用getTag方法获取position
                onItemCheckedListener.onItemNoChecked(buttonView, (Integer) buttonView.getTag());
            }
        }
    }

    public interface OnItemCheckedListener {
        void onItemChecked(View view, int position);

        void onItemNoChecked(View view, int position);
    }

    public void setOnItemChenedListener(AddressAdapter.OnItemCheckedListener listener) {
        this.onItemCheckedListener = listener;
    }
}
