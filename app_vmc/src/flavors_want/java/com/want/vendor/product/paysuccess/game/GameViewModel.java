package com.want.vendor.product.paysuccess.game;

import android.content.Context;
import android.databinding.Bindable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.vmc.core.BLLController;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.BLLProductUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.common.ToastUtil;
import com.want.vendor.deliver.DeliverActivity;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vendor.product.info.ProductInfoActivity;
import com.want.vendor.product.list.ProductListActivity;


/**
 * ViewModel Stub.
 */
public class GameViewModel extends AbsViewModel {

    private Context mContext;
    private String outProudctStatus;
    private boolean isPay = true;
    private String payType = "";
    private Context context;
    private int id;
    private Toast mToast;

    private long firstClickTime = 0L;
    private static int CONTINUE_DURATION = 3 * 1000;

    public GameViewModel() {
    }

    public GameViewModel(Context context, String payType, int productId) {

        super(context);
        this.context = context;
        this.payType = payType;
        this.id = productId;
    }


    public GameViewModel(Context context) {
        super(context);
        this.mContext = context;
    }


    // TODO
    public void onClick(final View view) {
        view.setEnabled(false);
        if (!TextUtils.isEmpty(payType) && payType.equals("CODE")) {
            MEventBus.getDefault().post(new ClearEditTextEventBus());
            view.setEnabled(true);
            DeliverActivity.start(view.getContext());
        } else {
            BLLProduct product = BLLProductUtils.getProductById(id);
            BLLStackProduct bsp = BLLController.getInstance().getSaleableStackProductByProduct(product);
            firstClickTime = System.currentTimeMillis() - firstClickTime;
            if (bsp == null) {
                if (firstClickTime >= CONTINUE_DURATION) {
                    ToastUtil.toast(context, "刚刚购买的商品没货了\n请选购其他商品~", false);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.hiddeToast();
                        view.setEnabled(true);
                        ProductListActivity.start(context);
                    }
                }, CONTINUE_DURATION);
                return;
            }
            //生成本地订单
            BLLController.getInstance().selectProduct(context, bsp);
            ProductInfoActivity.start(context, bsp);
        }
    }


    @Bindable
    public int getContinueIsShow() {
        return View.VISIBLE;
    }

    @Bindable
    public String getContinueIsShop() {
        if (!TextUtils.isEmpty(payType) && payType.equals("CODE")) {
            return "继续提货";
        }
        return "再次购买";
    }


}
