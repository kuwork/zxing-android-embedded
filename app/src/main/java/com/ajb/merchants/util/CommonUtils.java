package com.ajb.merchants.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.merchants.R;
import com.util.DensityUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础工具类,主要用于网络监测
 *
 * @author chenming
 */
public class CommonUtils {

    public static AlertDialog processAlertDialog;

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvaliable(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
     *
     * @return
     */
    static String setNetworkType(Context mInstance) {
        String networkType = "wifi";
        ConnectivityManager manager = (ConnectivityManager) mInstance
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            // 当前网络不可用
            return "";
        }

        String info = netWrokInfo.getExtraInfo();
        if ((info != null)
                && ((info.trim().toLowerCase().equals("cmwap"))
                || (info.trim().toLowerCase().equals("uniwap"))
                || (info.trim().toLowerCase().equals("3gwap")) || (info
                .trim().toLowerCase().equals("ctwap")))) {
            // 上网方式为wap
            if (info.trim().toLowerCase().equals("ctwap")) {
                // 电信
                networkType = "ctwap";
            } else {
                networkType = "cmwap";
            }

        }
        // else
        // {
        // networkType="wifi";
        // }
        return networkType;
    }

    private void openGPSSettings(Context context) {
        LocationManager alm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "GPS模块正常", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        // context.startActivityForResult(intent,0); //此为设置完成后返回到获取界面

    }

