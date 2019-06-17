package com.want.vmc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.model.init.MachineInit;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.OdooPromotionList;
import com.vmc.core.model.product.OdooStockList;
import com.vmc.core.model.user.UserInfo;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.request.init.InitRequest;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.ConfigUtils;
import com.vmc.core.utils.InitUtils;
import com.want.base.http.error.HttpError;
import com.want.base.sdk.framework.app.core.UIRunable;
import com.want.base.sdk.utils.PhoneUtils;

import org.w3c.dom.Text;

import java.util.Locale;

import vmc.core.log;
import vmc.machine.core.VMCContoller;
import vmc.vendor.VActivity;
import vmc.vendor.service.boot.RebootUtils;
import vmc.vendor.utils.DeviceInfo;
import vmc.vendor.utils.IntentHelper;
import vmc.vendor.utils.NavigationBarUtils;
import vmc.vendor.utils.SerialPortUtils;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 入口页面
 * <br>
 */
public class MainActivity extends VActivity {
    private static final String TAG = "MainActivity";
    private final static int REQUESTCODE = 1001; // 返回的结果码
    private boolean hasInited = true;
    private TextView mConsoleText;
    private SetProductReceiver receiver;

    private MachineInitReceiver mMachineInitReceiver;

    private boolean initTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        NavigationBarUtils.hide();
        mConsoleText = (TextView) findViewById(R.id.console_text);

        //打印设备信息
        printDeviceInfo();

        appedText("");
        appedText("");
        appedText("");

        appedText("服务器地址：" + getResources().getString(R.string.odoo_host));

        log.d(TAG, "onCreate: 配置机器自动重启时间");
        RebootUtils.setRebootTime(getApplicationContext());


        log.d(TAG, "onCreate: 初始化串口状态 ");
        SerialPortUtils.setSerialPortInit(this);

        //注册广播
        registBroadCast();

        if (initTag) {
            return;
        }

