package com.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.HttpHandler.State;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.model.UpdateContent;
import com.model.UpdateInfo;
import com.model.UpdateResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class UpdateManager {
    private static final String PRODUCT_ID = "product_app_id";
    private static final String UPDATE_URL = "update_url";
    private static final String UPDATE_TYPE = "update_type";
    private static HttpUtils httpUtilsNoCache, httpUtils;
    private UpdateCallback callback;
    private Context ctx;

    public static final String UPDATEMANAGER = "UPDATEMANAGER";
    public static final int UPDATE_DB_CHECK_COMPLETED = 0;
    public static final int UPDATE_CHECK_COMPLETED = 1;
    public static final int UPDATE_DOWNLOADING = 2;
    public static final int UPDATE_DOWNLOAD_ERROR = 3;
    public static final int UPDATE_DOWNLOAD_COMPLETED = 4;
    public static final int UPDATE_DOWNLOAD_CANCELED = 5;
    public static final int MERGE_FILE_ERROR = 6;
    public static final int UPDATE_CHECK_ERROR = 7;

    public static final int FOUNDNEWVERSION = 666;
    public static final int NO_NEWVERSION = 777;
    public static final int CHECKUPDATE_FAIL = 888;

    private UpdateResult result;
    private String sdcardPath;// SDCard路径
    private String packageNameString;// APP包名
    private String newPathString;// 新的APP路径
    private String patchPathString;// 增量包路径
    private String oldPathString;// APP路径
    private HttpHandler<String> handler;
    private Gson gson;
    private Handler mHandler;
    private HttpHandler<File> downloadHandler;
    private Notification notification;
    private NotificationManager mNotifManager;

    public UpdateManager(Context context, UpdateCallback updateCallback) {
        ctx = context;
        callback = updateCallback;
        getCurVersion();
        sdcardPath = PathManager.getDownLoadDir();
        packageNameString = App.getPackageName(context);
        LogUtils.i("sdcard路径：" + sdcardPath);
        LogUtils.i("包名：" + packageNameString);
        gson = new Gson();
        mHandler = new Handler();
        mNotifManager = (NotificationManager) ctx
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public String getNewVersionName() {
        // return fileInfo.getVersionNum();
        if (result.getUpdateInfo() != null) {
            return result.getUpdateInfo().getVersionName();
        }
        return null;
    }

    public List<UpdateContent> getUpdateMessage() {
        if (result.getUpdateInfo() != null) {
            return result.getUpdateInfo().getUpdateList();
        }
        return null;
    }

    public String getDownloadUrl() {
        if (result.getUpdateInfo() != null) {
            return result.getUpdateInfo().getUrl();
        }
        return null;
    }

    private String getCurVersion() {
        return App.getVersionName(ctx);
    }

    public String getCurrentVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // 如果没有发现包的操作
            e.printStackTrace();
            return "null";
        }
        return pInfo.versionName;
    }

    public void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            LogUtils.i("文件" + file.getAbsolutePath() + "存在！");
            if (file.isFile()) { // 判断是否是文件
                boolean result = file.delete(); // delete()方法 你应该知道 是删除的意思;
                LogUtils.i("文件" + file.getAbsolutePath() + "删除结果为" + result);
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
        } else {
            LogUtils.i("文件" + file.getAbsolutePath() + "不存在！");
        }
    }

    public interface UpdateCallback {
        public static final int ERRORCODE_UNKNOWN = 10000;
        public static final int ERRORCODE_NO_PRODUCT_ID = 10001;
        public static final int ERRORCODE_NO_UPDATE_URL = 10002;
        public static final int ERRORCODE_NO_UPDATE_TYPE = 10003;
        public static final int ERRORCODE_JSON_PRASE_EXCEPTION = 10004;
        public static final int ERRORCODE_HTTP_EXCEPTION = 10005;
        public static final int ERRORCODE_SERVER_DB_CONNECT_FAIL = 10006;
        public static final int ERRORCODE_SERVER_PRODUCT_ID_NOT_EXIST = 10007;
        public static final int ERRORCODE_DOWNLOAD_ERROR = 10008;
        public static final int ERRORCODE_OUT_OF_MEMORY = 10009;
        public static final int ERRORCODE_IO_EXCEPTION = 10010;
        public static final int ERRORCODE_NAME_NOT_FOUND_EXCEPTION = 10011;
        public static final int ERRORCODE_MERGE_ERROR = 10012;

        public void onCheckUpdateStart();

        public void onCheckUpdateCancelled();

        public void onDownloadStart();

        public void onDownloadCancelled();

        public void onCheckUpdateCompleted(UpdateResult result);

        /**
         * @param erroCode 错误代码
         * @param error    异常对象
         * @param msg      内部提示信息
         * @Title checkUpdateError
         * @Description 检查更新失败
         * @author jerry
         * @date 2015年8月24日 下午5:11:58
         */
        public void onError(int erroCode, Object error, String msg);

        public void onDownloadProgressChanged(long total, long current,
                                              boolean isUploading);

        public void onDownloadCompleted(UpdateInfo updateInfo, File file);
    }

    private HttpUtils getNoCacheHttpUtils() {
        if (httpUtilsNoCache == null) {
            httpUtilsNoCache = new HttpUtils();
            // 设置当前请求的缓存时间
            httpUtilsNoCache.configCurrentHttpCacheExpiry(0 * 1000);
            // 设置默认请求的缓存时间
            httpUtilsNoCache.configDefaultHttpCacheExpiry(0);
            // 设置线程数
            httpUtilsNoCache.configRequestThreadPoolSize(5);
            httpUtilsNoCache.configResponseTextCharset("utf-8");
        }
        return httpUtilsNoCache;
    }

    private HttpUtils getHttpUtils() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
            // 设置当前请求的缓存时间
            httpUtils.configCurrentHttpCacheExpiry(3 * 60 * 1000);
            // 设置默认请求的缓存时间
            httpUtils.configDefaultHttpCacheExpiry(3 * 60 * 1000);
            // 设置线程数
            httpUtils.configRequestThreadPoolSize(5);
            httpUtils.configResponseTextCharset("utf-8");
        }
        return httpUtils;
    }

    public void checkUpdate() {
        if (callback == null) {
            LogUtils.e("UpdateCallback can't be null！！");
        }
        long timeStart = System.currentTimeMillis();
        String product_id = getProductId();
        String type = getUpdateType();
        String updateUrl = getUpdateUrl();
        if (TextUtils.isEmpty(product_id)) {
            callback.onError(UpdateCallback.ERRORCODE_NO_PRODUCT_ID,
                    new NullPointerException("update id can not be null!"),
                    "update id can not be null!");
            return;
        }
        if (TextUtils.isEmpty(updateUrl)) {
            callback.onError(UpdateCallback.ERRORCODE_NO_UPDATE_URL,
                    new NullPointerException("update url can not be null!"),
                    "update url can not be null!");
            return;
        }
        if (TextUtils.isEmpty(type)) {
            callback.onError(UpdateCallback.ERRORCODE_NO_UPDATE_TYPE,
                    new NullPointerException("update type can not be null!"),
                    "update type can not be null!");
            return;
        }
        String urlString = updateUrl
                + "/info/AppVersionInfo/checkversion?productId=" + product_id
                + "&versionCode=" + App.getVersionCode(ctx) + "&type=" + type
                + "&oldsha1code=" + App.getSha1SumString(ctx);
        long timeUsed = System.currentTimeMillis() - timeStart;
        LogUtils.d("检验URL为" + urlString + "\n生成URL用时:" + timeUsed);
        if (handler != null && handler.getState() != State.FAILURE
                && handler.getState() != State.SUCCESS
                && handler.getState() != State.CANCELLED) {
            // 关闭handler后 onStart()和onLoading()还是会执行
            handler.cancel();
        }
        HttpUtils httpUtils = getNoCacheHttpUtils();
        handler = httpUtils.send(HttpMethod.GET, urlString,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.onCheckUpdateStart();
                    }

                    @Override
                    public void onCancelled() {
                        super.onCancelled();
                        callback.onCheckUpdateCancelled();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.statusCode == 200) {
                            try {
                                result = gson.fromJson(responseInfo.result,
                                        new TypeToken<UpdateResult>() {
                                        }.getType());
                                switch (result.code) {
                                    case UpdateResult.CHECK_SUCCESS:
                                        callback.onCheckUpdateCompleted(result);
                                        break;
                                    case UpdateResult.ERROR_DB_CONNECT_FAIL:
                                        callback.onError(
                                                UpdateCallback.ERRORCODE_SERVER_DB_CONNECT_FAIL,
                                                null,
                                                "Server can not connect to DB Service!");
                                        break;
                                    case UpdateResult.ERRPR_NO_PRODUCT_ID:
                                        callback.onError(
                                                UpdateCallback.ERRORCODE_SERVER_PRODUCT_ID_NOT_EXIST,
                                                null,
                                                "The Product ID does not exist!");
                                        break;
                                    default:
                                        break;
                                }
                            } catch (JsonParseException e) {
                                e.printStackTrace();
                                callback.onError(
                                        UpdateCallback.ERRORCODE_JSON_PRASE_EXCEPTION,
                                        e, e.getMessage());
                            }
                        } else {
                            callback.onError(UpdateCallback.ERRORCODE_UNKNOWN,
                                    responseInfo, responseInfo.result);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        callback.onError(
                                UpdateCallback.ERRORCODE_HTTP_EXCEPTION, error,
                                msg);
                    }
                });
    }

    public String getUpdateUrl() {
        return App.getMetaData(ctx, UPDATE_URL);
    }

    public String getUpdateType() {
        return App.getMetaData(ctx, UPDATE_TYPE);
    }

    public String getProductId() {
        return App.getMetaData(ctx, PRODUCT_ID);
    }

    public void download() {
        if (result == null) {
            LogUtils.e("请先检查更新");
            return;
        }
        UpdateInfo updateInfo = result.getUpdateInfo();
        if (updateInfo == null) {
            LogUtils.e("更新信息为空，无法进行下载");
            return;
        }
        if (sdcardPath == null) {
            callback.onError(UpdateCallback.ERRORCODE_DOWNLOAD_ERROR, null,
                    "sdcard can not be used!");
            return;
        }
        newPathString = sdcardPath + App.getPackageName(ctx) + "_"
                + updateInfo.getVersionName() + "_"
                + updateInfo.getVersionCode() + ".apk";
        patchPathString = sdcardPath + App.getPackageName(ctx) + "_"
                + App.getVersionCode(ctx) + "_" + updateInfo.getVersionCode()
                + ".patch";
        String target = patchPathString;
        if (UpdateInfo.FULL_UPDATE.equals(updateInfo.getUpdateType())) {
            target = newPathString;
        } else if (UpdateInfo.INC_UPDATE.equals(updateInfo.getUpdateType())) {
            target = patchPathString;
        }
        File file = new File(newPathString);
        if (file.exists()) {
            LogUtils.d("文件已存在");
            try {
                String localSha1Value = SHA1Util.sumFile(ctx, file);
                if (localSha1Value.equals(updateInfo.getNewSHA())) {
                    callback.onDownloadCompleted(updateInfo, new File(
                            newPathString));
                    return;
                }
            } catch (OutOfMemoryError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        downloadHandler = getHttpUtils().download(updateInfo.getUrl(), target,
                true, new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.onDownloadStart();
                    }

                    @Override
                    public void onCancelled() {
                        super.onCancelled();
                        callback.onDownloadCancelled();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        LogUtils.d(total + "/" + current);
                        callback.onDownloadProgressChanged(total, current,
                                isUploading);
                        if (total == current) {// 下载完成
                            try {
                                checkDownloadState();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {

                        // callback.onDownloadCompleted(result.getUpdateInfo(),
                        // responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (error.getExceptionCode() == 416) {
                            // 416:maybe the file has downloaded completely
                            if (result == null) {
                                callback.onError(
                                        UpdateCallback.ERRORCODE_HTTP_EXCEPTION,
                                        error, msg);
                            } else {
                                UpdateInfo updateInfo = result.getUpdateInfo();
                                File file = null;
                                if (UpdateInfo.FULL_UPDATE.equals(updateInfo
                                        .getUpdateType())) {
                                    file = new File(newPathString);
                                } else if (UpdateInfo.INC_UPDATE
                                        .equals(updateInfo.getUpdateType())) {
                                    file = new File(patchPathString);
                                }
                                if (!file.exists()) {
                                    LogUtils.e("416:查找文件，发现不存在");
                                    return;
                                }
                                callback.onDownloadProgressChanged(
                                        file.length(), file.length(), false);
                                try {
                                    checkDownloadState();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            callback.onError(
                                    UpdateCallback.ERRORCODE_HTTP_EXCEPTION,
                                    error, msg);
                        }
                    }
                });
    }

    public void checkDownloadState() {
        try {
            UpdateInfo updateInfo = result.getUpdateInfo();
            if (UpdateInfo.FULL_UPDATE.equals(updateInfo.getUpdateType())) {
                // 判断SHA1
                LogUtils.d("full package downloaded，start checking...");
                if (updateInfo.getNewSHA().equals(
                        SHA1Util.sumFile(ctx, newPathString))) {
                    callback.onDownloadCompleted(updateInfo, new File(
                            newPathString));
                } else {
                    if (!TextUtils.isEmpty(newPathString)) {
                        LogUtils.d("下载失败，删除文件");
                        deleteFile(new File(newPathString));
                    }
                    callback.onError(UpdateCallback.ERRORCODE_DOWNLOAD_ERROR,
                            null, "The file downloaded is damaged!");
                }
            } else if (UpdateInfo.INC_UPDATE.equals(updateInfo.getUpdateType())) {
                // 判断SHA1
                LogUtils.d("inc package downloaded，start checking...");
                if (updateInfo.getIncreaseSHA().equals(
                        SHA1Util.sumFile(ctx, patchPathString))) {// a14c575bdbcabdf40ab8f09b570f6a55d658a4d8
                    // 合并文件
                    File file = new File(ctx.getPackageManager()
                            .getApplicationInfo(packageNameString, 0).sourceDir);
                    oldPathString = sdcardPath + App.getPackageName(ctx) + "_"
                            + App.getVersionName(ctx) + "_"
                            + App.getVersionCode(ctx) + ".apk";
                    LogUtils.i(oldPathString);
                    // 从系统目录拷贝出sdcard
                    InputStream fosfrom = new FileInputStream(file);
                    OutputStream fosto = new FileOutputStream(new File(
                            oldPathString), false);
                    byte bt[] = new byte[1024];
                    int c;
                    while ((c = fosfrom.read(bt)) > 0) {
                        fosto.write(bt, 0, c);
                    }
                    fosfrom.close();
                    fosto.close();
                    PatchUtils.patch(oldPathString, newPathString,
                            patchPathString);
                    if (updateInfo.getNewSHA().equals(
                            SHA1Util.sumFile(ctx, newPathString))) {
                        callback.onDownloadCompleted(updateInfo, new File(
                                newPathString));
                    } else {
                        if (!TextUtils.isEmpty(newPathString)) {
                            LogUtils.d("下载失败，删除文件");
                            deleteFile(new File(newPathString));
                        }
                        callback.onError(UpdateCallback.ERRORCODE_MERGE_ERROR,
                                null, "Failed to merge file!");
                    }
                } else {
                    callback.onError(UpdateCallback.ERRORCODE_DOWNLOAD_ERROR,
                            null, "The file downloaded is damaged!");
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            callback.onError(UpdateCallback.ERRORCODE_OUT_OF_MEMORY, e,
                    e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
            callback.onError(UpdateCallback.ERRORCODE_IO_EXCEPTION, e2,
                    e2.getMessage());
        } catch (NameNotFoundException e3) {
            e3.printStackTrace();
            callback.onError(UpdateCallback.ERRORCODE_NAME_NOT_FOUND_EXCEPTION,
                    e3, e3.getMessage());
        } finally {
            // 删除临时数据
            if (!TextUtils.isEmpty(oldPathString)) {
                deleteFile(new File(oldPathString));
            }
            if (!TextUtils.isEmpty(patchPathString)) {
                deleteFile(new File(patchPathString));
            }
        }
    }

    public void changeDownload() {
        if (downloadHandler != null) {
            if (downloadHandler.getState().equals(State.STARTED)
                    || downloadHandler.getState().equals(State.LOADING)) {
                LogUtils.d("处于已启动或者加载状态");
                if (downloadHandler.isPaused()) {
                    LogUtils.d("恢复下载");
                    downloadHandler.resume();
                } else {
                    LogUtils.d("暂停下载");
                    downloadHandler.pause();
                }
            }
            downloadHandler.pause();

        }
    }

    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.getExtraInfo() != null
                        && mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}