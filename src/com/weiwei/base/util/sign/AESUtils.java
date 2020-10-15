package com.weiwei.base.util.sign;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author hc.
 * Description:
 * @date 2020/7/12
 */
public class AESUtils {

        // 解密
        public static String decrypt(String key,String sSrc){
            try {
                byte[] raw = key.getBytes("ASCII");
                SecretKeySpec mSecretKeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec);
                byte[] encrypted1 = Base64Utils.decode(sSrc);// 先用base64解密
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

}
