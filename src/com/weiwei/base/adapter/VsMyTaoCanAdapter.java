package com.weiwei.base.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;


public class VsMyTaoCanAdapter extends BaseAdapter {
    private static final String TAG = VsCallLogTextAdapter.class.getName();
    private ArrayList<Map<String,Object>> data;
    Context ctx;

   public VsMyTaoCanAdapter(Context ctx, ArrayList<Map<String,Object>> data){
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//    	NoticeDataList mc = (NoticeDataList)getItem(position); 
    	
        ViewHolder holder= null;
        if(convertView == null){
            View view = LayoutInflater.from(ctx).inflate(R.layout.vs_myred_text,null);
            holder=new ViewHolder(view);
            convertView = view;
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        String redlee = data.get(position).get("package_name").toString();
        String action = data.get(position).get("is_nature").toString();
        String amount = data.get(position).get("left_call_time").toString();
        String ctime = data.get(position).get("expire_time").toString();
        String is_time = data.get(position).get("is_time").toString();
        if (is_time.equals("y")) {
        	holder.ctime.setText(amount);
		}else {
			holder.ctime.setText("- -");
		}
        ctime = ctime.substring(0, ctime.indexOf(" "));
        holder.redlee.setText(ctime);
        holder.action.setVisibility(View.GONE);
        holder.money.setText(redlee);
        

   
       
        
        return convertView;
    }

    

    final static class ViewHolder{
       
        
        TextView redlee,action,money,ctime;
        ImageView image_notice_zan;
        ViewHolder(View view){
            this.redlee = (TextView) view.findViewById(R.id.redlee);//名称
            this.money = (TextView) view.findViewById(R.id.money);//有效时间
            this.action = (TextView) view.findViewById(R.id.action);
            this.ctime = (TextView) view.findViewById(R.id.ctime);//分钟数
            view.setTag(this);
        }
    }
    
    
    
    
    
  
}