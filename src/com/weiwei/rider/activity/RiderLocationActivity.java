package com.weiwei.rider.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hwtx.dududh.R;
import com.weiwei.rider.adapter.LocationAdapter;
import com.weiwei.rider.base.RiderBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RiderLocationActivity extends RiderBaseActivity implements OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener, AdapterView.OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bmapView)
    MapView bmapView;
    @BindView(R.id.img_location_point)
    ImageView img_location_point;
    @BindView(R.id.lv_location_position)
    ListView lv_location_position;
    @BindView(R.id.pb_location_load_bar)
    ProgressBar pb_location_load_bar;

    /**
     * 列表数据
     */
    private List<PoiInfo> datas;
    /**
     * 列表适配器
     */
    private LocationAdapter locatorAdapter;
    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;
    /**
     * 地理编码
     */
    private GeoCoder mSearch;
    /**
     * 定位
     */
    private LocationClient mLocClient;
    private MyLocationListener myLocationListener = new MyLocationListener();
    // MapView 中央对于的屏幕坐标
    private android.graphics.Point mCenterPoint = null;
    /**
     * 当前经纬度
     */
    private LatLng mLoactionLatLng;
    /**
     * 是否第一次定位
     */
    private boolean isFirstLoc = true;
    /**
     * 获取的位置
     */
    private String mLocationValue;
    /**
     * 请求码
     */
    private final static int REQUEST_CODE = 0x123;
    private boolean isTouch = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_location;
    }
    @Override
    protected void initView() {
        tvTitle.setText("我的定位");
        // 地图初始化
        mBaiduMap = bmapView.getMap();
        bmapView.removeViews(1,2);
        // 设置为普通矢量图地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bmapView.setPadding(10, 0, 0, 10);
        bmapView.showZoomControls(false);
        // 设置缩放比例(100米)
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);

        // 初始化当前 MapView 中心屏幕坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;

        // 地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 地图状态监听
        mBaiduMap.setOnMapStatusChangeListener(this);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 可定位
        mBaiduMap.setMyLocationEnabled(true);

        // 列表初始化
        datas = new ArrayList();
        locatorAdapter = new LocationAdapter(this, datas);
        lv_location_position.setAdapter(locatorAdapter);

        // 注册监听
        lv_location_position.setOnItemClickListener(this);


    }

    @Override
    protected void initData() {

    }


    @OnClick({R.id.rl_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        // 正向地理编码指的是由地址信息转换为坐标点的过程
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        // 获取反向地理编码结果
        PoiInfo mCurrentInfo = new PoiInfo();
        mCurrentInfo.address = result.getAddress();
        mCurrentInfo.location = result.getLocation();
        mCurrentInfo.name = result.getAddress();
        mLocationValue = result.getAddress();
        datas.clear();
        if (!TextUtils.isEmpty(mLocationValue)) {
            datas.add(mCurrentInfo);
        }
        if (result.getPoiList() != null && result.getPoiList().size() > 0) {
            datas.addAll(result.getPoiList());
        }
        locatorAdapter.notifyDataSetChanged();
        pb_location_load_bar.setVisibility(View.GONE);
    }
    /**
     * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
     *
     * @param mapStatus 地图状态改变开始时的地图状态
     */
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }
    /**
     * 地图状态变化中
     *
     * @param mapStatus 当前地图状态
     */
    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        if (isTouch) {
            datas.clear();
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(mapStatus.target));
            lv_location_position.setSelection(0);
            locatorAdapter.setSelectItemIndex(0);

        }
    }
    /**
     * 地图状态改变结束
     *
     * @param mapStatus 地图状态改变结束后的地图状态
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        isTouch = false;
        // 设置选中项下标，并刷新
        locatorAdapter.setSelectItemIndex(position);
        locatorAdapter.notifyDataSetChanged();

        mBaiduMap.clear();
        PoiInfo info = (PoiInfo) locatorAdapter.getItem(position);
        LatLng la = info.location;
        // 获取位置
        mLocationValue = info.name;

        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            locatorAdapter.setSelectItemIndex(0);

            // 获取经纬度
            LatLng latLng = data.getParcelableExtra("LatLng");

            // 实现动画跳转
            MapStatusUpdate u = MapStatusUpdateFactory
                    .newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                    .location(latLng));
        }
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || bmapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            Double mLatitude = location.getLatitude();
            Double mLongitude = location.getLongitude();

            LatLng currentLatLng = new LatLng(mLatitude, mLongitude);
            mLoactionLatLng = new LatLng(mLatitude, mLongitude);

            // 是否第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                // 实现动画跳转
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);

                mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
                return;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }
    // 地图触摸事件监听器
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            isTouch = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 显示列表，查找附近的地点
                searchPoi();
            }
        }
    };
    /**
     * 显示列表，查找附近的地点
     */
    public void searchPoi() {
        if (mCenterPoint == null) {
            return;
        }
        // 获取当前 MapView 中心屏幕坐标对应的地理坐标
        LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(
                mCenterPoint);
        // 发起反地理编码检索
        mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                .location(currentLatLng));
        pb_location_load_bar.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        bmapView.onPause();
    }
}