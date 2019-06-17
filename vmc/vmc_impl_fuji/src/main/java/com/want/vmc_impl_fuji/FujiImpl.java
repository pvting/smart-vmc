package com.want.vmc_impl_fuji;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import vmc.core.log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.machine.core.model.VMCStack;
import vmc.machine.core.model.VMCStackProduct;
import vmc.machine.core.model.VmcRunStates;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b> 17/3/3<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b>
 * 富士售货机控制层实现
 * <br>
 */
public class FujiImpl implements IVMCController {
    //富士逻辑处理
    private static final String TAG = "FujiVmc";

    private Context mContext;

    private static final int CASH = 1;
    private static final int NOCASH = 2;

    private static final int EVENT_OUTGOODS_RESULT = 229;
    private static final int EVENT_SELECT_GOODS = 13;
    private static final int EVENT_CASH_CHANGED = 24;
    private static final int EVENT_CASH_RETURN = 14;
    private static final int EVENT_DOOR_STATE_OPEN = 15;
    private static final int EVENT_DOOR_STATE_CLOSE = 47;
    private static final int EVENT_INIT_FINISH = 11;
    private static final int EVENT_TEMP_REPORT = 30;
    private static final int EVENT_MACHINEID_REPORT = 36;
    private static final int EVENT_ROAD_SELLABLE = 26;
    private static final int EVENT_VMCSTATE_REPORT = 21;
    private static final int EVENT_NAK = 161;
    private static final int EVENT_SERIALPORTCONNECT_ERROR = 48;
    private static final int EVENT_SERIALPORTCONNECT_NORMAL = 49;

    private static final int BOX_ID = 9;

    //本地现金（以角为单位）
    private int localCash;
    private boolean mIsDoorOpen;
    private boolean mIsLackOf50Cent;
    private boolean mIsLackOf100Cent;
    private boolean mSerialportConnectError;


    //状态机 0:空闲中 1.现金支付中 2.出货中
    private int stateMachine;
    private final int IDLE = 0;
    private final int CASHING = 1;
    private final int OUTING = 2;
    private Timer timer;

    private ArrayList<Integer> mSellables;



//    private boolean mIsOuttingGoods;
//    private int mOuttingRoadId;

