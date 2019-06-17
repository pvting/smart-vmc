package com.vmc.core.worker.product;

import android.content.Context;


import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.product.OdooPromotionList;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.worker.Worker;
import com.vmc.core.utils.InitUtils;
import com.want.base.http.error.HttpError;

import java.lang.ref.WeakReference;

import vmc.core.log;


/**
 * <b>Create Date:</b> 2016/12/7<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b>
 * 自动从服务器拉取最新的商品列表
 * <br>
 */
public class PromotionUpdateWorker extends Worker {
    private static final String TAG = "PromotionUpdateWorker";

    private WeakReference<Context> mContextWeakReference;

    private static PromotionUpdateWorker INSTANCE;

    private PromotionUpdateWorker(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static PromotionUpdateWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (PromotionUpdateWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new PromotionUpdateWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onWorking() {
        final Context context = mContextWeakReference.get();
        if (null == context) {
            // when context is null, application has been killed.
            stopWork();
            return;
        }

        final Odoo odoo = Odoo.getInstance(context);
        if (InitUtils.isInit(context)) {
            odoo.promotionList(new OdooHttpCallback<OdooPromotionList>(context) {
                @Override
                public void onSuccess(OdooPromotionList result) {
                    BLLProductUtils.updatePromotionList(result, context);
                    log.v(TAG, "updatePromotion-->onSuccess: 同步后台促销成功");
                }

                @Override
                public void onError(HttpError error) {
                    super.onError(error);
                    log.w(TAG, "updatePromotion-->onError: 同步后台促销失败");
                }

            });
        }
        // 5分钟再次更新
        safeWait(Time.MINUTE_5);
    }
}
