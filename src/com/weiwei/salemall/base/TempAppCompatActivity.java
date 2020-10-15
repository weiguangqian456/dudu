package com.weiwei.salemall.base;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public abstract class TempAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 初始化
     */
    protected abstract void init();

    protected Activity getAct() {
        return this;
    }

    /**
     * 获取控件id
     *
     * @param resid
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T fv(int resid) {
        return (T) this.findViewById(resid);
    }

    /**
     * 获取控件id
     *
     * @param v
     * @param resid
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T fv(View v, int resid) {
        return (T) v.findViewById(resid);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusHeight() {
        // 原理是从R文件中找到dimen这个内部类，然后通过反射拿到dimen中的status_bar_height的值，
        // 这个值其实就是资源id，然后再通过getResource方法拿到该id对应的值
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = this.getResources().getDimensionPixelSize(height);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreeWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public int getScreeHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 跳转界面
     */
    public void skipPage(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 跳转到拨打电话界面
     *
     * @param s
     */
    public void skipPage(String s) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + s));
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String value) {
        Intent intent = new Intent(this, cls);
        if (null != value) {
            intent.putExtra("value", value);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param key
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String key, String value) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param key
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(this, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param key
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String key, boolean value) {
        Intent intent = new Intent(this, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param key
     * @param values
     */
    public void skipPage(Class<? extends Activity> cls, String[] key, String[] values) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != values) {
            for (int i = 0; i < key.length; i++) {
                intent.putExtra(key[i], values[i]);
            }
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param bundle
     */
    public void skipPage(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent();
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * @param msg
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String msg, int t) {
        Toast.makeText(this, msg, t).show();
    }

    public void showToast(int resId, int t) {
        showToast(getString(resId), t);
    }

    public void showToast_error() {
        Toast.makeText(this, "未连接服务", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg
     */
    protected void LOG_I(String msg) {
        Log.i("tag", msg);
    }

    protected void LOG_D(String msg) {
        Log.d("tag", msg);
    }

    protected void LOG_E(String msg) {
        Log.e("tag", msg);
    }

    protected void LOG_W(String msg) {
        Log.w("tag", msg);
    }

    protected void LOG_V(String msg) {
        Log.v("tag", msg);
    }
}
