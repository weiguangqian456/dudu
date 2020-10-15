package com.zte.common;

import java.security.MessageDigest;

/**
 * Created by Jiangxuewu on 2015/4/8.
 */
public class MD5 {

    private MD5() {}

    /**
     *
     * @param buffer
     * @return
     */
    private final static String[] hexDigits = {      
		  "0", "1", "2", "3", "4", "5", "6", "7",      
		  "8", "9", "a", "b", "c", "d", "e", "f"};  
    
    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    public static String encode(String origin) {    
		  String resultString = null;
		  try {      
			   resultString=new String(origin);      
			   MessageDigest md = MessageDigest.getInstance("MD5");      
			   resultString = byteArrayToHexString(md.digest(resultString.getBytes()));    
		   }catch (Exception ex) {
			   ex.printStackTrace();
		   }    
		   return resultString;  
	}
	
	public static String byteArrayToHexString(byte[] b) {    
		  StringBuffer resultSb = new StringBuffer();    
		  for (int i = 0; i < b.length; i++) {     
			  resultSb.append(byteToHexString(b[i]));    
		  }    
		  return resultSb.toString(); 
	}  
	
	private static String byteToHexString(byte b) {   
		  int n = b;
		  int d1 = 0;
		  int d2 = 0;
		  if (n < 0){
			   n = 256 + n;   
			   d1 = n / 16;    
			   d2 = n % 16;    
		  }
			return hexDigits[d1] + hexDigits[d2];
	 }
	
	
}
