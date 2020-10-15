package com.weiwei.home.dialog;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.hwtx.dududh.R;
import com.weiwei.base.area.ArrayWheelAdapter;
import com.weiwei.base.area.CityModel;
import com.weiwei.base.area.DistrictModel;
import com.weiwei.base.area.OnWheelChangedListener;
import com.weiwei.base.area.ProvinceModel;
import com.weiwei.base.area.TownModel;
import com.weiwei.base.area.WheelView;
import com.weiwei.base.area.XmlParserHandler;
import com.weiwei.home.utils.LogUtils;

import net.sf.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseAreaDialog extends BottomBaseDialog<ChooseAreaDialog> implements OnWheelChangedListener {

    @BindView(R.id.wv_province)
    WheelView mWvProvince;
    @BindView(R.id.wv_city)
    WheelView mWvCity;
    @BindView(R.id.wv_district)
    WheelView mWvDistrict;
    @BindView(R.id.wv_town)
    WheelView mWvTown;

    private String[] mProvinceDatas;
    private Map<String, String[]> mCityDataMaps = new HashMap<String, String[]>();
    private Map<String, String[]> mDistrictDataMaps = new HashMap<String, String[]>();
    private Map<String, String[]> mTownDataMaps = new HashMap<String, String[]>();

    public String mCurrentProvinceName = "";
    public String mCurrentCityName = "";
    public String mCurrentDistrictName = "";
    public String mCurrentTownName = "";

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public String getAllArea() {
        return mCurrentProvinceName + mCurrentCityName + mCurrentDistrictName + mCurrentTownName;
    }

    public ChooseAreaDialog(Context context) {
        super(context);
    }


    @Override
    public View onCreateView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose_area, null);
        ButterKnife.bind(this, view);

        mWvProvince.addChangingListener(this);
        mWvCity.addChangingListener(this);
        mWvDistrict.addChangingListener(this);
        mWvTown.addChangingListener(this);

        initProvinceDatas();

        return view;
    }

    @Override
    public void setUiBeforShow() {
    }

    /**
     * 加载assest文件夹中地址的数据（解析xml文件）
     */
    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();

            if (provinceList == null || provinceList.size() == 0) {
                return;
            }

            //省
            mProvinceDatas = new String[provinceList.size()];
            mWvProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
            mWvProvince.setCurrentItem(0);

            //遍历省份
            for (int i = 0; i < provinceList.size(); i++) {
                //当前省份名
                mProvinceDatas[i] = provinceList.get(i).getName();
                //当前省份下的所有市
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];

                //遍历市
                for (int j = 0; j < cityList.size(); j++) {
                    //当前市名
                    cityNames[j] = cityList.get(j).getName();
                    //当前市下的所有县
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctName = new String[districtList.size()];

                    //遍历县
                    for (int k = 0; k < districtList.size(); k++) {
                        //当前县名
                        distrinctName[k] = districtList.get(k).getName();
                        //当前县下的所有镇
                        List<TownModel> townList = districtList.get(k).getTownList();
                        String[] townName = new String[townList.size()];

                        //遍历镇
                        for (int l = 0; l < townList.size(); l++) {
                            townName[l] = townList.get(l).getName();
                        }
                        mTownDataMaps.put(distrinctName[k], townName);
                    }
                    mDistrictDataMaps.put(cityNames[j], distrinctName);
                }
                mCityDataMaps.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
        updateCities();
    }

    private void updateCities() {
        //得到当前省坐标
        int provinceCurrent = mWvProvince.getCurrentItem();
        //得到当前省的名称
        mCurrentProvinceName = mProvinceDatas[provinceCurrent];
        //根据当前省的名称得到下面的市
        String[] citys = mCityDataMaps.get(mCurrentProvinceName);
        if (citys == null || citys.length == 0) {
            citys = new String[]{""};
        }
//        LogUtils.e("++++++++++---------provinceCurrent-------->" + provinceCurrent + "<--------mCurrentProvinceName----->" + mCurrentProvinceName + "<--------cities----->" + JSON.toJSONString(citys));
        mWvCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, citys));
        mWvCity.setCurrentItem(0);
        updateDistrict();
    }

    private void updateDistrict() {
        //得到当前市的坐标
        int districtCurrent = mWvCity.getCurrentItem();
        //根据当前省名称得到当前市的名称
        mCurrentCityName = mCityDataMaps.get(mCurrentProvinceName)[districtCurrent];
        String[] areas = mDistrictDataMaps.get(mCurrentCityName);
        if (areas == null || areas.length == 0) {
            areas = new String[]{""};
        }
//        LogUtils.e("++++++++++---------districtCurrent-------->" + districtCurrent + "<--------mCurrentCityName----->" + mCurrentCityName + "<--------cities----->" + JSON.toJSONString(areas));
        mWvDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        mWvDistrict.setCurrentItem(0);
        updateTowns();
    }

    private void updateTowns() {
        int townsCurrent = mWvDistrict.getCurrentItem();
        mCurrentDistrictName = mDistrictDataMaps.get(mCurrentCityName)[townsCurrent];
        String[] towns = mTownDataMaps.get(mCurrentDistrictName);
        if (towns == null || towns.length == 0) {
            mCurrentTownName = "";
            towns = new String[]{""};
        } else {
            mCurrentTownName = towns[0];
        }
//        LogUtils.e("++++++++++---------townsCurrent-------->" + townsCurrent + "<--------mCurrentDistrictName----->" + mCurrentDistrictName + "<--------cities----->" + JSON.toJSONString(towns));
//        LogUtils.e("++++++++++---------mCurrentTownName-------->" + mCurrentTownName);
        mWvTown.setViewAdapter(new ArrayWheelAdapter<String>(mContext, towns));
        mWvTown.setCurrentItem(0);
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mWvProvince) {   //省
            updateCities();
        } else if (wheel == mWvCity) {  //市
            updateDistrict();
        } else if (wheel == mWvDistrict) {  //县
            updateTowns();
        } else if (wheel == mWvTown) {     //区镇
            String[] towns = mTownDataMaps.get(mCurrentDistrictName);
            if (towns == null || towns.length == 0) {
                mCurrentTownName = "";
                return;
            }
            mCurrentTownName = towns[newValue];
//            LogUtils.e("++++++++++---mWvTown------mCurrentTown-------->" + mCurrentTownName + "<--------towns----->" + JSONObject.fromObject(towns));
//            LogUtils.e("++++++++++---mWvTown------mCurrentTownName-------->" + mCurrentTownName + "<--------towns----->" + JSON.toJSONString(towns));
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    public interface OnClickListener {
        void onClick(View view);
    }

}

