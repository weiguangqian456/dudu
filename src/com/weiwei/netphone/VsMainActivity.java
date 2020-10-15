package com.weiwei.netphone;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.helpdesk.easeui.event.ShoppingChangeEvent;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.fragment.FragmentIndicator;
import com.weiwei.base.fragment.FragmentIndicator.OnIndicateListener;
import com.weiwei.base.fragment.VsDialFragment;
import com.weiwei.base.widgets.CustomDialog2;
import com.weiwei.base.widgets.TitleBarView;
import com.weiwei.home.utils.StateBarUtils;
import com.weiwei.home.fragment.HomeMallFragment;
import com.weiwei.home.utils.SystemBarTintManager;
import com.weiwei.salemall.activity.SearchViewActivity;
import com.weiwei.salemall.bean.MessageEvent;
import com.weiwei.salemall.db.ShoppingItemsBeanDao;
import com.weiwei.salemall.emsemob.ChatActivity;
import com.weiwei.salemall.emsemob.EaseMobMsgHelper;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.ToastUtils;
import com.weiwei.salemall.widget.BadgeView;
import com.zte.functions.ad.AdManager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.weiwei.base.dataprovider.DfineAction.COMPANY_NAME;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_DEFAULT_PASSWORD;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_IMSERVER;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TAGNAME;
import static java.lang.Integer.MAX_VALUE;


/**
 * @author yangyu 功能描述：主Activity类，继承自FragmentActivity
 */
public class VsMainActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = VsMainActivity.class.getSimpleName();
    public Fragment[] mFragments;
    public Context mContext;
    //private TextView mAddContactTV;
    private int flag = -1;
    /**
     * 打电话按钮
     */
    public RelativeLayout ll_mybottom;
    /**
     * 呼叫按钮
     */
    public Button mCallBtn;

    /**
     *
     *
     *
     * 拨号按钮是否显示
     */
    public static boolean isShow = true;
    /**
     * 需要跳转的fragment
     */
    public int indicator = 0;
    /**
     * 第一个消息
     */
    public static String msg1 = "";
    /**
     * 是否为新注册，true为新注册、false不是
     */
    public static String firstreg = "";
    /**
     * 是否绑定手机，true为绑定，false未绑定
     */
    public static String check = "";
    /**
     * 第一次绑定，true为第一次，false不是
     */
    public static String firstbind = "";
    /**
     * 底部导航栏
     */
    public FragmentIndicator mIndicator = null;

    /**
     * 联系人
     */
    private LinearLayout vs_mian_contact_layout;

    /**
     * 更新tab选中图标
     */
    private final char MSG_ID_UPDATE_TAB_FOUSE = 150;
    /**
     * 更新未选中
     */
    private final char MSG_ID_UPDATE_TAB_NOMAL = 151;
    /**
     * 显示余额相关提示
     */
    private final char MSG_ID_SHOW_BALANCE = 152;
    // 更新消息提醒
    private final char MSG_ID_MESSAGE_SHOW = 158;
    //标题
    private com.weiwei.base.widgets.TitleBarView mContactsTitleBarView;
    private LinearLayout titleMain;
    // 拨号图标动画
    private Animation animDown = null;
    private Animation animUp = null;


    /**
     * 标题
     */
    private TextView titleView;
    private ImageView tv_left;
    private ImageView tv_right;
    private RelativeLayout rightRl;
    private EditText btn;
    private String phoneNumberString = "";
    private String save_name = "";
    private VsDialFragment mFragment;

    //占据状态栏高度
    private View mBarView;

    private ShoppingItemsBeanDao shoppingItemsBeanDao;
    private BadgeView badgeView;

    /**
     * 未读消息
     */
    public int noReadNum;

    /**
     * 搜索按钮
     */
    private RelativeLayout searchRl;

    /**
     * 购物车Fragment
     */
    private static final String SHOPPINGCART_FRAGMENT_INDICATOR = "shoppingcart_fragment";
    private static final String SALEMALL_FRAGMENT_INDICATOR = "salemall_fragment";
    private static final String SALEMALL_FRAGMENT_NOMSG = "noReadMsg";

    private MyConnectionListener connectionListener = null;

    private String mMessageEvent;
    private TitleBarView mTitleBarView ;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_ID_MESSAGE_SHOW:// 更新消息状态
                    Bundle b = msg.getData();
                    // b.getString("countString");
                    FragmentIndicator.showHideRed(b.getInt("countString"));
//                    if(indicator==0){
//                    	 mContactsTitleBarView.setTextMms(b.getInt("countString"),false);
//                    }else{
//                    	 mContactsTitleBarView.setTextMms(b.getInt("countString"),true); //更新信息

//                    }
                    break;
                case MSG_ID_UPDATE_TAB_FOUSE:
