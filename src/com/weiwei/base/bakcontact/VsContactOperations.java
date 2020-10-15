package com.weiwei.base.bakcontact;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;

public class VsContactOperations {

	private final ContentValues mValues;
	private ContentProviderOperation.Builder mBuilder;
	private final VsBatchOperation mBatchOperation;
	private boolean mYield;
	private long mRawContactId;
	private int mBackReference;
	private boolean mIsNewContact;

	/**
	 * Returns an instance of KcContactOperations instance for adding new
	 * contact to the platform contacts provider.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param userId
	 *            the userId of the sample SyncAdapter user object
	 * @param accountName
	 *            the username of the current login
	 * @return instance of KcContactOperations
	 */
	public static VsContactOperations createNewContact(Context context, String accountName, VsBatchOperation batchOperation) {
		return new VsContactOperations(context, accountName, batchOperation);
	}

	public VsContactOperations(Context context, VsBatchOperation batchOperation) {
		mValues = new ContentValues();
		mYield = true;
		mBatchOperation = batchOperation;
	}

	public VsContactOperations(Context context, String accountName, VsBatchOperation batchOperation) {
		this(context, batchOperation);
		mBackReference = mBatchOperation.size();
		mIsNewContact = true;
		mBuilder = newInsertCpo(RawContacts.CONTENT_URI, true).withValues(mValues);
		mBatchOperation.add(mBuilder.build());
	}

