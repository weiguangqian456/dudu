package com.weiwei.salemall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.hyphenate.helpdesk.easeui.event.ShoppingChangeEvent;
import com.weiwei.base.application.VsApplication;
import com.weiwei.salemall.activity.GoodsDetailActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;
import com.weiwei.salemall.utils.JudgeImageUrlUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by EDZ on 2018/5/23.
 * 购物车管理  RecycleViewAdapter
 */

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.MyViewHolder> implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    protected List<ShoppingItemsBean> mData;
    private CheckItemListener mCheckListener;
    /**
     * 选中的item
     */
    private List<ShoppingItemsBean> mSelectedList;
    /**
     * 记录哪一个item被选中，哪一个item没选中
     */
    private SparseBooleanArray mSelectedFlags;

    private int shopNumber = 1;
    private int minValue = 1;

    private ShoppingItemsBeanDao shoppingItemsBeanDao;

    private static final String action = "com.duduhx.shoppingcartNum";

    public ShoppingCartAdapter(Context context, List<ShoppingItemsBean> data, CheckItemListener mCheckListener) {
        this.mContext = context;
        this.mData = data;
        this.mCheckListener = mCheckListener;
        mSelectedList = new ArrayList<>();
        mSelectedFlags = new SparseBooleanArray();
        initDao();
    }

    private void initDao() {
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_shoppingcart_item, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int pos) {
        final ShoppingItemsBean shoppingItemsBean = mData.get(pos);

        final String image = JudgeImageUrlUtils.isAvailable(shoppingItemsBean.getImage());

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.mall_logits_default);
        Glide.with(mContext).load(TextUtils.isEmpty(shoppingItemsBean.getImgaeUrl()) ? image : shoppingItemsBean.getImgaeUrl()).apply(options).into(myViewHolder.shopImage);

        final String shopname = shoppingItemsBean.getName();
        if (!StringUtils.isEmpty(shopname)) {
            myViewHolder.shopName.setText(shopname);
        }

        final String shopdesc = shoppingItemsBean.getDesc();
        String deliveryMsg = shoppingItemsBean.getDeliveryMsg();

        if (!StringUtils.isEmpty(deliveryMsg)) {
            myViewHolder.economicalMoney.setText(deliveryMsg);
        }

        if (!StringUtils.isEmpty(shopdesc)) {
            myViewHolder.shopDesc.setText((shopdesc.substring(0, shopdesc.length() - 1)));
        }

        final String shopMallPrice = shoppingItemsBean.getMallPrice();
        if (!StringUtils.isEmpty(shopMallPrice)) {
            myViewHolder.mallprice.setText("¥" + shopMallPrice);
        }

        String shopJdPrice = shoppingItemsBean.getJdPrice();
        myViewHolder.jdPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (!StringUtils.isEmpty(shopJdPrice)) {
            myViewHolder.jdPrice.setText("￥" + shopJdPrice);
        }

        final String productNo = shoppingItemsBean.getProductNo();
        final String columnId = shoppingItemsBean.getColumnId();
        if (!StringUtils.isEmpty(productNo)) {
            myViewHolder.containerRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] key = new String[]{"productNo", "productImage", "productName", "productPrice", "columnId"};
                    String[] value = new String[]{productNo, image, shopname, shopMallPrice, columnId};
                    SkipPageUtils.getInstance(mContext).skipPage(GoodsDetailActivity.class, key, value);
                }
            });
        }

        shopNumber = shoppingItemsBean.getNum();
        if (shopNumber != 0) {
            myViewHolder.shopNum.setText(shopNumber + "");
//            String shopEconomicalMoney = DealEcnomicalMoneyUtils.get(shopJdPrice, shopMallPrice, 1);
//            if (!StringUtils.isEmpty(shopEconomicalMoney)) {
////                myViewHolder.economicalMoney.setText("省:¥" + shopEconomicalMoney);
//            }
        }

        //按钮减点击事件
        myViewHolder.subShopNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = mData.get(pos).getNum();
                Long id = mData.get(pos).get_id();

                //物流相关
                String deliveryType = mData.get(pos).getDeliveryType();
                int addNum = mData.get(pos).getAddNum();

                if (!StringUtils.isEmpty(deliveryType) && deliveryType.equals("logistics")) {   //物流

                    num -= addNum;
                    shoppingItemsBean.setNum(num);
                    myViewHolder.shopNum.setText(num + "");
                    shoppingItemsBeanDao.update(shoppingItemsBean);

                    if (num < 1) {
                        mSelectedList.remove(mData.get(pos));
                        mData.remove(pos);
                        /**===============删除某一项至0时===============**/
                        mSelectedFlags.put(pos, false);
                        deleteShoppingData(id);
                        notifyDataSetChanged();
                    }
                } else {
                    if (num > minValue) {           //大于最小值
                        num--;
                        shoppingItemsBean.setNum(num);
                        myViewHolder.shopNum.setText(num + "");
                        shoppingItemsBeanDao.update(shoppingItemsBean);
                    } else {
                        mSelectedList.remove(mData.get(pos));
                        mData.remove(pos);
                        /**===============删除某一项至0时===============**/
                        mSelectedFlags.put(pos, false);
                        deleteShoppingData(id);
                        notifyDataSetChanged();
                        EventBus.getDefault().post(new ShoppingChangeEvent());
                    }
                }

                //发送数量广播
                sendShoppingCartNumBroadcastReceiver(getTotalNum());

                //发送当前商品数量
                MessageEvent event = new MessageEvent();
                event.setMessage(num + "");
                if (num != 0) {
                    EventBus.getDefault().post(event);
                }

                if (mData == null || mData.size() == 0) {    //购物车数据为空
                    MessageEvent event0 = new MessageEvent();
                    event0.setMessage("0");
                    EventBus.getDefault().post(event0);
                }
            }
        });

        //按钮+点击事件
        myViewHolder.addShopNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = mData.get(pos).getNum();

                //物流相关
                String deliveryType = mData.get(pos).getDeliveryType();

                int addNum = mData.get(pos).getAddNum();
                if (!StringUtils.isEmpty(deliveryType) && deliveryType.equals("logistics")) {   //物流
                    num += addNum;
                } else {
                    num++;
                }
                shoppingItemsBean.setNum(num);
                myViewHolder.shopNum.setText(num + "");
                shoppingItemsBeanDao.update(shoppingItemsBean);

                MessageEvent event = new MessageEvent();
                event.setMessage(num + "");
                EventBus.getDefault().post(event);

                //发送数量广播
                sendShoppingCartNumBroadcastReceiver(getTotalNum());
            }
        });

        myViewHolder.cbIsSelected.setOnCheckedChangeListener(null);
        myViewHolder.cbIsSelected.setTag(pos);
        myViewHolder.cbIsSelected.setOnCheckedChangeListener(this);

        //界面更新的关键是这个地方
        if (mSelectedFlags.get(pos)) {
            myViewHolder.cbIsSelected.setChecked(true);
        } else {
            myViewHolder.cbIsSelected.setChecked(false);
        }
    }

    /**
     * 删除指定id数据
     *
     * @param id
     */
    private void deleteShoppingData(Long id) {
        shoppingItemsBeanDao.deleteByKey(id);
    }

    /**
     * 发送数量广播
     *
     * @param cartTotalNum
     */
    private void sendShoppingCartNumBroadcastReceiver(int cartTotalNum) {
        Intent intent = new Intent(action);
        intent.putExtra("cartNumber", cartTotalNum);
        VsApplication.getContext().sendBroadcast(intent);
    }

    /**
     * 遍历本地数据库
     */
    private int getTotalNum() {
        int num = 0;
        List<ShoppingItemsBean> beanList = shoppingItemsBeanDao.loadAll();
        for (Iterator iterators = beanList.iterator(); iterators.hasNext(); ) {
            ShoppingItemsBean bean = (ShoppingItemsBean) iterators.next();
            int shopNum = bean.getNum();
            num = num + shopNum;
        }
        return num;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * adapter 中选择框的选择事件
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // setChecked()的时候也会回调，屏蔽掉这种情况，只有用户点击的时候才进行操作
        if (!buttonView.isPressed()) {
            return;
        }
        int position = (Integer) buttonView.getTag();
        if (isChecked) {     //选中
            mSelectedFlags.put(position, true);
            mSelectedList.add(mData.get(position));
        } else {             //取消选中
            mSelectedFlags.put(position, false);
            mSelectedList.remove(mData.get(position));
        }
        if (mCheckListener != null) {
            mCheckListener.itemChecked(position, isChecked);
        }
    }

    /**
     * 获取被选择的item
     *
     * @return
     */
    public List<ShoppingItemsBean> getSelectedList() {
        return mSelectedList;
    }

    public void clearSelectedList() {
        if (mSelectedList != null) {
            mSelectedList.clear();
        }
    }

    /**
     * 全选
     */
    public void selectAll() {
        mSelectedList.clear();
        mSelectedList.addAll(mData);
        for (int i = 0; i < getItemCount(); i++) {
            mSelectedFlags.put(i, true);
        }
        //更新UI，onBindViewHolder会被回调
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void cancelSelectAll() {
        mSelectedList.clear();
        for (int i = 0; i < getItemCount(); i++) {
            mSelectedFlags.put(i, false);
        }
        //更新UI，onBindViewHolder会被回调
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbIsSelected;
        ImageView shopImage;
        TextView shopName;
        TextView economicalMoney;
        TextView shopDesc;
        TextView mallprice;
        TextView jdPrice;
        ImageView subShopNum;
        TextView shopNum;
        ImageView addShopNum;
        RelativeLayout contentRl;
        RelativeLayout containerRl;

        public MyViewHolder(View view) {
            super(view);
            contentRl = (RelativeLayout) view.findViewById(R.id.ll_view);
            containerRl = (RelativeLayout) view.findViewById(R.id.rl_content);
            cbIsSelected = (CheckBox) view.findViewById(R.id.cb_buy);
            shopImage = (ImageView) itemView.findViewById(R.id.iv_shop);
            shopName = (TextView) itemView.findViewById(R.id.tv_shopName);
            economicalMoney = (TextView) itemView.findViewById(R.id.tv_economical);
            shopDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            mallprice = (TextView) itemView.findViewById(R.id.mallPrice);
            jdPrice = (TextView) itemView.findViewById(R.id.jdPrice);
            subShopNum = (ImageView) itemView.findViewById(R.id.iv_sub);
            shopNum = (TextView) itemView.findViewById(R.id.tv_shop_num);
            addShopNum = (ImageView) itemView.findViewById(R.id.iv_add);
        }
    }

    public interface CheckItemListener {
        void itemChecked(int position, boolean isChecked);
    }
}

