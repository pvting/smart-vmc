package vmc.machine.impl.yichu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.avm.serialport_142.MainHandler;
import com.avm.serialport_142.service.CommService;
import com.avm.serialport_142.service.CommServiceThread;
import com.avm.serialport_142.utils.Avm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vmc.core.log;
import vmc.machine.core.DataUtils;
import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.machine.core.model.VMCStack;
import vmc.machine.core.model.VMCStackProduct;
import vmc.machine.core.model.VmcRunStates;
import vmc.machine.core.model.VmcState;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b> 8/20/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 易触售货机控制层实现
 * <br>
 */
public class YiChuImpl implements IVMCController {
    private static final String TAG = "YiChu";

    private static final String ACTION_GOODS_SELECTED = "com.avm.serialport.SELECT_GOODS";
    private static final String ACTION_RECEIVE_MONEY = "com.avm.serialport.RECEIVE_MONEY";
    private static final String ACTION_OUT_GOODS = "com.avm.serialport.OUT_GOODS";
    private static final String ACTION_DOOR_STATE = "com.avm.serialport.door_state";

    //食品机
    private int FOODBOXID = 9;

    //状态机 0:空闲中 1:选中商品 2.现金支付中 3.出货中
    private int stateMachine;

    private final int IDLE = 0;
    private final int SELECTED = 1;
    private final int CASHING = 2;
    private final int OUTING = 3;

    private final int OUT_SUCCESS = 0;  //出货正常／售货机不支持

    private final int OUT_ERROR1 = 1;   //弹簧不转

    private final int OUT_ERROR2 = 2;   //弹簧转,光感未检测到

    private Timer timer;

    private Context mContext;

    private String lastSerialNum;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();

