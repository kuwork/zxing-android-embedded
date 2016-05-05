package com.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.util.Base64;
import com.util.DensityUtil;

public class BitmapUtil {
/**设置缩放大小对图片作处理
 * 
 * @param dst
 * @param width
 * @param height
 * @return
 */
	public Bitmap getBitmapFromFile(File dst, int width, int height) {
	     if (null != dst && dst.exists()) {
	         BitmapFactory.Options opts = null;
	         if (width > 0 && height > 0) {
	             opts = new BitmapFactory.Options();
	             opts.inJustDecodeBounds = true;
	             BitmapFactory.decodeFile(dst.getPath(), opts);
	             // 计算图片缩放比例
	             final int minSideLength = Math.min(width, height);
	             opts.inSampleSize = computeSampleSize(opts, minSideLength,
	                     width * height);
	             opts.inJustDecodeBounds = false;
	             opts.inInputShareable = true;
	             opts.inPurgeable = true;
	         }
	         try {
	             return BitmapFactory.decodeFile(dst.getPath(), opts);
	         } catch (OutOfMemoryError e) {
	             e.printStackTrace();
	         }
	     }
	     return null;
	 }
	
	public static int computeSampleSize(BitmapFactory.Options options,
	         int minSideLength, int maxNumOfPixels) {
	     int initialSize = computeInitialSampleSize(options, minSideLength,
	             maxNumOfPixels);
	 
	    int roundedSize;
	     if (initialSize <= 8) {
	         roundedSize = 1;
	         while (roundedSize < initialSize) {
	             roundedSize <<= 1;
	         }
	     } else {
	         roundedSize = (initialSize + 7) / 8 * 8;
	     }
	 
	    return roundedSize;
	 }
	 
	private static int computeInitialSampleSize(BitmapFactory.Options options,
	         int minSideLength, int maxNumOfPixels) {
	     double w = options.outWidth;
	     double h = options.outHeight;
	 
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
	             .sqrt(w * h / maxNumOfPixels));
	     int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
	             .floor(w / minSideLength), Math.floor(h / minSideLength));
	 
	    if (upperBound < lowerBound) {
	         // return the larger one when there is no overlapping zone.
	         return lowerBound;
	     }
	 
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	         return 1;
	     } else if (minSideLength == -1) {
	         return lowerBound;
	     } else {
	         return upperBound;
	     }
	 }
	
	
	/** 
	* @Title resizedBitmap 
	* @Description 
	* @author 陈国宏
	* @date 2014年1月23日 上午9:56:24 
	* @param context 上下文
	* @param resId 资源id
	* @param newWidth 宽度px
	* @param newHeight 高度px
	* @return 
	*/
	public static Bitmap resizedBitmap(Context context, int resId,
			int newWidth, int newHeight) {
		// 加载需要操作的图片，这里是一张图片
		Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(),
				resId);
		// 获取这个图片的宽和高
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// // 旋转图片 动作
		// matrix.postRotate(45);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	public static Bitmap resizedBitmap(Context context, Bitmap bitmapOrg,
			int newWidth, int newHeight) {
		// 获取这个图片的宽和高
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// // 旋转图片 动作
		// matrix.postRotate(45);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	public static Bitmap resizedBitmap(Context context, Drawable drawable,
			int newWidth, int newHeight) {
		return resizedBitmap(context, fromDrawable(drawable), newWidth,
				newHeight);
	}

	/**
	 * @Title getBase64
	 * @Description 将Bitmap对象转成Base64
	 * @author 陈国宏
	 * @date 2013年12月26日 上午11:29:03
	 * @param bitmapOrg
	 * @return
	 */
	public static String toBase64(Bitmap bitmapOrg) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmapOrg != null) {
				baos = new ByteArrayOutputStream();
				bitmapOrg.compress(Bitmap.CompressFormat.PNG, 100, baos);
				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = android.util.Base64.encodeToString(bitmapBytes,
						android.util.Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @Title getBase64
	 * @Description 将Base64字符串转成Bitmap对象
	 * @author 陈国宏
	 * @date 2013年12月26日 上午11:30:16
	 * @param bitmapBase64
	 * @return
	 */
	public static Bitmap fromBase64(String bitmapBase64) {
		byte[] bytes;
		try {
			bytes = android.util.Base64.decode(bitmapBase64,
					android.util.Base64.DEFAULT);
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title toRoundCorner
	 * @Description 图片圆角化
	 * @author 陈国宏
	 * @date 2013年12月30日 上午10:05:30
	 * @param context
	 *            上下文
	 * @param bitmap
	 *            图片对象
	 * @param dp
	 *            圆角半径,单位是DP
	 * @return 处理后的Bitmap对象
	 */
	public static Bitmap toRoundCorner(Context context, Bitmap bitmap, int dp) {
		return toRoundCorner(bitmap, DensityUtil.dp2px(context, dp));
	}

	/**
	 * @Title toRoundCorner
	 * @Description 图片圆角化
	 * @author 陈国宏
	 * @date 2013年12月30日 上午10:05:30
	 * @param bitmap
	 *            图片对象
	 * @param px
	 *            圆角半径,单位是px
	 * @return 处理后的Bitmap对象
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int px) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = px;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * @Title fromDrawable
	 * @Description 从Drawable对象转成Bitmap对象
	 * @author 陈国宏
	 * @date 2014年1月4日 下午4:14:08
	 * @param drawable
	 * @return
	 */
	public static Bitmap fromDrawable(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * @Title toDrawable
	 * @Description 从Bitmap对象转成Drawable对象
	 * @author 陈国宏
	 * @date 2014年1月4日 下午4:15:49
	 * @param bitmap
	 * @return
	 */
	public static Drawable toDrawable(Context context,Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(),bitmap);
	}

	public static String FileToBase64(File file) {
		String str = null;
		try {
			boolean flag = false;
			FileInputStream inputstream = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputstream.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] date = bos.toByteArray();
			str = Base64.encode(date);
			inputstream.close();
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static boolean Base64ToFile(String str, String path) {
		boolean flag = false;
		try {
			File file = new File(path);
			FileOutputStream outputStream = new FileOutputStream(file);
			byte[] buffer = Base64.decodeToByteArray(str);
			outputStream.write(buffer);
			outputStream.flush();
			outputStream.close();
			if (file.length() == buffer.length) {
				return true;
			} else {
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
