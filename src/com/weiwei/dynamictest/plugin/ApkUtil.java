package com.weiwei.dynamictest.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;

import android.content.pm.PackageInfo;
import android.util.DisplayMetrics;

public class ApkUtil {

	// public static PackageInfo generatePackageInfo(PackageParser.Package p,int
	// gids[], int flags, long firstInstallTime, long
	// lastUpdateTime,HashSet<String> grantedPermissions, PackageUserState
	// state);
	// public static PackageInfo generatePackageInfo(PackageParser.Package p,int
	// gids[], int flags, long firstInstallTime, long lastUpdateTime,
	// HashSet<String> grantedPermissions, PackageUserState state, int userId);

	// String archiveSourcePath

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static PackageInfo getPackageInfo(String archiveSourcePath, int flags) {
		PackageInfo packageInfo = null;
		try {
			Class packageParser = Class.forName("android.content.pm.PackageParser");
			Class packageParser_Package = Class.forName("android.content.pm.PackageParser$Package");
			Class[] packageParserClass = new Class[1];
			packageParserClass[0] = String.class;
			Constructor packageParser_Constructor = packageParser.getConstructor(packageParserClass);

			// ����parsePackage����
			// public Package parsePackage(File sourceFile, String destCodePath,
			// DisplayMetrics metrics, int flags)
			Class[] parsePackageParams = new Class[4];
			parsePackageParams[0] = File.class;
			parsePackageParams[1] = String.class;
			parsePackageParams[2] = DisplayMetrics.class;
			parsePackageParams[3] = Integer.TYPE;
			Method parsePackage = packageParser.getMethod("parsePackage", parsePackageParams);

			Class[] collectCertificatesParams = new Class[2];
			collectCertificatesParams[0] = packageParser_Package;
			collectCertificatesParams[1] = Integer.TYPE;
			// public boolean collectCertificates(Package pkg, int flags)
			Method collectCertificatesMethod = packageParser
					.getMethod("collectCertificates", collectCertificatesParams);
			packageParser_Package.getField("mSignatures");

			// ����PackageParserʵ��
			// public PackageParser(String archiveSourcePath)
			Object[] packageParserParams = new Object[1];
			packageParserParams[0] = archiveSourcePath;
			Object packageParserObj = packageParser_Constructor.newInstance(packageParserParams);
			DisplayMetrics localDisplayMetrics = new DisplayMetrics();
			localDisplayMetrics.setToDefaults();
			File localFile = new File(archiveSourcePath);
			Object[] parsePackageMethodParams = new Object[4];
			parsePackageMethodParams[0] = localFile;
			parsePackageMethodParams[1] = archiveSourcePath;
			parsePackageMethodParams[2] = localDisplayMetrics;
			parsePackageMethodParams[3] = Integer.valueOf(0);
			// Package parsePackage(File sourceFile, String destCodePath,
			// DisplayMetrics metrics, int flags)
			Object packageObj = parsePackage.invoke(packageParserObj, parsePackageMethodParams);
			if (packageObj == null) {
				return null;
			}

			if ((flags | 0x40) != 0) {
				Object[] methodParams = new Object[2];
				methodParams[0] = packageObj;
				methodParams[1] = Integer.valueOf(flags);
				collectCertificatesMethod.invoke(packageParserObj, methodParams);
			}

			try {
				Class[] methodParamsTypes = new Class[5];
				methodParamsTypes[0] = packageParser_Package;
				methodParamsTypes[1] = int[].class;
				methodParamsTypes[2] = Integer.TYPE;
				methodParamsTypes[3] = Long.TYPE;
				methodParamsTypes[4] = Long.TYPE;
				Method generatePackageInfoMethod = packageParser.getMethod("generatePackageInfo", methodParamsTypes);
				Object[] methodParams = new Object[5];
				methodParams[0] = packageObj;
				methodParams[1] = null;
				methodParams[2] = Integer.valueOf(flags);
				methodParams[3] = Integer.valueOf(0);
				methodParams[4] = Integer.valueOf(0);
				Object packageInfoObj = generatePackageInfoMethod.invoke(packageParserObj, methodParams);
				if ((packageInfoObj != null) && ((packageInfoObj instanceof PackageInfo))) {
					packageInfo = (PackageInfo) packageInfoObj;
				}
			} catch (NoSuchMethodException localNoSuchMethodException1) {
				try {
					Class[] arrayOfClass6 = new Class[3];
					arrayOfClass6[0] = packageParser_Package;
					arrayOfClass6[1] = Integer.TYPE;
					arrayOfClass6[2] = Integer.TYPE;
					Method localMethod5 = packageParser.getMethod("generatePackageInfo", arrayOfClass6);
					Object[] arrayOfObject5 = new Object[3];
					arrayOfObject5[0] = packageObj;
					arrayOfObject5[1] = null;
					arrayOfObject5[2] = Integer.valueOf(flags);
					Object localObject6 = localMethod5.invoke(packageParserObj, arrayOfObject5);
					if ((localObject6 != null) && ((localObject6 instanceof PackageInfo))) {
						packageInfo = (PackageInfo) localObject6;
					}

				} catch (NoSuchMethodException localNoSuchMethodException2) {
					try {
						Class[] arrayOfClass5 = new Class[3];
						arrayOfClass5[0] = packageParser_Package;
						arrayOfClass5[1] = int[].class;
						arrayOfClass5[2] = Integer.TYPE;
						Method localMethod4 = packageParser.getMethod("generatePackageInfo", arrayOfClass5);
						Object[] arrayOfObject4 = new Object[3];
						arrayOfObject4[0] = packageObj;
						arrayOfObject4[1] = null;
						arrayOfObject4[2] = Integer.valueOf(flags);
						Object localObject5 = localMethod4.invoke(packageParserObj, arrayOfObject4);
						if ((localObject5 != null) && ((localObject5 instanceof PackageInfo))) {
							packageInfo = (PackageInfo) localObject5;
						}

					} catch (NoSuchMethodException localNoSuchMethodException3) {
						Class[] arrayOfClass4 = new Class[9];
						arrayOfClass4[0] = packageParser_Package;
						arrayOfClass4[1] = int[].class;
						arrayOfClass4[2] = Integer.TYPE;
						arrayOfClass4[3] = Long.TYPE;
						arrayOfClass4[4] = Long.TYPE;
						arrayOfClass4[5] = HashSet.class;
						arrayOfClass4[6] = Boolean.TYPE;
						arrayOfClass4[7] = Integer.TYPE;
						arrayOfClass4[8] = Integer.TYPE;
						Method localMethod3 = packageParser.getMethod("generatePackageInfo", arrayOfClass4);
						Object[] arrayOfObject3 = new Object[9];
						arrayOfObject3[0] = packageObj;
						arrayOfObject3[1] = null;
						arrayOfObject3[2] = Integer.valueOf(flags);
						arrayOfObject3[3] = Integer.valueOf(0);
						arrayOfObject3[4] = Integer.valueOf(0);
						arrayOfObject3[5] = null;
						arrayOfObject3[6] = Boolean.valueOf(false);
						arrayOfObject3[7] = Integer.valueOf(0);
						arrayOfObject3[8] = Integer.valueOf(0);
						Object localObject3 = localMethod3.invoke(packageParserObj, arrayOfObject3);
						if ((localObject3 != null) && ((localObject3 instanceof PackageInfo))) {
							packageInfo = (PackageInfo) localObject3;
						}
					}
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return packageInfo;
	}

	/*
	 * public static boolean a(Context paramContext, String paramString) { PackageInfo localPackageInfo =
	 * paramContext.getPackageManager().getPackageArchiveInfo(paramString, 64); if ((localPackageInfo == null) ||
	 * (localPackageInfo.applicationInfo == null)) { return false; } return true; }
	 * 
	 * public static Signature[] a(String paramString) { Signature[] arrayOfSignature = null; try { Class localClass1 =
	 * Class.forName("android.content.pm.PackageParser"); Class localClass2 =
	 * Class.forName("android.content.pm.PackageParser$Package"); Class[] arrayOfClass1 = new Class[1]; arrayOfClass1[0]
	 * = String.class; Constructor localConstructor = localClass1.getConstructor(arrayOfClass1); Class[] arrayOfClass2 =
	 * new Class[4]; arrayOfClass2[0] = File.class; arrayOfClass2[1] = String.class; arrayOfClass2[2] =
	 * DisplayMetrics.class; arrayOfClass2[3] = Integer.TYPE; Method localMethod1 =
	 * localClass1.getMethod("parsePackage", arrayOfClass2); Class[] arrayOfClass3 = new Class[2]; arrayOfClass3[0] =
	 * localClass2; arrayOfClass3[1] = Integer.TYPE; Method localMethod2 = localClass1.getMethod("collectCertificates",
	 * arrayOfClass3); Field localField = localClass2.getField("mSignatures"); Object[] arrayOfObject1 = new Object[1];
	 * arrayOfObject1[0] = paramString; Object localObject1 = localConstructor.newInstance(arrayOfObject1);
	 * DisplayMetrics localDisplayMetrics = new DisplayMetrics(); localDisplayMetrics.setToDefaults(); File localFile =
	 * new File(paramString); Object[] arrayOfObject2 = new Object[4]; arrayOfObject2[0] = localFile; arrayOfObject2[1]
	 * = paramString; arrayOfObject2[2] = localDisplayMetrics; arrayOfObject2[3] = Integer.valueOf(0); Object
	 * localObject2 = localMethod1.invoke(localObject1, arrayOfObject2); if (localObject2 == null) { arrayOfSignature =
	 * null; } else { Object[] arrayOfObject3 = new Object[2]; arrayOfObject3[0] = localObject2; arrayOfObject3[1] =
	 * Integer.valueOf(64); localMethod2.invoke(localObject1, arrayOfObject3); Object localObject3 =
	 * localField.get(localObject2); if ((localObject3 != null) && ((localObject3 instanceof Signature[])))
	 * arrayOfSignature = (Signature[]) (Signature[]) localObject3; } } catch (Exception localException) {
	 * localException.printStackTrace(); arrayOfSignature = null; } return arrayOfSignature; }
	 */
}