//                    mIndicator.setTabImageFouse(msg.getData().getBoolean("isopen", false));
//                    if (flag == 0) {// 自己点击自己有动画CollinWang2015.03.25
//                        mIndicator.setTabImageFouse2(msg.getData().getBoolean("isopen", false));
//                    } else {
//                        mIndicator.setTabImageFouse(msg.getData().getBoolean("isopen", false));
//                    }
                    break;
                case MSG_ID_UPDATE_TAB_NOMAL:
                    mIndicator.setTabImage(msg.getData().getBoolean("isopen", false));
                    break;
                case MSG_ID_SHOW_BALANCE:
                    showBalance();
                    break;
                default:
                    break;
            }
        }
    };
    private ImageView vs_mian_keyborad_btn;
    /**
     * 关闭键盘
     */
    private LinearLayout vs_mian_keyborad_layout;
    private ImageView vs_mian_keyborad_btn1;
    private TextView vs_mian_keyborad_tv;

    public class MyConnectionListener implements ChatClient.ConnectionListener {
        @Override
        public void onConnected() {
            Log.e(EASEMOB_TAGNAME, "连接成功");
        }

        @Override
        public void onDisconnected(final int errorCode) {
            Log.e(EASEMOB_TAGNAME, "连接失败 errorCode" + errorCode);
        }

    }

    private BroadcastReceiver mListenAdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            CustomLog.i(TAG, "mListenAdReceiver(),....");

            if (AdManager.ACTION_UPDATE_AD.equals(action)) {
                for (Fragment f : mFragments) {
                    if (f instanceof VsDialFragment) {
                        //((VsDialFragment)f).updateAd();
                    }
                }
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AdManager.ACTION_UPDATE_AD);
        registerReceiver(mListenAdReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mListenAdReceiver);
    }

    /**
     * 单聊、群聊选择Dialog
     */
    private Dialog mDialogSelect;

    private void initAd() {
        AdManager.getInstance().initAd(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.vs_activity_main);
        mBarView = findViewById(R.id.view_state_bar);
        ViewGroup.LayoutParams layoutParams = mBarView.getLayoutParams();
        layoutParams.height = StateBarUtils.getStateBarHeight(this);
        mBarView.setLayoutParams(layoutParams);

        VsUtil.savedToSDCard("登录的uid=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId), "帐号信息");
        VsUtil.savedToSDCard("登录的手机号=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber), "帐号信息");
        VsUtil.savedToSDCard("========================================================", "帐号信息");
        inittitlebar();
        initView();
        initAd();

        initEventBus();

        Intent intent = getIntent();
        indicator = intent.getIntExtra("indicator", 6);


        if (intent.hasExtra("extra_conversation")) {
            indicator = 2;
            //findViewById(R.id.).setVisibility(View.VISIBLE);
        }
        if ((intent.getData() != null) && (intent.getData().getScheme().equals("rong"))) {
            indicator = 2;
            //findViewById(R.id.custom).setVisibility(View.VISIBLE);
        }
        GlobalVariables.curIndicator = indicator;
        msg1 = intent.getStringExtra("msg1");
        firstreg = intent.getStringExtra("firstreg");
        check = intent.getStringExtra("check");
        firstbind = intent.getStringExtra("firstbind");
        CustomLog.i("beifen", "msg1====" + msg1 + "    firstreg====" + firstreg + "    check====" + check + "    firstbind====" + firstbind);
        if (msg1 == null) {
            msg1 = "";
        }
        if (firstreg == null) {
            firstreg = "";
        }
        if (check == null) {
            check = "";
        }
        if (firstbind == null) {
            firstbind = "";
        }
        setFragmentIndicator(indicator);
        // 注册显示拨号和关闭拨号广播
        IntentFilter open_close_filter = new IntentFilter();
        open_close_filter.addAction(GlobalVariables.action_open_call_btn);
        open_close_filter.addAction(GlobalVariables.action_close_call_btn);
        open_close_filter.addAction(GlobalVariables.action_is_double_btn);
        mContext.registerReceiver(openOrCloseCall, open_close_filter);
        registerReceiver();
        // curIndicator = 0;// 不然每次退出。下次进入还会是当时退出界面
        // 获取Token建立拨打连接
        phoneNumberString = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_NUM_PEOPLE, "0");//getResources().getString(R.string.phone_person_mobile);

        if (!StringUtils.isEmpty(phoneNumberString) && !phoneNumberString.equals("0")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    checkContactPermission(phoneNumberString);
                }
            }).start();
        }
        //下载启动页图片
        downLoadSplashAd();

        registerCustomServiceRecervier();

        //注册一个监听连接状态的listener
        connectionListener = new MyConnectionListener();
        ChatClient.getInstance().addConnectionListener(connectionListener);

        //registerReceiver();
        // handler.sendEmptyMessage(MSG_ID_SHOW_BALANCE);
         mFragment = (VsDialFragment) mFragments[FragmentIndicator.HOME_PAGE_PHONE];
        VsApplication.getInstance().addActivity(this);
    }

    private void registerCustomServiceRecervier() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
//        mContext.registerReceiver(unReadMsgReceiver, filter);
//        filter.addAction(ZhiChiConstant.SOBOT_NOTIFICATION_CLICK);
//        mContext.registerReceiver(nClickReceiver, filter);
    }

    //智齿通知栏
