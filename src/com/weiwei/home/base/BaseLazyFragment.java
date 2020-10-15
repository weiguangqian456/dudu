package com.weiwei.home.base;

/**
 * @author hc
 * @date 2019/3/4.
 * @description: Fragment 懒加载 - 懒得写
 */

public abstract class BaseLazyFragment extends BaseFragment {

    /**
     * 交给子类实现
     * @return 布局id
     */
    @Override
    protected abstract int getLayoutId();

    /**
     * 布局显示后调用
     */
    protected abstract void loadLazyData();

    @Override
    protected  void initView(){
        //懒加载实现initView调用isLazyLoad
        isLazyLoad();
    }

    private void isLazyLoad(){
        loadLazyData();
    }
}
