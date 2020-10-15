package com.weiwei.base.activity.me;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.util.CircleImageView;
import com.weiwei.base.util.CustomAlertDialog;
import com.weiwei.home.fragment.DateSelectDialog;
import com.weiwei.home.fragment.ImageGetDialog;
import com.weiwei.home.utils.ImageFileUtils;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

public class VsMyInfoTextActivity extends VsBaseActivity implements OnClickListener {

    //我的头像
    private RelativeLayout vs_myselft_infolayout;
    //我的昵称
    private RelativeLayout rl_my_nickname;
    //我的账户
    private RelativeLayout rl_my_account;
    //我的二维码
    private RelativeLayout rl_my_qrcodeimg;
    //我的邮箱
    private RelativeLayout rl_my_mailbox;
    //性别
    private RelativeLayout rl_my_sex;
    //我的生日
    private RelativeLayout vs_my_birthday;
    //我的地址
    private RelativeLayout vs_my_address;


    //头像设置
    private CircleImageView vs_myselft_infoimgView;

    /**
     * 文本类设置
     */
    //昵称设置
    private TextView my_nickname_text;
    //账户设置
    private TextView my_account_text;

    //邮箱设置
    private TextView vs_my_mailbox_text;
    //性别设置
    private TextView vs_my_sex_text;
    //生日设置
    private TextView vs_my_birthday_text;
    //地址设置
    private TextView vs_my_address_text;

    private ImageView imgright;

    private ArrayList<View.OnClickListener> click_listener;
    private CustomAlertDialog dialog;
    public static final int PHOTO_REQUEST_TAKEPHOTO = 7000;
    public static final int PHOTO_REQUEST_GALLERY = 7001;
    public static final int PHOTO_REQUEST_GALLERY_IMAGE = 7002;
    private Uri photoUri;
    private File file_pto;
    private String uid;

    private String mPhotoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_myinfo_text);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getResources().getString(R.string.vs_myinfo_text_title));
        showLeftNavaBtn(R.drawable.icon_back);
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
        init();
        String icon = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Icon);
        String name = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname);
        String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Mobile);
        String sex = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Gender);
        String bir = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Birth);
        String province = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Province);
        String city = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_City);
        String mailbox = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_MailBox);
        String account = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
        if (name.length() != 0) {
            my_nickname_text.setText(name);
        }
        if (sex.length() != 0) {
            vs_my_sex_text.setText(sex);
        }
        if (account.length() != 0) {
            my_account_text.setText(account);
        }
        if (mailbox.length() != 0) {
            vs_my_mailbox_text.setText(mailbox);
        }
        if (bir.length() > 6) {
            vs_my_birthday_text.setText(bir);
        }
        if (province.length() != 0 || city.length() != 0) {
            vs_my_address_text.setText(province + " " + city);
        }
//		my_nickname_text.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//		my_balance_phone.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));

