package com.weiwei.base.bakcontact;

import java.util.ArrayList;

import android.content.Context;

import com.weiwei.base.common.CustomLog;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

/**
 * 通讯录恢复和备份
 */
public class ContactSync {
	private final static String TAG = "ContactSync";

	public static ContactSync cs;

	public static ContactSync getInstance() {
		if (cs == null) {
			cs = new ContactSync();
		}
		return cs;
	}

	/**
	 * 通讯录备份
	 * 
	 * @author: 谢康林
	 * @version: 2012-5-14 上午11:41:02
	 */
	public String getBakContactData(Context context) {
		ArrayList<VsBackContactItem> contactlist = ContactHelper.getContactDetail_v2(context);

		// JSONObject contactObject = new JSONObject();
		JSONArray contactArray = new JSONArray();
		{
			try {
				// JSONArray contactArray = new JSONArray();
				for (int i = 0; i < contactlist.size(); i++) {
					JSONObject object = new JSONObject();
					VsBackContactItem item = contactlist.get(i);
					try {
						object.put("contactid", item.contactid);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (item.gid != null) {
						JSONArray gidArray = new JSONArray();
						for (int j = 0; j < item.gid.size(); j++) {
							gidArray.put(item.gid.get(j));
						}
						try {
							object.put("gid", gidArray);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					if (item.nickname != null && item.nickname.length() > 0) {
						object.put("nickname", getReplaceAllString(item.nickname));
					}
					if (item.firstname != null && item.firstname.length() > 0) {
						object.put("firstname", getReplaceAllString(item.firstname));
					}
					if (item.lastname != null && item.lastname.length() > 0) {
						object.put("lastname", getReplaceAllString(item.lastname));
					}
					if (item.middlename != null && item.middlename.length() > 0) {
						object.put("middlename", getReplaceAllString(item.middlename));
					}
					if (item.prefix != null && item.prefix.length() > 0) {
						object.put("prefix", getReplaceAllString(item.prefix));
					}
					if (item.suffix != null && item.suffix.length() > 0) {
						object.put("suffix", getReplaceAllString(item.suffix));
					}
					if (item.firstname_phonetic != null && item.firstname_phonetic.length() > 0) {
						object.put("firstname_phonetic", getReplaceAllString(item.firstname_phonetic));
					}
					if (item.lastname_phonetic != null && item.lastname_phonetic.length() > 0) {
						object.put("lastname_phonetic", getReplaceAllString(item.lastname_phonetic));
					}
					if (item.middlename_phonetic != null && item.middlename_phonetic.length() > 0) {
						object.put("middlename_phonetic", getReplaceAllString(item.middlename_phonetic));
					}
					if (item.display_name != null && item.display_name.length() > 0) {
						object.put("display_name", getReplaceAllString(item.display_name));
					}
					if (item.picmd5 != null && item.picmd5.length() > 0) {
						object.put("picmd5", getReplaceAllString(item.picmd5));
					}
					if (item.birthday != null && item.birthday.length() > 0) {
						object.put("birthday", getReplaceAllString(item.birthday));
					}
					if (item.picture != null && item.picture.length() > 0) {
						object.put("picture", getReplaceAllString(item.picture));
					}
					if (item.company != null && item.company.length() > 0) {
						object.put("company", getReplaceAllString(item.company));
					}
					if (item.department != null && item.department.length() > 0) {
						object.put("department", getReplaceAllString(item.department));
					}
					if (item.postion != null && item.postion.length() > 0) {
						object.put("postion", getReplaceAllString(item.postion));
					}
					if (item.remark != null && item.remark.length() > 0) {
						object.put("remark", getReplaceAllString(item.remark));
					}

					if (item.phone != null) {
						JSONArray phoneArray = new JSONArray();
						for (int j = 0; j < item.phone.size(); j++) {
							JSONObject pObject = new JSONObject();
							pObject.put("lable", getReplaceAllString(item.phone.get(j).lable));
							pObject.put("phoneNumber", getReplaceAllString(item.phone.get(j).phoneNumber));
							phoneArray.put(pObject);
						}
						object.put("phone", phoneArray);
					}
					if (item.email != null) {
						JSONArray emailArray = new JSONArray();
						for (int j = 0; j < item.email.size(); j++) {
							JSONObject eObject = new JSONObject();
							eObject.put("lable", getReplaceAllString(item.email.get(j).lable));
							eObject.put("email", getReplaceAllString(item.email.get(j).email));
							emailArray.put(eObject);
						}
						object.put("email", emailArray);
					}
					if (item.url != null) {
						JSONArray urlArray = new JSONArray();
						for (int j = 0; j < item.url.size(); j++) {
							JSONObject uObject = new JSONObject();
							uObject.put("lable", getReplaceAllString(item.url.get(j).lable));
							uObject.put("url", getReplaceAllString(item.url.get(j).url));
							urlArray.put(uObject);
						}
						object.put("url", urlArray);
					}
					if (item.relatedName != null) {
						JSONArray relatedNameArray = new JSONArray();
						for (int j = 0; j < item.relatedName.size(); j++) {
							JSONObject rObject = new JSONObject();
							rObject.put("lable", getReplaceAllString(item.relatedName.get(j).lable));
							rObject.put("name", getReplaceAllString(item.relatedName.get(j).name));
							relatedNameArray.put(rObject);
						}
						object.put("relatedName", relatedNameArray);
					}
					if (item.address != null) {
						JSONArray addressArray = new JSONArray();
						for (int j = 0; j < item.address.size(); j++) {
							JSONObject aObject = new JSONObject();
							AddressLable lable = item.address.get(j);
							if (lable != null) {
								aObject.put("lable", getReplaceAllString(lable.lable));
								aObject.put("zip", getReplaceAllString(lable.zip));
								aObject.put("state", getReplaceAllString(lable.state));
								aObject.put("street", getReplaceAllString(lable.street));
								aObject.put("city", getReplaceAllString(lable.city));
								aObject.put("countrykey", getReplaceAllString(lable.countrykey));
								aObject.put("neighborhood", getReplaceAllString(lable.neighborhood));
								aObject.put("pobox", getReplaceAllString(lable.pobox));
							}
							addressArray.put(aObject);
						}
						object.put("address", addressArray);
					}
					if (item.instantMessage != null) {
						JSONArray instantMessageArray = new JSONArray();
						for (int j = 0; j < item.instantMessage.size(); j++) {
							JSONObject iObject = new JSONObject();
							InstantMessage message = item.instantMessage.get(j);
							iObject.put("lable", getReplaceAllString(message.lable));
							iObject.put("service", getReplaceAllString(message.service));
							iObject.put("userName", getReplaceAllString(message.userName));
							instantMessageArray.put(iObject);
						}
						object.put("instantMessage", instantMessageArray);
					}
					if (item.mobileGeneral != null) {
						JSONArray mobileGeneralArray = new JSONArray();
						for (int j = 0; j < item.mobileGeneral.size(); j++) {
							mobileGeneralArray.put(getReplaceAllString(item.mobileGeneral.get(j).toString()));
						}
						object.put("mobileGeneral", mobileGeneralArray);
					}
					CustomLog.i(TAG, "object mobileGeneral=" + object.toString());
					if (item.mobileWork != null) {
						JSONArray mobileWorkArray = new JSONArray();
						for (int j = 0; j < item.mobileWork.size(); j++) {
							mobileWorkArray.put(getReplaceAllString(item.mobileWork.get(j).toString()));
						}
						object.put("mobileWork", mobileWorkArray);
					}
					if (item.mobileHome != null) {
						JSONArray mobileHomeArray = new JSONArray();
						for (int j = 0; j < item.mobileHome.size(); j++) {
							mobileHomeArray.put(getReplaceAllString(item.mobileHome.get(j).toString()));
						}
						object.put("mobileHome", mobileHomeArray);
					}
					if (item.phoneGeneral != null) {
						JSONArray phoneGeneralArray = new JSONArray();
						for (int j = 0; j < item.phoneGeneral.size(); j++) {
							phoneGeneralArray.put(getReplaceAllString(item.phoneGeneral.get(j).toString()));
						}
						object.put("phoneGeneral", phoneGeneralArray);
					}
					if (item.phoneWork != null) {
						JSONArray phoneWorkArray = new JSONArray();
						for (int j = 0; j < item.phoneWork.size(); j++) {
							phoneWorkArray.put(getReplaceAllString(item.phoneWork.get(j).toString()));
						}
						object.put("phoneWork", phoneWorkArray);
					}
					if (item.phoneHome != null) {
						JSONArray phoneHomeArray = new JSONArray();
						for (int j = 0; j < item.phoneHome.size(); j++) {
							phoneHomeArray.put(getReplaceAllString(item.phoneHome.get(j).toString()));
						}
						object.put("phoneHome", phoneHomeArray);
					}
					if (item.emailGeneral != null) {
						JSONArray emailGeneralArray = new JSONArray();
						for (int j = 0; j < item.emailGeneral.size(); j++) {
							emailGeneralArray.put(getReplaceAllString(item.emailGeneral.get(j).toString()));
						}
						object.put("emailGeneral", emailGeneralArray);
					}
					if (item.emailWork != null) {
						JSONArray emailWorkArray = new JSONArray();
						for (int j = 0; j < item.emailWork.size(); j++) {
							emailWorkArray.put(getReplaceAllString(item.emailWork.get(j).toString()));
						}
						object.put("emailWork", emailWorkArray);
					}
					if (item.emailHome != null) {
						JSONArray emailHomeArray = new JSONArray();
						for (int j = 0; j < item.emailHome.size(); j++) {
							emailHomeArray.put(getReplaceAllString(item.emailHome.get(j).toString()));
						}
						object.put("emailHome", emailHomeArray);
					}
					if (item.faxGeneral != null) {
						JSONArray faxGeneralArray = new JSONArray();
						for (int j = 0; j < item.faxGeneral.size(); j++) {
							faxGeneralArray.put(getReplaceAllString(item.faxGeneral.get(j).toString()));
						}
						object.put("faxGeneral", faxGeneralArray);
					}
					if (item.faxWork != null) {
						JSONArray faxWorkArray = new JSONArray();
						for (int j = 0; j < item.faxWork.size(); j++) {
							faxWorkArray.put(getReplaceAllString(item.faxWork.get(j).toString()));
						}
						object.put("faxWork", faxWorkArray);
					}
					if (item.faxHome != null) {
						JSONArray faxHomeArray = new JSONArray();
						for (int j = 0; j < item.faxHome.size(); j++) {
							faxHomeArray.put(getReplaceAllString(item.faxHome.get(j).toString()));
						}
						object.put("faxHome", faxHomeArray);
					}
					contactArray.put(object);
				}
				// contactObject.put("contactlist", contactArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		String contactString = contactArray.toString();// contactObject.toString();
		CustomLog.i(TAG, "联系人备份数据：" + contactString);
		return contactString;
	}

	/**
	 * 去掉\和"
	 * 
	 * @param str
	 * @return
	 */
	private String getReplaceAllString(String str) {
		String ret = str;
		if (ret != null) {
			ret = ret.replaceAll("\"", "");
			while (ret.indexOf("\\") != -1) {
				ret = ret.replace("\\", "");
			}
		}
		return ret;
	}

	/**
	 * 通讯录恢复
	 * 
	 * @author: 谢康林
	 * @version: 2012-5-14 上午11:40:14
	 */
	public boolean recoverPhoneContatcs_Cover(JSONObject obj, Context context) {
		if (obj == null)
			return false;
		return ContactHelper.recoverContacts_Cover(StringToContactList(obj), context);
	}

	public ArrayList<VsBackContactItem> StringToContactList(JSONObject object) {
		if (object == null)
			return null;
		ArrayList<VsBackContactItem> contactList = null;
		try {
			if (!object.has("contactlist"))
				return null;

			{
				contactList = new ArrayList<VsBackContactItem>();
				JSONArray contactArray = object.getJSONArray("contactlist");
				if (contactArray != null) {
					for (int i = 0; i < contactArray.length(); i++) {
						JSONObject contactObject = contactArray.getJSONObject(i);
						VsBackContactItem item = new VsBackContactItem();
						{
							if (contactObject.has("gid")) {
								JSONArray gidArray = contactObject.getJSONArray("gid");
								if (gidArray != null) {
									item.gid = new ArrayList<Integer>(gidArray.length());
									for (int j = 0; j < gidArray.length(); j++) {
										item.gid.add(gidArray.getInt(j));
									}
								}
							}
							if (contactObject.has("nickname")) {
								item.nickname = contactObject.getString("nickname");
							}
							if (contactObject.has("firstname")) {
								item.firstname = contactObject.getString("firstname");
							}
							if (contactObject.has("lastname")) {
								item.lastname = contactObject.getString("lastname");
							}
							if (contactObject.has("middlename")) {
								item.middlename = contactObject.getString("middlename");
							}
							if (contactObject.has("prefix")) {
								item.prefix = contactObject.getString("prefix");
							}
							if (contactObject.has("suffix")) {
								item.suffix = contactObject.getString("suffix");
							}
							if (contactObject.has("lastname_phonetic")) {
								item.lastname_phonetic = contactObject.getString("lastname_phonetic");
							}
							if (contactObject.has("lastname_phonetic")) {
								item.lastname_phonetic = contactObject.getString("lastname_phonetic");
							}
							if (contactObject.has("middlename_phonetic")) {
								item.middlename_phonetic = contactObject.getString("middlename_phonetic");
							}
							if (contactObject.has("contactid")) {
								item.contactid = contactObject.getInt("contactid");
							}
							if (contactObject.has("display_name")) {
								item.display_name = contactObject.getString("display_name");
							}
							if (contactObject.has("picmd5")) {
								item.picmd5 = contactObject.getString("picmd5");
							}
							if (contactObject.has("birthday")) {
								item.birthday = contactObject.getString("birthday");
							}
							if (contactObject.has("picture")) {
								item.picture = contactObject.getString("picture");
							}
							if (contactObject.has("company")) {
								item.company = contactObject.getString("company");
							}
							if (contactObject.has("department")) {
								item.department = contactObject.getString("department");
							}
							if (contactObject.has("postion")) {
								item.postion = contactObject.getString("postion");
							}
							if (contactObject.has("remark")) {
								item.remark = contactObject.getString("remark");
							}

							if (contactObject.has("phone")) {
								JSONArray phoneArray = contactObject.getJSONArray("phone");
								if (phoneArray != null) {
									item.phone = new ArrayList<PhoneLable>(phoneArray.length());
									for (int j = 0; j < phoneArray.length(); j++) {
										JSONObject pObject = phoneArray.getJSONObject(j);
										if (pObject != null) {
											PhoneLable pLabel = new PhoneLable();
											if (pObject.has("lable")) {
												pLabel.lable = pObject.getString("lable");
											}
											if (pObject.has("phoneNumber")) {
												pLabel.phoneNumber = pObject.getString("phoneNumber");
											}
											item.phone.add(pLabel);
										}
									}
								}
							}

							if (contactObject.has("email")) {
								JSONArray emailArray = contactObject.getJSONArray("email");
								if (emailArray != null) {
									item.email = new ArrayList<EmailLable>(emailArray.length());
									for (int j = 0; j < emailArray.length(); j++) {
										JSONObject eObject = emailArray.getJSONObject(j);
										if (eObject != null) {
											EmailLable eLabel = new EmailLable();
											if (eObject.has("lable")) {
												eLabel.lable = eObject.getString("lable");
											}
											if (eObject.has("email")) {
												eLabel.email = eObject.getString("email");
											}
											item.email.add(eLabel);
										}
									}
								}
							}

							if (contactObject.has("url")) {
								JSONArray urlArray = contactObject.getJSONArray("url");
								if (urlArray != null) {
									item.url = new ArrayList<UrlLable>(urlArray.length());
									for (int j = 0; j < urlArray.length(); j++) {
										JSONObject uObject = urlArray.getJSONObject(j);
										if (uObject != null) {
											UrlLable uLabel = new UrlLable();
											if (uObject.has("lable")) {
												uLabel.lable = uObject.getString("lable");
											}
											if (uObject.has("url")) {
												uLabel.url = uObject.getString("url");
											}
											item.url.add(uLabel);
										}
									}
								}
							}

							if (contactObject.has("relatedName")) {
								JSONArray relatedNameArray = contactObject.getJSONArray("relatedName");
								if (relatedNameArray != null) {
									item.relatedName = new ArrayList<RelationLable>(relatedNameArray.length());
									for (int j = 0; j < relatedNameArray.length(); j++) {
										JSONObject rObject = relatedNameArray.getJSONObject(j);
										if (rObject != null) {
											RelationLable rLabel = new RelationLable();
											if (rObject.has("lable")) {
												rLabel.lable = rObject.getString("lable");
											}
											if (rObject.has("name")) {
												rLabel.name = rObject.getString("name");
											}
											item.relatedName.add(rLabel);
										}
									}
								}
							}

							if (contactObject.has("address")) {
								JSONArray addressArray = contactObject.getJSONArray("address");
								if (addressArray != null) {
									item.address = new ArrayList<AddressLable>(addressArray.length());
									for (int j = 0; j < addressArray.length(); j++) {
										JSONObject rObject = addressArray.getJSONObject(j);
										if (rObject != null) {
											AddressLable aLabel = new AddressLable();
											if (rObject.has("lable")) {
												aLabel.lable = rObject.getString("lable");
											}
											if (rObject.has("zip")) {
												aLabel.zip = rObject.getString("zip");
											}
											if (rObject.has("state")) {
												aLabel.state = rObject.getString("state");
											}
											if (rObject.has("street")) {
												aLabel.street = rObject.getString("street");
											}
											if (rObject.has("city")) {
												aLabel.city = rObject.getString("city");
											}
											if (rObject.has("countrykey")) {
												aLabel.countrykey = rObject.getString("countrykey");
											}
											if (rObject.has("neighborhood")) {
												aLabel.neighborhood = rObject.getString("neighborhood");
											}
											if (rObject.has("pobox")) {
												aLabel.pobox = rObject.getString("pobox");
											}
											item.address.add(aLabel);
										}
									}
								}
							}

							if (contactObject.has("instantMessage")) {
								JSONArray instantMessageArray = contactObject.getJSONArray("instantMessage");
								if (instantMessageArray != null) {
									item.instantMessage = new ArrayList<InstantMessage>(instantMessageArray.length());
									for (int j = 0; j < instantMessageArray.length(); j++) {
										JSONObject iObject = instantMessageArray.getJSONObject(j);
										if (iObject != null) {
											InstantMessage iLabel = new InstantMessage();
											if (iObject.has("lable")) {
												iLabel.lable = iObject.getString("lable");
											}
											if (iObject.has("service")) {
												iLabel.service = iObject.getString("service");
											}
											if (iObject.has("userName")) {
												iLabel.userName = iObject.getString("userName");
											}
											item.instantMessage.add(iLabel);
										}
									}
								}
							}
						}
						contactList.add(item);
					}
				}
			}
			if (contactList.size() == 0) {
				CustomLog.v("ContactSync", "convert to google gson failure");
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return contactList;
	}
}
