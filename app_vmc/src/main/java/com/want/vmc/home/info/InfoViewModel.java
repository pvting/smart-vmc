package com.want.vmc.home.info;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.Bindable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.TimeUtils;
import com.want.vmc.BR;
import com.want.vmc.R;
import com.want.vmc.core.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vmc.core.log;
import vmc.machine.core.VMCContoller;


/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class InfoViewModel extends AbsViewModel {

    private static final String TAG = "InfoViewModel";

    private int count = 0;
    private static final long MAX_CLICK_TIME = 5000L;
    private static final int CLICK_TIMES = 5;
    private long firstClickTime = 0;

    protected Context mContext;

    public InfoViewModel() {

    }

    public InfoViewModel(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 获取当前时间， 如：10:24
     *
     * @return
     */
    @Bindable
    public String getTime() {
        return TimeUtils.format(getString(R.string.home_time_format), System.currentTimeMillis());
    }


    /**
     * 获取客服电话
     *
     * @return
     */
    @Bindable
    public String getConfigNum() {

        if (!TextUtils.isEmpty(ConfigUtils.getConfig(mContext).customer_phone)) {
            log.d(TAG, "客服电话:" + ConfigUtils.getConfig(mContext).customer_phone);
            return "客服电话：" + ConfigUtils.getConfig(mContext).customer_phone;
        } else {
            log.d(TAG, "服务电话获取失败");
            return "";
        }
    }


    /**
     * 获取机器出厂编码
     *
     * @return
     */
    @Bindable
    public String getFactoryCode() {
        final String michineID = VMCContoller.getInstance().getVendingMachineId();
        if (michineID != null && !TextUtils.isEmpty(michineID)) {
            log.d(TAG, "机器码:" + michineID);
            return getString(R.string.home_machine_id) + michineID;
        } else {
            return getString(R.string.home_machine_id);
        }
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    @Bindable
    public String getDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA);

        return getWeekStr() + "  " + sdf.format(new Date());
    }

    public void onResume() {
        notifyPropertyChanged(BR.configNum);
        notifyPropertyChanged(BR.date);
        notifyPropertyChanged(BR.time);
    }

    /**
     * 连续在一定时间内点击图片5下跳转补货app
     *
     * @param view
     */
    public void btnGotoManagement(View view) {
        count += 1;
        if (count == 1) {
            firstClickTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - firstClickTime < MAX_CLICK_TIME) {
            if (count == CLICK_TIMES) {
                count = 0;
                firstClickTime = 0;
                String packageName = getManagementPackageName(mContext);
                if (TextUtils.isEmpty(packageName)) {
                    Toast.makeText(mContext.getApplicationContext(), "未安装补货管理软件", Toast.LENGTH_SHORT).show();
                    return;
                }
                PackageManager mPackageManager = mContext.getPackageManager();
                Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
                if (null == intent) {
                    return;
                }
                mContext.startActivity(intent);
            }
        } else {
            count = 0;
            firstClickTime = 0;
        }


    }

    /**
     * @param context
     *
     * @return
     */

    public String getManagementPackageName(Context context) {
        String name = null;
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }
                if (packageInfo.packageName != null) {
                    String packageName = packageInfo.packageName.toLowerCase();
                    if (packageName.contains("com.want.management")) {
                        name = packageInfo.packageName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }


    /**
     * 显示星期
     *
     * @return
     */
    public String getWeekStr() {
        Calendar c = Calendar.getInstance();
        String str = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(str)) {
            str = getString(R.string.home_week_7);
        } else if ("2".equals(str)) {
            str = getString(R.string.home_week_1);
        } else if ("3".equals(str)) {
            str = getString(R.string.home_week_2);
        } else if ("4".equals(str)) {
            str = getString(R.string.home_week_3);
        } else if ("5".equals(str)) {
            str = getString(R.string.home_week_4);
        } else if ("6".equals(str)) {
            str = getString(R.string.home_week_5);
        } else if ("7".equals(str)) {
            str = getString(R.string.home_week_6);
        }
        return str;
    }

}
