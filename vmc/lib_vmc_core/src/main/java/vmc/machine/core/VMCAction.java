package vmc.machine.core;

/**
 * <b>Project:</b> hollywant<br>
 * <b>Create Date:</b> 16/2/22<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> <br>
 */
public interface VMCAction {

   /************VMC通知BLL专属广播**************/

    String VMC_TO_BLL_GOODS_SELECTED = "com.want.vmc.VMC_TO_BLL_GOODS_SELECTED";

    String VMC_TO_BLL_DOOR_STATE = "com.want.vmc.VMC_TO_BLL_DOOR_STATE";

    String VMC_TO_BLL_OUTGOODS = "com.want.vmc.VMC_TO_BLL_OUTGOODS";

    String VMC_TO_BLL_SETPRODUCT = "com.want.vmc.VMC_TO_BLL_SETPRODUCT";

    String VMC_TO_BLL_RECEIVE_MONEY = "com.want.vmc.VMC_TO_BLL_RECEIVE_MONEY";

    String VMC_TO_BLL_CANCEL_DEAL = "com.want.vmc.VMC_TO_BLL_CANCEL_DEAL";

    String VMC_TO_BLL_DEAL_FINISH = "com.want.vmc.VMC_TO_BLL_DEAL_FINISH";

    String VMC_TO_BLL_CLEAR_ROAD_ERROR = "com.want.vmc.VMC_TO_BLL_CLEAR_ROAD_ERROR";

    String VMC_TO_BLL_SELLABLE_ROADS = "com.want.vmc.VMC_TO_BLL_SELLABLE_ROADS";

    String VMC_TO_BLL_LIQUID_STATE = "com.want.vmc.VMC_TO_BLL_LIQUID_STATE";

    String VMC_TO_BLL_OUTPUT1_STATE = "com.want.vmc.VMC_TO_BLL_OUTPUT1_STATE";

    String VMC_TO_BLL_OUTPUT2_STATE = "com.want.vmc.VMC_TO_BLL_OUTPUT2_STATE";

    String VMC_TO_BLL_F2BREAKUP_STATE = "com.want.vmc.VMC_TO_BLL_F2BREAKUP_STATE";

    String VMC_TO_BLL_WATER_PRESSURE_STATE = "com.want.vmc.VMC_TO_BLL_WATER_PRESSURE_STATE";

    String VMC_TO_BLL_SALE_STATE = "com.want.vmc.VMC_TO_BLL_SALE_STATE";

    String VMC_TO_BLL_PULSE_STATE = "com.want.vmc.VMC_TO_BLL_PULSE_STATE";

    String VMC_TO_BLL_LIMIT_LITRE_STATE = "com.want.vmc.VMC_TO_BLL_LIMIT_LITRE_STATE";

    String VMC_TO_BLL_TOTAL_LITRE_STATE = "com.want.vmc.VMC_TO_BLL_TOTAL_LITRE_STATE";

    String VMC_TO_BLL_BUY_WATER_WAIT_TIME_STATE = "com.want.vmc.VMC_TO_BLL_BUY_WATER_WAIT_TIME_STATE";

    String VMC_TO_BLL_DRAINAGE_WATER_WAIT_TIME_STATE = "com.want.vmc.VMC_TO_BLL_DRAINAGE_WATER_WAIT_TIME_STATE";

    String VMC_TO_BLL_MACHINE_ID_STATE = "com.want.vmc.VMC_TO_BLL_MACHINE_ID_STATE";

    String VMC_TO_BLL_MACHINE_VERSION_STATE = "com.want.vmc.VMC_TO_BLL_MACHINE_VERSION_STATE";

    String VMC_TO_BLL_INIT_FINISH = "com.want.vmc.VMC_TO_BLL_INIT_FINISH";

    String VMC_TO_BLL_SENSOR_STATE = "com.want.vmc.VMC_TO_BLL_SENSOR_STATE";

    String VMC_TO_BLL_CARD_BAN = "com.want.vmc.VMC_TO_BLL_CARD_BAN";

    String VMC_TO_BLL_CARD_CAN = "com.want.vmc.VMC_TO_BLL_CARD_CAN";
}
