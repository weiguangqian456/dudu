package com.weiwei.home.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwtx.dududh.R;
import com.weiwei.account.GrowMoneyActivity;
import com.weiwei.base.activity.me.KcMyQcodeActivity;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.base.BaseActivity;
import com.weiwei.home.entity.CustomShareEntity;
import com.weiwei.home.entity.IncomeEntity;
import com.weiwei.home.entity.SeckillTab;
import com.weiwei.home.entity.UserEntity;
import com.weiwei.home.fragment.CustomShareDialog;
import com.weiwei.home.test.RetrofitCallback;
import com.weiwei.home.test.ShareListener;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitUtils;

import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeMoneyActivity extends BaseActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_personNum)
    TextView tvPersonNum;
    @BindView(R.id.tv_red)
    TextView tvRed;
    @BindView(R.id.tv_total)
    TextView tvTotal;

    private String uid = "";
    private String phone = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_make_money;
    }

    @Override
    protected void initView() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText(mContext.getResources().getString(R.string.make_money));

        getData();
    }

    private void getData() {
        uid = VsUserConfig.getDataString(this, VsUserConfig.JKey_KcId, "");
        phone = VsUserConfig.getDataString(this,VsUserConfig.JKey_PhoneNumber,"");
 //       LogUtils.e("uid:",uid);
        final Map<String,String> params = new HashMap<>();
        params.put("uid",uid);
        params.put("appId","dudu");
        params.put("phone",phone);

        RetrofitUtils.getInstance().inviteIncome(params).enqueue(new RetrofitCallback<ResultEntity>() {
            @Override
            protected void onNext(ResultEntity result) {
                IncomeEntity incomeEntity = JSON.parseObject(result.getData().toString(),IncomeEntity.class);
//                LogUtils.e("incomeEntity:",incomeEntity.toString());
                tvRed.setText(incomeEntity.getRed());
                tvTotal.setText(incomeEntity.getAmount());
            }
        });

       RetrofitUtils.getInstance().getUserLevel(params)
               .enqueue(new Callback<ResultEntity>() {
                   @Override
                   public void onResponse(Call<ResultEntity> call, final Response<ResultEntity> response) {
                       if(response.body() != null && response.body().getData() != null) {
                           UserEntity userEntity = JSON.parseObject(response.body().getData().toString(),UserEntity.class);
                           params.put("level",userEntity.getLevel()+"");
                           LogUtils.e("level",userEntity.getLevel() + "");
                           params.put("pageSize","0");
                           RetrofitUtils.getInstance().inviteNum(params).enqueue(new Callback<SeckillTab>() {
                               @Override
                               public void onResponse(Call<SeckillTab> call, Response<SeckillTab> response) {
                                   if(response.body() != null) {
                                       tvPersonNum.setText(response.body().total + "");
                                   }
                               }

                               @Override
                               public void onFailure(Call<SeckillTab> call, Throwable t) {

                               }
                           });
                       }
                   }

                   @Override
                   public void onFailure(Call<ResultEntity> call, Throwable t) {

                   }
               });
    }

    @OnClick({R.id.iv_invite,R.id.iv_line_btn,R.id.rl_account,R.id.ll_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_invite:
                toShare();
                break;
            case R.id.iv_line_btn:
                ArmsUtils.startActivity(mContext, KcMyQcodeActivity.class);
                break;
            case R.id.rl_account:
            case R.id.ll_card:
                ArmsUtils.startActivity(mContext, GrowMoneyActivity.class);
                break;
        }
    }

    private void toShare() {
        if (getFragmentManager() == null) {
            ToastAstrictUtils.getInstance().show("分享异常 - 05F");
        }
        String product = DfineAction.RES.getString(R.string.product);
        String url = "";
        String content = shareContent();
        if (content.indexOf("http") > 0) {
            url = content.substring(content.indexOf("http"));
        }
        Bitmap iconMap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        CustomShareDialog
                .getInstance()
                .removeShare(CustomShareDialog.SHARE_TYPE_SINA)
                .removeShare(CustomShareDialog.SHARE_TYPE_QZONE)
                .initEntity(new CustomShareEntity(content, url, product, compressBitmap(iconMap)))
                .setShareActivity(new ShareListener())
                .show(getSupportFragmentManager(),"");
    }
    /**
     * 设置分享内容
     *
     * @return
     */
    public String shareContent() {
        // 推荐好友
        String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
        if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
            mRecommendInfo = DfineAction.InviteFriendInfo;
        }
        return mRecommendInfo;
    }
    /**
     * 压缩图片
     *
     * @param bitMap
     */
    private Bitmap compressBitmap(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 99;
        int newHeight = 99;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        return newBitMap;
    }

}
