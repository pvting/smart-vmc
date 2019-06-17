package vmc.machine.impl.boueki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import vmc.core.log;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.serialport.VMCProtocol;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b> 9/23/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 久保田通讯协议实现
 * <br>
 */
class BouekiProtocol extends VMCProtocol {

    private static final int LOG_MASK = 0xFFFF;
    private static final int LOG_GLOABL_ON = 0x8000;
    private static final int LOG_OFF = 0;

    /** 指令校验 */
    private static final int LOG_SWITCH_CMD_CHECK = 0x4000;

    private static final int LOG_SWTICH_B0 = 0x1;
    private static final int LOG_SWTICH_B1 = 0x2;
    private static final int LOG_SWTICH_B2 = 0x4;
    private static final int LOG_SWTICH_B3 = 0x6;
    private static final int LOG_SWTICH_B4 = 0x8;

    /** 指令类型 */
    private static final int LOG_TYPE_DEFAULT = 0;
    private static final int LOG_TYPE_B0 = 1;
    private static final int LOG_TYPE_B1 = 2;
    private static final int LOG_TYPE_B2 = 3;
    private static final int LOG_TYPE_B3 = 4;
    private static final int LOG_TYPE_B4 = 5;
    private static final int LOG_TYPE_CMD_CHECK = 6;

    private static final String TAG = "Kubota";
    //指令开始
    private static final String STX = "f2";
    //指令结束
    private static final String ETX = "ff";
    //呼吸回复
    private static final String ACK1 = "11";
    //待机状态, 需回复ACK1
    private static final String B0 = "b04f";
    //一揽请求
    private static final String B1 = "b14e";
    //输入请求, 接受子控指令
    private static final String B2 = "b24d";
    //输入再请求
    private static final String B4 = "b44b";
    //禁止卡支付
    private static final String CMD_SEND_ACK = "11F20F084D11560007000001025B01025A000BFF";
    int count = 0;
    private int LOG_BITS = LOG_SWITCH_CMD_CHECK // 指令校验开
                           & LOG_SWTICH_B0 // BO开
                           & LOG_SWTICH_B1 // B1开
                           & LOG_SWTICH_B2 // B2开
                           & LOG_SWTICH_B3 // B3开
                           & LOG_SWTICH_B4 // B4开
                           & LOG_GLOABL_ON;// 全局开

    private StringBuilder mDataCachedBuilder = new StringBuilder();
    // 有效指令
    private String mCommand = "";

    private Context mContext;

    private OnOutGoodsOK mOnOutGoodsOK;
    private String mPrice = "0000";
    private String mExedcmd;
    private ArrayList<String> execCommands = new ArrayList<>();
    private boolean log11 = true;
    private boolean isOutGoodsOK = false;
    private boolean cardPay5000 = false;
    private boolean mSelectMask = false;

//    private boolean hasCancel;

    public static final String MECHINEID = "mechineId";
    public static final String RUNSTATE = "runState";
    public static final String TEMPERATURE = "temperature";
    public static final String TROUBLE = "trouble";
    public static final String STOCK = "stock";
    public static final String TIMESTAMP = "timestamp";

    BouekiProtocol(Context context) {
        this.mContext = context;
    }

    /**
     * 16进制转换成二进制
     *
     * @param str 售空源数据
     *
     * @return 目标数据
     */
    private static String hex2binary(String str) {
        if (str == null || str.length() != 8) {
            return "";
        }
        String str1 = str.substring(6, 8) + str.substring(4, 6) + str.substring(2, 4) + str.substring(0, 2);

        char[] hexs = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        String[] binarys = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
                            "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};

        StringBuilder result = new StringBuilder();


        char[] hexChar = str1.toCharArray();
        for (int i = 0; i < hexChar.length; i++) {
            for (int j = 0; j < hexs.length; j++) {
                if (hexs[j] == hexChar[i]) {
                    result.append(binarys[j]);
                    break;
                }
            }
        }
        return result.reverse().toString();
    }

