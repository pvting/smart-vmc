package com.vmc.yichu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.want.vmc.core.Constants;

/**
 * <b>Create Date:</b>2017/6/20 10:27<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class SDKUploadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Constants.Action.SDK_ZIP_UPLOAD_LOG.equals(action)) {
            SDKLogUploadUtils.getInstance().zipAndUploadSDKlog(context);
        }

        if (Constants.Action.SDK_UPLOAD_LOG.equals(action)) {
            SDKLogUploadUtils.getInstance().uploadSDKlog(context);
        }

    }
}