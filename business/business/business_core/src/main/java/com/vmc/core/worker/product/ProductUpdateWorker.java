package com.vmc.core.worker.product;

import android.content.Context;


import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.product.OdooProductList;
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
public class ProductUpdateWorker extends Worker {
    private static final String TAG = "ProductUpdateWorker";

    private WeakReference<Context> mContextWeakReference;

    private static ProductUpdateWorker INSTANCE;

    private ProductUpdateWorker(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static ProductUpdateWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (ProductUpdateWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ProductUpdateWorker(context);
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
            odoo.stackProductList(new OdooHttpCallback<OdooProductList>(context) {
                @Override
                public void onSuccess(OdooProductList result) {
                    BLLController.getInstance().updateStackProduct(result, context);
                    log.v(TAG, "stackProductList-->onSuccess: 获取商品列表成功");
                }

                @Override
                public void onError(HttpError error) {
                    super.onError(error);
                    log.w(TAG, "stackProductList-->onError: 获取商品列表失败");
                }

            });
        }
        // 1分钟再次更新
        safeWait(Time.WORK_INTERVAL);
    }
}
