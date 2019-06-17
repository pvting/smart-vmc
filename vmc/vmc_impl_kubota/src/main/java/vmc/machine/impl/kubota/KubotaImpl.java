package vmc.machine.impl.kubota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
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
 * <b>Create Date:</b> 17/3/3<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:久保田售货机</b>
 * <br>
 */
public class KubotaImpl implements IVMCController {
    private static final String TAG = "KubotaVmc";

    private Context mContext;

    private static final int CASH = 1;
    private static final int NOCASH = 2;

    private static final String STOCK = "stock";

    private static final int PUSH_BUTTON_EVENT_ID = 0x01;  //按键事件
    private static final int OUT_GOODS_SUCCESS_EVENT_ID = 0x02;  //出货成功事件
    private static final int BAN_ONLINE_TRADING_EVENT_ID = 0x04;  //线上交易禁止事件
    private static final int ALLOWS_ONLINE_TRANSACTION_EVENT_ID = 0x08;  //线上交易允许事件
    private static final int OUT_GOODS_FAIL_EVENT_ID = 0x10;  //出货失败事件
    private static final int SET_PRICE_RESULT_EVENT_ID = 0x20;  //设置单价指令执行结果事件
    private static final int TRANSACTION_END_EVENT_ID = 0x40;  //交易结束事件
    private static final int STATE_CHANGE_EVENT_ID = 0x80;  //售货机状态发生改变事件
    private static final int FAULT_MESSAGE_EVENT_ID = 0x0100;  //故障信息事件
    private static final int SERIAL_ERROR_EVENT_ID = 0x0200;  //串口通信异常事件
    private static final int GOODS_SOLD_OUT_STATE_EVENT_ID = 0x0400;  //货道售空状态事件

    private static final int BOX_ID = 11;

    //本地现金（以角为单位）
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

    private boolean mCardPayTag = false;

    //料道总数
    private int mRoadSum = 30;
    //货道可售
    private int[] mSellableRoads;
    //卡静止
    private boolean mCardCan = true;

    private ArrayList<Integer> mSellables;

