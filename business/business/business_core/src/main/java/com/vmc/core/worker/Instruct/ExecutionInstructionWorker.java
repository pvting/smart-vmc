package com.vmc.core.worker.Instruct;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.instruct.Instruct;
import com.vmc.core.model.instruct.InstructStatus;
import com.vmc.core.request.instruct.InstructUpdateRequest;
import com.vmc.core.utils.InstructUtils;
import com.vmc.core.worker.Worker;
import com.want.base.http.error.HttpError;
import com.want.base.sdk.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import vmc.core.log;

import static com.want.vmc.core.Constants.Time.MINUTE_10;

/**
 * <b>Create Date:</b> 06/12/2016<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b>
 * 执行指令
 * <br>
 */
public class ExecutionInstructionWorker extends Worker {
    private  final String TAG = "ExInstructionWorker";
    private static ExecutionInstructionWorker INSTANCE;
    private WeakReference<Context> mContextWeakReference;

    private ExecutionInstructionWorker(Context context) {
        super();
        this.mContextWeakReference = new WeakReference<Context>(context);
    }

    public static ExecutionInstructionWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (ExecutionInstructionWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ExecutionInstructionWorker(context);
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
            log.v(TAG,"ExecutionInstructionWorker: context is null ,application has been killed ");
            stopWork();
            return;
        }
    }

    @Override
    protected void onWorking() {
        // 10S从缓存获取一次
        safeWait(10 * Time.SECOND_1);
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            log.v(TAG,"ExecutionInstructionWorker: context is null ,application has been killed ");
            return;
        }
        executorInstruct(context);
    }

    /**
     * 执行指令
     */
    public void executorInstruct(Context context) {
        HashSet<String> set = (HashSet<String>) InstructUtils.getInstruct(context);
        if (set.size() <= 0) {//未检索到指令
            Log.v(TAG, "executorInstruct: 未检索到指令");
            return;
        } else {
            for (String s : set) {
                Instruct ins = JsonUtils.fromJson(s, Instruct.class);
                long currentTime = System.currentTimeMillis();
                long runTime = getMillisecond(ins.run_time);
                if (currentTime >= runTime) {
                    //执行相应的指令
                    option(context, ins);
                    break;
                }
            }
        }
    }

    /**
     * 执行相应指令
     *
     * @param ins
     */
    public void option(final Context context, Instruct ins) {
        Log.v(TAG, "option: 执行指令: 指令编号="+ins.id);
        updateStatus(context, ins.id);
        InstructUtils.removeInstruct(context, ins);
        switch (ins.industrial_type) {
            case "1":
                log.v(TAG,"option: 执行指令:工控机重启");

                break;
            case "2":
                log.v(TAG,"option: 执行指令:app重启");
//                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(intent);

                break;
            case "3":

                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        Log.v(TAG, "结束执行指令任务");
    }

    /**
     * 通知服务器已拉取到指令 更新服务器状态
     */
    public void updateStatus(final Context context, final String id) {
        log.v(TAG, "updateStatus: 更新指令状态中...");
        InstructUpdateRequest request = new InstructUpdateRequest();
        InstructStatus status = new InstructStatus();
        status.id = id;
        status.status = "finished";
        try {
            request.status.put(new JSONObject(JsonUtils.toJson(status)));
        } catch (JSONException e) {
            e.printStackTrace();
            log.v(TAG, "updateStatus: 指令上报失败,JSON异常");
            return;
        }
        Odoo.getInstance(context).updateInstructStatus(request, new OdooHttpCallback<OdooMessage>(context) {
            @Override
            public void onSuccess(OdooMessage result) {
                log.v(TAG, "updateInstructStatus-->onSuccess: 更新指令状态成功");
            }
            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.v(TAG, "updateInstructStatus-->onError: 更新指令状态失败,10分钟后继续通知");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus(context, id);//失败，继续通知
                    }
                },MINUTE_10);
            }
        });
    }

    /**
     * 转换为毫秒数
     *
     * @param time
     * @return
     */
    public static long getMillisecond(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date_util = null; //转换为util.date
        try {
            date_util = sdf.parse(time);
            return date_util.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}