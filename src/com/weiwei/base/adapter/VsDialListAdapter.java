package com.weiwei.base.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.contacts.VsContactsDetailsActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.Resource;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.fragment.VsDialFragment;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.softphone.AudioPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * 拨号盘搜索适配器
 */
public class VsDialListAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater = null;
    private Context mContext = null;
    public ArrayList<Integer> mOpenItem;
    int oldPosition = -1, old_Btn_index = -1;
    int btn_index = -1;
    View flagView;
    private int mLcdWidth = 0;
    private float mDensity = 0;

    public VsDialListAdapter(Context mC) {
        this.mContext = mC;
        mInflater = LayoutInflater.from(mC);
        mOpenItem = new ArrayList<Integer>();
    }

    @Override
    public int getCount() {
        return VsDialFragment.searchInput ? (VsPhoneCallHistory.CONTACTIONFOLIST == null ? 0 : VsPhoneCallHistory.CONTACTIONFOLIST.size()) : 0;
    }

    @Override
    public Object getItem(int position) {
        return VsDialFragment.searchInput ? (VsPhoneCallHistory.CONTACTIONFOLIST.get(position)) : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /**
         * 电话号码
         */
        String phoneNumber = "";
        final CacheViewHolder cViewHolder;
        final VsContactItem item = (VsContactItem) getItem(position);
        if (convertView == null) {
            cViewHolder = new CacheViewHolder();
            convertView = mInflater.inflate(R.layout.vs_dia_contact_search, null);
            cViewHolder.callViewLayout = (RelativeLayout) convertView.findViewById(R.id.layout_information);
            cViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.search_name_textview);
            cViewHolder.namePyTextView = (TextView) convertView.findViewById(R.id.search_namepy_textview);
            cViewHolder.button = (ImageView) convertView.findViewById(R.id.prog_list_button);
            cViewHolder.v_dia_line1 = (View) convertView.findViewById(R.id.v_dia_line1);
            cViewHolder.v_dia_line2 = (View) convertView.findViewById(R.id.v_dia_line2);
            convertView.setTag(cViewHolder);
        } else {
            cViewHolder = (CacheViewHolder) convertView.getTag();
        }
        switch (item.mIndex) {
            case 0:
            case 1:
                cViewHolder.nameTextView.setText(item.mContactName);
                cViewHolder.namePyTextView.setText(VsUtil.formatHtml(item.mContactFirstUpper, item.mContactPY, item.mInput, item.mContactFirstUpperToNumber, item.mIndex));
                // cViewHolder.phoneNumberTextView.setText(item.mContactPhoneNumber);
                // cViewHolder.BelongingToTextView.setText(item.mContactBelongTo);
                showContactInfoView(cViewHolder);
                break;
            case 2:
                cViewHolder.nameTextView.setText(item.mContactName);
                cViewHolder.namePyTextView.setText(VsUtil.formatHtml(item.mContactPY, null, item.mInput, item.mContactPYToNumber, item.mIndex));
                // cViewHolder.phoneNumberTextView.setText(item.mContactPhoneNumber);
                // cViewHolder.BelongingToTextView.setText(item.mContactBelongTo);
                showContactInfoView(cViewHolder);
                break;
            case 3:
                cViewHolder.nameTextView.setText(item.mContactName);
                // cViewHolder.namePyTextView.setText(item.mContactPY);
                cViewHolder.namePyTextView.setText(VsUtil.formatHtml(item.mContactPhoneNumber, null, item.mInput, null, item.mIndex));
                // cViewHolder.BelongingToTextView.setText(item.mContactBelongTo);
                showContactInfoView(cViewHolder);
                break;
            case 9:
                cViewHolder.nameTextView.setText(item.mContactName);
                // cViewHolder.namePyTextView.setText(item.mContactPY);
                for (int i = 0; i < item.phoneNumList.size(); i++) {
                    if (item.phoneNumList.get(i).contains(item.mInput)) {
                        phoneNumber = item.phoneNumList.get(i);
                    }
                }
                cViewHolder.namePyTextView.setText(VsUtil.formatHtml(phoneNumber, null, item.mInput, null, 3));
                // cViewHolder.BelongingToTextView.setText(item.mContactBelongTo);
                showContactInfoView(cViewHolder);
                break;
            case -10:
                cViewHolder.nameTextView.setText(item.mContactName);
                cViewHolder.namePyTextView.setVisibility(View.GONE);
                cViewHolder.button.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        final String callname = (item.mContactName == null || item.mContactName.length() == 0) ? item.mContactPhoneNumber : item.mContactName;
        if ("".equals(phoneNumber)) {
            phoneNumber = item.mContactPhoneNumber;
        }
        final String callnumber = phoneNumber;
        final String localName = item.mContactBelongTo;
        if (position == VsPhoneCallHistory.CONTACTIONFOLIST.size() - 1) {
            cViewHolder.v_dia_line1.setVisibility(View.GONE);
            cViewHolder.v_dia_line2.setVisibility(View.VISIBLE);
        } else {
            cViewHolder.v_dia_line2.setVisibility(View.GONE);
            cViewHolder.v_dia_line1.setVisibility(View.VISIBLE);
        }
        // 打开显示开关
        cViewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }

                // 跳转联系人详情
                Intent intent = new Intent(mContext, VsContactsDetailsActivity.class);
                // intent.putExtra("CONTACTDETAILS", true);
                intent.putExtra("contact_type", 2);
                intent.putExtra("CONTACTDETAILSDATA", item);
                mContext.startActivity(intent);
            }
        });
        cViewHolder.button.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btn_index = 2;
                flagView = v;
                return false;
            }
        });

        cViewHolder.callViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }


                if (item.mIndex == -10) {
                    if (item.mContactName.equals(Resource.CREATE_CONTACT)) {
                        VsUtil.addContact(mContext, item.mInput);
                        GlobalVariables.SAVE_CALLLONG_NUMBER = item.mInput;//保存输入的手机号码
                    } else if (item.mContactName.equals(Resource.ADD_CURRENT_CONTACT)) {
                        VsUtil.addCurrentContact(mContext, item.mInput);
                    }
                } else {


//					if (VsUtil.checkPhoneNmbeIsVs(mContext, callname, callnumber, null)) {
//						VsUtil.callNumber(callname, callnumber, localName, mContext, null, true);
//					}

                    // 无网络提醒
                    if (VsUtil.isCallNoNetWork(mContext)) {
                        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {// 判断语音提醒是否打开
                            AudioPlayer.getInstance().startRingBefore180Player(R.raw.vs_no_network, false);// 播放提示音
                            AudioPlayer.getInstance().stopRingBefore180Player();// 释放资源
                        }
                    } else {
                        GlobalVariables.netmode = 4;
                        if(mContext instanceof Activity) {
                            FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
                            VsUtil.callNumber(callname, callnumber, localName, mContext, null, true,fragmentManager);
                        }else{
                            VsUtil.callNumber(callname, callnumber, localName, mContext, null, true,null);
                        }
                    }
                }
            }
        });

        return convertView;
    }

    private class CacheViewHolder {
        RelativeLayout callViewLayout = null;
        // LinearLayout enterViewLayout = null;
        TextView nameTextView;// 名字
        TextView namePyTextView;// 标题
        ImageView button;// 更多
        View v_dia_line1;
        View v_dia_line2;
    }

    private void showContactInfoView(CacheViewHolder cViewHolder) {
        cViewHolder.namePyTextView.setVisibility(View.VISIBLE);
        cViewHolder.button.setVisibility(View.VISIBLE);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                VsPhoneCallHistory.CONTACTIONFOLIST.clear();
                @SuppressWarnings("unchecked") ArrayList<VsContactItem> list = (ArrayList<VsContactItem>) results.values;
                if (list != null && list.size() > 0) {
                    VsPhoneCallHistory.CONTACTIONFOLIST.addAll(list);
                }
                mOpenItem.clear();
                oldPosition = -1;
                old_Btn_index = -1;
                btn_index = -1;
                mLcdWidth = 0;
                mDensity = 0;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence s) {
                FilterResults results = new FilterResults();
                ArrayList<VsContactItem> list = new ArrayList<VsContactItem>();
                String input = s.toString();
                // 匹配联系人
                for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                    item.mInput = input;
                    if (item.mContactFirstUpperToNumber.equals(input) && !list.contains(item)) {
                        item.mIndex = 0;
                        list.add(item);
                    } else if (item.mContactFirstUpperToNumber.contains(input) && !list.contains(item)) {
                        item.mIndex = 1;
                        item.mMatchIndex = item.mContactFirstUpperToNumber.indexOf(input);
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
                    } else if (item.mContactPhoneNumber.contains(input) && !list.contains(item)) {
                        item.mIndex = 3;
                        list.add(item);
                    } else {
                        if (item.phoneNumList.size() > 1) {
                            for (int i = 0; i < item.phoneNumList.size(); i++) {
                                if (item.phoneNumList.get(i).contains(input) && !list.contains(item)) {
                                    item.mIndex = 9;
                                    list.add(item);
                                }
                            }
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
        };
        return filter;
    }
}
