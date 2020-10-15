package com.weiwei.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.DensityUtils;
import com.weiwei.salemall.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hc
 * @date by hc on 2019/2/28.
 * @description: 加载异常时的布局  不大完善 未测试 未使用
 */

public class CustomErrorView extends FrameLayout {

    public static final String ERROR_NOT = "ERROR_NOT";
    public static final String ERROR_VIEW_EMPTY = "ERROR_VIEW_EMPTY";
    public static final String ERROR_VIEW_LOGIN = "ERROR_VIEW_LOGIN";
    public static final String ERROR_VIEW_NETWORK = "ERROR_VIEW_NETWORK";

    private String mState = ERROR_NOT;
    private OnErrorClickListener onErrorClickListener;

    private Map<String, View> mErrorViewMap = new HashMap<>();

    private ImageView noContent;
    private ImageView noNet;
    private Context mContext;

    public CustomErrorView(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CustomErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CustomErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void initState(String mState) {
        this.mState = mState;
        if (mErrorViewMap.size() == 0) {
            setVisibility(View.GONE);
        } else {
            for (String key : mErrorViewMap.keySet()) {
                if (!ERROR_NOT.equals(mState) && mErrorViewMap.containsKey(key)) {
                    setVisibility(View.VISIBLE);
                    mErrorViewMap.get(key).setVisibility(key.equals(mState) ? View.VISIBLE : View.GONE);
                } else {
                    setVisibility(View.GONE);
                }
            }
        }
    }

    private void initView() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //占用触摸事件
            }
        });
        initState(ERROR_NOT);
        View initEmptyView = getInitEmpty();
        View initNetworkView = getInitNetwork();
        initEmptyView.setVisibility(View.GONE);
        initNetworkView.setVisibility(View.GONE);
        this.addView(initEmptyView);
        this.addView(initNetworkView);
        mErrorViewMap.put(ERROR_VIEW_EMPTY, initEmptyView);
        mErrorViewMap.put(ERROR_VIEW_NETWORK, initNetworkView);
    }

    public View getEmptyView() {
        return mErrorViewMap.get(ERROR_VIEW_EMPTY);
    }

    private View getInitEmpty() {
        View errorEmptyView = View.inflate(getContext(), R.layout.layout_error_empty, null);

        errorEmptyView.findViewById(R.id.btn_fresh).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toRefresh();
            }
        });
        noContent = errorEmptyView.findViewById(R.id.iv_no_content);

        return errorEmptyView;
    }

    private View getInitNetwork() {
        View errorNetworkView = View.inflate(getContext(), R.layout.layout_error_network, null);
        errorNetworkView.findViewById(R.id.btn_fresh).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toRefresh();
            }
        });
        noNet = errorNetworkView.findViewById(R.id.iv_no_net);
        return errorNetworkView;
    }

    public void setShowView() {
        if (noContent != null) {
            MarginLayoutParams noContentLayoutParams = (MarginLayoutParams) noContent.getLayoutParams();
            noContentLayoutParams.topMargin = DensityUtils.dp2px(mContext, 40);
            ;
            noContent.setLayoutParams(noContentLayoutParams);
        }

        if (noNet != null) {
            MarginLayoutParams noNetLayoutParams = (MarginLayoutParams) noNet.getLayoutParams();
            noNetLayoutParams.topMargin = DensityUtils.dp2px(mContext, 40);
            ;
            noNet.setLayoutParams(noNetLayoutParams);
        }

    }

    private void toRefresh() {
        if (onErrorClickListener != null) {
            onErrorClickListener.onRefresh();
        } else {
            ToastUtils.show(getContext(), "请实现OnErrorClickListener接口");
        }
    }

    public void setOnErrorClickListener(OnErrorClickListener onErrorClickListener) {
        this.onErrorClickListener = onErrorClickListener;
    }

    public interface OnErrorClickListener {
        void onRefresh();
    }
}
