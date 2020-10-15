package com.zte.functions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import android.content.Context;
import android.text.TextUtils;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsMd5;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.http.VsHttpClient;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;

/**
 * Created by Jiangxuewu on 2015/2/3.
 * <p>
 * 服务器请求处理基类 2处联网请求走这里，第一：获取token 第二：拉取企业信息号
 * </p>
 */
public abstract class BaseHttp {

	private static final String TAG = BaseHttp.class.getSimpleName();


	private String getSign(String content) {
		return VsMd5.md5(content);
	}
}
