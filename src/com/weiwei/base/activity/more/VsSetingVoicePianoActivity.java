package com.weiwei.base.activity.more;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.Vspiano;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

public class VsSetingVoicePianoActivity extends VsBaseActivity {
    private ArrayList<Vspiano> pianoList = new ArrayList<Vspiano>();
    private ListView pianoListView;
    private PianoAdapter pianoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_voicepiano_set);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        getData();
        init();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void init() {
        mTitleTextView.setText("按键音琴音设置");
        pianoAdapter = new PianoAdapter(pianoList, mContext);
        pianoListView = (ListView) findViewById(R.id.poano_name_list);
        pianoListView.setAdapter(pianoAdapter);
    }

    class Hold {
        TextView pianoName;
        Button pianobtn;
        RelativeLayout pianoLayout;
    }

    private class PianoAdapter extends BaseAdapter {
        private ArrayList<Vspiano> dataList;
        private Context context;
        private LayoutInflater inflter;

        public PianoAdapter(ArrayList<Vspiano> dataList, Context context) {
            this.dataList = dataList;
            this.context = context;
            this.inflter = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Hold h;
            if (convertView == null) {
                h = new Hold();
                convertView = inflter
                        .inflate(R.layout.vs_voicepiano_item, null);
                h.pianoName = (TextView) convertView
                        .findViewById(R.id.piano_name);
                h.pianobtn = (Button) convertView
                        .findViewById(R.id.is_piano_checked);
                h.pianoLayout = (RelativeLayout) convertView
                        .findViewById(R.id.piano_type_Layout);
                convertView.setTag(h);
            } else {
                h = (Hold) convertView.getTag();
            }
            h.pianoName.setText(dataList.get(position).getPianoName());
            if (dataList.get(position).getId() == VsUserConfig.getDataInt(
                    mContext, VsUserConfig.JKEY_PIANO_ISCHECHED_ID)) {
                dataList.get(position).setIscheck(true);
                PianoAdapter.this.notifyDataSetChanged();// 重置
            }
            setCheck(h.pianobtn, dataList.get(position).isIscheck());
            final int i = position;
            h.pianoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataList.get(i).setIscheck(!dataList.get(i).isIscheck());
                    setCheck(h.pianobtn, dataList.get(i).isIscheck());
                    if (dataList.get(i).isIscheck()) {// 单位true时
                        for (int j = 0; j < dataList.size(); j++) {
                            if (j != i) {
                                dataList.get(j).setIscheck(false);
                            }
                        }
                        VsUserConfig.setData(mContext,
                                VsUserConfig.JKEY_PIANO_ISCHECHED_ID, dataList
                                        .get(i).getId());
                        vsCoreService.setSpData(dataList.get(i).getId(),
                                mContext);
                    } else {
                        setCheck(h.pianobtn, dataList.get(i).isIscheck());
                        VsUserConfig.setData(mContext,
                                VsUserConfig.JKEY_PIANO_ISCHECHED_ID, 0);

                    }
                    PianoAdapter.this.notifyDataSetChanged();// 重置

                }
            });
            return convertView;
        }

    }

    public void setCheck(Button btn, boolean ischecked) {
        if (ischecked) {
            btn.setBackgroundResource(R.drawable.sel_yes_img);
        } else {
            btn.setBackgroundResource(R.drawable.select_img);
        }
    }

    public void getData() {
        Iterator iterator = vsCoreService.pianoMap.keySet().iterator();
        while (iterator.hasNext()) {
            pianoList.add(vsCoreService.pianoMap.get(iterator.next()));
        }
    }
}
