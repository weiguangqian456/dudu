package com.weiwei.base.db.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.ImFrienMessage;
import com.weiwei.base.item.ImMessageItem;

public class VsMessage {
    public final static String TAG = "VVMessage";
    public static final String TABLE_NAME_MESSAGE = "immessage";
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
    public static final String MIME_ITEM = "vnd.skype.immessage";

    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;

    public static final String MEME_TYPE_MULTIPLE = "MIME_DIR_PREFIX" + "/" + MIME_ITEM;
    public static final Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsMessage.PATH_MULTIPLE);

    public static final String PATH_SINGLE = "immessage/#";
    public static final String PATH_MULTIPLE = "immessage";

    public static final String ID = "_id"; // 唯一标识主键
    public static final String MESSAGE_ID = "id"; // 消息id
    public static final String MESSAGE_MSG = "msg"; // 消息
    public static final String MESSAGE_FILEPATH = "filePath"; // 消息路径
    public static final String MESSAGE_TYPE = "type";// 消息类型
    public static final String MESSAGE_FROMUID = "fromuid";// 来自
    public static final String MESSAGE_TOUID = "touid";// 发送
    public static final String MESSAGE_SIZE = "size";// 大小
    public static final String MESSAGE_NAME = "name";// 名字
    public static final String MESSAGE_TIME = "time";// 时间
    public static final String MESSAGE_READ = "read";// 是否读取
    public static final String MESSAGE_ISME = "isme";// 是否自己
    public static final String MESSAGE_UID = "uid";// UID 标识帐号 切换时区分
    public static final String MESSAGE_ISSENDSUC = "issendsuc";// UID 是否发送成功

    /**
     * 获取消息数据
     */
    public final static String VS_ACTION_LOADNOTICE = "com.kc.logic.loadnotice";
    /**
     * 消息略读反馈接口
     */
    public final static String VS_ACTION_FEEDBACK = "com.kc.logic.feedback";

    /**
     * 所有消息数据
     */
    public static ArrayList<ImMessageItem> messageList = new ArrayList<ImMessageItem>();
    /**
     * 显示的消息数据
     */
    public static ArrayList<ImMessageItem> messageViewList = new ArrayList<ImMessageItem>();

    /**
     * 添加消息记录
     */
    public static void addImMsg(Context mContext, ImMessageItem imMessageItem) {
        
        ContentValues cv = new ContentValues();
        cv.put(VsMessage.MESSAGE_ID, imMessageItem.id);
        cv.put(VsMessage.MESSAGE_MSG, imMessageItem.msg);
        cv.put(VsMessage.MESSAGE_FILEPATH, imMessageItem.filePath);
        cv.put(VsMessage.MESSAGE_TYPE, imMessageItem.type);
        cv.put(VsMessage.MESSAGE_FROMUID, imMessageItem.fromuid);

        cv.put(VsMessage.MESSAGE_TOUID, imMessageItem.touid);
        cv.put(VsMessage.MESSAGE_SIZE, imMessageItem.size);
        cv.put(VsMessage.MESSAGE_NAME, imMessageItem.name);
        cv.put(VsMessage.MESSAGE_TIME, imMessageItem.time);
        cv.put(VsMessage.MESSAGE_READ, imMessageItem.read);
        cv.put(VsMessage.MESSAGE_ISME, imMessageItem.isme);
        cv.put(VsMessage.MESSAGE_UID, imMessageItem.uid);
        cv.put(VsMessage.MESSAGE_ISSENDSUC, imMessageItem.isSendSuc);
        mContext.getContentResolver().insert(contacturl, cv);
        CustomLog.i(TAG, "input read=" + imMessageItem.read);
        /*
         * vsNoticeItem.notice_id = loadNoticeDataID(mContext);
         */
        if (imMessageItem.fromuid.equals(// 在聊天界面收到的
        VsUserConfig.getDataString(VsApplication.getContext(), VsUserConfig.jkey_clentid_chatting))) {
            messageViewList.add(imMessageItem);// 页面显示增加一条记录
            updateMsg(mContext);
        }
        // copyStaticMessageToViewList();

    }

    /**
     * 排序后显示的消息
     */
    public static void copyStaticMessageToViewList() {
        ArrayList<ImMessageItem> tmpNotices = new ArrayList<ImMessageItem>();
        tmpNotices.addAll(messageList);
        Collections.sort(tmpNotices, new Comparator<ImMessageItem>() {

            @Override
            public int compare(ImMessageItem lhs, ImMessageItem rhs) {
                // TODO Auto-generated method stub
                long time1 = Long.parseLong(lhs.time);
                long time2 = Long.parseLong(rhs.time);
                return (int) (time1 - time2);
            }

        });
        messageViewList.clear();
        messageViewList.addAll(tmpNotices);
        /**/
    }

    /**
     * 读取消息
     */
    public static HashMap<String, ImFrienMessage> loadMsgDataMap(Context context) {
        if (context == null) {
            return null;
        }
        // msgListMap.clear();
        /*
         * Intent intent = new Intent();
         * intent.setAction(DfineAction.ACTION_LOAD_NOTICE);
         * context.sendBroadcast(intent);
         */
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);

        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        HashMap<String, ImFrienMessage> msgMap = new HashMap<String, ImFrienMessage>();
        HashMap<String, ArrayList<ImMessageItem>> msgArraylistMap_user = new HashMap<String, ArrayList<ImMessageItem>>();
        ArrayList<ImMessageItem> msgItemList = null;
        Cursor mCursor = null;
        try {
            mCursor = context.getContentResolver().query(contacturl, null, VsMessage.MESSAGE_UID + "=? ", new String[] { uid }, "_id desc");// "_id desc"倒序 不加就是正序
            if (mCursor == null) {
                return null;
            }
            Integer num = mCursor.getCount();
            mCursor.moveToFirst();
            while (mCursor.getPosition() != num) {
                ImFrienMessage imFriend = new ImFrienMessage();
                msgItemList = new ArrayList<ImMessageItem>();
                ImMessageItem imMessageItem = new ImMessageItem();

                imMessageItem.id = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_ID));
                imMessageItem.filePath = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FILEPATH));
                imMessageItem.type = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_TYPE));
                imMessageItem.fromuid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FROMUID));
                imMessageItem.touid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TOUID));
                imMessageItem.size = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_SIZE));
                imMessageItem.name = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_NAME));
                imMessageItem.time = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TIME));

                imMessageItem.msg = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_MSG));
                imMessageItem.read = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_READ));
                imMessageItem.isme = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_ISME));
                imMessageItem.uid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_UID));
                if (msgMap.containsKey(imMessageItem.fromuid)) { //如果最外层已经有
                    ImFrienMessage imFriend_temp = msgMap.get(imMessageItem.fromuid);
                    imFriend.fromuid = imMessageItem.fromuid;
                    imFriend.message_count = imFriend_temp.message_count + 1;
                    CustomLog.i(TAG, "isrean=" + imMessageItem.read);
                    if (imMessageItem.read == 1) {
                        CustomLog.i(TAG, "进入unread_message_count=" + imFriend.unread_message_count);
                        imFriend.unread_message_count = imFriend_temp.unread_message_count + 1;
                    }
                    else {
                        imFriend.unread_message_count = imFriend_temp.unread_message_count;
                    }
                    CustomLog.i(TAG, "然后unread_message_count=" + imFriend.unread_message_count);
                    imFriend.time = imFriend_temp.time;// sql语句已经是按照最后时间排序的，最后一条时间
                    imFriend.type = imFriend_temp.type;// 最后一条记录的会话类型
                    imFriend.isread = imFriend_temp.isread;// 最后一条记录是否已经阅读
                    imFriend.isme = imFriend_temp.isme;// 最后一条记录是不是自己发送的
                    imFriend.msg = imFriend_temp.msg;// 最后一条记录是不是自己发送的
                    if (msgArraylistMap_user.containsKey(imMessageItem.fromuid)) {
                        msgItemList = msgArraylistMap_user.get(imMessageItem.fromuid);
                        msgItemList.add(imMessageItem);
                    }
                    else {
                        msgItemList.add(imMessageItem);
                        imFriend.time = imMessageItem.time;// sql语句已经是按照最后时间排序的，最后一条时间
                        imFriend.type = imMessageItem.type;// 最后一条记录的会话类型
                        imFriend.isread = imMessageItem.read;// 最后一条记录是否已经阅读
                        imFriend.isme = imMessageItem.isme;// 最后一条记录是不是自己发送的
                        imFriend.msg = imMessageItem.msg;// 
                    }
                }
                else {
                    imFriend.fromuid = imMessageItem.fromuid;
                    imFriend.message_count = imFriend.message_count + 1;
                    if (imMessageItem.read == 1)
                        imFriend.unread_message_count = imFriend.unread_message_count + 1;
                    if (msgArraylistMap_user.containsKey(imMessageItem.fromuid)) {
                        msgItemList = msgArraylistMap_user.get(imMessageItem.fromuid);
                        msgItemList.add(imMessageItem);
                    }
                    else {
                        msgItemList.add(imMessageItem);
                        imFriend.time = imMessageItem.time;// sql语句已经是按照最后时间排序的，最后一条时间
                        imFriend.type = imMessageItem.type;// 最后一条记录的会话类型
                        imFriend.isread = imMessageItem.read;// 最后一条记录是否已经阅读
                        imFriend.isme = imMessageItem.isme;// 最后一条记录是不是自己发送的
                        imFriend.msg = imMessageItem.msg;// 最后一条记录是不是自己发送的
                    }
                }
                msgArraylistMap_user.put(imMessageItem.fromuid, msgItemList);
                imFriend.msgArraylistMap_user = msgArraylistMap_user;
                msgMap.put(imMessageItem.fromuid, imFriend);

                // // 如果存储消息大于一个月就删除掉
                // if (VsUtil.getDateMonth(imMessageItem.time)) {
                // // delNotice(context, vsNoticeItem.messageid);
                // } else {
                // messageList.add(imMessageItem);
                // }
                mCursor.moveToNext();
            }
            return msgMap;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
    }

    /**
     * 读取消息
     */
    public static void loadMessageData(Context context) {
        if (context == null) {
            return;
        }
        messageList.clear();
        /*
         * Intent intent = new Intent();
         * intent.setAction(DfineAction.ACTION_LOAD_NOTICE);
         * context.sendBroadcast(intent);
         */
        Cursor mCursor = null;
        try {
            mCursor = context.getContentResolver().query(contacturl, null, null, null, "_id desc");
            if (mCursor == null) {
                return;
            }
            Integer num = mCursor.getCount();
            mCursor.moveToFirst();
            while (mCursor.getPosition() != num) {
                ImMessageItem imMessageItem = new ImMessageItem();
                // calldistnum
                imMessageItem.id = mCursor.getString(mCursor.getColumnIndex(VsMessage.ID));
                imMessageItem.filePath = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FILEPATH));
                imMessageItem.type = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_TYPE));
                imMessageItem.fromuid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FROMUID));
                imMessageItem.touid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TOUID));
                imMessageItem.size = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_SIZE));
                imMessageItem.name = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_NAME));
                imMessageItem.time = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TIME));

                imMessageItem.msg = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_MSG));
                imMessageItem.read = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_READ));
                imMessageItem.isme = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_ISME));
                imMessageItem.uid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_UID));
                // 如果存储消息大于一个月就删除掉
                if (VsUtil.getDateMonth(imMessageItem.time)) {
                    // delNotice(context, vsNoticeItem.messageid);
                }
                else {
                    messageList.add(imMessageItem);
                }
                mCursor.moveToNext();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }

        // copyStaticMessageToViewList();
        // updateMsg(context);
    }

    /**
     * 根据对方clientid读取消息
     */
    public static void loadMessageDataById(Context context, String fromid) {
        if (context == null) {
            return;
        }
        messageViewList.clear();
        /*
         * Intent intent = new Intent();
         * intent.setAction(DfineAction.ACTION_LOAD_NOTICE);
         * context.sendBroadcast(intent);
         */
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);

        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        Cursor mCursor = null;
        try {
            mCursor = context.getContentResolver().query(contacturl, null, VsMessage.MESSAGE_UID + "=? and " + VsMessage.MESSAGE_FROMUID + " =?", new String[] { uid, fromid }, null);// "_id desc"倒序 不加就是正序
            if (mCursor == null) {
                return;
            }
            Integer num = mCursor.getCount();
            mCursor.moveToFirst();
            while (mCursor.getPosition() != num) {
                ImMessageItem imMessageItem = new ImMessageItem();
                // calldistnum
                imMessageItem.id = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_ID));
                imMessageItem.filePath = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FILEPATH));
                imMessageItem.type = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_TYPE));
                imMessageItem.fromuid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_FROMUID));
                imMessageItem.touid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TOUID));
                imMessageItem.size = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_SIZE));
                imMessageItem.name = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_NAME));
                imMessageItem.time = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_TIME));
                imMessageItem.msg = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_MSG));
                imMessageItem.read = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_READ));
                imMessageItem.isme = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_ISME));
                imMessageItem.uid = mCursor.getString(mCursor.getColumnIndex(VsMessage.MESSAGE_UID));
                imMessageItem.isSendSuc = mCursor.getInt(mCursor.getColumnIndex(VsMessage.MESSAGE_ISSENDSUC));
                // 如果存储消息大于一个月就删除掉
                if (VsUtil.getDateMonth(imMessageItem.time)) {
                    // delMessage(context, imMessageItem.id);
                }
                else {
                    messageViewList.add(imMessageItem);
                }
                mCursor.moveToNext();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        // 如果没有消息 加一条默认消息

        // copyStaticMessageToViewList();
        updateMsg(context);
    }

    /**
     * 删除单个消息记录
     * 
     * @param context
     * @param phonenum
     */
    public static void delMessage(Context context, String fromUid) {
        if (context == null) {
            return;
        }
        /*
         * // 删除列表 for (int i = 0; i < messageList.size(); i++) { if
         * (messageList.get(i).id.equals(notice_id)) { messageList.remove(i); }
         * }
         */
        // 前提是UID 切换帐号时区分
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);

        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        context.getContentResolver().delete(contacturl, VsMessage.MESSAGE_UID + "=? and " + VsMessage.MESSAGE_FROMUID + " =?", new String[] { uid, fromUid });
        refreshMsg(context);
        return;
    }

    /**
     * 删除单个消息记录
     * 
     * @param context
     * @param phonenum
     */
    public static void delAllMessageByUId(Context context) {
        if (context == null) {
            return;
        }
        /*
         * // 删除列表 for (int i = 0; i < messageList.size(); i++) { if
         * (messageList.get(i).id.equals(notice_id)) { messageList.remove(i); }
         * }
         */
        // 前提是UID 切换帐号时区分
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);

        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        context.getContentResolver().delete(contacturl, VsMessage.MESSAGE_UID + "=? ", new String[] { uid });
        refreshMsg(context);
        return;
    }

    /**
     * 更新消息内容
     * 
     * @param Context
     */
    private static void updateMsg(Context Context) {
        Intent intent = new Intent();
        intent.setAction(DfineAction.ACTION_SHOW_MESSAGE);
        Context.sendBroadcast(intent);
        /*
         * Intent updateNoticeNumIntent = new Intent();
         * updateNoticeNumIntent.setAction(DfineAction.ACTION_UPDATENOTICENUM);
         * Context.sendBroadcast(updateNoticeNumIntent);
         */
    }

    /**
     * 更新消息内容
     * 
     * @param Context
     */
    private static void queryMsg(Context Context) {
        Intent intent = new Intent();
        intent.setAction(DfineAction.ACTION_QUERY_MESSAGE);
        Context.sendBroadcast(intent);
        /*
         * Intent updateNoticeNumIntent = new Intent();
         * updateNoticeNumIntent.setAction(DfineAction.ACTION_UPDATENOTICENUM);
         * Context.sendBroadcast(updateNoticeNumIntent);
         */
    }

    /**
     * 更新会话消息内容
     * 
     * @param Context
     */
    public static void refreshMsg(Context Context) {
        Intent intent = new Intent();
        intent.setAction(DfineAction.ACTION_UPDA_MESSAGE_COME);
        Context.sendBroadcast(intent);
        /*
         * Intent updateNoticeNumIntent = new Intent();
         * updateNoticeNumIntent.setAction(DfineAction.ACTION_UPDATENOTICENUM);
         * Context.sendBroadcast(updateNoticeNumIntent);
         */
    }

    /**
     * 删除所有消息
     * 
     * @param context
     */
    public static void delAllMessage(Context context) {
        if (context == null)
            return;

        // 删除列表
        messageList.clear();

        // 删除数据库中所以通话记录
        // CursorHelper.deleteAllHistroy(context);
        context.getContentResolver().delete(contacturl, null, null);
        updateMsg(context);
        return;
    }

    /**
     * 根据好友获取是否有未读消息
     */
    public static boolean noreadMessages(Context context, String fromId) {
        boolean istype = false;
        if (context == null) {
            return istype;
        }
        Cursor mCursor = null;
        try {
            mCursor = context.getContentResolver().query(contacturl, null, MESSAGE_READ + "= 1" + " and " + MESSAGE_FROMUID + "= " + fromId, null, null);
            if (mCursor == null) {
                return istype;
            }
            // CustomLog.i("MESSAGE", "有没有新消息" + mCursor.getCount());
            if (mCursor.getCount() > 0) {
                istype = true;
                return istype;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
        return istype;
    }

    /**
     * 根据clentid修改消息是否读状态
     * 
     * @param context
     * @param phonenum
     */
    public static void updateMessageRead(Context context, String clentid) {
        if (context == null) {
            return;
        }
        // 删除列表
        /*
         * for (int i = 0; i < NoticeList.size(); i++) { if
         * (NoticeList.get(i).notice_id.equals(notice_id)) {
         * NoticeList.get(i).messagetype = "1"; } }
         */
        // 前提是UID 切换帐号时区分
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        ContentValues args = new ContentValues();
        args.put(VsMessage.MESSAGE_READ, 0);// 0已经读取
        context.getContentResolver().update(contacturl, args, VsMessage.MESSAGE_FROMUID + "=? and " + VsMessage.MESSAGE_UID + " =?", new String[] { clentid, uid });
        // sendNoticeMsg(context);
        return;
    }

    /**
     * 根据clentid修改消息发送成功
     * 
     * @param context
     * @param phonenum
     */
    public static void updateMessageIssendSuc(Context context, String clentid, int issendsuc, String mesId) {
        if (context == null) {
            return;
        }
        // 删除列表
        /*
         * for (int i = 0; i < NoticeList.size(); i++) { if
         * (NoticeList.get(i).notice_id.equals(notice_id)) {
         * NoticeList.get(i).messagetype = "1"; } }
         */
        // 前提是UID 切换帐号时区分
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        String uid = settings.getString(VsUserConfig.JKey_KcId, null);
        ContentValues args = new ContentValues();
        args.put(VsMessage.MESSAGE_ISSENDSUC, issendsuc);// 
        context.getContentResolver().update(contacturl, args, VsMessage.MESSAGE_FROMUID + "=? and " + VsMessage.MESSAGE_UID + " =? and " + VsMessage.MESSAGE_ID + " =?", new String[] { clentid, uid, mesId });
       queryMsg(context);
       ///updateMsg(context);
        return;
    }

}
