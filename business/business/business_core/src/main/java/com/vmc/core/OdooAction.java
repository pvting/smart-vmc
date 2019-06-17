package com.vmc.core;

/**
 * <b>Project:</b> hollywant<br>
 * <b>Create Date:</b> 16/2/22<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface OdooAction extends odoo.core.OdooAction {


    String VMC_RECEIVE_MONEY = "com.want.vmc.RECEIVE_MONEY";

    /******** UI通知UI专属广播 ********/
    String VMC_NOTICE_PHONE_UPDATE = "com.want.vmc.VMC_NOTICE_PHONE_UPDATE";

    String UI_CANCEL_PROBLEM_TO_UI ="com.want.vmc.UI_CANCEL_PROBLEM_TO_UI";


    /******** BLL通知BLL专属广播 ********/
    String BLL_ORDERSYNC_TO_BLL = "com.want.vmc.BLL_ORDERSYNC_TO_BLL";

    /******** BLL通知UI专属广播 ********/

    String BLL_GOODS_SELECTED_TO_UI = "com.want.vmc.BLL_SELECT_GOODS_FOR_UI";

    String BLL_UPGRADE_TO_UI ="com.want.vmc.BLL_UPGRADE_TO_UI";

    String BLL_CANCEL_UPGRADE_TO_UI ="com.want.vmc.BLL_CANCEL_UPGRADE_TO_UI";

    String BLL_DOOR_STATE_TO_UI = "com.want.vmc.BLL_DOOR_STATE_TO_UI";

    String BLL_DOOR_STATE_TO_UI_SELECTED_PRODUCT = "com.want.vmc.BLL_DOOR_STATE_TO_UI_SELECTED_PRODUCT";

    String BLL_OUTGOODS_TO_UI = "com.want.vmc.BLL_OUTGOODS_TO_UI";

    String BLL_OUTGOODS_TIMEOUT_TO_UI = "com.want.vmc.BLL_OUTGOODS_TIMEOUT_TO_UI";

    String BLL_SETPRODUCT_TO_UI = "com.want.vmc.BLL_SETPRODUCT_TO_UI";


    String BLL_RECIVERMONEY_TO_UI = "com.want.vmc.BLL_RECIVERMONEY_TO_UI";


    String BLL_PAY_STATUS_TO_UI = "com.want.vmc.BLL_PAY_STATUS_TO_UI";

    String BLL_CREATE_IMAGE_TO_UI = "com.want.vmc.BLL_CREATE_IMAGE_TO_UI";


    String BLL_INIT_STATE_TO_UI = "com.want.vmc.BLL_INIT_STATE_TO_UI";

    String BLL_SENSOR_STATE_TO_UI = "com.want.vmc.BLL_SENSOR_STATE_TO_UI";//

    String BLL_NET_STATE_TO_UI ="com.want.vmc.BLL_NET_STATE_TO_UI";

    String BLL_CARD_CAN_TO_UI = "com.want.vmc.BLL_CARD_CAN_TO_UI";//

    String BLL_CARD_BAN_TO_UI = "com.want.vmc.BLL_CARD_BAN_TO_UI";//

    String BLL_SERIAL_ERROR_TO_UI = "com.want.vmc.BLL_SERIAL_ERROR_TO_UI";

    String BLL_DEAL_FINISH_TO_UI = "com.want.vmc.BLL_DEAL_FINISH_TO_UI";//

    String BLL_VERIFY_RESULT_TO_UI = "com.want.vmc.BLL_VERIFY_RESULT_TO_UI";

    /********提供订单共享*********/
    String BLL_ORDER_SHARE = "com.want.vmc.BLL_ORDER_SHARE";//

}
