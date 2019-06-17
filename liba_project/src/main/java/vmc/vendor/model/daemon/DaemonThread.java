package vmc.vendor.model.daemon;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import vmc.core.log;

/**
 * <b>Create Date:</b> 9/12/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class DaemonThread extends Thread {
    private static final String TAG = "DaemonThread";
    /** 主线程未响应时间为4秒 */
    private static final int DELAY = 4 * 1000;

    private static final int MAX_ERROR_COUNT = 3;

    private final Object lock = new Object();
    private Handler mHandler;
    private boolean checked = false;
    private int mErrorCount = 0;
    private Context mContext;

    public DaemonThread(Context context) {
        this.mContext = context;
        setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        setDaemon(true);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        super.run();
        log.d(TAG, "run: 主线程守护线程已启动");
        while (true) {
            log.v(TAG, "run: 发送消息给主线程");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    log.v(TAG, "run: 主线程已响应");
                    checked = true;
                }
            });

            // 等待程序响应
            safeWait();
            if (!checked) {
                mErrorCount += 1;
                log.w(TAG, "run: 主线程未响应, 未响应次数: " + mErrorCount);
                if (mErrorCount >= MAX_ERROR_COUNT) {
                    log.e(TAG, "run: 主线程未响应次数超过限制, 重启主线程");
                    logStackTrace();
                    restartMainProcess();
                }
            } else {
                // 重置为0
                mErrorCount = 0;
            }

            // 重置状态
            checked = false;
        }
    }

    private void safeWait() {
        synchronized (lock) {
            try {
                lock.wait(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logStackTrace() {
        log.d(TAG, "stackTrace: ============= 记录线程堆栈 =============");
        final StackTraceElement[] ses = mHandler.getLooper().getThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement se : ses) {
            builder.append(se.toString());
            builder.append("\n");
        }
        log.v(TAG, "stackTrace: 线程堆栈信息: \n" + builder.toString());
    }

    private void restartMainProcess() {
        DaemonService.restartMainProcess(mContext);
    }
}
