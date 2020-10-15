package com.weiwei.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.item.VsCallLogListItem;

/**
 * 通话记录数据管理
 * 
 * @author Administrator
 */
public class VsCallLogManagement {
	
	/**
     * 排序后显示的历史记录
     */
    public static void copyStaticCallLogsToViewList(final boolean isall) {
        ArrayList<VsCallLogListItem> tmpCallLogs = new ArrayList<VsCallLogListItem>();

        if (!isall) {//过滤没接通
            ArrayList<VsCallLogListItem> misstmpCallLogs = new ArrayList<VsCallLogListItem>();
            misstmpCallLogs.clear();

            String calltus = "1";

            int size = VsPhoneCallHistory.callLogs.size();
            Log.i("CollinWang", "过滤前大小size=" + size);
            for (int i = 0; i < size; i++) {
                VsCallLogListItem callLogListItem = VsPhoneCallHistory.callLogs.get(i);
                boolean isHasmiss = false;
                ArrayList<VsCallLogItem> vsCallLogItems = callLogListItem.getMissChilds();
                ArrayList<VsCallLogItem> tempitem = new ArrayList<VsCallLogItem>();
                tempitem.clear();
                
                Log.i("CollinWang", "vsCallLogItems大小=" + vsCallLogItems.size());
                
                for (int j = 0; j < vsCallLogItems.size(); j++) {
                    VsCallLogItem vsCallLogItem = vsCallLogItems.get(j);
                    calltus = vsCallLogItem.ctype;
                    Log.i("CollinWang", "过滤中状态码=" + calltus);
                    /* if (!"2".equals(calltus) || !"1".equals(calltus)) {
                         tempitem.add(vsCallLogItem);
                         isHasmiss = true;
                     }*/
                /*
                    if ("3".equals(calltus)) {
                        tempitem.add(vsCallLogItem);
                        isHasmiss = true;
                   }*/
                    
                    if (vsCallLogItem.calltimelength != null && !"".equals(vsCallLogItem.calltimelength) && !"222".equals(vsCallLogItem.calltimelength) && vsCallLogItem.callmoney != null && !"".equals(vsCallLogItem.callmoney) && Double.valueOf(vsCallLogItem.callmoney) > 0 && !"3".equals(vsCallLogItem.ctype) && !"00分00秒".equals(vsCallLogItem.calltimelength)) {

                        //  holder.calllog_detail_calltime.setText(vsCallLogItem.calltimelength);
                    }
                    else {
                        if ("1".equals(calltus)&&vsCallLogItem.directCall==3) {
                            tempitem.add(vsCallLogItem);
                            isHasmiss = true; 
                        } 
                    }
                    if ("3".equals(calltus)) {
                        tempitem.add(vsCallLogItem);
                        isHasmiss = true; 
                    }
                /*    if (vsCallLogItem.directCall != 1) {
                        if (vsCallLogItem.calltimelength != null && !"".equals(vsCallLogItem.calltimelength) && !"222".equals(vsCallLogItem.calltimelength) && vsCallLogItem.callmoney != null && !"".equals(vsCallLogItem.callmoney) && Double.valueOf(vsCallLogItem.callmoney) > 0 && !"3".equals(vsCallLogItem.ctype) && !"00分00秒".equals(vsCallLogItem.calltimelength)) {

                            //  holder.calllog_detail_calltime.setText(vsCallLogItem.calltimelength);
                        }
                        else {
                            if (vsCallLogItem.directCall == 2) {
                                // holder.calllog_detail_calltime.setText("接通");
                            }
                            else {
                                //  holder.calllog_detail_calltime.setText("未接通");
                                tempitem.add(vsCallLogItem);
                                isHasmiss = true;
                            }
                        }
                    }
                    else {
                        if (vsCallLogItem.calltimelength != null && !"".equals(vsCallLogItem.calltimelength) && !"222".equals(vsCallLogItem.calltimelength) && !"3".equals(vsCallLogItem.ctype) && !"00分00秒".equals(vsCallLogItem.calltimelength)) {
                            // holder.calllog_detail_calltime.setText(vsCallLogItem.calltimelength);
                        }
                        else {
                            //  holder.calllog_detail_calltime.setText("未接通");
                            tempitem.add(vsCallLogItem);
                            isHasmiss = true;
                        }
                    }*/

                }
                if (isHasmiss) {
                    callLogListItem.getMissChilds().clear();
                    callLogListItem.setMissChilds(tempitem);

                    misstmpCallLogs.add(callLogListItem);
                    Log.i("CollinWang", "过滤后未接size=" + misstmpCallLogs.size());
                }

            }
            tmpCallLogs.addAll(misstmpCallLogs);
        }
        else {
            tmpCallLogs.addAll(VsPhoneCallHistory.callLogs);
        }

        Collections.sort(tmpCallLogs, new Comparator<VsCallLogListItem>() {
            @Override
            public int compare(VsCallLogListItem object1, VsCallLogListItem object2) {
                if (isall) {
                    if (object1.getChilds().get(0).calltimestamp < object2.getChilds().get(0).calltimestamp) {
                        return 1;
                    }
                    if (object1.getChilds().get(0).calltimestamp > object2.getChilds().get(0).calltimestamp) {
                        return -1;
                    }
                    return 0;
                }
                else {
                    if (object1.getMissChilds().get(0).calltimestamp < object2.getMissChilds().get(0).calltimestamp) {
                        return 1;
                    }
                    if (object1.getMissChilds().get(0).calltimestamp > object2.getMissChilds().get(0).calltimestamp) {
                        return -1;
                    }
                    return 0;
                }

            }
        });
        VsPhoneCallHistory.callLogViewList.clear();        
        /**
         * 是否是好友
         */
        int size = tmpCallLogs.size();
        for (int i = 0; i < size; i++) {
            VsCallLogListItem callLogListItem = tmpCallLogs.get(i);
            VsCallLogItem callLogItem = null;
            if (isall) {
                callLogItem = callLogListItem.getFirst();
            }
            else {
                callLogItem = callLogListItem.getMissFirst();
            }
        }
        VsPhoneCallHistory.callLogViewList.addAll(tmpCallLogs);
    }

