package com.weiwei.base.activity.contacts;

import android.os.Bundle;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseFragmentActivity;
import com.weiwei.base.fragment.VsContactsListFragment;

/**
 * 联系人界面
 */
public class VsContactListActivity extends VsBaseFragmentActivity {
    VsContactsListFragment mVsContactsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs_contact_list);

        mVsContactsListFragment = (VsContactsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contacts_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVsContactsListFragment.showLeftNavBtn();
    }

}
