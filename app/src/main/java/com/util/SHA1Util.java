/**   
 * @Title SHA1Util.java 
 * @Package util 
 * @Description  
 * @author 陈国宏
 * @date 2013年12月2日 下午2:31:20 
 * @version V1.0   
 */
package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName SHA1Util
 * @Description 用于产生SHA1校验码
 * @author 陈国宏
 * @date 2013年12月2日 下午2:31:20
 */
public class SHA1Util {

	/**
	 * @Title sum
	 * @Description 计算字符串的SHA1
	 * @param str
	 * @return
	 * @throws OutOfMemoryError
	 * @throws IOException
	 */
	public static String sum(String str) throws OutOfMemoryError, IOException {
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");
			messagedigest.update(str.getBytes());
			// 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
			// 对象被重新设置成其初始状态。
			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			e.printStackTrace();
			throw e;
		}
		return null;
	}

	/**
	 * @Title sumFile
	 * @Description 计算文件的SHA1,适用于上G大的文件
	 * @param path
	 * @return
	 * @throws OutOfMemoryError
	 * @throws IOException
	 */
	public static String sumFile(String path) throws OutOfMemoryError,
			IOException {
		File file = new File(path);
		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024 * 1024 * 1];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				// 该对象通过使用 update（）方法处理数据
				messagedigest.update(buffer, 0, len);
			}

			// 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
			// 对象被重新设置成其初始状态。
			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			// NQLog.e("getFileSha1->NoSuchAlgorithmException###",
			// e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			// NQLog.e("getFileSha1->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	public static String sumFile(File file) throws OutOfMemoryError,
			IOException {
		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024 * 1024 * 10];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				// 该对象通过使用 update（）方法处理数据
				messagedigest.update(buffer, 0, len);
			}
			// 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
			// 对象被重新设置成其初始状态。
			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			// NQLog.e("getFileSha1->NoSuchAlgorithmException###",
			// e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			// NQLog.e("getFileSha1->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	public static String sumFile(FileInputStream in) throws OutOfMemoryError,
			IOException {
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024 * 1024 * 10];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				// 该对象通过使用 update（）方法处理数据
				messagedigest.update(buffer, 0, len);
			}

			// 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
			// 对象被重新设置成其初始状态。
			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			// NQLog.e("getFileSha1->NoSuchAlgorithmException###",
			// e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			// NQLog.e("getFileSha1->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String byte2hex(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
}