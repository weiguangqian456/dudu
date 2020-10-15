package com.weiwei.salemall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.PropertyBean;
import com.weiwei.salemall.widget.CustomRadioGroup;

import java.util.List;

/**
 * @author Created by EDZ on 2018/5/23.
 *         商品属性  Adapter
 */

public class GoodsPropertyAdapter extends RecyclerView.Adapter<GoodsPropertyAdapter.MyViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    private List<PropertyBean> dataList;
    private OnItemClickListener mOnItemClickListener;
    private OnRadioBtnCheckedListener mOnRadioBtnCheckedListener;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int propertype;
    private int checkId;

    public interface OnRadioBtnCheckedListener {
        void OnRadioBtnChecked(int propertype, int tag);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public GoodsPropertyAdapter(Context context, List<PropertyBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_goods_property_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_propername.setText(dataList.get(position).getPropertyName());
        setSpacing(holder.radioGroup, 12, 8);
        for (int i = 0; i < dataList.get(position).getPropertyValues().size(); i++) {
            radioButton = new RadioButton(context);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, convertDpToPixel(25));
            radioButton.setLayoutParams(layoutParams);
            radioButton.setPadding(convertDpToPixel(20), convertDpToPixel(10), convertDpToPixel(20), convertDpToPixel(10));
            radioButton.setText(dataList.get(position).getPropertyValues().get(i).getPropertyValueName());
            radioButton.setTextSize(10);
            radioButton.setBackgroundResource(R.drawable.radiobtn_selector);
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setGravity(Gravity.CENTER);

            Resources resources = context.getResources();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                radioButton.setTextColor(resources.getColorStateList(R.color.radiobutton_color_selector, context.getTheme()));
            } else {
                radioButton.setTextColor(resources.getColorStateList(R.color.radiobutton_color_selector));
            }

            radioButton.setOnCheckedChangeListener(this);
            holder.radioGroup.addView(radioButton, i);

            propertype = position;
            checkId = i;
            radioButton.setTag(R.id.tag_first, propertype);
            radioButton.setTag(R.id.tag_second, checkId);
        }
        holder.radioGroup.setOrientation(LinearLayout.HORIZONTAL);
    }

    private void setSpacing(CustomRadioGroup cg, int widthdp, int heightdp) {
        cg.setHorizontalSpacing(widthdp);
        cg.setVerticalSpacing(heightdp);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (mOnRadioBtnCheckedListener != null) {
                mOnRadioBtnCheckedListener.OnRadioBtnChecked((Integer) buttonView.getTag(R.id.tag_first), (Integer) buttonView.getTag(R.id.tag_second));
            }
        }
    }

    public void setOnRadioBtnCheckedListener(OnRadioBtnCheckedListener listener) {
        this.mOnRadioBtnCheckedListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_propername;
        private CustomRadioGroup radioGroup;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_propername = (TextView) itemView.findViewById(R.id.tv_propertyname);
            radioGroup = (CustomRadioGroup) itemView.findViewById(R.id.rg_property);
        }
    }
}