    public static void ShowMessage(Context context, final AlertDialog myDialog,
                                   String messageString, Drawable background) {
        if (myDialog != null) {
            try {
                myDialog.dismiss();

            } catch (Exception e) {
                return;
            }
        }

        try {
            myDialog.show();
        } catch (Exception e) {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public static void DissmissProcess() {
        try {
            if (processAlertDialog != null) {
                processAlertDialog.dismiss();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 单位转换，dip->px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px_(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.i("CommonUtils",
                context.getResources().getDisplayMetrics().widthPixels
                        + "   "
                        + context.getResources().getDisplayMetrics().heightPixels);
        Log.i("CommonUtils", ""
                + context.getResources().getDisplayMetrics().densityDpi);
        Log.i("CommonUtils", "" + scale);
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 单位转换，px->dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip_(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param src
     * @param scaleType 1:保持宽度 2：保持高度 3：缩放
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap src, int scaleType, int tWidth,
                                     int tHeight) {
        Bitmap temp = null;
        Matrix matrix = new Matrix();
        if (src == null) {
            return null;
        }
        float hScale = ((float) tHeight) / src.getHeight();
        float wScale = ((float) tWidth) / src.getWidth();
        switch (scaleType) {
            case 1: {
                matrix.postScale(1, hScale);
                break;
            }
            case 2: {
                matrix.postScale(wScale, 1);
                break;
            }
            case 3: {
                matrix.postScale(wScale, hScale);
                break;
            }
            default:
                break;
        }

        temp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
                matrix, true);
        return temp;
    }

    public static boolean isServiceRunning(Context context,
                                           String serviceClassName) {
        if (serviceClassName == null || "".equals(serviceClassName)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : rs) {
            String className = info.service.getClassName();
            if (serviceClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证手机号码
     *
     * @param mobiles 　　 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     *                联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
     * @return [0-9]{5,9}
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern
                    .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证输入是否为数字
     *
     * @param number
     * @return
     */
    public static boolean isNum(String number) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^[0-9]{5}$");
            Matcher m = p.matcher(number);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    // 获取当前时间
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    public static String getFormateTime(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
        // Calendar calendar=Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    /**
     * 监控app流量使用
     *
     * @param context
     */
    public void getGps(Context context) {
        ConnectivityManager connectMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        ApplicationInfo info = context.getApplicationInfo();
        NetworkInfo netWork = connectMan.getActiveNetworkInfo();
        if (null != netWork && netWork.isAvailable()) {
            System.out.println("============");
            if (netWork.getType() == ConnectivityManager.TYPE_MOBILE) {
                String name = info.packageName;
                if (name.equals("com.example.testappgps")
                        || name == "com.example.testappgps") {
                    int uid = info.uid;
                    System.out.println("uid===" + uid);
                    System.out.println("获取2G/3G的流量"
                            + TrafficStats.getUidRxBytes(uid) + "---"
                            + TrafficStats.getUidTxBytes(uid));
                }
            }
            if (netWork.getType() == ConnectivityManager.TYPE_WIFI) {
                String name = info.packageName;

                if (name.equals("com.example.testappgps")
                        || name == "com.example.testappgps") {
                    int uid = info.uid;
                    System.out.println(TrafficStats.getUidRxBytes(uid));
                    System.out.println(TrafficStats.getUidTxBytes(uid));
                    System.out.println("消耗的wifi流量"
                            + (int) TrafficStats.getUidRxBytes(uid)
                            + (int) TrafficStats.getUidTxBytes(uid));
                }
            }
        }

    }

    public static void ShowProcess(Context context, String mesString) {

        processAlertDialog = new AlertDialog.Builder(context).create();
        try {
            processAlertDialog.show();
        } catch (Exception e) {
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.process_alert, null);
        processAlertDialog.getWindow().setContentView(layout);

        if (mesString != "") {
            ((TextView) layout.findViewById(R.id.tvMsg)).setText(mesString);
        }
    }

    /**
     * @param @param  context
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getVersionName
     * @Description: TODO(获取程序版本名)
     */
    public static String getVersionName(Context context) {
        String version = "";
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    /**
     * @param @param  context
     * @param @return 设定文件
     * @return int 返回类型
     * @throws
     * @Title: getVersionCode
     * @Description: TODO(获取程序版本号)
     */
    public static int getVersionCode(Context context) {
        int version = 1;// 默认版本号为1
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    // 判断特殊字符
    public static String removeSpecialChar(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static Bitmap getMarkerColored(Context context, int res,
                                          int oldColor, int newColor, String unit) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.loc_front);
        Bitmap bmp_shadow = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.loc_shadow);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap tmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas c = new Canvas(tmp);
        // 保存为单独的图层
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int saveCount = c.saveLayer(0, 0, w, h, mPaint, Canvas.ALL_SAVE_FLAG);
        // 绘制目标图
        c.drawBitmap(bmp, 0, 0, mPaint);
        // 设置混合模式
        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // 绘制源图
        mPaint.setColor(newColor);
        c.drawRect(0, 0, w, h, mPaint);
        mPaint.setXfermode(null);
        c.restoreToCount(saveCount);

        Paint paintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintWhite.setColor(Color.WHITE);

        float radius_old = 36f;
        float radius = (float) Math.floor(w * radius_old / 46 / 2);
        float m = (float) Math.floor((w - 2 * w * radius_old / 46 / 2) / 2);
        c.drawCircle((float) Math.ceil(w / 2), (float) Math.floor(m + radius),
                radius, paintWhite);// 绘制背景
        c.drawBitmap(bmp_shadow, 0, 0, null);
        float radius2 = (float) Math.floor(w / 5f);
        c.drawCircle(w - radius2, radius2, radius2, paintWhite);
        TextPaint textPaint = new TextPaint(Paint.FAKE_BOLD_TEXT_FLAG
                | Paint.ANTI_ALIAS_FLAG);
        textPaint.density = context.getResources().getDisplayMetrics().density;
        textPaint.setColor(newColor);
        int margin = DensityUtil.dp2px(context, 1);
        drawText(c, textPaint, true,
                (int) Math.floor(w - radius2 * 2 + margin), 0 + margin, w
                        - margin, (int) Math.floor(radius2 * 2) - margin, unit,
                Gravity.CENTER);
        bmp.recycle();
        return tmp;
    }

    /**
     * @param canvas  画布
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param timeStr 需要绘制的字符串
     * @param gravity 对齐方式,仅支持TOP,BOTTOM,CENTER_VERTICAL
     * @return 字体非空高度
     * @Title drawTime
     * @Description 绘制字符串
     * @author 陈国宏
     * @date 2014年10月15日 上午10:28:58
     */
    public static int drawText(Canvas canvas, TextPaint mTextPaint,
                               boolean isCalAgain, int left, int top, int right, int bottom,
                               String timeStr, int gravity) {
        int h;
        FontMetrics fm = null;
        if (isCalAgain) {
            fm = initTextPaint(mTextPaint, right - left, bottom - top, timeStr);
        } else {
            fm = mTextPaint.getFontMetrics();
        }
        h = (int) (Math.ceil(fm.descent - fm.ascent));// 获得字体高度
        switch (gravity) {
            case Gravity.TOP:
            case Gravity.TOP | Gravity.LEFT:
                canvas.drawText(timeStr, left, top - fm.ascent - h / 8, mTextPaint);
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Gravity.LEFT:
                canvas.drawText(timeStr, left, bottom - fm.descent + h / 8,
                        mTextPaint);
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                canvas.drawText(timeStr, left, (bottom + top) / 2 + h / 2
                        - fm.descent, mTextPaint);
                break;
            case Gravity.TOP | Gravity.RIGHT:
            case Gravity.RIGHT:
                canvas.drawText(timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr)),
                        top - fm.ascent - h / 8, mTextPaint);
                break;
            case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
                canvas.drawText(timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr)),
                        (bottom + top) / 2 + h / 2 - fm.descent, mTextPaint);
                break;
            case Gravity.CENTER:
                canvas.drawText(
                        timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr)) / 2,
                        (bottom + top) / 2 + h / 2 - fm.descent, mTextPaint);
                break;
            default:
                canvas.drawText(timeStr, left, top - fm.ascent - h / 8, mTextPaint);
                break;
        }
        return (int) -fm.ascent;
    }

    /**
     * @param mTextPaint
     * @param width
     * @param height
     * @param str
     * @return
     * @Title initTextPaint
     * @Description 最大化字体
     * @author 陈国宏
     * @date 2014年12月1日 下午3:41:53
     */
    private static FontMetrics initTextPaint(TextPaint mTextPaint, float width,
                                             float height, String str) {
        mTextPaint.setTextSize(1f);
        String[] lines = str.trim().split("\n");
        float[] result = getTextSize(str, (Paint) mTextPaint, lines);
        float textSize = mTextPaint.getTextSize();
        while (result[0] <= width && result[1] <= height) {
            textSize = mTextPaint.getTextSize();
            mTextPaint.setTextSize(mTextPaint.getTextSize() + 1f);
            result = getTextSize(str, (Paint) mTextPaint, lines);
        }
        mTextPaint.setTextSize(textSize);
        return mTextPaint.getFontMetrics();
    }

    /**
     * @param str
     * @param paint
     * @param lines
     * @return
     * @Title getTextSize
     * @Description 获取字体的大小
     * @author 陈国宏
     * @date 2014年12月1日 下午3:42:17
     */
    private static float[] getTextSize(String str, Paint paint, String[] lines) {
        float[] max = new float[]{0, 1};
        String maxLine = lines[0];
        for (int j = 0; j < lines.length; j++) {
            if (lines[j].length() > maxLine.length()) {
                maxLine = lines[j];
            }
        }
        max[0] = paint.measureText(maxLine);
        FontMetrics fm = paint.getFontMetrics();
        max[1] = lines.length * (fm.descent - fm.ascent) * 6 / 8;
        return max;
    }


    public static String omittText(String str, int startRemained, int endRemained) {
        if (startRemained < 0) {
            startRemained = 0;
        }
        if (endRemained < 0) {
            endRemained = 0;
        }
        if (startRemained + endRemained > str.length()) {
            startRemained = str.length() / 2 - 1;
            endRemained = str.length() / 2 - 1;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, startRemained));
        int len = str.length() - startRemained - endRemained;
        while (len > 0) {
            sb.append('*');
            len--;
        }
        sb.append(str.substring(str.length() - startRemained));
        return sb.toString();
    }

    /**
     * 获取assets文本文件内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
}
