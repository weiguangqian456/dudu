package com.zte.functions.agent;

import java.util.TreeMap;

import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.fragment.VsContactsListFragment;
import com.weiwei.json.me.JSONObject;
import com.zte.functions.BaseInfo;
import com.zte.functions.IHttpResult;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Jiangxuewu on 2015/2/3.
 * <p>
 * 接口： 获取企业号接口请求处理类
 * </p>
 */
public class GetAgentInfo extends BaseInfo {

	private static final String METHOD = "/ips/agent";
	private static final String BID = "bid";// 品牌ID
	private static final String UID = "userId";// 用户ID
	private static final String TAG = GetAgentInfo.class.getSimpleName();

	private String bid;
	private String uid;
	private String authType; // 签名类型
	private IHttpResult callBack;
	private Handler handler;

	private static int count = 0;

	public GetAgentInfo(Handler handler, String bid, String uid) {
		this.handler = handler;
		this.bid = bid;
		this.uid = uid;
		this.authType = DfineAction.authType_AUTO;
	}

	public GetAgentInfo(String bid, String uid, IHttpResult callBack) {
		this.bid = bid;
		this.uid = uid;
		this.callBack = callBack;
		this.authType = DfineAction.authType_AUTO;
	}

	/**
	 * 获取方法名
	 *
	 * @return
	 */
	@Override
	public String getMethod() {
		return METHOD;
	}

	/**
	 * 获取参数
	 *
	 * @param context
	 * @return
	 */
	@Override
	public TreeMap<String, String> getParamas(Context context) {
		TreeMap<String, String> bizparams = new TreeMap<String, String>();
		bizparams.put(BID, bid);
		bizparams.put(UID, bid + "-" + uid);
		return bizparams;
	}

	/**
	 * 处理请求结果
	 *
	 * @param context
	 * @param result
	 */
	@Override
	public void handleResult(Context context, String result) {

		if (null != callBack) {
			callBack.handleResult(context, result);
			return;
		}

		try {
			JSONObject json = new JSONObject(result);
			if (json.getInt("code") == 200) {
				if (result != null && !result.equals("")) {
					VsUserConfig.setData(context, VsUserConfig.JKEY_AGENT_INFO, result);
				}
				Message msg = handler.obtainMessage();
				msg.what = VsContactsListFragment.MSG_ID_SHOW_AGENT;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Message msg = handler.obtainMessage();
			msg.what = VsContactsListFragment.MSG_ID_SHOW_AGENT;
			msg.obj = null;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 获取签名参数
	 *
	 * @param context
	 * @return
	 */
	@Override
	public String getSignParams(Context context) {
		String uid = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
		return DfineAction.brandid + DfineAction.brandid + "-" + uid + getData();
	}

	@Override
	public String getAuthType() {
		return authType;
	}
}
