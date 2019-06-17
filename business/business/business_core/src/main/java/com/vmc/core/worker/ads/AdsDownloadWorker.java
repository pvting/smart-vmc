package com.vmc.core.worker.ads;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


import com.vmc.core.utils.AdsUtils;
import com.vmc.core.worker.Worker;
import com.want.base.sdk.utils.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

import vmc.core.log;

/**
 * <b>Create Date:</b> 9/5/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class AdsDownloadWorker extends Worker {

    public static final String ACTION = "vmc.vendor.ACTION_ADS_DOWNLOADED";

    private static final String TAG = "AdsDownloadWorker";

    private static final int TIME_OUT = 30 * 1000;

    private static AdsDownloadWorker INSTANCE;

    private final Queue<Request> mDownloadRequests = new ArrayDeque<>();

    private WeakReference<Context> mContext;

    //已经下载的文件大小
    private int mDownloadSize;

    private AdsDownloadWorker(Context context) {
        //no instance
        mContext = new WeakReference<Context>(context);
    }

    public static AdsDownloadWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (AdsDownloadWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new AdsDownloadWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    public void download(final String[] urls) {
        Request request;
        for (String url : urls) {
            request = new Request(url);
            mDownloadRequests.add(request);
        }
        safeNotify();
    }

    public void download(final String url) {
        download(new String[]{url});
    }

    private class Request {
        String url;

        Request(String url) {
            this.url = url;
        }
    }


    private void notifyDownloadChanged() {
        final Context context = mContext.get();
        if (null == context) {
            return;
        }

        final Intent intent = new Intent(ACTION);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
    }

    @Override
    protected void onWorking() {
        final Context context = mContext.get();
        File cacheDir = context.getCacheDir();
        cacheDir = new File(cacheDir, "ads");

        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                log.w(TAG, "onWorking: 广告目录创建失败，稍后重试.");
                safeWait(5000);
                return;
            }
        }

        Request request;
        HttpURLConnection conn = null;
        URL url;

        InputStream ins = null;
        RandomAccessFile fos = null;
        File cacheFile = null;

        synchronized (mDownloadRequests) {
            request = mDownloadRequests.poll();
        }

        if (null == request) {
            log.v(TAG, "onWorking: 下载队列为空, 暂停下载。");
            safeWait();
            return;
        }

        log.v(TAG, "onWorking: 待下载URL: " + request.url);

        try {
            cacheFile = new File(cacheDir, AdsUtils.getCacheFileName(request.url));
            if (cacheFile.exists()){

                int length = AdsUtils.getDownloadFileAttr(mContext.get(),AdsUtils.getCacheFileName(request.url));
                if(length!=-1&&length != cacheFile.length()){
                    cacheFile.delete();
                }else {
                    log.v(TAG, "onWorking: 文件已经下载了, 跳过下载。");
                    return;
                }
            }

            cacheFile = new File(cacheDir, AdsUtils.getCacheFileName(request.url) + "a");
            log.v(TAG, "onWorking: 文件保存位置: " + cacheFile.getAbsolutePath());

            url = new URL(request.url);
            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.connect();

            final int contentLength = conn.getContentLength();

            if (contentLength == -1) {
                log.v(TAG, "onWorking: 云端文件异常！跳过下载");
                conn.disconnect();
                return;
            }
            log.v(TAG, "onWorking: 云端大小=" + contentLength);
            /**
             * 保存下载文件的大小
             * 下次进行下载时，先判断文件在不在，再判断大小符不符合
             */
            AdsUtils.setDownloadFileAttr(mContext.get(),AdsUtils.getCacheFileName(request.url),contentLength);
            if (cacheFile.exists()) {
                mDownloadSize = (int) cacheFile.length();
            } else {
                mDownloadSize = 0;
            }
            log.v(TAG, "onWorking: 本地文件状态mDownloadSize=" + mDownloadSize);

            if (mDownloadSize == contentLength) {
                log.v(TAG, "onWorking: 云端文件正常且本地文件大小等于云端文件大小，跳过下载");
                return;
            }

            if (mDownloadSize > contentLength) {
                mDownloadSize = 0;
                log.v(TAG, "onWorking: 本地文件大于云端文件，重新下载");
                SystemClock.sleep(100);
                cacheFile.delete();
                return;
            }

            if (mDownloadSize < contentLength) {
                log.v(TAG, "onWorking: 本地文件大小小于云端，开始下载");

                fos = new RandomAccessFile(cacheFile, "rw");

                URL url1 = new URL(request.url);
                HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                con1.setConnectTimeout(TIME_OUT);
                con1.setReadTimeout(TIME_OUT);
                con1.setRequestProperty("Range", "bytes=" + mDownloadSize + "-" + contentLength);

                ins = con1.getInputStream();
                byte[] byts = new byte[1024 * 10];
                //设置开始写文件的位置
                fos.seek(mDownloadSize);

                //开始循环以流的形式读写文件
                int count = 0;
                while ((count = ins.read(byts)) >= 0) {
                    fos.write(byts, 0, count);
                    mDownloadSize = mDownloadSize + count;
                }

                log.v(TAG,"mDownloadSize="+mDownloadSize+",contentLength="+contentLength);

                if (mDownloadSize==contentLength){

                    if (null != ins) {
                        StreamUtils.close(ins);
                    }

                    if (null != fos) {
                        StreamUtils.close(fos);
                    }
                    SystemClock.sleep(500);

                    /**
                     * 视频加载黑屏
                     * 解决方案 重命名文件名
                     * 在下载之前，修改文件名
                     * 下载完成后，再把文件名改回原来的名字（MD5）
                     */
                    String oName = AdsUtils.getCacheFileName(request.url);
                    log.v(TAG, "request.url=" + request.url);
                    File newFile = new File(cacheDir, oName);
                    boolean is = cacheFile.renameTo(newFile);
                    if (!is) {
                        log.w(TAG, "onWorking: 改名出错");
                    }
                }
            }

            notifyDownloadChanged();
            log.v(TAG, "onWorking: 下载任务结束。");
        } catch (Exception e) {
            e.printStackTrace();
            log.w(TAG, "onWorking: 下载任务出错, exception: " + e.getMessage());
            // any of exception, return
        } finally {
            log.v(TAG, "onWorking: 结束当前文件下载。");
            if (null != ins) {
                StreamUtils.close(ins);
            }

            if (null != fos) {
                StreamUtils.close(fos);
            }

            if (null != conn) {
                conn.disconnect();
            }
        }
    }

}