    @Override
    public void onDataReceived(byte[] data, int size) {
        StringUtils.toHexString(data, size, mDataCachedBuilder);
        final int length = mDataCachedBuilder.length();
        // 过滤无效指令
        if (length < 2) {
            return;
        }
        if (STX.charAt(0) != mDataCachedBuilder.charAt(0)
            && STX.charAt(1) != mDataCachedBuilder.charAt(1)) {
            resetCommand();
            return;
        }

        // 不足八位, 继续接收数据
        if (length < 8) {
            return;
        }
        // 第一位
        char chs1;
        // 第二位
        char chs2;
        // 最后一位
        char che1 = mDataCachedBuilder.charAt(length - 1);
        // 最后二位
        char che2 = mDataCachedBuilder.charAt(length - 2);

        chs1 = mDataCachedBuilder.charAt(0);
        chs2 = mDataCachedBuilder.charAt(1);
        // 如果STX和ETX都匹配, 则当前mCommand可能是一条有效指令
        if (ETX.charAt(0) == che2 &&
            ETX.charAt(1) == che1 &&
            STX.charAt(0) == chs1 &&
            STX.charAt(1) == chs2) {
            final String cmd = mDataCachedBuilder.toString();
            if (!checkCommand(cmd)) {
                return;
            }

            mCommand = cmd;
            mDataCachedBuilder.setLength(0);
            onCommandReceived(mCommand);
            mCommand = "";
        }
    }

    /**
     * <pre>
     * 检查指令是否完整。机器返回的数据格式也符合指令框架的,即: STX开始, ETX结束。
     * 当返回数据时, 需要对数据中包含的FF进行处理, 以使指令完整。
     *
     * 数据框架:
     *
     * STX | BC | BC1 | DC1 | 数据 | BCn | DCn | 数据 | FCC | ETX |
     *
     * 其中,
     * 1. BC是所有数据指令和数据的byte数量;
     * 2. DC1 ~ DCn是数据指令(即: 数据的种类, 如: C1);
     * 3. FCC是Frame CHeck Code;
     *
     * 根据BC的值, 以及BC1 ~ BCn和DC1 ~ DCn的长度来进行数据完整性的判断。如果数据
     * 是完整的, 则数据指令完整。
     * </pre>
     *
     * @param command
     *
     * @return true, 这是一个完整指令; false, 这是一个不完整指令
     */
    private boolean checkCommand(final String command) {
        final int length = command.length();

        // 指令长度为8位时, 可以确定这是一个完整的指令
        if (length == 8) {
            return true;
        }

        // 指令长度小于等于10时,可以确定这是一个非完整的指令
        if (length <= 10) {
            printLog("checkCommand, not a full command: " + command, LOG_TYPE_CMD_CHECK);
            return false;
        }

        // 检查机器是否刚刚重启
        if (command.contains("f2b04fff")) {
            printLog("checkCommand, command contains f2b04fff: " + command, LOG_TYPE_CMD_CHECK);
            resetCommand();
            return false;
        }

        // 出现B2指令的不会在这里出现, 如果在这里出现了, 指令可能受到干扰, 需要重置
        if (command.contains("f2b24dff")) {
            printLog("checkCommand, command contains f2b24dff: " + command, LOG_TYPE_CMD_CHECK);
            resetCommand();
            return false;
        }

        // 获取数据指令和数据的byte数
        final String BC = command.substring(2, 4);
        final int total = Integer.parseInt(BC, 16);

        final String BCnDCn = command.substring(4, length - 4);
        final int count = BCnDCn.length() / 2;

        // BC的值与BCnDCn的byte长度一致时, 这是一个完整的指令
        if (total == count) {
            return true;
        }

        printLog("checkCommand, command checked faild: " + command, LOG_TYPE_CMD_CHECK);
        return false;
    }

    private void resetCommand() {
        mDataCachedBuilder.setLength(0);
        mCommand = "";
    }

    /**
     * 写入串口数据
     *
     * @param data 被写数据
     */
    private void sendData(String data) {
        if (!ACK1.equals(data) && log11) {
            log.i(TAG, "write : " + data);
        }
        //保存已经发送的指令,保留一个周期
        mExedcmd = data;
        final byte[] bytes = StringUtils.toByteArray(data);
        getSerialPortController().sendData(bytes);
    }

