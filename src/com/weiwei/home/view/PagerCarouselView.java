package com.weiwei.home.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.home.entity.CustomCarouselEntity;
import com.weiwei.home.test.ValidClickListener;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.utils.DensityUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author : hc
 * @date : 2019/3/19.
 * @description:
 */

public class PagerCarouselView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private Timer timer;
    private TimerTask timerTask;
    private ViewPager viewPager;
    private int mCurrentPosition = 0;
    private int  mChangeTime   = 800;
    private int  mIntervalTime = 5000;
    private LinearLayout layout;
    private final int mDefaultRadius = R.drawable.bg_default_radius;
    private final int mThemeRadius   = R.drawable.bg_theme_radius;
    private String mFirstUrl,mLastUrl;
    private List<View> mDocList = new ArrayList<>();
    private List<String> mUrlList = new ArrayList<>();
    private OnImageClickListener mImageClickListener;

    public PagerCarouselView(Context context) {
        super(context);
    }

    public PagerCarouselView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerCarouselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PagerCarouselView setImageList(List<? extends CustomCarouselEntity> list){
        mUrlList.clear();
        if(list == null || list.size() == 0){
            ToastAstrictUtils.getInstance().show("不能为NULL");
            return this;
        }
        //其实只做了String
        if(list.get(0).getUrl() instanceof String){
            if(TextUtils.isEmpty(mFirstUrl)){
                mFirstUrl = (String)list.get(0).getUrl();
            }
            mLastUrl  = (String) list.get(list.size() - 1).getUrl();
            for(int i = 0 ; i < list.size() ; i++){
                mUrlList.add((String) list.get(i).getUrl());
            }
        }
        return this;
    }

    public PagerCarouselView addImageUrl(String url){
        if(mUrlList.size() == 0){
            mFirstUrl = url;
        }
        mLastUrl = url;
        mUrlList.add(url);
        return this;
    }

    public PagerCarouselView setValidClickListener(OnImageClickListener mImageClickListener){
        this.mImageClickListener = mImageClickListener;
        return this;
    }

    public PagerCarouselView setChangeTime(int mChangeTime){
        this.mChangeTime = mChangeTime;
        return this;
    }

    public PagerCarouselView setIntervalTime(int mIntervalTime){
        this.mIntervalTime = mIntervalTime;
        return this;
    }

    public PagerCarouselView toRemoveView(){
        removeAllViews();
        layout = null;
        viewPager = null;
        return this;
    }

    public void initViews(){
        stopTimer();
        initViewPager();
        initLinearLayout();
        startTimer();
    }

    private void initLinearLayout(){
        if(layout == null){
            layout = new LinearLayout(getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtils.dp2px(getContext(),20));
            params.gravity = Gravity.BOTTOM;
            layout.setGravity(Gravity.CENTER);
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.removeAllViews();
            mDocList.clear();
            for(int i = 0 ; i < mUrlList.size(); i++){
                View docView = getDocView();
                if(i == 0){  docView.setBackgroundResource(mThemeRadius); }
                //保存对象引用
                mDocList.add(docView);
                layout.addView(docView);
            }
            addView(layout);
        }
    }

    private void initViewPager(){
        if(viewPager == null){
            viewPager = new ViewPager(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            viewPager.setLayoutParams(params);
            viewPager.setAdapter(new CarousePagerAdapter(mUrlList));
            viewPager.addOnPageChangeListener(this);
            viewPager.setOffscreenPageLimit(4);//防止白屏
            PagerScroller scroller = new PagerScroller(getContext());
            scroller.initViewPagerScroll(viewPager,mChangeTime);
            addView(viewPager);
        }
        viewPager.setCurrentItem(1);
    }

    private View getDocView(){
        View view = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtils.dp2px(getContext(),6),DensityUtils.dp2px(getContext(),6));
        params.gravity = Gravity.CENTER_VERTICAL;
        params.rightMargin = DensityUtils.dp2px(getContext(),8);
        view.setLayoutParams(params);
        view.setBackgroundResource(mDefaultRadius);
        return view;
    }

    private ImageView getImageView(String url, final int finalPosition){
        ImageView view = new ImageView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setOnClickListener(new ValidClickListener() {
            @Override
            public void onValidClick() {
                if(mImageClickListener != null){
                    int position = finalPosition;
                    if(position == 0 ) {
                        position = mUrlList.size();
                    }else if(position > mUrlList.size() + 1){
                        position = 0;
                    }else{
                        position -= 1;
                    }
                    mImageClickListener.onImageClick(position);
                }
            }
        });
        Glide.with(getContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.mall_logits_default)).into(view);
        return view;
    }

    private long mOrderTime = 0;
    private void startTimer(){
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long l = 0;
                        if(mOrderTime != 0){
                            l = System.currentTimeMillis() - mOrderTime;
                        }
                        mOrderTime = System.currentTimeMillis();
                        Log.e("TAG-A","Time:" + l + " run: " + mCurrentPosition);
                        int k = mCurrentPosition;
                        int count = mUrlList.size() + 1;
                        if(k <= count){
                            k++;
                        }else{
                            k = 0;
                        }
                        if(viewPager != null)viewPager.setCurrentItem(k,true);
                    }
                });
            }
        };
        timer.schedule(timerTask,mIntervalTime,mIntervalTime);
    }

    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position == 0 && positionOffset == 0){
            viewPager.setCurrentItem(mUrlList.size(),false);
        }else if(position > mUrlList.size()){
            viewPager.setCurrentItem(1,false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        //改变指向器
        if(position == 0){
            position = mUrlList.size();
        } else if (position > mUrlList.size()){
            position = 1;
        }
        for(int i = 0 ; i < mDocList.size() ; i++){
            mDocList.get(i).setBackgroundResource(i == position - 1? mThemeRadius : mDefaultRadius);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            stopTimer();
        }else if(ev.getAction() == MotionEvent.ACTION_UP){
            startTimer();
        }
        return super.dispatchTouchEvent(ev);
    }

    //不显示时停止播放
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            startTimer();
        } else if (visibility == INVISIBLE || visibility == GONE) {
            stopTimer();
        }
    }

    /**
     * Adapter 适配器
     */
    class CarousePagerAdapter extends PagerAdapter{

        private List<String> mUrlArray = new ArrayList<>();
        private Map<String,View> mImageMap = new HashMap<>();

        CarousePagerAdapter(List<String> mUrlArray){
            //设置视图列表 前后额外添加一个用于循环显示
            this.mUrlArray.clear();
            this.mUrlArray.add(mLastUrl );
            this.mUrlArray.addAll(mUrlArray);
            this.mUrlArray.add(mFirstUrl);
        }

        @Override
        public int getCount() {
            return mUrlArray.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String key = mUrlArray.get(position) + position;
            if(!mImageMap.containsKey(key)){
                mImageMap.put(key,getImageView(mUrlArray.get(position),position));
            }
            container.addView(mImageMap.get(key));
            return mImageMap.get(key);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object
                object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    public interface OnImageClickListener{
        void onImageClick(int position);
    }
}
