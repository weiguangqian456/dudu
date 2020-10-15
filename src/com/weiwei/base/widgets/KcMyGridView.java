package com.weiwei.base.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class KcMyGridView extends GridView{
	public KcMyGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
   
    public KcMyGridView(Context context) {   
        super(context);   
    }   
   
    public KcMyGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
   
    @Override   
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
   
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
                MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }   				
}
