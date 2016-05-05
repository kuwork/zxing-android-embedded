/**   
 * @Title: MapMathUtil.java 
 * @Package com.businfovision.util 
 * @Description: 
 * @author 陈国宏
 * @date 2013年9月13日 下午3:39:55 
 * @version V1.0   
 */
package com.ajb.merchants.util;

import java.text.DecimalFormat;
import java.util.Map;

import com.lidroid.xutils.util.LogUtils;

/**
 * @ClassName: MapMathUtil
 * @Description:
 * @author 陈国宏
 * @date 2013年9月13日 下午3:39:55
 */
public class MapMathUtil {
	private final static double earth_Radius = 6378137;// 地球半径

	/**
	 * @Title: calDistance
	 * @Description:计算直线经度、纬度两点之间的距离
	 * @author 陈国宏
	 * @date 2013年9月13日 下午3:41:24
	 * @param lng1
	 * @param lng2
	 * @param lat1
	 * @param lat2
	 * @return Distance距离
	 */
	public static double calDistance(double lng1, double lng2, double lat1,
			double lat2) {
		LogUtils.d("lng1=" + lng1 + ",lng2=" + lng2 + ",lat1=" + lat1
				+ ",lat2=" + lat2);
		double latRadians1 = lat1 * (Math.PI / 180);
		double latRadians2 = lat2 * (Math.PI / 180);
		double latRadians = latRadians1 - latRadians2;
		double lngRadians1 = lng1 * (Math.PI / 180);
		double lngRadians2 = lng2 * (Math.PI / 180);
		double lngRadians = lngRadians1 - lngRadians2;
		;
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(
				Math.sin(latRadians / 2), 2)
				+ Math.cos(latRadians1)
				* Math.cos(latRadians2)
				* Math.pow(Math.sin(lngRadians / 2), 2)));
		return distance * earth_Radius;
	}

	public static double calDistanceString(Map<String, Object> map,
			String longitude, String latitude) {
		double lng1, lng2, lat1, lat2;
		lng1 = Double.parseDouble(map.get("Longitude").toString());
		lat1 = Double.parseDouble(map.get("Latitude").toString());
		lng2 = Double.parseDouble(longitude);
		lat2 = Double.parseDouble(latitude);
		double distance = MapMathUtil.calDistance(lng1, lng2, lat1, lat2);
		DecimalFormat df = new DecimalFormat("#0.00");
		String st = df.format(distance);
		return Double.parseDouble(st);
	}

	/**
	 * @Title: calLngDegree
	 * @Description:根据距离，得到经度差
	 * @author 陈国宏
	 * @date 2013年9月20日 上午9:25:39
	 * @param m
	 * @return
	 * @throws
	 */
	public static double calLngDegree(double m, double lat) {
		// 纬度为 B 的地区：
		// 纬度变化一度，球面南北方向距离变化：πR/180 ........111.7km
		// 经度变化一度，球面东西方向距离变化：πR/180*cosB ....111.7*cosB
		// m单位是米
		double lngRadians = lat * (Math.PI / 180);
		return m * 180 / (Math.PI * earth_Radius * Math.cos(lngRadians));
	}

	/**
	 * @Title: calLngDegree
	 * @Description:根据距离，得到纬度差
	 * @author 陈国宏
	 * @date 2013年9月20日 上午9:25:39
	 * @param m
	 * @return
	 * @throws
	 */
	public static double calLatDegree(double m) {
		// 纬度为 B 的地区：
		// 纬度变化一度，球面南北方向距离变化：πR/180 ........111.7km
		// 经度变化一度，球面东西方向距离变化：πR/180*cosB ....111.7*cosB
		// m单位是米
		return m * 180 / (Math.PI * earth_Radius);

	}

	/**
	 * @Title gps2d
	 * @Description 计算方位角pab，增加爱误差修正，
	 * @author jerry
	 * @date 2015年6月15日 上午10:08:59
	 * @param latA
	 * @param lngA
	 * @param latB
	 * @param lngB
	 * @return
	 */
	public static double gps2d(double latA, double lngA, double latB,
			double lngB) {
		if (latA == latB && lngA == lngB) {
			return -1;
		}
		double d = 0;
		double lat_a = latA * Math.PI / 180;
		double lng_a = lngA * Math.PI / 180;
		double lat_b = latB * Math.PI / 180;
		double lng_b = lngB * Math.PI / 180;

		double a = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a)
				* Math.cos(lat_b) * Math.cos(lng_b - lng_a);
		double b = Math.sqrt(1 - a * a);
		double c = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / b;
		if (Math.abs(c) < 1) {
			d = Math.asin(c) * 180 / Math.PI;
		} else {
			if (Math.abs(latA - latB) <= 0.0001) {// 纬度相同
				if (lngA < lngB) {// A在B西边
					return 90;
				} else {
					return 270;
				}
			}
		}

		double Azimuth = 0;
		// B点在第一象限，Azimuth=A；
		// B在第二象限，Azimuth=360+A;
		// B在第三四象限，Azimuth=180-A。
		if (latB >= latA && lngB >= lngA) {// 第一象限
			Azimuth = d;
		} else if (latB < latA && lngB >= lngA) {// 第二象限
			// Azimuth = 360 + d;
			Azimuth = 180 - d;
		} else if (lngB < lngA && latB < latA) {// 第三象限
			Azimuth = 180 - d;
		} else if (lngB < lngA && latB >= latA) {// 第四象限
			Azimuth = 360 + d;
		}
		// d = Math.round(d*10000);
		return Azimuth;
	}

	/**
	 * @Title: calAD
	 * @Description:偏移纠正，计算当前到最近拐点的歉意拐点的距离
	 * @author 陈国宏
	 * @date 2013年10月23日 下午4:09:23
	 * @param AB
	 *            最近拐点到前一个拐点的距离
	 * @param BC
	 *            当前到最近拐点的距离
	 * @param AC
	 *            当前到最近拐点的前一个拐点的距离
	 * @return 修正后的当前到最近拐点的歉意拐点的距离
	 */
	public static double calAD(double AB, double BC, double AC) {
		double AD = 0;
		if (AB != 0) {
			AD = (AB * AB + AC * AC - BC * BC) / (2 * AB);
		}
		return AD;
	}

	/**
	 * @Title calDirection 算B相对于A的方位角
	 * @Description
	 * @author jerry
	 * @date 2015年6月10日 下午2:26:49
	 * @param lngA
	 * @param lngB
	 * @param latA
	 * @param latB
	 * @return
	 */
	public static double calDirection(double lngA, double lngB, double latA,
			double latB) {
		if (latA == latB && lngA == lngB) {
			return -1;
		} else if (latA == latB) {// 纬度相同
			if (lngA < lngB) {// A在B西边
				return 90;
			} else {
				return 270;
			}
		} else if (lngA == lngB) {
			if (latA < latB) {
				return 0;
			} else {
				return 180;
			}
		}
		// 由三面角余弦公式，求AOB的球心角余弦
		double cos_c = Math.cos((90 - latB) * Math.PI / 180)
				* Math.cos((90 - latA) * Math.PI / 180)
				+ Math.sin((90 - latB) * Math.PI / 180)
				* Math.sin((90 - latA) * Math.PI / 180)
				* Math.cos((lngB - lngA) * Math.PI / 180);
		double sin_c = Math.sqrt(1 - Math.pow(cos_c, 2));
		// 由球面正弦公式，得
		double sin_A = Math.sin(Math.sin((90 - latB) * Math.PI / 180)
				* Math.sin((lngB - lngA) * Math.PI / 180) / sin_c);
		double A = Math.asin(Math.sin((90 - latB) * Math.PI / 180)
				* Math.sin((lngB - lngA) * Math.PI / 180) / sin_c)
				* 180 / Math.PI;
		double Azimuth = 0;
		// B点在第一象限，Azimuth=A；
		// B在第二象限，Azimuth=360+A;
		// B在第三四象限，Azimuth=180-A。
		if (latB >= latA && lngB >= lngA) {// 第一象限
			Azimuth = A;
		} else if (latB < latA && lngB >= lngA) {// 第二象限
			Azimuth = 360 + A;
		} else if (lngB < lngA) {// 第三第四象限
			Azimuth = 180 - A;
		}
		return Azimuth;
	}

}
