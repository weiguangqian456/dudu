package com.weiwei.base.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.VsContactsSelectActivity;
import com.weiwei.base.activity.contacts.VsContactsDetailsActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.Resource;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.fragment.VsContactsListFragment;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.util.CircleImageDrawable;
import com.weiwei.base.util.CustomAlertDialog;
import com.hwtx.dududh.R;


public class VsContactListAdapter extends BaseAdapter implements Filterable {


    private static final String TAG = VsContactListAdapter.class.getSimpleName();
    /**
     * 存放选中的号码:key为mContactId+该号码的index
     */
    private Hashtable<String, Boolean> checkedNumList = new Hashtable<String, Boolean>();
    private Handler mHandler;
    private Context mContext = null;
    private boolean isContact = true;
    private List<VsContactItem> data = null;
    private int maxCount = VsPhoneCallHistory.CONTACTLIST.size();// 最多可以选择个数
    /**
     * 长按的点击事件
     */
    private ArrayList<View.OnClickListener> click_listener;
    private CustomAlertDialog dialog;
    /**
     * 是否是fragement
     */
    private boolean isFragment = true;
    /**
     * 是否是我的分组
     */
    private boolean isMyGroup = false;
    /**
     * 我的分组编号
     */
    private int groupIndex;
    /**
     * 需要处理的ID
     */
    private String mContactId = "";
    /**
     * 需要在列表里面删除的indext
     */
    private int num;
    /**
     * 是否为好友列表
     */
    private boolean isVS = false;
    /**
     * 输入状态
     */
    private boolean searchIn = false;

    public VsContactListAdapter(Context context) {
        this.mContext = context;
    }

    public VsContactListAdapter(Context context, boolean isContact, Handler tHandler) {
        this.mContext = context;
        this.mHandler = tHandler;
        this.isContact = isContact;
        checkedNumList.clear();
        for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {//将联系人多个号码存入该集合中
            if (item.phoneNumList.size() > 1) {
                for (int i = 0; i < item.phoneNumList.size(); i++) {
                    checkedNumList.put(item.mContactId + i, false);//默认都为false
                }
            }
        }
    }

    public void setData(List<VsContactItem> data, final int commonLen) {
        if (commonLen != 0) {
            this.data = data;
        }
    }