//    BroadcastReceiver nClickReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ZhiChiConstant.SOBOT_NOTIFICATION_CLICK.equals(intent.getAction())) {
//                SobotUtils.getInstance(context).startChat();
//            }
//        }
//    };

    BroadcastReceiver unReadMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            noReadNum = intent.getIntExtra("noReadCount", 0);
            VsApplication.getInstance().setNoReadMsg(noReadNum);
            String content = intent.getStringExtra("content"); //未读消息数
            setBadgeView(VsApplication.getInstance().getNoReadMsg(), indicator);
            //新消息内容
            Log.e(TAG, "未读消息条数：" + noReadNum + "新消息内容:" + content);
        }
    };

    /**
     * 注册EventBus
     */
    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void checkContactPermission(final String phoneNumberString) {
        XXPermissions.with(this).permission(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS, Permission.READ_PHONE_STATE, Permission
                .WRITE_EXTERNAL_STORAGE,Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION,
                Permission.READ_CALL_LOG).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                if (isAll) {
                    // 加载通话记录
                    if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, true)) {
                        VsPhoneCallHistory.loadCallLog();
                    }
                    //加载通讯录
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // - 额外做一次权限判断
                            String[] permission = new String[]{Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS, Permission.READ_PHONE_STATE, Permission
                                    .WRITE_EXTERNAL_STORAGE};
                            if (XXPermissions.isHasPermission(getApplicationContext(), permission)) {
                                getQueryData(getResources().getString(R.string.product_name), phoneNumberString);
                            }
                        }
                    }).start();

                } else {
                    Toast.makeText(VsMainActivity.this, "为了更好的用户体验，请授权开启通讯录功能", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {
                    Toast.makeText(VsMainActivity.this, "为了更好的用户体验，请授权开启通讯录功能", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VsMainActivity.this, "为了更好的用户体验，请授权开启通讯录功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void downLoadSplashAd() {
        //启动页广告
        String dataString = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CONFIG_0001, "");
        if (dataString.length() > 0) {
            try {
                JSONArray array = new JSONArray(dataString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    String image_url = object.getString("image_url");
                    saveUrlImg(image_url);
                    CustomLog.d("download", "下载图片: " + dataString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUrlImg(String image_url) {
        new GetImg().execute(image_url);
        CustomLog.d("download", "image_url: " + image_url);
    }

    /**
     * 将url转换成bitmap异步任务
     */
    private class GetImg extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection con = null;//访问网络
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(5 * 1000);
                /*http 响应吗
                 * 200：成功
                 * 404：未找到
                 * 500：发生错误
                 */
                if (con.getResponseCode() == 200) {
                    is = con.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }

        // onPostExecute在UI线程中执行命令
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            String path = Environment.getExternalStorageDirectory().toString() + "/fs_img/chache";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            //TODO 暂时只考虑保存一张图片
            File file = new File(path1, "splash.jpg");
            String filePath = file.getAbsolutePath();
            saveFileByBitmap(bitmap, filePath);
        }
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @param newImagePath
     */
    private void saveFileByBitmap(Bitmap bitmap, String newImagePath) {
        File file = new File(newImagePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                //保存图片路径
                VsUserConfig.setData(mContext, GlobalVariables.JKEY_AD_SPLASHIMAG_PATH, newImagePath);
                CustomLog.d("download", "保存路径:" + newImagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] columns = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract
            .CommonDataKinds.Phone.CONTACT_ID};

    private void getQueryData(String name, String number) {
        ContentResolver resolver = getContentResolver();
        //TODO 某些情况下会崩溃
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Boolean ishas = false;
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(columns[0]);
            int displayNameIndex = cursor.getColumnIndex(columns[1]);
            int id = cursor.getInt(idIndex);
            String disPlayName = cursor.getString(displayNameIndex);


            if (disPlayName != null && disPlayName.equals(getResources().getString(R.string.product_name))) {
                ishas = true;
                SharedPreferences cus_number = getSharedPreferences("number", 0);
                save_name = cus_number.getString("numberString", "1");

                if (!save_name.equals(phoneNumberString)) {

                    String h_name = getResources().getString(R.string.product_name);
                    //根据姓名求id
                    Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                    ContentResolver h_resolver = getContentResolver();
                    Cursor h_cursor = h_resolver.query(uri, new String[]{Data._ID}, "display_name=?", new String[]{h_name}, null);

                    if (h_cursor.moveToFirst()) {
                        int h_id = h_cursor.getInt(0);
                        //根据id删除data中的相应数据
                        resolver.delete(uri, "display_name=?", new String[]{h_name});
                        uri = Uri.parse("content://com.android.contacts/data");
                        resolver.delete(uri, "raw_contact_id=?", new String[]{h_id + ""});
                        ishas = false;
                    }
                }
            }
        }
        if (!ishas) {
            insert(name, number);
        }
        cursor.close();
    }

    public boolean insert(String given_name, String mobile_number) {
        try {
            ContentValues values = new ContentValues();
            // 下面的操作会根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContactId
            Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);
            String[] number = mobile_number.split(",");
            // 向data表插入姓名数据
            if (given_name != "") {
                values.clear();
                values.put(Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                values.put(StructuredName.GIVEN_NAME, given_name);
                getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            }
            for (int i = 0; i < number.length; i++) {
                // 向data表插入电话数据
                if (mobile_number != "") {
                    values.clear();
                    values.put(Data.RAW_CONTACT_ID, rawContactId);
                    values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    values.put(Phone.NUMBER, number[i]);
                    values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                }
            }

            SharedPreferences cus_number = getSharedPreferences("number", 0);
            SharedPreferences.Editor editor = cus_number.edit();
            editor.putString("numberString", phoneNumberString);
            editor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleMissedCallNotify(intent);
        super.onNewIntent(intent);
        if (SHOPPINGCART_FRAGMENT_INDICATOR.equals(mMessageEvent)) {
            mBarView.setVisibility(View.GONE);
            FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        }
    }


    private void handleMissedCallNotify(Intent intent) {
        boolean isMissCallNotify = intent.getBooleanExtra("is_miss_call_notification", false);
        CustomLog.i("VsMainActivity", "handleMissedCallNotify(), isMissCallNotify = " + isMissCallNotify);
        if (isMissCallNotify) {
            showFragment(0);
            mIndicator.setIndicator(0);
            Intent i = new Intent(VsUserConfig.JKey_CLOSE_USER_LEAD);
            // 拨号盘开启状态
            boolean isopen = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, true);
            if (isopen) {
                intent.putExtra("isopen", false);
                intent.putExtra("indicator", indicator);
            } else {
                intent.putExtra("isopen", true);
                intent.putExtra("indicator", indicator);
            }
            mContext.sendBroadcast(i);
            //findViewById(R.id.custom).setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver openOrCloseCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = VsUtil.isNull(intent.getAction()) ? "" : intent.getAction();
            // 显示拨号按钮
            if (action.equals(GlobalVariables.action_open_call_btn)) {
                showCallAnimation();
                // 隐藏拨号按钮
            } else if (action.equals(GlobalVariables.action_close_call_btn)) {
                closeCallAnimation();
                // 是否显示两个按钮（直播回拨按钮）
            } else if (action.equals(GlobalVariables.action_is_double_btn)) {
                setisCall();
            }
        }
    };

    /**
     * 初始化组件
     */
    private void initView() {
        titleView = (TextView) findViewById(R.id.sys_title_txt);
        tv_left = (ImageView) findViewById(R.id.tv_left_contact);
        tv_right = (ImageView) findViewById(R.id.tv_right_contact);

//        vs_mian_keyborad_layout= (LinearLayout) findViewById(R.id.vs_mian_keyborad_layout);  //拨号布局
//        vs_mian_keyborad_btn= (ImageView) findViewById(R.id.vs_mian_keyborad_btn);   //拨号图片
//        //拨号文字
//        vs_mian_keyborad_tv = (TextView) findViewById(R.id.vs_mian_keyborad_tv);


        badgeView = (BadgeView) findViewById(R.id.badgeView);
        rightRl = (RelativeLayout) findViewById(R.id.rl_right);

        vs_mian_contact_layout = (LinearLayout) findViewById(R.id.vs_mian_contact_layout);
        searchRl = (RelativeLayout) findViewById(R.id.rl_search);
        searchRl.setOnClickListener(this);
//         List<Fragment> lf = getSupportFragmentManager().getFragments();
//         for (Fragment fragment : lf) {
//			if (fragment instanceof VsDialFragment) {
//				mFragment = (VsDialFragment) fragment;
//				break;
//			}
//		}
        vs_mian_contact_layout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//						btn.setText("");
//						intent_del.putExtra("del", "longdel");
//						mContext.sendBroadcast(intent_del);
                mFragment.setTitleText();
                return false;
            }
        });
        ll_mybottom = (RelativeLayout) findViewById(R.id.ll_mybottom);
        mCallBtn = (Button) findViewById(R.id.btn_bottom_call);
        mIndicator = (FragmentIndicator) findViewById(R.id.indicator);

        mTitleBarView = (TitleBarView)findViewById(R.id.title_tar);
       final Button contactBtn = mTitleBarView.getContactBtn();
        contactBtn.setOnClickListener(new OnClickListener() {    //通讯录
            @Override
            public void onClick(View view) {
                ll_mybottom.setVisibility(View.GONE);
            }
        });

        // 拨号盘开启状态
        boolean isopen = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, true);
        // 初始化按钮状态
       // mIndicator.setTabImageFouse(isopen);
       // mIndicator.setTabText(isopen);
        // 设置监听事件
     //   vs_mian_keyborad_layout.setOnClickListener(this);
        vs_mian_contact_layout.setOnClickListener(this);
        mCallBtn.setOnClickListener(this);


        // mAddContactTV = (TextView) findViewById(R.id.tv_add_contact);
        //mAddContactTV.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom_call:
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                // 跳转打电话
                sendBroadcast(new Intent(GlobalVariables.action_dial_phone).setPackage(getPackageName()));
                break;

//            case R.id.vs_mian_keyborad_layout:
//                if (VsUtil.isFastDoubleClick()) {
//                    return;
//                }
//
//                    if (isShow){
//                    vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused);
//                    vs_mian_keyborad_tv.setText("拨号");
//                    isShow=false;
//
//
//                    }else {
//                        vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_down);
//                        vs_mian_keyborad_tv.setText("最近通话");
//                        isShow=true;
//                    }
//
//
////                Intent intent = new Intent(VsUserConfig.JKey_CLOSE_USER_LEAD);
////                // 切换TAB改变清空编辑框数据
////                mIndicator.setTabImageFouse(false);
////                intent.putExtra("isopen", false);
////
////                intent.putExtra("indicator", 4);
////                mContext.sendBroadcast(intent);
//                break;
            case R.id.vs_mian_contact_layout:
//                setFragmentIndicator(1);// 跳转到分类
//                setTabImage(true);
//                closeCallAnimation();
               mFragment.upTitleText();
                break;
            case R.id.rl_search:
                SkipPageUtils.getInstance(this).skipPage(SearchViewActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 显示拨打按钮动画
     */
    public void showCallAnimation() {
        if (!isShow) {
            ll_mybottom.setVisibility(View.VISIBLE);
//            isShow = true;
            setisCall();
        }
    }


    /**
     * 隐藏拨打按钮动画
     */
    public void closeCallAnimation() {
        if (isShow) {
            isShow = false;
            ll_mybottom.setVisibility(View.GONE);
        }
    }
    /**
     * 设置是否是两个按钮
     */
    public void setisCall() {
        mCallBtn.setVisibility(View.VISIBLE);
    }



    /**
     * 设置tab导航栏按钮选中状态
     *
     * @param isopen
     */
    public void setTabImageFouse(boolean isopen) {
//        if (isopen) {
//            vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_down_2);
//
//        } else {
//            vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_down_1);
//        }
        Message msg = handler.obtainMessage();
        msg.what = MSG_ID_UPDATE_TAB_FOUSE;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isopen", isopen);
        msg.setData(bundle);
        handler.sendMessage(msg);


    }

    /**
     * 更新未选中时的tab
     *
     * @param isopen
     */
    public void setTabImage(boolean isopen) {
        Message msg = handler.obtainMessage();
        msg.what = MSG_ID_UPDATE_TAB_NOMAL;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isopen", isopen);
        msg.setData(bundle);
        handler.sendMessage(msg);
//        if (isopen) {
//            vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_normal);
//        } else {
//            vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_normal_down);
//        }
    }

    /**
     * 初始化fragment
     */
    public void setFragmentIndicator(final int whichIsDefault) {
        if (null == mFragments) {
            mFragments = new Fragment[7];
            mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_classify);
            mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_dial);
            mFragments[3] = getSupportFragmentManager().findFragmentById(R.id.fragment_shoppingcart);
            mFragments[4] = getSupportFragmentManager().findFragmentById(R.id.fragment_myself);
            mFragments[5] = getSupportFragmentManager().findFragmentById(R.id.fragment_contacts_list);
            mFragments[6] = getSupportFragmentManager().findFragmentById(R.id.fragment_refuel_main);
        }

        boolean isopen = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, true);
        setView(whichIsDefault, isopen);
        showFragment(whichIsDefault);
        mIndicator.setIndicator(whichIsDefault);
        mIndicator.setOnIndicateListener(new OnIndicateListener() {
            @Override
            public void onIndicate(View v, int which) {
                // 拨号盘开启状态
                boolean isopen = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, true);
                CustomLog.i("vsdebug", "isopen=" + isopen + "-----上次选中是curIndicator：" + GlobalVariables.curIndicator + "-----本次选中是which：" + which);
                flag = GlobalVariables.curIndicator;
                showFragment(which);
                setView(which, isopen);
                indicator = which;
                GlobalVariables.curIndicator = which;
                if (which == indicator && which != 0) {
                    return;
                }
            }
        });
    }

    /**
     * 设置界面
     *
     * @param which
     * @param isopen
     */
    public void setView(int which, boolean isopen) {
        //智齿
//        setBadgeView(VsApplication.getInstance().getNoReadMsg(), which);
        //环信
        setBadgeView(getEaseMobNoReadMsgCount(), which);

        //全部通话栏是否显示
        if (which == FragmentIndicator.HOME_PAGE_PHONE) {    //电话
            findViewById(R.id.custom).setVisibility(View.GONE);
            titleMain.setVisibility(View.VISIBLE);
            mContactsTitleBarView.setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.custom).setVisibility(View.VISIBLE);
            mContactsTitleBarView.setVisibility(View.GONE);
            titleMain.setVisibility(View.GONE);
            mContactsTitleBarView.setBackgroundColor(getResources().getColor(R.color.public_color_EC6941));
        }

        //根据设置不同的标题栏
        updateTitleView(which, isopen);
    }

    /**
     * 设置标题栏
     *
     * @param which
     * @param isopen
     */
    @SuppressLint("NewApi")
    private void updateTitleView(int which, boolean isopen) {
        //状态栏  文字颜色更新
//        if(which != FragmentIndicator.HOME_PAGE_MAIN && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            getWindow().getDecorView().setSystemUiVisibility(which == FragmentIndicator.HOME_PAGE_CLASSIFY ?
//                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_VISIBLE);
//        }else if(mFragments[FragmentIndicator.HOME_PAGE_MAIN]  != null && mFragments[FragmentIndicator.HOME_PAGE_MAIN] instanceof HomeMallFragment){
//            ((HomeMallFragment)mFragments[FragmentIndicator.HOME_PAGE_MAIN]).toChangeText();
//        }
//        findViewById(R.id.custom).setBackgroundColor(Color.parseColor(which == FragmentIndicator.HOME_PAGE_CLASSIFY ? "#FFFFFF" : "#1086FF"));
 //       findViewById(R.id.custom).setBackgroundResource(which == FragmentIndicator.HOME_PAGE_CLASSIFY ? R.color.color_white : R.color.public_color_EC6941);
        findViewById(R.id.custom).setBackgroundResource(R.color.public_color_EC6941);
        findViewById(R.id.custom).setVisibility(which == FragmentIndicator.HOME_PAGE_MAIN || which == FragmentIndicator.HOME_PAGE_PHONE || which == FragmentIndicator.HOME_PAGE_REFUEL ? View.GONE : View.VISIBLE);
        switch (which) {
            case FragmentIndicator.HOME_PAGE_MAIN:   //商城
                ll_mybottom.setVisibility(View.GONE);
                titleView.setVisibility(View.GONE);
                searchRl.setVisibility(View.VISIBLE);
                rightRl.setVisibility(View.VISIBLE);
                tv_left.setVisibility(View.GONE);
                tv_right.setVisibility(View.VISIBLE);
                tv_right.setImageResource(R.drawable.message);
                ViewGroup.LayoutParams params = tv_right.getLayoutParams();
                int height = (int) getResources().getDimension(R.dimen.w_19_dip);
                int width = (int) getResources().getDimension(R.dimen.w_19_dip);
                params.height = height;
                params.width = width;
                tv_right.setLayoutParams(params);
                setTabImage(false);// 设置键盘图标
                tv_right.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                            //智齿
//                            noReadNum = 0;
//                            SobotUtils.getInstance(mContext).startChat();

                            //环信
                            if (ChatClient.getInstance().isLoggedInBefore()) {
                                Intent intent = new IntentBuilder(VsMainActivity.this).setServiceIMNumber(EASEMOB_IMSERVER).setTitleName(COMPANY_NAME).setVisitorInfo
                                        (EaseMobMsgHelper.createVisitorInfo("")).setTargetClass(ChatActivity.class).build();
                                startActivity(intent);
                            } else {
                                loginEaseMob();
                            }
                        }
                    }
                });
                break;
            case FragmentIndicator.HOME_PAGE_CLASSIFY:  //分类
                ll_mybottom.setVisibility(View.GONE);
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(getResources().getString(R.string.tab_my_order));
                tv_left.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                badgeView.setVisibility(View.GONE);
                searchRl.setVisibility(View.GONE);
                setTabImage(false);// 设置键盘图标
                break;
            case FragmentIndicator.HOME_PAGE_PHONE:  //拨号+通讯录
