package com.weiwei.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.base.BaseBarActivity;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.SkipPageUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * @author : hc
 * @date : 2019/3/7.
 * @description:
 */

public class SimBackActivity extends BaseBarActivity {

    private final static String KEY_SIM_BACK = "KEY_SIM_BACK";
    private final static String KEY_STATE_TYPE = "KEY_STATE_TYPE";
    private final static String KEY_BUNDLE = "KEY_BUNDLE";
    private int stateType;
    private SimBackEnum simBackEnum;


    public static void launch(Context context, SimBackEnum simBackEnum, Bundle bundle) {
        launch(context, simBackEnum, BaseBarActivity.STATE_BAR_DEFAULT, bundle);
    }

    /**
     *
     */
    public static void launch(Context context, SimBackEnum simBackEnum, int stateType, Bundle bundle1) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_STATE_TYPE, stateType);
        bundle.putSerializable(KEY_SIM_BACK, simBackEnum);
        bundle.putBundle(KEY_BUNDLE, bundle1);
        Intent intent = new Intent();
        intent.setClass(context, SimBackActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        initBar();
        initFragment();
    }

    private void initBar() {
        View.OnClickListener listener = new ValidClickListener() {
            @Override
            public void onValidClick() {
                finish();
            }
        };
        stateType = getIntent().getIntExtra(KEY_STATE_TYPE, 0);
        if (stateType == BaseBarActivity.STATE_BAR_DEFAULT) {

            addIconView(R.drawable.icon_sign_back, listener, true);
        } else if (stateType == BaseBarActivity.STATE_BAR_BLUE) {
            ((TextView) getStateBar().findViewById(R.id.tv_title)).setTextColor(Color.WHITE);
            (getStateBar()).setBackgroundColor(Color.parseColor("#1086FF"));
         //    getTopLine().setVisibility(View.GONE);
            addIconView(getBackView(), listener, true);
        }else if (stateType == BaseBarActivity.STATE_BAR_LOCAL) {
            addIconView(R.drawable.icon_sign_back, listener, true);
            addIconView(getImageView(R.drawable.gwc_hui), new ValidClickListener() {
                @Override
                public void onValidClick() {
                    MessageEvent bean = new MessageEvent();
                    bean.setMessage("shoppingcart_fragment");
                    EventBus.getDefault().post(bean);
                    finish();
                }
            }, false);
        }
    }

    private View getBackView() {
        ImageView view = new ImageView(mContext);
        int width = (int) getResources().getDimension(R.dimen.w_9_dip);
        int height = (int) getResources().getDimension(R.dimen.w_16_dip);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(params);
        Glide.with(mContext).load(R.drawable.icon_back).into(view);
        return view;
    }

    private View getImageView(@DrawableRes int draw) {
        ImageView view = new ImageView(mContext);
        int width = DensityUtils.dp2px(mContext, 30);
        int height = DensityUtils.dp2px(mContext, 30);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.CENTER);
        Glide.with(mContext).load(draw).into(view);
        return view;
    }

    @Override
    protected int getContentId() {
        return 0;
    }

    @Override
    protected int getStateBarColor() {
        return getIntent().getIntExtra(KEY_STATE_TYPE, 0) == BaseBarActivity.STATE_BAR_DEFAULT ||
                getIntent().getIntExtra(KEY_STATE_TYPE, 0) == BaseBarActivity.STATE_BAR_LOCAL ?
                Color.parseColor("#FFFFFF") : getResources().getColor(R.color.public_color_EC6941);
    }

    private void initFragment() {
        if (getIntent() == null || getIntent().getSerializableExtra(KEY_SIM_BACK) == null) {
            return;
        }
        simBackEnum = (SimBackEnum) getIntent().getSerializableExtra(KEY_SIM_BACK);

        if (simBackEnum.getTitle().equals("新品首发")&& simBackEnum.getValue()==1 ) {
            ((TextView) getStateBar().findViewById(R.id.tv_title)).setVisibility(View.GONE);
          (getStateBar()).setVisibility(View.GONE);
          //  getTopLine().setBackgroundColor(Color.parseColor("#ef0022"));
        //  getTopLine().setBackgroundColor(R.color.color_theme_3);


        }
        try {
            setBarTitle(simBackEnum.getTitle());
            Class<? extends Fragment> anEnum = simBackEnum.getClazz();
            if (anEnum == null) {
                return;
            }
            Fragment fragment = anEnum.newInstance();
            fragment.setArguments(getIntent().getBundleExtra(KEY_BUNDLE));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_fragment, fragment)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