    //料道总数
    private byte mRoadSum;
    //货道可售
    private byte[] mSellableRoads;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int eventId = (int) msg.obj;
            switch (eventId) {
                case EVENT_OUTGOODS_RESULT:
                    //出货
                    log.i(TAG, "收到出货结果事件");
                    boolean outGoodsState = false;
                    int column = -1;
                    byte[] result = getVendoutRpt();
                    if (result.length == 49) {
                        column = result[26];
                        int status = result[25];
                        if (status == 1) {
                            outGoodsState = true;
                            log.i(TAG, "收到出货结果事件:出货正常");
                        } else {
                            outGoodsState = false;
                            setRoadError(column);
                            log.e(TAG, "VMC出货失败,错误码:0x" + Integer.toHexString(status));
                        }
                    } else {
                        log.e(TAG, "VMC出货报告长度异常");
                    }
                    //发送出货结果
                    sendOutGoodsState(BOX_ID,column,outGoodsState);
//                    mIsOuttingGoods = false;
//                    mOuttingRoadId = 0;
                    changeMachineState(IDLE);
                    break;
                case EVENT_SELECT_GOODS:
                    int columnId = getSelectedColumnId();
                    log.i(TAG, "收到选货事件，选择商品货道号:" + columnId);
                    //选货广播
                    if(stateMachine == OUTING) {
                        log.w(TAG,"正在出货，则不接受选货操作");
                    }else{
                        if(getStockByRoad(0,columnId)==1) {
                            Intent intentSelectProduct = new Intent();
                            intentSelectProduct.setAction(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
                            intentSelectProduct.putExtra("selectGoods_roadId", columnId);
                            intentSelectProduct.putExtra("selectGoods_boxId", BOX_ID);
                            mContext.sendBroadcast(intentSelectProduct);
                        }else{
                            Log.w(TAG,"该商品无货，屏蔽选择");
                        }
                    }
                    break;
                case EVENT_CASH_CHANGED:
                    log.i(TAG, "收到现金广播，现金额度变化");
                    localCash = getInputCash();
                    Intent intentReceiveMoney = new Intent();
                    intentReceiveMoney.setAction(VMCAction.VMC_TO_BLL_RECEIVE_MONEY);
                    intentReceiveMoney.putExtra("localCash", localCash * 10);
                    log.i(TAG, "已投入金额：" + localCash * 10);
                    mContext.sendBroadcast(intentReceiveMoney);
                    if(localCash==0){
                        changeMachineState(IDLE);
                    }else{
                        changeMachineState(CASHING);
                    }
                    break;
                case EVENT_CASH_RETURN:
                    //退币杆
                    log.i(TAG, "收到退币杆事件，拨退币杆");
                    Intent cashIntent = new Intent();
                    cashIntent.setAction(VMCAction.VMC_TO_BLL_CANCEL_DEAL);
                    mContext.sendBroadcast(cashIntent);
                    break;
                case EVENT_DOOR_STATE_OPEN:
                    //门状态
                    log.i(TAG, "收到事件，门已打开");
                    if(!mIsDoorOpen) {
                        Intent doorIntent = new Intent();
                        doorIntent.setAction(VMCAction.VMC_TO_BLL_DOOR_STATE);
                        doorIntent.putExtra("doorState", true);
                        mContext.sendBroadcast(doorIntent);
                        mIsDoorOpen = true;
                    }
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
                    break;
                case EVENT_INIT_FINISH:
                    //初始化完成通知上层
                    //延时5s发送初始化成功，防止售货机上层接受不到
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask(){
                        @Override
                        public void run() {
                            Intent initIntent = new Intent();
                            initIntent.setAction(VMCAction.VMC_TO_BLL_INIT_FINISH);
                            initIntent.putExtra("initState", true);
                            mContext.sendBroadcast(initIntent);
                        }
                    };
                    timer.schedule(task, 5000);
                    //启动完成自动清除货道故障
                    sendClearRoadErrorMessage();
                    break;
                case EVENT_TEMP_REPORT:
                    //温度报告
//                    //底层库初始化完成，请求机器编号，与getMachineId()合用
//                    getInfo(13);
//                    //请求货道可售信息，与getVmcSoldoutRpt()合用
//                    getStatus(6);
//                    //获取设备状态信息
//                    getStatus(1);
                    break;
                case EVENT_MACHINEID_REPORT:
                    //机器Id
                    byte[] machineId = getMachineId();
                    String machineIdStr = getString(machineId);
                    //把machineId存本地
                    if (machineIdStr == null ||
                        machineIdStr.length() != 8 ||
                        "00000000".equals("machineIdStr")) {
                        log.e(TAG,"machineIdStr出错:"+machineIdStr);
                        return;
                    }
                    SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
                    String code = sp.getString("machineId","");
                    if(!TextUtils.equals(code,machineIdStr)) {
                        saveToSp("machineId", machineIdStr);
                    }
                    break;
                case EVENT_ROAD_SELLABLE:
                    //可销售货道
                    byte[] sellable = getVmcClmsalestatusRpt();
                    log.i(TAG, "Sellable roads:" + bytes2HexString(sellable));
                    if (sellable.length > 1) {
                        mRoadSum = sellable[1];
                        if (mSellableRoads == null || mSellableRoads.length != sellable.length - 2) {
                            mSellableRoads = new byte[mRoadSum];
                        }
                        if(mSellables==null){
                            mSellables = new ArrayList<>();
                        }else{
                            mSellables.clear();
                        }
                        for (int i = 0; i < mRoadSum; i++) {
                            if (i + 2 >= sellable.length) {
                                return;
                            }
                            mSellableRoads[i] = sellable[i + 2];
                            mSellables.add(mSellableRoads[i] & 0xff);
                        }
                        //发送可售货道列表
                        Intent sellableIntent = new Intent();
                        sellableIntent.setAction(VMCAction.VMC_TO_BLL_SELLABLE_ROADS);
                        sellableIntent.putExtra("sellableroads",mSellables);
                        mContext.sendBroadcast(sellableIntent);
                    }
                    break;
                case EVENT_VMCSTATE_REPORT:
                    //vmc状态信息
                    byte[] states = getVmcStatusRpt();
                    if (states.length == 33) {
                        boolean isLackOf50Cent = (states[6] != 0);
                        boolean isLackOf100Cent = (states[9] != 0);
                        mIsLackOf50Cent = isLackOf50Cent;
                        mIsLackOf100Cent = isLackOf100Cent;
                        log.i(TAG, "isLackOf50Cent:" + isLackOf50Cent);
                        log.i(TAG, "isLackOf100Cent:" + isLackOf100Cent);
                    } else {
                        log.e(TAG, "系统状态报告异常");
                    }
                    break;
                case EVENT_SERIALPORTCONNECT_ERROR:
                    //收到事件48
                    log.i(TAG,"收到串口异常事件");
                    mSerialportConnectError = true;
                    break;
                case EVENT_SERIALPORTCONNECT_NORMAL:
                    //收到事件49
                    log.i(TAG,"收到串口正常事件");
                    mSerialportConnectError = false;
                    break;
                case EVENT_NAK:
                    //处理失败
//                    if(mIsOuttingGoods){
//                        sendOutGoodsState(BOX_ID,mOuttingRoadId,false);
//                        mOuttingRoadId = 0;
//                        mIsOuttingGoods = false;
//                    }
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

        System.loadLibrary("fujiProtocol");
        //启动协议
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                startProtocol();
            }
        }.start();
        //启动事件读取
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
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
     * @param key key
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
    public int outGoods(int boxId, final int roadId) {
        //如果设备中有现金，则先出现金
        if (localCash != 0) {
            //有现金无法在线出货
//            sendOutGoodsState(boxId, roadId, false);
//            log.e(TAG, "有现金无法在线出货 localCash:" + localCash + "角");
//            log.e(TAG, "该货道故障,出货失败 roadId:" + roadId);
            log.w(TAG, "在线出货，退出多余的钱币");
            returnCoin();
        }
        //开始出货
        if (getStockByRoad(boxId, roadId) == 0) {
            sendOutGoodsState(boxId, roadId, false);
            log.e(TAG, "该货道故障,出货失败 roadId:" + roadId);
            return 1;
        } else {
            new Thread(){
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    log.i(TAG,"开始在线出货roadId:"+roadId);
                    vendoutIndAction(NOCASH, roadId);
                    changeMachineState(OUTING);
                    stateMachineBackToIdle(90);
                }
            }.start();
//            mIsOuttingGoods = true;
//            mOuttingRoadId = roadId;
        }
        return 0;
    }

    /**
     * 现金出货
     *
     * @param roadId 货道好
     * @param price  价格
     */
    @Override
    public int outGoodsByCash(int boxId, final int roadId, int price) {
        //开始出货
        log.i(TAG, "outGoodsByCash,boxId:" + boxId + ",roadId:" + roadId + ",price:" + price);
        if (getStockByRoad(boxId, roadId) == 0) {
            sendOutGoodsState(boxId, roadId, false);
            log.e(TAG, "该货道故障,出货失败 roadId:" + roadId);
            return 1;
        } else {
            new Thread(){
                @Override
                public void run() {
                    log.i(TAG,"开始现金出货roadId:"+roadId);
                    SystemClock.sleep(1000);
                    vendoutIndAction(CASH, roadId);
                    changeMachineState(OUTING);
                    stateMachineBackToIdle(90);
                }
            }.start();
//            lightButton(roadId);
//            selectProduct(boxId,roadId);
//            mOuttingRoadId = roadId;
//            mIsOuttingGoods = true;
        }
        return 0;
    }

    /**
     * 发送出货结果广播
     * @param boxId 货柜号
     * @param roadId 货道号
     * @param state 出货状态
     */
    private void sendOutGoodsState(int boxId, int roadId, boolean state) {
        Intent orderIntent = new Intent();
        orderIntent.setAction(VMCAction.VMC_TO_BLL_OUTGOODS);
        orderIntent.putExtra("stack_no", roadId);
        orderIntent.putExtra("box_no", boxId);
        orderIntent.putExtra("outGoodsState", state);
        log.i(TAG,"roadId："+roadId + ",出货结果"+state);
        mContext.sendBroadcast(orderIntent);
        //如果出货失败，启用退币
        if(!state){
            new Thread(){
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    log.i(TAG,"发送退币命令");
                    returnCoin();
                }
            }.start();

        }
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

    // byte转String
    private String getString(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return new String(cb.array());
    }

    /**
     * 设置机器id
     * @param machineId 设备ID
     */
    @Override
    public int setVendingMachineId(String machineId) {
        if(machineId==null || machineId.length()==0){
            return -1;
        }
        byte[] machineBytes = machineId.getBytes();
        if (machineBytes.length == 8) {
            setMachineId(machineBytes);
        }
        return 0;
    }

    /**
     * 设置商品列表
     *
     * @param list 设置各个商品到售货机
     */
    @Override
    public void setProducts(final List<VMCStackProduct> list) {
        //获取当前的最大货道数量
        int maxLoad = mRoadSum;
        if (maxLoad == 0) {
            return;
        }
        //给所有货道赋初值
        byte[] prices = new byte[maxLoad * 2];
        //默认价格为2元
        for (int i = 0; i < prices.length; i = i + 2) {
            prices[i] = 0;
            prices[i + 1] = 0;
        }
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            prices[(list.get(i).roadId) * 2 - 1] = (byte) (list.get(i).price / 10);
        }
        setColumnPrice_Cash(prices);

        //设置商品成功通知
        Intent intent = new Intent();
        intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
        intent.putExtra("setProductResult", true);
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取设备状态
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
    public int getStockByRoad(int boxId, int roadId) {
        int count = -1;
        if (roadId > 0 && roadId <= mRoadSum) {
            if (mSellableRoads[roadId - 1] == 0) {
                count = 1;
            } else if (mSellableRoads[roadId - 1] == 1) {
                count = 0;
            }
        } else {
            log.e(TAG, "VMC库存报告异常");
        }
        return count;
    }

    /**
     * 自动修改故障货道为0
     * @param roadId
     */
    private void setRoadError(int roadId){
        mSellableRoads[roadId - 1] = 1;
    }

    @Override
    public void selectProduct(int boxId, int roadId) {
        //代码选货
        log.i(TAG, "选择商品(lightButton):" + roadId);
        lightButton(roadId);
    }

    @Override
    public String getBrand() {
        return "Fuji";
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
    public boolean isLackOf50Cent(){
        return mIsLackOf50Cent;
    }

    @Override
    public boolean isLackOf100Cent(){
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

    //以下方法，富士售货机暂不使用。

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
    public boolean isConnectError() {
        return mSerialportConnectError;
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


    //native 方法
//    static {
//        System.loadLibrary("fujiProtocol");
//    }

    public native void startProtocol();

    public native int getEvent();

    public native int getSelectedColumnId();

    public native short getInputCash();

    public native void charge(int charge_cost);

    public native void lightButton(int columnNo);

    public native short getColumnNum();

    public native void returnCoin();

    public native void vendoutIndRequest(int payment, int columnID);

    public native void vendoutIndAction(int payment, int columnID);

    public native byte[] getVendoutRpt();

    public native void getStatus(int m_type);

    public native void getInfo(int m_type);

    public native byte[] getVmcStatusRpt();

    public native byte[] getVmcErrorRpt();

    public native byte[] getVmcSoldoutRpt();

    public native byte[] getVmcClmsalestatusRpt();

    public native void setMachineId(byte[] machine_id);

    public native byte[] getMachineId();

    public native void setColumnPrice_Cash(byte[] Column_Price_Cash);

    public native byte[] getVmcColumnPrice_Cash();

    public native void setColumnPrice_notCash(byte[] Column_Price_notCash);

    public native byte[] getVmcColumnPrice_notCash();

    public native void setSalesMaxNum(int m_type);

    public native int getSalesMaxNum();

    public native byte[] getRpt(int rpt_type);
}
