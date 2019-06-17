package vmc.machine.impl.watergod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import vmc.machine.core.DataUtils;
import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.machine.core.model.VMCStackProduct;

import static android.content.Context.MODE_MULTI_PROCESS;
import static vmc.machine.core.DataUtils.*;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/2/23<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class WaterGodImpl implements IVMCController {

    private static final String TAG = WaterGodImpl.class.getSimpleName();

    /**
     * 维保的包名
     */
    private static final String ACTION_FROM_MAINTAIN = "com.want.vmc.watergod.getVmcStatus";

    private static final String ACTION_FROM_MAINTAIN_PULSE = "com.want.vmc.WaterGod_OutWater_PulseCount";

    private static final String ACTION_FROM_MAINTAIN_DRAINAGE_TIME = "com.want.vmc.WaterGod_OutWater_DrainageTime";


    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 水神协议
     */
    private WaterGodProtocol waterGodProtocol = WaterGodProtocol.getInstance();

    /**
     * 是否连接
     */
    private boolean isConnectError = false;

    /**
     * 维保sp
     */
    private static final String MAINTAIN_SP_NAME = "maintainData";
    private static final String MAINTAIN_PH_KEY = "state_ph";
    private static final String MAINTAIN_ACC_KEY = "state_acc";
    private static final String MAINTAIN_WATER_DEGREE_KEY = "state_water_degree";


    private int mRoadId;
    private int mBoxId;
    private String mMachineId;
    private String mMachineVersion;
    private String mDoorState;


    private boolean isSaleStatus = true;
    private boolean mLiquidState = true;
    private boolean mWaterPressureState = true;
    private boolean mOutPut = true;
    private boolean isSerialError = false;
    private int mPulse;
    private int mPumpTime;


    /**
     * 串口读取状态轮询
     */
    private Runnable mSerialRunnable;

    /**
     * 串口读取状态轮询时间
     */
    private static final long SERIAL_LOOP_TIME = 5000L;


    /**
     * 写入机器间隔时间
     */
    private static final long CMD_LOOP_TIME = 500L;

    /**
     * 默认脉冲数
     */
    private static final int DEFAULT_PULSE = 300;

    /**
     * 默认排废水时间 s
     */
    private static final int DEFAULT_DRAINAGE_TIME = 4;






    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WaterGodProtocol.WATER_GOD_EVENT_ID_VMC_INIT_FINISH:
                    //机器初始化完成
                    Log.d(TAG, "收到初始化完成信息");
                    Intent intentInit = new Intent(VMCAction.VMC_TO_BLL_INIT_FINISH);
                    mContext.sendBroadcast(intentInit);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_OUT_GOODS_SUCCESS:
                    //出货成功
                    Log.d(TAG, "收到出货成功消息");
                    Intent intentOutGoodsSuccess = new Intent(VMCAction.VMC_TO_BLL_OUTGOODS);
                    String outGoodInfo = bytes2HexString(waterGodProtocol.getVenderActionStatus());
                    if (outGoodInfo.length() < 6) {
                        return;
                    }
                    boolean outGoodState = false;
                    mRoadId = Integer.parseInt(outGoodInfo.substring(2, 4), 16);//10 == 1L
                    if (Integer.parseInt(outGoodInfo.substring(0, 2), 16) == 1) {
                        //出水成功
                        outGoodState = true;
                    } else if (Integer.parseInt(outGoodInfo.substring(0, 2), 16) == 2) {
                        outGoodState = false;
                    }
                    intentOutGoodsSuccess.putExtra("stack_no", mRoadId);
                    intentOutGoodsSuccess.putExtra("box_no", mBoxId);
                    intentOutGoodsSuccess.putExtra("outGoodsState", outGoodState);
                    mContext.sendBroadcast(intentOutGoodsSuccess);
                    break;

                case WaterGodProtocol.WATER_GOD_EVENT_ID_MACHINE_ID:
                    // 机器ID
                    Intent intentMachineId = new Intent(VMCAction.VMC_TO_BLL_MACHINE_ID_STATE);

                    Log.d(TAG, "收到机器mMachineId信息byte2HexString=" + bytes2HexString(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_MACHINE_ID)));

                    byte[]
                            getRealData =
                            reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_MACHINE_ID));
                    String machineIdS = new String(getRealData);
                    if (machineIdS.length() > 0 && machineIdS.length() < 8) {
                        machineIdS = "00000000";
                    } else if (machineIdS.length() > 8) {
                        machineIdS = "00000000";
                    }
                    mMachineId = machineIdS;
                    Log.d(TAG, "收到机器mMachineId信息=" + mMachineId);
                    intentMachineId.putExtra("machineIdState", machineIdS);
                    mContext.sendBroadcast(intentMachineId);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_MACHINE_VERSION:
                    //固件版本
                    Intent intentMachineVersion = new Intent(VMCAction.VMC_TO_BLL_MACHINE_VERSION_STATE);
                    String
                            machineVersionS =
                            bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_MACHINE_VERSION)));
                    mMachineVersion = String.valueOf(Integer.parseInt(machineVersionS, 16));
                    intentMachineVersion.putExtra("machineVersionState", mMachineVersion);
                    Log.d(TAG, "收到固件版本信息=" + mMachineVersion);
                    mContext.sendBroadcast(intentMachineVersion);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_PULSE:
                    //出厂设置脉冲，登录页面？
                    Intent intentPulse = new Intent(VMCAction.VMC_TO_BLL_PULSE_STATE);
                    int
                            pulseInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_PULSE))),
                                             16);
                    mPulse = pulseInt;
                    intentPulse.putExtra("pulseState", pulseInt);
                    Log.d(TAG, "收到脉冲信息=" + pulseInt);
                    mContext.sendBroadcast(intentPulse);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_LIMIT_LITRE:
                    //单次购买水 上限
                    Intent intentLimitLitre = new Intent(VMCAction.VMC_TO_BLL_LIMIT_LITRE_STATE);
                    int
                            limitLitreInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_LIMIT_LITRE))),
                                             16);
                    intentLimitLitre.putExtra("limitLitreState", limitLitreInt);
                    Log.d(TAG, "收到单次购买水上限信息=" + limitLitreInt);
                    mContext.sendBroadcast(intentLimitLitre);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_TOTAL_LITRE:
                    //总计出水量
                    Intent intentTotalLitre = new Intent(VMCAction.VMC_TO_BLL_TOTAL_LITRE_STATE);
                    int
                            totalLitreInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_TOTAL_LITRE))),
                                             16);
                    intentTotalLitre.putExtra("totalLitreState", totalLitreInt);
                    Log.d(TAG, "收到总计出水量信息=" + totalLitreInt);
                    mContext.sendBroadcast(intentTotalLitre);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_BUY_WATER_WAIT_TIME:
                    //买水等待时间
                    Intent
                            intentBuyWaterWaitTime =
                            new Intent(VMCAction.VMC_TO_BLL_BUY_WATER_WAIT_TIME_STATE);
                    int
                            buyWaterWaitTimeInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_BUY_WATER_WAIT_TIME))),
                                             16);
                    intentBuyWaterWaitTime.putExtra("buyWaterWaitTimeState", buyWaterWaitTimeInt);
                    Log.d(TAG, "收到买水等待时间信息=" + buyWaterWaitTimeInt);
                    mContext.sendBroadcast(intentBuyWaterWaitTime);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_DRAINAGE_WATER_WAIT_TIME:
                    //排水等待时间
                    Intent
                            intentDrainageWaterWaitTime =
                            new Intent(VMCAction.VMC_TO_BLL_DRAINAGE_WATER_WAIT_TIME_STATE);
                    int
                            drainageWaterWaitTimeInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_DRAINAGE_WATER_WAIT_TIME))),
                                             16);
                    mPumpTime = drainageWaterWaitTimeInt;
                    intentDrainageWaterWaitTime.putExtra("drainageWaterWaitTimeState",
                                                         drainageWaterWaitTimeInt);
                    Log.d(TAG, "收到排水等待时间信息=" + drainageWaterWaitTimeInt);
                    mContext.sendBroadcast(intentDrainageWaterWaitTime);

                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_SALE:

                    Intent intentSale = new Intent(VMCAction.VMC_TO_BLL_SALE_STATE);
                    int
                            isSaleValue =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_SALE))), 16);
                    //去除第一个byte后的data 应该为value值，可以直接判断
                    isSaleStatus = (isSaleValue == 1);
                    intentSale.putExtra("saleState", isSaleStatus);
                    Log.d(TAG, "收到是否可售卖信息=" + isSaleStatus);
                    mContext.sendBroadcast(intentSale);
                    //可售卖状态
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_DOOR:
                    Intent intentDoor = new Intent(VMCAction.VMC_TO_BLL_DOOR_STATE);
                    intentDoor.putExtra("doorState",
                                        bytes2HexString(waterGodProtocol.getStatusRpt(WaterGodProtocol.WATER_GOD_GET_STATUS_DOOR)));
                    //00表示门关 01表示门开
                    boolean
                            doorStates =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_DOOR))), 16) == 0;
                    mDoorState = String.valueOf(doorStates);
                    Log.d(TAG, "收到门开关信息=" + mDoorState);
                    mContext.sendBroadcast(intentDoor);
                    //开关门
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_LIQUID:
                    Intent intentLiquid = new Intent(VMCAction.VMC_TO_BLL_LIQUID_STATE);
                    int
                            liquidValue =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_LIQUID))), 16);
                    mLiquidState = liquidValue == 0;
                    intentLiquid.putExtra("liquidState", mLiquidState);
                    Log.d(TAG, "收到原液mLiquidState信息=" + mLiquidState);
                    mContext.sendBroadcast(intentLiquid);

                    Intent intentSensor = new Intent(VMCAction.VMC_TO_BLL_SENSOR_STATE);
                    intentSensor.putExtra("liquidState", mLiquidState);
                    intentSensor.putExtra("waterPressureState", mWaterPressureState);
                    intentSensor.putExtra("outPut", mOutPut);
                    mContext.sendBroadcast(intentSensor);

                    //原液状态
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_WATER_PRESSURE:
                    Intent intentWaterPressure = new Intent(VMCAction.VMC_TO_BLL_WATER_PRESSURE_STATE);
                    int
                            waterPressureInt =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_WATER_PRESSURE))), 16);
                    mWaterPressureState = waterPressureInt == 0;
                    Log.d(TAG, "收到水压mWaterPressureState信息=" + mWaterPressureState);
                    intentWaterPressure.putExtra("waterPressureState", mWaterPressureState);
                    mContext.sendBroadcast(intentWaterPressure);
                    //水压状态

                    Intent intentSensor2 = new Intent(VMCAction.VMC_TO_BLL_SENSOR_STATE);
                    intentSensor2.putExtra("liquidState", mLiquidState);
                    intentSensor2.putExtra("waterPressureState", mWaterPressureState);
                    intentSensor2.putExtra("outPut", mOutPut);
                    mContext.sendBroadcast(intentSensor2);
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_F2BREAKUP:

                    Intent intentF2breakup = new Intent(VMCAction.VMC_TO_BLL_F2BREAKUP_STATE);
                    intentF2breakup.putExtra("f2breakupState",
                                             bytes2HexString(waterGodProtocol.getStatusRpt(WaterGodProtocol.WATER_GOD_GET_STATUS_F2_BREAKUP)));
                    //00 正常   01 不正常
                    boolean
                            f2break =
                            (Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_F2_BREAKUP))), 16) == 0);
                    Log.d(TAG, "收到F2断开信息=" + f2break);
                    mContext.sendBroadcast(intentF2breakup);
                    //F2断开状态
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_OUTPUT1:

                    int
                            outPut1Value =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_OUTPUT_1_4))), 16);
                    mOutPut = outPut1Value == 0;
                    Intent intentOutPut1 = new Intent(VMCAction.VMC_TO_BLL_OUTPUT1_STATE);
                    intentOutPut1.putExtra("outPut1State", mOutPut);
                    mContext.sendBroadcast(intentOutPut1);

                    Log.d(TAG, "收到outPut1信息=" + mOutPut);
                    Intent intentSensor3 = new Intent(VMCAction.VMC_TO_BLL_SENSOR_STATE);
                    intentSensor3.putExtra("liquidState", mLiquidState);
                    intentSensor3.putExtra("waterPressureState", mWaterPressureState);
                    intentSensor3.putExtra("outPut", mOutPut);
                    mContext.sendBroadcast(intentSensor3);

                    //生成机1状态
                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_OUTPUT2:
                    int
                            outPut2Value =
                            Integer.parseInt(bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(
                                    WaterGodProtocol.WATER_GOD_GET_STATUS_OUTPUT_1_5))), 16);
                    mOutPut = outPut2Value == 0;
                    Intent intentOutPut2 = new Intent(VMCAction.VMC_TO_BLL_OUTPUT2_STATE);
                    intentOutPut2.putExtra("outPut2State", mOutPut);
                    mContext.sendBroadcast(intentOutPut2);
                    //生成机2状态
                    Log.d(TAG, "收到outPut2信息=" + mOutPut);
                    Intent intentSensor4 = new Intent(VMCAction.VMC_TO_BLL_SENSOR_STATE);
                    intentSensor4.putExtra("liquidState", mLiquidState);
                    intentSensor4.putExtra("waterPressureState", mWaterPressureState);
                    intentSensor4.putExtra("outPut", mOutPut);
                    mContext.sendBroadcast(intentSensor4);

                    break;
                case WaterGodProtocol.WATER_GOD_EVENT_ID_INFO_ALL:
                    //格式应该为"FF 12 34 31 32 33 34 35 36 37 38 00 01 01 2C 32 00 00 00 00 78 04"
                    //reduceOneByte格式应该为"12 34 31 32 33 34 35 36 37 38 00 01 01 2C 32 00 00 00 00 78 04"
                    Log.d(TAG, "收到infoAll信息====");
                    String
                            machineInfoAll =
                            bytes2HexString(reduceOneByte(waterGodProtocol.getRpt(WaterGodProtocol.WATER_GOD_GET_INFO_ALL)));

                    if (!TextUtils.isEmpty(machineInfoAll) && machineInfoAll.length() == 42) {
                        Log.d(TAG, "收到infoAll信息=" + machineInfoAll);
//                      123431323334353637380001012C32000000007804
                        mMachineId = asciiString2Decs(machineInfoAll.substring(4, 20));//8位机器码
                        mMachineVersion =
                                String.valueOf(Integer.parseInt(machineInfoAll.substring(20, 24), 16));//固件版本
                        mPulse = Integer.parseInt(machineInfoAll.substring(24,28), 16);
                        mPumpTime = Integer.parseInt(machineInfoAll.substring(40,42), 16);
                        WaterGodUtils.setPulseAndPumpTimeConfig(mContext,mPulse,mPumpTime);

                        Log.d(TAG, "收到infoAll信息 mMachineId=" + mMachineId+" mMachineVersion="+mMachineVersion+" mPulse="+mPulse+" mPumpTime="+mPumpTime);

                    }

                    break;

                case WaterGodProtocol.WATER_GOD_EVENT_ID_STATUS_ALL:
                    Log.d(TAG, "收到statusAll信息====");
                    //获取所有状态值 FF 00 01 00 00 00 00 00 00
                    /*
                      1 预留 1byte
                      2 售卖状态 1byte 00不可售卖 01可售卖
                      3 门开关 1byte 00门关 01门开
                      4 原液低位 1byte 00正常 01异常
                      5 低水压 1byte 00正常 01异常
                      6 F2 1byte 00正常 01异常
                      7 outPut1 1byte 00正常 01异常
                      8 outPut2 1byte 00正常 01异常
                     */
                    String statusAll = bytes2HexString(reduceOneByte(waterGodProtocol.getStatusRpt(WaterGodProtocol.WATER_GOD_GET_STATUS_ALL)));
                    // statusAll = "00 01 00 00 00 00 00 00"
                    isSaleStatus = (DataUtils.convertToInt(statusAll.substring(2,4),1)==1);
                    mLiquidState = (DataUtils.convertToInt(statusAll.substring(6,8),0)==0);
                    mWaterPressureState = (DataUtils.convertToInt(statusAll.substring(8,10),0)==0);
                    mOutPut =  (DataUtils.convertToInt(statusAll.substring(12,14),0)==0)||(DataUtils.convertToInt(statusAll.substring(14,16),0)==0);
                    break;

                case WaterGodProtocol.WATER_GOD_EVENT_ID_READ_STATUS://read
                    String commStatus = new String((reduceOneByte(waterGodProtocol.getCommStatusRpt(WaterGodProtocol.COMM_READ))));
