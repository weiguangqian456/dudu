package com.weiwei.base.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.weiwei.base.adapter.VsContactListAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.BaseRunable;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.fragment.VsContactsListFragment;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.util.SendNoteObserver;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description:选择联系人界面(推荐好友) 
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsContactsSelectActivity extends VsBaseActivity {
	/**
	 * 选中的邀请号码
	 */
	private ArrayList<String> isCheckNum =new ArrayList<String>(); 
	private InputMethodManager imm;
	private LinearLayout mSelectContactLayout;
	/**
	 * 选中所有
	 */
	private Button mSelectAllButton;
	/**
	 *  确定
	 */
	private Button mConfirmButton;
	/**
	 *  确定layout
	 */
	private LinearLayout mConfirmButton_layout;
	/**
	 *  删除
	 */
	private ImageView deleteImage;
	/**
	 *  搜索输入框
	 */
	private EditText ctsKeywordEdt;
	/**
	 * 搜索输入键盘
	 */
	private static LinearLayout mInputKeyboard;
	private Handler mHandle = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			return false;
		}
	});
	/**
	 * 用来放在WindowManager中显示提示字符
	 */
	private LinearLayout mCurrentLetterView;
	/**
	 * 9种透明字
	 */
	private TextView tv_content1, tv_content2, tv_content3, tv_content4, tv_content5, tv_content6, tv_content7,
			tv_content8, tv_content9;
	/**
	 *  联系人listview
	 */
	private ListView mContactListView = null; 
	/**
	 * 适配器
	 */
	public static VsContactListAdapter adapter = null;
	private WindowManager windowManager;
	// private TextView mSelectContactNum;// 选择人数提示
	/**
	 * 选中的联系人信息
	 */
	private ArrayList<VsContactItem> contactList = null;

	private View mAtoZView;
	/**
	 *  滚动的状态
	 */
	private int scrollState; 
	private String mCurrentLetter = "A";
	private DisapearThread disapearThread = new DisapearThread();

	private SendNoteObserver noteObserver;

	private boolean batchremove = false;
	private boolean invitecontact = false;
	private boolean inviteSend = false;
	/**
	 * 是否是发送名片
	 */
	private boolean sendPhoneCard =false;
	private boolean addgroupcontacts = false;
	public int now_index = 0;
	private int oldid = -100;
	private final int mAzId[] = { R.id.az02, R.id.az03, R.id.az04, R.id.az05, R.id.az06, R.id.az07,
			R.id.az08, R.id.az09, R.id.az10, R.id.az11, R.id.az12, R.id.az13, R.id.az14, R.id.az15, R.id.az16,
			R.id.az17, R.id.az18, R.id.az19, R.id.az20, R.id.az21, R.id.az22, R.id.az23, R.id.az24, R.id.az25,
			R.id.az26, R.id.az27,R.id.az01 };
	private final String mAzStr[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" ,"#" };

	// private int maxcount = 0;
	// private float balance = 0.00f;

	private byte conIsSel = -1;

	private String callback;
	private String callbacktype;
	private String titleName;
	private String groupId;
	/**
	 * 名片信息
	 */
	private String phoneCardInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_contacts_list);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		VsContactsListFragment.searchInput = false;
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		Intent intent = this.getIntent();
		batchremove = intent.getBooleanExtra("BATCHREMOVE", false);
		invitecontact = intent.getBooleanExtra("INVITECONTACT", false);
		inviteSend = intent.getBooleanExtra("INVITECONTACTSENDSMS", false);
	    sendPhoneCard = intent.getBooleanExtra("SENDCARDCONTACTSENDSMS", false);
		addgroupcontacts = intent.getBooleanExtra("ADDGROUPCONTACTS", false);
		titleName = intent.getStringExtra("titleName");
		callback = intent.getStringExtra("callback");
		callbacktype = intent.getStringExtra("callbacktype");
		groupId = intent.getStringExtra("groupId");
		phoneCardInfo=intent.getStringExtra("phoneCardInfo");
		if (batchremove) {
			mTitleTextView.setText("批量删除");
		} else if (inviteSend) {
			mTitleTextView.setText("邀请好友");
		} else if(sendPhoneCard){
			mTitleTextView.setText("选择联系人");
		}else if (addgroupcontacts) {
			mTitleTextView.setText("添加到" + titleName + "分组");
		} else {
			mTitleTextView.setText("选择好友");
		}
		initView();
		contactList = new ArrayList<VsContactItem>();
		if (invitecontact) {
			showRightTxtBtn("全选");
			adapter.allSelect(conIsSel);
			ArrayList<VsContactItem> selList = intent.getParcelableArrayListExtra("SELCONTACTDATALIST");
			if (selList != null) {
				for (VsContactItem item : selList) {
					int size = VsPhoneCallHistory.CONTACTLIST.size();
					for (int i = 0; i < size; i++) {
						if (VsPhoneCallHistory.CONTACTLIST.get(i).mContactId.equals(item.mContactId)) {
							VsPhoneCallHistory.CONTACTLIST.get(i).isSelect = (byte) 0;
							break;
						}
					}
				}
				contactList.addAll(selList);
			}
		}
		if (invitecontact) {
			if (contactList.size() > 1) {
				mConfirmButton.setText(getResources().getString(R.string.ok) + "(" + contactList.size() + ")");
			} else {
				mConfirmButton.setText(getResources().getString(R.string.ok));
			}
		}
		// else if (redpackcontact) {
		// adapter.allSelect(conIsSel);
		// ArrayList<KcContactItem> selList = intent.getParcelableArrayListExtra("SELCONTACTDATALIST");
		// if (selList != null) {
		// for (KcContactItem item : selList) {
		// int size = KcPhoneCallHistory.CONTACTLIST.size();
		// for (int i = 0; i < size; i++) {
		// if (KcPhoneCallHistory.CONTACTLIST.get(i).mContactId.equals(item.mContactId)) {
		// KcPhoneCallHistory.CONTACTLIST.get(i).isSelect = (byte) 0;
		// break;
		// }
		// }
		// }
		// contactList.addAll(selList);
		// }
		// maxcount = this.getIntent().getIntExtra("number", 0);
		// balance = this.getIntent().getFloatExtra("balance", 0);
		// adapter.setMaxCount(maxcount - contactList.size());
		// if (maxcount >= KcPhoneCallHistory.CONTACTLIST.size()) {
		// showRightTxtBtn("全选");
		// }
		// mSelectContactNum.setText(String.format(getString(R.string.redpack_share_contact_sel), balance, maxcount,
		// contactList.size()));
		// }
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

	@Override
	protected void HandleRightNavBtn() {
		super.HandleRightNavBtn();
		conIsSel = (byte) (conIsSel == 0 ? -1 : 0);
		adapter.allSelect(conIsSel);
		mBtnNavRightTv.setText(conIsSel == 0 ? "不选" : "全选");
		contactList.clear();
		if (conIsSel == 0) {
			contactList.addAll(VsPhoneCallHistory.CONTACTLIST);
		}
	}

	private void initView() {
		mContactListView = (ListView) findViewById(R.id.contactlistview);
		// 添加搜索输入框
		windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout mHeaderLayout = (LinearLayout) inflater.inflate(R.layout.vs_contacts_editext, null);
		mContactListView.setFocusable(false);
		mContactListView.addHeaderView(mHeaderLayout);
		// 搜索输入框
		ctsKeywordEdt = (EditText) findViewById(R.id.cts_keyword);
		ctsKeywordEdt.addTextChangedListener(new textChangedListener());
		deleteImage = (ImageView) findViewById(R.id.deleteImage);
		deleteImage.setOnClickListener(new deleteTextListener());

		// 添加通讯录数据
		adapter = new VsContactListAdapter(mContext, false, mBaseHandler);
		adapter.setData(VsPhoneCallHistory.CONTACTLIST, -1);
		mContactListView.setAdapter(adapter);

		mInputKeyboard = (LinearLayout) findViewById(R.id.input_keyboard);
		setupControlers();// 拨号键盘界面处理
		findViewById(R.id.DigitHideButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (VsUtil.isFastDoubleClick()) {
					return;
				}
				mInputKeyboard.setVisibility(View.GONE);
			}
		});// 隐藏键盘
		hideDigitsIM();// 隐藏系统键盘，显示自定义键盘

		noteObserver = new SendNoteObserver(mBaseHandler, mContext);
		this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, noteObserver);

		mSelectContactLayout = (LinearLayout) findViewById(R.id.select_contact_button_layout);
		mSelectContactLayout.setVisibility(View.VISIBLE);
		// mSelectContactNum = (TextView) findViewById(R.id.select_contact_num);
		mSelectAllButton = (Button) findViewById(R.id.selectAllButton);
		mConfirmButton = (Button) findViewById(R.id.confirmButton); // 确定

		if (invitecontact) {
			mSelectAllButton.setVisibility(View.GONE);
			mConfirmButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (VsUtil.isFastDoubleClick()) {
						return;
					}
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra("SELECTCONTACTLIST", contactList);
					intent.putExtra("callback", callback);
					intent.putExtra("callbacktype", callbacktype);
					setResult(0, intent);
					finish();
				}
			});
		} else if (batchremove) {
			mConfirmButton.setText(getResources().getString(R.string.contact_delete) + "(" + contactList.size() + ")");
			mConfirmButton.setOnClickListener(mDeleteOnClickListener);
			// 全选
			mSelectAllButton.setText(getResources().getString(R.string.contact_choose_all));
			mSelectAllButton.setOnClickListener(mSelectAllOnclickListener);
		} else if (inviteSend) {//邀请好友
			mSelectAllButton.setVisibility(View.GONE);
			mConfirmButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (VsUtil.isFastDoubleClick()) {
						return;
					}
				
					StringBuffer sbf = new StringBuffer();//拼接号码
					for (int i = 0; i < isCheckNum.size(); i++) {
						
						if (sbf.length() == 0) {
							sbf.append(isCheckNum.get(i));
						} else {
							sbf.append(";").append(isCheckNum.get(i));
						}
					}
					
					String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
					if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
						String InviteFriendInfo = DfineAction.InviteFriendInfo;
						String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
						if (uid != null && !"".equals(uid)) {
							StringBuilder builder = new StringBuilder(InviteFriendInfo);
							builder.append("a=").append(uid).append("&s=sm");
							mRecommendInfo = new String(builder);
						} else {
							mRecommendInfo = DfineAction.InviteFriendInfo;
						}
					} else {
						mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
					}
					VsUtil.smsShare(mContext, mRecommendInfo, sbf.toString());
				}
			});
		} else if(sendPhoneCard){//发送名片

			mSelectAllButton.setVisibility(View.GONE);
			mConfirmButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (VsUtil.isFastDoubleClick()) {
						return;
					}
				
					StringBuffer sbf = new StringBuffer();//拼接号码
					for (int i = 0; i < isCheckNum.size(); i++) {
						
						if (sbf.length() == 0) {
							sbf.append(isCheckNum.get(i));
						} else {
							sbf.append(";").append(isCheckNum.get(i));
						}
					}
					
					VsUtil.smsShare(mContext, phoneCardInfo, sbf.toString());
				}
			});
		
		}	
		mContactListView.setOnScrollListener(new ListViewScrollListener());
		new GetContacts().execute();
		populateFastClick();// 首字母提示
	}

	/**
	 * 取消选择联系人
	 */
	public static final char MSG_ID_DELETE_CONTACT = 12;
	/**
	 * 添加选择联系人
	 */
	public static final char MSG_ID_INVITE_CONTACT = 22;

	@Override
	protected void handleBaseMessage(Message msg) {
		super.handleBaseMessage(msg);
		//KcContactItem item = msg.getData().getParcelable("CONTACTINFO");
		
		String contactPhoneNumber=msg.getData().getString("ContactPhoneNumber");//获取号码
		switch (msg.what) {
		case MSG_ID_INVITE_CONTACT:
			//contactList.add(item);
			if(VsUtil.checkMobilePhone(contactPhoneNumber)){
				isCheckNum.add(contactPhoneNumber);
			}else{
				VsUtil.showT(mContext, "请选择有效的手机号码");
				return;
			}
			if (batchremove) {
				mConfirmButton.setText(getResources().getString(R.string.contact_delete) + "(" + isCheckNum.size()
						+ ")");
			}
			// else if (redpackcontact) {
			// mConfirmButton.setText(getResources().getString(R.string.ok));
			// mSelectContactNum.setVisibility(View.VISIBLE);
			// mSelectContactNum.setText(String.format(getString(R.string.redpack_share_contact_sel), balance,
			// maxcount, contactList.size()));
			// }
			else {
				mConfirmButton.setText(getResources().getString(R.string.ok) + "(" + isCheckNum.size() + ")");
			}
			break;
		case MSG_ID_DELETE_CONTACT:
			// contactList.remove(item);
			/*final String id = item.mContactId;
			for (int i = 0; i < contactList.size(); i++) {
				if (id.equals(contactList.get(i).mContactId))
					contactList.remove(i);
			}*/
			for(int i =0 ;i <isCheckNum.size();i++){
				if(isCheckNum.get(i).equals(contactPhoneNumber)){
					isCheckNum.remove(i);
				}
			}
			if (batchremove) {
				mConfirmButton.setText(getResources().getString(R.string.contact_delete) + "(" + isCheckNum.size()
						+ ")");
			}
			// else if (redpackcontact) {
			// mConfirmButton.setText(getResources().getString(R.string.ok));
			// mSelectContactNum.setText(String.format(getString(R.string.redpack_share_contact_sel), balance,
			// maxcount, contactList.size()));
			// mSelectContactNum.setVisibility(View.VISIBLE);
			// }
			else {
				mConfirmButton.setText(getResources().getString(R.string.ok) + "(" + isCheckNum.size() + ")");
			}
			break;
		case 300:
			dismissProgressDialog();
			mToast.show("批量删除联系人失败!", Toast.LENGTH_SHORT);
			break;
		case 400:
			dismissProgressDialog();
			contactList.clear();
			mConfirmButton.setText("删除(0)");
			mToast.show("批量删除联系人成功!", Toast.LENGTH_SHORT);
			finish();
			break;
		// case KcCommonContactHistory.MSG_ID_INSERTCONTACT_OK:
		// dismissProgressDialog();
		// mToast.show("收藏联系人成功!", Toast.LENGTH_SHORT);
		// break;
		// case KcCommonContactHistory.MSG_ID_INSERTCONTACT_ERROR:
		// dismissProgressDialog();
		// mToast.show("收藏联系人失败!", Toast.LENGTH_SHORT);
		// break;
		default:
			break;
		}
	}

	/**
	 * 全选
	 */
	private View.OnClickListener mSelectAllOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			// MobclickAgent.onEvent(mContext, "g3GhooseAllClick");
			String tag = mSelectAllButton.getTag().toString();
			if (tag.equals("yes")) {
				contactList.clear();
				contactList.addAll(VsPhoneCallHistory.CONTACTLIST);
				if (batchremove) {
					mConfirmButton.setText(getResources().getString(R.string.contact_delete) + "(" + contactList.size()
							+ ")");
				}
				mSelectAllButton.setTag("no");
				mSelectAllButton.setText(getResources().getString(R.string.contact_cancel_all));
				adapter.allSelect((byte) 0);
			} else {
				contactList.clear();
				if (batchremove) {
					mConfirmButton.setText(getResources().getString(R.string.contact_delete) + "(0)");
				}
				mSelectAllButton.setTag("yes");
				mSelectAllButton.setText(getResources().getString(R.string.contact_choose_all));
				adapter.allSelect((byte) -1);
			}
		}
	};

	/**
	 * 隐藏系统键盘，显示自定义键盘
	 */
	private void hideDigitsIM() {
		ctsKeywordEdt.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// ctsKeywordEdt.setInputType(InputType.TYPE_NULL); // 关闭软键盘
				if (android.os.Build.VERSION.SDK_INT <= 10) {
					ctsKeywordEdt.setInputType(InputType.TYPE_NULL);
				} else {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					try {
						Class<EditText> cls = EditText.class;
						Method setSoftInputShownOnFocus;
						setSoftInputShownOnFocus = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
						setSoftInputShownOnFocus.setAccessible(true);
						setSoftInputShownOnFocus.invoke(ctsKeywordEdt, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Class<EditText> cls = EditText.class;
						Method setShowSoftInputOnFocus;
						setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
						setShowSoftInputOnFocus.setAccessible(true);
						setShowSoftInputOnFocus.invoke(ctsKeywordEdt, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 显示自定义键盘
				mInputKeyboard.setVisibility(View.VISIBLE);
				return false;
			}
		});

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ctsKeywordEdt.getWindowToken(), 0);
	}

	private static boolean onKeyDownListener() {
		boolean retbool = true;
		int visibility = mInputKeyboard.getVisibility();
		if (visibility == View.VISIBLE) {
			mInputKeyboard.setVisibility(View.GONE);
			retbool = true;
		} else {
			retbool = false;
		}
		return retbool;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 点击回退以退出
			if (onKeyDownListener() == false) {
				return super.onKeyDown(keyCode, event);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SendNoteObserver.isSendSuc && SendNoteObserver.sendSendNoteNumber > 0) {
			adapter.setData(VsPhoneCallHistory.CONTACTLIST, -1);
			adapter.notifyDataSetChanged();
			showMessageDialog("温馨提示", "您已给" + SendNoteObserver.sendSendNoteNumber + "个好友发送了邀请短信，对方注册成功后，将马上给您赠送话费！",
					true);
		}
		SendNoteObserver.sendSendNoteNumber = 0;
		SendNoteObserver.isSendSuc = false;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (windowManager != null) {//销毁时将windowManager对象置为null
			if (mCurrentLetterView != null) {
//				windowManager.removeView(mCurrentLetterView);
			}
			windowManager = null;
		}
		if (adapter != null) {
			adapter.allSelect((byte) -1);
		}
		ctsKeywordEdt.setText("");
		getContentResolver().unregisterContentObserver(noteObserver);
	}

	/**
	 * 搜索框内容改变监听
	 * 
	 * @author dell
	 */
	private class textChangedListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() == 0) {
				ctsKeywordEdt.setTextSize(13);
			} else {
				ctsKeywordEdt.setTextSize(15);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// MobclickAgent.onEvent(mContext, "gnContactSearchInput");
			String keyword = s.toString();
			DataSetChangedNotify(keyword);
		}
	}

	/**
	 * 搜索处理
	 * 
	 * @param s
	 */
	private void DataSetChangedNotify(CharSequence s) {
		if (s.length() > 0) {
			VsContactsListFragment.searchInput = true;
			adapter.getFilter().filter(s);
			deleteImage.setVisibility(View.VISIBLE);
		} else {
			VsContactsListFragment.searchInput = false;
			ArrayList<VsContactItem> items = new ArrayList<VsContactItem>();
			items.addAll(VsPhoneCallHistory.CONTACTLIST);
			adapter.setData(items, -1);
			mContactListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			deleteImage.setVisibility(View.GONE);
		}
	}

	/**
	 * 搜索框内容清除监听
	 */
	private class deleteTextListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (ctsKeywordEdt.getText().length() > 0) {
				ctsKeywordEdt.setText("");
				ctsKeywordEdt.setSelection(ctsKeywordEdt.getText().length());
			}
		}
	}

	/**
	 * 批量删除监听
	 */
	private View.OnClickListener mDeleteOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			if (contactList.size() > 0) {
				showYesNoDialog("删除", "删除" + contactList.size() + "个联系人？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loadProgressDialog("正在删除联系人信息，请稍后...");
						// 使用runable实现多线程
						BaseRunable newRunable = new BaseRunable() {
							public void run() { // 线程运行主体
								VsUtil.deleteContactAll(mContext, mBaseHandler, contactList);
							}
						};
						// 使用线程池进行管理
						GlobalVariables.fixedThreadPool.execute(newRunable);
					}
				}, null, null);
			}
		}
	};

	/**
	 * 收藏联系人监听
	 */
	// private View.OnClickListener mCommonOnClickListener = new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (contactList.size() > 0) {
	// loadProgressDialog("正在收藏联系人，请稍后...");
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// KcCommonContactHistory.insertContact(contactList, mBaseHandler, mContext);
	// finish();
	// }
	// }).start();
	// }
	// }
	// };

	/**
	 * 联系人ListView滑动监听
	 * 
	 * @author dell
	 */
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (VsPhoneCallHistory.CONTACTLIST != null) {
				int countSize = VsPhoneCallHistory.CONTACTLIST.size();
				// 以第一个ListItem为标准项来显示
				if (firstVisibleItem == 0) {
					if (mCurrentLetterView != null) {
						mCurrentLetterView.setVisibility(View.INVISIBLE);
					}
					if (oldid != -100) {
						((TextView) findViewById(oldid)).setTextColor(mContext.getResources().getColor(
								R.color.vs_gray_deep));
					}
				} else if (VsPhoneCallHistory.CONTACTLIST != null && mCurrentLetterView != null && countSize > 0) {
					try {
						String contactPinYin = VsPhoneCallHistory.CONTACTLIST.get(firstVisibleItem - 1).mContactFirstLetter;
						mCurrentLetter = contactPinYin.substring(0, 1);
						int i = 1;
						do {
							if (mCurrentLetter.equals(mAzStr[i]) && mAzId[i] != oldid) {
								if (oldid != -100) {
									((TextView) findViewById(oldid)).setTextColor(mContext.getResources().getColor(
											R.color.vs_gray_deep));
								}
								oldid = mAzId[i];
								((TextView) findViewById(oldid)).setTextColor(mContext.getResources().getColor(
										R.color.croci));
								break;
							}
							i++;
						} while (i < mAzStr.length);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mInputKeyboard.setVisibility(View.GONE);
			// if (scrollState ==
			// ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			// mHandle.removeCallbacks(disapearThread);
			// // 提示延迟1.5s再消失
			// mHandle.postDelayed(disapearThread, 1000);
			// }
		}
	}

	/**
	 * 拨号键盘界面处理
	 */
	private void setupControlers() {
		/* 注册拨号相关数字键 */
		findViewById(R.id.DigitZeroButton).setOnClickListener(mDigintButtonListener);// "0"
		findViewById(R.id.DigitOneButton).setOnClickListener(mDigintButtonListener);// "1"
		findViewById(R.id.DigitTwoButton).setOnClickListener(mDigintButtonListener);// "2"
		findViewById(R.id.DigitThreeButton).setOnClickListener(mDigintButtonListener);// "3"
		findViewById(R.id.DigitFourButton).setOnClickListener(mDigintButtonListener);// "4"
		findViewById(R.id.DigitFiveButton).setOnClickListener(mDigintButtonListener);// "5"
		findViewById(R.id.DigitSixButton).setOnClickListener(mDigintButtonListener);// "6"
		findViewById(R.id.DigitSevenButton).setOnClickListener(mDigintButtonListener);// "7"
		findViewById(R.id.DigitEightButton).setOnClickListener(mDigintButtonListener);// "8"
		findViewById(R.id.DigitNineButton).setOnClickListener(mDigintButtonListener);// "9"
		findViewById(R.id.DigitDeleteButton).setOnClickListener(mDigintButtonListener);// 删除键
		findViewById(R.id.DigitHideButton).setOnClickListener(mDigintButtonListener);// 收起
		findViewById(R.id.DigitHideButton).setBackgroundResource(R.drawable.vs_dia_keyboard_sq_selecter);
		findViewById(R.id.DigitDeleteButton).setOnLongClickListener(new mDigintButtonLongListener());// 删除键长按
	}

	/**
	 * 按键监听
	 */
	private View.OnClickListener mDigintButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.DigitZeroButton:// "0"键
				keyPressed(KeyEvent.KEYCODE_0);
				break;
			case R.id.DigitOneButton:// "1"
				keyPressed(KeyEvent.KEYCODE_1);
				break;
			case R.id.DigitTwoButton:// "2"
				keyPressed(KeyEvent.KEYCODE_2);
				break;
			case R.id.DigitThreeButton:// "3"
				keyPressed(KeyEvent.KEYCODE_3);
				break;
			case R.id.DigitFourButton:// "4"
				keyPressed(KeyEvent.KEYCODE_4);
				break;
			case R.id.DigitFiveButton:// "5"
				keyPressed(KeyEvent.KEYCODE_5);
				break;
			case R.id.DigitSixButton:// "6"
				keyPressed(KeyEvent.KEYCODE_6);
				break;
			case R.id.DigitSevenButton:// "7"
				keyPressed(KeyEvent.KEYCODE_7);
				break;
			case R.id.DigitEightButton: // "8"
				keyPressed(KeyEvent.KEYCODE_8);
				break;
			case R.id.DigitNineButton:// "9"
				keyPressed(KeyEvent.KEYCODE_9);
				break;
			case R.id.DigitDeleteButton:// "x"
				keyPressed(KeyEvent.KEYCODE_DEL);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 删除键长按监听
	 * 
	 * @author dell
	 */
	private class mDigintButtonLongListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			if (ctsKeywordEdt.getText().length() > 0) {
				ctsKeywordEdt.setText("");
				ctsKeywordEdt.setSelection(ctsKeywordEdt.getText().length());
				return true;
			}
			return false;
		}
	}

	/**
	 * 输入电话号码
	 * 
	 * @param keyCode
	 */
	private void keyPressed(int keyCode) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		ctsKeywordEdt.onKeyDown(keyCode, event);
		mContactListView.setSelection(0);
		if (keyCode == KeyEvent.KEYCODE_DEL && ctsKeywordEdt.getText().toString().length() <= 0) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 异步任务类，用于检索联系人
	 */
	private class GetContacts extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mHandle.post(new UpdateUi());
		}
	}

	private class UpdateUi implements Runnable {
		public void run() {
			SetUPLetterNavio();// 字母导航
			
		}
	}

	/**
	 * 设置字母导航数据
	 */
	public void SetUPLetterNavio() {
		mAtoZView = findViewById(R.id.aazz);// 父
		// mAtoZLayout = findViewById(R.id.kc_a_z);// 子
		final int count = ((LinearLayout) mAtoZView).getChildCount();
		mAtoZView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x1 = (int) event.getX();
				int y1 = (int) event.getY();
				Rect frame = new Rect();
				v.getHitRect(frame);
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					for (int index = 0; index < count; index++) {
						View view = ((ViewGroup) mAtoZView).getChildAt(index);
						view.getHitRect(frame);
						if (frame.contains(x1, y1)) {
							showUpLetter(view);
							return true;
						}
					}
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					for (int index = 0; index < count; index++) {
						View view = ((ViewGroup) mAtoZView).getChildAt(index);
						view.getHitRect(frame);
						if (frame.contains(x1, y1)) {
							showUpLetter(view);
							return true;
						}
					}
					// } else if (event.getAction() == MotionEvent.ACTION_UP) {
					// mAtoZLayout.setBackgroundResource(R.drawable.contact_list_scroll_long_normal);
				}
				return false;
			}
		});
	}

	/**
	 * 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
	 * 
	 * @author dell
	 */
	private class DisapearThread implements Runnable {
		public void run() {
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				mCurrentLetterView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置9种字体透明度
	 * 
	 * @param view
	 */
	private void showUpLetter(View view) {
		mCurrentLetter = (String) view.getTag();
		if ("#".equals(mCurrentLetter)) {
			mContactListView.setSelection(mContactListView.getCount()-1);
			if (oldid != -100) {
				((TextView) findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.Black));
			}
		} else {
			if (oldid == -100) {
				oldid = view.getId();
				((TextView) findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.croci));
			} else {
				((TextView) findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.Black));
				oldid = view.getId();
				((TextView) findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.croci));
			}
		}
		for (int i = 0; i < mAzStr.length; i++) {
			if (mCurrentLetter.equals(mAzStr[i])) {
				now_index = i;
			}
		}
		if (now_index == 2) {
			tv_content1.setVisibility(View.GONE);
			tv_content2.setVisibility(View.VISIBLE);
			tv_content3.setVisibility(View.VISIBLE);
			tv_content2.setText(mAzStr[now_index - 2]);
			tv_content2.setTextColor(VsUtil.setTransparency(3));
			tv_content3.setText(mAzStr[now_index - 1]);
			tv_content3.setTextColor(VsUtil.setTransparency(2));
			setAD();
		} else if (now_index == 1) {
			tv_content1.setVisibility(View.GONE);
			tv_content2.setVisibility(View.GONE);
			tv_content3.setVisibility(View.VISIBLE);
			tv_content3.setText(mAzStr[now_index - 1]);
			tv_content3.setTextColor(VsUtil.setTransparency(2));
			setAD();
		} else if (now_index == 0) {
			tv_content1.setVisibility(View.GONE);
			tv_content2.setVisibility(View.GONE);
			tv_content3.setVisibility(View.GONE);
			setAD();
		} else if (now_index == 26) {
			tv_content5.setVisibility(View.GONE);
			tv_content6.setVisibility(View.GONE);
			tv_content7.setVisibility(View.GONE);
			tv_content8.setVisibility(View.GONE);
			tv_content9.setVisibility(View.GONE);
			setSZ();
		} else if (now_index == 25) {
			tv_content5.setVisibility(View.VISIBLE);
			tv_content6.setVisibility(View.GONE);
			tv_content7.setVisibility(View.GONE);
			tv_content8.setVisibility(View.GONE);
			tv_content9.setVisibility(View.GONE);
			tv_content5.setText(mAzStr[now_index + 1]);
			tv_content5.setTextColor(VsUtil.setTransparency(2));
			setSZ();
		} else if (now_index == 24) {
			tv_content5.setVisibility(View.VISIBLE);
			tv_content6.setVisibility(View.VISIBLE);
			tv_content7.setVisibility(View.GONE);
			tv_content8.setVisibility(View.GONE);
			tv_content9.setVisibility(View.GONE);
			tv_content5.setText(mAzStr[now_index + 1]);
			tv_content5.setTextColor(VsUtil.setTransparency(2));
			tv_content6.setText(mAzStr[now_index + 2]);
			tv_content6.setTextColor(VsUtil.setTransparency(3));
			setSZ();
		} else if (now_index == 23) {
			tv_content5.setVisibility(View.VISIBLE);
			tv_content6.setVisibility(View.VISIBLE);
			tv_content7.setVisibility(View.VISIBLE);
			tv_content8.setVisibility(View.GONE);
			tv_content9.setVisibility(View.GONE);
			tv_content5.setText(mAzStr[now_index + 1]);
			tv_content5.setTextColor(VsUtil.setTransparency(2));
			tv_content6.setText(mAzStr[now_index + 2]);
			tv_content6.setTextColor(VsUtil.setTransparency(3));
			tv_content7.setText(mAzStr[now_index + 3]);
			tv_content7.setTextColor(VsUtil.setTransparency(4));
			setSZ();
		} else if (now_index == 22) {
			tv_content5.setVisibility(View.VISIBLE);
			tv_content6.setVisibility(View.VISIBLE);
			tv_content7.setVisibility(View.VISIBLE);
			tv_content8.setVisibility(View.VISIBLE);
			tv_content9.setVisibility(View.GONE);
			tv_content5.setText(mAzStr[now_index + 1]);
			tv_content5.setTextColor(VsUtil.setTransparency(2));
			tv_content6.setText(mAzStr[now_index + 2]);
			tv_content6.setTextColor(VsUtil.setTransparency(3));
			tv_content7.setText(mAzStr[now_index + 3]);
			tv_content7.setTextColor(VsUtil.setTransparency(4));
			tv_content8.setText(mAzStr[now_index + 4]);
			tv_content8.setTextColor(VsUtil.setTransparency(5));
			setSZ();
		} else {
			tv_content1.setVisibility(View.VISIBLE);
			tv_content2.setVisibility(View.VISIBLE);
			tv_content3.setVisibility(View.VISIBLE);
			tv_content1.setText(mAzStr[now_index - 3]);
			tv_content1.setTextColor(VsUtil.setTransparency(4));
			tv_content2.setText(mAzStr[now_index - 2]);
			tv_content2.setTextColor(VsUtil.setTransparency(3));
			tv_content3.setText(mAzStr[now_index - 1]);
			tv_content3.setTextColor(VsUtil.setTransparency(2));
			setAD();
		}
		mCurrentLetterView.setVisibility(View.VISIBLE);
		mHandle.removeCallbacks(disapearThread);
		mHandle.postDelayed(disapearThread, 700);
		int localPosition = binSearch(VsPhoneCallHistory.CONTACTLIST, mCurrentLetter); // 接收返回值
		if (localPosition != -1) {
			mContactListView.setSelection(localPosition + 1); // 让List指向对应位置的Item
		}
	}

	/**
	 * S-Z处理
	 */
	private void setSZ() {
		tv_content1.setVisibility(View.VISIBLE);
		tv_content2.setVisibility(View.VISIBLE);
		tv_content3.setVisibility(View.VISIBLE);
		tv_content4.setVisibility(View.VISIBLE);
		tv_content1.setText(mAzStr[now_index - 3]);
		tv_content1.setTextColor(VsUtil.setTransparency(4));
		tv_content2.setText(mAzStr[now_index - 2]);
		tv_content2.setTextColor(VsUtil.setTransparency(3));
		tv_content3.setText(mAzStr[now_index - 1]);
		tv_content3.setTextColor(VsUtil.setTransparency(2));
		tv_content4.setText(mAzStr[now_index]);
		tv_content4.setTextColor(getResources().getColor(R.color.vs_white));
		// setTextColor();
	}

	// private void setTextColor() {
	// tv_content1.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content2.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content3.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content4.setTextColor(getResources().getColor(R.color.White));
	// tv_content5.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content6.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content7.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content8.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// tv_content9.setTextColor(getResources().getColor(R.color.titlebar_blue));
	// }

	/**
	 * A-D处理
	 */
	private void setAD() {
		tv_content4.setVisibility(View.VISIBLE);
		tv_content5.setVisibility(View.VISIBLE);
		tv_content6.setVisibility(View.VISIBLE);
		tv_content7.setVisibility(View.VISIBLE);
		tv_content8.setVisibility(View.VISIBLE);
		tv_content9.setVisibility(View.VISIBLE);
		tv_content4.setText(mAzStr[now_index]);
		tv_content4.setTextColor(getResources().getColor(R.color.vs_white));
		tv_content5.setText(mAzStr[now_index + 1]);
		tv_content5.setTextColor(VsUtil.setTransparency(2));
		tv_content6.setText(mAzStr[now_index + 2]);
		tv_content6.setTextColor(VsUtil.setTransparency(3));
		tv_content7.setText(mAzStr[now_index + 3]);
		tv_content7.setTextColor(VsUtil.setTransparency(4));
		tv_content8.setText(mAzStr[now_index + 4]);
		tv_content8.setTextColor(VsUtil.setTransparency(5));
		tv_content9.setText(mAzStr[now_index + 5]);
		tv_content9.setTextColor(VsUtil.setTransparency(6));
		// setTextColor();
	}

	/**
	 * 将选中的py与stringArr的首字符进行匹配并返回对应字符串在数组中的位置
	 * 
	 * @param list
	 * @param s
	 * @return
	 */
	private int binSearch(List<VsContactItem> list, String s) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (s.equalsIgnoreCase("" + list.get(i).mContactFirstLetter.charAt(0))) { // 不区分大小写
				return i;
			}
		}
		return -1;
	}

	/**
	 * 首字母提示
	 */
	private void populateFastClick() {
		if (mCurrentLetterView == null) {
//			mCurrentLetterView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_popup_char_hint,null);
            mCurrentLetterView = (LinearLayout) findViewById(R.id.pop_view);
			tv_content1 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num1);
			tv_content2 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num2);
			tv_content3 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num3);
			tv_content4 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num4);
			tv_content5 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num5);
			tv_content6 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num6);
			tv_content7 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num7);
			tv_content8 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num8);
			tv_content9 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num9);
			// 默认设置为不可见。
			mCurrentLetterView.setVisibility(View.INVISIBLE);
			// 设置WindowManager
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
					// 设置为无焦点状态
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					// 半透明效果
					PixelFormat.TRANSLUCENT);
			try {
//				windowManager.addView(mCurrentLetterView, lp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
