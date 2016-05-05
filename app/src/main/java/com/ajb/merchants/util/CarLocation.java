package com.ajb.merchants.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.util.PathManager;

import java.io.File;

/**
 * 负责获取上次拍照图片的对象
 *
 * @author 李庆育
 * @ClassName CarLocation
 * @Description
 * @date 2015-10-22 上午11:34:48
 */
public class CarLocation {

    public static final String ZTB_CARNO = "ztb_carno.jpg"; //拍照图片名称
    public static final String CAPTURE_FOLDER = "ztb_capture";  //拍照图片所在文件夹
    public static final String ZTB_BANNER_SPLASH = "ztb_banner_splash";  //欢迎页广告图片所在文件夹
    public static final String ZTB_BANNER_HOME = "ztb_banner_home";  //首页广告图片所在文件夹
    public static final String ZTB_BANNER_HOME_SLIDE = "ztb_banner_home_slide";  //首页侧滑广告图片所在文件夹
    public static final String ZTB_CACHE_HOME_SLIDE_ICON = "ztb_cache_home_slide_icon";  //首页侧滑菜单图标所在缓存文件夹
    public static final String ZTB_CACHE_AROUND = "ztb_cache_around";  //车场详情页所在缓存文件夹
    public static final String PIC_DIRECTORY = File.separator + CAPTURE_FOLDER + File.separator + ZTB_CARNO;

    /**
     * 获取上次拍照的图片
     *
     * @return
     * @Title getCarLocationInfo
     * @Description
     * @author 李庆育
     * @date 2015-10-22 上午11:35:10
     */
    public static Bitmap getCarLocationBitmap(Context context) {
        Bitmap bitmap = null;
        try {
            /**
             * 强制再次获得略缩图，避免OOM
             */
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            File file = new File(PathManager.getDiskFileDir(context)
                    + PIC_DIRECTORY);
            if (!file.exists()) {
                return null;
            }
            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            // 得到图片真实的宽高
            int normalWidth = opts.outWidth;
            int normalHeight = opts.outHeight;
            int w, h;
            if (normalWidth > normalHeight) {// 横向的
                w = 500 * normalWidth / normalHeight;
                h = 500;
            } else {
                w = 500;
                h = 500 * normalHeight / normalWidth;
            }
            // 计算采样率
            int scaleX = normalHeight / h;
            int scaleY = normalWidth / w;
            int scale = Math.min(scaleX, scaleY);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(PathManager.getDiskFileDir(context)
                    + PIC_DIRECTORY, opts);
            // 再次检查图片比例
            // 图片接近(0~0.01)是16:9的比例的时候，强行旋转图片
            double sixteen = 16;
            double nine = 9;
            double sixteenToNineValue = sixteen / nine; // 代表16除以9
            double imgW = Double.valueOf(normalWidth);
            double imgH = Double.valueOf(normalHeight);
            double imgHToimgW = imgH / imgW; // 代表高除以宽
            if (Math.abs(sixteenToNineValue - imgHToimgW) >= 0
                    && Math.abs(sixteenToNineValue - imgHToimgW) < 0.02) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90); // 旋转90度
                // 宽高都缩小4倍
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, normalWidth / 4,
                        normalHeight / 4, matrix, true);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 删除照片
     *
     * @return
     */
    public static boolean deleteCarLocationBitmap(Context context) {
        File file = null;
        boolean isDelete = false;
        try {
            file = new File(PathManager.getDiskFileDir(context)
                    + PIC_DIRECTORY);
            if (file.exists()) {
                isDelete = file.delete();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return isDelete;
    }

}
