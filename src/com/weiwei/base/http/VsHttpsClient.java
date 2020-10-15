package com.weiwei.base.http;

import java.util.Hashtable;
import java.util.TreeMap;

import android.content.Context;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsJsonTool;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;

public class VsHttpsClient {

	// 客户端带上证书访问。。如果与服务端证书不相同。则访问失败
	// public String requestHTTPSPage(Context context) {
	// String netType = KcLoginPacket.getNetTypeString();
	// Hashtable<String, String> bizparams = new Hashtable<String, String>();
	// bizparams.put("account", "7510785");
	// bizparams.put("passwd", KcMd5.md5("252822"));
	// bizparams.put("netmode", netType);
	// bizparams.put("ptype", android.os.Build.MODEL);
	// String mUrl = "https://113.31.81.108/1.0/3g/account/nobind_login?"
	// + KcCoreService.returnParamStr(context, bizparams, DfineAction.authType_Key);
	// InputStream ins = null;
	// String result = "";
	// try {
	// ins = context.getAssets().open("server.crt"); // 下载的证书放到项目中的assets目录中
	// CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
	// Certificate cer = cerFactory.generateCertificate(ins);
	// KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
	// keyStore.load(null, null);
	// keyStore.setCertificateEntry("trust", cer);
	//
	// SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
	// Scheme sch = new Scheme("https", socketFactory, 443);
	// socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	// // 不校验域名
	// // SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
	// SSLContext sc = SSLContext.getInstance("TLS");
	// sc.init(null, new TrustManager[] { new X509TrustManager() {
	// @Override
	// public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	// }
	//
	// @Override
	// public void checkServerTrusted(X509Certificate[] chain, String authType)
	//
	// throws CertificateException {
	// }
	//
	// @Override
	// public X509Certificate[] getAcceptedIssuers() {
	// return null;
	// }
	// } }, new SecureRandom());
	// BufferedReader reader = null;
	// HttpClient mHttpClient = new DefaultHttpClient();
	// mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
	// try {
	// CustomLog.d("GDK", "executeGet is in,murl:" + mUrl);
	// HttpGet request = new HttpGet();
	// request.setURI(new URI(mUrl));
	// HttpResponse response = mHttpClient.execute(request);
	// if (response.getStatusLine().getStatusCode() != 200) {
	// request.abort();
	// return result;
	// }
	//
	// reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	// StringBuffer buffer = new StringBuffer();
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// buffer.append(line);
	// }
	// result = buffer.toString();
	// CustomLog.d("GDK", "mUrl=" + mUrl + "\nresult = " + result);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (reader != null) {
	// reader.close();
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// // TODO: handle exception
	// } finally {
	// try {
	// if (ins != null)
	// ins.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return result;
	// }