    /**
     * 设置最多可以选择个数
     */
    public void setMaxCount(final int count) {
        this.maxCount = count;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @return the isFragment
     */
    public boolean isFragment() {
        return isFragment;
    }

    /**
     * @param isFragment the isFragment to set
     */
    public void setFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    /**
     * @return the isMyGroup
     */
    public boolean isMyGroup() {
        return isMyGroup;
    }

    /**
     * @param isMyGroup the isMyGroup to set
     */
    public void setMyGroup(boolean isMyGroup) {
        this.isMyGroup = isMyGroup;
    }

    /**
     * @return the groupIndex
     */
    public int getGroupIndex() {
        return groupIndex;
    }

    /**
     * @param groupIndex the groupIndex to set
     */
    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Activity activity = (Activity) mContext;
        // View currentFocus = activity.getCurrentFocus();
        // if (currentFocus != null) {
        // currentFocus.clearFocus();
        // }
        View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(R.layout.vs_contact_list_item, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }

        final VsContactItem item = (VsContactItem) getItem(position);// 联系人信息
        TextView titleView = viewCache.getContactTitleView();// title
        TextView nameView = viewCache.getContactNameView();// 名字
        TextView namePyView = viewCache.getContactNamePyView();// 拼音
        LinearLayout numLocalLayout = viewCache.getContactNumLocalLayout();// 号码和归属地
        TextView numView = viewCache.getContactNumberView();// 号码
        TextView localView = viewCache.getContactLocalView();// 归属地
        ImageButton selectView = viewCache.getContactSelectView();// 选择框
        ImageView isvsuser_image = viewCache.getIsVsUserImageView();// 是否是VS好友
        LinearLayout selectNumLocalLayout = viewCache.getSelectNumLocalLayout();//多个号码        
        TextView imageView_pic=viewCache.getContactPic();
        titleView.setVisibility(View.GONE);
        if (isFragment()) {
            searchIn = VsContactsListFragment.searchInput;
        }

        if (position >= 0 && searchIn == false) {
            // String now = KcPhoneCallHistory.CONTACTLIST.get(position).mContactFirstLetter.substring(0, 1);
            // if (!now.equals(KcPhoneCallHistory.CONTACTLIST.get(position > 0 ? position - 1 :
            // position).mContactFirstLetter
            if (data.get(position).mContactFirstLetter != null && !"".equals(data.get(position).mContactFirstLetter)) {
                String now = data.get(position).mContactFirstLetter.substring(0, 1);
                if (!now.equals(data.get(position > 0 ? position - 1 : position).mContactFirstLetter.substring(0, 1))
                        || position == 0) {
                    titleView.setText(now);
                    titleView.setVisibility(View.VISIBLE);
                } else {
                    titleView.setVisibility(View.GONE);
                    titleView.setText("");
                }
            }
        }
        // 名字
        nameView.setText(item.mContactName);
        nameView.setSelected(true);

        if (item.mIndex == -10) {
            namePyView.setVisibility(View.GONE);
            numLocalLayout.setVisibility(View.GONE);
        } else if (searchIn) {
            namePyView.setVisibility(View.VISIBLE);
            numLocalLayout.setVisibility(View.VISIBLE);
            if (item.mIndex == 4) {
                setContactPyViewInfo(item, namePyView);
            } else if (item.mIndex == 5) {
                setContactPyViewInfo(item, nameView);
            } else {
                setContactPyViewInfo(item, namePyView);
            }
            numView.setText(item.mContactPhoneNumber);
            localView.setText(item.mContactBelongTo);
        } else {
            namePyView.setVisibility(View.GONE);
            numLocalLayout.setVisibility(View.GONE);
        }

        if (isContact) {
            selectView.setVisibility(View.GONE);
            // CustomLog.i("fragment", "isKcUser：" + data.get(position).isKcUser);
//            imageView_pic.setVisibility(View.VISIBLE);
            if(position%4 == 0){
            	imageView_pic.setBackgroundResource(R.drawable.vs_myhead_contact_a);
            }
            if(position%4 == 1){
            	imageView_pic.setBackgroundResource(R.drawable.vs_myhead_contact_b);
            }
            if(position%4 == 2){
            	imageView_pic.setBackgroundResource(R.drawable.vs_myhead_contact_c);
            }
            if(position%4 == 3){
            	imageView_pic.setBackgroundResource(R.drawable.vs_myhead_contact_d);
            }
            String a = String.valueOf((item.mContactName.toCharArray()[item.mContactName.toCharArray().length-1]));
           
            imageView_pic.setText(a);
            CustomLog.i("123", "come in");
            CustomLog.i("123", "position = " + position);
            CustomLog.i("123", "item.mContactName = " + item.mContactName.toCharArray()[item.mContactName.toCharArray().length-1]);
            
            
//            Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.myself_head);
      
            if (data.get(position).isVsUser) {
                isvsuser_image.setVisibility(View.VISIBLE);
                isvsuser_image.setBackgroundResource(R.drawable.vs_call_icon);
            } else {
                isvsuser_image.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams layoutParamsRoot = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            nameView.setLayoutParams(layoutParamsRoot);
            nameView.setCompoundDrawables(null, null, null, null);
            rowView.setOnClickListener(new ContactOnClickListener(item));
            // }
        } else {
            /*
             * author:黄文武 修改时间:2014/10/17
			 * 
			 */
            if (item.phoneNumList.size() > 1) {//联系人号码有多个的时候
                selectView.setVisibility(View.INVISIBLE);
                selectNumLocalLayout.removeAllViews();
                selectNumLocalLayout.setVisibility(View.VISIBLE);
                //遍历所有号码
                for (int i = 0; i < item.phoneNumList.size(); i++) {
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View view = inflater.inflate(R.layout.vs_contact_list_item_select_num, null);
                    CheckBox cb_phone_num = (CheckBox) view.findViewById(R.id.cb_phone_num);
                    cb_phone_num.setText(item.phoneNumList.get(i));
                    selectNumLocalLayout.addView(view);

                    final int index = i;//号码下标
                    cb_phone_num.setOnCheckedChangeListener(new OnCheckedChangeListener() {//添加选中监听

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (!checkedNumList.get(item.mContactId + index) && isChecked) {
                                System.out.println("选中" + buttonView.getText().toString());
                                sendMessage(buttonView.getText().toString(), VsContactsSelectActivity.MSG_ID_INVITE_CONTACT);

                            } else if (checkedNumList.get(item.mContactId + index) && !isChecked) {
                                sendMessage(buttonView.getText().toString(), VsContactsSelectActivity.MSG_ID_DELETE_CONTACT);
                            }
                            checkedNumList.put(item.mContactId + index, isChecked);//将选中号码状态保存
                        }
                    });
                    cb_phone_num.setChecked(checkedNumList.get(item.mContactId + index));
                }
            } else {
                selectView.setVisibility(View.VISIBLE);
                selectNumLocalLayout.removeAllViews();
                selectNumLocalLayout.setVisibility(View.GONE);


                selectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if (KcUtil.isFastDoubleClick()) {
                        // return;
                        // }
                        toggle(position, item);
                        if (item.isSelect == 0) {
                            sendMessage(item.mContactPhoneNumber, VsContactsSelectActivity.MSG_ID_INVITE_CONTACT);
                        } else {
                            sendMessage(item.mContactPhoneNumber, VsContactsSelectActivity.MSG_ID_DELETE_CONTACT);
                        }
                    }
                });
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if (KcUtil.isFastDoubleClick()) {
                    // return;
                    // }
                    if (item.phoneNumList.size() == 1) {
                        if (item.mIndex == -10) {
                            AddUpdateContact(item);
                        } else {
                            toggle(position, item);
                            if (item.isSelect == 0) {
                                sendMessage(item.mContactPhoneNumber, VsContactsSelectActivity.MSG_ID_INVITE_CONTACT);
                            } else {
                                sendMessage(item.mContactPhoneNumber, VsContactsSelectActivity.MSG_ID_DELETE_CONTACT);
                            }
                        }
                    }
                }
            });

            if (item.isSelect == 0) {
                selectView.setImageResource(R.drawable.sel_yes_img);
            } else {
                selectView.setImageResource(R.drawable.transparent);
            }

        }
        if (item.mIndex == -10)
            selectView.setVisibility(View.GONE);
        click_listener = new ArrayList<View.OnClickListener>();
        // 查看联系人
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 跳转联系人详情
                Intent intent = new Intent(mContext, VsContactsDetailsActivity.class);
                // intent.putExtra("CONTACTDETAILS", true);
                intent.putExtra("contact_type", 2);
                intent.putExtra("CONTACTDETAILSDATA", (VsContactItem) getItem(num));
                mContext.startActivity(intent);
            }
        });
        // 编辑联系人
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VsUtil.updateContact(mContext, mContactId);
                dialog.dismiss();
            }
        });
        // 删除联系人
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomLog.i(TAG, "进来第" + mContactId + "次" + "num===" + num);
                new DelContact().execute();
            }
        });
        /*
		 * author :黄文武
		 * 修改时间:14/09/29
		 */
        //取消
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        rowView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (isMyGroup) {
                    dialog = VsUtil.showChoose(mContext, item.mContactName, 4, click_listener);
                } else {
                    dialog = VsUtil.showChoose(mContext, item.mContactName, 2, click_listener);
                }
                mContactId = item.mContactId;
                num = position;
                return false;
            }
        });
        rowView.clearFocus();
        return rowView;
    }
       

    /**
     * 添加或更新系统联系人
     *
     * @param item
     */
    private void AddUpdateContact(VsContactItem item) {
        // TODO Auto-generated method stub
        if (item.mContactName.equals(Resource.CREATE_CONTACT)) {
            Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
            addContactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
			/*
			 * author:黄文武 修改时间:2014/10/13
			 * 验证是否为纯数字
			 */
            if (item.mInput.matches("^[0-9]+$")) {
                addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, item.mInput);
            } else {
                addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, item.mInput);
            }
            mContext.startActivity(addContactIntent);
        } else if (item.mContactName.equals(Resource.ADD_CURRENT_CONTACT)) {
            Intent addContactIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            addContactIntent.setType("vnd.android.cursor.item/person");
            addContactIntent.setType("vnd.android.cursor.item/contact");
            addContactIntent.setType("vnd.android.cursor.item/raw_contact");
            addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, item.mInput);
            mContext.startActivity(addContactIntent);
        }
    }

    private class ContactOnClickListener implements View.OnClickListener {
        private VsContactItem item;

        public ContactOnClickListener(VsContactItem item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            if (item.mIndex == -10) {
                AddUpdateContact(item);
            } else {
                CustomLog.i("GDK", "item=" + item.isVsUser);
                // 跳转联系人详情
                Intent intent = new Intent(mContext, VsContactsDetailsActivity.class);
                intent.putExtra("contact_type", 2);
                intent.putExtra("CONTACTDETAILSDATA", item);
                mContext.startActivity(intent);
            }
        }
    }

    /**
     * 这里通过ViewCatch来减少了 findViewById的使用
     *
     * @author dell create at 2013-6-6上午11:14:31
     */
    public class ViewCache {
        private View baseView;
        private TextView contact_titleview;// title
        private TextView contact_nameview;// 名字
        private TextView contact_namepyview;// 拼音
        private LinearLayout contact_num_local_layout;// 号码和归属地
        private TextView contact_num_view;// 号码
        private TextView contact_local_view;// 归属地
        private ImageButton select_item_yes;// 多选框
        private LinearLayout select_num_local_layout;//多个手机号码
        private TextView image_pic;//头像
        /**
         * 是否为好友
         */
        private ImageView isvsuser_image;

        public ViewCache(View baseView) {
            this.baseView = baseView;
        }

        public TextView getContactTitleView() {
            if (contact_titleview == null) {
                contact_titleview = (TextView) baseView.findViewById(R.id.contact_titleview);
            }
            return contact_titleview;
        }

        public void setContactTitleView(TextView contact_titleview) {
            this.contact_titleview = contact_titleview;
        }

        public TextView getContactNameView() {
            if (contact_nameview == null) {
                contact_nameview = (TextView) baseView.findViewById(R.id.contact_nameview);
            }
            return contact_nameview;
        }

        public TextView getContactNamePyView() {
            if (contact_namepyview == null) {
                contact_namepyview = (TextView) baseView.findViewById(R.id.contact_namepyview);
            }
            return contact_namepyview;
        }

        public LinearLayout getContactNumLocalLayout() {
            if (contact_num_local_layout == null) {
                contact_num_local_layout = (LinearLayout) baseView.findViewById(R.id.contact_num_local_layout);
            }
            return contact_num_local_layout;
        }

        public TextView getContactNumberView() {
            if (contact_num_view == null) {
                contact_num_view = (TextView) baseView.findViewById(R.id.contact_num_view);
            }
            return contact_num_view;
        }

        public TextView getContactLocalView() {
            if (contact_local_view == null) {
                contact_local_view = (TextView) baseView.findViewById(R.id.contact_local_view);
            }
            return contact_local_view;
        }

        public ImageButton getContactSelectView() {
            if (select_item_yes == null) {
                select_item_yes = (ImageButton) baseView.findViewById(R.id.select_item_btn);
            }
            return select_item_yes;
        }

        public ImageView getIsVsUserImageView() {
            if (isvsuser_image == null) {
                isvsuser_image = (ImageView) baseView.findViewById(R.id.isvsuser_image);
            }
            return isvsuser_image;
        }

        public LinearLayout getSelectNumLocalLayout() {
            if (select_num_local_layout == null) {
                select_num_local_layout = (LinearLayout) baseView.findViewById(R.id.select_num_local_layout);
            }
            return select_num_local_layout;
        }
        public TextView getContactPic() {
            if (image_pic == null) {
                image_pic = (TextView) baseView.findViewById(R.id.contact_list_item_head_text);
            }
            return image_pic;
        }
    }

    /**
     * 设置搜索显示拼音
     *
     * @param item
     * @param contactPyView
     */
    private void setContactPyViewInfo(VsContactItem item, TextView contactPyView) {
        switch (item.mIndex) {// 0和1:首字母 2：全拼 3：号码
            case 0:
            case 1:
                contactPyView.setText(VsUtil.formatHtml(item.mContactFirstUpper, item.mContactPY, item.mInput,
                        item.mContactFirstUpperToNumber, item.mIndex));
                break;
            case 2:
                contactPyView.setText(VsUtil.formatHtml(item.mContactPY, null, item.mInput, item.mContactPYToNumber,
                        item.mIndex));
                break;
            case 3:
                contactPyView.setText(VsUtil.formatHtml(item.mContactPhoneNumber, null, item.mInput, null, item.mIndex));
                break;
            case 4:
                contactPyView.setText(VsUtil.formatHtml(item.mContactPY, null, item.mInput, null, item.mIndex));
                break;
            case 5:
                contactPyView.setText(VsUtil.formatHtml(item.mContactName, null, item.mInput, null, item.mIndex));
                break;
            case 6:
            case 7:
                contactPyView.setText(VsUtil.formatHtml(item.mContactFirstUpper, item.mContactPY, item.mInput,
                        item.mContactFirstUpper, item.mIndex));
                break;
            case 9:
                String phoneNumber = "";
                for (int i = 0; i < item.phoneNumList.size(); i++) {
                    if (item.phoneNumList.get(i).contains(item.mInput)) {
                        phoneNumber = item.phoneNumList.get(i);
                    }
                }
                contactPyView.setText(VsUtil.formatHtml(phoneNumber, null, item.mInput, null, 3));
                break;
            case 10:
                contactPyView.setText(VsUtil.formatHtml(item.mContactPhoneNumber, null, item.mInput, null, 3));
                break;
            default:
                break;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence s) {
                FilterResults results = new FilterResults();
                ArrayList<VsContactItem> list = new ArrayList<VsContactItem>(200);
                String input = s.toString();
                // 匹配联系人
                // for (KcContactItem item : KcPhoneCallHistory.CONTACTLIST) {
                for (VsContactItem item : isVS ? VsPhoneCallHistory.VSCONTACTLIST : VsPhoneCallHistory.CONTACTLIST) {
                    item.mInput = input;
                    if (item.mContactFirstUpperToNumber.equals(input) && !list.contains(item)) {
                        item.mIndex = 0;
                        list.add(item);
                    } else if (item.mContactFirstUpperToNumber.contains(input) && !list.contains(item)) {
                        item.mIndex = 1;
                        item.mMatchIndex = item.mContactFirstUpperToNumber.indexOf(input);
                        list.add(item);
                    } else if (item.mContactFirstUpper.equals(input.toUpperCase()) && !list.contains(item)) {
                        item.mIndex = 6;
                        list.add(item);
                    } else if (item.mContactFirstUpper.contains(input.toUpperCase()) && !list.contains(item)) {
                        item.mIndex = 7;
                        item.mMatchIndex = item.mContactFirstUpper.indexOf(input.toUpperCase());
                        list.add(item);
                    } else if (item.mContactPYToNumber.contains(input) && !list.contains(item)) {
                        char[] chars = item.mContactFirstUpperToNumber.toCharArray();
                        for (char c : chars) {
                            if (c == input.toCharArray()[0] && !list.contains(item)) {
                                item.mIndex = 2;
                                item.mMatchIndex = item.mContactPYToNumber.indexOf(input);
                                list.add(item);
                            }
                        }
                    }
                    // else if (!list.contains(item) && item.mContactPYToNumber.contains(input)) {
                    // // 拼音匹配，需要去韵母匹配
                    // String strLeftLetter = item.mContactPY;
                    // String strPinyinNumber = item.mContactPYToNumber;
                    // int nMatchIndex = strPinyinNumber.indexOf(input);
                    // item.mMatchIndex = nMatchIndex;
                    // String inputLetter = strLeftLetter.substring(nMatchIndex, nMatchIndex + input.length());
                    // while (-1 != strLeftLetter.toLowerCase().indexOf(inputLetter.toLowerCase())) {
                    // String strTmp = strLeftLetter.substring(nMatchIndex);
                    // strTmp = strTmp.replace(strTmp.substring(1), strTmp.substring(1).toLowerCase());
                    // if (strTmp.contains(setFirstLetterUpper(inputLetter.toLowerCase()))) {
                    // item.mIndex = 2;
                    // list.add(item);
                    // break;
                    // }
                    // strLeftLetter = strLeftLetter.substring(nMatchIndex + 1);
                    // strPinyinNumber = strPinyinNumber.substring(nMatchIndex + 1);
                    // nMatchIndex = strPinyinNumber.indexOf(input);
                    // if (-1 != nMatchIndex) {
                    // inputLetter = strLeftLetter.substring(nMatchIndex, nMatchIndex + input.length());
                    // }
                    // item.mMatchIndex += nMatchIndex + 1;
                    // }
                    // }
                    else if (item.mContactPY.toLowerCase().contains(input.toLowerCase()) && !list.contains(item)) {
                        item.mIndex = 4;
                        list.add(item);
                    } else if (item.mContactName.contains(input) && !list.contains(item)) {
                        item.mIndex = 5;
                        list.add(item);
                    } else {
                        if (item.phoneNumList.size() > 1) {
                            String vsNumbers = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_GETVSUSERINFO);
                            for (int i = 0; i < item.phoneNumList.size(); i++) {
                                if (item.phoneNumList.get(i).contains(input) && !list.contains(item)) {
                                    item.mIndex = 9;
                                    VsContactItem tmpItem = new VsContactItem(item);
                                    tmpItem.mIndex = 10;
                                    tmpItem.mContactPhoneNumber = item.phoneNumList.get(i);
                                    tmpItem.isVsUser = vsNumbers.contains(item.phoneNumList.get(i));
                                    list.add(tmpItem);
                                }
                            }
                        } else if (item.mContactPhoneNumber.contains(input) && !list.contains(item)) {
                            item.mIndex = 3;
                            list.add(item);
                        }
                    }
                }
                // 搜索顺序排序/全拼匹配顺序排序
                Collections.sort(list, new Comparator<VsContactItem>() {
                    @Override
                    public int compare(VsContactItem date1, VsContactItem date2) {
                        int x = date1.mIndex - date2.mIndex;
                        if (x == 0) {
                            return date1.mMatchIndex - date2.mMatchIndex;
                        }
                        return x;
                    }
                });
                if (list.size() <= 0) {
                    VsContactItem item1 = new VsContactItem();
                    item1.mContactName = Resource.CREATE_CONTACT;
                    item1.mIndex = -10;
                    item1.mInput = input;
                    list.add(item1);

                    VsContactItem item2 = new VsContactItem();
                    item2.mContactName = Resource.ADD_CURRENT_CONTACT;
                    item2.mIndex = -10;
                    item2.mInput = input;
                    list.add(item2);
                }
                results.values = list;
                results.count = list.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Resource.SEARCH_LIST.clear();
                @SuppressWarnings("unchecked")
                ArrayList<VsContactItem> result = (ArrayList<VsContactItem>) results.values;

                if (result != null && result.size() > 0) {
                    setData(result, -1);
                }
                notifyDataSetChanged();
            }
        };
    }

    private static String setFirstLetterUpper(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        return output;// input.replace(input.substring(0, 1), input.substring(0, 1).toUpperCase());
    }

    public void toggle(int position, VsContactItem item) {
        if (item.isSelect == 0) {
            item.isSelect = -1;
            maxCount++;
        } else {
            if (maxCount != 0) {
                maxCount--;
                item.isSelect = 0;
            }
        }
        this.notifyDataSetChanged();// date changed and we should refresh the
    }

    /**
     * 全选
     *
     * @param sel
     */
    public void allSelect(byte sel) {
        int size = VsPhoneCallHistory.CONTACTLIST.size();
        for (int i = 0; i < size; i++) {
            VsPhoneCallHistory.CONTACTLIST.get(i).isSelect = sel;
        }
        this.notifyDataSetChanged();
    }

    /**
     * 选中后发消息
     *
     * @param mContactPhoneNumber
     * @param msgId
     */
    private void sendMessage(String mContactPhoneNumber, int msgId) {
        Message msg = mHandler.obtainMessage();
        Bundle data = new Bundle();
        data.putString("ContactPhoneNumber", mContactPhoneNumber);
        msg.setData(data);
        msg.what = msgId;
        mHandler.sendMessage(msg);
    }

    /**
     * 删除联系人异步Task
     */
    class DelContact extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            return VsUtil.deleteContact(mContext, mContactId);
        }

        @Override
        protected void onPreExecute() {
            dialog.dismiss();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (null != b && b) {
                Toast.makeText(mContext, R.string.contact_delete_contacts_success, Toast.LENGTH_SHORT).show();
                data.remove(num);
                notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, R.string.contact_delete_contacts_failed_and_again, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(b);
        }
    }
}