//                if (isopen=true){
//                    ll_mybottom.setVisibility(View.VISIBLE);
//                    isopen=false;
//                }else {
//                    ll_mybottom.setVisibility(View.VISIBLE);
//                    isopen=true;
//                }
                ll_mybottom.setVisibility(View.VISIBLE);

                badgeView.setVisibility(View.GONE);
                mContactsTitleBarView.setBackgroundColor(getResources().getColor(R.color.public_color_EC6941));
                Intent intent = new Intent(VsUserConfig.JKey_CLOSE_USER_LEAD);
                if (GlobalVariables.curIndicator == FragmentIndicator.HOME_PAGE_PHONE) {
                   // mIndicator.setTabText(isopen);
                    // 切换TAB改变清空编辑框数据
                    if (isopen) {
                        intent.putExtra("isopen", false);
                        intent.putExtra("indicator", indicator);
                    } else {

                        intent.putExtra("isopen", true);
                        intent.putExtra("indicator", indicator);
                    }
                } else {
                    setTabImageFouse(isopen);
                }
                mContactsTitleBarView.setTextMms(0, false); // 更新信息
                mContext.sendBroadcast(intent);
                break;
            case FragmentIndicator.HOME_PAGE_SHOPPING:  //购物车
                ll_mybottom.setVisibility(View.GONE);
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(getResources().getString(R.string.tab_shoppingcart));
                tv_left.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                badgeView.setVisibility(View.GONE);
                searchRl.setVisibility(View.GONE);
                setTabImage(false);// 设置键盘图标
                break;
            case FragmentIndicator.HOME_PAGE_MINE:  //个人中心
                ll_mybottom.setVisibility(View.GONE);
                findViewById(R.id.custom).setVisibility(View.GONE);
                badgeView.setVisibility(View.GONE);
                titleView.setVisibility(View.GONE);
                tv_left.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);
                setTabImage(false);// 设置键盘图标
                if (GlobalVariables.curIndicator != 3) {
                    // 发送查询余额广播
                    mContext.sendBroadcast(new Intent(VsUserConfig.JKEY_SEARCH_BALANCE));
                }
                break;
                case FragmentIndicator.HOME_PAGE_REFUEL:
                    ll_mybottom.setVisibility(View.GONE);
                    titleView.setVisibility(View.GONE);
 //                   titleView.setText("嘟嘟加油");
                    tv_left.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);
                    badgeView.setVisibility(View.GONE);
                    searchRl.setVisibility(View.GONE);
                    setTabImage(false);// 设置键盘图标
                    break;
            default:
                break;
        }
    }




