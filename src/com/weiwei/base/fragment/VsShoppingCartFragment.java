package com.weiwei.base.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.activity.OrderEnsureActivity;
import com.weiwei.salemall.adapter.ShoppingCartAdapter;
import com.weiwei.salemall.bean.DealCartPriceBean;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.bean.OrderEnsureEntity;
import com.weiwei.salemall.bean.ShoppingItemsBean;
import com.weiwei.salemall.db.GreenDaoManager;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.utils.DealEcnomicalMoneyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * @author Created by EDZ on 2018/08/17
 *         购物车Fragment
 */
public class VsShoppingCartFragment extends Fragment implements ShoppingCartAdapter.CheckItemListener {
    private View shoppingCartView;
    @BindView(R.id.rv_shoppingcart)
    RecyclerView shoppingcartRecycleView;
    @BindView(R.id.cart_num)
    TextView orderNum;
    @BindView(R.id.cart_money)
    TextView orderMoney;
    @BindView(R.id.btn_commit_order)
    Button btnCommitOrder;
    @BindView(R.id.cb_select_all)
    CheckBox selectAllCheckBox;
    @BindView(R.id.rl_cart_data)
    RelativeLayout cartDataRl;
    @BindView(R.id.ll_no_order_data)
    LinearLayout noCartDataLl;
    @BindView(R.id.btn_back_mall)
    TextView backMallBtn;

    private static final String TAG = "VsShoppingCartFragment";
    /**
     * 购物车列表适配器
     */
    private ShoppingCartAdapter shoppingCartAdapter;

    /**
     * 处理价格
     */
    private List<ShoppingItemsBean> beanList = new ArrayList<>();
    private ShoppingItemsBeanDao shoppingItemsBeanDao;

    /**
     * 订单确认
     */
    private List<OrderEnsureEntity> orderstrList = new ArrayList<>();

    /**
     * 计算价格需要参数
     */
    private List<DealCartPriceBean> dealCartPriceBeanList = new ArrayList<>();

    /**
     * 购物车结算到确定订单界面flag
     */
    private int orderEnsureActivityFlag = 1;

    /**
     * 选中后数据
     */
    public List<ShoppingItemsBean> checkedList = new ArrayList<>();
    private static final String NO_GOODS_FLAG = "0";

