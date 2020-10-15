package com.weiwei.base.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.adapter.VsCallLogTextAdapter.ViewHolder;

public class VsCallMoneyTextAdapter extends BaseAdapter {
    private static final String TAG = VsCallLogTextAdapter.class.getName();
    private ArrayList<Map<String,Object>> data;
    Context ctx;

   public VsCallMoneyTextAdapter(Context ctx, ArrayList<Map<String,Object>> data){
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//    	NoticeDataList mc = (NoticeDataList)getItem(position); 
    	
        ViewHolder holder= null;
        if(convertView == null){
            View view = LayoutInflater.from(ctx).inflate(R.layout.vs_callmoney_text,null);
            holder=new ViewHolder(view);
            convertView = view;
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
       String time = data.get(position).get("ctime").toString();
       time = time.substring(0, time.indexOf(" "));
        holder.program.setText(time);
        String total = data.get(position).get("balance").toString();
        if (total != null&&total.length()!=0) {
			
        	total = ""+Double.parseDouble(total)/100000;
		}
        String count = data.get(position).get("reason").toString();
        holder.totalMoney.setText(data.get(position).get("remark").toString());
        holder.time.setText(count);

   
       
        
        return convertView;
    }

    

    final static class ViewHolder{
       
        
        TextView program,totalMoney,time;
      
        ViewHolder(View view){

        	this.program = (TextView) view.findViewById(R.id.program);
            this.totalMoney = (TextView) view.findViewById(R.id.totalMoney);
            this.time = (TextView) view.findViewById(R.id.time);

            view.setTag(this);
        }
    }
    
    
    
    
    
  
}
