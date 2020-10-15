package com.weiwei.home.base;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;

import butterknife.BindView;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description: 带标题栏的Activity
 */

public abstract class BaseBarActivity extends BaseActivity {

    public final static int STATE_BAR_DEFAULT = 0;
    public final static int STATE_BAR_BLUE    = 1;
    public final static int STATE_BAR_LOCAL   = 2;

    @BindView(R.id.rl_title)
    RelativeLayout rl_title;
    @BindView(R.id.ll_title_left)
    LinearLayout ll_title_left;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_title_right)
    LinearLayout ll_title_right;
//    @BindView(R.id.view_line)
//    View view_line;
    @BindView(R.id.ll_search)
    protected LinearLayout ll_search;

    protected FrameLayout frame;
    protected abstract int getContentId();

    protected void setBarTitle(String title){
        tv_title.setText(title);
    }

    @Override
    protected void initViewPrevious() {
        frame = findViewById(R.id.fl_fragment);
        if(getContentId() != 0){
            frame.addView(getLayoutInflater().inflate(getContentId(),null));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bar;
    }

    protected View getStateBar(){
        return rl_title;
    }

//    protected View getTopLine(){
//        return view_line;
//    }

    protected void addIconView(int drawId, View.OnClickListener listener){
        addIconView(drawId, listener, false);
    }

    protected void addIconView(View view,View.OnClickListener listener,boolean isLeft){
        View layout = addFragment(view,listener);
        if(isLeft){
            ll_title_left.addView(layout);
        }else{
            ll_title_right.addView(layout);
        }
    }

    protected void addIconView(int drawId, View.OnClickListener listener,boolean isLeft){
        View layout = addFragment(createView(drawId),listener);
        if(isLeft){
            ll_title_left.addView(layout);
        }else{
            ll_title_right.addView(layout);
        }
    }

    private View addFragment(View view, View.OnClickListener listener){
        FrameLayout layout = new FrameLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(VsUtil.dp2px(mContext,45),VsUtil.dp2px(mContext,45));
        layout.setLayoutParams(params);
        layout.addView(view);
        layout.setOnClickListener(listener);
        return layout;
    }

    private View createView(int drawId){
        ImageView view = new ImageView(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(VsUtil.dp2px(mContext,24),VsUtil.dp2px(mContext,24));
        params.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(params);
        Glide.with(mContext).load(drawId).into(view);
        return view;
    }

}