//		String getuid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
//        String path = "";
//        if (uid != null && uid.length() > 0) {
//            path = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_photo, "");
//        }
//
//        if (fileIsExists(path) && path.contains(uid)) {
//            vs_myselft_infoimgView.setImageBitmap(BitmapFactory.decodeFile(path));
//        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(DfineAction.ACTION_GETTIME);
        mContext.registerReceiver(gettimeReceiver, filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(DfineAction.ACTION_PUSHINFOSUCCESS);
        mContext.registerReceiver(getInfoReceiver, filter1);
        Glide.with(mContext)
                .load(new File(ImageFileUtils.mSaveHeadPortraitPath))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.myself_head)
                        .skipMemoryCache(true)//不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE))//不使用本地缓存
                .into(vs_myselft_infoimgView);
        VsApplication.getInstance().addActivity(this);
    }

    private void init() {
        vs_myselft_infolayout = (RelativeLayout) findViewById(R.id.vs_myselft_infolayout);
        rl_my_account = (RelativeLayout) findViewById(R.id.rl_my_account);
        rl_my_nickname = (RelativeLayout) findViewById(R.id.rl_my_nickname);
        rl_my_qrcodeimg = (RelativeLayout) findViewById(R.id.rl_my_qrcodeimg);
        imgright = (ImageView)findViewById(R.id.imgright);
        String yrn = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Invite_person);
        if ("n".equals(yrn)) {
            rl_my_qrcodeimg.setVisibility(View.GONE);
            rl_my_account.setVisibility(View.GONE);
        }
        rl_my_mailbox = (RelativeLayout) findViewById(R.id.rl_my_mailbox);
        rl_my_sex = (RelativeLayout) findViewById(R.id.rl_my_sex);
        vs_my_birthday = (RelativeLayout) findViewById(R.id.vs_my_birthday);
        vs_my_address = (RelativeLayout) findViewById(R.id.vs_my_address);


        vs_myselft_infoimgView = (CircleImageView) findViewById(R.id.vs_myselft_infoimgView);
        my_account_text = (TextView) findViewById(R.id.my_account_text);
        my_nickname_text = (TextView) findViewById(R.id.my_nickname_text);
        vs_my_mailbox_text = (TextView) findViewById(R.id.vs_my_mailbox_text);
        vs_my_sex_text = (TextView) findViewById(R.id.vs_my_sex_text);
        vs_my_birthday_text = (TextView) findViewById(R.id.vs_my_birthday_text);
        vs_my_address_text = (TextView) findViewById(R.id.vs_my_address_text);


        vs_myselft_infolayout.setOnClickListener(this);
        rl_my_account.setOnClickListener(this);
        rl_my_nickname.setOnClickListener(this);
        rl_my_qrcodeimg.setOnClickListener(this);
        rl_my_mailbox.setOnClickListener(this);
        rl_my_sex.setOnClickListener(this);
        vs_my_birthday.setOnClickListener(this);
        vs_my_address.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(VsMyInfoTextActivity.this, VsMyTextDetailActivity.class);
        switch (v.getId()) {
            case R.id.vs_myselft_infolayout:

//                // TODO Auto-generated method stub
//                click_listener = new ArrayList<View.OnClickListener>();
//                // 删除通话记录
//                click_listener.add(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (VsUtil.isFastDoubleClick()) {
//                            return;
//                        }
//                        onTakePicClickListener(v);
//                        dialog.dismiss();
//                    }
//                });
//
//
//                click_listener.add(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        onChoosePicClickListener(v);
//                        dialog.dismiss();
//
//                    }
//                });
//
//                //取消
//                click_listener.add(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//
//                    }
//                });
//                dialog = VsUtil.showChoose(mContext, "请选择图片来源", 7, click_listener);
                ImageGetDialog dialog = new ImageGetDialog();
                dialog.setOnGetPath(new ImageGetDialog.OnImagePathListener() {
                    @Override
                    public void strPath(String strPath) {
                        mPhotoPath = strPath;
                    }
                });
                FragmentManager fragmentManager = getFragmentManager();
                dialog.show(fragmentManager,"IMAGE");
                break;

            case R.id.rl_my_nickname:

                intent.putExtra("flag", "name");
                String name = my_nickname_text.getText().toString();
                if (name.equals("未设置")) {
                    name = "";
                }
                intent.putExtra("oldName", name);
                startActivityForResult(intent, 1000);
                break;

            case R.id.rl_my_qrcodeimg:
                Intent intent_qrcode = new Intent(mContext, KcMyQcodeActivity.class);
                String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
                intent_qrcode.putExtra("code", mRecommendInfo);
                startActivity(intent_qrcode);

                break;

            case R.id.rl_my_sex:
                intent.putExtra("flag", "sex");
                String sex = vs_my_sex_text.getText().toString();
                if (sex.equals("未设置")) {
                    sex = "";
                }
                intent.putExtra("oldName", sex);
                startActivityForResult(intent, 2000);

                break;
            case R.id.vs_my_birthday:
//        	intent.putExtra("flag", "bir");
//        	String bir = vs_my_birthday_text.getText().toString();
//        	if (bir.equals("未设置")) {
//        		Calendar c = Calendar.getInstance(); 
//        		bir = c.get(Calendar.YEAR)+"-"+c.get(Calendar.MONTH)+1+"-"+c.get(Calendar.DAY_OF_MONTH);
//			}
//        	intent.putExtra("oldName", bir);
//        	startActivityForResult(intent, 3000);

            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CANADA);
            DateSelectDialog
                    .getInstance()
                    .setInitTime(df.format(date))
                    .show(getFragmentManager(),"TAG-A");

