package com.weiwei.base.callback;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.weiwei.base.common.CustomLog;

public class PhoneUtils {
	public static String tag = "PhoneUtils";

	public static void answerRingingCall(Context context) {
		CustomLog.i("IncomingCallListener", "是否执行自动接听" + !IncomingCallListener.isSuccCalling);
		if (!IncomingCallListener.isSuccCalling) {
			try {
				Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent1.putExtra("state", 1);
				localIntent1.putExtra("microphone", 1);
				localIntent1.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");

				Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent2.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent1);
				context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");

				Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent3.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent2);
				context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");

				Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent4.putExtra("state", 0);
				localIntent4.putExtra("microphone", 1);
				localIntent4.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
			} catch (Exception e) {
				try {
					Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
					KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
					intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
					context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");

					intent = new Intent("android.intent.action.MEDIA_BUTTON");
					keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
					intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
					context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
					CustomLog.i(tag, "e.error:" + e.getMessage());
					e.printStackTrace();
				} catch (Exception e1) {
					CustomLog.i(tag, "e1.error:" + e.getMessage());
					e1.printStackTrace();
					Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
					KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
					meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
					context.sendOrderedBroadcast(meidaButtonIntent, null);
				}
			}
		}

	}
	// public static void endCall(Context paramContext) {
	// TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService("phone");
	// Class localClass1 = localTelephonyManager.getClass();
	// Class[] arrayOfClass1 = new Class[0];
	// Method localMethod1;
	// try {
	// localMethod1 = localClass1.getDeclaredMethod("getITelephony", arrayOfClass1);
	// localMethod1.setAccessible(true);
	// Object[] arrayOfObject1 = new Object[0];
	// Object localObject1 = localMethod1.invoke(localTelephonyManager, arrayOfObject1);
	// Class localClass2 = localObject1.getClass();
	// Class[] arrayOfClass2 = new Class[0];
	// Method localMethod2 = localClass2.getMethod("endCall", arrayOfClass2);
	// localMethod2.setAccessible(true);
	// Object[] arrayOfObject2 = new Object[0];
	// Object localObject2 = localMethod2.invoke(localObject1, arrayOfObject2);
	// } catch (SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (NoSuchMethodException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InvocationTargetException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// public static void answerRingingCall_reflect(Context paramContext) {
	// TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService("phone");
	// Class localClass1 = localTelephonyManager.getClass();
	// Class[] arrayOfClass1 = new Class[0];
	// Method localMethod1;
	// try {
	// localMethod1 = localClass1.getDeclaredMethod("getITelephony", arrayOfClass1);
	// localMethod1.setAccessible(true);
	// Object[] arrayOfObject1 = new Object[0];
	// Object localObject1 = localMethod1.invoke(localTelephonyManager, arrayOfObject1);
	// Class localClass2 = localObject1.getClass();
	// Class[] arrayOfClass2 = new Class[0];
	// Method localMethod2 = localClass2.getMethod("answerRingingCall", arrayOfClass2);
	// localMethod2.setAccessible(true);
	// Object[] arrayOfObject2 = new Object[0];
	// Object localObject2 = localMethod2.invoke(localObject1, arrayOfObject2);
	// } catch (SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (NoSuchMethodException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InvocationTargetException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// /**
	// * 根据传入的TelephonyManager来取得系统的ITelephony实例.
	// *
	// * @param telephony
	// * @return 系统的ITelephony实例
	// * @throws Exception
	// */
	//
	// public static ITelephony getITelephony(TelephonyManager telephony) throws Exception {
	// Method getITelephonyMethod = telephony.getClass().getDeclaredMethod("getITelephony");
	// getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
	// return (ITelephony) getITelephonyMethod.invoke(telephony);
	// }

	/*
	 * public static ITelephony getITelephony(TelephonyManager telephony) throws Exception { Method getITelephonyMethod
	 * = telephony.getClass().getDeclaredMethod( "getITelephony"); getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
	 * return (ITelephony) getITelephonyMethod.invoke(telephony); }
	 * 
	 * public static ITelephony getITelephony(Context paramContext) throws Exception { ITelephony localITelephony =
	 * ITelephony.Stub.asInterface(ServiceManager .getService(paramContext.getString(R.string.phone_action1))); return
	 * localITelephony; }
	 */

	// /**
	// * 2.3以下系统
	// *
	// * @param paramContext
	// * @throws Exception
	// */
	// public static void autoAnswerMethod(Context paramContext) throws Exception {
	// TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService("phone");
	// Method localMethod = Class.forName(localTelephonyManager.getClass().getName()).getDeclaredMethod(
	// "getITelephony", new Class[0]);
	// localMethod.setAccessible(true);
	// ITelephony localITelephony = (ITelephony) localMethod.invoke(localTelephonyManager, new Object[0]);
	// localITelephony.silenceRinger();
	// localITelephony.answerRingingCall();
	//
	// }
	//
	// /**
	// * 2.3以上系统
	// *
	// * @param paramContext
	// * @throws Exception
	// */
	// public static void autoAnswerMethod1(Context paramContext) {
	// try {
	// Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
	// localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// localIntent1.putExtra("state", 1);
	// localIntent1.putExtra("microphone", 1);
	// localIntent1.putExtra("name", "Headset");
	// paramContext.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
	// Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	// KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
	// localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
	// paramContext.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
	// Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	// KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
	// localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
	// paramContext.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
	// Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
	// localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// localIntent4.putExtra("state", 0);
	// localIntent4.putExtra("microphone", 1);
	// localIntent4.putExtra("name", "Headset");
	// paramContext.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /**
	// * 特殊4.0以上系统
	// *
	// * @param paramContext
	// * @throws Exception
	// */
	// public static void autoAnswerMethod2(Context context) {
	// try {
	// Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
	// KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
	// intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
	// context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
	//
	// intent = new Intent("android.intent.action.MEDIA_BUTTON");
	// keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
	// intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
	// context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
	//
	// Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
	// localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// localIntent1.putExtra("state", 1);
	// localIntent1.putExtra("microphone", 1);
	// localIntent1.putExtra("name", "Headset");
	// context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
	//
	// Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	// KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
	// localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
	// context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
	//
	// Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	// KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
	// localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
	// context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
	//
	// Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
	// localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// localIntent4.putExtra("state", 0);
	// localIntent4.putExtra("microphone", 1);
	// localIntent4.putExtra("name", "Headset");
	// context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
