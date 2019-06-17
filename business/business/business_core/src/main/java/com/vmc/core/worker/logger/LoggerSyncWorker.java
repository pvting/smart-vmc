package com.vmc.core.worker.logger;

import android.content.Context;
import android.text.TextUtils;

import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.vmc.core.utils.FileUtils;
import com.vmc.core.utils.InitUtils;
import com.vmc.core.worker.Worker;
import com.want.base.sdk.utils.TimeUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import vmc.core.log;
import vmc.machine.core.VMCContoller;


/**
 * <b>Create Date:</b> 05/11/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 日志同步服务
 * <br>
 */
public class LoggerSyncWorker extends Worker {
    private static final String TAG = "LoggerSyncWorker";
    /** 最大文件大小 */
    private static final long FILE_SIZE = log.FILE_MAX_SIZE;
    /** 文件修改时间间隔 */
    private static final long INTERNAL_TIMES = 10 * 60 * 1000;
    /** 文件最大修改时间间隔 */
    private static final long INTERNAL_TIMES_MAX = 60 * 60 * 1000;

    private static final String BUCKET_NAME = "svmlog";

    private static LoggerSyncWorker INSTANCE;

    private WeakReference<Context> mContextWeakReference;

    private File mLogPath;

    private File mUpLoadLogPath;

    private BosClient mBosClient;

    private DateFormat mDayFormat;

    private LoggerSyncWorker(Context context) {
        super();
        this.mContextWeakReference = new WeakReference<>(context);
    }

    public static LoggerSyncWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (LoggerSyncWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new LoggerSyncWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            return;
        }

        final File cacheDir = context.getCacheDir();//获取app缓存目录
        mLogPath = new File(cacheDir, "log");//获取log目录
        mUpLoadLogPath = new File(cacheDir, "log_zip");//需要上传的日志地址
        mDayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//本地时间格式化
    }

    @Override
    protected void onWorking() {
        // 10分钟检测一次日志的上传
        safeWait(Time.MINUTE_1*10);

        if (!mLogPath.exists() || !mLogPath.isDirectory()) {
            log.w(TAG, "onWorking: log目录不存在或者不是目录");
            return;
        }


        if (!mUpLoadLogPath.exists() || !mUpLoadLogPath.isDirectory()) {
            mUpLoadLogPath.mkdir();
        }


        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            return;
        }

        final String prefix = InitUtils.getInitMachineId(context);//获取机器ID
        if (TextUtils.isEmpty(prefix)) {
            log.w(TAG, "onWorking: 当前机器ID不存在.");
            return;
        }

        for (File file : mLogPath.listFiles()) {//循环上传里面所有文件
            if (validFile(file)) {//00580001_2017-07-22*15:23:23.zip
                String fileName = VMCContoller.getInstance().getVendingMachineId() +
                                  "_" +
                                  mDayFormat.format(file.lastModified()) +
                                  ".zip";
                boolean isSuccess = FileUtils.createZip(fileName, mUpLoadLogPath.getAbsolutePath(), file);
                if (isSuccess) {
                    file.delete();
                }
            }
        }
        if (!initBDC(context)) {//判断百度云是否初始化
            log.w(TAG, "onWorking: 百度云未初始化.");
            return;
        }
        for (File file : mUpLoadLogPath.listFiles()) {
            doLogUpload(file);
        }
    }

    /**
     * 上传文件
     *
     * @param f
     */
    private void doLogUpload(File f) {

        final String fileName = f.getName();//文件名

        log.v(TAG, "doLogUpload, 上传的文件名: " + fileName);

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
     * 判断是否满足条件
     *
     * @param f
     *
     * @return
     */
    private boolean validFile(File f) {
        // 文件存在，且不是文件夹
        if (f.exists() && f.isFile()) {
            final long lastModified = f.lastModified();//最后一次修改时间
            final long current = System.currentTimeMillis();//当前系统时间

            final long length = f.length();//文件长度

            log.v(TAG, "validFile, file: " + f.getName() +
                       ", length: " + length +
                       ", lastModified: " + TimeUtils.getSimpleDateInfo(lastModified) +
                       ", current: " + TimeUtils.getSimpleDateInfo(current));


            if (length == 0) {
                return false;
            }

            // 文件满足大小
            if (length >= FILE_SIZE) {
                // 文件距上次修改时间大于指定时间
                if (current - lastModified >= INTERNAL_TIMES) {
                    log.i(TAG, "validFile: 文件满足大小,文件距上次修改时间大于指定时间");
                    return true;
                }
            } else if (current - lastModified >= INTERNAL_TIMES_MAX) {
                // 文件不满足大小时，如果很久没有更新了，则该文件可能不会再被写入，需要上传
                log.i(TAG, "validFile: 日志文件很久没有更新了,可能不会再被写入，需要上传");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
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
