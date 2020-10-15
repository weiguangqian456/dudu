package com.weiwei.base.http;

import java.net.URI;
import java.util.Hashtable;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.netphone.data.process.CoreBusiness;

public class VsHttpClient {
	public static int TIME_OUT_DELAY = 10 * 1000;
	private final String TAG = "VsHttpClient";
	private String uri;
	private String proxyHost;
	private int proxyPort;
	
	public VsHttpClient(String uri) {
		this.uri = uri;
	}

	/**
	 * 在一定时间内持续尝试连接
	 * 
	 * @param duration
	 * @return
	 */
	public String excuteDoMaxTime(long duration, TreeMap<String, String> treeMap, String url, String authType) {
		String strResult = "";
		long startTime = System.currentTimeMillis();
		do {
			strResult = excute();
			long usedTime = System.currentTimeMillis() - startTime;
			if (strResult == null || strResult.length() == 0) {
				if (usedTime >= duration) {
					try {
						if (treeMap != null) {
//							uri = url
//									+ CoreBusiness.getInstance().returnParamStr(VsApplication.getContext(), treeMap,
//											authType);
						}
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			} else {
				break;
			}
		} while (true);
		return strResult;
	}

	public String excute() {
		String strResult = "";
		DefaultHttpClient httpClient = null;
		boolean needRedirect = false;
		String redirectUrl = uri;
		int noJsonTypeCount = 0;
		do {
			needRedirect = false;
			// 设置代理
			try {
				httpClient = new DefaultHttpClient();

				if (proxyHost != null && proxyHost.length() > 0) {
					if (proxyPort == 0) {
						proxyPort = 80;
					}
					// 设置代理
					HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
				// 创建HttpGet实例
				HttpGet request = new HttpGet(redirectUrl);
				// 重定向设置连接服务器
				httpClient.setRedirectHandler(new VsRedirectHandler());
				httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, TIME_OUT_DELAY); // 超时设置
				httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, TIME_OUT_DELAY);// 连接超时
				String androidOsVersion = android.os.Build.VERSION.RELEASE;// 系统版本
				String androidPhoneType = android.os.Build.MODEL; // 型号
				if (androidOsVersion == null) {
					androidOsVersion = "2.3.6";
				}
				if (androidPhoneType == null) {
					androidPhoneType = "HTC Wildfire S A510e";
				}
				String useragnet = String.format("Dalvik/1.4.0 (Linux; U; Android %S; %S Build/GRK39F)",
						androidOsVersion, androidPhoneType);
				// X-Online-Host
				httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, useragnet);

				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				CustomLog.i(TAG, "------------------connected---------------------");
				CustomLog.i(TAG, "statusCode = " + statusCode);

				Header[] headers = response.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					CustomLog.i(TAG, headers[i].getName() + " = " + headers[i].getValue());
				}

				if (statusCode == HttpStatus.SC_OK) {
					Header contentTypeHeader = response.getFirstHeader("Content-Type");
					if (contentTypeHeader != null && contentTypeHeader.getValue() != null) {
						// 用这个来判断是否为移动网关的提示页面数据
						strResult = EntityUtils.toString(response.getEntity(), "utf-8");
						CustomLog.i(TAG, strResult);
						// 不是json格式。并且含有go href字样 这种情况认为是cmwap移动网管返回的数据 我们需要进行二次请求
						if (contentTypeHeader.getValue().indexOf("json") == -1 && strResult.indexOf("go href") != -1) {
							noJsonTypeCount++;
							if (noJsonTypeCount < 2) {
								needRedirect = true;
								CustomLog.i(TAG, "need redirect.................");
							}
						}
					} else {
						strResult = EntityUtils.toString(response.getEntity(), "utf-8");
						CustomLog.i(TAG, strResult);
					}
				} else if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
						|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
						|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 此处重定向处理
					Header locationHeader = response.getFirstHeader("Location");
					if (locationHeader != null) {
						redirectUrl = locationHeader.getValue();
						needRedirect = true;
						CustomLog.i(TAG, "need redirect.................");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				CustomLog.e(TAG, e.getMessage());
				strResult = DfineAction.defaultResult;
			} finally {
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
		} while (needRedirect);

		return strResult;
	}

	public String excutePost(byte[] data) {
		String strResult = "";
		DefaultHttpClient httpClient = null;
		boolean needRedirect = false;
		String redirectUrl = uri;
		int noJsonTypeCount = 0;
		CustomLog.i(TAG, "uri = " + redirectUrl);
		do {
			needRedirect = false;
			// 设置代理
			try {

				httpClient = new DefaultHttpClient();

				CustomLog.i(TAG, "proxyHost=" + proxyHost);
				CustomLog.i(TAG, "proxyPort=" + proxyPort);

				if (proxyHost != null && proxyHost.length() > 0) {
					if (proxyPort == 0) {
						proxyPort = 80;
					}
					// 设置代理
					HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				}

				// 创建HttpPost实例
				HttpPost request = new HttpPost(redirectUrl);
				request.setHeader("Content-Type", "application/x-www-form-urlencoded");
				ByteArrayEntity entity = new ByteArrayEntity(data);
				request.setEntity(entity);

				// 重定向设置连接服务器
				httpClient.setRedirectHandler(new VsRedirectHandler());
				httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, TIME_OUT_DELAY); // 超时设置
				httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, TIME_OUT_DELAY);// 连接超时
				String androidOsVersion = android.os.Build.VERSION.RELEASE;// 系统版本
				String androidPhoneType = android.os.Build.MODEL; // 型号
				if (androidOsVersion == null) {
					androidOsVersion = "2.3.6";
				}
				if (androidPhoneType == null) {
					androidPhoneType = "HTC Wildfire S A510e";
				}
				String useragnet = String.format("Dalvik/1.4.0 (Linux; U; Android %S; %S Build/GRK39F)",
						androidOsVersion, androidPhoneType);
				// X-Online-Host
				httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, useragnet);
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				CustomLog.i(TAG, "------------------connected---------------------");
				CustomLog.i(TAG, "statusCode = " + statusCode);

				Header[] headers = response.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					CustomLog.i(TAG, headers[i].getName() + " = " + headers[i].getValue());
				}

