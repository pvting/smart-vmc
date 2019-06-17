package vmc.vendor.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wisn on 2017/5/18.
 * 用于和其他app之间共享数据的广播
 */

public class ShareDataReceiver extends BroadcastReceiver {
    private static final String Supply_Stock = "com.want.vmc.Supply_Stock";
    private static final String TAG = "ShareDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String returnAction = intent.getStringExtra("action");
        if (Supply_Stock.equals(action)) {
            //todo 处理获取库存的逻辑,不能处理耗时操作
            //todo 返回处理结果
            returnData(context, returnAction, "结果是xxxxxxxxxx");
        }
    }

    private void returnData(Context context, String action, String data) {
        Intent intent = new Intent(action);
        intent.putExtra("data", data);
        context.sendBroadcast(intent);
    }
}