    /**
     * 组装完整的指令
     *
     * @param cmd 简单指令
     * @return 完整命令
     */
    public String genFullCommand(String cmd) {
        String command;
        if (TextUtils.isEmpty(cmd) || cmd.length() % 2 == 1) {
            command = ACK1;
            Log.v(TAG, "gen command error:" + cmd);
        } else {
            int len = cmd.length() >> 1;
            String len1 = format2Size(Integer.toHexString(len));
            String len2 = format2Size(Integer.toHexString(len + 1));
            command = "33F2" + len2 + len1 + cmd + genXorCode(len2 + len1 + cmd) + "FF";
        }
        return command;
    }

    /**
     * 转换格式
     *
     * @param str 格式化字符串
     *
     * @return 1->01,11->11
     */
    private String format2Size(String str) {
        String cmd;
        if (TextUtils.isEmpty(str)) {
            cmd = "00";
        } else {
            str = "00" + str;
            cmd = str.substring(str.length() - 2, str.length());
        }
        return cmd;
    }

    /**
     * 获取异或校验码
     *
     * @param src 源码
     *
     * @return 校验结果
     */
    private String genXorCode(String src) {
        String str;
        if (TextUtils.isEmpty(src)) {
            str = "00";
        } else {
            int xcode = 0;
            for (int i = 0; i < src.length(); i += 2) {
                String sg = src.substring(i, i + 2);
                xcode ^= Integer.parseInt(sg, 16);
            }
            str = Integer.toHexString(xcode);
            str = format2Size(str);
        }
        return str;
    }

