package com.vmc.core.http;

import com.vmc.core.model.OdooError;
import com.want.base.http.error.HttpError;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 16/1/4<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Odoo internal error.
 * <br>
 */
public class HttpOdooError extends HttpError {

    private OdooError mOdooError;

    public HttpOdooError(OdooError error) {
        super(error.message);
        this.mOdooError = error;
    }

    public OdooError getOdooError() {
        return this.mOdooError;
    }

    public int getCode() {
        return mOdooError.code;
    }

    public String getMessage() {
        return mOdooError.message;
    }

    @Override
    public String toString() {
        return "HttpOdooError{" +
               "code=" + mOdooError.code +
               ", message=" + mOdooError.message +
               '}';
    }
}
