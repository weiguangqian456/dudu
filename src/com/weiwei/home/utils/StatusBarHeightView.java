package com.weiwei.home.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.hwtx.dududh.R;


/**
 * @ProjectName: ApplicPhone
 * @Package: com.example.applicphone.ui.statusbar
 * @ClassName: StatusBarHeightView
 * @Author: LXJ
 * @CreateDate: 2020/7/28 15:09
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/7/28 15:09
 * @UpdateRemark: 更新说明
 * @Version:
 * @Description: java类作用描述:状态栏高度View,用于沉浸占位
 *
 * 使用方法：
 * 设置状态栏透明：侵入式透明status bar >> 顶部需要沉浸的是图片View时.
 * 用StatusBarHeightView 来包住你要往下移动的内容! 单独留出要沉浸的View不包住,
 * 举例:
 * <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     xmlns:tools="http://schemas.android.com/tools"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent" >
 *      <!--顶部的需要沉浸的图片View 或其他东西 视频View  等 -->
 *      <ImageView
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"
 *         android:background="@mipmap/icon_top_bg"
 *         android:scaleType="centerCrop" />
 *
 *  <!-- app:use_type="use_padding_top 向上paddingTop状态栏高度-->
 * 	<com.xxx.views.StatusBarHeightView
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:layout_alignParentEnd="true"
 *         android:layout_marginEnd="@dimen/widget_size_5"
 *         app:use_type="use_padding_top"
 *         android:orientation="vertical" >
 * 		    <!--这里放内容布局或View-->
 *          <ImageView
 *               android:id="@+id/ivUserShare"
 *               android:layout_width="@dimen/title_height"
 *               android:layout_height="@dimen/title_height"
 *               android:padding="@dimen/widget_size_10"
 *               android:src="@mipmap/icon_share_white" />
 *
 * 	</com.xxx.views.StatusBarHeightView>
 * </RelativeLayout>
 *
 * //不要忘记了, 在当前activity onCreate中设置 取消padding,  因为这个padding 我们用代码实现了,不需要系统帮我
 * StatusBarUtil.setRootViewFitsSystemWindows(this,false);
 *
 */
public class StatusBarHeightView extends LinearLayout {

    private int statusBarHeight;
    private int type;

    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public StatusBarHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(attrs);


    }

    private void init(@Nullable AttributeSet attrs) {

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(resourceId>0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }else{
            //低版本 直接设置0
            statusBarHeight = 0;
        }
        if (attrs != null) {
//            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusBarHeightView);
//            type = typedArray.getInt(R.styleable.StatusBarHeightView_use_type, 0);
//            typedArray.recycle();
        }
        if (type == 1) {
            setPadding(getPaddingLeft(), statusBarHeight, getPaddingRight(), getPaddingBottom());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (type == 0) {
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    statusBarHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
