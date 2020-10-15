package com.weiwei.base.adapter;

	import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hwtx.dududh.R;


	public class VsCallLogTextAdapter  extends BaseAdapter {
	    private static final String TAG = VsCallLogTextAdapter.class.getName();
	    private ArrayList<Map<String,Object>> data;
	    Context ctx;

	   public VsCallLogTextAdapter(Context ctx, ArrayList<Map<String,Object>> data){
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
//	    	NoticeDataList mc = (NoticeDataList)getItem(position); 
	    	
	        ViewHolder holder= null;
	        if(convertView == null){
	            View view = LayoutInflater.from(ctx).inflate(R.layout.vs_calllog_text,null);
	            holder=new ViewHolder(view);
	            convertView = view;
	        }else{
	            holder = (ViewHolder) convertView.getTag();
	        }
	        
	        String callee = data.get(position).get("callee").toString();//被叫
	        String stime = data.get(position).get("start_time").toString();
	        //通话时长  
	        String end = data.get(position).get("end_time").toString();
	        String gettime = getTime(stime, end);
	        stime = stime.substring(0, stime.indexOf(" "));
	        String money = data.get(position).get("field_fee").toString();
	        if (money!=null&&money.length()!=0) {				
	        	money = ""+Double.parseDouble(money)/10000;
			}
	       
	        
	        String time = data.get(position).get("call_time").toString();
	        holder.callee.setText(callee);
	        holder.stime.setText(stime);
	        holder.money.setText(gettime);
	        holder.time.setVisibility(View.GONE);
	        holder.time.setText(time);

	   
	       
	        
	        return convertView;
	    }

	    

	    final static class ViewHolder{
	       
	        
	        TextView callee,money,stime,time;
	        ImageView image_notice_zan;
	        ViewHolder(View view){
	            this.callee = (TextView) view.findViewById(R.id.calllee);
	            this.money = (TextView) view.findViewById(R.id.money);
	            this.stime = (TextView) view.findViewById(R.id.stime);
	            this.time = (TextView) view.findViewById(R.id.time);
	            view.setTag(this);
	        }
	    }
	    
	    
private String getTime(String start,String end){
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String time = "";
	try{
	   Date d1 = df.parse(start);
	   Date d2 = df.parse(end);
	   long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别 
	   long days = diff / (1000 * 60 * 60 * 24);
	   long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
	   long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
	   long second = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60)-minutes*(1000* 60))/1000;
	   
	  time = (hours>=10.0?hours+"":"0"+hours+"") +":" + (minutes>=10.0?minutes+"":"0"+minutes+"") + ":" + (second>=10.0?second+"":"0"+second+"");
	   return time;
	}

	catch (Exception e){

	}
	 return time;
} 
	    
	    
	  
	}
