package com.vmc.core;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.model.init.MachineInit;
import com.vmc.core.model.instruct.InstructList;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.pay.PayStatusResult;
import com.vmc.core.model.pay.QRCodeResult;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.DeliverProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.OdooPromotionList;
import com.vmc.core.model.product.OdooStockList;
import com.vmc.core.model.product.PickUpProduct;
import com.vmc.core.model.product.RestVerify;
import com.vmc.core.model.stock.Stock;
import com.vmc.core.model.user.UserInfo;
import com.vmc.core.request.ads.AdsListRequest;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.request.init.InitRequest;
import com.vmc.core.request.instruct.InstructRequest;
import com.vmc.core.request.instruct.InstructUpdateRequest;
import com.vmc.core.request.order.OrderSyncRequest;
import com.vmc.core.request.pay.CardPayStatus;
import com.vmc.core.request.pay.PayRequest;
import com.vmc.core.request.pay.PayStatusRequest;
import com.vmc.core.request.replenishment.VmcSyncRequest;
import com.vmc.core.request.stock.StockSyncRequest;
import com.want.base.http.HttpResponse;
import com.want.base.http.error.HttpError;
import com.want.core.log.lg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import odoo.core.OdooCookieManage;
import odoo.core.http.InternalOdooHttpListener;
import vmc.core.log;


/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/29<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Odoo api implements
 * <br>
 */
public class Odoo extends odoo.core.Odoo implements IOdooApi, OdooAction, PATH {


    private static volatile Odoo INSTANCE;

    public Odoo(Context context) {
        super(context, context.getString(R.string.odoo_host));
    }

