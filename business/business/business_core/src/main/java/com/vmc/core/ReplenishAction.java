package com.vmc.core;

/**
 * <b>Create Date:</b>2017/5/25 08:46<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ReplenishAction {

    /** 补货app通知售卖app */

    public static final String TAKE_STOCK = "com.want.vmc.TAKE_STOCK";//补货app发送盘点单


    public static final String STOCK_SYNC = "com.want.vmc.STOCK_SYNC";//补货app发送补货|换货单


    public static final String SUPPLY_GET_STOCK = "com.want.vmc.SUPPLY_GET_STOCK";//获取库存

    public static final String GET_FACTORY_CODE = "com.want.vmc.GET_FACTORY_CODE";//获取机器编号


    public static final String SUPPLY_EXIT = "com.want.vmc.SUPPLY_EXIT";//补货退出

    /** 售卖app通知补货app */

    public static final String STOCK_SHARE = "com.want.vmc.STOCK_SHARE";//售卖app提供商品以及库存给补货app

    public static final String FACTORY_CODE_SHARE = "com.want.vmc.FACTORY_CODE_SHARE";//售卖app提供机器编号


    public static final String STOCK_SYNC_RESULT = "com.want.vmc.STOCK_SYNC_RESULT";//售卖app返回补货|换货结果


    public static final String STOCK_SYNC_ACTION= "com.want.vmc.STOCK_SYNC_ACTION";//售卖app返回补货|换货结果

}