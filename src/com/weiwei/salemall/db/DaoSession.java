package com.weiwei.salemall.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.weiwei.salemall.bean.ShoppingItemsBean;

import com.weiwei.salemall.db.ShoppingItemsBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig shoppingItemsBeanDaoConfig;

    private final ShoppingItemsBeanDao shoppingItemsBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        shoppingItemsBeanDaoConfig = daoConfigMap.get(ShoppingItemsBeanDao.class).clone();
        shoppingItemsBeanDaoConfig.initIdentityScope(type);

        shoppingItemsBeanDao = new ShoppingItemsBeanDao(shoppingItemsBeanDaoConfig, this);

        registerDao(ShoppingItemsBean.class, shoppingItemsBeanDao);
    }
    
    public void clear() {
        shoppingItemsBeanDaoConfig.clearIdentityScope();
    }

    public ShoppingItemsBeanDao getShoppingItemsBeanDao() {
        return shoppingItemsBeanDao;
    }

}
