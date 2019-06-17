package vmc.vendor.service.boot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.want.base.sdk.utils.TimeUtils;
import com.want.vmc.core.Constants;

import java.util.Calendar;

import vmc.core.log;

/**
 * <b>Create Date:</b> 9/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class RebootUtils {
    private static final String TAG = "Reboot";

    private RebootUtils() {
        //no instance
    }

    /**
     * 配置系统重启时间
     *
     * @param context
     */
    public static void setRebootTime(Context context) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        final String time = TimeUtils.format("yyyy-MM-dd HH:mm:ss", calendar.getTimeInMillis());
        log.d(TAG, "setRebootTime: 重启时间: " + time);

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(Constants.Action.REBOOT);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


}