    private static final String
            FILE_PATH =
            File.separator +
            "sdcard" +
            File.separator +
            "vendingMachineFile";



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int eventId = (int) msg.obj;
            switch (eventId) {
                case OUT_GOODS_SUCCESS_EVENT_ID://收到出货成功事件
                    //出货
                    log.i(TAG, "收到出货成功事件");
                    //发送出货结果
                    sendOutGoodsState(BOX_ID, getCurrentSalesColumnNo(), true);
                    changeMachineState(IDLE);
                    break;
                case OUT_GOODS_FAIL_EVENT_ID://收到出货失败事件
                    log.i(TAG, "收到出货失败事件");
                    sendOutGoodsState(BOX_ID, getCurrentSalesColumnNo(), false);
                    changeMachineState(IDLE);
                    break;
                case SET_PRICE_RESULT_EVENT_ID://收到设置价格结果事件
                    //设置商品成功通知
                    boolean setProductResult;
                    if (getSetPriceResults()) {
                        setProductResult = true;
                    } else {
                        setProductResult = false;
                    }
                    log.i(TAG, "收到设置价格结果事件：" + setProductResult);
                    Intent intent = new Intent();
                    intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
                    intent.putExtra("setProductResult", setProductResult);
                    mContext.sendBroadcast(intent);
                    break;
                case PUSH_BUTTON_EVENT_ID://收到选货事件
                    int columnId = getSelectedGoodColumnNo();
                    log.i(TAG, "收到选货事件，选择商品货道号:" + columnId);
                    //选货广播
                    if (stateMachine == OUTING) {
                        log.w(TAG, "正在出货，则不接受选货操作");
                    } else {
                        Intent intentSelectProduct = new Intent();
                        intentSelectProduct.setAction(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
                        intentSelectProduct.putExtra("selectGoods_roadId", columnId);
                        intentSelectProduct.putExtra("selectGoods_boxId", BOX_ID);
                        mContext.sendBroadcast(intentSelectProduct);
                    }
                    break;
                case TRANSACTION_END_EVENT_ID://订单结束事件
                    Intent finishIntent = new Intent();
                    finishIntent.setAction(VMCAction.VMC_TO_BLL_DEAL_FINISH);
                    mContext.sendBroadcast(finishIntent);
                    if (mCardPayTag) {
                        mCardPayTag = false;
                    }
                    break;
                case GOODS_SOLD_OUT_STATE_EVENT_ID://获取可销售货道
                    //可销售货道
                    byte[] sellable = getGoodsSoldOutState();
                    if (sellable.length == 0) {
                        break;
                    } else {
                        if (mSellableRoads == null || mSellableRoads.length != sellable.length * 8) {
                            mSellableRoads = new int[sellable.length * 8];
                        }
                        for (int i = 0; i < sellable.length; i++) {
                            String str = "00000000" + Integer.toBinaryString(sellable[i] & 0xff);
                            str = str.substring(str.length() - 8, str.length());
                            for (int j = 0; j < 8; j++) {
                                mSellableRoads[i * 8 + j] = "0".equals(str.substring(7 - j, 8 - j)) ? 1 : 0;
                            }
                        }
                        String str = "";
                        for (int i = 0; i < mSellableRoads.length; i++) {
                            str += mSellableRoads[i];
                        }
                        saveData(STOCK, str);
                    }


                    if (mSellables == null) {
                        mSellables = new ArrayList<>();
                    } else {
                        mSellables.clear();
                    }
                    for (int i = 1; i <= mRoadSum; i++) {
                        mSellables.add(1 - getStockByRoad(11, i));
                    }

                    for (int i = 0; i < mSellables.size(); i++) {
                        log.d(TAG, mSellables + "");
                    }
                    //发送可售货道列表
                    Intent sellableIntent = new Intent();
                    sellableIntent.setAction(VMCAction.VMC_TO_BLL_SELLABLE_ROADS);
                    sellableIntent.putExtra("sellableroads", mSellables);
                    mContext.sendBroadcast(sellableIntent);
                    break;
                case SERIAL_ERROR_EVENT_ID://串口异常
                    //收到事件48
                    log.i(TAG, "收到串口异常事件");
                    mSerialportConnectError = true;
                    break;
                case ALLOWS_ONLINE_TRANSACTION_EVENT_ID://卡允许
                    mCardCan = true;
                    Intent cardCanIntent = new Intent();
                    cardCanIntent.setAction(VMCAction.VMC_TO_BLL_CARD_CAN);
                    mContext.sendBroadcast(cardCanIntent);
                    log.i(TAG, "mCardCan:" + mCardCan);
                    break;
                case BAN_ONLINE_TRADING_EVENT_ID://卡禁止
                    if (mCardPayTag) {
                        mCardPayTag = false;
                        return;
                    }
                    mCardCan = false;
                    log.i(TAG, "mCardCan:" + mCardCan);
                    Intent cardBanIntent = new Intent();
                    cardBanIntent.setAction(VMCAction.VMC_TO_BLL_CARD_BAN);
                    mContext.sendBroadcast(cardBanIntent);
                    break;
                case STATE_CHANGE_EVENT_ID://运行状态等等
                    byte state = getStateData();
                    String str1 = "0000" + Integer.toBinaryString(state & 0xFF);
                    String str2 = str1.substring(str1.length() - 4, str1.length());

                    if ("1".equals(str2.substring(0, 1))) {
                        mIsLackOf50Cent = true;
                    } else {
                        mIsLackOf50Cent = false;
                    }

                    if ("1".equals(str2.substring(1, 2))) {
                        mIsLackOf100Cent = true;
                    } else {
                        mIsLackOf100Cent = false;
                    }
                    if ("1".equals(str2.substring(3, 4))) {
                        if (!mIsDoorOpen) {
                            mIsDoorOpen = true;
                            Intent doorIntent = new Intent();
                            doorIntent.setAction(VMCAction.VMC_TO_BLL_DOOR_STATE);
                            doorIntent.putExtra("doorState", true);
                            mContext.sendBroadcast(doorIntent);
                        }
                    } else {
                        if (mIsDoorOpen) {
                            mIsDoorOpen = false;
                            Intent doorIntent = new Intent();
                            doorIntent.setAction(VMCAction.VMC_TO_BLL_DOOR_STATE);
                            doorIntent.putExtra("doorState", false);
                            mContext.sendBroadcast(doorIntent);
                        }
                    }
                    break;
            }

        }
    };

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
        //添加配置
        setConfig();
        //启动协议
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                byte b[] = "/dev/ttymxc1".getBytes();
                byte[] b1 = new byte[100];
                for (int i = 0; i < 12; i++) {
                    b1[i] = b[1];
                }
                b1[12] = 0;
                startProtocol1(b1, 19200, true);
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


    }

    /**
     * 创建文件夹
     */
    private void setConfig() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            //mkdir只能创建一级目录，mkdirs可以创建多级目录
            boolean isMakeSuccess = file.mkdirs();
            if (!isMakeSuccess) {
                log.e(TAG, "dir is make fail");
                return;
            }
            log.i(TAG, "dir is make" + file.getAbsolutePath());
        }
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
        //开始出货
        if (getStockByRoad(boxId, roadId) == 0) {
            sendOutGoodsState(boxId, roadId, false);
            log.e(TAG, "该货道无货,出货失败 roadId:" + roadId);
            return 1;
        } else if (!mCardCan) {
            sendOutGoodsState(boxId, roadId, false);
            log.e(TAG, "卡静止，无法出货 roadId:" + roadId);
            return 2;
        } else {
            log.i(TAG, "开始出货 roadId:" + roadId);
            outGoodsUseB0((byte) roadId, (byte) 1);
            mCardPayTag = true;
            return 0;
        }
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
        String machineIdStr = "";
        byte[] machineId = getVendingMachineNumber();
        for (int i = 0; i < machineId.length; i++) {
            String str = "00" + Integer.toHexString(machineId[i]);
            str = str.substring(str.length() - 2, str.length());
            machineIdStr += str;
        }
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        if (machineIdStr.length() == 8) {
            log.i(TAG, "获取机器id:" + machineIdStr);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("mechineId", machineIdStr);
            edit.apply();
            log.i(TAG, "saved machineId: " + machineIdStr + "to sp file");
            return machineIdStr;
        } else {
            String savedMechineId = sp.getString("mechineId", "00000000");
            log.e(TAG, "获取机器id异常:从sp文件获取:" + savedMechineId);
            return savedMechineId;
        }

    }

    @Override
    public int setVendingMachineId(String machineId) {
        return 0;
    }

    /**
     * 设置商品列表
     *
     * @param list 设置各个商品到售货机
     */
    @Override
    public void setProducts(final List<VMCStackProduct> list) {
        log.d(TAG, "setProducts()");
        //获取当前的最大货道数量
        int maxLoad = getColumnCount();
        //给所有货道赋初值
        byte[] prices = new byte[maxLoad * 2];
        //默认价格为2元
        for (int i = 0; i < maxLoad; i++) {
            prices[i * 2] = 0;
            prices[i * 2 + 1] = 0;
        }
        if (list == null || list.size() == 0) {
            return;
        }
        log.d(TAG, "setProducts().toString=" + list.toString());
        for (int i = 0; i < list.size(); i++) {
            prices[list.get(i).roadId * 2 - 1] = (byte) (list.get(i).price / 10);
        }
        setColumnPrice(prices);
    }

    /**
     * 获取设备状态
     *
     * @return 状态信息
     */
    @Override
    public String getVmcRunningStates() {
        log.d(TAG, "getVmcRunningStates()");
        VmcRunStates vmcRunStates = new VmcRunStates();
        vmcRunStates.isLackOf50Cent = mIsLackOf50Cent;
        vmcRunStates.isLackOf100Cent = mIsLackOf100Cent;
        vmcRunStates.isSoldOut = isStockEmpty();
        vmcRunStates.isVMCDisconnected = mSerialportConnectError;
        vmcRunStates.isDoorOpened = mIsDoorOpen;
        return new Gson().toJson(vmcRunStates);
    }

    /**
     * 货道售空状态(以机器实际货道为准)
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
        log.i(TAG, "getStockByRoad boxId=" + boxId + " roadId=" + roadId);
        int count = -1;
        if (mSellableRoads == null || mSellableRoads.length == 0) {
            SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
            String stocks = sp.getString(STOCK, "000000000000000000000000000000");
            mSellableRoads = new int[stocks.length()];
            for (int i = 0; i < stocks.length(); i++) {
                mSellableRoads[i] = "0".equals(stocks.substring(i, i + 1)) ? 0 : 1;
            }
        }

        if (roadId > 0 && roadId <= mRoadSum) {
            count = mSellableRoads[roadId - 1];
        } else {
            log.e(TAG, "不存在货道号roadId:" + roadId);
        }
        return count;
    }

    @Override
    public void selectProduct(int boxId, int roadId) {
        //代码选货
        log.i(TAG, "选择商品(selectGoodsByScreen):" + roadId);
        selectGoodsByScreen((byte) roadId);
    }

    @Override
    public String getBrand() {
        return "Kubota";
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
     * 保存数据到sp文件
     *
     * @param key   key
     * @param value value
     */
    private void saveData(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
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
        if (mCardCan) {
            cancelTransaction();
        } else {
            log.e(TAG, "无法取消，已经卡停用");
        }
        mCardCan = false;

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

    @Override
    public int outGoodsByCash(int boxId, final int roadId, int price) {
        return 0;
    }

    //native 方法
    static {
        System.loadLibrary("Serial_kubota");
    }

    public native void startProtocol();

    public native void startProtocol1(byte[] portNameIn, int baudRateIn, boolean isDebug);

    public native int getEvent();

    public native short getSelectedGoodAmount();

    public native byte getSelectedGoodColumnNo();

    public native byte getCurrentSalesColumnNo();

    public native byte[] getCurrentSalesCount();

    public native byte[] getCurrentSalesAmount();

    public native boolean getSetPriceResults();

    public native void outGoods();

    public native void outGoodsUseB0ByButton(byte salesCategory);

    public native void outGoodsUseB0(byte columnNo, byte salesCategory);

    public native int getColumnCount();

    public native void setColumnPrice(byte[] price);

    public native void cancelTransaction();

    public native void selectGoodsByScreen(byte columnNo);

    public native byte[] getGoodsSoldOutState();

    public native byte getStateData();

    public native byte[] getFaultInformation();

    public native int getFaultInformationCount();

    public native byte[] getVendingMachineNumber();


}