//    /**
//     * 设置键盘开关按钮状态
//     */
//    public void setTabImageFouse(boolean isopen) {
//
//    }

//    /**
//     * 设置键盘开关按钮你状态
//     */
//    public void setTabImageFouse2(boolean isopen) {
//        // 直接默认
//        vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_down_2);
//        animDown = AnimationUtils.loadAnimation(mContext, R.anim.call_tab_logo_anim_down);
//        LinearInterpolator lin = new LinearInterpolator();
//        animDown.setInterpolator(lin);
//        animDown.setFillAfter(true);
//        animUp = AnimationUtils.loadAnimation(mContext, R.anim.call_tab_logo_anim_up);
//        LinearInterpolator linUp = new LinearInterpolator();
//        animUp.setInterpolator(linUp);
//        animUp.setFillAfter(true);
//
//        if (isopen) {
//            vs_mian_keyborad_btn.startAnimation(animDown);
//
//        } else {
//            vs_mian_keyborad_btn.startAnimation(animUp);
//
//        }
//
//    }


    private void loginEaseMob() {
        String uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId);
        if (!StringUtils.isEmpty(uid)) {
            ChatClient.getInstance().login(uid, EASEMOB_DEFAULT_PASSWORD, new com.hyphenate.helpdesk.callback.Callback() {
                @Override
                public void onSuccess() {
                    Log.e(EASEMOB_TAGNAME, "demo login succedd");
                    Intent intent = new IntentBuilder(VsMainActivity.this).setTargetClass(ChatActivity.class).setServiceIMNumber(EASEMOB_IMSERVER).setVisitorInfo
                            (EaseMobMsgHelper.createVisitorInfo("")).setTitleName(COMPANY_NAME).build();
                    startActivity(intent);
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login fail");
                }

                @Override
                public void onProgress(int i, String s) {
                    Log.e(EASEMOB_TAGNAME, "demo login progress");
                }
            });
        } else {
            ToastUtils.show(VsMainActivity.this, "登录后方可联系客服");
        }
    }

    private void setBadgeView(int shoppingCartcount, int which) {
        if (shoppingCartcount > 0 && which == 0) {
            badgeView.setVisibility(View.VISIBLE);
            badgeView.setText(shoppingCartcount + "");
            if (shoppingCartcount > 10) {
                badgeView.setText("10+");
            }
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }

    /**
     * 添加联系人
     */
    public void inittitlecontact() {
        rightRl.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setImageResource(R.drawable.add_title);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VsUtil.addContact(mContext, "");
            }
        });
    }

    /**
     * 隐藏右边添加联系人按键
     */
    public void setRightTvHide() {
        rightRl.setVisibility(View.GONE);
        tv_right.setVisibility(View.GONE);
    }

    private void inittitlebar() {
        titleMain = (LinearLayout) findViewById(R.id.title_main);
        mContactsTitleBarView = (TitleBarView) findViewById(R.id.title_tar);
        mContactsTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
        mContactsTitleBarView.setTitleLeft(R.string.vs_contacts_all);//联系人按钮
        mContactsTitleBarView.setTitleRight(R.string.vs_msm_all);//信息按钮

        mContactsTitleBarView.getBtnLeft().setVisibility(View.INVISIBLE);

        mContactsTitleBarView.getTitleLeft().setEnabled(false);
        mContactsTitleBarView.getTitleRight().setEnabled(true);
        changRight(true);
        mContactsTitleBarView.getTitleLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContactsTitleBarView.getTitleLeft().isEnabled()) {
                    mContactsTitleBarView.getTitleLeft().setEnabled(false);
                    mContactsTitleBarView.getTitleRight().setEnabled(true);
                }
                changRight(true);
                setFragmentIndicator(FragmentIndicator.HOME_PAGE_PHONE);// 跳转到联系人
                setTabImage(true);
                closeCallAnimation();
            }
        });//左边按钮

        mContactsTitleBarView.getTitleRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContactsTitleBarView.getTitleRight().isEnabled()) {
                    mContactsTitleBarView.getTitleLeft().setEnabled(true);
                    mContactsTitleBarView.getTitleRight().setEnabled(false);
                }
                changRight(false);
                setFragmentIndicator(FragmentIndicator.HOME_PAGE_PHONE);// 跳转信息界面
            }
        });//右边按钮
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void changRight(boolean isright) {
        if (isright) {
            mContactsTitleBarView.setTxtRightShow();
            mContactsTitleBarView.getRightBtn().setVisibility(View.GONE);
//    		mContactsTitleBarView.setTxtRight(R.string.vs_dial_clog_add);
            mContactsTitleBarView.getTxtRight().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VsUtil.addContact(mContext, "");
                }
            });
        } else {
            mContactsTitleBarView.getRightBtn().setImageResource(R.drawable.vs_invite_add_selector);
            mContactsTitleBarView.getRightBtn().setVisibility(View.VISIBLE);
            mContactsTitleBarView.setTxtRightHide();
            mContactsTitleBarView.getRightBtn().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//					 showSelectDialog();//单聊群聊					
                }

            });
        }
    }


    @SuppressLint("ResourceType")
    public void showFragment(int which) {
       /* if(which == FragmentIndicator.HOME_PAGE_REFUEL) {
            ImmersionBar.with(this).statusBarDarkFont(false).init();
        }else {
            mBarView.setBackgroundResource(R.color.public_color_EC6941);
            ImmersionBar.with(this).statusBarDarkFont(false).init();
        }*/
        mBarView.setVisibility(which == FragmentIndicator.HOME_PAGE_MAIN || which == FragmentIndicator.HOME_PAGE_REFUEL ? View.GONE : View.VISIBLE);

        mBarView.setBackgroundResource(R.color.public_color_EC6941);
        ImmersionBar.with(this).statusBarDarkFont(false).init();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

         fragmentTransaction.hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).hide(mFragments[4]).hide(mFragments[5]).hide(mFragments[6]).show(mFragments[which]).commitAllowingStateLoss();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent i= new Intent(Intent.ACTION_MAIN);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.addCategory(Intent.CATEGORY_HOME);
