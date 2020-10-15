package com.weiwei.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import java.util.ArrayList;
import java.util.List;

/**
 * 万能的RecyclerView适配器
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {

    protected OnItemClickListener<T> listener;
    private Context context;//上下文
    private List<T> list;//数据源
    private LayoutInflater inflater;//布局器
    private int itemLayoutId;//布局id
    private boolean isScrolling;//是否在滚动
    //    private OnItemClickListener listener;//点击事件监听器
//    private OnItemLongClickListener longClickListener;//长按监听器
    private RecyclerView recyclerView;
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public BaseRecyclerAdapter(Context context, List<T> list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);

        //        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //                super.onScrollStateChanged(recyclerView, newState);
        //                isScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
        //                if (!isScrolling) {
        //                    notifyDataSetChanged();
        //                }
        //            }
        //        });
    }

    //在RecyclerView提供数据的时候调用
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    /**
     * 定义一个点击事件接口回调
     */
//    public interface OnItemClickListener {
//        void onItemClick(RecyclerView parent, T data, View view, int position);
//    }
//
//    public interface OnItemLongClickListener {
//        boolean onItemLongClick(RecyclerView parent, T data, View view, int position);
//    }

    /**
     * 插入一项
     *
     * @param item
     * @param position
     */
    public void insert(T item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * 删除一项
     *
     * @param position 删除位置
     */
    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 修改一项
     *
     * @param position 删除位置
     */
    public void change(T item, int position) {
        list.set(position, item);
        notifyItemInserted(position);
    }


    /**
     * 获取某一项的数据
     *
     * @param position
     * @return
     */
    public T getitem(int position) {
        return list.get(position);
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayoutId, parent, false);

        return BaseRecyclerHolder.getRecyclerHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerHolder holder, final int position) {
        final BaseRecyclerHolder baseRecyclerHolder = holder;

        if (listener != null) {
            //      holder.itemView.setBackgroundResource(R.drawable.recycler_bg);//设置背景
        }
// /       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (longClickListener != null && view != null && recyclerView != null) {
//                    int position = recyclerView.getChildAdapterPosition(view);
//                    longClickListener.onItemLongClick(recyclerView, list.get(position), view, position);
//                    return true;
//                }
//                return false;
//            }
//        });
        convert(holder, list.get(position), position, isScrolling, selectedPosition);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && view != null && recyclerView != null) {
//                    int position = recyclerView.getChildAdapterPosition(view);
                    listener.onItemClick(baseRecyclerHolder, list.get(position), position);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(BaseRecyclerHolder viewHolder, T data, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }
//    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
//        mItemClickListener = itemClickListener;
//    }

//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
//        this.longClickListener = longClickListener;
//    }

    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder      ViewHolder
     * @param data        子项
     * @param position    位置
     * @param isScrolling 是否在滑动
     */
    public abstract void convert(BaseRecyclerHolder holder, T data, int position, boolean isScrolling, int selectedPosition);

    public void setData(List<T> data) {
        if (null == this.list) {
            this.list = new ArrayList<>();
        }
        list.addAll(data);
        this.notifyDataSetChanged();
    }


    public void clear() {
        if (null != list)
            list.clear();
        this.notifyDataSetChanged();
    }

    public void setNewData(List<T> data) {
        clear();
        setData(data);
    }
}