	/**
	 * 排序后显示的历史记录
	 */
	public static void copyStaticCallLogsToViewList() {
		ArrayList<VsCallLogListItem> tmpCallLogs = new ArrayList<VsCallLogListItem>();
		tmpCallLogs.addAll(VsPhoneCallHistory.callLogs);
		Collections.sort(tmpCallLogs, new Comparator<VsCallLogListItem>() {
			@Override
			public int compare(VsCallLogListItem object1, VsCallLogListItem object2) {
				if (object1.getChilds().get(0).calltimestamp < object2.getChilds().get(0).calltimestamp) {
					return 1;
				}
				if (object1.getChilds().get(0).calltimestamp > object2.getChilds().get(0).calltimestamp) {
					return -1;
				}
				return 0;
			}
		});
		VsPhoneCallHistory.callLogViewList.clear();
		VsPhoneCallHistory.callLogViewList.addAll(tmpCallLogs);
	}

	// /**
	// * 根据名字得到联系人号码。
	// *
	// * @param ctx
	// * @param name
	// * @return
	// */
	// public static String[] getContacNumberByContactName(Context ctx, String name) {
	// String[] nums = mgetContact.getContacNumberByContactId(ctx, name);
	// return nums;
	// }

	// /**
	// * 根据电话号码获取名称
	// *
	// * @param context
	// * @param number
	// * @return String name
	// */
	// public static String getPeople(Context context, String phoneNumber) {
	// String name = phoneNumber;
	// try {
	// Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	// Cursor cursor = context.getContentResolver().query(uri,
	// new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
	// if (cursor != null && cursor.moveToFirst()) {
	// name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
	// }
	// if (cursor != null)
	// cursor.close();
	// } catch (Exception e) {
	// }
	//
	// return name;
	// }
	//
	// public void sortAddChildCallLog() {
	//
	// }
}
