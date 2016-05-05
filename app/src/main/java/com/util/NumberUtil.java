/**   
 * @Title NumberUtil.java 
 * @Package com.util 
 * @Description  
 * @author 陈国宏
 * @date 2014年1月24日 上午9:27:43 
 * @version V1.0   
 */
package com.util;

import java.math.BigDecimal;

/**
 * @ClassName NumberUtil
 * @Description 
 * @author 陈国宏
 * @date 2014年1月24日 上午9:27:43
 */
public class NumberUtil {
	public static String byte2MB(long size) {
		return (new BigDecimal(size / 1024.0 / 1024.0).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue()) + "MB";
	}

	public static String byte2KB(long size) {
		return (new BigDecimal(size / 1024.0).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue()) + "KB";
	}

	public static String byte2GB(long size) {
		return (new BigDecimal(size / 1024.0 / 1024.0 / 1024.0).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue()) + "GB";
	}
	
	public static String byteFormat(long size) {
		if(size > 1024*1024*1024){
			return byte2GB(size);
		}else if (size>1024*1024) {
			return byte2MB(size);
		}else if (size>1024) {
			return byte2KB(size);
		}
		return size+"B";
	}
}
