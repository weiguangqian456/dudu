package com.weiwei.softphone;

import android.content.Context;
import android.media.AudioManager;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;



/**
 * �������ڱ���ϵͳMedia�����뻹ԭ
 */
public class SystemMediaConfig {

	/**
	 * ��ʼϵͳMedia����
	 */
	public static void initMediaConfig(AudioManager am) {
		if (am == null) {
			am = (AudioManager) VsApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
		}
		VsUserConfig.setData(VsApplication.getContext(), VsUserConfig.JKEY_MEDIA_MODE, am.getMode());
		VsUserConfig.setData(VsApplication.getContext(), VsUserConfig.JKEY_MEDIA_SPEAKERON, am.isSpeakerphoneOn());
		VsUserConfig.setData(VsApplication.getContext(), VsUserConfig.JKEY_MEDIA_RINGERMODE, am.getRingerMode());
	}

	/**
	 * ��ԭMedia����
	 */
	public static void restoreMediaConfig(AudioManager am) {
		if (am == null) {
			am = (AudioManager) VsApplication.getInstance().getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE);
		}
		int mode = VsUserConfig.getDataInt(VsApplication.getContext(), VsUserConfig.JKEY_MEDIA_MODE);
		int ringerMode = VsUserConfig.getDataInt(VsApplication.getContext(), VsUserConfig.JKEY_MEDIA_RINGERMODE);
		boolean isPpeakerphoneOn = VsUserConfig.getDataBoolean(VsApplication.getContext(),
				VsUserConfig.JKEY_MEDIA_SPEAKERON, true);

		am.setSpeakerphoneOn(isPpeakerphoneOn);
		am.setMode(mode);
		// am.setRingerMode(ringerMode);
	}
}
