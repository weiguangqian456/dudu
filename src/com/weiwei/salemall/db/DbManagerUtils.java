package com.weiwei.salemall.db;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * author: Created by EDZ on 2018/7/10.
 * function:  数据库操作统一管理
 */

public class DbManagerUtils {

    ShoppingItemsBeanDao shoppingItemsBeanDao = GreenDaoManager.getmInstance(mContext).getDaoSession().getShoppingItemsBeanDao();

    public void deleteDb(){

    }
}
