package vmc.vendor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;
import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.ads.Ads;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.AdsUtils;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.InitUtils;
import com.vmc.core.worker.ads.AdsDownloadWorker;
import com.vmc.core.worker.ads.AdsUpdaterWorker;
import com.vmc.core.worker.order.OrderSyncWorker;
import com.want.base.sdk.framework.app.MApplication;
import com.want.base.sdk.model.crash.BaseCrashhandler;
import com.want.base.sdk.utils.TimeUtils;
import com.want.core.log.lg;
import com.want.imageloader.ImageLoader;
import com.want.location.LocationManager;
import com.want.location.gd.GDLocationClient;
import com.want.pjt.BuildConfig;

import org.apache.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import odoo.core.OdooCookieManage;
import vmc.core.log;
import vmc.project.R;
import vmc.vendor.utils.IntentHelper;
import vmc.vendor.utils.NavigationBarUtils;
import vmc.vendor.utils.SerialPortUtils;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public abstract class VApplication extends MApplication {

    public static final String TAG = "VApplication";

    public Level mLevel;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        File root = base.getCacheDir();
        File file = new File(root, "log");
        if (!file.exists()) {
            file.mkdirs();
        }

        File file2 = new File(Environment.getExternalStorageDirectory(), "svm_log");
        if (!file2.exists()) {
            file2.mkdirs();
        }


        String log_level = this.getResources().getString(R.string.log_level);
        switch (log_level) {
            case "ALL":
                mLevel = Level.ALL;
                break;
            case "INFO":
                mLevel = Level.INFO;
                break;
            case "DEBUG":
                mLevel = Level.DEBUG;
                break;
            case "WARN":
                mLevel = Level.WARN;
                break;
            case "ERROR":
                mLevel = Level.ERROR;
                break;
        }

        final String times = TimeUtils.format("yyyy-MM-dd");

        SharedPreferences sp = base.getSharedPreferences("config", MODE_PRIVATE);

        String machineId = sp.getString("mechineId", "");


        if (TextUtils.isEmpty(machineId)) {
            machineId = InitUtils.getFactoryCode(this);
        }


        file = new File(file, times + "_" + machineId + ".txt");
        Log.i(TAG, "日志文件名称:" + file.getName());

        file2 = new File(file2, "log.txt");

        log.config(mLevel, file.getAbsolutePath(), file2.getAbsolutePath());
        log.i(TAG, "attachBaseContext: machineId=" + machineId);

    }


    @Override
    protected void onSetupCrashHandler(final Context context) {
        super.onSetupCrashHandler(context);


        Thread.setDefaultUncaughtExceptionHandler(new BaseCrashhandler(this) {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                super.uncaughtException(thread, ex);
            }

            @Override
            protected void showAlertDialog(Context context) {

            }

            @Override
            protected void handleUncaughtException(Thread thread,
                                                   Throwable ex,
                                                   boolean mainthread,
                                                   String formatedMessage) {

                log.e("crash", "crash message: " + formatedMessage);
                for (int i = 0; i < 300; i++) {
                    log.e("null", "null");
                }
                restartApp();
            }

            @Override
            public void reportError(Context context, Throwable e, String error) {
            }
        });
    }

    /**
     * 重启app
     */
    private void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    @Override
    protected void onSetupAnalytic(Context context) {
        super.onSetupAnalytic(context);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setCatchUncaughtExceptions(false);
    }

    @Override
    public void onCreate() {
        final boolean DEVELOPER_MODE = false;
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                               .detectDiskReads()
                                               .detectDiskWrites()
                                               .detectNetwork()   // or .detectAll() for all detectable problems
                                               .penaltyLog()
                                               .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                           .detectLeakedSqlLiteObjects()
                                           .detectLeakedClosableObjects()
                                           .penaltyLog()
                                           .penaltyDeath()
                                           .build());
        }

        // 配置日志工具
        lg.setAppTag(TAG);
        lg.setDebug(true);
        lg.RELEASE = false;
        log.v(TAG, "onCreate: ==========================system start==========================");
        super.onCreate();
        instance = this;
        // 初始化Odoo信息
        Odoo.getInstance(this).setMachineId(InitUtils.getInitMachineId(this));
        //qiangzeng160428 检查内存泄露
        if (BuildConfig.LEAKCANARY_ON) {
            LeakCanary.install(this);
        }
        //初始化fresco
        Fresco.initialize(this);
    }

    @Override
    protected void onInitApplication(Context context) {
        super.onInitApplication(context);
        // 配置默认的Imageloader
        ImageLoader.Builder defaultBuilder = new ImageLoader.Builder();
        defaultBuilder.error(R.drawable.default_image).cookie(OdooCookieManage.getCookie(this));
        ImageLoader.defaultBuilder(defaultBuilder);
    }

    protected void onInitMachine(Context context) {
        throw new RuntimeException("You must override this method in sub applicaiton");
    }

    @Override
    protected void onInitApplicationInMainProcess(Context context) {
        super.onInitApplicationInMainProcess(context);
        // 启动主线程守护
//        DaemonThread thread = new DaemonThread(this);
//        thread.start();

        //启动轮询work
        startLooperWork();

        // 初始化机器
        onInitMachine(context);

        //初始化商品列表
        BLLProductUtils.initProductFromSP(context);

        // 注册广播接收器
        initBroadcast(context);


        // 初始化位置模块
        LocationManager.init(new GDLocationClient(this));

        LocationManager.getInstance().requestLocationUpdate();


    }


    /**
     * 初始化广播接收器
     * @param context
     */
    void initBroadcast(Context context) {

        IntentFilter filter;

        filter = new IntentFilter(OdooAction.USER_STATE_CHANGED_LOGIN);
        registerLocalReceiver(new VApplication.UserStateChangedReceiver(), filter);


        filter = new IntentFilter(AdsUpdaterWorker.ACTION_ADS_UPDATE);
        registerReceiver(new VApplication.AdsUpdateReceiver(), filter);


        filter = new IntentFilter(OdooAction.BLL_SERIAL_ERROR_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(new VApplication.VmcErrorReceiver(), filter);



        filter = new IntentFilter(OdooAction.BLL_DOOR_STATE_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(new VApplication.DoorStateReceiver(), filter);


        filter = new IntentFilter(OdooAction.BLL_GOODS_SELECTED_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(new VApplication.SelectGoodsReceiver(), filter);


        filter = new IntentFilter(OdooAction.BLL_ORDERSYNC_TO_BLL);
        LocalBroadcastManager.getInstance(context).registerReceiver(new VApplication.OrderSyncReceiver(), filter);


        filter = new IntentFilter(OdooAction.BLL_OUTGOODS_TIMEOUT_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcOutGoodsTimeReceiver, filter);




        filter = new IntentFilter(OdooAction.BLL_UPGRADE_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mUpgradeReceiver,filter);

    }


    public abstract void startLooperWork();



    private class UserStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            log.v(TAG, "UserStateChangedReceiver-->onReceive: 用户信息改变");

            if (OdooAction.USER_STATE_CHANGED_LOGIN.equals(action)) {
                final ImageLoader.Builder builder = ImageLoader.getDefaultBuilder();
                builder.cookie(OdooCookieManage.getCookie(context));
                ImageLoader.defaultBuilder(builder);
            }
        }
    }


    private class AdsUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            log.v(TAG, "AdsUpdateReceiver-->onReceive: 收到广告列表更新的广播");
            final AdList list = AdsUtils.getAdList(context);
            if (null != list) {
                List<String> urls = new ArrayList<>();
                for (Ads ad : list.records) {
                    if (Ads.AdType.VIDEO.getAdType().equals(ad.ad_type)) {
                        urls.add(ad.ad_url);
                        log.v(TAG, "ad.ad_url: " + ad.ad_url);


                    }
                }
                if (!urls.isEmpty()) {
                    AdsDownloadWorker.getInstance(context).download(urls.toArray(new String[urls.size()]));
                }
            }
        }
    }


    /**
     * 选货广播
     */
    private class SelectGoodsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            log.v(TAG, "SelectGoodsReceiver-->onReceive: 收到选货通知的广播");
            BLLStackProduct product = intent.getParcelableExtra("product");
            log.v(TAG, "选择商品货道：" + "," + product);
            IntentHelper.startProductInfo(context, product);
        }
    }


    /**
     * 接受串口异常发送的广播，并进行响应的处理。
     */
    private class VmcErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (OdooAction.BLL_SERIAL_ERROR_TO_UI.equals(action)) {
                intent.setAction("vmc.vendor.ACTION_SERIPORT_ERROR");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                log.v(TAG, "VmcErrorReceiver-->onReceive：接受到串口异常广播");
            }
        }
    }


    /**
     * 开关门广播
     */
    private class DoorStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            log.v(TAG, "DoorStateReceiver-->onReceive: 收到门状态广播");
            boolean doorState = intent.getBooleanExtra("doorState", false);
            if (doorState) {
                SharedPreferences sp = VApplication.this.getSharedPreferences("user", MODE_PRIVATE);
                String username = sp.getString("name", "");
                String password = sp.getString("password", "");
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    launchApp();
                }
                NavigationBarUtils.show();
            } else {
                NavigationBarUtils.hide();
            }
        }
    }


    /**
     * 触发上传订单上传广播
     */
    private class OrderSyncReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrderSyncWorker.getInstance(context).notifySync();
        }
    }


    /**
     * 启动App
     */
    private void launchApp() {
        String packageName = getManagementPackageName(this);
        if (TextUtils.isEmpty(packageName)) {
            Toast.makeText(getApplicationContext(), "未安装补货管理软件", Toast.LENGTH_SHORT).show();
            return;
        }
        PackageManager mPackageManager = this.getPackageManager();
        Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
        if (null == intent) {
            return;
        }
        startActivity(intent);
    }


    /**
     * 打开的activity
     **/
    private List<Activity> activities = new ArrayList<>();
    /**
     * 应用实例
     **/
    private static VApplication instance;

    /**
     * 获得实例
     *
     * @return
     */
    public static VApplication getInstance() {
        return instance;
    }

    /**
     * 新建了一个activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 应用退出，结束所有的activity
     */
    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * @param context
     *
     * @return
     */
    public String getManagementPackageName(Context context) {
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
                        return packageInfo.packageName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 出货超时页面跳转
     */
    public BroadcastReceiver mVmcOutGoodsTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:mVmcOutGoodsTimeReceiver --begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_OUTGOODS_TIMEOUT_TO_UI)) {
                int productId = intent.getIntExtra("product_id", 0);
                boolean isPromotion = intent.getBooleanExtra("isPromotion", false);
                String payType = intent.getStringExtra("payMentType");
                // 0:出货成功  1:出货失败 2:赠品出货失败  3: 提货码出货失败  4:出货超时  5:赠品出货超时  6:提货码出货超时
                boolean isRefund = intent.getBooleanExtra("isRefund", false);
                String order = intent.getStringExtra("currentOrderId");
                if (payType.equals("CODE")) {
                    IntentHelper.startProductResult(context, order, "3", payType, productId, isRefund);
                } else {
                    if (isPromotion) {
                        IntentHelper.startProductResult(context, order, "2", payType, productId, isRefund);
                    } else {
                        IntentHelper.startProductResult(context, order, "1", payType, productId, isRefund);
                    }
                }
            }
            log.d(TAG, "onReceive:mVmcOutGoodsTimeReceiver --end");
        }
    };


    /**
     * 后台升级提醒
     */
    public BroadcastReceiver mUpgradeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(OdooAction.BLL_UPGRADE_TO_UI)){
              IntentHelper.startUpgrade(context);
            }
        }
    };
}