            if (TextUtils.equals(action, ACTION_RECEIVE_MONEY)) {
                log.i(TAG, "接收到vmc广播：ACTION_RECEIVE_MONEY");
                //更新主控机状态为现金支付中
                int paperCash = intent.getIntExtra("receive_paper_money", 0); //接收的纸币金额，以分为单位;
                int coinCash = intent.getIntExtra("receive_coin_money", 0); //接收的硬币金额，以分为单位;
                int localCash = paperCash + coinCash;
                if (localCash == 0) {
                    changeMachineState(IDLE);
                } else if (localCash > 0) {
                    changeMachineState(CASHING);
                }

                log.i(TAG, "投币结果 纸币:" + paperCash + " 硬币:" + coinCash + " 总投币:" + localCash);

                //发送广播通知金额已发生变动
                sendCashChangeState(context, localCash);

            } else if (TextUtils.equals(action, ACTION_OUT_GOODS)) {
                log.i(TAG, "接收到vmc广播：ACTION_OUT_GOODS");
                changeMachineState(IDLE);
                String outGoodsResult = MainHandler.getTranResult();

                if (TextUtils.isEmpty(outGoodsResult) || outGoodsResult.length() < 18) {
                    log.e(TAG, "出货结果异常 MainHandler.getTranResult()：" + outGoodsResult);
                    return;
                }

                log.i(TAG, "出货结果 MainHandler.getTranResult(): " + outGoodsResult);

                if (!TextUtils.isEmpty(lastSerialNum) &&
                    TextUtils.equals(lastSerialNum, outGoodsResult)) {//防止多次出货广播
                    log.e(TAG, "出货结果 MainHandler.getTranResult(): 重复订单，认定为无效");
                    lastSerialNum = outGoodsResult;
                    return;
                } else {
                    lastSerialNum = outGoodsResult;
                }

                int roadId = DataUtils.convertToInt(outGoodsResult.substring(2, 4), 0);
                int boxId = DataUtils.convertToInt(outGoodsResult.substring(0, 2), 0);
                if (boxId == FOODBOXID) {
                    roadId = getRealIdByProcessId(roadId);
                }
                String str = outGoodsResult.substring(17, 18);
                boolean outGoodsState = false;
                int errorCode = 0;
                switch (str) {
                    case "0":
                        log.i(TAG, "MainHandler.getTranResult(): 0 出货成功");
                        outGoodsState = true;
                        errorCode = OUT_SUCCESS;
                        break;
                    case "1":
                        log.i(TAG, "MainHandler.getTranResult(): 1 出货检测失败(电机未归位)");
                        errorCode = OUT_ERROR1;
                        outGoodsState = false;
                        break;
                    case "2":
                        log.i(TAG, "MainHandler.getTranResult(): 2 料道无货或故障");
                        errorCode = OUT_ERROR1;
                        outGoodsState = false;
                        break;
                    case "3":
                        log.i(TAG, "MainHandler.getTranResult(): 3 驱动正常，但光感检测不到出货");
                        errorCode = OUT_ERROR2;
                        outGoodsState = false;
                        break;
                    case "4":
                        log.i(TAG, "MainHandler.getTranResult(): 4 现金和刷卡支付时的支付失败");
                        errorCode = OUT_ERROR1;
                        outGoodsState = false;
                        break;
                    case "5":
                        log.i(TAG, "MainHandler.getTranResult(): 5 退币成功");
                        break;
                    case "6":
                        log.i(TAG, "MainHandler.getTranResult(): 6 退币失败");
                        break;
                    case "7":
                        log.i(TAG, "MainHandler.getTranResult(): 7 料道测试成功出货");
                        break;
                    case "8":
                        log.i(TAG, "MainHandler.getTranResult(): 8 交易取消成功");
                        break;
                    case "9":
                        log.i(TAG, "MainHandler.getTranResult(): 9 料道测试出货失败");
                        break;
                    default:
                        log.i(TAG, "MainHandler.getTranResult(): 其他");
                        break;
                }

                //发送出货广播
                sendOutGoodsState(context, boxId, roadId, outGoodsState, errorCode);
            } else if (TextUtils.equals(action, ACTION_GOODS_SELECTED)) {
                log.i(TAG, "接收到vmc广播：ACTION_GOODS_SELECTED");
                //更新主控机状态为选中商品
                changeMachineState(SELECTED);

                stateMachineBackToIdle(30);

                String roadInfo = MainHandler.getSelectInfo();
                if (roadInfo.length() == 12) {
                    String strBox = roadInfo.substring(0, 2);
                    String strRoad = roadInfo.substring(2, 4);
                    int boxId = DataUtils.convertToInt(strBox, 0);
                    int roadId = DataUtils.convertToInt(strRoad, 0);
                    if (boxId == FOODBOXID) {
                        roadId = getRealIdByProcessId(roadId);
                    }
                    log.i(TAG, "选货成功 MainHandler.getSelectInfo():" + boxId + "," + roadId);
                    //发送选货广播
                    sendSelectState(context, boxId, roadId);
                } else {
                    log.e(TAG, "选货失败 MainHandler.getSelectInfo():" + roadInfo);
                }
            } else if (TextUtils.equals(action, ACTION_DOOR_STATE)) {
                log.i(TAG, "接收到vmc广播：ACTION_DOOR_STATE");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendDoorState(context);
                    }
                }, 1000);
            }
        }
    };

    /**
     * 发送出货状态
     *
     * @param context       上下文
     * @param roadId        货道号
     * @param boxId         货柜号
     * @param outGoodsState 出货状态
     */
    private void sendOutGoodsState(Context context,
                                   int boxId,
                                   int roadId,
                                   boolean outGoodsState,
                                   int errorCode) {
        Intent orderIntent = new Intent();
        orderIntent.setAction(VMCAction.VMC_TO_BLL_OUTGOODS);
        orderIntent.putExtra("stack_no", roadId);
        orderIntent.putExtra("box_no", boxId);
        orderIntent.putExtra("error_code", errorCode);
        orderIntent.putExtra("outGoodsState", outGoodsState);
        context.sendBroadcast(orderIntent);
    }

    /**
     * 发送出货状态
     *
     * @param context   上下文
     * @param localCash 出货状态
     */
    private void sendCashChangeState(Context context,
                                     int localCash) {
        Intent intentReceiveMoney = new Intent();
        intentReceiveMoney.setAction(VMCAction.VMC_TO_BLL_RECEIVE_MONEY);
        intentReceiveMoney.putExtra("localCash", localCash);
        context.sendBroadcast(intentReceiveMoney);
    }

    /**
     * 发送选货广播
     *
     * @param context
     * @param boxId
     * @param roadId
     */
    private void sendSelectState(Context context, int boxId, int roadId) {
        Intent intentSelectProduct = new Intent();
        intentSelectProduct.setAction(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
        intentSelectProduct.putExtra("selectGoods_roadId", roadId);
        intentSelectProduct.putExtra("selectGoods_boxId", boxId);
        context.sendBroadcast(intentSelectProduct);
    }

    /**
     * 发送门状态
     *
     * @param context
     */
    private void sendDoorState(Context context) {
        boolean doorState = MainHandler.isDoorOpen();
        log.i(TAG, "开门广播  MainHandler.isDoorOpen():" + doorState);
        Intent intentDoor = new Intent(VMCAction.VMC_TO_BLL_DOOR_STATE);
        intentDoor.putExtra("doorState", doorState);
        context.sendBroadcast(intentDoor);
    }


    /**
     * 初始化
     *
     * @param context 上下文
     */
    @Override
    public void init(Context context) {
        log.d(TAG, "init: 机器初始化");

        //如果没有配置文件，则复制assets下的config.ini到set文件夹下
        FileCreate fileCreate = new FileCreate();

        fileCreate.createFile(context);

        MainHandler.isDebug();

        mContext = context.getApplicationContext();

        //注册广播，保留上下文
        registBroadcast(mContext);

        //驱动注册
        int state = MainHandler.load(context);
        switch (state) {
            case MainHandler.LOAD_DATA_SUCCESS:
                log.i(TAG, "init：加载配置文件成功");
                startVmcService(context);
                break;
            case MainHandler.ERROR_NO_SDCARD:
                log.e(TAG, "init：系统没有存储卡");
                break;
            case MainHandler.ERROR_EMPTY_DATA:
                log.e(TAG, "init：串口信息没有配置或者读取失败");
                break;
            case MainHandler.ERROR_NET_NOT_AVAILABLE:
                log.e(TAG, "init：系统没有连接网络");
                break;
        }
    }


    private void registBroadcast(Context context) {
        IntentFilter filter = new IntentFilter(ACTION_GOODS_SELECTED);
        filter.addAction(ACTION_RECEIVE_MONEY);
        filter.addAction(ACTION_OUT_GOODS);
        filter.addAction(ACTION_DOOR_STATE);
        context.registerReceiver(mReceiver, filter);
    }


    /**
     * 驱动激活（初始化）
     *
     * @param context 上下文
     */
    private void startVmcService(final Context context) {
        new CommService() {
            @Override
            public void result(int i) {
                boolean initState = false;
                switch (i) {
                    case CommService.ERROR_SYSTEM_SERVICE:
                        log.e(TAG, "startVmcService：数据配置或者网络调用错误");
                        break;
                    case CommService.ERROR_SYSTEM_TIME:
                        log.e(TAG, "startVmcService：系统时间不正确");
                        break;
                    case CommService.ERROR_CODE_NO_EXIST:
                        log.e(TAG, "startVmcService：激活码不存在");
                        break;
                    case CommService.ERROR_SYSTEM_CODE:
                        log.e(TAG, "startVmcService：激活码校验失败");
                        break;
                    case CommService.ERROR_ACTIVATE_CHECK:
                        log.e(TAG, "startVmcService：激活校验失败");
                        break;
                    case CommService.ERROR_CODE_USED:
                        log.e(TAG, "startVmcService：激活码已被使用");
                        break;
                    case CommService.ERROR_OTHER:
                        log.e(TAG, "startVmcService：其他错误");
                        break;
                    case CommServiceThread.ERROR_IO_PROBLEM:
                        log.e(TAG, "startVmcService：串口打开IO出错");
                        break;
                    case CommServiceThread.ERROR_PERMISSION_REJECT:
                        log.e(TAG, "startVmcService：没有打开串口的权限");
                        break;
                    case CommServiceThread.ERROR_NOT_CONFIG:
                        log.e(TAG, "startVmcService：系统中没有配置要打开的串口");
                        break;
                    case CommServiceThread.ERROR_UNKNOWN:
                        log.e(TAG, "startVmcService：串口打开时的未知错误");
                        break;
                    case CommServiceThread.COMM_SERVICE_START:
                        log.i(TAG, "startVmcService：激活启动成功");
                        initState = true;
                        break;
                }
                //初始化完成通知上层
                //延时5s发送初始化成功，防止售货机上层接受不到
                final boolean finalInitState = initState;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendInitState(finalInitState);
                    }
                }, 5000);
            }
        }.connect(context, "", 1);
    }

    private void sendInitState(boolean initState) {
        Intent initIntent = new Intent();
        initIntent.setAction(VMCAction.VMC_TO_BLL_INIT_FINISH);
        initIntent.putExtra("initState", initState);
        mContext.sendBroadcast(initIntent);
    }

    /**
     * 调用出货，如果需要多次出货，必须调用cashInit方法
     *
     * @param roadId 货道号
     * @param boxId  货柜号
     *               return 0:正常 1：
     */
    @Override
    public int outGoods(int boxId, int roadId) {
        log.i(TAG, "outGoods method start");
        if (stateMachine == OUTING) {
            //状态不对直接出货失败
            log.i(TAG, "outGoods failed that state is outting,outGoods method finish");
            sendOutGoodsState(mContext, boxId, roadId, false, OUT_ERROR1);
            return 1;
        }
        if (FOODBOXID == boxId) {
            roadId = getProcessIdByRealId(roadId);
        }
        String strRoad = formatInt(roadId);
        String strBox = formatInt(boxId);
        //更新主控机状态为正在出货
        changeMachineState(OUTING);
        stateMachineBackToIdle(5);

        String inputStr = strBox + "1" + strRoad + "00000100" + Avm.OUT_GOODS_ALIPAY;

        String vmcOrderId = getTimeStamp();

        log.i(TAG, "outGoods: MainHandler.noticeAvmOutGoods " + inputStr + "," + vmcOrderId);

        boolean result = MainHandler.noticeAvmOutGoods(inputStr, vmcOrderId);
        if (!result) {
            log.i(TAG, "outGoods failed,outGoods method finish,result" + result);
            sendOutGoodsState(mContext, boxId, roadId, false, OUT_ERROR1);
            return 2;
        } else {
            log.i(TAG, "outGoods method finish,result" + result);
            return 0;
        }
    }

    /**
     * auto back to idle
     *
     * @param delay delay time
     */
    private void stateMachineBackToIdle(int delay) {//如在执行出货命令之后，n秒内自动回到空闲
        TimerTask task = new TimerTask() {
            public void run() {
                stateMachine = IDLE;
            }
        };
        timer = new Timer();
        timer.schedule(task, delay * 1000);
    }


    /**
     * 现金出货，调用此方法并不一定会出货，要等现金投入足够才会出货
     *
     * @param roadId 货道号
     * @param price  价格
     */
    @Override
    public int outGoodsByCash(int boxId, int roadId, int price) {
        log.i(TAG, "outGoodsByCash method start");
        if (stateMachine == OUTING) {
            //状态不对直接出货失败
            log.i(TAG, "outGoodsByCash failed that state is outting,outGoodsByCash method finish");
            sendOutGoodsState(mContext, boxId, roadId, false, OUT_ERROR1);
            return 1;
        }
        if (FOODBOXID == boxId) {
            roadId = getProcessIdByRealId(roadId);
        }
        String strRoad = formatInt(roadId);
        String strBox = formatInt(boxId);
        String strPrice = ("00000000" + price);
        String formatPrice = strPrice.substring(strPrice.length() - 8, strPrice.length());
        //更新主控机状态为正在出货
        changeMachineState(OUTING);
        stateMachineBackToIdle(5);

        String inputStr = strBox + "1" + strRoad + formatPrice + Avm.OUT_GOODS_CASH;
        String vmcOrderId = getTimeStamp();

        log.i(TAG, "outGoodsByCash: MainHandler.noticeAvmOutGoods " + inputStr + "," + vmcOrderId);

        boolean result = MainHandler.noticeAvmOutGoods(inputStr, vmcOrderId);
        if (!result) {
            log.i(TAG, "outGoodsByCash failed,outGoods method finish,result" + result);
            sendOutGoodsState(mContext, boxId, roadId, false, OUT_ERROR1);
            return 2;
        } else {
            log.i(TAG, "outGoodsByCash method finish,result" + result);
            return 0;
        }
    }

    /**
     * 获取机器编号
     *
     * @return 返回机器编号，注意要在初始化完成后获取
     */
    @Override
    public String getVendingMachineId() {
        try {
            String machineId = MainHandler.getMachNo();
            if (!TextUtils.isEmpty(machineId)) {
                SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
                String savedMechineId = sp.getString("mechineId", "00000000");
                if (!savedMechineId.equals(machineId)) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("mechineId", machineId);
                    edit.apply();
                    log.i(TAG, "saved machineId: " + machineId + "to sp file");
                }
            }
            log.i(TAG, "getVendingMachineId,machineId:" + machineId);
            return machineId;
        } catch (Exception e) {
            log.e(TAG, "getVendingMachineId,error message: " + e.getMessage());
            return "";
        }
    }

    @Override
    public int setVendingMachineId(String machineId) {
        return 0;
    }

    /**
     * 设置单个商品到售货机
     *
     * @param boxId     货柜号
     * @param roadId    货道号
     * @param productId 产品id
     * @param count     数量
     * @param price     价格（分）
     */
    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {
        String strCount = formatInt(count);
        //如果是设置的食品机,则需要货道变换
        if (boxId == FOODBOXID) {
            roadId = getProcessIdByRealId(roadId);
        }
        String strRoadId = formatInt(roadId);
        String strBoxId = formatInt(boxId);
        String strPrice = formatPrice(price);
        log.i(TAG,
              "setProduct: " +
              "货柜号：" +
              strBoxId +
              " 货道号：" +
              strRoadId +
              " 数量：" +
              strCount +
              " 商品号：" +
              productId +
              " 价格：" +
              strPrice);
        MainHandler.setRoad(strBoxId + strRoadId + productId + strPrice);
        log.i(TAG, "setProduct: " + strBoxId + strRoadId + productId + strPrice);
    }

    /**
     * 格式化价格
     *
     * @param price 价格（单位为分）
     *
     * @return 单位为角的四位价格
     */
    private String formatPrice(int price) {
        String strPrice = "0000" + String.valueOf(price / 10);
        int size = strPrice.length();
        return strPrice.substring(size - 4, size);
    }

    /**
     * 格式化int型数据
     * 1 -> 01，11 -> 11
     */
    private String formatInt(int num) {
        String str = "00" + num;
        return str.substring(str.length() - 2, str.length());
    }


    private final int SUCCESS = 0;
    private final int FAILED = 1;
    private final int TIMEOUT = 2;

    /**
     * 设置商品列表
     *
     * @param list 设置各个商品到售货机
     */
    @Override
    public void setProducts(final List<VMCStackProduct> list) {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                for (int i = 0; i < list.size(); i++) {
                    VMCStackProduct product = list.get(i);
                    int roadId = product.roadId;
                    int boxId = product.boxId;
                    setProduct(boxId,
                               roadId,
                               product.seqNo,
                               product.stock < 0 ? 0 : product.stock,
                               product.price);
                    int count = 0;
                    while (true) {
                        if (isProductWriteDown() == 0) {
                            log.i(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置成功");
                            break;
                        } else if (isProductWriteDown() == -1) {
                            //重新设置（第二次）
                            setProduct(boxId,
                                       roadId,
                                       product.seqNo,
                                       product.stock < 0 ? 0 : product.stock,
                                       product.price);
                            SystemClock.sleep(5000);
                            if (isProductWriteDown() == 0) {
                                log.i(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置成功");
                                break;
                            }
                            //重新设置（第三次）
                            setProduct(boxId,
                                       roadId,
                                       product.seqNo,
                                       product.stock < 0 ? 0 : product.stock,
                                       product.price);
                            SystemClock.sleep(5000);
                            if (isProductWriteDown() == 0) {
                                log.i(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置成功");
                                break;
                            }
                            log.e(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置失败");
                            sendWriteProductStateBoardcast(FAILED);
                            return;
                        }
                        count++;
                        if (count >= 10) {
                            //重新设置（第二次）
                            setProduct(boxId,
                                       roadId,
                                       product.seqNo,
                                       product.stock < 0 ? 0 : product.stock,
                                       product.price);
                            SystemClock.sleep(5000);
                            if (isProductWriteDown() == 0) {
                                log.i(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置成功");
                                break;
                            }
                            //重新设置（第三次）
                            setProduct(boxId,
                                       roadId,
                                       product.seqNo,
                                       product.stock < 0 ? 0 : product.stock,
                                       product.price);
                            SystemClock.sleep(5000);
                            if (isProductWriteDown() == 0) {
                                log.i(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置成功");
                                break;
                            }
                            log.e(TAG, "box_no:" + boxId + " stack_no:" + roadId + "货道设置超时");
                            sendWriteProductStateBoardcast(TIMEOUT);
                            return;
                        }
                        SystemClock.sleep(500);
                    }
                }
                sendWriteProductStateBoardcast(SUCCESS);
            }
        }.start();
    }


    /**
     * 发送状态写入状态广播
     *
     * @param state
     */
    private void sendWriteProductStateBoardcast(final int state) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
                if (state == SUCCESS) {
                    intent.putExtra("setProductResult", true);
                } else {
                    intent.putExtra("setProductResult", false);
                }
                mContext.sendBroadcast(intent);
            }
        });
    }


    /**
     * 判断是否已经写入
     *
     * @return 0:已写入 1：未写入完成 -1：写入失败
     */
    private int isProductWriteDown() {
        int setroad = MainHandler.getRtSetRoad();
        if (setroad == 0) {
            return 0;
        } else if (setroad == 1) {
            return 1;
        } else {
            return -1;
        }

    }

    /**
     * 获取时间戳(6位)
     */
    private String getTimeStamp() {
        String timeStr = String.valueOf(System.currentTimeMillis());
        return timeStr.substring(timeStr.length() - 9, timeStr.length() - 3);
    }


    /**
     * 获取当前售货机的运行状态
     *
     * @return 运行状态
     */
    public String getVmcRunningStates() {

        VmcState vmcState = catchVmcRunningStates();

        VmcRunStates vmcRunStates = new VmcRunStates();

        String emptyStockIds = getEmptyStock();

        //串口通讯
        vmcRunStates.isVMCDisconnected = !MainHandler.isAvmRunning();


        //纸币器异常
        vmcRunStates.isPaperMError = !vmcState.paperCashM;


        //硬币器异常
        vmcRunStates.isCoinMError = !vmcState.coinCashM;


        //5角找零不足
        vmcRunStates.isLackOf50Cent = vmcState.isLeakChange5jiao;


        //1元找零不足
        vmcRunStates.isLackOf100Cent = vmcState.isLeakChange1yuan;


        //售空
        vmcRunStates.isSoldOut = emptyStockIds.length() > 0;


        //售空货道
        vmcRunStates.soldOutStockId = emptyStockIds;


        //故障货道集合
        vmcRunStates.breakdownStockId = getBreakStock();


        //温度
        vmcRunStates.temperature = new ArrayList<>();


        //灯状态
        vmcRunStates.lightState = "";


        //门状态
        vmcRunStates.isDoorOpened = MainHandler.isDoorOpen();


        //主控版本
        vmcRunStates.majorVersionId = MainHandler.getVMCVersion();


        //售卖版本
        vmcRunStates.saleAppVersionId = getsaleAppVersion();


        //补货管理版本
        vmcRunStates.maintainAppVersionId = getManagementVersion();


        //获取IMEI号
        vmcRunStates.vmc_code = getImei();


        //获取SIM卡号(ICCID,非手机号码)
        vmcRunStates.sim_code = getTelephoneNumber();


        log.d(TAG, vmcRunStates.toString());


        return new Gson().toJson(vmcRunStates);
    }


    public String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String IMEI = telephonyManager.getDeviceId();
        if (null == IMEI) {
            return "";
        }
        return IMEI.replace("IMEI:", "").trim();
    }

    public String getTelephoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String SIM = telephonyManager.getSimSerialNumber();
        if (null == SIM) {
            return "";
        }
        return SIM;
    }


    public String getsaleAppVersion() {
        String versionName = "";
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    public String getManagementVersion() {
        try {
            List<PackageInfo> packageInfos = mContext.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }
                if (packageInfo.packageName != null) {
                    String packageName = packageInfo.packageName.toLowerCase();
                    if (packageName.contains("com.want.management")) {
                        return packageInfo.versionName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 货道售空状态
     *
     * @return 空数量料道集合
     */
    private String getEmptyStock() {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, VMCStack> sBLLProductsByRoadMap = null;

        SharedPreferences sp = mContext.getSharedPreferences("products", Context.MODE_PRIVATE);

        String soruceStr = sp.getString("stackProducts", null);
        ArrayList<Integer> stockIds = new ArrayList<>();

        if (soruceStr != null) {
            Gson gson = new Gson();
            sBLLProductsByRoadMap =
                    gson.fromJson(soruceStr, new TypeToken<HashMap<String, VMCStack>>() {}.getType());
        }

        if (sBLLProductsByRoadMap == null) {
            sBLLProductsByRoadMap = new HashMap<>();
        }
        for (String key : sBLLProductsByRoadMap.keySet()) {

            VMCStack vmcStackProduct = sBLLProductsByRoadMap.get(key);

            if (vmcStackProduct.getBoxNoInt() == 9) {
                if (vmcStackProduct.quantity <= 0) {
                    stockIds.add(vmcStackProduct.getStackNoInt());


                }
            } else {
                if (vmcStackProduct.quantity <= 1) {
                    stockIds.add(vmcStackProduct.getStackNoInt());
                }
            }
        }


        Collections.sort(stockIds);


        for (Integer item : stockIds) {
            stringBuilder.append(item).append(",");

        }


        String s = stringBuilder.toString();
        if (s.length() > 0) {

            return s.substring(0, s.length() - 1);
        }
        return "";
    }


    /**
     * 货道售空状态
     *
     * @return 空数量料道集合
     */
    private String getBreakStock() {

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Integer> stockIds = new ArrayList<>();

        HashMap<String, VMCStack> sBLLProductsByRoadMap = null;

        SharedPreferences sp = mContext.getSharedPreferences("products", Context.MODE_PRIVATE);

        String soruceStr = sp.getString("stackProducts", null);

        if (soruceStr != null) {
            Gson gson = new Gson();
            sBLLProductsByRoadMap =
                    gson.fromJson(soruceStr, new TypeToken<HashMap<String, VMCStack>>() {}.getType());
        }

        if (sBLLProductsByRoadMap == null) {
            sBLLProductsByRoadMap = new HashMap<>();
        }
        for (String key : sBLLProductsByRoadMap.keySet()) {

            VMCStack vmcStackProduct = sBLLProductsByRoadMap.get(key);
            if (getStockByRoad(vmcStackProduct.getBoxNoInt(), vmcStackProduct.getStackNoInt()) < 0) {
                stockIds.add(vmcStackProduct.getStackNoInt());
            }
        }

        Collections.sort(stockIds);


        for (Integer item : stockIds) {
            stringBuilder.append(item).append(",");
        }

        String s = stringBuilder.toString();
        if (s.length() > 0) {
            return s.substring(0, s.length() - 1);
        }
        return "";
    }


    /**
     * 获取售货机的运行状态
     *
     * @return 售货机运行状态
     */
    private VmcState catchVmcRunningStates() {


        String state = MainHandler.getMachRunInfo();


        if (state == null || state.length() < 40) {
            log.v(TAG, "获取设备状态失败");
            return new VmcState(false, false, false, false);
        }


        //4 位:0 光感关;1 光感开 注:1号食品机
        String lightSensation = state.substring(3, 4);


        //7 位:0 门关;1 门开
        String doorOpen = state.substring(6, 7);


        //8 位:0 营业;1 暂停营业
        String bussiness = state.substring(7, 8);


        //读卡器
        String cardReader = state.substring(8, 9);

        //红外模块
        String infrared = state.substring(9, 10);

        //右室温度传感器
        String rightVentricle = state.substring(14, 15);

        //左室温度传感器
        String leftVentricle = state.substring(15, 16);

        //驱动板无应答(暂停营业)
        String drive = state.substring(17, 18);


        //纸币器状态
        String papercash = state.substring(16, 24);


        //硬币起状态(不包含缺币)
        String coincash = state.substring(26, 32);


        //缺少5角
        boolean isLeakChange5jiao = false;

        //缺少1元
        boolean isLeakChange1yuan = false;


        if (Integer.parseInt(state.substring(24, 25)) == 1) {
            isLeakChange1yuan = true;
        }

        if (Integer.parseInt(state.substring(25, 26)) == 1) {
            isLeakChange5jiao = true;
        }


        return new VmcState(
                TextUtils.equals(papercash, "00000000"),
                TextUtils.equals(coincash, "000000"),
                isLeakChange5jiao,
                isLeakChange1yuan);
    }


    /**
     * 通过货道号（标签id）获取库存
     *
     * @param roadId 货道号
     *
     * @return 库存数量
     */
    public int getStockByRoad(int boxId, int roadId) {
        if (boxId == FOODBOXID) {
            roadId = getProcessIdByRealId(roadId)
            ;
        }
        String resultStr = MainHandler.getGoodsInfo2(boxId, roadId);
        String resultTag = resultStr.substring(0, 1);
        if (TextUtils.equals(resultTag, "0")) {
            log.v(TAG, "getStockByRoad: 获取销售状态成功," + boxId + "，" + roadId + "result: " + resultTag);
            return 1;
        } else if (TextUtils.equals(resultTag, "1")) {
            log.v(TAG, "getStockByRoad: 获取销售状态成功," + boxId + "，" + roadId + "result: " + resultTag);
            return 0;
        } else {
            log.e(TAG, "getStockByRoad: 获取销售状态异常," + boxId + "，" + roadId + "result:" + resultTag);
            return -1;
        }
    }

    @Override
    public void cashInit() {
    }

    @Override
    public void cashFinish() {
    }


    /**
     * 通过标签id获取程序id
     *
     * @param realId 标签id
     *
     * @return 程序id
     */
    public int getProcessIdByRealId(int realId) {
        int processId = ((realId / 10 - 1) << 3) + realId % 10;
        if (processId > 0 && processId <= 48) {
            return processId;
        } else {
            return -1;
        }
    }

    /**
     * 通过程序id获取标签id
     *
     * @param processId 程序id
     *
     * @return 标签id
     */
    public int getRealIdByProcessId(int processId) {
        if (processId <= 0 || processId >= 100) {
            return 0;
        }
        int line = ((processId - 1) >> 3) + 1;
        int column = processId % 8;
        if (column == 0) {
            column = 8;
        }
        return line * 10 + column;
    }

    @Override
    public void selectProduct(int boxId, int roadId) {
        //no use
    }

    @Override
    public void cancelDeal() {
        //no use
    }

    @Override
    public boolean isConnectError() {
        boolean isConnect = MainHandler.isAvmRunning();
        log.i(TAG, "isConnect: " + isConnect);
        return !isConnect;
    }


    @Override
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {
        //no use
    }

    @Override
    public int setFlowController(int waterFlow) {
        return 0;
    }


    @Override
    public int setMaxLitreAndTime(int Litre, int waterTime) {
        return 0;
    }

    @Override
    public int setPumpTime(int pumpTime) {
        return 0;
    }


    @Override
    public String getBrand() {
        return "EasyTouch";
    }

    @Override
    public boolean isLackOf50Cent() {
        String state = MainHandler.getMachRunInfo();
        if (state == null || state.length() < 40) {
            log.v(TAG, "获取设备状态失败");
            return false;
        }
        //缺少5角
        boolean isLeakChange5jiao = false;
        if (Integer.parseInt(state.substring(32, 36)) < 8) {
            isLeakChange5jiao = true;
        }
        return isLeakChange5jiao;
    }

    @Override
    public boolean isLackOf100Cent() {
        String state = MainHandler.getMachRunInfo();
        if (state == null || state.length() < 40) {
            log.v(TAG, "获取设备状态失败");
            return false;
        }
        //缺少1元
        boolean isLeakChange1yuan = false;
        if (Integer.parseInt(state.substring(36, 40)) < 8) {
            isLeakChange1yuan = true;
        }
        return isLeakChange1yuan;
    }

    @Override
    public boolean isDoorOpen() {
        return MainHandler.isDoorOpen();
    }

    @Override
    public boolean isDriveError() {

        String  str =  MainHandler.getMachRunInfo();
        if (TextUtils.isEmpty(str)){
            log.e(TAG,"isDriveError: MainHandler.getMachRunInfo()"+str);
            return  true;
        }

        if (str.length() < 16) {
            log.v(TAG, "isDriveError: 获取驱动版状态失败");
            return true;
        }

       if (str.substring(15, 16).equals("1")){
           log.v(TAG, "isDriveError: 驱动版故障:");
           return true;
       }
        return false;
    }

    /**
     * 更改状态机状态
     *
     * @param state 状态
     */
    private void changeMachineState(int state) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stateMachine = state;
    }
}
