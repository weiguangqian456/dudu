package com.weiwei.home.utils;

import android.app.Activity;

/**
 * @author : hc
 * @date : 2019/3/7.
 * @description:
 */

public class StateBarUtils {

    public static int getStateBarHeight(Activity activity){
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return  statusBarHeight;
    }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getActivity() != null){
//            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
//            View decorView = getActivity().getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            //根据上面设置是否对状态栏单独设置颜色
////            if (false) {
////                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorTheme));//设置状态栏背景色
////            } else {
//                getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
////            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
//            WindowManager.LayoutParams localLayoutParams = getActivity().getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        } else {
//            ToastAstrictUtils.getInstance().show("低于4.4的android系统版本不存在沉浸式状态栏");
//        }
    //防止覆盖状态栏
//        ViewGroup.LayoutParams layoutParams = view_bar.getLayoutParams();
//        layoutParams.height = StateBarUtils.getStateBarHeight(getActivity());
//        view_bar.setLayoutParams(layoutParams);
}
