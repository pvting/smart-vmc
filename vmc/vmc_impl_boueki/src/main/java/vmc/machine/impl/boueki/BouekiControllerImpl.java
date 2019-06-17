package vmc.machine.impl.boueki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.OnSetProductComplete;
import vmc.machine.core.model.VMCStackProduct;
import vmc.serialport.SerialPortController;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b> 9/23/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class BouekiControllerImpl implements IVMCController {

    private static final int BAUDRATE = 19200;
    private String DEVICE_NAME = "/dev/ttymxc1";
    private Context mContext;
    private BouekiProtocol protocol;

    @Override
    public void init(Context context) {
        this.mContext = context;

        protocol = new BouekiProtocol(context);
        SerialPortController controller =
                new SerialPortController(DEVICE_NAME, BAUDRATE, protocol);
        try {
            controller.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int outGoods(int box,int road) {
        protocol.outGoods(road,null);
        return 0;
    }

    @Override
    public int outGoodsByCash(int box, int road, int price) {
        return protocol.outGoodsByCash(road);
    }

    /**
     * 直接出货，无需选择
     * @param roadId 货道id
     * @param onOutGoodsOK 出货回调
     */
    @Override
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {
        protocol.outGoodsOneStep(roadId, onOutGoodsOK);
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
        return "Kubota";
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
        return false;
    }

    @Override
    public boolean isDriveError() {
        return false;
    }


    @Override
    public String getVendingMachineId() {
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        final String machineId = sp.getString("mechineId", "");
        // 久保田的机器编码默认为"00000000"
        if ("00000000".equals(machineId)) {
            return "";
        }
        return machineId;
    }

    @Override
    public int setVendingMachineId(String machineId) {
        return 0;
    }

    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {
        //(useless)
    }

    @Override
    public void setProducts(List<VMCStackProduct> list) {
        //等待初始化完成
        String str = "c3";
        // 货道默认初始价格为2元
        int[] price = {20, 20, 20, 20, 20,
                       20, 20, 20, 20, 20,
                       20, 20, 20, 20, 20,
                       20, 20, 20, 20, 20,
                       20, 20, 20, 20, 20,
                       20, 20, 20, 20, 20};
        for (int i = 0; i < list.size(); i++) {
            VMCStackProduct product = list.get(i);
            price[product.roadId - 1] = product.price / 10;
        }
        for (int i = 0; i < price.length; i++) {
            str += "00"+format2Size(Integer.toHexString(price[i]));
        }
        protocol.setPrice(str);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                onSetProductComplete.onComplete();
            }
        });
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



    @Override
    public String getVmcRunningStates() {
        VmcRunStates states = new VmcRunStates();
        SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
        String trouble = sp.getString(BouekiProtocol.TROUBLE, "0000");
        if ("0000".equals(trouble)) {
            states.faultCode = "";
        } else {
            states.faultCode = trouble;
        }
        String runState = sp.getString(BouekiProtocol.RUNSTATE, "00");
        String str1 = "0000" + Integer.toBinaryString(Integer.parseInt(runState,16));
        String str2 = str1.substring(str1.length() - 4, str1.length());
        if("1".equals(str2.substring(0, 1))){
            states.isLackOf50Cent = true;
        }
        if("1".equals(str2.substring(1, 2))){
            states.isLackOf100Cent = true;
        }
        if("1".equals(str2.substring(2, 3))){
            states.isSaleStop = true;
        }
        if("1".equals(str2.substring(3, 4))){
            states.isDoorOpened = true;
        }
        String temperature = sp.getString(BouekiProtocol.TEMPERATURE, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        states.temperature = new ArrayList<>();
        for (int i = 0; i < temperature.length(); i = i + 2) {
            states.temperature.add(Integer.parseInt(temperature.substring(i, i + 2), 16));
        }
        //售空信息
        for (int roadId = 1; roadId <= 30; roadId++) {
            if(getStockByRoad(0,roadId) == 0){
                states.isSoldOut = true;
                break;
            }
        }

        //工控主控通讯异常
        states.isVMCDisconnected = isConnectError();

        return new Gson().toJson(states);
    }

    @Override
    public int getStockByRoad(int boxId,int roadId) {
        int stock;
        if (roadId < 31 && roadId > 0) {
            SharedPreferences sp = mContext.getSharedPreferences("config", MODE_PRIVATE);
            String stocks = sp.getString(BouekiProtocol.STOCK, "");
            if (TextUtils.isEmpty(stocks) || stocks.length() != 32) {
                stock = -1;
            } else {
                String stock_ = stocks.substring(roadId - 1, roadId);
                if ("1".equals(stock_)) {
                    stock = 0;
                } else if ("0".equals(stock_)) {
                    stock = 5;
                } else {
                    stock = -1;
                }
            }
        } else {
            stock = -1;
        }
        return stock;
    }

    @Override
    public void cashInit() {
        //(useless)
    }

    @Override
    public void cashFinish() {
        //useless
    }

    @Override
    public int getProcessIdByRealId(int realId) {
        return realId;
    }

    @Override
    public void selectProduct(int boxId,int roadId) {
        protocol.selectProduct(roadId);
    }

    @Override
    public void cancelDeal() {
        protocol.cancelDeal();
    }

    @Override
    public boolean isConnectError() {
        SharedPreferences sp = mContext.getSharedPreferences(BouekiProtocol.TIMESTAMP, MODE_PRIVATE);
        long gap = System.currentTimeMillis() - sp.getLong(BouekiProtocol.TIMESTAMP, 0);
        if (gap > 1 * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

}