        if (BLLController.getInstance().getInitState() == 1) {
            initTag = true;
            startWork();

        } else if (BLLController.getInstance().getInitState() == 2) {
            initTag = true;
            finishInit();
        }

    }

  private void  registBroadCast(){

        //注册料道设置成功广播
        receiver = new SetProductReceiver();
        IntentFilter intentFilter = new IntentFilter(OdooAction.BLL_SETPRODUCT_TO_UI);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);


        //注册机器初始化广播
        mMachineInitReceiver = new MachineInitReceiver();
        IntentFilter intentFilter2 = new IntentFilter(OdooAction.BLL_INIT_STATE_TO_UI);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMachineInitReceiver, intentFilter2);

    }

    /**
     * 售货机初始化广播
     */
    private class MachineInitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (initTag) {
                return;
            }
            initTag = true;
            if (BLLController.getInstance().getInitState() == 1) {
                startWork();
            } else if (BLLController.getInstance().getInitState() == 2) {
                finishInit();
            }
        }
    }


    private void startWork() {
        log.d(TAG, "onCreate: 开始登录");
        autoLogin();
        String URL=ConfigUtils.getConfig(this).img_url;
        if(URL!=null) {
            Glide.with(this).load(URL).downloadOnly(0, 0);
        }
        }

    private void appedText(String text) {
        mConsoleText.append(text + "\n");
    }


    private void printDeviceInfo() {
        final String[] cpuInfo = PhoneUtils.getCpuInfo();
        DeviceInfo info = new DeviceInfo(this);
        appedText(String.format(Locale.getDefault(), "cpu: %s, %s", cpuInfo[0], cpuInfo[1]));
        final String[] memInfo = PhoneUtils.getMemoryInfo(this);
        appedText(String.format(Locale.getDefault(), "mem: %s, %s ", memInfo[0], memInfo[1]));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        appedText(String.format(Locale.getDefault(),
                                "width: %s, height: %s",
                                dm.widthPixels,
                                dm.heightPixels));

        appedText(String.format(Locale.getDefault(), "densityDpi: %s", dm.densityDpi));
        appedText(String.format(Locale.getDefault(), "density: %s", dm.density));
        appedText(String.format(Locale.getDefault(), "scaledDensity: %s", dm.scaledDensity));
        appedText(String.format(Locale.getDefault(), "xdpi: %s, ydpi: %s", dm.xdpi, dm.ydpi));
        appedText(String.format(Locale.getDefault(),
                                "理论英寸: %.2f",
                                Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2)) /
                                dm.densityDpi));
        appedText(String.format(Locale.getDefault(),
                                "当前版本号: %s, 当前版本名称: %s",
                                info.getVersionCode(),
                                info.getVersionName()));
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMachineInitReceiver);
        super.onDestroy();
    }

    private void autoLogin() {
        //获取用户名密码
        SharedPreferences sp = this.getSharedPreferences("user", MODE_PRIVATE);
        String username = sp.getString("name", "");
        String password = sp.getString("password", "");

        boolean isUserNameEmpty = TextUtils.isEmpty(username);

        boolean isPassWordeEmpty = TextUtils.isEmpty(password);


        //获取机器编号
        String factory_code=VMCContoller.getInstance().getVendingMachineId();

        if (isUserNameEmpty){
            appedText("登录Odoo用户为第一次登录");
        }else {
            appedText("登录Odoo用户. userName: " + username);
        }


        if (!BLLController.getInstance().isNetState(this)) {//如果没网
            if (isUserNameEmpty) {//如果第一次没有网络，也要进行登录
                log.w(TAG, "onNetError: 第一次登录没网络，必须登录");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUESTCODE);
            } else {
                log.w(TAG, "onNetError: 网络连接失败,用户登录失败!");
                appedText("网络连接失败,Odoo用户登录失败.");
                BLLProductUtils.initProductFromSP(this);
                postDelay(new UIRunable(MainActivity.this) {
                    @Override
                    protected void runOnUiThread(Context context) {
                        finishInit();//结束初始化
                    }
                }, 5000);
            }
        }

            if (!isUserNameEmpty&& !isPassWordeEmpty) {//登录过,自动登录
                final Odoo odoo = Odoo.getInstance(this);
                odoo.authenticate(this, username, password, factory_code, new OdooHttpCallback<UserInfo>(this) {
                                      @Override
                                      public void onSuccess(UserInfo result) {
                                          log.d(TAG, "onSuccess: 用户登录成功!");
                                          appedText("Odoo用户已登录.");
                                          userLogined(odoo);
                                      }

                                      @Override
                                      public void onError(HttpError error) {
                                          super.onError(error);
                                          log.w(TAG, "onError: 用户登录失败!");
                                          appedText("Odoo用户登录失败.");
                                          BLLProductUtils.initProductFromSP(MainActivity.this);
                                          postDelay(new UIRunable(MainActivity.this) {
                                              @Override
                                              protected void runOnUiThread(Context context) {
                                                  finishInit();
                                              }
                                          }, 5000);
                                      }
                                  });
            } else {//手动登录
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUESTCODE);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1100) {
            if (requestCode == REQUESTCODE)
                autoLogin();
        }
    }

    /**
     * 用户已经登录后
     * @param odoo
     */
    private void userLogined(final Odoo odoo) {
        /** 判断是否初始化过*/
        if (!InitUtils.isInit(this)) {//如果没有初始化过
            log.d(TAG, "onCreate: 初始化进行中...");

            appedText("初始化进行中...");

            hasInited = false;

            init(odoo);

        } else {//已经初始化过

            log.d(TAG, "onCreate: 已经初始化过了。");

            appedText("已经初始化过了.");

            log.d(TAG, "onCreate: 从本地sp文件初始化商品。");

            BLLProductUtils.initProductFromSP(this);

            log.d(TAG, "onCreate: 网络请求配置参数。");
            initConfig(odoo, false);
        }

    }


    public void updatePromotion(Odoo odoo) {
        odoo.promotionList(new OdooHttpCallback<OdooPromotionList>(this) {
            @Override
            public void onSuccess(OdooPromotionList result) {
                BLLProductUtils.updatePromotionList(result, MainActivity.this);
                log.v(TAG, "updatePromotion-->onSuccess: 同步后台促销成功");
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.w(TAG,  "updatePromotion-->onError: 同步后台促销失败");
            }

        });
    }


    private void init(final Odoo odoo) {
        log.d(TAG, "init: 机器初始化中...");

        appedText("初始化机器.");

        final String factoryCode = VMCContoller.getInstance().getVendingMachineId();

        //保存机器号
        InitUtils.setFactoryCode(this, factoryCode);

        if (TextUtils.isEmpty(factoryCode) || "null".equals(factoryCode)) {
            appedText("获取机器编码失败，稍后将重新获取。");
            postDelay(new UIRunable(MainActivity.this) {
                @Override
                protected void runOnUiThread(Context context) {
                    init(odoo);
                }
            }, 5000);
            return;
        }

        appedText("机器唯一编码: " + factoryCode);

        log.d(TAG, "init: 机器唯一编码: " + factoryCode);

        InitRequest request = new InitRequest();

        request.factory_code = factoryCode;

        odoo.init(request, new OdooHttpCallback<MachineInit>(this) {
            @Override
            public void onSuccess(MachineInit result) {
                log.i(TAG, "onSuccess: 机器初始化成功,"+ factoryCode+"对应机器ID为:"+result.machine_id);
                appedText("机器初始化成功.");
                InitUtils.setInitMachineId(MainActivity.this, result.machine_id);
                InitUtils.setInit(MainActivity.this);
                initConfig(odoo, true);

            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.e(TAG, "onError: 机器初始化失败, 5S后重新初始化!!!");
                appedText("机器初始化失败, 稍后重新初始化");
                postDelay(new UIRunable(MainActivity.this) {
                    @Override
                    protected void runOnUiThread(Context context) {
                        init(odoo);
                    }
                }, 5000);
            }
        });
    }

    /**
     * 获取配置参数
     *
     * @param odoo
     */
    private void initConfig(final Odoo odoo, final boolean init) {

        log.d(TAG, "initconfig: 获取配置参数...");

        appedText("获取配置参数中...");

        odoo.initConfig(new ConfigRequest(), new OdooHttpCallback<ConfigInit>(this) {
            @Override
            public void onSuccess(ConfigInit result) {
                log.d(TAG, "onSuccess: 获取配置参数成功.");
                appedText("获取配置参数成功.");
                ConfigUtils.setConfig(MainActivity.this, result);
                initProductList(odoo, init);
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.d(TAG, "onError: 获取配置参数失败...");
                appedText("获取配置参数失败.");
                postDelay(new UIRunable(MainActivity.this) {
                    @Override
                    protected void runOnUiThread(Context context) {
                        initConfig(odoo, init);
                    }
                }, 5000);
            }
        });
    }

    private void initProductList(final Odoo odoo, final boolean initMachine) {
        log.d(TAG, "initProductList: 商品列表初始化中...");
        appedText("拉取商品列表.");
        odoo.stackProductList(new OdooHttpCallback<OdooProductList>(this) {
            @Override
            public void onSuccess(OdooProductList result) {
                log.d(TAG, "initProductList: 商品列表初始化成功");
                appedText("商品列表拉取成功.");

                if (initMachine) {//如果是第一次初始化
                    BLLProductUtils.initProduct(result, MainActivity.this);
                    initStackProductStock(odoo);
                    updatePromotion(odoo);
                } else {//否者直接更新商品列表
                    BLLController.getInstance().updateStackProduct(result, MainActivity.this);
                    updatePromotion(odoo);
                    appedText("不需要重新配置货道.");
                    hasInited = true;
                    finishInit();
                    return;

                }
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.w(TAG, "onError: 商品列表初始化失败");
                appedText("商品列表拉取失败. 稍后将重新拉取");
                // 只有需要初始化货道的时候才去重新获取商品列表
                if (initMachine) {
                    postDelay(new UIRunable(MainActivity.this) {
                        @Override
                        protected void runOnUiThread(Context context) {
                            initProductList(odoo, initMachine);
                        }
                    }, 5000);
                } else {
                    finishInit();
                }
            }
        });
    }

    /**
     * 更新库存
     * 第一次装机初始化更新库存，必然都是0，必须补货。以后就直接拉取库存初始化数据
     *
     * @param odoo
     */
    public void initStackProductStock(final Odoo odoo) {
        log.d(TAG, "initProductStockList: 商品库存列表初始化中...");
        appedText("商品库存列表初始化中.");
        odoo.productStockList(new OdooHttpCallback<OdooStockList>(this) {
            @Override
            public void onSuccess(OdooStockList result) {
                log.w(TAG, "initProductStockList: 商品库存列表初拉取成功");
                appedText("商品库存列表拉取成功.");
                BLLProductUtils.updateStackProductStock(result, MainActivity.this);//更新库存
                appedText("开始配置货道.");
                BLLController.getInstance().setProducts(BLLProductUtils.makeVmcProductList());
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.w(TAG, "initProductStockList: 商品库存列表拉取失败");
                postDelay(new UIRunable(MainActivity.this) {
                    @Override
                    protected void runOnUiThread(Context context) {
                        initStackProductStock(odoo);
                    }
                }, 5000);
            }
            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }



    private void finishInit() {
        if (!hasInited) {
            return;
        }
        log.d(TAG, "finishInit: 初始化工作结束");
        appedText("初始化完成.");
        postDelay(new UIRunable(this) {
            @Override
            protected void runOnUiThread(Context context) {
                IntentHelper.startHome(context);
                finish();
            }
        }, 4000);
    }


    /**
     * 写完商品回调广播
     */
    public class SetProductReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean writeState = intent.getBooleanExtra("setProductResult", false);
            if (writeState) {
                log.d(TAG, "initProductList: 写入商品信息成功。");
                appedText("货道配置成功.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hasInited = true;
                        finishInit();
                    }
                });

            } else {
                log.w(TAG, "onSetFailed: 商品信息写入失败");
                appedText("货道配置失败!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hasInited = true;
                        finishInit();
                    }
                });

            }

        }
    }


}