    /**
     * Return the single instance of Odoo
     *
     * @param context Android context
     *
     * @return {@link Odoo}
     */
    public static Odoo getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (Odoo.class) {
                if (null == INSTANCE) {
                    INSTANCE = new Odoo(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }


    /**
     * User authenticate.
     *
     * @param context  android context
     * @param uname    user name
     * @param pswd     user password
     * @param factoryCode     factory_code
     * @param callback {@link OdooHttpCallback}
     */
    public void authenticate(
            final Context context,
            final String uname,
            final String pswd,
            final String factoryCode,
            final OdooHttpCallback<UserInfo> callback) {
        if (OdooDebug.VERBOSE) {
            lg.v(OdooDebug.TAG, "request: authenticate. db=%s, uname=%s, password=%s", uname, pswd);
        }

        final JSONObject params = new JSONObject();
        JsonHelper.optput(params, "login", uname)
                  .optput("password", pswd)
                  .optput("factory_code", factoryCode);
        request(AUTHENTICATE,
                null,
                params,
                new InternalOdooHttpListener<UserInfo>(new InternalHttpCallback<UserInfo>(callback) {
                    @Override
                    public void onSuccess(UserInfo result) {
                        super.onSuccess(result);
                        LocalBroadcastManager.getInstance(context).sendBroadcast( new Intent(USER_STATE_CHANGED_LOGIN));
                    }
                }) {

                    @Override
                    public void onResponse(HttpResponse response) {
                        super.onResponse(response);
                        String headerName;
                        String headerValue;
                        for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                            headerName = entry.getKey();
                            headerValue = entry.getValue();

                            if (headerName.equalsIgnoreCase("Set-Cookie")) {
                                if (OdooDebug.VERBOSE) {
                                    lg.v(OdooDebug.TAG,
                                         "headers, key: " + headerName + ", value: " + headerValue);
                                }
                                setCookie(headerValue);
                                OdooCookieManage.setCookie(context, headerValue);
                                break;
                            }
                        }
                    }
                });
    }


    @Override
    public void init(InitRequest request, OdooHttpCallback<MachineInit> callback) {

        JSONObject params = newJSONParams();
        JsonHelper.optput(params, "factory_code", request.factory_code);

        request(URL, "vmc_machine_init", params,
                new InternalOdooHttpListener<MachineInit>(new InternalInitCallback(getContext(), callback)) {
                });
    }


    @Override
    public void adList(AdsListRequest request, OdooHttpCallback<AdList> callback) {
        request(URL, "vmc_ad_list", newJSONParams(), new InternalOdooHttpListener<AdList>(callback) {
        });
    }

    @Override
    public void orderSync(OrderSyncRequest request, OdooHttpCallback<OdooMessage> callback) {
        final Order order = request.order;
        final BLLStackProduct product = order.getProduct();
        JSONObject productParams = newJSONParams();

        JsonHelper.optput(productParams, "name", product.name)
                  .optput("id", product.product_id)
                  .optput("stack_no", product.origin_stack_no + "")
                  .optput("box_no", product.box_no + "");

        JSONObject params = newJSONParams();


        JsonHelper.optput(params, "id", order.id)
                  .optput("product", productParams)
                  .optput("payment_method", order.payment_method)
                  .optput("payment_status", order.payment_status)
                  .optput("amount", order.getAmount())
                  .optput("status", order.status)
                  .optput("promotion_id", order.promotion_id)
                  .optput("promotion_stack_no", order.promotion_stack_no)
                  .optput("promotion_box_no", order.promotion_box_no)
                  .optput("shipping_status", order.shipping_status)
                  .optput("promotion_shipping_status", order.promotion_shipping_status)
                  .optput("create_time", order.create_time)
                  .optput("error_code", order.error_code)
                  .optput("pick_good_code", order.pick_good_code)
                  .optput("promotion_error_code", order.promotion_error_code);

        if (!TextUtils.isEmpty(order.sub_product_stock)) {
            JsonHelper.optput(params, "sub_product_stock", order.sub_product_stock);

        }

        if (!TextUtils.isEmpty(order.sub_gift_stock)) {
            JsonHelper.optput(params, "sub_gift_stock", order.sub_gift_stock);
        }


        request(URL, "vmc_order_sync", params,
                new InternalOdooHttpListener<OdooMessage>(callback) {
                });
    }

    /**
     * 状态上报
     *
     * @param request
     * @param callback
     */

    @Override
    public void statusSync(StockSyncRequest request, OdooHttpCallback<OdooMessage> callback) {
        JSONArray array = new JSONArray();
        if (request != null && request.stocks != null) {
            for (Stock item : request.stocks) {
                JSONObject jso = new JSONObject();
                try {
                    jso.put("box_no", item.box_no);
                    jso.put("stack_no", item.stack_no);
                    jso.put("stock", item.stock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(jso);
            }
        }
        JSONObject params;
        params = newJSONParams();
        JsonHelper.optput(params, "stocks", array)
                  .optput("machine", request.machine.toJSONObject())
                  .optput("create_time", Utils.getTimeStamp());

        request(URL, "vmc_stock_sync", params, new InternalOdooHttpListener<OdooMessage>(callback) {
        });
    }





    /**
     * 提货码接口
     *
     * @param pickgoods_code
     * @param callback
     */
    @Override
    public void goodsPicked(String pickgoods_code,
                            OdooHttpCallback<PickUpProduct> callback) {

        final JSONObject params = new JSONObject();
        JsonHelper.optput(params, "pickgoods_code", pickgoods_code);
        request(URL, "get_pickgood_status", params, new InternalOdooHttpListener<PickUpProduct>(callback) {
                });
    }


    @Override
    public void resetVmcPickGoodCode(String pickgoods_code, OdooHttpCallback<RestVerify> callback) {

        final JSONObject params = new JSONObject();
        JsonHelper.optput(params, "pickgoods_code", pickgoods_code);
        request(URL,
                "reset_vmc_pickgoodcode",
                params,
                new InternalOdooHttpListener<RestVerify>(callback) {
                });


    }




    /**
     * 初始化参数
     *
     * @param configRequest
     * @param callback
     */
    @Override
    public void initConfig(ConfigRequest configRequest, OdooHttpCallback<ConfigInit> callback) {
        request(URL, "vmc_settings", newJSONParams(),
                new InternalOdooHttpListener<ConfigInit>(callback) {
                });
    }

    /**
     * 获取指令集合
     *
     * @param request
     * @param callback
     */
    @Override
    public void instructGather(InstructRequest request, OdooHttpCallback<InstructList> callback) {
        request(URL, "vmc_instructions_issued", newJSONParams(),
                new InternalOdooHttpListener<InstructList>(callback) {
                });
    }

    /**
     * 发送指令状态
     *
     * @param request
     * @param callback
     */
    @Override
    public void updateInstructStatus(InstructUpdateRequest request, OdooHttpCallback<OdooMessage> callback) {
        JSONObject jsonObject = newJSONParams();
        JsonHelper.optput(jsonObject, "status_list", request.status);
        request(URL, "vmc_instructions_issued_update", jsonObject,
                new InternalOdooHttpListener<OdooMessage>(callback) {
                });
    }

    @Override
    public void payRequest(PayRequest request, OdooHttpCallback<QRCodeResult> callback) {
        JSONObject params = newJSONParams();
        JsonHelper.optput(params, "payment_type", request.payment_type);
        JsonHelper.optput(params, "order_id", request.order_id);
        JsonHelper.optput(params, "total_amount", request.total_amount);
        JsonHelper.optput(params, "body", "SVM: " + getMachineId());
        request(URL, "vmc_payment",
                params,
                new InternalOdooHttpListener<QRCodeResult>(callback) {});
    }

    @Override
    public void payStatus(PayStatusRequest request, OdooHttpCallback<PayStatusResult> callback) {
        JSONObject jsonObject = newJSONParams();
        JsonHelper.optput(jsonObject, "order_id", request.order_id);
        request(URL, "vmc_query_order", jsonObject,
                new InternalOdooHttpListener<PayStatusResult>(callback) {

                });
    }

    private class InternalInitCallback extends OdooHttpCallback<MachineInit> {
        private OdooHttpCallback<MachineInit> mCallback;

        InternalInitCallback(Context context, OdooHttpCallback<MachineInit> callback) {
            super(context);
            this.mCallback = callback;


        }

        @Override
        public void onError(HttpError error) {
            super.onError(error);
            mCallback.onError(error);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mCallback.onFinish();
        }

        @Override
        public void onResponse(HttpResponse response) {
            super.onResponse(response);
            mCallback.onResponse(response);
        }

        @Override
        public void onSuccess(MachineInit result) {
            setMachineId(result.machine_id);
            mCallback.onSuccess(result);
        }
    }


    @Override
    public void stackProductList(OdooHttpCallback<OdooProductList> callback) {
        request(URL, "vmc_product_list", newJSONParams(),
                new InternalOdooHttpListener<OdooProductList>(callback) {
                });
    }


    @Override
    public void productStockList(OdooHttpCallback<OdooStockList> callback) {
        request(URL, "vmc_stock_list", newJSONParams(),
                new InternalOdooHttpListener<OdooStockList>(callback) {
                });

    }

    @Override
    public void promotionList(OdooHttpCallback<OdooPromotionList> callback) {
        request(URL, "vmc_promotion_list", newJSONParams(),
                new InternalOdooHttpListener<OdooPromotionList>(callback) {
                });
    }

    @Override
    public void requestCard(PayRequest request, OdooHttpCallback<CardPayStatus> callback) {
        JSONObject jsonObject = newJSONParams();
        JsonHelper.optput(jsonObject, "order_id", request.order_id);
        JsonHelper.optput(jsonObject, "total_amount", request.total_amount);
        JsonHelper.optput(jsonObject, "card_number", request.card_number);
        JsonHelper.optput(jsonObject, "payment_type", request.payment_type);
        request(URL, "vmc_watergod_payment", jsonObject,
                new InternalOdooHttpListener<CardPayStatus>(callback) {

                });
    }


}