//                    isSerialError = DataUtils.convertToInt(commStatus,0) == 1;
                    Log.d(TAG,"commStatus="+commStatus+" "+bytes2HexString(waterGodProtocol.getCommStatusRpt(WaterGodProtocol.COMM_READ)));
                    //连续3个串口异常信号，算串口异常@pei
                    if(DataUtils.convertToInt(commStatus,0) == 1){
                        countFlag ++;
                        if(countFlag>=3){
                            countFlag = 0;
                            isSerialError = true;
                        }
                    }else{
                        countFlag = 0;
                        isSerialError = false;
                    }
                    break;
            }
        }
    };

    int countFlag = 0;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "接收维保广播");
            if (TextUtils.equals(action, ACTION_FROM_MAINTAIN)) {
                //接收从维保过来的广播后马上请求VMC，获取状态
                Log.d(TAG, "接收维保广播 action==");
                waterGodProtocol.getStatus(WaterGodProtocol.WATER_GOD_GET_STATUS_LIQUID);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waterGodProtocol.getStatus(WaterGodProtocol.WATER_GOD_GET_STATUS_WATER_PRESSURE);
                        Log.d(TAG, "维保 请求水压");
                    }
                }, CMD_LOOP_TIME);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waterGodProtocol.getInfo(WaterGodProtocol.WATER_GOD_GET_INFO_MACHINE_ID);
                        Log.d(TAG, "维保 请求机器ID");
                    }
                }, CMD_LOOP_TIME*2);

            }

            if(TextUtils.equals(action,ACTION_FROM_MAINTAIN_PULSE)){
                Log.d(TAG, "接收维保广播 action==脉冲数");
                int pulse = intent.getIntExtra("pulse_count",DEFAULT_PULSE);
                setFlowController(pulse);

            }


            if(TextUtils.equals(action,ACTION_FROM_MAINTAIN_DRAINAGE_TIME)){
                Log.d(TAG, "接收维保广播 action==排水等待时间");
                int time = intent.getIntExtra("drainage_time",DEFAULT_DRAINAGE_TIME);
                setPumpTime(time);

            }

            // TODO: 2017/3/6 之后使用队列来处理这个事件
            // TODO: 2017/3/6 后续出厂设置是否使用广播处理待定

        }
    };


    @Override
    public void init(Context context) {
        mContext = context;
        //如果没有配置文件，则复制assets下的config.ini到VMC文件夹下
        FileCreate fileCreate = new FileCreate();
        fileCreate.createFile(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                waterGodProtocol.startProtocol();
            }
        }).start();

        Thread threadGetEvent = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isConnectError) {
                    int EventId = waterGodProtocol.getEvent();
                    mHandler.sendEmptyMessage(EventId);
                }
            }
        });

        //设置守护进程，主进程结束，进程也结束
        threadGetEvent.setDaemon(true);
        threadGetEvent.start();

        mSerialRunnable = new Runnable() {
            @Override
            public void run() {
                waterGodProtocol.getCommStatus();
                //5秒一次
                mHandler.postDelayed(mSerialRunnable, SERIAL_LOOP_TIME);
            }
        };
        mHandler.post(mSerialRunnable);

        /**
         * 发送指令给机器时，需要一定时间分别发送，不然只会相应最后一个发送的指令
         */
        //初始化机器,获取信息
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waterGodProtocol.getStatus(WaterGodProtocol.WATER_GOD_GET_STATUS_ALL);
                Log.d(TAG, "初始化StatusAll");
            }
        }, CMD_LOOP_TIME*2);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waterGodProtocol.getInfo(WaterGodProtocol.WATER_GOD_GET_INFO_ALL);
                Log.d(TAG, "初始化InfoAll");
            }
        }, CMD_LOOP_TIME*3);


        IntentFilter filter = new IntentFilter(ACTION_FROM_MAINTAIN);
        filter.addAction(ACTION_FROM_MAINTAIN_PULSE);
        filter.addAction(ACTION_FROM_MAINTAIN_DRAINAGE_TIME);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public int outGoods(int box, int road) {
        Log.d(TAG, "调用outGoods");

        if(!isSaleStatus||road<=0){
            return -1;
        }

//        if (road == 1) {
//            waterGodProtocol.setVenderAction(WaterGodProtocol.WATER_GOD_OUT_GOODS_1_LITRE);
//        } else if (road == 2) {
//            waterGodProtocol.setVenderAction(WaterGodProtocol.WATER_GOD_OUT_GOODS_2_LITRE);
//        } else if (road == 3) {
//            waterGodProtocol.setVenderAction(WaterGodProtocol.WATER_GOD_OUT_GOODS_3_LITRE);
//        } else if (road == 4) {
//            waterGodProtocol.setVenderAction(WaterGodProtocol.WATER_GOD_OUT_GOODS_4_LITRE);
//        } else if (road == 5) {
//            waterGodProtocol.setVenderAction(WaterGodProtocol.WATER_GOD_OUT_GOODS_5_LITRE);
//        }
        waterGodProtocol.setVenderAction(DataUtils.hexString2Bytes(Integer.toHexString(road))[0]);
        mBoxId = box;//对于水神没有作用，只是兼容VMC


        return 0;
    }




    @Override
    public String getVendingMachineId() {
        Log.d(TAG, "调用getVendingMachineId");

        //这是发送获取ID，从机器获取机器ID后会发送广播
        waterGodProtocol.getInfo(WaterGodProtocol.WATER_GOD_GET_INFO_MACHINE_ID);
        //机器号默认00000000
        mMachineId = mMachineId == null ? "00000000" : mMachineId;
        return mMachineId;
    }

    @Override
    public int setVendingMachineId(String machineId) {
        Log.d(TAG, "调用setVendingMachineId");

        //机器号要8位
        if (null == machineId) {
            return -1;
        }
        if (machineId.length() != 8) {
            return -1;
        }

        return waterGodProtocol.setMachineID(machineId.getBytes());

    }




    @Override
    public String getVmcRunningStates() {
        Log.d(TAG, "调用getVmcRunningStates");

        VmcRunStatesWg vmcRunStatesWg = new VmcRunStatesWg();

        vmcRunStatesWg.liquidState = mLiquidState;

        vmcRunStatesWg.waterPressureState = mWaterPressureState;

        vmcRunStatesWg.machineVersionState = mMachineVersion;

        vmcRunStatesWg.appVersionState = getAppVersionName(mContext);

        vmcRunStatesWg.phState = getMaintainInfoByKey(MAINTAIN_PH_KEY);

        vmcRunStatesWg.accState = getMaintainInfoByKey(MAINTAIN_ACC_KEY);

        vmcRunStatesWg.waterDegreeState = getMaintainInfoByKey(MAINTAIN_WATER_DEGREE_KEY);

        vmcRunStatesWg.doorState = mDoorState;

        vmcRunStatesWg.networkState = String.valueOf(getNetState(mContext));

        vmcRunStatesWg.machineIdState = mMachineId;

        Log.d(TAG, "调用getVmcRunningStates="+new Gson().toJson(vmcRunStatesWg));


        return new Gson().toJson(vmcRunStatesWg);
    }


    @Override
    public boolean isConnectError() {
        return isSerialError;
    }



    @Override
    public int setFlowController(int waterFlow) {
        //0为成功 -1为失败
        if(waterFlow<0){
            return -1;
        }
        mPulse = waterFlow;
        if(mPumpTime==0){
            waterGodProtocol.getInfo(WaterGodProtocol.WATER_GOD_GET_INFO_PULSE);
            mPumpTime = 4;
        }
        WaterGodUtils.setPulseAndPumpTimeConfig(mContext,mPulse,mPumpTime);
        Log.d(TAG,"setFlowController mPulse="+mPulse+" mPumpTime="+mPumpTime);
       return waterGodProtocol.setFlowControler(hexString2Bytes(Integer.toHexString(waterFlow)));
    }

    @Override
    public int setMaxLitreAndTime(int Litre, int waterTime) {

        Log.d(TAG, "调用setMaxLitreAndTime");
        /**
         * Litre 1字节
         * waterTime 1字节
         * 所以最大为127
         */
        if(Litre<0||waterTime<0||Litre>127||waterTime>127){
            return -1;
        }
        return waterGodProtocol.setMaxLitreAndTime(Byte.parseByte(String.valueOf(Litre)), Byte.parseByte(String.valueOf(waterTime)));
    }

    @Override
    public int setPumpTime(int pumpTime) {
        //范围为0~10s
        if(pumpTime<0||pumpTime>10){
            return -1;
        }
        mPumpTime = pumpTime;
        if(mPulse == 0){
            waterGodProtocol.getInfo(WaterGodProtocol.WATER_GOD_GET_INFO_DRAINAGE_WATER_WAIT_TIME);
            mPulse = 350;
        }
        WaterGodUtils.setPulseAndPumpTimeConfig(mContext,mPulse,mPumpTime);
        Log.d(TAG,"setPumpTime mPulse="+mPulse+" mPumpTime="+mPumpTime);
        return waterGodProtocol.setPumptime(pumpTime);
    }

    /**
     * 获取维保的sp数据
     *
     * @param key 维保存入的key
     *
     * @return 数据
     */
    private String getMaintainInfoByKey(String key) {
        String ret = "";
        try {
            Context context = mContext.createPackageContext("com.want.maintain", Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sharedPreferences = context.getSharedPreferences(MAINTAIN_SP_NAME, MODE_MULTI_PROCESS);
            ret = sharedPreferences.getString(key, "");
            Log.d(TAG, "sp数据:" + ret);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }



    @Override
    public String getBrand() {
        return "WaterGod";
    }

    @Override
    public boolean isLackOf50Cent() {
        return false;
    }

    @Override
    public boolean isLackOf100Cent() {
        return false;
    }

    @Override
    public boolean isDoorOpen() {
        return mDoorState.equals("01");
    }

    @Override
    public boolean isDriveError() {
        return false;
    }

    @Override
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {

    }

    @Override
    public void cashFinish() {
        // No cash
    }

    @Override
    public int getProcessIdByRealId(int realId) {
        return 0;
    }

    @Override
    public void selectProduct(int boxId, int roadId) {

    }

    @Override
    public void cancelDeal() {

    }

    @Override
    public int getStockByRoad(int box, int road) {
        return 0;
    }

    @Override
    public void cashInit() {
        // No cash
    }

    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {
        // No setProduct
    }

    @Override
    public void setProducts(List<VMCStackProduct> list) {
        // No setProducts
    }

    @Override
    public int outGoodsByCash(int box, int road, int price) {
        // No Cash
        return 0;
    }
}
