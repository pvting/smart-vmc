package com.vmc.core;

import com.want.base.http.HttpResponse;
import com.want.base.http.error.HttpError;

/**
 * <b>Project:</b> hollywant<br>
 * <b>Create Date:</b> 16/2/22<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
/*package */abstract class InternalHttpCallback<T> extends OdooHttpCallback<T> {

    private OdooHttpCallback<T> mOdooHttpCallback;

    InternalHttpCallback(OdooHttpCallback<T> callback) {
        super(callback.getContext());

        this.mOdooHttpCallback = callback;
    }

    /**
     * 网络交互过程中发生错误
     *
     * @param error {@link HttpError}
     */
    @Override
    public void onError(HttpError error) {
        mOdooHttpCallback.onError(error);
    }

    /**
     * 网络交互结束
     */
    @Override
    public void onFinish() {
        mOdooHttpCallback.onFinish();
    }

    /**
     * 服务器响应请求
     *
     * @param response {@link HttpResponse}
     */
    @Override
    public void onResponse(HttpResponse response) {
        mOdooHttpCallback.onResponse(response);
    }

    @Override
    public void onSuccess(T result) {
        mOdooHttpCallback.onSuccess(result);
    }
}
