package com.weiwei.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.home.entity.SeckillTab;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Created by EDZ on 2018/5/23.
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyViewHolder> {

    private Context context;
    private List<ConsumptionRecordsEntity> recordsBeanList;
    public RecordsAdapter(Context context, List<ConsumptionRecordsEntity> recordsBeanList) {
        this.context = context;
        this.recordsBeanList = recordsBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumption, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ConsumptionRecordsEntity entity = recordsBeanList.get(position);
        String remark = entity.getRemark();
        String amount = entity.getAmount();
        String time = entity.getTime();
        String ctime = entity.getCtime();
        String status = entity.getStatus();

        if(!TextUtils.isEmpty(remark) && remark.contains("用户充值")){
            int index = remark.indexOf("用户充值");
            remark = remark.substring(0, index + 4);
        }
        holder.remark.setText(remark);

        if(!StringUtils.isEmpty(time)){         //加油余额明细
            holder.time.setText(time);
            if (amount != null&&amount.length() != 0) {
//                amount = ""+Double.parseDouble(amount)/10000;
                BigDecimal bg = new BigDecimal(Double.parseDouble(amount)/10000);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.amount.setText(f1 + "");
            }
        }else {                                 //红包成长金明细
            holder.time.setText(ctime);
            holder.amount.setText(amount);
        }

        if (!StringUtils.isEmpty(status)) {
            switch (status) {
                case "0":
                    holder.status.setText("处理中");
                    break;
                case "1":
                    holder.status.setText("成功");
                    break;
                default:
                    holder.status.setText(status);
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return recordsBeanList == null ? 0 : recordsBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView remark;
        private TextView amount;
        private TextView time;
        private TextView status;
        private RelativeLayout bottomRl;

        public MyViewHolder(View itemView) {
            super(itemView);
            remark = (TextView) itemView.findViewById(R.id.tv_name);
            amount = (TextView) itemView.findViewById(R.id.tv_amount);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            bottomRl = (RelativeLayout) itemView.findViewById(R.id.rl_bottom);
        }
    }
}
