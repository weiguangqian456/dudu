package com.weiwei.base.activity.contacts;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.softphone.AudioPlayer;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @Title:Android客户端
 * @Description:联系人详情号码适配器
 * @Copyright: Copyright (c) 2014
 * @Date: 2014-9-23
 */
public class VsContactDetailsAdapter extends BaseAdapter {

    /**
     * 存放选中的号码:key号码的index
     */
    private Hashtable<String, Boolean> checkedNumList = new Hashtable<String, Boolean>();
    private Handler mHandler;
    public boolean isSelectPhoneCard = false;
    /**
     * 是否初始化过多选框状态 -1未有,0有
     */
    public int havaInit = -1;
    /**
     * 联系人对象
     */
    private VsContactItem data;
    /**
     *
     */
    private LayoutInflater mInflater;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 标记最近联系人标志
     */
    private boolean flag = false;

    /**
     * 构造方法
     *
     * @param context
     */
    public VsContactDetailsAdapter(Context context, ArrayList<Object> list, int displayWidth, Handler tHandler) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        if (list.size() > 0) {
            this.data = (VsContactItem) list.get(0);
        }

        this.mHandler = tHandler;
    }

    @Override
    public int getCount() {
        return data.phoneNumList == null ? 0 : data.phoneNumList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.phoneNumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.vs_contact_detail_item, null);
            holder.vs_contact_detail_layout = (RelativeLayout) convertView.findViewById(R.id.vs_contact_detail_layout);
            holder.vs_contact_type_local = (LinearLayout) convertView.findViewById(R.id.vs_contact_type_local);
            holder.vs_contact_detail_linearlayout = (LinearLayout) convertView.findViewById(R.id.vs_contact_detail_linearlayout);
            holder.vs_contact_detail_itm_call_img = (ImageView) convertView.findViewById(R.id.vs_contact_detail_itm_call_img);
            holder.vs_contact_detail_itm_call_layout = (RelativeLayout) convertView.findViewById(R.id.vs_contact_detail_itm_call_layout);

            holder.vs_contact_detail_number = (TextView) convertView.findViewById(R.id.vs_contact_detail_number);
            holder.vs_contact_detail_area = (TextView) convertView.findViewById(R.id.vs_contact_detail_area);
            holder.vs_contact_detail_type = (TextView) convertView.findViewById(R.id.vs_contact_detail_type);
            holder.vs_contact_detail_near = (TextView) convertView.findViewById(R.id.vs_contact_detail_near);

            holder.vs_contact_detail_itm_freecall_layout = (RelativeLayout) convertView.findViewById(R.id.vs_contact_detail_itm_freecall_layout);
            holder.vs_contact_detail_itm_freemsg_layout = (RelativeLayout) convertView.findViewById(R.id.vs_contact_detail_itm_freemsg_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String phoneNum = "";
        String localName = "";

        try {
            // 电话号码和昵称
            phoneNum = data.phoneNumList.get(position);
            localName = data.localNameList.get(position);

            if (data.phoneNumList != null && data.phoneNumList.size() > 0 && VsUtil.checheNumberIsVsUser(mContext, data.phoneNumList.get(position))) {

                holder.vs_contact_detail_itm_freecall_layout.setVisibility(View.GONE);
                holder.vs_contact_detail_itm_freemsg_layout.setVisibility(View.GONE);
                holder.vs_contact_detail_itm_call_layout.setVisibility(View.GONE);

            } else {

                holder.vs_contact_detail_itm_freecall_layout.setVisibility(View.GONE);
                holder.vs_contact_detail_itm_freemsg_layout.setVisibility(View.GONE);
                holder.vs_contact_detail_itm_call_layout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.vs_contact_detail_linearlayout.setOnClickListener(new ApapterOnClickLinener(position, phoneNum, localName));// 免费电话的点击事件
        //
        // holder.vs_contact_detail_itm_freecall_layout
        // .setOnClickListener(new ApapterOnClickLinener(position,
        // phoneNum, localName));// 免费电话的点击事件

        holder.vs_contact_detail_itm_freemsg_layout.setOnClickListener(new ApapterOnClickLinener(position, phoneNum, localName));// 免费短信的点击事件

        holder.vs_contact_detail_itm_call_layout.setOnClickListener(new ApapterOnClickLinener(position, phoneNum, localName));// TD电话的点击事件

        holder.vs_contact_detail_number.setText(phoneNum);
        holder.vs_contact_detail_area.setText(localName);

        if (VsUtil.checkMobilePhone(phoneNum)) {
            String type = VsUtil.checkMobileType(phoneNum);
            if (type != null) {
                holder.vs_contact_detail_type.setText(type);
            }
        }

        if (VsUtil.isNull(localName)) {
            holder.vs_contact_type_local.setVisibility(View.GONE);
        } else {
            holder.vs_contact_type_local.setVisibility(View.VISIBLE);
        }

        // 检查是否是发送名片选择请求
        if (!isSelectPhoneCard || data.phoneNumList.size() <= 1) {

        } else {
            saveSelectStatus(havaInit);// 初始化

        }

        return convertView;
    }

    /**
     * 适配器中点击监听事件
     *
     * @author 黄文武
     */
    class DetailAdapterItemlistener implements OnClickListener {
        private int position = 0;
        String phoneNum = "";

        public DetailAdapterItemlistener(String phoneNum, int position) {
            this.position = position;
            this.phoneNum = phoneNum;
        }

        @Override
        public void onClick(View v) {
            if (checkedNumList.get("" + position)) {
                checkedNumList.put("" + position, false);

                sendMessage(phoneNum, VsContactsDetailsActivity.MSG_ID_DELETESEND_CONTACT);

            } else {
                checkedNumList.put("" + position, true);
                sendMessage(phoneNum, VsContactsDetailsActivity.MSG_ID_ADDSEND_CONTACT);

            }
            notifyDataSetChanged();
        }

    }

    /**
     * 适配器中点击监听事件
     *
     * @author 李志
     */
    class ApapterOnClickLinener implements OnClickListener {
        private int position = 0;
        String phoneNum = "";
        String localName = "";

        public ApapterOnClickLinener(int position, String phoneNum, String localName) {
            this.position = position;
            this.phoneNum = phoneNum;
            this.localName = localName;
        }

        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            switch (v.getId()) {
                // case R.id.vs_contact_detail_itm_call_layout:// TD电话点击
                // case R.id.vs_contact_detail_itm_freecall_layout:// 免费电话点击
                // // 拨号
                // VsUtil.callNumber(data.mContactName, phoneNum, localName,
                // mContext, "", true);
                // break;

                // 点击打电话的事件
                case R.id.vs_contact_detail_linearlayout:

                    // 无网络提醒
                    if (VsUtil.isCallNoNetWork(mContext)) {
                        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {// 判断语音提醒是否打开
                            AudioPlayer.getInstance().startRingBefore180Player(R.raw.vs_no_network, false);// 播放提示音
                            AudioPlayer.getInstance().stopRingBefore180Player();// 释放资源
                        }
                    } else {
                        GlobalVariables.netmode = 4;
                        if(mContext instanceof Activity){
                            FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
                            VsUtil.callNumber(data.mContactName, phoneNum, localName, mContext, "", false,fragmentManager);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * ListView组件类
     *
     * @author 李志
     */
    private class ViewHolder {
        /**
         * 整体布局
         */
        private RelativeLayout vs_contact_detail_layout;

        private LinearLayout vs_contact_type_local;

        private LinearLayout vs_contact_detail_linearlayout;

        /**
         * 打电话的img：是好友显示V、不是好友显示电话图片
         */
        private ImageView vs_contact_detail_itm_call_img;
        // 打电话：TD电话 layout
        private RelativeLayout vs_contact_detail_itm_call_layout;
        // 打电话：TD电话 imageview
        /**
         * 电话号码
         */
        private TextView vs_contact_detail_number;
        /**
         * 电话号码归属地
         */
        private TextView vs_contact_detail_area;
        /**
         * 号码运营商类型
         */
        private TextView vs_contact_detail_type;
        /**
         * 最近添加
         */
        private TextView vs_contact_detail_near;

        // 免费电话
        private RelativeLayout vs_contact_detail_itm_freecall_layout;
        // 免费短信
        private RelativeLayout vs_contact_detail_itm_freemsg_layout;
    }

    /**
     * 初始化多选框状态
     *
     * @param haveInit 是否初始化过
     */
    private void saveSelectStatus(int haveInit) {

        if (haveInit == -1) {
            checkedNumList.clear();
            if (data.phoneNumList.size() > 1) {
                checkedNumList.put("" + 0, true);
                sendMessage(data.phoneNumList.get(0), VsContactsDetailsActivity.MSG_ID_ADDSEND_CONTACT);// 加入第一个号码
                for (int i = 1; i < data.phoneNumList.size(); i++) {
                    checkedNumList.put("" + i, false);// 默认第一个号码选中,其余都为false
                    this.havaInit = 0;
                }
            }
        }
    }


    private void sendMessage(String mContactPhoneNumber, int msgId) {
        Message msg = mHandler.obtainMessage();
        Bundle data = new Bundle();
        data.putString("ContactPhoneNumber", mContactPhoneNumber);
        msg.setData(data);
        msg.what = msgId;
        mHandler.sendMessage(msg);
    }
}
