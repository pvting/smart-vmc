package com.want.vmc_impl_aucma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vmc.core.log;
import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.machine.core.model.VMCStack;
import vmc.machine.core.model.VMCStackProduct;
import vmc.machine.core.model.VmcRunStates;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b> 17/5/11<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b>
 * 澳柯玛售货机控制层实现
 * <br>
 */
public class AucmaImpl implements IVMCController {
    //澳柯玛逻辑处理
    private static final String TAG = "AucmaVmc";

    private Context mContext;

    private static final int CASH = 1;
    private static final int NOCASH = 2;

    private static final int FOOD_BOXID = 9;
    private static final int DRINK_BOXID = 11;
    private static final int AUCMA_FOOD_BOXID = 1;
    private static final int AUCMA_DRINK_BOXID = 0;

    private static final int EVENT_OUTGOODS_RESULT = 0xA3;
    private static final int EVENT_CASH_CHANGED = 0xB7;
    private static final int EVENT_DOOR_STATE_OPEN = 0xB1;
    private static final int EVENT_DOOR_STATE_CLOSE = 0xB6;
    private static final int EVENT_INIT_FINISH = 0xA5;
    private static final int EVENT_SERIALPORTCONNECT_STATE = 0xC1;
    private static final int EVENT_SET_ROAD_SUCCESS = 0xB4;
    private static final int EVENT_SET_ROAD_FAILED = 0xB5;
    private static final int EVENT_ROAD_ERROR = 0xA2;
    private static final int EVENT_SYSTEM_ERROR = 0xA1;
    private static final int EVENT_ROAD_INFO = 0xA6;
    private static final int EVENT_SYSTEM_STATE = 0xA4;
    private static final int EVENT_ROAD_SELLABLE = 0xA9;

    //本地现金（以分为单位）
    private int localCash;
    private boolean mIsDoorOpen;
    private boolean mIsLackOf50Cent;
    private boolean mIsLackOf100Cent;
    private boolean mSerialportConnectError;
    private boolean mIsWritePriceSuccess;