//            startActivity(i);
//            return true;
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notiManager.cancel(GlobalVariables.NotificationID);
        VsUserConfig.setData(mContext, VsUserConfig.is_show, false);

        if (GlobalVariables.curIndicator != indicator) {
            setFragmentIndicator(GlobalVariables.curIndicator);
            setTabImage(VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, true));// 设置键盘图标
        }
        indicator = GlobalVariables.curIndicator;


//        if (indicator == FragmentIndicator.HOME_PAGE_PHONE) {     //当前Fragment 位置处于通话 ???  就不考虑下通用性 ???
//////            setTabImage(true);
////           // mIndicator.setTabImageFouse2(true);
////        } else {
////            setTabImage(false);
////        }

        CustomLog.i(TAG, "VsPhoneCallHistory.CONTACTLIST.size() = " + VsPhoneCallHistory.CONTACTLIST.size());
        super.onResume();
        CustomLog.i(TAG, "MainFragment------onResume(),...");

        Log.e(TAG, "主界面未读客服消息msg===>" + VsApplication.getInstance().getNoReadMsg() + "");

//        setBadgeView(VsApplication.getInstance().getNoReadMsg(), indicator);  //智齿

        setBadgeView(getEaseMobNoReadMsgCount(), indicator);  //环信

        //3-11 每次页面显示时 判断是否显示小红点
        onShoppingChange(null);
    }

    private int getEaseMobNoReadMsgCount() {
        int noReadMsgCount = 0;
        if (ChatClient.getInstance().isLoggedInBefore()) {
            Conversation conversation = ChatClient.getInstance().chatManager().getConversation(EASEMOB_IMSERVER);
            noReadMsgCount = conversation.unreadMessagesCount();
        }
        Log.e(EASEMOB_TAGNAME, TAG + "未读消息msg===》" + noReadMsgCount);
        return noReadMsgCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CustomLog.i(TAG, "onActivityResult(),.................");
        /**使用SSO授权必须添加如下代码 */
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /*private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AdManager.ACTION_UPDATE_AD);
        registerReceiver(mListenAdReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mListenAdReceiver);
    }*/

    @Override
    protected void onPause() {
        VsUserConfig.setData(mContext, VsUserConfig.is_show, true);
        VsUtil.unregisterCallNumberBC(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (openOrCloseCall != null) {
            mContext.unregisterReceiver(openOrCloseCall);
            openOrCloseCall = null;
        }
        unregisterReceiver();
        if (unReadMsgReceiver != null) {
            try {
                mContext.unregisterReceiver(unReadMsgReceiver);
            } catch (Exception e) {
                Log.e("TAG-A", "Exception - unReadMsgReceiver 错误");
            }
        }

//        if (nClickReceiver != null) {
//            mContext.unregisterReceiver(nClickReceiver);
//        }

        unRegisterEventBus();

        if (connectionListener != null) {
            ChatClient.getInstance().removeConnectionListener(connectionListener);
        }
        super.onDestroy();
    }

    /**
     * 注销EventBus
     */
    private void unRegisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 订阅方法，当接收到事件的时候，会调用该方法
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        Log.e(TAG, "接收到eventBus msg===>" + messageEvent.getMessage());
        mMessageEvent = messageEvent.getMessage();
        if (SALEMALL_FRAGMENT_INDICATOR.equals(messageEvent.getMessage())) {   //商城
            mIndicator.setIndicator(0);
            showFragment(0);
            findViewById(R.id.custom).setVisibility(View.GONE);
            rightRl.setVisibility(View.VISIBLE);
            searchRl.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setImageResource(R.drawable.shoppingcart_homepage);
        } else if (SALEMALL_FRAGMENT_NOMSG.equals(messageEvent.getMessage())) {    //没有未读消息
            if (badgeView != null) badgeView.setVisibility(View.GONE);
        } else if (SHOPPINGCART_FRAGMENT_INDICATOR.equals(messageEvent.getMessage())) {  //购物车
            mIndicator.setIndicator(FragmentIndicator.HOME_PAGE_SHOPPING);
            setView(FragmentIndicator.HOME_PAGE_SHOPPING,false);
            showFragment(FragmentIndicator.HOME_PAGE_SHOPPING);
            findViewById(R.id.custom).setVisibility(View.VISIBLE);
            rightRl.setVisibility(View.GONE);
            searchRl.setVisibility(View.GONE);
            tv_right.setVisibility(View.GONE);
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getResources().getString(R.string.tab_shoppingcart));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventV2(EventBusMsg msg) {
//        Log.e(TAG, "接收到eventBus msg===>" + msg.getMap().get("NoReadMsg"));
//    }

    /**
     * 3-11
     * 接收event事件 判断是否需要显示小红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShoppingChange(ShoppingChangeEvent event) {
        View viewById = findViewById(R.id.indicator);
        if (viewById != null && viewById instanceof FragmentIndicator) {
            ((FragmentIndicator) viewById).onShoppingRefresh();
        }
    }

    /**
     * @instruction 消息提醒
     * @author 黄发兴
     * @version 创建时间：2014-12-14 下午03:57:28
     */
    int unRedMessageCount;

    /**
     * @instruction 好友列表被清空时再加载
     * @author Sundy 兴
     * @version 创建时间：2014-12-18 下午01:19:25
     */
    public void getFriends() {
        CustomLog.i(TAG, "进入取本地保存Vs好友");
//        ConnectionIm.getInstance().changeContactsData(mContext, VsUserConfig.getDataString
// (mContext, VsUserConfig.JKEY_GETVSUSERINFO));
    }


    private void showBalance() {
        String blance = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_BALANCE_SAVE);
        String vaild_date = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_VALID_DATE);
        // 判断是否有余额
        if (blance != null && !"".equals(blance) && Float.valueOf(blance) > 0) {// 余额不为零
            int day = VsUtil.compareDate(mContext, vaild_date);
            if (day != 0 && day == 3) {
                VsUtil.showYesNoDialog(mContext, null, "余额还有三天到期", getResources().getString(R.string.vs_start_login_str), getResources().getString(R.string.vs_cannel), null, null);
            }
        } else if (Float.valueOf(blance) == 0) {// 余额为零
            if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_BALANCE_HINT, false)) {
                final CustomDialog2.Builder builder = new CustomDialog2.Builder(mContext);
                final CustomDialog2 dialog = builder.create();
                builder.getDialog_call().setVisibility(View.GONE);
                builder.getDialog_call_line().setVisibility(View.GONE);
                builder.getDialog_inivt().setVisibility(View.GONE);
                builder.getDialog_inivt_line().setVisibility(View.GONE);
                builder.getDialogMessage().setText("余额为0");
                builder.getDialog_chioce().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_BALANCE_HINT, false)) {
                            VsUserConfig.setData(mContext, VsUserConfig.JKEY_BALANCE_HINT, false);
                        } else {
                            VsUserConfig.setData(mContext, VsUserConfig.JKEY_BALANCE_HINT, true);
                        }
                    }
                });
                dialog.show();
            }
        }
    }

}