package com.weiwei.salemall.citypicker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.salemall.bean.CitiesBean;
import com.weiwei.salemall.bean.CitiesDataBean;
import com.weiwei.salemall.citypicker.adapter.CityListAdapter;
import com.weiwei.salemall.citypicker.adapter.InnerListener;
import com.weiwei.salemall.citypicker.adapter.OnPickListener;
import com.weiwei.salemall.citypicker.adapter.decoration.DividerItemDecoration;
import com.weiwei.salemall.citypicker.adapter.decoration.SectionItemDecoration;
import com.weiwei.salemall.citypicker.model.City;
import com.weiwei.salemall.citypicker.model.HotCity;
import com.weiwei.salemall.citypicker.model.LocateState;
import com.weiwei.salemall.citypicker.model.LocatedCity;
import com.weiwei.salemall.citypicker.util.GpsUtil;
import com.weiwei.salemall.citypicker.util.PinyinTool;
import com.weiwei.salemall.citypicker.view.SideIndexBar;
import com.weiwei.salemall.utils.PreferencesUtils;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Created by EDZ on 2018/7/23
 *         地址选择
 */
public class CityPickerDialogFragment extends AppCompatDialogFragment implements TextWatcher, View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private EditText mSearchBox;
    private TextView mCancelBtn;
    private ImageView mClearAllBtn;
    private ImageView backIv;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<City> mResults;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnPickListener mOnPickListener;

    private GpsUtil gpsUtils;

    /**
     * 当前定位城市
     */
    private String localCity = null;

    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogFragment newInstance(boolean enable) {
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CityPickerStyle);

        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }

        initHotCities();
        initLocatedCity();

        mAllCities = initAllCities();
        mAllCities.add(0, mLocatedCity);
        mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
        mResults = mAllCities;
    }

    /**
     * 获取全部城市
     */
    private List<City> initAllCities() {
        mResults = new ArrayList<>();
        String citiesData = PreferencesUtils.getString(getContext(), "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        PinyinTool tool = new PinyinTool();
        for (int i = 0; i < allCitiesList.size(); i++) {
            City city = new City();
            city.setName(allCitiesList.get(i).getCityName());
            city.setProvince(allCitiesList.get(i).getProvinceCode());
            try {
                city.setPinyin(tool.toPinYin(allCitiesList.get(i).getCityName(), "", PinyinTool.Type.LOWERCASE));
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            city.setCode(String.valueOf(allCitiesList.get(i).getId()));
            mResults.add(city);
        }
        Collections.sort(mResults, new CityComparator());
        return mResults;
    }

    private void initLocatedCity() {
        //开始定位
        //gpsUtils = new GpsUtils(getActivity());
        localCity = gpsUtils.getLocalCity();
        String citiesData = PreferencesUtils.getString(getContext(), "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        if (localCity != null && !localCity.isEmpty()) {
            for (int i = 0; i < allCitiesList.size(); i++) {
                if (allCitiesList.get(i).getCityName().contains(localCity)) {
                    mLocatedCity = new LocatedCity(localCity, allCitiesList.get(i).getProvinceCode(), String.valueOf(allCitiesList.get(i).getId()));
                }
            }
            locateState = LocateState.SUCCESS;
        } else {
            mLocatedCity = new LocatedCity("正在定位…", "未知", "0");
            locateState = LocateState.FAILURE;
        }
    }

    private void initHotCities() {
        mHotCities = new ArrayList<>();
        String citiesData = PreferencesUtils.getString(getContext(), "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> hotCitiesList = JSON.parseArray(citiesDataBean.getHotCity().toString(), CitiesBean.class);
        for (int i = 0; i < hotCitiesList.size(); i++) {
            mHotCities.add(new HotCity(hotCitiesList.get(i).getCityName(), hotCitiesList.get(i).getProvinceCode(), String.valueOf(hotCitiesList.get(i).getId())));
        }
    }

    public void setLocatedCity(LocatedCity location) {
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
    }

    public void setAnimationStyle(@StyleRes int style) {
        this.mAnimStyle = style <= 0 ? R.style.DefaultCityPickerAnimation : style;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()), 1);
        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = (TextView) mContentView.findViewById(R.id.cp_overlay);

        mIndexBar = (SideIndexBar) mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setOverlayTextView(mOverlayTextView).setOnIndexChangedListener(this);

        mSearchBox = (EditText) mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);

        mCancelBtn = (TextView) mContentView.findViewById(R.id.cp_cancel);
        mClearAllBtn = (ImageView) mContentView.findViewById(R.id.cp_clear_all);
        mCancelBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);

        backIv = (ImageView) mContentView.findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);

        return mContentView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
        return dialog;
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
            mResults = searchCity(keyword);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 按关键字搜索城市
     *
     * @param keyword
     * @return
     */
    private List<City> searchCity(String keyword) {
        List<City> result = new ArrayList<>();
        String citiesData = PreferencesUtils.getString(getContext(), "citiesData");
        CitiesDataBean citiesDataBean = JSON.parseObject(citiesData, CitiesDataBean.class);
        List<CitiesBean> allCitiesList = JSON.parseArray(citiesDataBean.getCities().toString(), CitiesBean.class);
        PinyinTool tool = new PinyinTool();
        String pinyin = null;
        for (int i = 0; i < allCitiesList.size(); i++) {
            if (allCitiesList.get(i).getCityName().contains(keyword)) {
                String name = allCitiesList.get(i).getCityName();
                String province = allCitiesList.get(i).getProvinceCode();
                try {
                    pinyin = tool.toPinYin(allCitiesList.get(i).getCityName(), "", PinyinTool.Type.LOWERCASE);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                String code = String.valueOf(allCitiesList.get(i).getId());
                City city = new City(name, province, pinyin, code);
                result.add(city);
            }
        }
        Collections.sort(result, new CityComparator());
        return result;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss(-1, null);
        } else if (id == R.id.cp_clear_all) {
            mSearchBox.setText("");
        } else if (id == R.id.iv_back) {
            dismiss();
        }
    }

    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    public void locationChanged(LocatedCity location, int state) {
        mAdapter.updateLocateState(location, state);
    }

    @Override
    public void dismiss(final int position, final City data) {
        dismiss();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }

    /**
     * sort by a-z
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }
}
