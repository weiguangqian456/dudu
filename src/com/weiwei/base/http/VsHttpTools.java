package com.weiwei.base.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.rc.rc.Rc;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;

public class VsHttpTools {
	private static String TAG = "VsHttpTools";
	private Context context;
	private static VsHttpTools httpTools = null;

	public VsHttpTools(Context context) {
		this.context = context;
	}

	public static VsHttpTools getInstance(Context context) {
		if (httpTools == null) {
			httpTools = new VsHttpTools(context);
		}
		return httpTools;
	}

	private String uri_prefix = null;
	private String uri_port = null;

	public String getUri_prefix() {
		if (uri_prefix == null || uri_prefix.length() == 0) {
			uri_prefix = VsUserConfig.getDataString(context, VsUserConfig.JKey_UriAndPort);
		}
		return uri_prefix;
	}


	public void setUri_prefix(String uri_prefix) {
		this.uri_prefix = uri_prefix;
		VsUserConfig.setData(context, VsUserConfig.JKey_UriAndPort, uri_prefix);
	}

	@SuppressWarnings("deprecation")
	public JSONObject doGetMethod(String uri, TreeMap<String, String> treeMap, String url, String authType) {
		JSONObject jsonOuter = null;
		String jsonstr = null;
		try {
			String gProxyHost = android.net.Proxy.getDefaultHost();
			int gProxyPort = android.net.Proxy.getDefaultPort();
			VsHttpClient httpClient = new VsHttpClient(uri);
			httpClient.setProxyHost(gProxyHost);
			httpClient.setProxyPort(gProxyPort);
			if (GlobalVariables.netmode == 1) {
				httpClient.setProxyHost(null);
				httpClient.setProxyPort(-1);
			}
			jsonstr = httpClient.excuteDoMaxTime(20 * 1000, treeMap, url, authType);
			CustomLog.i(TAG, "url = " + uri + ",jsonstr=" + jsonstr);
			if (jsonstr != null && jsonstr.length() > 0) {
				if (jsonstr.indexOf("{") > -1)
					jsonOuter = new JSONObject(jsonstr);
				else
					jsonOuter = new JSONObject("{\"ipaddr\": \"" + jsonstr + "\"}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonOuter;
	}

	public static byte[] readStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, len);
		}

		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	// 得到html代码
	public String getHtml(String urlpath) throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(6 * 1000);
		conn.setRequestMethod("GET");

		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			String html = new String(data);
			return html;
		}
		return null;
	}

	public String GetStringFromJSON(JSONObject getObj, String item) {
		String getStr = null;
		try {
			getStr = String.valueOf(getObj.get(item));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getStr;
	}

	@SuppressWarnings("null")
	public Map<String, String> parseJsonString(String[] items, String jsonstr) {
		Map<String, String> result = null;
		try {
			JSONObject getdata = new JSONObject(jsonstr);
			int len = items.length;
			for (int i = len - 1; i >= 0; i--) {
				result.put(items[i], getdata.getString(items[i]));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String convertStreamToString(InputStream is) {
		String str = "";
		try {
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream(1024);
			int ch = 0;
			while ((ch = is.read()) != -1) {
				byteOs.write(ch);
			}
			str = new String(byteOs.toByteArray(), "utf-8");
			byteOs.close();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		CustomLog.i(TAG, "convertStreamToString=" + str);
		return str;
	}

	/**
	 * 是否插入SD卡
	 * 
	 * @return
	 */
	public static boolean isExistsSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取得本地通讯录个数
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getContactsCount(Context mContext) {
        Cursor cur = null;
        try {
			ContentResolver cr = mContext.getContentResolver();
			cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			return cur.getCount();
		} catch (Exception e) {
            CustomLog.e(TAG, "getContactsCount(), error : " + e.getLocalizedMessage());
		} finally {
            if (cur != null && !cur.isClosed()){
                cur.close();
            }
        }
        return 0;
	}

	/**
	 * Post提交
	 * 
	 *            路径
	 *            编码
	 * @return
	 * @throws Exception
	 * @author: 石云升
	 * @version: 2012-3-13 下午02:53:13
	 */
	@SuppressWarnings("deprecation")
	public JSONObject sendPostRequest(String uri, Context mContext, String paramStr) {
		JSONObject jsonOuter = null;
		String jsonstr = null;
		try {
			byte[] jsonData = paramStr.getBytes("utf-8");
			String gProxyHost = android.net.Proxy.getDefaultHost();
			int gProxyPort = android.net.Proxy.getDefaultPort();
			VsHttpClient httpClient = new VsHttpClient(uri);
			httpClient.setProxyHost(gProxyHost);
			httpClient.setProxyPort(gProxyPort);
			if (VsUtil.isWifi(context)) {
				httpClient.setProxyHost(null);
				httpClient.setProxyPort(-1);
			}
			jsonstr = httpClient.excutePost(jsonData);
			
			// jsonstr = httpClient.excuteDoMaxTime(20 * 1000, reqParam, url, authType);
	            CustomLog.i(TAG, "sendPostRequest url = " + uri + ",jsonstr=" + jsonstr);
			if (jsonstr != null) {
				jsonOuter = new JSONObject(jsonstr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonOuter;
	}
	
	public JSONObject sendPostRequesthttp(final String uri, Context mContext, TreeMap<String, String> params) {
		JSONObject jsonOuter = null;
		String jsonstr = null;
        // 创建一个请求  
        HttpPost httpPost = new HttpPost(uri);  
        try {  
            JSONObject jsonhttp = new JSONObject();
            try {
        		Iterator iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					jsonhttp.put(String.valueOf(key), val);
				}
		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //rc4加密
 //           Log.e("rcget_url", uri);
 //           Log.e("rcget_param", jsonhttp.toString());
            String rc_String = Rc.putString(jsonhttp.toString(), 1);
            // 设置编码  
            StringEntity se = new StringEntity(rc_String, "utf-8");
            httpPost.setEntity(se);
            // 发送请求 并获得反馈  
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // 解析返回的数据  
            if (httpResponse.getStatusLine().getStatusCode() != 404) {//判断服务器状态  
                jsonstr = EntityUtils.toString(httpResponse.getEntity());
              //  Log.e("jsonstr", jsonstr);
              //rc4解密 -- 崩溃率高且无法解决（）
				String rcget_String = "";
				try {
					rcget_String = Rc.getString(jsonstr, 1);
				}catch (Exception ex){
					ex.printStackTrace();
				}

 //               Log.e("rcget_String", rcget_String);
                try {
					jsonOuter = new JSONObject(rcget_String);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println(jsonstr);  
            }  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		return jsonOuter;
	}
}
