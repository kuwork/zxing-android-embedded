/**   
 * @Title ObjectUtil.java 
 * @Package com.businfovision.util 
 * @Description  
 * @author 陈国宏
 * @date 2013年9月19日 上午2:10:53 
 * @version V1.0   
 */
package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @ClassName ObjectUtil
 * @Description 对可序列对象BASE64字符串化
 * @author 陈国宏
 * @date 2013年9月19日 上午2:10:53
 */
public class ObjectUtil {
	public static String getBASE64String(Object obj) {
		ByteArrayOutputStream toByte = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(toByte);
			oos.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 对byte[]进行Base64编码
		return Base64.encode(toByte.toByteArray());
	}
	
	public static Object getObject(String base64String) {
		byte[] base64Bytes;
		try {
			base64Bytes = Base64.decodeToByteArray(base64String);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object object = ois.readObject();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}