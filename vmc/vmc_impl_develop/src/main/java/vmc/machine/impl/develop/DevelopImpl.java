package vmc.machine.impl.develop;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Random;

import vmc.machine.core.IVMCController;
import vmc.machine.core.OnOutGoodsOK;
import vmc.machine.core.VMCAction;
import vmc.machine.core.model.VMCStackProduct;

/**
 * <b>Create Date:</b> 9/8/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 开发阶段使用的售货机控制层实现。
 * <br>
 */
public class DevelopImpl implements IVMCController {

    private Context mContext;

    public DevelopImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void init(final Context context) {
        result(context, "正在初始化");
        postResult(context, "初始化成功");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("initState", true);
                intent.setAction(VMCAction.VMC_TO_BLL_INIT_FINISH);
                context.sendBroadcast(intent);
            }
        }, 2000);


    }

    @Override
    public int outGoods(final int box, final int road) {
        result(mContext, "正在出货");
        postResult(mContext, "出货成功", new Runnable() {
            @Override
            public void run() {
                Intent orderIntent = new Intent();
                orderIntent.setAction(VMCAction.VMC_TO_BLL_OUTGOODS);
                orderIntent.putExtra("stack_no", road);
                orderIntent.putExtra("box_no", box);
                orderIntent.putExtra("outGoodsState", true);
                orderIntent.putExtra("error_code", 0);
                mContext.sendBroadcast(orderIntent);
            }
        });
        return 0;

    }

    @Override
    public int outGoodsByCash(final int box, final int road, int price) {
        result(mContext, "正在现金支付");

        postResult(mContext, "现金支付成功", new Runnable() {
            @Override
            public void run() {
                Intent orderIntent = new Intent();
                orderIntent.setAction(VMCAction.VMC_TO_BLL_OUTGOODS);
                orderIntent.putExtra("stack_no", road);
                orderIntent.putExtra("box_no", box);
                orderIntent.putExtra("outGoodsState", true);
                mContext.sendBroadcast(orderIntent);
//                onOutGoodsOK.onSuccess();
            }
        });
        return 0;
    }

    @Override
    public void cashInit() {

    }

    @Override
    public void cashFinish() {

    }

    @Override
    public int getProcessIdByRealId(int realId) {
        int processId = (realId / 10 - 1) * 8 + realId % 10;
        if (processId > 0 && processId <= 48) {
            return processId;
        } else {
            return -1;
        }
    }

    @Override
    public void selectProduct(int box, int roadId) {

    }

    @Override
    public void cancelDeal() {

    }

    @Override
    public boolean isConnectError() {
        return false;
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
    public String getBrand() {
        return "Develop";
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
//        return "develop001";
        return "05580001";
    }

    @Override
    public int setVendingMachineId(String machineId) {
        return 0;
    }

    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {
        // do nothing
    }

    @Override
    public void setProducts(List<VMCStackProduct> list) {
        result(mContext, "正在配置商品");
        postResult(mContext, "配置商品成功", new Runnable() {
            @Override
            public void run() {
//                onSetProductComplete.onComplete();


                Intent intent = new Intent();
                intent.setAction(VMCAction.VMC_TO_BLL_SETPRODUCT);
//                if(state == SUCCESS){
                intent.putExtra("setProductResult", true);
//                }else{
//                    intent.putExtra("setProductResult",false);
//                }
                mContext.sendBroadcast(intent);
            }
        });
    }

    @Override
    public String getVmcRunningStates() {
        return "设备正常";
    }

    @Override
    public int getStockByRoad(int box, int road) {
        switch (road) {
            case 1:
            case 4:
            case 6:
            case 12:
            case 20:
            case 25: {
                return 0;
            }
        }

        return new Random().nextInt(10) + 1;
    }

    private void result(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postResult(final Context context, final String message, final Runnable... runs) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(6000);
                    if (null != runs) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                for (Runnable run : runs) {
                                    run.run();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != context) {

                        }
                    }
                });
            }


        }.start();
    }
}