	/**
	 * Adds a contact name
	 * 
	 * @param name
	 *            Name of contact
	 * @param nameType
	 *            type of name: family name, given name, etc.
	 * @return instance of KcContactOperations
	 */
	public VsContactOperations addName(String showName, String firstName, String lastName, String middlename, String prefix, String suffix,
			String firstname_phonetic, String lastname_phonetic, String middlename_phonetic) {
		mValues.clear();
		if (!TextUtils.isEmpty(showName)) {
			mValues.put(StructuredName.DISPLAY_NAME, showName);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(firstName)) {
			mValues.put(StructuredName.GIVEN_NAME, firstName);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(lastName)) {
			mValues.put(StructuredName.FAMILY_NAME, lastName);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}

		if (!TextUtils.isEmpty(middlename)) {
			mValues.put(StructuredName.MIDDLE_NAME, middlename);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(prefix)) {
			mValues.put(StructuredName.PREFIX, prefix);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(suffix)) {
			mValues.put(StructuredName.SUFFIX, suffix);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(firstname_phonetic)) {
			mValues.put(StructuredName.PHONETIC_GIVEN_NAME, firstname_phonetic);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(lastname_phonetic)) {
			mValues.put(StructuredName.PHONETIC_FAMILY_NAME, lastname_phonetic);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (!TextUtils.isEmpty(middlename_phonetic)) {
			mValues.put(StructuredName.PHONETIC_MIDDLE_NAME, middlename_phonetic);
			mValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		}
		if (mValues.size() > 0) {
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addNickName(String nickName) {
		mValues.clear();
		if (!TextUtils.isEmpty(nickName)) {
			mValues.put(Nickname.NAME, nickName);
			mValues.put(Nickname.MIMETYPE, Nickname.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addCDP(String company, String department, String postion) {
		mValues.clear();
		if (!TextUtils.isEmpty(company) || !TextUtils.isEmpty(department) || !TextUtils.isEmpty(postion)) {
			mValues.put(Organization.COMPANY, company != null ? company : "");
			mValues.put(Organization.DEPARTMENT, department != null ? department : "");
			mValues.put(Organization.TITLE, postion != null ? postion : "");
			mValues.put(Organization.TYPE, Organization.TYPE_WORK);
			mValues.put(Organization.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addAP(String address, String postcode, int type) {
		mValues.clear();
		if (!TextUtils.isEmpty(address) || !TextUtils.isEmpty(postcode)) {
			mValues.put(StructuredPostal.FORMATTED_ADDRESS, address);
			mValues.put(StructuredPostal.POSTCODE, postcode);
			mValues.put(StructuredPostal.TYPE, type);
			mValues.put(StructuredPostal.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addAddress(String neighborhood, String pobox, String address, String city, String postcode, String state,
			String country, String addressLabe, int type) {
		mValues.clear();
		if (!TextUtils.isEmpty(address) || !TextUtils.isEmpty(postcode) || !TextUtils.isEmpty(city)) {
			// mValues.put(StructuredPostal.FORMATTED_ADDRESS, address);
			mValues.put(StructuredPostal.STREET, address);
			mValues.put(StructuredPostal.CITY, city);
			mValues.put(StructuredPostal.COUNTRY, country);
			mValues.put(StructuredPostal.POSTCODE, postcode);
			mValues.put(StructuredPostal.REGION, state);
			mValues.put(StructuredPostal.NEIGHBORHOOD, neighborhood);
			mValues.put(StructuredPostal.POBOX, pobox);
			if (type == 0) {
				mValues.put(StructuredPostal.LABEL, addressLabe);
			}
			mValues.put(StructuredPostal.TYPE, type);
			mValues.put(StructuredPostal.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addIM(String service, String userName, String lable, int im_type, int service_type) {
		mValues.clear();
		if (!TextUtils.isEmpty(service) || !TextUtils.isEmpty(userName) || !TextUtils.isEmpty(lable)) {
			// mValues.put(StructuredPostal.FORMATTED_ADDRESS, address);
			mValues.put(Im.DATA, userName);
			if (im_type == 0) {
				mValues.put(Im.LABEL, lable);
			}
			if (service_type == -1) {
				mValues.put(Im.CUSTOM_PROTOCOL, service);
			}
			mValues.put(Im.PROTOCOL, service_type);
			mValues.put(Im.TYPE, im_type);
			mValues.put(Im.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addEvent(String startDate, String customLable, int type) {
		mValues.clear();
		if (!TextUtils.isEmpty(customLable)) {
			if (!TextUtils.isEmpty(startDate)) {
				mValues.put(Event.START_DATE, startDate);
				if (type == 0) {
					mValues.put(Event.LABEL, customLable);
				}
				mValues.put(Event.TYPE, type);
				mValues.put(Event.MIMETYPE, Event.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

	public VsContactOperations addWebsite(String website, String urlLable, int websiteType) {
		mValues.clear();
		if (!TextUtils.isEmpty(urlLable)) {
			if (!TextUtils.isEmpty(website)) {
				mValues.put(Website.URL, website);
				if (websiteType == 0) {
					mValues.put(Website.LABEL, urlLable);
				}
				mValues.put(Website.TYPE, websiteType);
				mValues.put(Website.MIMETYPE, Website.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

	public VsContactOperations addRelation(String relation, String urlLable, int relationType) {
		mValues.clear();
		if (!TextUtils.isEmpty(urlLable)) {
			if (!TextUtils.isEmpty(relation)) {
				mValues.put(Relation.NAME, relation);
				if (relationType == 0) {
					mValues.put(Relation.LABEL, urlLable);
				}
				mValues.put(Relation.TYPE, relationType);
				mValues.put(Relation.MIMETYPE, Relation.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

	public VsContactOperations addRemark(String remark) {
		mValues.clear();
		if (!TextUtils.isEmpty(remark)) {
			mValues.put(Note.NOTE, remark);
			mValues.put(Note.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	public VsContactOperations addGid(int gid) {
		mValues.clear();
		if (gid != 0) {
			mValues.put(GroupMembership.GROUP_ROW_ID, gid);
			mValues.put(GroupMembership.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
			addInsertOp();
		} else {
			mValues.put(GroupMembership.GROUP_ROW_ID, 1);
			mValues.put(GroupMembership.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	/**
	 * Adds an email
	 * 
	 * @param new email for user
	 * @return instance of ContactOperations
	 */

	public VsContactOperations addEmail(String email, String emialLable, int emailType) {
		mValues.clear();
		if (!TextUtils.isEmpty(emialLable)) {
			if (!TextUtils.isEmpty(email)) {
				mValues.put(Email.DATA, email);
				if (emailType == 0) {
					mValues.put(Email.LABEL, emialLable);
				}
				mValues.put(Email.TYPE, emailType);
				mValues.put(Email.MIMETYPE, Email.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

	public VsContactOperations addAvatar(String avatarUrl, boolean isWifi) {
		if (avatarUrl != null) {
			byte[] avatarBuffer = downloadAvatar(avatarUrl, isWifi);
			if (avatarBuffer != null) {
				mValues.clear();
				mValues.put(Photo.PHOTO, avatarBuffer);
				mValues.put(Photo.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

    /**
     * 添加头像，本地头像
     * @param photo
     * @return
     */
	public VsContactOperations addAvatar(byte[] photo) {
			if (photo != null) {
				mValues.clear();
				mValues.put(Photo.PHOTO, photo);
				mValues.put(Photo.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		return this;
	}

	/**
	 * Adds a phone number
	 * 
	 * @param phone
	 *            new phone number for the contact
	 * @param phoneType
	 *            the type: cell, home, etc.
	 * @return instance of ContactOperations
	 */
	public VsContactOperations addPhone(String phone, int phoneType) {
		mValues.clear();
		if (!TextUtils.isEmpty(phone)) {
			mValues.put(Phone.NUMBER, phone);
			mValues.put(Phone.TYPE, phoneType);
			mValues.put(Phone.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			addInsertOp();
		}
		return this;
	}

	/**
	 * Adds a phone number
	 * 
	 * @param phone
	 *            new phone number for the contact
	 * @param phoneType
	 *            the type: cell, home, etc.
	 * @return instance of ContactOperations
	 */
	public VsContactOperations addPhone(String phone, String customLable, int phoneType) {
		mValues.clear();
		if (!TextUtils.isEmpty(customLable)) {
			if (!TextUtils.isEmpty(phone)) {
				mValues.put(Phone.NUMBER, phone);
				if (phoneType == 0) {
					mValues.put(Phone.LABEL, customLable);
				}
				mValues.put(Phone.TYPE, phoneType);
				mValues.put(Phone.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				addInsertOp();
			}
		}
		return this;
	}

	/**
	 * Updates contact's phone
	 * 
	 * @param existingNumber
	 *            phone number stored in contacts provider
	 * @param phone
	 *            new phone number for the contact
	 * @param uri
	 *            Uri for the existing raw contact to be updated
	 * @return instance of KcContactOperations
	 */
	public VsContactOperations updatePhone(String existingNumber, String phone, Uri uri, int type) {
		if (!TextUtils.equals(phone, existingNumber)) {
			mValues.clear();
			mValues.put(Phone.NUMBER, phone);
			mValues.put(Phone.TYPE, type);
			addUpdateOp(uri);
		}
		return this;
	}

	/**
	 * Adds an insert operation into the batch
	 */
	private void addInsertOp() {
		if (!mIsNewContact) {
			mValues.put(Phone.RAW_CONTACT_ID, mRawContactId);
		}
		mBuilder = newInsertCpo(addCallerIsSyncAdapterParameter(Data.CONTENT_URI), mYield);
		mBuilder.withValues(mValues);
		if (mIsNewContact) {
			mBuilder.withValueBackReference(Data.RAW_CONTACT_ID, mBackReference);
		}
		mYield = false;
		mBatchOperation.add(mBuilder.build());
	}

	/**
	 * Adds an update operation into the batch
	 */
	private void addUpdateOp(Uri uri) {
		mBuilder = newUpdateCpo(uri, mYield).withValues(mValues);
		mYield = false;
		mBatchOperation.add(mBuilder.build());
	}

	public static ContentProviderOperation.Builder newInsertCpo(Uri uri, boolean yield) {
		return ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(uri)).withYieldAllowed(yield);
	}

	public static ContentProviderOperation.Builder newUpdateCpo(Uri uri, boolean yield) {
		return ContentProviderOperation.newUpdate(addCallerIsSyncAdapterParameter(uri)).withYieldAllowed(yield);
	}

	public static ContentProviderOperation.Builder newDeleteCpo(Uri uri, boolean yield) {
		return ContentProviderOperation.newDelete(addCallerIsSyncAdapterParameter(uri)).withYieldAllowed(yield);

	}

	private static Uri addCallerIsSyncAdapterParameter(Uri uri) {
		return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
	}

	@SuppressWarnings("deprecation")
	private byte[] downloadAvatar(String avatarUrl, boolean isWifi) {
		if (TextUtils.isEmpty(avatarUrl)) {
			return null;
		}
		HttpURLConnection connection = null;
		InputStream is = null;
		try {
			URL url = new URL(avatarUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				connection = (HttpURLConnection) url.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getDefaultHost(),
						android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.connect();
			try {
				is = connection.getInputStream();
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
				int ch;
				while ((ch = is.read()) != -1) {
					bytestream.write(ch);
				}
				byte imgdata[] = bytestream.toByteArray();
				bytestream.flush();
				bytestream.close();
				return imgdata;
			} finally {
				if (is != null) {
					is.close();
					is = null;
				}
				connection.disconnect();
			}
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return null;
	}
}