//                final DatePickerPopWindow popWindow = new DatePickerPopWindow(VsMyInfoTextActivity.this, df.format(date));
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 0.5f;
//                getWindow().setAttributes(lp);
//                popWindow.showAtLocation(mContext.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
//                popWindow.setOnDismissListener(new OnDismissListener() {
//
//                    @Override
//                    public void onDismiss() {
//                        // TODO Auto-generated method stub
//                        WindowManager.LayoutParams lp = getWindow().getAttributes();
//                        lp.alpha = 1f;
//                        getWindow().setAttributes(lp);
//                    }
//                });
                break;
            case R.id.vs_my_address:
                intent.putExtra("flag", "area");
                String area = vs_my_address_text.getText().toString();
                if (area.equals("未设置")) {

                    area = "";
                }
                intent.putExtra("oldName", area);
                startActivityForResult(intent, 4000);

                break;
            case R.id.rl_my_mailbox:
                intent.putExtra("flag", "mailbox");
                String mailbox = vs_my_mailbox_text.getText().toString();
                if (mailbox.equals("未设置")) {

                    mailbox = "";
                }
                intent.putExtra("oldName", mailbox);
                startActivityForResult(intent, 5000);

                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = null;
        if(requestCode == 1000 ||
                requestCode == 2000 ||
                requestCode == 3000 ||
                requestCode == 4000 ||
                requestCode == 5000 ){
            if(data != null){
                bundle = data.getExtras();
            }
        }
        switch (requestCode){
            case 1000:
                if(bundle == null){break;}
                my_nickname_text.setText(bundle.getString("name"));
                break;
            case 2000:
                if(bundle == null){break;}
                vs_my_sex_text.setText(bundle.getString("sex"));
                break;
            case 3000:
                if(bundle == null){break;}
                vs_my_birthday_text.setText(bundle.getString("bir"));
                break;
            case 4000:
                if(bundle == null){break;}
                vs_my_address_text.setText(bundle.getString("area"));
                break;
            case 5000:
                if(bundle == null){break;}
                vs_my_mailbox_text.setText(bundle.getString("mailbox"));
                break;
            case ImageGetDialog.REQUEST_TAKE_PHOTO:
                Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
                if(resultCode == Activity.RESULT_OK && bitmap != null){
                    Glide.with(this).load(bitmap).into(vs_myselft_infoimgView);
                    ImageFileUtils.threadSaveBitmapFile(bitmap,ImageFileUtils.mHeadPortraitName);
//                    ToastUtils.show(mContext,"抱歉，现在还不可以换头像哦~");
                }
                break;
            case ImageGetDialog.REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK &&
                        data != null &&
                        data.getData() != null){
                    Uri imageUri = data.getData();
                    ImageFileUtils.saveUriFile(imageUri,ImageFileUtils.mHeadPortraitName);
                    Glide.with(this).load(imageUri).into(vs_myselft_infoimgView);
//                    ToastUtils.show(mContext,"抱歉，现在还不可以换头像哦~");
                }
                break;
            default:
                break;
        }
        /*
        if (requestCode == 1000 && data != null) {
            Bundle bundle = data.getExtras();
            my_nickname_text.setText(bundle.getString("name"));

        }
        if (requestCode == 2000 && data != null) {
            Bundle bundle = data.getExtras();
            vs_my_sex_text.setText(bundle.getString("sex"));
        }
        if (requestCode == 3000 && data != null) {
            Bundle bundle = data.getExtras();
            vs_my_birthday_text.setText(bundle.getString("bir"));
        }
        if (requestCode == 4000 && data != null) {
            Bundle bundle = data.getExtras();
            vs_my_address_text.setText(bundle.getString("area"));
        }
        if (requestCode == 5000 && data != null) {
            Bundle bundle = data.getExtras();
            vs_my_mailbox_text.setText(bundle.getString("mailbox"));
        }

        if (requestCode == PHOTO_REQUEST_GALLERY_IMAGE && data != null) {

            final Intent intent = getCropImageIntent(data.getData());
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
        if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {

            if (resultCode == Activity.RESULT_CANCELED) {

            } else {

                try {

                    final Intent intent = getCropImageIntent(Uri.fromFile(file_pto));
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {

                }
            }


        }


        if (requestCode == PHOTO_REQUEST_GALLERY) {


            Bitmap photo = null;
            try {
                photo = data.getParcelableExtra("data");

                if (photo == null) {
                    Uri originalUri = data.getData();        //获得图片的uri
//	   	                  bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片
                    photo = getThumbnail(VsMyInfoTextActivity.this, originalUri, 1000);

                }


                String path2 = Environment.getExternalStorageDirectory().toString() + "/fs_img/chache";
                File path1 = new File(path2);
                if (!path1.exists()) {
                    path1.mkdirs();
                }
//	            		uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
                File file = new File(path1, uid + System.currentTimeMillis() + ".jpg");
                String filePath = file.getAbsolutePath();
                saveFileByBitmap(photo, filePath);

                vs_myselft_infoimgView.setImageBitmap(photo);

                上传图片
//	                    getMyInfo(uid, url, file);
//	                    uploadFile(url, filePath, uid);
//	                    if (photo != null&&!photo.isRecycled()) {
//	                    	photo.recycle();
//						}
            } catch (Exception e) {


            }

        }
         */
    }


    //调取相机
    public void onTakePicClickListener(View v) {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        String path = Environment.getExternalStorageDirectory().toString() + "/lxt_img";
        File path1 = new File(path);
        if (!path1.exists()) {
            path1.mkdirs();
        }
        file_pto = new File(path1, System.currentTimeMillis() + "p.jpg");
        String filePath = file_pto.getAbsolutePath();
//		pathString = filePath;
        photoUri = Uri.fromFile(file_pto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_pto));
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }


    //调取相册
    public void onChoosePicClickListener(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);  
        intent.setType("image/*");
