package com.weiwei.base.activity.me;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hwtx.dududh.R;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.MyButton;
import com.weiwei.base.util.MyButton.CountDownOnClickListener;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.salemall.activity.OrderEnsureActivity.JSON_TYPE;
import static com.weiwei.salemall.base.Const.REQUEST_CODE;

/**
 * @author Created by EDZ on 2018/10/9.
 *         Describe 提现弹框
 */

public class VsMyRedPopActivity extends Activity {

    private String uid = "";
    private EditText money_get_edit;
    private MyButton money_btn;
    private ImageView money_img;
    private TextView weixin;
    private Boolean time_set = true;
    private String uiFlag;
    private String invitation;

    private static final String TAG = "VsMyRedPopActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_red_popwindow);
        uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        uiFlag = getIntent().getStringExtra("uiFlag");
        invitation = getIntent().getStringExtra("invitation");
        init();
        String wx_id = VsUserConfig.getDataString(this, VsUserConfig.JKey_Weixin);
        if (null != wx_id && wx_id.length() > 0) {
            weixin.setText("已绑定");
        } else {
            weixin.setText("未绑定");
        }
        IntentFilter mycalllogFilter = new IntentFilter();
        mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_REDMONEY);
        getApplicationContext().registerReceiver(mycalllogReceiver, mycalllogFilter);

        money_btn.setCountDownOnClickListener(new CountDownOnClickListener() {
            @Override
            public void onClickListener() {
                getMoney(uiFlag);
            }
        });

        money_get_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });

        money_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        VsApplication.getInstance().addActivity(this);
    }

    private void init() {
        money_get_edit = (EditText) findViewById(R.id.money_get_edit);
        money_btn = (MyButton) findViewById(R.id.money_btn);
        money_img = (ImageView) findViewById(R.id.money_img);
        weixin = (TextView) findViewById(R.id.weixin);
    }

    /**
     * 提现
     * 返回数据  {"uid": "123456", "app_id": "wind", "sign": "7574cbeea5c3fee044e22173f1d3a6b0",
     * "agent_id": "", "remark": "test", "pv": "iphone", "ts": 1453445299, "amount": 1000,
     * "token": "C-US
     * .V36WC6K06J1_IT0Q4XITKK9T-5_J2SKEXXKWS8KRCJCD7B09RTMKUVULMCN3JEQXI36H1JEY58Z2UYBWIPL
     * .G_8-J6TZ0V43UGF4TCHES6BGU24S3JUD3DHC3MX", "v": "1.0.0"}
     *
     * @param uiFlag
     */
    private void getMoney(String uiFlag) {
        String amount = money_get_edit.getText().toString().trim();
        switch (uiFlag) {
            case "redbag":
                TreeMap<String, String> treeMap = new TreeMap<String, String>();
                if (!TextUtils.isEmpty(amount) && amount.length() != 0 && Double.parseDouble(amount) >= 1) {
                    amount = (Double.parseDouble(amount) * 100 + "").substring(0, (Double.parseDouble(amount) * 100 + "").indexOf("."));
                    treeMap.put("uid", uid);
                    treeMap.put("amount", amount);
                    treeMap.put("remark", "");
                    CoreBusiness.getInstance().startThread(getApplicationContext(), GlobalVariables.GetMyRedMoney, DfineAction.authType_AUTO, treeMap, GlobalVariables
                            .actionGetMyRedMoney);
                    money_btn.startCountDown("提交中", "提交");
                } else {
                    Toast.makeText(getApplicationContext(), "提现金额不能少于1", Toast.LENGTH_SHORT).show();
                }
                break;
            case "shareRedbag":
                drawToWeChat(amount);
                break;
            default:
                break;
        }
    }

    /**
     * 提现到微信
     *
     * @param amount
     */
    private void drawToWeChat(String amount) {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call;
        RequestBody requestBody;
        String changeStr;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, Object> params = new HashMap<>();
        if (amount != null && amount.length() != 0 && Double.parseDouble(amount) > 0 && Double.parseDouble(amount) < Double.parseDouble(invitation)) {
            params.put("appId", DfineAction.brand_id);
            params.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
            params.put("amount", amount);
            params.put("way", "wxpay_draw");
            money_btn.startCountDown("提交中", "提交");
        } else {
            Toast.makeText(getApplicationContext(), "输入的金额有误", Toast.LENGTH_SHORT).show();
            return;
        }
        changeStr = gson.toJson(params);
        requestBody = RequestBody.create(JSON_TYPE, changeStr);
        call = api.draw(requestBody);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                Toast.makeText(VsMyRedPopActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                if (REQUEST_CODE == result.getCode()) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private BroadcastReceiver mycalllogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //重复 -- 原因未知
            Bundle bundle = intent.getExtras();
            String total = bundle.get("list").toString();
            ToastAstrictUtils.getInstance().show(total);
//            Toast.makeText(getApplicationContext(), total, Toast.LENGTH_SHORT).show();
            if (total.contains("成功")) {
                VsUserConfig.setData(getApplicationContext(), VsUserConfig.JKey_tiem_set, getTime());
                finish();
            }
        }
    };

    private long getTime() {
        Calendar calendar = Calendar.getInstance();
        String time = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()).toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date;
        long time1 = 0;
        try {
            date = format.parse(time);
            time1 = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time1;
    }
}