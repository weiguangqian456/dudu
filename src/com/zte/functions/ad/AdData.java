package com.zte.functions.ad;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.text.TextUtils;

import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

/**
 * Created by Jiangxuewu on 2015/3/19.
 * <p>
 * 广告数据
 * </p>
 */
public class AdData {

    private static final String TITLE = "title";//广告标题
    private static final String URL = "url";//广告跳转地址
    private static final String PHONE = "phone";//电话号码, 此项不为空时，优先启动拨号程序）
    private static final String LOGOPIC = "logopic";//广告的LOG图片地址
    private static final String BIGPIC = "bigpic";//广告的图片地址
    private static final String STIME = "stime";//广告生效时间
    private static final String ETIME = "etime";//广告结束时间
    private static final String TYPE = "type";//广告链接的打开方式(inline_wap:内部浏览器打开，out_wap：外部浏览器打开)
    private static final String ORDERNO = "orderno";//广告显示排序
    private static final String TAG = AdData.class.getSimpleName();

    private String title;
    private String url;
    private String phone;
    private String logopic;
    private String bigpic;
    private String stime;
    private String etime;
    private String type;
    private int orderno;

    public boolean equal(AdData d) {
        boolean r = null == d;

        if (r)
            return false;
        r = this.title.equals(d.getTitle());
        if (!r)
            return false;
        r = this.url.equals(d.getUrl());
        if (!r)
            return false;
        r = this.phone.equals(d.getPhone());
        if (!r)
            return false;
        r = this.logopic.equals(d.getLogopic());
        if (!r)
            return false;
        r = this.bigpic.equals(d.getBigpic());
        if (!r)
            return false;
        r = this.stime.equals(d.getStime());
        if (!r)
            return false;
        r = this.etime.equals(d.getEtime());
        if (!r)
            return false;
        r = this.type.equals(d.getType());
        if (!r)
            return false;
        r = this.orderno == d.getOrderno();
        if (!r)
            return false;
        return true;
    }

    public void downloadImg(){
        AdManager.getInstance().downloadImage(logopic);
        AdManager.getInstance().downloadImage(bigpic);
    }

    public AdData(JSONObject json) {

        if (null == json) {
            return;
        }

        if (json.has(TITLE)) {
            try {
                title = json.getString(TITLE);
            } catch (JSONException e) {
            }
        }
        if (json.has(URL)) {
            try {
                url = json.getString(URL);
            } catch (JSONException e) {
            }
        }
        if (json.has(PHONE)) {
            try {
                phone = json.getString(PHONE);
            } catch (JSONException e) {
            }
        }
        if (json.has(LOGOPIC)) {
            try {
                logopic = json.getString(LOGOPIC);
            } catch (JSONException e) {
            }
        }
        if (json.has(BIGPIC)) {
            try {
                bigpic = json.getString(BIGPIC);

            } catch (JSONException e) {
            }
        }
        if (json.has(STIME)) {
            try {
                stime = json.getString(STIME);
            } catch (JSONException e) {
            }
        }
        if (json.has(ETIME)) {
            try {
                etime = json.getString(ETIME);
            } catch (JSONException e) {
            }
        }
        if (json.has(TYPE)) {
            try {
                type = json.getString(TYPE);
            } catch (JSONException e) {
            }
        }
        if (json.has(ORDERNO)) {
            try {
                orderno = json.getInt(ORDERNO);
            } catch (JSONException e) {
            }
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        if (!TextUtils.isEmpty(url)){
            if (!url.startsWith("http://")){
                return "http://" + url;
            }
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogopic() {
        return logopic;
    }

    public void setLogopic(String logopic) {
        this.logopic = logopic;
    }

    public String getBigpic() {

        return bigpic;
    }

    public void setBigpic(String bigpic) {
        this.bigpic = bigpic;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrderno() {
        return orderno;
    }

    public void setOrderno(int orderno) {
        this.orderno = orderno;
    }

    /**
     * 是否过期
     *
     * @return
     */
	public boolean isRight() {
        long st = Long.valueOf(stime);
        long et = Long.valueOf(etime);

//        int y = Calendar.getInstance().get(Calendar.YEAR);
//        int M = Calendar.getInstance().get(Calendar.MONTH) + 1;
//        int d = Calendar.getInstance().get(Calendar.DATE);
//        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        int m = Calendar.getInstance().get(Calendar.MINUTE);
//        int s = Calendar.getInstance().get(Calendar.SECOND);

        // 24小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        String cur = sdf.format(new Date(System.currentTimeMillis()));

        long curTime = Long.valueOf(cur);

        return curTime >= st && curTime < et;
    }
}
