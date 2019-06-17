package com.vmc.core;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.http.HttpCode;
import com.vmc.core.http.HttpOdooError;
import com.want.base.http.HttpResponse;
import com.want.base.http.error.HttpAuthFailureError;
import com.want.base.http.error.HttpError;
import com.want.base.http.error.HttpNetworkError;
import com.want.base.http.error.HttpNoConnectionError;
import com.want.base.http.error.HttpServerError;
import com.want.base.http.error.HttpTimeOutError;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Base Odoo Http Callback.
 * <br>
 */
public abstract class OdooHttpCallback<T> extends odoo.core.OdooHttpCallback<T> implements OdooAction {

    private Context mContext;

    public OdooHttpCallback(Context context) {
        super(context);
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * 服务器响应请求
     *
     * @param response {@link HttpResponse}
     */
    @Override
    public void onResponse(HttpResponse response) {

    }

    /**
     * 网络交互过程中发生错误
     *
     * @param error {@link HttpError}
     */
    @Override
    public void onError(HttpError error) {
        String message = "";
        if (error instanceof HttpAuthFailureError) {
            message = mContext.getString(R.string.odoo_http_error_auth);
        } else if (error instanceof HttpNoConnectionError) {
            message = mContext.getString(R.string.odoo_http_error_noconn);
        } else if (error instanceof HttpNetworkError) {
            message = mContext.getString(R.string.odoo_http_error_network);
        } else if (error instanceof HttpServerError) {
            message = mContext.getString(R.string.odoo_http_error_server);
        } else if (error instanceof HttpTimeOutError) {
            message = mContext.getString(R.string.odoo_http_error_timeout);
        } else if (error instanceof HttpOdooError) {
            if (OdooDebug.DEBUG) {
                message = "code=" + ((HttpOdooError) error).getCode();
                message += ", meesage=" + ((HttpOdooError) error).getOdooError().message;
            } else {
                message = ((HttpOdooError) error).getOdooError().message;
            }

            if (((HttpOdooError) error).getCode() == HttpCode.USER_EXPIRED) {
                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(new Intent(OdooAction.USER_EXPIRED));
            }
        }

//        if (!TextUtils.isEmpty(message)) {
//            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * 网络交互结束
     */
    @Override
    public void onFinish() {

    }
}