	// 信任所有证书的https联网请求
	// public static JSONObject GetLoginHttps(final Context mContext, final Hashtable<String, String> bizparams) {
	// String mUrl = "https://" + KcHttpTools.getInstance(mContext).getUri_prefix() + "/" + DfineAction.uri_verson
	// + "/" + DfineAction.brandid + "/account/login?"
	// + CoreBusiness.getInstance().returnParamStr(mContext, bizparams, DfineAction.authType_Key);
	// String json = DfineAction.defaultResult;
	// JSONObject obj = null;
	// try {
	// SSLContext sc = SSLContext.getInstance("TLS");
	// sc.init(null, new TrustManager[] { new MyTrustManager() }, new SecureRandom());
	// HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	// HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
	// HttpsURLConnection conn = (HttpsURLConnection) new URL(mUrl).openConnection();
	// conn.setDoOutput(true);
	// conn.setDoInput(true);
	// conn.connect();
	//
	// BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	// StringBuffer sb = new StringBuffer();
	// String line;
	// while ((line = br.readLine()) != null)
	// sb.append(line);
	//
	// if (sb != null && sb.length() > 0) {
	// json = sb.toString();
	// }
	// obj = new JSONObject(json);
	// if (KcJsonTool.GetStringFromJSON(obj, "result").equals("403")) {
	// if (KcUserConfig.getDataString(mContext, KcUserConfig.JKey_SIGN_TK).equals(
	// KcJsonTool.GetStringFromJSON(obj, "tk"))
	// && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_AN) == KcJsonTool
	// .GetIntegerFromJSON(obj, "an")
	// && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_KN) == KcJsonTool
	// .GetIntegerFromJSON(obj, "kn")) {
	// } else {
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_TK, KcJsonTool.GetStringFromJSON(obj, "tk"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_AN, KcJsonTool.GetIntegerFromJSON(obj, "an"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_KN, KcJsonTool.GetIntegerFromJSON(obj, "kn"));
	// if (KcUserConfig.getDataBoolean(mContext, KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN, true)) {//
	// 需要进行sign验证。如果需要就进行登录
	// obj = GetLoginHttps(mContext, bizparams);
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN, false);
	// }
	//
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return obj;
	// }
	/**
	 * 直接登录 http请求
	 */
	public static JSONObject GetLoginHttp(final Context mContext, final TreeMap<String, String> treeMap) {
		String target = GlobalVariables.INRFACE_LOGIN;
		String authType = DfineAction.authType_Key;
		JSONObject root = null;
		try {
			if (GlobalVariables.netmode == 0) {
				return root;
			}
			String RealUrl =VsHttpTools.getInstance(mContext).getUri_prefix()+ "/" + DfineAction.uri_verson + "/"
					+ DfineAction.brandid + target;
			CustomLog.i("vsdebug", "url---" + RealUrl);
//			String paramStr = CoreBusiness.getInstance().returnParamStr(mContext, treeMap, authType);
//			CustomLog.i("vsdebug", "paramStr---" + paramStr);
//			root = VsHttpTools.getInstance(mContext).doGetMethod(RealUrl + "?" + paramStr, treeMap, RealUrl, authType);
			if (root == null || root.length() == 0) {
				root = new JSONObject(DfineAction.defaultResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}

	// 信任所有证书的https联网请求
	// public static void GetHttps(final Context mContext, final Hashtable<String, String> bizparams) {
	// // 使用runable实现多线程
	// BaseRunable newRunable = new BaseRunable() {
	// public void run() { // 线程运行主体
	// // 记录线程id
	// GlobalFuntion.PrintValue("启动新的一个线程去登录", "");
	// String mUrl = "https://" + KcHttpTools.getInstance(mContext).getUri_prefix() + "/"
	// + DfineAction.uri_verson + "/" + DfineAction.brandid + "/account/nobind_login.act?"
	// + CoreBusiness.getInstance().returnParamStr(mContext, bizparams, DfineAction.authType_Key);
	// String json = DfineAction.defaultResult;
	// CustomLog.i("GDK", "mUrl" + mUrl);
	// JSONObject obj = null;
	// try {
	// SSLContext sc = SSLContext.getInstance("TLS");
	// sc.init(null, new TrustManager[] { new MyTrustManager() }, new SecureRandom());
	// HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	// HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
	// HttpsURLConnection conn = (HttpsURLConnection) new URL(mUrl).openConnection();
	// conn.setDoOutput(true);
	// conn.setDoInput(true);
	// conn.connect();
	//
	// BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	// StringBuffer sb = new StringBuffer();
	// String line;
	// while ((line = br.readLine()) != null)
	// sb.append(line);
	//
	// if (sb != null && sb.length() > 0) {
	// json = sb.toString();
	// }
	// obj = new JSONObject(json);
	// if (KcJsonTool.GetStringFromJSON(obj, "result").equals("403")) {
	// if (KcUserConfig.getDataString(mContext, KcUserConfig.JKey_SIGN_TK).equals(
	// KcJsonTool.GetStringFromJSON(obj, "tk"))
	// && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_AN) == KcJsonTool
	// .GetIntegerFromJSON(obj, "an")
	// && KcUserConfig.getDataInt(mContext, KcUserConfig.JKey_SIGN_KN) == KcJsonTool
	// .GetIntegerFromJSON(obj, "kn")) {
	//
	// } else {
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_TK,
	// KcJsonTool.GetStringFromJSON(obj, "tk"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_AN,
	// KcJsonTool.GetIntegerFromJSON(obj, "an"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_SIGN_KN,
	// KcJsonTool.GetIntegerFromJSON(obj, "kn"));
	// if (KcUserConfig.getDataBoolean(mContext, KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN, true)) {//
	// 需要进行sign验证。如果需要就进行登录
	// obj = GetLoginHttps(mContext, bizparams);
	// json = obj.toString();
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_IS_NEED_VALIDATE_LOGIN, false);
	// }
	//
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// CustomLog.i("GDK", "sb=" + json);
	// Intent intent = new Intent();
	// intent.setAction(KcCoreService.KC_ACTION_LOGIN);
	// intent.putExtra("msg", json);
	// mContext.sendBroadcast(intent);
	// }
	// }
	// };
	// // 使用线程池进行管理
	// GlobalVariables.fixedThreadPool.execute(newRunable);
	//
	// }

	// public static class MyHostnameVerifier implements HostnameVerifier {
	//
	// @Override
	// public boolean verify(String hostname, SSLSession session) {
	// // TODO Auto-generated method stub
	// return true;
	// }
	// }
	//
	// public static class MyTrustManager implements X509TrustManager {
	//
	// @Override
	// public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public X509Certificate[] getAcceptedIssuers() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// }

	// public static String sendPostSSLRequest(String reqURL, Map<String, String> params, String encodeCharset) {
	// String responseContent = "";
	// HttpClient httpClient = new DefaultHttpClient();
	// // 设置代理服务器
	// // httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("10.0.0.4", 8080));
	// httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
	// httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
	// // 创建TrustManager
	// // 用于解决javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
	// X509TrustManager trustManager = new X509TrustManager() {
	// @Override
	// public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	// }
	//
	// @Override
	// public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	// }
	//
	// @Override
	// public X509Certificate[] getAcceptedIssuers() {
	// return null;
	// }
	// };
	// // 创建HostnameVerifier
	// // 用于解决javax.net.ssl.SSLException: hostname in certificate didn't match: <123.125.97.66> != <123.125.97.241>
	// X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
	// @Override
	// public void verify(String host, SSLSocket ssl) throws IOException {
	// }
	//
	// @Override
	// public void verify(String host, X509Certificate cert) throws SSLException {
	// }
	//
	// @Override
	// public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
	// }
	//
	// @Override
	// public boolean verify(String arg0, SSLSession arg1) {
	// return true;
	// }
	// };
	// try {
	// // TLS1.0与SSL3.0基本上没有太大的差别,可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
	// SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
	// // 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
	// sslContext.init(null, new TrustManager[] { trustManager }, null);
	// // 创建SSLSocketFactory
	// SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
	// // 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
	// httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
	// // 创建HttpPost
	// HttpPost httpPost = new HttpPost(reqURL);
	// // httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
	// // 构建POST请求的表单参数
	// if (null != params) {
	// List<NameValuePair> formParams = new ArrayList<NameValuePair>();
	// for (Map.Entry<String, String> entry : params.entrySet()) {
	// formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	// }
	// httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset));
	// }
	// HttpResponse response = httpClient.execute(httpPost);
	// HttpEntity entity = response.getEntity();
	// if (null != entity) {
	// // //打印HTTP响应报文的第一行,即状态行
	// // System.out.println(response.getStatusLine());
	// // //打印HTTP响应头信息
	// // for(Header header : response.getAllHeaders()){
	// // System.out.println(header.toString());
	// // }
	// responseContent = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
	// EntityUtils.consume(entity);
	// }
	// } catch (ConnectTimeoutException cte) {
	// // Should catch ConnectTimeoutException, and don`t catch org.apache.http.conn.HttpHostConnectException
	// CustomLog.i("GDK", ("与[" + reqURL + "]连接超时,自动返回空字符串"));
	// } catch (SocketTimeoutException ste) {
	// CustomLog.i("GDK", ("与[" + reqURL + "]读取超时,自动返回空字符串"));
	// } catch (Exception e) {
	// CustomLog.i("GDK", ("与[" + reqURL + "]通信异常,堆栈信息为"));
	// } finally {
	// httpClient.getConnectionManager().shutdown();
	// }
	// return responseContent;
	// }

	// private static final int SERVER_PORT = 50030;// 端口号
	// private static final String SERVER_IP = "218.206.176.146";// 连接IP
	// private static final String CLIENT_KET_PASSWORD = "123456";// 私钥密码
	// private static final String CLIENT_TRUST_PASSWORD = "123456";// 信任证书密码
	// private static final String CLIENT_AGREEMENT = "TLS";// 使用协议
	// private static final String CLIENT_KEY_MANAGER = "X509";// 密钥管理器
	// private static final String CLIENT_TRUST_MANAGER = "X509";//
	// private static final String CLIENT_KEY_KEYSTORE = "BKS";// 密库，这里用的是BouncyCastle密库
	// private static final String CLIENT_TRUST_KEYSTORE = "BKS";//
	// private static final String ENCONDING = "utf-8";// 字符集
	// private SSLSocket Client_sslSocket;
	//
	// public void init(Context mContext) {
	// try {
	// // 取得SSL的SSLContext实例
	// SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
	// // 取得KeyManagerFactory和TrustManagerFactory的X509密钥管理器实例
	// KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
	// TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
	// // 取得BKS密库实例
	// KeyStore kks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
	// KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
	// // 加客户端载证书和私钥,通过读取资源文件的方式读取密钥和信任证书
	// // kks.load(mContext.getResources().openRawResource(R.drawable.kclient), CLIENT_KET_PASSWORD.toCharArray());
	// tks.load(mContext.getResources().getAssets().open("server.crt"), CLIENT_TRUST_PASSWORD.toCharArray());
	// // 初始化密钥管理器
	// keyManager.init(kks, CLIENT_KET_PASSWORD.toCharArray());
	// trustManager.init(tks);
	// // 初始化SSLContext
	// sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
	// // 生成SSLSocket
	// Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(SERVER_IP, SERVER_PORT);
	// } catch (Exception e) {
	// }
	// }
	//
	// public void getOut(SSLSocket socket, String message) {
	// PrintWriter out;
	// try {
	// out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	// out.println(message);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public void getIn(SSLSocket socket) {
	// BufferedReader in = null;
	// String str = null;
	// try {
	// in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	// str = new String(in.readLine().getBytes(), ENCONDING);
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
}