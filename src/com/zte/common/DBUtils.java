package com.zte.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.weiwei.base.common.FileUtils;

/**
 * Created by Jiangxuewu on 2014/9/28.
 */
public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    private static final String COMMAND_BACKUP = "backupDatabase";
    private static final String COMMAND_RESTORE = "restroeDatabase";

    private Context mContext;

    private static DBUtils mInstance = null;
    private static Object lock = new Object();
    private Handler mHandler;

    private static String packegePath;
    private static String backPath;

    public static DBUtils getInstance(Context context) {
        synchronized (lock) {
            if (mInstance == null) {
                mInstance = new DBUtils(context);
            }
        }
        return mInstance;
    }

    public String getSDCardPath() {
        return backPath;
    }

    private DBUtils(Context context) {
        mContext = context;
        packegePath = context.getPackageName();
//        dbName = Database.getName();
//        dbFilePath = context.getApplicationContext().getDatabasePath(dbName).getAbsolutePath();
        backPath = FileUtils.getSaveFilePath() + "backup";
        mHandler = new Handler();

        List<String> list = FileUtils.getListFiles("/data/data/" + packegePath, null, true);
        for (String item : list) {
            Log.i(TAG, "data list : " + item);
        }
    }

    public void setPackegePath(String path) {
        packegePath = path;
    }

    public class BackupTask extends AsyncTask<String, Void, Void> {

        public BackupTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isRestore) {
                Toast.makeText(mContext, "restore_success", Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Process.killProcess(Process.myPid());
                            }
                        });
                    }
                }).start();

            } else {
                Toast.makeText(mContext, "backup_success", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {


            try {

                String command = params[0];
                if (command.equals(COMMAND_BACKUP)) {

                    List<String> dirs = FileUtils.getListFiles("/data/data/" + packegePath, null, true);
                    if (null == dirs) {
                        return null;
                    }

                    ArrayList<File> srcDB = new ArrayList<File>();
                    ArrayList<File> srcPR = new ArrayList<File>();

                    for (String d : dirs) {
                        File f = new File(d);
                        String name = f.getParentFile().getName();

                        if ("databases".equals(name)) {
                            srcDB.add(f);
                        } else if ("shared_prefs".equals(name)) {
                            srcPR.add(f);
                        }
                    }

                    File[] dbArr = new File[srcDB.size()];
                    File[] prArr = new File[srcPR.size()];

                    int i = 0, j = 0;
                    for (File f : srcDB) {
                        dbArr[i++] = f;
                    }
                    for (File f : srcPR) {
                        prArr[j++] = f;
                    }

                    File dbBack = new File("/data/data/" + packegePath + "/databases.zip");
                    File prBack = new File("/data/data/" + packegePath + "/shared_prefs.zip");

                    FileUtils.zipFiles(dbArr, dbBack);
                    FileUtils.zipFiles(prArr, prBack);

                    File[] src = {dbBack, prBack};


                    File backup = FileUtils.createFile(backPath + File.separator + getFileName());

                    FileUtils.zipFiles(src, backup);

                    FileUtils.deleteFile(dbBack.getPath());

                    FileUtils.deleteFile(prBack.getPath());

                } else if (command.equals(COMMAND_RESTORE)) {

                    File src = new File(backPath + File.separator + params[1]);
                    File out = new File(backPath + File.separator + "tmp");
                    out.mkdir();
                    ZipCompress.unZipFolder(src.getAbsolutePath(), out.getAbsolutePath());
                    List<String> zips = FileUtils.getListFiles(out.getAbsolutePath(), "zip", true);
                    if (null == zips) {
                        return null;
                    }

                    for (String i : zips) {

                        File out1 = new File(out.getAbsolutePath() + File.separator + new File(i).getName().replaceAll(".zip", ""));

                        ZipCompress.unZipFolder(new File(i).getAbsolutePath(), out1.getAbsolutePath());

                        List<String> list = FileUtils.getListFiles(out1.getAbsolutePath(), null, true);
                        if (null != list) {
                            for (String f : list) {
                                File from = new File(f);
                                File to = new File("/data/data/" + packegePath + File.separator
                                        + from.getParentFile().getName() + File.separator
                                        + from.getName());
                                FileUtils.copyfile(from, to, true);

                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    delete(new File(backPath + File.separator + "tmp"));
                } catch (Exception e) {

                }
            }

            return null;
        }

    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    private void deleteFile(String s) {
        List<String> list = FileUtils.getListFiles(s, null, true);
        if (null != list) {
            for (String f : list) {
                FileUtils.deleteFile(f);
            }
        }
    }

    /**
     * get date from timestamp
     *
     * @return date format dd-MM-yyyy
     */
    private String getDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HHmmss");
        return formatter.format(date);

    }

    private String getFileName() {
        String name = getDate(System.currentTimeMillis());

        return name;
    }

    boolean isRestore = false;

    /**
     * 数据恢复
     */
    public void dataRestore(String name) {
        isRestore = true;
        new BackupTask().execute(COMMAND_RESTORE, name);
    }

    /**
     * 数据备份
     */
    public void dataBackup() {
        isRestore = false;
        new BackupTask().execute(COMMAND_BACKUP);
    }
}
