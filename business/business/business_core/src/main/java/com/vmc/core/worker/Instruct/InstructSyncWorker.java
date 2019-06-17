package com.vmc.core.worker.Instruct;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.instruct.Instruct;
import com.vmc.core.model.instruct.InstructList;
import com.vmc.core.model.instruct.InstructStatus;
import com.vmc.core.request.instruct.InstructRequest;
import com.vmc.core.request.instruct.InstructUpdateRequest;
import com.vmc.core.utils.InstructUtils;
import com.vmc.core.worker.Worker;
import com.want.base.http.error.HttpError;
import com.want.base.sdk.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import static com.want.vmc.core.Constants.Time.MINUTE_10;

/**
 * <b>Create Date:</b> 06/12/2016<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b>
 * 拉取指令
 * <br>
 */
public class InstructSyncWorker extends Worker {
    private static final String TAG = "InstructSyncWorker";
    private static InstructSyncWorker INSTANCE;
    private WeakReference<Context> mContextWeakReference;

    private InstructSyncWorker(Context context) {
        super();
        this.mContextWeakReference = new WeakReference<Context>(context);
    }

    public static InstructSyncWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (InstructSyncWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new InstructSyncWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            return;
        }
    }

    @Override
    protected void onWorking() {
        final boolean DISABLE = true;
        if(DISABLE){
            return;
        }
        
        // 5分钟从服务器上拉取一次指令
        safeWait(Time.WORK_INTERVAL);
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            return;
        }
        Log.v(TAG, "onWorking: 接收指令中...");
        Odoo.getInstance(context).instructGather(new InstructRequest(), new OdooHttpCallback<InstructList>(context) {
            @Override
            public void onSuccess(InstructList result) {
                Log.v(TAG, result.toString());
                if (result.records != null && result.records.size() > 0) {
                    Log.v(TAG, "instructGather-->onSuccess,接收指令成功");
                    updateStatus(context, result);
                    InstructUtils.setInstruct(context, result);
                } else {
                    Log.v(TAG, "instructGather-->onSuccess,接收指令成功,但无有效指令");
                }
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                Log.v(TAG, "instructGather-->onError,接收指令失败");
            }
        });
    }

    /**
     * 通知服务器已拉取到指令 更新服务器状态
     */
    public void updateStatus(final Context context, final InstructList list) {
        Log.v(TAG, "updateStatus: 更新指令状态中...");
        InstructUpdateRequest request = new InstructUpdateRequest();
        for (Instruct item : list.records) {
            InstructStatus status = new InstructStatus();
            status.id = item.id;
            status.status ="get_successful";
            try {
                request.status.put(new JSONObject(JsonUtils.toJson(status)));
            } catch (JSONException e) {e.printStackTrace();

            }
        }
        Odoo.getInstance(context).updateInstructStatus(request, new OdooHttpCallback<OdooMessage>(context) {
            @Override
            public void onSuccess(OdooMessage result) {
                Log.v(TAG, "updateInstructStatus-->onSuccess,更新指令状态成功");
            }
            @Override
            public void onError(HttpError error) {
                super.onError(error);
                Log.v(TAG, "updateInstructStatus-->onError,更新指令状态失败");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus(context, list);//失败，继续通知
                    }
                },MINUTE_10);
            }
        });
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }
}