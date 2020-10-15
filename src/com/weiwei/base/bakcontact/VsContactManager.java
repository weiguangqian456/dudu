package com.weiwei.base.bakcontact;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;

import com.weiwei.base.common.CustomLog;

/**
 * Class for managing contacts sync related mOperations xkl
 */
public class VsContactManager {
	/**
	 * Custom IM protocol used when storing status messages.
	 */
	public static final String CUSTOM_IM_PROTOCOL = "SampleSyncAdapter";
	public static String TAG = "KcContactSync";
	public static ArrayList<Integer> contactId = new ArrayList<Integer>();
	public static int renewContactNum = 0;// 保存恢复联系人个数

	/**
	 * Synchronize raw contacts
	 * 
	 * @param context
	 *            The context of Authenticator Activity
	 * @param itemList
	 *            The itemList
	 */
	public static synchronized void syncContacts(Context context, ArrayList<VsBackContactItem> itemList) {
		try {
			CustomLog.v(TAG, "After download contacts, size=" + itemList.size());
			// deleteAllContact(context);
			final ContentResolver resolver = context.getContentResolver();
			final VsBatchOperation batchOperation = new VsBatchOperation(context, resolver);
			int i = 0;
			renewContactNum = 0;
			for (final VsBackContactItem item : itemList) {
				CustomLog.v(TAG, "After download contacts, addContact to op, " + i);

				if (item.phone == null)
					continue;
				int k = 0;
				while (k < item.phone.size()) {
					if (!isContextExist(context, item.phone.get(k).getPhoneNumber()))
						break;
					k++;
				}

				if (k == item.phone.size()) {
					continue;
				} else if (k > 0) {
					deleteContact(context, item.phone.get(0).getPhoneNumber());
				}
				renewContactNum += 1;
				addContact(context, item, batchOperation);
				i++;
				if (i > 300) {
					batchOperation.execute();
					CustomLog.v(TAG, "After download contacts, batchOperation.execute(), ");
					i = 0;
				}
			}
			if (batchOperation.size() > 0) {
				batchOperation.execute();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds a single contact to the platform contacts provider.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param batchOperation
	 *            the VsBatchOperation
	 */
	private static void addContact(Context context, VsBackContactItem item, VsBatchOperation batchOperation) {
		try {
			final VsContactOperations contactOp = VsContactOperations.createNewContact(context, "", batchOperation);
			String showName = "";
			if (item.lastname != null)
				showName += item.lastname;
			if (item.middlename != null) {
				showName += item.middlename;
			}
			if (item.firstname != null)
				showName += item.firstname;
			if ("".equals(showName)) {// 如果没有姓名则有用户名
				if (item.nickname != null)
					showName = item.nickname;
			}

			if (!"".equals(showName)) {
				contactOp.addName(showName, item.firstname != null ? item.firstname : "",
						item.lastname != null ? item.lastname : "", item.middlename != null ? item.middlename : "",
						item.prefix != null ? item.prefix : "", item.suffix != null ? item.suffix : "",
						item.firstname_phonetic != null ? item.firstname_phonetic : "",
						item.lastname_phonetic != null ? item.lastname_phonetic : "",
						item.middlename_phonetic != null ? item.middlename_phonetic : "");
			}
			CustomLog.e(TAG, "contact showname=" + item.firstname + "====lastname:" + item.lastname + "==nickname"
					+ item.nickname);

			if ((item.nickname != null) && (item.nickname != "")) {
				contactOp.addNickName(item.nickname);
			}

			if (item.phone != null) {
				for (PhoneLable phone : item.phone) {
					String key = phone.lable;
					String val = phone.phoneNumber;
					int phone_type = VsContactOpUtil.getPhoneType(context, key);
					contactOp.addPhone(val, key, phone_type);
				}
			} else {
				if (item.phoneGeneral != null) {
					for (int i = 0; i < item.phoneGeneral.size(); i++) {
						contactOp.addPhone(item.phoneGeneral.get(i), Phone.TYPE_MOBILE);
						CustomLog.e(TAG, "contact phoneGeneral=" + item.phoneGeneral.get(i));
					}
				}

				if (item.mobileGeneral != null) {
					for (int i = 0; i < item.mobileGeneral.size(); i++) {
						contactOp.addPhone(item.mobileGeneral.get(i), Phone.TYPE_MOBILE);
						CustomLog.e(TAG, "contact mobileGeneral=" + item.mobileGeneral.get(i));
					}
				}
				if (item.mobileHome != null) {
					for (int i = 0; i < item.mobileHome.size(); i++) {
						contactOp.addPhone(item.mobileHome.get(i), Phone.TYPE_HOME);
						CustomLog.e(TAG, "contact mobileHome=" + item.mobileHome.get(i));
					}
				}
				if (item.mobileWork != null) {
					for (int i = 0; i < item.mobileWork.size(); i++) {
						contactOp.addPhone(item.mobileWork.get(i), Phone.TYPE_WORK);
					}
				}
				if (item.phoneHome != null) {
					for (int i = 0; i < item.phoneHome.size(); i++) {
						contactOp.addPhone(item.phoneHome.get(i), Phone.TYPE_HOME);
					}
				}
				if (item.phoneWork != null) {
					for (int i = 0; i < item.phoneWork.size(); i++) {
						contactOp.addPhone(item.phoneWork.get(i), Phone.TYPE_WORK);
					}
				}
				if (item.faxWork != null) {
					for (int i = 0; i < item.faxWork.size(); i++) {
						contactOp.addPhone(item.faxWork.get(i), Phone.TYPE_FAX_WORK);
					}
				}
				if (item.faxGeneral != null) {
					for (int i = 0; i < item.faxGeneral.size(); i++) {
						contactOp.addPhone(item.faxGeneral.get(i), Phone.TYPE_OTHER_FAX);
					}
				}
				if (item.faxHome != null) {
					for (int i = 0; i < item.faxHome.size(); i++) {
						contactOp.addPhone(item.faxHome.get(i), Phone.TYPE_FAX_HOME);
					}
				}
			}

			if (item.email != null) {
				for (EmailLable email : item.email) {
					String key = email.lable;
					String val = email.email;
					int email_type = VsContactOpUtil.getEmailType(context, key);
					contactOp.addEmail(val, key, email_type);
				}
			} else {
				if (item.emailGeneral != null) {
					for (int i = 0; i < item.emailGeneral.size(); i++) {
						contactOp.addEmail(item.emailGeneral.get(i), "", Email.TYPE_OTHER);
					}
				}
				if (item.emailWork != null) {
					for (int i = 0; i < item.emailWork.size(); i++) {
						contactOp.addEmail(item.emailWork.get(i), "", Email.TYPE_WORK);
					}
				}
				if (item.emailHome != null) {
					for (int i = 0; i < item.emailHome.size(); i++) {
						contactOp.addEmail(item.emailHome.get(i), "", Email.TYPE_HOME);
					}
				}
			}

			if (item.url != null) {
				for (UrlLable url : item.url) {
					String key = url.lable;
					String val = url.url;
					int url_type = VsContactOpUtil.getUrlType(context, key);
					contactOp.addWebsite(val, key, url_type);
				}
			} else {
				if (item.websiteWork != null && item.websiteWork != "") {
					contactOp.addWebsite(item.websiteWork, "", Website.TYPE_WORK);
				}
				if (item.websiteHome != null && item.websiteHome != "") {
					contactOp.addWebsite(item.websiteHome, "", Website.TYPE_HOME);
				}
				if (item.websiteOther != null && item.websiteOther != "") {
					contactOp.addWebsite(item.websiteOther, "", Website.TYPE_OTHER);
				}
			}

			if (item.relatedName != null) {
				for (RelationLable relation : item.relatedName) {
					String key = relation.lable;
					String val = relation.name;
					int relatedName_type = VsContactOpUtil.getRelationType(key);
					contactOp.addRelation(val, key, relatedName_type);
				}
			}

			if (item.address != null) {
				for (AddressLable relation : item.address) {
					String key = relation.lable;
					String zip = relation.zip;
					String street = relation.street;
					String city = relation.city;
					String country = relation.countrykey;
					String state = relation.state;
					String pobox = relation.pobox;
					String neighborhood = relation.neighborhood;
					int address_type = VsContactOpUtil.getAddressType(context, key);
					contactOp.addAddress(neighborhood != null ? neighborhood : "", pobox != null ? pobox : "",
							street != null ? street : "", city != null ? city : "", zip != null ? zip : "",
							state != null ? state : "", country != null ? country : "", key, address_type);
				}
			} else {
				if ((item.addressWork != null && item.addressWork != "")
						|| (item.postcodeWork != null && item.postcodeWork != "")) {
					contactOp.addAP(item.addressWork != null ? item.addressWork : "",
							item.postcodeWork != null ? item.postcodeWork : "", StructuredPostal.TYPE_WORK);
				}
				if ((item.addressHome != null && item.addressHome != "")
						|| (item.postcodeHome != null && item.postcodeHome != "")) {
					contactOp.addAP(item.addressHome != null ? item.addressHome : "",
							item.postcodeHome != null ? item.postcodeHome : "", StructuredPostal.TYPE_HOME);
				}
				if ((item.addressOther != null && item.addressOther != "")
						|| (item.postcodeOther != null && item.postcodeOther != "")) {
					contactOp.addAP(item.addressOther != null ? item.addressOther : "",
							item.postcodeOther != null ? item.postcodeOther : "", StructuredPostal.TYPE_OTHER);
				}
			}

			if (item.instantMessage != null) {
				for (InstantMessage instantMessage : item.instantMessage) {
					String key = instantMessage.lable;
					String service = instantMessage.service;
					String userName = instantMessage.userName;
					int im_type = VsContactOpUtil.getImType(context, key);
					int service_type = VsContactOpUtil.getImProtocolType(service);
					contactOp.addIM(service != null ? service : "", userName != null ? userName : "", key, im_type,
							service_type);
				}
			}

			if (item.picture != null) {
				// contactOp.addAvatar(KcContactSync.picaddr + item.picture,
				// KcCoreService.isWifi(context));
			}

			if (item.birthday != null) {
				contactOp.addEvent(item.birthday, "", Event.TYPE_BIRTHDAY);
			}
			if (((item.company != null) && (item.company != ""))
					|| ((item.department != null) && (item.department != ""))
					|| ((item.postion != null) && (item.postion != ""))) {
				contactOp.addCDP(item.company != null ? item.company : "", item.department != null ? item.department
						: "", item.postion != null ? item.postion : "");
			}

			if (item.remark != null && item.remark != "") {
				contactOp.addRemark(item.remark);
			}
			if (item.gid != null) {
				for (int i = 0; i < item.gid.size(); i++) {
					contactOp.addGid(item.gid.get(i));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isContextExist(Context context, String mNumber) {
		boolean bool = false;
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] { mNumber }, null);

		try {
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String tempNumber = cursor.getString(0);
						if (mNumber.equals(tempNumber)) {
							bool = true;
						} else {
							bool = false;
						}
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return bool;
	}
	public static boolean isContextExist(Context context, String[] mNumber, String name) {
        boolean bool = false;
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + "=?", new String[]{ name }, null);

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String tempName = cursor.getString(0);
                        if (name.equals(tempName)) {
                            bool = true;
                        } else {
                            bool = false;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return bool;
    }

	/**
	 * 删除单个联系人
	 * 
	 * @param context
	 * @return
	 */
	public static boolean deleteContact(Context context, String mNumber) {
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID },
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] { mNumber }, null);
		String id = "";
		try {
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						id = cursor.getString(0);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		int ret = context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
				ContactsContract.RawContacts._ID + " =? ", new String[] { id });

		return ret > 0 ? true : false;
	}
}