//        intent.putExtra("crop", "true");  
//        intent.putExtra("aspectX", 1);  
//        intent.putExtra("aspectY", 1);  
//        intent.putExtra("outputX", 80);  
//        intent.putExtra("outputY", 80);  
//        intent.putExtra("return-data", true);  
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY_IMAGE);
//    	Intent intent = new Intent(this,TestPicActivity.class);
//    	intent.putExtra("where", "notice");
//    	startActivity(intent);
    }


    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        return intent;
    }


    private void saveFileByBitmap(Bitmap bitmap, String newImagePath) {
        File file = new File(newImagePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_photo, newImagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    //上传参数
//		private void getMyInfo(String uid,String url,File para){
//	   	 HttpUtils fh = new HttpUtils();
//	        RequestParams params = new RequestParams();
//	       
//	        
//	        params.addBodyParameter("a", "modify");
//	        params.addBodyParameter("s", "member");        
//	        params.addBodyParameter("uid", uid);
//	     
//	        params.addBodyParameter("icon", para,"image/jpg");
//	       
//	       
//	        fh.send(HttpMethod.POST, url,params, new RequestCallBack<String>(){
//	        	  @Override
//	        	  public void onLoading(long total, long current, boolean isUploading) {
//	      	     
//	        	  }
//	        	  @Override
//	        	  public void onSuccess(ResponseInfo<String> responseInfo) {
//	        		
//	        			  String result = responseInfo.result;
//	        	 System.out.println(result);
//	        	 if (result.contains("true")) {
//	        		 Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
//				}
//	        	
//	        	  }
//	        	  @Override
//	        	  public void onStart() {
//	        	  }
//	        	  @Override
//	        	  public void onFailure(HttpException error, String msg) {
//	        		 
//	        	  }
//	        	});
//	   }


    private static Bitmap getThumbnail(Context context, Uri uri, int size) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private BroadcastReceiver gettimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DfineAction.ACTION_GETTIME.equals(intent.getAction())) {

                vs_my_birthday_text.setText(intent.getStringExtra("time"));
                VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Birth, intent.getStringExtra("time"));
                getMyInfo(uid);
            }
        }
    };

    //上传参数
    private void getMyInfo(String uid) {
//			{  
//				"birthday": "1980-12-01", , "icon": "/data/sesfsdfsd/a.jpg",  "gender": "M",  "nick": "test中文",	
//				 , "email": "Jeyha_yan@126.c om"}
//			unregisterReceiver(getInfoReceiver);
//			IntentFilter filter = new IntentFilter();
//			filter.addAction(DfineAction.ACTION_PUSHINFOSUCCESS);
//			mContext.registerReceiver(getInfoReceiver, filter);

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("uid", uid);
//			treeMap.put(flag, para);
        treeMap.put("icon", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_photo));
        treeMap.put("gender", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Gender));
        treeMap.put("nick", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Nickname));
        treeMap.put("email", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_MailBox));
        treeMap.put("birthday", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_MyInfo_Birth));


        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.PushMyInfo, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionPushmyInfo);


    }

    private BroadcastReceiver getInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DfineAction.ACTION_PUSHINFOSUCCESS.equals(intent.getAction())) {
                if (intent.getStringExtra("result").equals("0")) {
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, intent.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gettimeReceiver != null) {
            unregisterReceiver(gettimeReceiver);
            gettimeReceiver = null;
        }

        if (getInfoReceiver != null) {
            unregisterReceiver(getInfoReceiver);
            getInfoReceiver = null;
        }
    }

    @Override
    public void onStop() {
//			if (gettimeReceiver != null) {
//				 unregisterReceiver(gettimeReceiver);
//				 gettimeReceiver = null;
//			}
//	   
//			if (getInfoReceiver != null) {
//				 unregisterReceiver(getInfoReceiver);
//				 getInfoReceiver = null;
//			}
        super.onStop();
    }

}