    /**
     * 指令执行与回复
     *
     * @param data 接收指令
     */
    private void onCommandReceived(String data) {
        if (!"f2b24dff".equals(data) && log11) {
            log.i(TAG, "read  : " + data);
        }

        final String cmd = data.substring(2, data.length() - 2);

        if (B0.equals(cmd)) {
            //1.上电
        } else if (B1.equals(cmd)) {
            //2.自己信息
            sendData(CMD_SEND_ACK);
            return;
        } else if (B4.equals(cmd)) {
            //9.异常重发
            if (!TextUtils.isEmpty(mExedcmd)) {
                sendData(mExedcmd);
                return;
            }
        } else if (B2.equals(cmd)) {
            //执行延时发送的指令
            if (execCommands.size() > 0) {
                String cmd_ = execCommands.get(0);
                if (TextUtils.isEmpty(cmd_)) {
                    cmd_ = ACK1;
                }else{
                    if(cmd_.equals("33F203025B005AFF")){
                        //保留时间戳
                        saveTimeStamp();
                    }
                }
                sendData(cmd_);
                execCommands.remove(0);
                return;
            }
        } else if ("0302500051".equals(cmd)) {
            //卡受理禁止,让给现金支付
            execCommands.add("33F203025B0359FF");
            if (cardPay5000) {
//                execCommands.add("33F203025B015BFF");
                cardPay5000 = false;
            } else {
//                execCommands.add("33F203025B0359FF");
                Intent intent = new Intent();
                intent.setAction(VMCAction.VMC_TO_BLL_RECEIVE_MONEY);
                mContext.sendBroadcast(intent);
            }
        } else if (cmd.startsWith("080250010451") && cmd.length() == 20) {
            //选货
            //修复由于价格不对导致的出货失败
            mPrice = cmd.substring(14, 18);
            String roadID = cmd.substring(12, 14);
            //用于屏蔽程序选货出现多个商品详细
            if (mSelectMask) {
                mSelectMask = false;
            } else {
                execCommands.add("33F203025B005AFF");
                //发送广播
                Intent selectIntent = new Intent();
                selectIntent.setAction(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
                selectIntent.putExtra("selectGoods_roadId", Integer.parseInt(roadID, 16));
                //久保田的货柜号写死成11
                selectIntent.putExtra("selectGoods_boxId", 11);
                mContext.sendBroadcast(selectIntent);
            }
            if(timer!=null) {
                timer.cancel();
            }
        } else if (cmd.startsWith("0605c0") && cmd.length() == 16) {
            //售空信息
            String msg = cmd.substring(6, 14);
            msg = hex2binary(msg);
            saveData(STOCK, msg);
        } else if (cmd.length() >= 20 && cmd.substring(2, 12).equals("02500105c0")) {
            //售空信息
            String msg = cmd.substring(12, 20);
            msg = hex2binary(msg);
            saveData(STOCK, msg);
            execCommands.add("33F203025B005AFF");
        } else if (cmd.length() >= 8 && "025001".equals(cmd.substring(2,8))) {
            //卡受理许可,卡支付
            execCommands.add("33F203025B005AFF");
        } else if (cmd.startsWith("0403c1")) {
            //故障信息
            String msg = cmd.substring(6, 10);
            saveData(TROUBLE, msg);
        } else if (cmd.startsWith("201fc4")) {
            //温度信息
            String msg = cmd.substring(6, 66);
            saveData(TEMPERATURE, msg);
        } else if (cmd.startsWith("0302c5")) {
            //状态管理
            String msg = cmd.substring(6, 8);
            saveData(RUNSTATE, msg);
        } else if (cmd.startsWith("0a09c6")) {
            //贩卖机编号
            String msg = cmd.substring(6, 14);
            saveData(MECHINEID, msg);
        } else if (cmd.startsWith("3002500021d0") ||
                   cmd.startsWith("2d21d0") ||
                   cmd.startsWith("3002508021d0") ||
                   cmd.startsWith("3103e101")) {
            if(cmd.startsWith("3002508021d0")){
                cardPay5000 = false;
            }
            //返回销售数据,作为判断出货成功
            if (mOnOutGoodsOK != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mOnOutGoodsOK.onSuccess();
                    }
                });
            }
            isOutGoodsOK = true;
            //抓取售货信息
            if (cmd.startsWith("2d21d0") && cmd.length() == 94 && "d5".equals(cmd.substring(72,74))) {
                int roadId = Integer.parseInt(cmd.substring(74,76),16);
//                Intent intent = new Intent();
//                intent.setAction(VMCAction.VMC_CREATE_ORDER);
//                intent.putExtra("roadId",roadId);
//                mContext.sendBroadcast(intent);
            }
        } else if (cmd.startsWith("03025080d1")) {
            if (isOutGoodsOK) {
                isOutGoodsOK = false;
//                hasCancel = false;
            }
            if(cardPay5000){
                cardPay5000 = false;
            }
            Intent intent = new Intent();
            intent.setAction(VMCAction.VMC_TO_BLL_CANCEL_DEAL);
            mContext.sendBroadcast(intent);
        } else if(cmd.startsWith("0403e1")){
            //B0出货失败
            if (mOnOutGoodsOK != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mOnOutGoodsOK.onFailed();
                    }
                });
            }
        }
        sendData(ACK1);
    }

    /**
     * 添加要执行的命令
     *
     * @param cmd 添加要执行的命令
     */
    private void execCmd(String cmd) {
        execCommands.add(cmd);
    }

    /**
     * 设置价格
     *
     * @param str 价格参数
     */
    public void setPrice(String str) {
        execCommands.add(genFullCommand(str));
    }

    /**
     * 直接出货
     *
     * @param roadId 货道号
     */
    public void outGoods(int roadId, OnOutGoodsOK onOutGoodsOK) {
        this.mOnOutGoodsOK = onOutGoodsOK;
        //先选货,再出货
        if (roadId < 31 && roadId > 0) {
            String strRoadId = "00" + Integer.toHexString(roadId);
            strRoadId = strRoadId.substring(strRoadId.length() - 2, strRoadId.length());
            execCmd(genFullCommand("5B04"));
            execCmd(genFullCommand("5B01"));
            execCmd(genFullCommand("5941" + strRoadId + "41" + mPrice + "000000000000"));
            cardPay5000 = true;
        } else {
            Log.e(TAG, "outGoods,roadId:" + roadId);
        }
    }

    /**
     * 直接出货，无需选择
     * @param roadId 货道id
     * @param onOutGoodsOK 出货回调
     */
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {
        mOnOutGoodsOK = onOutGoodsOK;
        if (roadId < 31 && roadId > 0) {
            String strRoadId = format2Size(Integer.toHexString(roadId));
            //B0出货，01为默认非现金
            execCmd(genFullCommand("B0" + strRoadId + "01"));
            log.i(TAG, "outGoods,roadId:" + roadId);
            cardPay5000 = true;
        } else {
            log.e(TAG, "B0 outGoods error,roadId:" + roadId);
        }
    }

    /**
     * 现金出货
     *
     * @param roadId 货道号
     */
    public int outGoodsByCash(int roadId) {
//        this.mOnOutGoodsOK = onOutGoodsOK;
        //先选货,再出货
        if (roadId < 1 || roadId > 30) {
            log.e(TAG, "outGoods,roadId:" + roadId);
        }
        return 0;
    }


    private Timer timer;

    /**
     * 取消交易
     */
    public void cancelDeal() {
//        hasCancel = true;
//        new Thread(){
//            @Override
//            public void run() {
////                SystemClock.sleep(4000);
//                hasCancel = false;
//            }
//        }.start();

        TimerTask task = new TimerTask() {
            public void run() {
                execCmd("33F203025B0359FF");
            }
        };
        timer = new Timer();
        timer.schedule(task, 10000);
    }

    /**
     * 选择商品
     *
     * @param roadId 商品货道号
     */
    public void selectProduct(int roadId) {
        if (roadId < 31 && roadId > 0) {
            if (timer != null) {
                timer.cancel();
            }
            String strRoadId = "00" + Integer.toHexString(roadId);
            execCmd(genFullCommand("57" + strRoadId.substring(strRoadId.length() - 2, strRoadId.length())));
            mSelectMask = true;
            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    mSelectMask = false;
                }
            }.start();
        } else {
            Log.e(TAG, "selectProduct,roadId:" + roadId);
        }
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
     * 保存时间戳到sp文件
     */
    private void saveTimeStamp() {
        SharedPreferences sp = mContext.getSharedPreferences(TIMESTAMP, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(TIMESTAMP, System.currentTimeMillis());
        edit.apply();
    }

    /**
     * 打印日志
     *
     * @param log  日志内容
     * @param type 日志类型 {@link LOG_TYPE}
     */
    private void printLog(String log, @LOG_TYPE int type) {
        // TODO: 10/16/16 加入日志分级打印
        // 日志记录已关闭
        if (LOG_GLOABL_ON != (LOG_BITS & LOG_GLOABL_ON)) {
            return;
        }

        switch (type) {
            case LOG_TYPE_B0: {
                if (!logable(LOG_SWTICH_B0)) {
                    return;
                }
            }
            case LOG_TYPE_B1: {
                if (!logable(LOG_SWTICH_B1)) {
                    return;
                }
            }
            case LOG_TYPE_B2: {
                if (!logable(LOG_SWTICH_B2)) {
                    return;
                }
            }
            case LOG_TYPE_B3: {
                if (!logable(LOG_SWTICH_B3)) {
                    return;
                }
            }
            case LOG_TYPE_B4: {
                if (!logable(LOG_SWTICH_B4)) {
                    return;
                }
            }
            case LOG_TYPE_CMD_CHECK: {
                if (!logable(LOG_SWITCH_CMD_CHECK)) {
                    return;
                }
            }
            case LOG_TYPE_DEFAULT:
            default: {
                Log.v(TAG, log);
            }
        }
    }

    private boolean logable(int swtch) {
        return (LOG_BITS & swtch) == swtch;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {LOG_TYPE_DEFAULT, LOG_TYPE_B0, LOG_TYPE_B1,
                     LOG_TYPE_B2, LOG_TYPE_B3, LOG_TYPE_B4,
                     LOG_TYPE_CMD_CHECK})
    private @interface LOG_TYPE {}

}