				if (statusCode == HttpStatus.SC_OK) {
					Header contentTypeHeader = response.getFirstHeader("Content-Type");
					if (contentTypeHeader != null && contentTypeHeader.getValue() != null) {
						// 用这个来判断是否为移动网关的提示页面数据
						strResult = EntityUtils.toString(response.getEntity(), "utf-8");
						CustomLog.i(TAG, strResult);
						// 不是json格式。并且含有go href字样 这种情况认为是cmwap移动网管返回的数据 我们需要进行二次请求
						if (contentTypeHeader.getValue().indexOf("json") == -1 && strResult.indexOf("go href") != -1) {
							noJsonTypeCount++;
							if (noJsonTypeCount < 2) {
								needRedirect = true;
								CustomLog.i(TAG, "need redirect.................");
							}
						}
					} else {
						strResult = EntityUtils.toString(response.getEntity(), "utf-8");
						CustomLog.i(TAG, strResult);
					}
				} else if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
						|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
						|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
					// 此处重定向处理
					Header locationHeader = response.getFirstHeader("Location");
					if (locationHeader != null) {
						redirectUrl = locationHeader.getValue();
						needRedirect = true;
						CustomLog.i(TAG, "need redirect.................");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				CustomLog.e(TAG, e.toString());
			} finally {
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
			}
		} while (needRedirect);

		return strResult;
	}

	public class VsRedirectHandler implements RedirectHandler {

		public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
			int statusCode = response.getStatusLine().getStatusCode();

			if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				// 此处重定向处理
				return null;
			}
			return null;
		}

		public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
			return false;
		}
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

}
