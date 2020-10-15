package com.weiwei.base.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.calllog.VsCallLogDetailsActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.fragment.VsDialFragment;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.util.CustomAlertDialog;
import com.weiwei.softphone.AudioPlayer;

import java.util.ArrayList;


public class VsCallLogAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private Context mContext = null;
    // private RelativeLayout mUserLeadlLayout;
    /**
     * 长按的点击事件
     */
    private ArrayList<View.OnClickListener> click_listener;
    private CustomAlertDialog dialog;
    /**
     * 需要在列表里面删除的indext
     */
    private int num = -1;
    public ArrayList<Integer> mOpenItem;
    int oldPosition = -1, old_Btn_index = -1;
    int btn_index = -1;
    private int mLcdWidth = 0;
    private float mDensity = 0;

    /**
     * item的底部是否打开
     */
    public boolean isshow = false;

    public VsCallLogAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mOpenItem = new ArrayList<Integer>();
        // this.mUserLeadlLayout = mUserLeadlLayout;
    }

    public int getCount() {
        return VsPhoneCallHistory.callLogViewList.size();
    }

    public Object getItem(int position) {
        return VsPhoneCallHistory.callLogViewList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        String mTitle = null, phoneNumStr = null, local = null, mInfotext = null;
        int directCall = 1;
        boolean isVs = false;
        final VsCallLogListItem callLogListItem = (VsCallLogListItem) getItem(position);
        final VsCallLogItem callLogItem;
        int mNum = 0;
        String calltus = "1";
        if (VsDialFragment.isall) {
            callLogItem = callLogListItem.getFirst();
            mNum = callLogListItem.getChilds().size();
            //For循环打印 然后把自己弄崩溃了 ？？？
            for (int i = 0; i < callLogListItem.getChilds().size(); i++) {
                try {
                    CustomLog.i("callLogListItem", "type=" + callLogListItem.getChilds().get(i).ctype);
                    CustomLog.i("callLogListItem", "moeny=" + callLogListItem.getChilds().get(i).callmoney);
                    CustomLog.i("callLogListItem", "calltimelength=" + callLogListItem.getChilds().get(i).calltimelength);
                }catch (Exception e){
                    Log.e("TAG-A",e.toString());
                }
            }
        } else {
            callLogItem = callLogListItem.getMissFirst();
            // mNum = callLogListItem.getChildSize();
            mNum = callLogListItem.getMissChilds().size();
        }

        local = callLogListItem.getLocalName();
        // mNum = callLogListItem.getChilds().size();

        if (callLogItem != null) {
            calltus = callLogItem.ctype;
            mTitle = callLogItem.callName;
            phoneNumStr = callLogItem.callNumber;
            directCall = callLogItem.directCall;
            isVs = callLogItem.isVs;
            mInfotext = VsUtil.kc_times_conversion_forcallog_too(callLogItem.calltimestamp);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vs_calllog_list_item, null);
            convertView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            holder = new ViewHolder();
            holder.prog_list_layout = (RelativeLayout) convertView.findViewById(R.id.prog_list_text);
            holder.title = (TextView) convertView.findViewById(R.id.prog_list_title);
            holder.calllog_times = (TextView) convertView.findViewById(R.id.calllog_times);
            holder.content_number = (TextView) convertView.findViewById(R.id.prog_list_content_number);
            holder.content_local = (TextView) convertView.findViewById(R.id.prog_list_content_local);
            holder.infoicon = (ImageView) convertView.findViewById(R.id.prog_list_icon);
            holder.button = (ImageView) convertView.findViewById(R.id.prog_list_button);
            holder.call_time = (TextView) convertView.findViewById(R.id.prog_list_time);
            holder.prog_list_free = (TextView) convertView.findViewById(R.id.prog_list_free);
            holder.vs_calllog_vs = (ImageView) convertView.findViewById(R.id.vs_calllog_vs);
            holder.prog_list_button_del = (ImageView) convertView.findViewById(R.id.prog_list_button_del);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.infoicon.setVisibility(View.VISIBLE);
        if (VsDialFragment.isedit) {// 编辑
            holder.prog_list_button_del.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        } else {
            holder.prog_list_button_del.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
        }
        CustomLog.i("vscallll", "状态=" + calltus);
        if ("1".equals(calltus)) {
            // 已接
            holder.infoicon.setImageResource(R.drawable.vs_calllog_in);
            holder.calllog_times.setTextColor(mContext.getResources().getColor(R.color.call_log_title));
            holder.title.setTextColor(mContext.getResources().getColor(R.color.call_log_title));
        } else if ("2".equals(calltus)) {
            // 呼出
            holder.infoicon.setImageResource(R.drawable.vs_calllog_out);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.call_log_title));
            holder.calllog_times.setTextColor(mContext.getResources().getColor(R.color.call_log_title));
        } else if ("3".equals(calltus)) {
            // 未接
            // holder.content_local.setVisibility(View.VISIBLE);
            holder.infoicon.setImageResource(R.drawable.vs_calllog_in);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.miss_call_text_color));
            holder.calllog_times.setTextColor(mContext.getResources().getColor(R.color.miss_call_text_color));
        } else {
            holder.infoicon.setImageResource(R.drawable.vs_calllog_in);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.vs_black));
            holder.calllog_times.setTextColor(mContext.getResources().getColor(R.color.call_log_title));
        }
        // if (VsUtil.isNull(mTitle)||phoneNumStr.equals(mTitle)) {//判断联系人名称是否为空
        // mTitle = phoneNumStr;
        // holder.content_local.setVisibility(View.VISIBLE);
        // }else{
        // holder.content_local.setVisibility(View.GONE);
        // }

        if (VsUtil.isNull(mTitle) || phoneNumStr.equals(mTitle)) {
            if (VsUtil.isNull(local)) {
                holder.content_number.setVisibility(View.GONE);
                holder.content_local.setVisibility(View.GONE);
            } else {
                holder.content_number.setVisibility(View.VISIBLE);
                holder.content_local.setVisibility(View.GONE);
                holder.content_number.setText(local);
            }
        } else {

            if (VsUtil.isNull(local)) {
                holder.content_number.setVisibility(View.VISIBLE);
                holder.content_local.setVisibility(View.GONE);
            } else {
                holder.content_number.setVisibility(View.VISIBLE);
                holder.content_local.setVisibility(View.VISIBLE);
                holder.content_number.setText(phoneNumStr);
                holder.content_local.setText(local);
            }
        }

        holder.title.setText(mTitle);
        holder.title.setVisibility(View.VISIBLE);
        // 判断是否为好友
        if (isVs) {
            holder.vs_calllog_vs.setVisibility(View.VISIBLE);
        } else {
            holder.vs_calllog_vs.setVisibility(View.GONE);
        }

        // if (directCall == 3) {
        // holder.prog_list_free.setVisibility(View.VISIBLE);
        // } else {
        // holder.prog_list_free.setVisibility(View.GONE);
        // }

        holder.call_time.setText(mInfotext);
        if (mNum > 1) {
            holder.calllog_times.setText("(" + mNum + ")");
        } else {
            holder.calllog_times.setText("");
        }

        // holder.infotext.setVisibility(View.VISIBLE);
        // holder.infotext.setText(mInfotext);

        holder.button.setVisibility(View.VISIBLE);
        final String callname = mTitle;
        final String callnumber = phoneNumStr;
        final String localName = callLogListItem.getLocalName();

        holder.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                showChoose(position);
            }
        });

        holder.prog_list_button_del.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                VsPhoneCallHistory.delCallLog(mContext, VsPhoneCallHistory.callLogViewList.get(position).getFirst().callNumber);
            }
        });
        /*
         * holder.button.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * btn_index = 1; return false; } });
		 */

        click_listener = new ArrayList<View.OnClickListener>();
        // 删除通话记录
        click_listener.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                if (VsDialFragment.isall) {
                    VsPhoneCallHistory.delCallLog(mContext, VsPhoneCallHistory.callLogViewList.get(num).getFirst().callNumber);
                } else {
                    VsPhoneCallHistory.delCallLogBYtype(mContext, VsPhoneCallHistory.callLogViewList.get(num).getMissFirst().callNumber, "3");
                }

                dialog.dismiss();
            }
        });
        // 呼叫联系人
        /*
         * click_listener.add(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if
		 * (KcUtil.isFastDoubleClick()) { return; } if
		 * (KcUtil.checkPhoneNmbeIsVs(mContext, callnumber, null)) {
		 * KcUtil.callNumber(callname, callnumber, localName, mContext, null,
		 * true); } dialog.dismiss(); } });
		 */

        holder.prog_list_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }

                // 无网络提醒
                if (VsUtil.isCallNoNetWork(mContext)) {
                    if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {// 判断语音提醒是否打开
                        AudioPlayer.getInstance().startRingBefore180Player(R.raw.vs_no_network, false);// 播放提示音
                        AudioPlayer.getInstance().stopRingBefore180Player();// 释放资源
                    }
                } else {
                    GlobalVariables.netmode = 4;
                    VsUtil.callNumber(callname, callnumber, localName, mContext, null, true,null);
                }
            }
        });

        // 查看通话记录
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                dialog.dismiss();
                // 跳转通话记录详情
                showChoose(num);
            }
        });
        /*
		 * author :黄文武 修改时间:14/09/29
		 */
        // 取消
        click_listener.add(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        // 长按事件的处理
        holder.prog_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                dialog = VsUtil.showChoose(mContext, callname, 1, click_listener);
                num = position;
                return true;
            }
        });

        return convertView;
    }

    /**
     * 点击通话记录处理
     *
     * @param position
     */
    public void showChoose(int position) {
        Intent mIntent = new Intent(mContext, VsCallLogDetailsActivity.class);
        mIntent.putExtra("CALLLOGDETAILSDATA", VsPhoneCallHistory.callLogViewList.get(position));
        mContext.startActivity(mIntent);
    }

    private class ViewHolder {
        TextView title;// 名字 或者手机号码
        TextView calllog_times;// 拨打次数
        TextView content_number;// 归属地
        TextView content_local;// 归属地
        ImageView button;// 更多
        ImageView infoicon;// 通话记录状态图标+旋转图标
        TextView call_time;// 拨打时间
        RelativeLayout prog_list_layout;
        private ImageView vs_calllog_vs;// 图标
        private TextView prog_list_free;// 免费标识
        private ImageView prog_list_button_del;
    }

    private void itemStatusChanged(int pos) {
        mOpenItem.clear();
        mOpenItem.add(pos);
    }

    private boolean getItemStatus(int pos) {
        for (int i = 0; i < mOpenItem.size(); i++) {
            int ipos = mOpenItem.get(i);
            if (ipos == pos) {
                return true;
            }
        }
        return false;
    }
}