    //状态机 0:空闲中 1.现金支付中 2.出货中
    private int stateMachine;
    private final int IDLE = 0;
    private final int CASHING = 1;
    private final int OUTING = 2;
    private Timer timer;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int eventId = (int) msg.obj;
            switch (eventId) {
                case EVENT_OUTGOODS_RESULT:
                    String outgoodsResult = bytes2HexString(getVenderOutReport());
                    log.i(TAG, "出货报告：" + outgoodsResult);//log
                    if (outgoodsResult.length() == 96) {
                        //出货成功
                        String outgoodsResultFlag = outgoodsResult.substring(52, 54);
                        String outgoodsBoxId = outgoodsResult.substring(2, 4);
                        String outgoodsRoadId = outgoodsResult.substring(4, 6);
                        int boxId = Integer.parseInt(outgoodsBoxId);
                        int roadId = -1;
                        if (boxId == 0) {
                            roadId = Integer.parseInt(outgoodsRoadId, 16);
                            boxId = 11;
                        } else if (boxId == 1) {
                            roadId = Integer.parseInt(outgoodsRoadId);
                            boxId = 9;
                        }
                        int flag = Integer.parseInt(outgoodsResultFlag, 16);
                        if (flag == 0) {
                            //出货成功
                            sendOutGoodsState(boxId, roadId, true);
                            GetMachineStatus();
                        } else {
                            //出货失败
                            sendOutGoodsState(boxId, roadId, false);
                            GetChannelError((char)1);
                        }
                    } else {
                        log.e(TAG, "VMC出货报告长度异常");
                    }
                    changeMachineState(IDLE);
                    break;
                case EVENT_CASH_CHANGED:
                    log.i(TAG, "收到现金广播，现金额度变化");
                    String cashStr = bytes2HexString(getTotalPrice());
                    localCash = Integer.parseInt(cashStr, 16) >> 8;
                    Intent intentReceiveMoney = new Intent();
                    intentReceiveMoney.setAction(VMCAction.VMC_TO_BLL_RECEIVE_MONEY);
                    intentReceiveMoney.putExtra("localCash", localCash);
                    log.i(TAG, "已投入金额：" + localCash);
                    mContext.sendBroadcast(intentReceiveMoney);
                    if (localCash == 0) {
                        //更新状态
                        changeMachineState(IDLE);
                    } else {
                        changeMachineState(CASHING);
                    }
                    break;
                case EVENT_DOOR_STATE_OPEN:
                    //门状态
                    log.i(TAG, "收到事件，门已打开");
                    Intent doorIntent = new Intent();
                    doorIntent.setAction(VMCAction.VMC_TO_BLL_DOOR_STATE);
                    doorIntent.putExtra("doorState", true);
                    mContext.sendBroadcast(doorIntent);
                    mIsDoorOpen = true;

                    sendClearRoadErrorMessage();
                    break;
                case EVENT_DOOR_STATE_CLOSE:
                    //门状态
                    log.i(TAG, "收到事件，门关闭");
                    Intent doorIntent_ = new Intent();
                    doorIntent_.setAction(VMCAction.VMC_TO_BLL_DOOR_STATE);
                    doorIntent_.putExtra("doorState", false);
                    mContext.sendBroadcast(doorIntent_);
                    mIsDoorOpen = false;
                    //更新状态
                    GetMachineStatus();
                    break;
                case EVENT_INIT_FINISH:
                    //1.初始化完成通知上层
                    //延时10s发送初始化成功，防止售货机上层接受不到
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            Intent initIntent = new Intent();
                            initIntent.setAction(VMCAction.VMC_TO_BLL_INIT_FINISH);
                            initIntent.putExtra("initState", true);
                            mContext.sendBroadcast(initIntent);
                        }
                    };
                    timer.schedule(task, 5000);
                    //2.启动完成自动清除货道故障
                    sendClearRoadErrorMessage();
                    //3.保存住machineId
                    //78016226000001010038000000000000004D00
                    String str = bytes2HexString(getVersion());
                    if (str.length() == 38) {
                        log.i(TAG, "初始化成功");
                        //把machineId存本地
                        String machineId = "7000" + str.substring(8, 12);
                        if (machineId.length() != 8 ||
                            "00000000".equals("machineIdStr")) {
                            log.e(TAG, "machineIdStr出错:" + machineId);
                            return;
                        }
                        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
                        String code = sp.getString("machineId", "");
                        if (!TextUtils.equals(code, machineId)) {
                            saveToSp("machineId", machineId);
                        }
                    }
                    break;
                case EVENT_ROAD_INFO:
                    log.i(TAG, "货道配置信息:" + bytes2HexString(getChannelSetInfo()));
                    break;
                case EVENT_ROAD_SELLABLE:
                    byte[] sellableRoadsByte = getInventoryInfo();
                    log.i(TAG, "货道有无货信息:" + bytes2HexString(sellableRoadsByte));
                    if(sellableRoadsByte.length == 14){
                        byte[] sellableRoads = new byte[2];
                        for (int i = 0; i < sellableRoads.length; i++) {
                            sellableRoads[i] = sellableRoadsByte[i+3];
                        }
                        String sellableRoadsBinary = bytes2Binary(sellableRoads);
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int i = 0; i < 32; i++) {
                            list.add(1);
                        }
                        for (int i = 0; i < sellableRoadsBinary.length(); i++) {
                            int j = "1".equals(sellableRoadsBinary.substring(i,i+1)) ? 0:1;
                            int index = i + 9;
                            list.add(index,j);
                        }
                        log.i(TAG,"货道可销售to bll:"+list.toString());
                        //发送可售货道列表
                        Intent sellableIntent = new Intent();
                        sellableIntent.setAction(VMCAction.VMC_TO_BLL_SELLABLE_ROADS);
                        sellableIntent.putExtra("sellableroads_drink",list);
                        mContext.sendBroadcast(sellableIntent);
                    }
                    break;
                case EVENT_SYSTEM_ERROR:
                    log.i(TAG, "系统故障:" + bytes2HexString(getSystemFailureInfo()));
                    break;
                case EVENT_ROAD_ERROR:
                    //7A 01 00000000 00000000 8600
                    byte[] errorRoadsByte = getChannelFailureInfo();
                    log.i(TAG, "货道故障:" + bytes2HexString(errorRoadsByte));
                    if(errorRoadsByte.length == 12){
                        byte[] errorRoads = new byte[8];
                        for (int i = 0; i < errorRoads.length; i++) {
                            errorRoads[i] = errorRoadsByte[i+2];
                        }
                        String errorRoadsBinary = bytes2Binary(errorRoads);
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int i = 0; i < 100; i++) {
                            list.add(1);
                        }
                        for (int i = 0; i < errorRoadsBinary.length(); i++) {
                            int j = "1".equals(errorRoadsBinary.substring(i,i+1)) ? 1:0;
                            int index = (i>>3)*10 + i%8 + 11;
                            list.add(index,j);
                        }
                        log.i(TAG,"货道故障to bll:"+list.toString());
                        //发送可售货道列表
                        Intent sellableIntent = new Intent();
                        sellableIntent.setAction(VMCAction.VMC_TO_BLL_SELLABLE_ROADS);
                        sellableIntent.putExtra("sellableroads_food",list);
                        mContext.sendBroadcast(sellableIntent);
                    }
                    break;
                case EVENT_SYSTEM_STATE:
                    //7D000000 00000000 00000000 0A0A00001801198900000000000000000000000A0F001E241D1C00060A33004F00
                    //7D000000 00000000 00000000 1A120000 1801198900000000000000000000000A0F001E241D1C00060A33006700
                    byte[] states = getVenderStatus();
                    log.i(TAG, "状态信息:" + bytes2HexString(getVenderStatus()));
                    if (states.length == 45) {
                        if (states[12] >= 10) {
                            mIsLackOf100Cent = false;
                        } else {
                            mIsLackOf100Cent = true;
                        }

                        if (states[13] >= 10) {
                            mIsLackOf50Cent = false;
                        } else {
                            mIsLackOf50Cent = true;
                        }
                    }
                    log.i(TAG, "是否缺币(1元)：" + mIsLackOf100Cent);
                    log.i(TAG, "是否缺币(5角)：" + mIsLackOf50Cent);
                    break;
                case EVENT_SET_ROAD_SUCCESS:
                    log.i(TAG, "writeProduct 价格设置成功");
                    mIsWritePriceSuccess = true;
                    break;
                case EVENT_SET_ROAD_FAILED:
                    log.i(TAG, "writeProduct 价格设置失败");
                    mIsWritePriceSuccess = false;
                    break;
                case EVENT_SERIALPORTCONNECT_STATE:
                    String connState = bytes2HexString(getCommStatusRpt());
                    if (connState.length() == 6) {
                        String connFlag = connState.substring(2, 4);
                        if ("30".equals(connFlag)) {
                            mSerialportConnectError = false;
                        } else if ("31".equals(connFlag)) {
                            mSerialportConnectError = true;
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 发送清空货道故障消息
     */
    private void sendClearRoadErrorMessage() {
        Intent doorIntent = new Intent();
        doorIntent.setAction(VMCAction.VMC_TO_BLL_CLEAR_ROAD_ERROR);
        mContext.sendBroadcast(doorIntent);
    }

    //byte转16进制字符串函数
    private static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    //byte转2进制
    public static String bytes2Binary(byte[] data) {
        String str = "";
        for (int i = 0; i < data.length; i++) {
            String cache = Integer.toBinaryString(data[i] & 0xFF);
            cache = "00000000" + cache;
            cache = cache.substring(cache.length() - 8, cache.length());
            str = cache + str;
        }
        StringBuilder sb = new StringBuilder(str);
        return sb.reverse().toString();
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    @Override
    public void init(Context context) {
        mContext = context;

        //如果没有配置文件，则复制assets下的config.ini到set文件夹下
        FileCreate fileCreate = new FileCreate();
        fileCreate.createFile(context);

        System.loadLibrary("Aucma");
        //启动协议
        new Thread() {
            @Override
            public void run() {
                //启动协议
                SystemClock.sleep(5000);
                startProtocol();
            }
        }.start();
        //启动事件读取
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(5000);
                while (true) {
                    Message msg = Message.obtain();
                    int eventId = getEvent();
                    log.i(TAG, "收到事件EventId:" + eventId);
                    msg.obj = eventId;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }


    /**
     * 保存到sp文件
     *
     * @param key          key
     * @param machineIdStr value
     */
    private void saveToSp(String key, String machineIdStr) {
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, machineIdStr);
        edit.apply();
    }


    /**
     * 调用出货
     *
     * @param roadId 货道号
     * @param boxId  货柜号
     *               return 0:正常 1：异常
     */
    @Override
    public int outGoods(int boxId, int roadId) {
        if (boxId == FOOD_BOXID) {
            boxId = AUCMA_FOOD_BOXID;
            roadId = (roadId / 10) * 16 + roadId % 10;
        } else if (boxId == DRINK_BOXID) {
            boxId = AUCMA_DRINK_BOXID;
        }
        VenderOutAction((char) NOCASH, (char) boxId, (char) roadId);
        changeMachineState(OUTING);
        return 0;
    }

    /**
     * 现金出货
     *
     * @param roadId 货道好
     * @param price  价格
     */
    @Override
    public int outGoodsByCash(int boxId, int roadId, int price) {
        if (boxId == FOOD_BOXID) {
            boxId = AUCMA_FOOD_BOXID;
            roadId = (roadId / 10) * 16 + roadId % 10;
        } else if (boxId == DRINK_BOXID) {
            boxId = AUCMA_DRINK_BOXID;
        }

        VenderOutAction((char) CASH, (char) boxId, (char) roadId);
        changeMachineState(OUTING);
        return 0;
    }

    /**
     * 发送出货结果广播
     *
     * @param boxId  货柜号
     * @param roadId 货道号
     * @param state  出货状态
     */
    private void sendOutGoodsState(int boxId, int roadId, boolean state) {
        Intent orderIntent = new Intent();
        orderIntent.setAction(VMCAction.VMC_TO_BLL_OUTGOODS);
        orderIntent.putExtra("stack_no", roadId);
        orderIntent.putExtra("box_no", boxId);
        orderIntent.putExtra("outGoodsState", state);
        log.i(TAG, "roadId：" + roadId + ",出货结果" + state);
        mContext.sendBroadcast(orderIntent);
    }

    /**
     * 获取机器编号
     *
     * @return 返回机器编号，注意要在初始化完成后获取
     */
    @Override
    public String getVendingMachineId() {
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString("machineId", "00000000");
    }

    /**
     * 设置商品列表
     *
     * @param list 设置各个商品到售货机
     */
    @Override
    public void setProducts(final List<VMCStackProduct> list) {
        //设置商品成功通知
        new Thread() {
            @Override
            public void run() {
                int failedCount = 0;
                for (int i = 0; i < 3; i++) {
                    failedCount = 0;
                    for (int j = 0; j < list.size(); j++) {
                        setProduct(list.get(j).boxId, list.get(j).roadId, list.get(j).price);
                        SystemClock.sleep(3000);
                        if (!mIsWritePriceSuccess) {
                            failedCount++;
                        } else {
                            mIsWritePriceSuccess = false;
                        }
                    }
                    if (failedCount == 0) {
                        break;
                    }
                }
                log.i(TAG, "writeProduct failedCount:" + failedCount);
                if (failedCount == 0) {
                    Intent intent = new Intent();
                    intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
                    intent.putExtra("setProductResult", true);
                    mContext.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
                    intent.putExtra("setProductResult", false);
                    mContext.sendBroadcast(intent);
                }

            }
        }.start();


    }


    public void setProduct(int boxId, int roadId, int price) {
        log.i(TAG, "writeProduct boxId:" + boxId + "_roadId:" + roadId + "_price:" + price);
        if (boxId == FOOD_BOXID) {
            boxId = AUCMA_FOOD_BOXID;
            roadId = (roadId / 10) * 16 + roadId % 10;
        } else if (boxId == DRINK_BOXID) {
            boxId = AUCMA_DRINK_BOXID;
        }
        SetPrice((char) boxId, (char) roadId, price);
    }

    /**
     * 获取设备状态
     *
     * @return 状态信息
     */
    @Override
    public String getVmcRunningStates() {
        VmcRunStates vmcRunStates = new VmcRunStates();
        vmcRunStates.isLackOf50Cent = mIsLackOf50Cent;
        vmcRunStates.isLackOf100Cent = mIsLackOf100Cent;
        vmcRunStates.isSoldOut = isStockEmpty();
        vmcRunStates.isVMCDisconnected = mSerialportConnectError;
        vmcRunStates.isDoorOpened = mIsDoorOpen;
        return new Gson().toJson(vmcRunStates);
    }

    /**
     * 货道售空状态
     *
     * @return 是否为空
     */
    private boolean isStockEmpty() {
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
        boolean result = false;
        for (String key : sBLLProductsByRoadMap.keySet()) {
            VMCStack vmcStackProduct = sBLLProductsByRoadMap.get(key);
            if (getStockByRoad(vmcStackProduct.getBoxNoInt(), vmcStackProduct.getStackNoInt()) == 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 通过货道号获取库存
     *
     * @param roadId 货道号
     *
     * @return 库存数量
     */
    @Override
    public int getStockByRoad(int boxId, int roadId) {
        return 1;
    }


    @Override
    public String getBrand() {
        return "Aucma";
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

    @Override
    public boolean isLackOf50Cent() {
        return mIsLackOf50Cent;
    }

    @Override
    public boolean isLackOf100Cent() {
        return mIsLackOf100Cent;
    }

    @Override
    public boolean isDoorOpen() {
        return mIsDoorOpen;
    }

    @Override
    public boolean isDriveError() {
        return false;
    }

    @Override
    public boolean isConnectError() {
        return mSerialportConnectError;
    }

    //以下方法，澳柯玛售货机暂不使用。
    @Override
    public void selectProduct(int boxId, int roadId) {
    }

    @Override
    public int setVendingMachineId(String machineId) {
        return 0;
    }

    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {

    }

    @Override
    public void cashInit() {

    }

    @Override
    public void cashFinish() {

    }

    @Override
    public int getProcessIdByRealId(int realId) {
        return 0;
    }

    @Override
    public void cancelDeal() {

    }

    @Override
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {

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

    //native方法一览

    public native int getEvent();

    public native int startProtocol();

    public native int VenderOutRequest(char cSetAddress, char cSetChannelAddress);

    public native int VenderOutAction(char cSetPaytype, char cSetAddress, char cSetChannelAddress);

    public native byte[] getVersion();

    public native byte[] getSystemFailureInfo();

    public native byte[] getChannelFailureInfo();

    public native byte[] getChannelSetInfo();

    public native byte[] getInventoryInfo();

    public native byte[] getVenderOutReport();

    public native byte[] getVenderStatus();

    public native int SetPrice(char cSetAddress, char cSetChannelAddress, int nSetPrice);

    public native int addproducts(char cSetAddress, char cSetChannelAddress, int nSetNumber);

    public native int SetIsOpen(char cSetIsOpen);

    public native void getCommStatus();

    public native byte[] getCommStatusRpt();

    public native int Refund(int nSetPrice);

    public native byte[] getTotalPrice();

    public native void GetChannelError(char cSetAddress);

    public native int GetInventory(char cSetAddress);

    public native int GetMachineStatus();

    public native int GetMachineVersion();

    public native int GetSystemFaultInfo();
}
