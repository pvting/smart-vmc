package vmc.machine.core;

import android.content.Context;

import java.util.List;

import vmc.machine.core.model.VMCStackProduct;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 8/8/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * VMC控制接口
 * <br>
 */
public interface IVMCController {
    /** 商品通过选择器被选择时发出的通知 */

    // TODO: 8/8/16 VMC 接口定义

    /**
     * 售货机初始化
     *
     * @param context
     */
    void init(Context context);

    /**
     * 出货
     * @param box     货柜号
     * @param road    货道号
     */
    int outGoods(int box,int road);

    /**
     * 现金支付
     */
    int outGoodsByCash(int box,int road,int price);

    /**
     * 获取设备id
     * @return 返回设备号
     */
    String getVendingMachineId();

    /**
     * 设置设备ID
     * @param machineId 设备ID
     */
    int setVendingMachineId(String machineId);

    /**
     * 设置各货道商品和价格
     */
    void setProduct(int boxId, int roadId, String productId, int count, int price);

    /**
     * 设置所有商品信息
     */
    void setProducts(List<VMCStackProduct> list);

    /**
     * 获取商品库存
     */

    /**
     * 获取设备状态
     * @return 设备状态
     */
    String getVmcRunningStates();

    /**
     * 获取库存
     * @param box 货柜号
     * @param road 货道号
     * @return 库存数量
     */
    int getStockByRoad(int box,int road);

    /**
     * 初始化现金支付
     */
    void cashInit();

    /**
     * 初始化现金支付2
     */
    void cashFinish();

    /**
     * 通过机显商品id获取程序id
     * @param realId 机显id
     * @return 程序id
     */
    int getProcessIdByRealId(int realId);

    /**
     * 选择商品
     * @param boxId 货柜号
     * @param roadId 货道号
     */
    void selectProduct(int boxId,int roadId);

    /**
     * 取消交易
     */
    void cancelDeal();

    /**
     * 判断串口是不是连接错误
     * @return 错误为true，正常为false
     */
    boolean isConnectError();

    /**
     * 直接出货，无需选择
     * @param roadId 货道id
     * @param onOutGoodsOK 出货回调
     */
    void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK);

//    /**
//     * 固定选择的货道
//     */
//    void forceRoad();

    /**
     * 设置脉冲数流量计
     * @param waterFlow 脉冲数
     * @return 设置成功或者失败
     */
    int setFlowController(int waterFlow);


    /**
     * 设置购买水量上限和供水超时时间
     * @param Litre 水量上限
     * @param waterTime 供水超时时间
     * @return 设置成功或者失败
     */
    int setMaxLitreAndTime(int Litre,int waterTime);

    /**
     * 设置排水等待时间
     * @param pumpTime 排水时间 单位为秒
     * @return 设置成功或者失败
     */
    int setPumpTime(int pumpTime);

    /**
     * 机器品牌
     * @return 机器品牌
     */
    String getBrand();

    /**
     * 缺5角
     * @return 是否缺
     */
    boolean isLackOf50Cent();

    /**
     * 缺1元
     * @return 是否缺
     */
    boolean isLackOf100Cent();


    /**
     * 是否门开
     * @return
     */
    boolean isDoorOpen();

    boolean isDriveError();
}