    /**
     * 主界面
     */
    private VsMainActivity vsMain;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.vsMain = (VsMainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shoppingCartView = inflater.inflate(R.layout.fragment_vs_shopping_cart, container, false);
        ButterKnife.bind(this, shoppingCartView);
        initView();
        initEventBus();
        return shoppingCartView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //取消全选

        if (shoppingCartAdapter != null) {
            shoppingCartAdapter.cancelSelectAll();
            shoppingCartAdapter.getSelectedList().clear();
        }
        checkedList.clear();
        selectAllCheckBox.setChecked(false);
        setViewData(getOrderDealPriceEntitys(checkedList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    public VsShoppingCartFragment() {
        // Required empty public constructor
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getDbData();
        }else{
            //界面不显示时  重置价格 - 临时解决方案
            selectAllCheckBox.setChecked(false);
            if(checkedList!=null){
                checkedList.clear();
                setViewData(getOrderDealPriceEntitys(checkedList));
            }
            if(shoppingCartAdapter != null){ shoppingCartAdapter.clearSelectedList(); }
        }
    }

    private void initView() {
        /**
         * 全选按钮的点击事件
         */
        selectAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // setChecked()的时候也会回调，屏蔽掉这种情况，只有用户点击的时候才进行操作
                if (!buttonView.isPressed()) {
                    return;
                }

                if (isChecked) {    //全选
                    checkedList.clear();
                    shoppingCartAdapter.selectAll();
                    checkedList.addAll(shoppingCartAdapter.getSelectedList());
                } else {    //取消全选
                    shoppingCartAdapter.cancelSelectAll();
                    checkedList.clear();
                }

                setViewData(getOrderDealPriceEntitys(checkedList));
            }
        });
    }

    /**
     * 从数据库中拿数据
     */
    public void getDbData() {
        shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();
        beanList = shoppingItemsBeanDao.loadAll();
        if (beanList != null && beanList.size() > 0) {
            cartDataRl.setVisibility(View.VISIBLE);
            noCartDataLl.setVisibility(View.GONE);
            setRecycleView();
        } else {
            cartDataRl.setVisibility(View.GONE);
            noCartDataLl.setVisibility(View.VISIBLE);
            backMallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vsMain.showFragment(0);
                    vsMain.setView(0, false);
                    vsMain.setFragmentIndicator(0);
                }
            });
        }
    }

    public void setRecycleView() {
        shoppingcartRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), beanList, this);
        shoppingcartRecycleView.setAdapter(shoppingCartAdapter);
    }

    @OnClick({R.id.btn_commit_order})
    void commitOrder() {
        dealCartPriceBeanList.clear();
        orderstrList.clear();

        /**========确定购物车订单=========**/
        orderstrList.addAll(getOrderDealPriceEntitys(shoppingCartAdapter.getSelectedList()));
        /**========计算购物车价格=========**/
        dealCartPriceBeanList.addAll(getDealPriceEntitys(shoppingCartAdapter.getSelectedList()));

        if (shoppingCartAdapter.getSelectedList().size() > 0) {
            Intent intent = new Intent(getActivity(), OrderEnsureActivity.class);
            intent.putExtra("orderEnsureActivityFlag", orderEnsureActivityFlag);
            Bundle bundle = new Bundle();

            /**========确定购物车订单=========**/
            bundle.putSerializable("arrayList", (Serializable) orderstrList);
            /**========计算购物车价格=========**/
            bundle.putSerializable("dealCartPriceBeanList", (Serializable) dealCartPriceBeanList);

            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "请选择商品进行结算", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ShoppingItemBean转换OrderEnsureEntity List
     * 配置订单确定需要参数
     *
     * @param beans
     * @return
     */
    public List<OrderEnsureEntity> getOrderDealPriceEntitys(List<ShoppingItemsBean> beans) {
        OrderEnsureEntity orderEnsureEntity;
        List<OrderEnsureEntity> ensureEntities = new ArrayList<>();
        for (ShoppingItemsBean bean : beans) {
            orderEnsureEntity = new OrderEnsureEntity();
            Long carId = bean.get_id();
            String strproductNo = bean.getProductNo();
            String strproperty = bean.getProperty();
            String strproperDesc = bean.getDesc();
            int intproductNum = bean.getNum();
            String strproductImage = bean.getImage();
            String strproductName = bean.getName();
            String strmallPrice = bean.getMallPrice();
            String strjdPrice = bean.getJdPrice();
            String columnId = bean.getColumnId();
            String conversionPrice = bean.getConversionPrice();
            String coupon = bean.getCoupon();
            String isExchange = bean.getIsExchange();
            String ecnomical = DealEcnomicalMoneyUtils.get(strjdPrice, strmallPrice, intproductNum);
            String deliveryType = bean.getDeliveryType();
            String deliveryMsg = bean.getDeliveryMsg();
            strproductImage = TextUtils.isEmpty(bean.getImgaeUrl()) ? strproductImage: bean.getImgaeUrl();
            //订单
            orderEnsureEntity.setId(carId);
            orderEnsureEntity.setColumnId(columnId);
            orderEnsureEntity.setProductNo(strproductNo);
            orderEnsureEntity.setProductPropery(strproperty);
            orderEnsureEntity.setProductNum(intproductNum);
            orderEnsureEntity.setProductImage(strproductImage);
            orderEnsureEntity.setProductName(strproductName);
            orderEnsureEntity.setProductMallPrice(strmallPrice);
            orderEnsureEntity.setProductJdPrice(strjdPrice);
            orderEnsureEntity.setProductEcnomicalMoney(ecnomical);
            orderEnsureEntity.setProductDesc(strproperDesc);
            orderEnsureEntity.setIsExchange(isExchange);
            orderEnsureEntity.setConversionPrice(conversionPrice);
            orderEnsureEntity.setCoupon(coupon);
            orderEnsureEntity.setDeliveryType(deliveryType);
            orderEnsureEntity.setDeliveryMsg(deliveryMsg);

            ensureEntities.add(orderEnsureEntity);
        }
        return ensureEntities;
    }

    /**
     * 计算价格  配置参数
     *
     * @param beans
     * @return
     */
    private List<DealCartPriceBean> getDealPriceEntitys(List<ShoppingItemsBean> beans) {
        DealCartPriceBean dealEntity;
        List<DealCartPriceBean> dealEntities = new ArrayList<>();
        for (ShoppingItemsBean bean : beans) {
            dealEntity = new DealCartPriceBean();
            String strproductNo = bean.getProductNo();
            String strproperty = bean.getProperty();
            int intproductNum = bean.getNum();
            String columnId = bean.getColumnId();
            String deliveryType = bean.getDeliveryType();

            dealEntity.setProductNo(strproductNo);
            dealEntity.setProperty(strproperty);
            dealEntity.setProductNum(intproductNum);
            dealEntity.setColumnId(columnId);
            dealEntity.setDeliveryType(deliveryType);
            dealEntities.add(dealEntity);
        }
        return dealEntities;
    }

    /**
     * 单选加减按钮的点击事件
     *
     * @param position
     * @param isChecked
     */
    @Override
    public void itemChecked(int position, boolean isChecked) {
        if (!isChecked) {  //未选中
            checkedList.remove(beanList.get(position));
        } else {           //选中
            checkedList.add(beanList.get(position));
        }

        if (shoppingCartAdapter.getSelectedList().size() == beanList.size()) {
            selectAllCheckBox.setChecked(true);
        } else {
            selectAllCheckBox.setChecked(false);
        }

        setViewData(getOrderDealPriceEntitys(checkedList));
    }

    /**
     * 配置界面显示价格
     *
     * @param orderDealPriceEntitys
     */
    public void setViewData(List<OrderEnsureEntity> orderDealPriceEntitys) {
        double totalMoney = 0;
        for (Iterator iterators = orderDealPriceEntitys.iterator(); iterators.hasNext(); ) {
            OrderEnsureEntity bean = (OrderEnsureEntity) iterators.next();
            int shopNum = bean.getProductNum();
            String mallPrice = bean.getProductMallPrice();
            double malPrice = Double.parseDouble(mallPrice);
            totalMoney += malPrice * shopNum;
        }
        btnCommitOrder.setText("结算（" + orderDealPriceEntitys.size() + ")");
        orderMoney.setText("¥" + String.format("%.2f", totalMoney));
    }

    /**
     * 订阅方法，当接收到事件的时候，会调用该方法
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        Log.e(TAG, "接收到eventBus msg===>" + messageEvent.getMessage());
        String eventBusMsg = messageEvent.getMessage();
        switch (eventBusMsg) {
            case NO_GOODS_FLAG:
                cartDataRl.setVisibility(View.GONE);
                noCartDataLl.setVisibility(View.VISIBLE);
                backMallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vsMain.showFragment(0);
                        vsMain.setView(0, false);
                        vsMain.setFragmentIndicator(0);
                    }
                });
                break;
            default:
                setViewData(getOrderDealPriceEntitys(shoppingCartAdapter.getSelectedList()));
                break;
        }
    }

    /**
     * 解绑
     */
    private void unRegisterEventBus() {
        EventBus.getDefault().unregister(this);
    }
}