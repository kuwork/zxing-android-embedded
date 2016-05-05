package com.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class PathManager {

	/**
	 * 存放图片
	 * 
	 * @Title getDiskFileDir
	 * @Description
	 * @author 李庆育
	 * @date 2015-12-21 下午5:27:09
	 * @param context
	 * @return
	 */
	public static String getDiskFileDir(Context context) {
		String picDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File externalDir = context
					.getExternalFilesDir(Environment.DIRECTORY_DCIM);
			if (externalDir != null && externalDir.exists()) {
				picDir = externalDir.getPath();
			}
		}
		if (picDir == null) {
			File externalDir = context.getFilesDir();
			if (externalDir != null && externalDir.exists()) {
				picDir = externalDir.getPath();
			}
		}
		return picDir;
	}

	/**
	 * @param context
	 * @return app_cache_path/dirName
	 */
	public static String getDiskCacheDir(Context context) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File externalCacheDir = context.getExternalCacheDir();
			if (externalCacheDir != null && externalCacheDir.exists()) {
				cachePath = externalCacheDir.getPath();
			}
		}
		if (cachePath == null) {
			File cacheDir = context.getCacheDir();
			if (cacheDir != null && cacheDir.exists()) {
				cachePath = cacheDir.getPath();
			}
		}
		return cachePath;
	}

	/**
	 * 获取sd卡路径
	 * 
	 * @Title getSdcardDir
	 * @Description
	 * @author 李庆育
	 * @date 2015-12-21 下午5:27:43
	 * @return
	 */
	public static String getSdcardDir() {
		String sdPath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File externalCacheDir = Environment.getExternalStorageDirectory();
			if (externalCacheDir != null && externalCacheDir.exists()) {
				sdPath = externalCacheDir.getPath();
			}
		}
		if (sdPath == null) {
			File cacheDir = Environment.getDownloadCacheDirectory();
			if (cacheDir != null && cacheDir.exists()) {
				sdPath = cacheDir.getPath();
			}
		}
		return sdPath;
	}

	public static String getDownLoadDir() {
		String dir = "";
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File downloadFile = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if (downloadFile != null && downloadFile.exists()) {
				dir = downloadFile.getPath();
				return dir;
			} else {
				downloadFile = Environment.getExternalStorageDirectory();
				if (downloadFile != null && downloadFile.exists()) {
					dir = downloadFile.getPath();
					return dir;
				}
			}
		}
		return null;
	}

}
