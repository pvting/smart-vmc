package com.vmc.yichu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.vmc.core.utils.FileUtils;
import com.vmc.core.utils.InitUtils;
import com.want.base.sdk.utils.TimeUtils;
import com.want.vmc.core.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vmc.core.log;

/**
 * <b>Create Date:</b>2017/6/20 10:20<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class SDKLogUploadUtils {

    private final String TAG = "SDKLogUploadUtils";

    private final String BUCKET_NAME = "yichu-log";

    private static volatile SDKLogUploadUtils mInstance;

    private BosClient mBosClient;


    private SDKLogUploadUtils() {}


    public static SDKLogUploadUtils getInstance() {
        if (mInstance == null) {
            synchronized (SDKLogUploadUtils.class) {
                if (mInstance == null) {
                    mInstance = new SDKLogUploadUtils();
                }
            }
        }
        return mInstance;
    }


    /**
     * 配置SDK日志上传时间
     *
     * @param context
     */
    public void setUploadTime(Context context) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        final String time = TimeUtils.format("yyyy-MM-dd HH:mm:ss", calendar.getTimeInMillis());
        log.d(TAG, "setUploadTime: SDK日志第一次压缩并上传时间: " + time);

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(Constants.Action.SDK_ZIP_UPLOAD_LOG);
        final PendingIntent
                pendingIntent =
                PendingIntent.getBroadcast(context, 110, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 9);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);

        final String time2 = TimeUtils.format("yyyy-MM-dd HH:mm:ss", calendar2.getTimeInMillis());
        log.d(TAG, "setUploadTime: SDK日志第二次上传时间: " + time2);

        final Intent intent2 = new Intent(Constants.Action.SDK_UPLOAD_LOG);
        final PendingIntent
                pendingIntent2 =
                PendingIntent.getBroadcast(context, 111, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.HOUR_OF_DAY, 17);
        calendar3.set(Calendar.MINUTE, 59);
        calendar3.set(Calendar.SECOND, 59);

        final String time3 = TimeUtils.format("yyyy-MM-dd HH:mm:ss", calendar3.getTimeInMillis());
        log.d(TAG, "setUploadTime: SDK日志第三次上传时间: " + time3);

        final Intent intent3 = new Intent(Constants.Action.SDK_UPLOAD_LOG);
        final PendingIntent
                pendingIntent3 =
                PendingIntent.getBroadcast(context, 112, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent3);

    }


    /**
     * 上传日志
     *
     * @param context
     */
    public void zipAndUploadSDKlog(final Context context) {

        final String logpath = File.separator +
                               "sdcard" +
                               File.separator +
                               "Android" +
                               File.separator +
                               "data" +
                               File.separator +
                               context.getPackageName() +
                               File.separator +
                               "log";


        log.v(TAG, "zipAndUploadSDKlog: 开始上报SDK日志");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                File logFile = new File(logpath);
                if (!logFile.exists()) {//SDKlog文件夹 是否存在
                    log.v(TAG, "zipAndUploadSDKlog: SDKlog文件不存在");
                    return null;
                }

                File zipDirectory = new File(Environment.getExternalStorageDirectory(), "sdk_log");
                if (!zipDirectory.exists()) {
                    log.v(TAG, "zipAndUploadSDKlog: 压缩的文件夹不存在,创建文件夹");
                    zipDirectory.mkdirs();
                }

                zipBeforeFile(logpath, zipDirectory, context);

                if (!initBDC(context)) {//判断百度云是否初始化
                    log.i(TAG, "zipAndUploadSDKlog: 百度云未初始化");
                    return null;
                }

                for (File fileItem : zipDirectory.listFiles()) {
                    doLogUpload(fileItem);
                }
                return null;
            }
        }.execute();

    }


    /**
     * 上传日志
     *
     * @param context
     */
    public void uploadSDKlog(final Context context) {


        log.v(TAG, "zipAndUploadSDKlog: 开始上报SDK日志");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                File zipDirectory = new File(Environment.getExternalStorageDirectory(), "sdk_log");
                if (!zipDirectory.exists()) {
                    log.v(TAG, "zipAndUploadSDKlog: 压缩的文件夹不存在,创建文件夹");
                    zipDirectory.mkdirs();
                }

                if (!initBDC(context)) {//判断百度云是否初始化
                    log.i(TAG, "zipAndUploadSDKlog: 百度云未初始化");
                    return null;
                }


                for (File fileItem : zipDirectory.listFiles()) {
                    doLogUpload(fileItem);
                }
                return null;
            }
        }.execute();

    }


    public void zipBeforeFile(String logpath, File zipDirectory, Context context) {
        for (int i = 1; i < 4; i++) {
            //获取前一天
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            Date time = cal.getTime();
            final String beforeDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time);

            //需要压缩的文件集合
            final ArrayList<File> files = new ArrayList<>();

            final File file1 = new File(logpath + File.separator + "activate" + beforeDay + ".txt");
            final File file2 = new File(logpath + File.separator + "comm_debug" + beforeDay + ".txt");
            final File file3 = new File(logpath + File.separator + "MainHandler" + beforeDay + ".txt");


            if (file1.exists()) {
                files.add(file1);
            }

            if (file2.exists()) {
                files.add(file2);
            }

            if (file3.exists()) {
                files.add(file3);
            }

            if (files.size() > 0) {
                String filename = InitUtils.getFactoryCode(context) + "_" + beforeDay + ".zip";

             FileUtils.createZip(filename, zipDirectory.getAbsolutePath(), files);
                
            } else {
                log.v(TAG, "zipAndUploadSDKlog: "+beforeDay+":没有可压缩的日志文件");
            }

        }

    }


    /**
     * 执行上传动作
     *
     * @param f
     */
    private void doLogUpload(File f) {


        log.v(TAG, "doLogUpload, 上传文件: " + f.getAbsolutePath());

        final String fileName = f.getName();//文件名


        log.v(TAG, "doLogUpload, objectKey: " + fileName);

        try {
            PutObjectResponse response = mBosClient.putObject(BUCKET_NAME, fileName, f);
            final String etag = response.getETag();
            log.v(TAG, "doLogUpload, etag: " + etag);

            if (!TextUtils.isEmpty(etag)) {
                log.v(TAG, "doLogUpload, 上传成功,即将删除");
                f.delete();
            }
        } catch (BceServiceException e) {
            log.e(TAG, "doLogUpload Error ErrorCode: " + e.getErrorCode());
            log.e(TAG, "doLogUpload Error RequestId: " + e.getRequestId());
            log.e(TAG, "doLogUpload Error StatusCode: " + e.getStatusCode());
            log.e(TAG, "doLogUpload Error Message: " + e.getMessage());
            log.e(TAG, "doLogUpload Error ErrorType: " + e.getErrorType());
        } catch (BceClientException e) {
            log.e(TAG, "Error Message: " + e.getMessage());
        }
    }

    /**
     * 初始化百度云上传服务
     *
     * @param context
     *
     * @return
     */
    private boolean initBDC(Context context) {
        try {
            if (null == mBosClient) {
                String ACCESS_KEY = context.getString(com.vmc.core.R.string.bos_access_key);
                String SECRET_KEY = context.getString(com.vmc.core.R.string.bos_secret_key);
                String END_POINT = context.getString(com.vmc.core.R.string.bos_end_point);
                BosClientConfiguration configuration = new BosClientConfiguration();
                configuration.setCredentials(new DefaultBceCredentials(ACCESS_KEY, SECRET_KEY));
                configuration.setEndpoint(END_POINT);
                mBosClient = new BosClient(configuration);
            }
            String prefix = InitUtils.getInitMachineId(context);//获取机器ID
            if (TextUtils.isEmpty(prefix)) {
                log.w(TAG, "initBDC: 当前机器ID不存在.");
                return false;
            }
            final boolean exists = mBosClient.doesBucketExist(BUCKET_NAME);
            if (!exists) {
                log.d(TAG, "initBDC: BOS bucket 没有初始化过, 立即创建.");
                mBosClient.createBucket(BUCKET_NAME);
            }
        } catch (BceServiceException e) {
            log.e(TAG, "initBDC Error ErrorCode: " + e.getErrorCode());
            log.e(TAG, "initBDC Error RequestId: " + e.getRequestId());
            log.e(TAG, "initBDC Error StatusCode: " + e.getStatusCode());
            log.e(TAG, "initBDC Error Message: " + e.getMessage());
            log.e(TAG, "initBDC Error ErrorType: " + e.getErrorType());
            return false;
        } catch (BceClientException e) {
            log.e(TAG, "initBDC Error Message: " + e.getMessage());
            return false;
        }
        return true;
    }
}