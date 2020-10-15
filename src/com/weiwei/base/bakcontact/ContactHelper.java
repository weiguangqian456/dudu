package com.weiwei.base.bakcontact;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.Log;

/**
 * 此类描述的是：获取联系人信息
 * 
 * @author: 谢康林
 * @version: 2012-9-28 下午03:08:18
 */
public class ContactHelper {
	public final static int ALL_CONTACT_GROUP = 1;

	private static final String TAG = ContactHelper.class.getSimpleName();

	/**
	 * 此方法描述的是：通讯录备份联系人读取
	 * 
	 * @author: 谢康林
	 * @version: 2012-9-28 下午03:07:37
	 */
	public static ArrayList<VsBackContactItem> getContactDetail_v2(Context ctext) {
		// 获得所有的联系人
		ArrayList<VsBackContactItem> dcitemList = new ArrayList<VsBackContactItem>();
		ContentResolver content = ctext.getContentResolver();
		String seletion = ContactsContract.Data.DISPLAY_NAME + " <> '我的名片' ";
		Cursor cur = content.query(ContactsContract.Data.CONTENT_URI, null, seletion, null, null);
		HashMap<Integer, VsBackContactItem> map = new HashMap<Integer, VsBackContactItem>();
		// 循环遍历
		if (cur == null)
			return null;
		while (cur.moveToNext()) {
			VsBackContactItem oneContact = null;
			int idColumn = cur.getColumnIndex(ContactsContract.Data.CONTACT_ID);
			// 获得联系人的ID号
			int contactId = cur.getInt(idColumn);
			if (!map.containsKey(contactId)) {
				oneContact = new VsBackContactItem();
				map.put(contactId, oneContact);
			} else {
				oneContact = map.get(contactId);
			}
			oneContact.contactid = contactId;
			// 获得联系人姓名
			String disPlayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			oneContact.display_name = disPlayName;
			String mimeTypeString = cur.getString(cur.getColumnIndex(Data.MIMETYPE));
			if (Nickname.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String nickNameString = cur.getString(cur.getColumnIndex(Nickname.NAME));
				oneContact.nickname = nickNameString;
			}
			if (StructuredName.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String middNameString = cur.getString(cur.getColumnIndex(StructuredName.MIDDLE_NAME));
				String firstNameString = cur.getString(cur.getColumnIndex(StructuredName.GIVEN_NAME));
				String lastNameString = cur.getString(cur.getColumnIndex(StructuredName.FAMILY_NAME));
				String prefixString = cur.getString(cur.getColumnIndex(StructuredName.PREFIX));
				String suffixString = cur.getString(cur.getColumnIndex(StructuredName.SUFFIX));
				String firstname_phonetic = cur.getString(cur.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
				String middlename_phonetic = cur.getString(cur.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
				String lastname_phonetic = cur.getString(cur.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
				oneContact.firstname = firstNameString;
				oneContact.lastname = lastNameString;
				oneContact.middlename = middNameString;
				oneContact.prefix = prefixString;
				oneContact.suffix = suffixString;
				oneContact.firstname_phonetic = firstname_phonetic;
				oneContact.middlename_phonetic = middlename_phonetic;
				oneContact.lastname_phonetic = lastname_phonetic;
			}

			// 获取公司，职位(Organization.TITLE)，部门
			if (Organization.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				oneContact.company = cur.getString(cur.getColumnIndex(Organization.COMPANY));
				oneContact.department = cur.getString(cur.getColumnIndex(Organization.DEPARTMENT));
				oneContact.postion = cur.getString(cur.getColumnIndex(Organization.TITLE));
			}

			// 获取分组
			if (GroupMembership.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.gid == null)
					oneContact.gid = new ArrayList<Integer>();
				int id = cur.getInt((cur.getColumnIndex(GroupMembership.GROUP_ROW_ID)));
				oneContact.gid.add(id);
			}

			if (Phone.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				int typeIndex = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				switch (typeIndex) {
				case Phone.TYPE_OTHER:
				case Phone.TYPE_MOBILE: {
					if (oneContact.mobileGeneral == null) {
						oneContact.mobileGeneral = new ArrayList<String>();
					}
					oneContact.mobileGeneral.add(phoneNumber);
				}
					break;
				case Phone.TYPE_WORK: {
					if (oneContact.phoneWork == null) {
						oneContact.phoneWork = new ArrayList<String>();
					}
					oneContact.phoneWork.add(phoneNumber);
				}
					break;

				case Phone.TYPE_HOME: {
					if (oneContact.phoneHome == null) {
						oneContact.phoneHome = new ArrayList<String>();
					}
					oneContact.phoneHome.add(phoneNumber);
				}
					break;
				case Phone.TYPE_FAX_HOME: {
					if (oneContact.faxHome == null) {
						oneContact.faxHome = new ArrayList<String>();
					}
					oneContact.faxHome.add(phoneNumber);
				}
					break;
				case Phone.TYPE_FAX_WORK: {
					if (oneContact.faxWork == null) {
						oneContact.faxWork = new ArrayList<String>();
					}
					oneContact.faxWork.add(phoneNumber);
				}
					break;
				case Phone.TYPE_OTHER_FAX: {
					if (oneContact.faxGeneral == null) {
						oneContact.faxGeneral = new ArrayList<String>();
					}
					oneContact.faxGeneral.add(phoneNumber);
				}
					break;
				default: {
					if (oneContact.mobileGeneral == null) {
						oneContact.mobileGeneral = new ArrayList<String>();
					}
					oneContact.mobileGeneral.add(phoneNumber);
				}
				}
			}
			if (Email.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String emailContent = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				int typeIndex = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				switch (typeIndex) {
				case Email.TYPE_WORK: {
					if (oneContact.emailWork == null) {
						oneContact.emailWork = new ArrayList<String>();
					}
					oneContact.emailWork.add(emailContent);
				}
					break;
				case Email.TYPE_HOME: {
					if (oneContact.emailHome == null) {
						oneContact.emailHome = new ArrayList<String>();
					}
					oneContact.emailHome.add(emailContent);
				}
					break;

				case Email.TYPE_OTHER:
					// default://其他类型统一放到emailGeneral里面
				{
					if (oneContact.emailGeneral == null) {
						oneContact.emailGeneral = new ArrayList<String>();
					}
					oneContact.emailGeneral.add(emailContent);
				}
				}
			}

			if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String address = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
				String postCode = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				int typeIndex_po = cur.getInt(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				switch (typeIndex_po) {
				case StructuredPostal.TYPE_WORK: {
					oneContact.addressWork = address;
					oneContact.postcodeWork = postCode;

				}
					break;
				case StructuredPostal.TYPE_HOME: {
					oneContact.addressHome = address;
					oneContact.postcodeHome = postCode;
				}
					break;
				case StructuredPostal.TYPE_OTHER:
					// default:
				{
					oneContact.addressOther = address;
					oneContact.postcodeOther = postCode;
				}
				}
			}

			if (Website.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				String website = cur.getString(cur.getColumnIndex(Website.URL));
				int typeIndex_web = cur.getInt(cur.getColumnIndex(Website.TYPE));
				switch (typeIndex_web) {
				case Website.TYPE_HOME: {
					oneContact.websiteHome = website;
				}
					break;
				case Website.TYPE_WORK: {
					oneContact.websiteWork = website;
				}
				case Website.TYPE_OTHER:
					// default:
				{
					oneContact.websiteOther = website;
				}
					break;
				}
			}

			if (Note.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				oneContact.remark = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
			}

			if (Event.CONTENT_ITEM_TYPE.endsWith(mimeTypeString)) {
				oneContact.birthday = cur.getString(cur.getColumnIndex(Event.DATA1));
			}

			// 新的方案
			if (Phone.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.phone == null) {
					oneContact.phone = new ArrayList<PhoneLable>();
				}
				String phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				String lable = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
				PhoneLable phone = new PhoneLable();
				phone.lable = VsContactOpUtil.getPhoneLable(ctext, lable, type);
				phone.phoneNumber = phoneNumber;
				oneContact.phone.add(phone);
			}

			if (Email.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.email == null) {
					oneContact.email = new ArrayList<EmailLable>();
				}
				String emailContent = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				String emailLable = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
				EmailLable email = new EmailLable();
				email.email = emailContent;
				email.lable = VsContactOpUtil.getEmailLbale(ctext, emailLable, type);
				oneContact.email.add(email);
			}

			if (Website.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.url == null) {
					oneContact.url = new ArrayList<UrlLable>();
				}
				String website = cur.getString(cur.getColumnIndex(Website.URL));
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
				String websiteLable = cur.getString(cur.getColumnIndex(Website.LABEL));
				UrlLable url = new UrlLable();
				url.url = website;
				url.lable = VsContactOpUtil.getUrlLbale(ctext, websiteLable, type);
				oneContact.url.add(url);
			}

			if (Relation.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.relatedName == null) {
					oneContact.relatedName = new ArrayList<RelationLable>();
				}
				String relation = cur.getString(cur.getColumnIndex(Relation.NAME));
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Relation.TYPE));
				String relationLable = cur.getString(cur.getColumnIndex(Relation.LABEL));
				RelationLable relationName = new RelationLable();
				relationName.name = relation;
				relationName.lable = VsContactOpUtil.getRelationLable(relationLable, type);
				oneContact.relatedName.add(relationName);
			}

			if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.address == null) {
					oneContact.address = new ArrayList<AddressLable>();
				}
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				String street = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				String city = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				String country = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				String postCode = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				String region = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				String addressLable = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL));
				String neighborhood = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
				String pobox = cur.getString(cur
						.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
				AddressLable address = new AddressLable();
				address.street = street;
				address.city = city;
				address.countrykey = country;
				address.zip = postCode;
				address.state = region;
				address.neighborhood = neighborhood;
				address.pobox = pobox;
				address.lable = VsContactOpUtil.getAddressLable(ctext, addressLable, type);
				oneContact.address.add(address);
			}

			if (Im.CONTENT_ITEM_TYPE.equals(mimeTypeString)) {
				if (oneContact.instantMessage == null) {
					oneContact.instantMessage = new ArrayList<InstantMessage>();
				}
				int type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
				int protocol_type = cur.getInt(cur.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
				String service = cur.getString(cur.getColumnIndex(Im.CUSTOM_PROTOCOL));
				String userName = cur.getString(cur.getColumnIndex(Im.DATA));
				String IMlable = cur.getString(cur.getColumnIndex(Im.LABEL));
				InstantMessage message = new InstantMessage();
				message.lable = VsContactOpUtil.getImLable(ctext, IMlable, type);
				message.service = VsContactOpUtil.getImProtocolLable(service, protocol_type);
				message.userName = userName;
				oneContact.instantMessage.add(message);
			}
		}
		if (cur != null)
			cur.close();
		dcitemList.addAll(map.values());// 把每个人的详细信息添加到联系人列表
		return dcitemList;
	}

	/**
	 * 此方法描述的是：通讯录恢复
	 * 
	 * @param itemList
	 * @param ctext
	 * @return
	 * @author: 谢康林
	 * @version: 2012-1-30 上午09:49:25
	 */
	public static boolean recoverContacts_Cover(ArrayList<VsBackContactItem> itemList, Context ctext) {
		if (itemList == null || itemList.size() == 0)
			return false;
		VsContactManager.syncContacts(ctext, itemList);
		return true;
	}
	
	/**
	 * 此方法描述的是:入参号码是否有在通讯录中保存
	 * 
	 * @param context
	 * @param number
	 * @return
	 * @version:2014年12月24日
	 * @author:Jiangxuewu
	 *
	 */
	public static boolean isExitInContacts(Context context, String number) {
		if (TextUtils.isEmpty(number)) {
			return false;
		}
		long startTime = System.currentTimeMillis();
		boolean result = false;
		ContentResolver cr = null;
		String seletion = ContactsContract.CommonDataKinds.Phone.NUMBER + " ='"
				+ number + "'";
		Cursor c = null;

		try {
			cr = context.getContentResolver();
			c = cr.query(ContactsContract.Data.CONTENT_URI, null, seletion,
					null, null);
			if (null != c && c.getCount() >= 1) {
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != c && !c.isClosed()) {
				c.close();
			}
		}

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "take time " + (endTime - startTime));
		return result;
	}
}
