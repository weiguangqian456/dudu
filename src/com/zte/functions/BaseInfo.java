package com.zte.functions;

import java.util.Calendar;

/**
 * Created by Jiangxuewu on 2015/2/3.
 */
public abstract class BaseInfo implements IHttp {

    protected String getData() {
        int yyyy = Calendar.getInstance().get(Calendar.YEAR);
        int mm = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int dd = Calendar.getInstance().get(Calendar.DATE);
        String year = String.valueOf(yyyy);
        String month = mm < 10 ? String.valueOf("0" + mm) : String.valueOf(mm);
        String day = dd < 10 ? String.valueOf("0" + dd) : String.valueOf(dd);

        return "" + year + month + day;
    }

}
