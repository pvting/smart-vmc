package com.vmc.core.worker.product;

import android.content.Context;

import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.worker.Worker;

import java.lang.ref.WeakReference;


/**
 * <b>Create Date:</b> 2016/12/7<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b>
 * 自动从服务器拉取最新的商品列表
 * <br>
 */
public class PromotionStatusWorker extends Worker {
    private static final String TAG = "PromotionUpdateWorker";

    private WeakReference<Context> mContextWeakReference;

    private static PromotionStatusWorker INSTANCE;

    private PromotionStatusWorker(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static PromotionStatusWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (PromotionStatusWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new PromotionStatusWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onWorking() {
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            return;
        }

        BLLProductUtils.updatePromotionDetailOfProduct(context);

        // 1分钟再次更新
        safeWait(Time.MINUTE_1);
    }
}
