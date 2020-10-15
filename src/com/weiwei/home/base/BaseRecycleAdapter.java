package com.weiwei.home.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.home.Constant;
import com.weiwei.home.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author hc
 * @date 2019/2/27.
 * @description: 用作Recycle加载的Adapter
 */

public abstract class BaseRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    private int mFooterState;
    private List<Object> mList = new ArrayList<>();
    private View mHeadView;

    private boolean isShowFooter = true;

    public BaseRecycleAdapter(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 不做逻辑处理
     * @param mView 头布局
     */
    public void addHeadView(View mView){
        this.mHeadView = mView;
        notifyDataSetChanged();
    }

    public Boolean hasHeadView(){
        return mHeadView != null;
    }

    /**
     * 判断是否显示底部加载中状态
     * @return 自己设置是否显示 + 是否需要显示
     */
    private Boolean isShowFooter(){
        return mHeadView != null || mList.size() != 0;
    }

//    protected Boolean isOpenFooter(){
//        return true;
//    }

    /**
     * 获取RecyclerView.ViewHolder
     * @param parent ViewGroup
     * @param viewType int
     * @return ViewHolder
     */
    protected abstract RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * onCreateViewHolder
     * @param holder1 holder
     * @param position position
     */
    protected abstract void onBindHolder(@NonNull RecyclerView.ViewHolder holder1, int position);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Constant.RECYCLE_TYPE_FOOTER){
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        }else if(viewType == Constant.RECYCLE_TYPE_HEAD){
            return new HeadViewHolder(mHeadView);
        }else{
            return onCreateHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder){
            if(mFooterState == Constant.RECYCLE_FOOTER_GONE){
                ((FooterViewHolder)holder).ll_item.setVisibility(View.GONE);
            }
            Boolean isNowLoad = mFooterState == Constant.RECYCLE_FOOTER_LOAD;
            String str =  isNowLoad ? mContext.getString(R.string.more_load) :
                    (mFooterState == Constant.RECYCLE_FOOTER_OVER ? mContext.getString(R.string.more_period) : (mContext.getString(R.string.more_error)));
            ((FooterViewHolder)holder).mTvFooter.setText(str);
            ((FooterViewHolder)holder).progress_bar.setVisibility(isNowLoad ? View.VISIBLE : View.GONE);
        }else if(holder instanceof HeadViewHolder) {
            LogUtils.e("BaseRecycleAdapter","HeadViewHolder");
        }else {
            position -= mHeadView == null ? 0 : 1;
            onBindHolder(holder,position);
        }
    }

    /**
     * 获取recycle的item数量
     * @return 数量
     */
    @Override
    public int getItemCount() {
        return isShowFooter() ? getListCount() + 1 : getListCount();
    }

    /**
     * 获取所有的数据
     * @return 所有数据
     */
    public List<Object> getList(){
        return mList;
    }

    /**
     * 用于获取指定item的数据
     * @param position 指定item
     * @return 数据
     */
    public Object getItem(int position){
        return mList.get(position);
    }

    /**
     * 列表初始化
     */
    public void initList(){
        mList.clear();
        notifyDataSetChanged();
    }


    public void clearList(){
        mList.clear();
    }

    public void initList(List<Object> array){
        mList.clear();
        addList(array);
    }

    /**
     * 添加List
     * @param list 累加的List
     */
    public void addList(List<Object> list){
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 重置加载中状态
     * @param mFooterState 状态
     */
    public void initFooterState(int mFooterState){
        this.mFooterState = mFooterState;
        if(getItemCount() != 0){
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int i = isShowFooter() && position == getListCount() ? Constant.RECYCLE_TYPE_FOOTER : getOfItemType(position);
        if(mHeadView != null && position == 0){
            return Constant.RECYCLE_TYPE_HEAD;
        }
        return i;
    }

    protected int getOfItemType(int position){
        return Constant.RECYCLE_TYPE_ITEM;
    }

    protected int getListCount(){
       return mList.size() + (mHeadView == null ? 0 : 1);
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_footer)
        TextView mTvFooter;
        @BindView(R.id.progress_bar)
        ProgressBar progress_bar;
        @BindView(R.id.ll_item)
        LinearLayout ll_item;
        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder{
        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }
}
