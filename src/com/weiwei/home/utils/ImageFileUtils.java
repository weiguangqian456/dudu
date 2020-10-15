package com.weiwei.home.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.weiwei.base.application.VsApplication;
import com.weiwei.home.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author : hc
 * @date : 2019/3/19.
 * @description: 文件保存工具类
 */

public class ImageFileUtils {

    private static final String mSavePath = Environment.getExternalStorageDirectory().getPath() + "/" + Constant.APP_ID;
    public static final String mHeadPortraitName = "tx.png";
    public static final String mSaveHeadPortraitPath = mSavePath + "/" + mHeadPortraitName;

    public static void threadSaveBitmapFile(final Bitmap bitmap,final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveBitmapFile(bitmap,fileName,true);
            }
        }).start();
    }


    private static void saveBitmapFile(final Bitmap bitmap,final String fileName,Boolean isQuality){
        File filePath = new File(mSavePath);
        if(!filePath.exists()){
            if(!filePath.mkdir()){
                LogUtils.e("文件夹创建失败");
            }
        }
        File file = new File(filePath,fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            //通知刷新
            Uri localUri = Uri.fromFile(file);
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            VsApplication.getContext().sendBroadcast(localIntent);
        } catch (FileNotFoundException e) {
            LogUtils.e("文件创建失败 FileNotFoundException : " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.e("文件创建失败 IOException : " + e.toString());
            e.printStackTrace();
        }
    }

    public static void saveUriFile(final Uri uri, final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(VsApplication.getContext().getContentResolver(), uri);
                    if(bitmap != null){
                        saveBitmapFile(bitmap,fileName,false);
                    }else{
                        LogUtils.e("IMAGE_FILE: Bitmap == null");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap compressBitmap(Bitmap bitMap) {
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
