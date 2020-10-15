package com.weiwei.home.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.R;
import com.weiwei.base.provider.FileProvider7;
import com.weiwei.salemall.utils.ToastUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : hc
 * @date : 2019/3/4.
 * @description: 图片获取的Dialog
 */

public class ImageGetDialog extends DialogFragment{

    private Uri mUri;
    private Context mContext;
    private String mStrPath;

    private OnImagePathListener onGetImagePath;

    public static final int REQUEST_TAKE_PHOTO = 2601;
    public static final int REQUEST_TAKE_ALBUM = 2602;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dialog_image, container);
        ButterKnife.bind(this,inflate);
        this.mContext = getActivity();
        return inflate;
    }

    public void onTakePhoto(){
        String fileName = "DD-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA) .format(new Date()) + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if(!file.getParentFile().exists()){
            boolean mkdirs = file.getParentFile().mkdirs();
            if(!mkdirs){
                ToastUtils.show(mContext,"文件创建失败");
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Android 7.0及以上获取文件 Uri
            if(onGetImagePath != null){
                onGetImagePath.strPath(file.getAbsolutePath());
            }
            mUri = FileProvider7.getUriForFile(getActivity(), file);
        } else {
            //获取文件Uri
            mUri = Uri.fromFile(file);
        }
        //调取系统拍照
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        getActivity().startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        this.dismiss();
    }

    @OnClick(R.id.tv_album)
    public void onAlbum(){
        this.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    @OnClick(R.id.tv_cancel)
    public void onCancel(){
        this.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if(window == null){return;}
        //背景
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //设置在底部 以及 填充全部宽度
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = R.style.DialogAnimation;
        params.width = getResources().getDisplayMetrics().widthPixels;
        window.setAttributes(params);
    }

    @OnClick(R.id.tv_photo)
    public void initPhotoPermission(){
        XXPermissions.with(getActivity())
                .permission(Permission.CAMERA,Permission.READ_PHONE_STATE, Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> list, boolean b) {
                        onTakePhoto();
                    }

                    @Override
                    public void noPermission(List<String> list, boolean b) {
                        ToastUtils.show(getActivity(),"无权限");
                    }
                });
    }

    public void setOnGetPath(OnImagePathListener onGetImagePath){
        this.onGetImagePath = onGetImagePath;
    }

    public interface OnImagePathListener{
        void strPath(String strPath);
    }

}
