package vmc.machine.impl.watergod;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/2/23<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class WaterGodProtocol {

    /**
     * 加载水神的so文件
     */
    static {
        System.loadLibrary("WaterDispenser");
    }

    /**
     * 单例
     */
    private static WaterGodProtocol INSTANCE;


    private static final Object sLock = new Object();

    /**
     * 构建函数为private
     */
    public WaterGodProtocol(){}

    public static WaterGodProtocol getInstance(){
        if(null==INSTANCE){
            synchronized (sLock){
                if(null==INSTANCE){
                    INSTANCE = new WaterGodProtocol();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 水神通信协议
     * 需最先调用，然后才能调用其他函数
     * @return 0
     */
    public native int startProtocol();


    /**
     * 获取事件ID
     * @return 事件ID
     * #define VMC_VENDERID          0xA1    机器编码
     * #define VMC_VENDERVERSION     0xA2    固件版本号
     * #define VMC_VENDERPULSE       0xA3    脉冲数
     * #define VMC_VENDERLIMITLITRE  0xA4    单次购买水量上限
     * #define VMC_VENDERTOTALLITRE  0xA5    总计出水量
     * #define VMC_VENDERWAITTIME    0xA6    购买水量等待时间
     * #define VMC_VENDERPUMPTIME    0xA7    排水等待时间
     * #define VMC_VENDERDETAIL      0xA8    所有值
     * #define VMC_VENDERSTATUS      0xB1    售卖状态
     * #define VMC_VENDERSENSOR      0xB2    门开关
     * #define VMC_VENDERLIQUID      0xB3    原液低位
     * #define VMC_VENDERPRESSURE    0xB4    低水压
     * #define VMC_VENDERF2BREAKUP   0xB5    F2断开
     * #define VMC_VENDEROUTPUT1     0xB6    生成机OUTPUT（1,4）异常警示
     * #define VMC_VENDEROUTPUT2     0xB7    生成机OUTPUT（1,5）异常警示
     * #define VMC_STATUSDETAIL      0xB8    状态所有值
     * #define VMC_INIT_FINISH       0xC1    vmc初始化完成
     * #define VMC_VENDERRPT         0xD1    VMC出水状态报告
     * #define VMC_COMMREAD          0xE1    通讯读状态
     * #define VMC_COMMWRITE         0xE2    通讯写状态
     */
    public native int getEvent();

    public static final int WATER_GOD_EVENT_ID_MACHINE_ID = 0xA1;
    public static final int WATER_GOD_EVENT_ID_MACHINE_VERSION = 0xA2;
    public static final int WATER_GOD_EVENT_ID_PULSE = 0xA3;
    public static final int WATER_GOD_EVENT_ID_LIMIT_LITRE = 0xA4;
    public static final int WATER_GOD_EVENT_ID_TOTAL_LITRE = 0xA5;
    public static final int WATER_GOD_EVENT_ID_BUY_WATER_WAIT_TIME = 0xA6;
    public static final int WATER_GOD_EVENT_ID_DRAINAGE_WATER_WAIT_TIME = 0xA7;
    public static final int WATER_GOD_EVENT_ID_INFO_ALL = 0xA8;
    public static final int WATER_GOD_EVENT_ID_SALE = 0xB1;
    public static final int WATER_GOD_EVENT_ID_DOOR = 0xB2;
    public static final int WATER_GOD_EVENT_ID_LIQUID = 0xB3;
    public static final int WATER_GOD_EVENT_ID_WATER_PRESSURE = 0xB4;
    public static final int WATER_GOD_EVENT_ID_F2BREAKUP = 0xB5;
    public static final int WATER_GOD_EVENT_ID_OUTPUT1 = 0xB6;
    public static final int WATER_GOD_EVENT_ID_OUTPUT2 = 0xB7;
    public static final int WATER_GOD_EVENT_ID_STATUS_ALL = 0xB8;
    public static final int WATER_GOD_EVENT_ID_VMC_INIT_FINISH = 0xC1;

    /**
     * 出水完成后接受到的eventId
     */
    public static final int WATER_GOD_EVENT_ID_OUT_GOODS_SUCCESS = 0xD1;
    public static final int WATER_GOD_EVENT_ID_READ_STATUS = 0xE1;
    public static final int WATER_GOD_EVENT_ID_WRITE_STATUS = 0xE2;





    /**
     * 请求获取VMC状态
     * @param m_type 需要获取的状态ID
     *
     * 1.预留；
     * 2.售卖状态（可销售/禁止销售 ）；
     * 3.门开关（1字节，传感器控制，当门打开时工控应该切换到维护app，底层需停止销售）；
     * 4.原液低位（1字节，低于3L需停止销售）；
     * 5.低水压（1字节，进水水压底）；
     * 6.F2断开（1字节）。
     * 7.生成机OUTPUT(1,4)原水异常警示
     * 8.生成机OUTPUT(1,5),原水异常警示
     *
     */
    public native void getStatus(int m_type);

    public static final int WATER_GOD_GET_STATUS_SALE = 2;
    public static final int WATER_GOD_GET_STATUS_DOOR = 3;
    public static final int WATER_GOD_GET_STATUS_LIQUID = 4;
    public static final int WATER_GOD_GET_STATUS_WATER_PRESSURE = 5;
    public static final int WATER_GOD_GET_STATUS_F2_BREAKUP = 6;
    public static final int WATER_GOD_GET_STATUS_OUTPUT_1_4 = 7;
    public static final int WATER_GOD_GET_STATUS_OUTPUT_1_5 = 8;
    public static final int WATER_GOD_GET_STATUS_ALL = 0xFF;


    /**
     *  状态报告
     * @param rpt_type 状态ID
     * @return
     *
     * VMC售卖状态 收到此事件0xB1 WATER_GOD_GET_STATUS_SALE
     * VMC门开关 收到此事件0xB2 WATER_GOD_GET_STATUS_DOOR
     * VMC原液低位 收到此事件0xB3 WATER_GOD_GET_STATUS_LIQUID
     * VMC低水压 收到此事件0xB4 WATER_GOD_GET_STATUS_WATER_PRESSURE
     * VMC F2断开 收到此事件0xB5 WATER_GOD_GET_STATUS_F2_BREAKUP
     * Machine_output1 收到此事件0xB6 WATER_GOD_GET_STATUS_OUTPUT_1_4
     * Machine_output2 收到此事件0xB7 WATER_GOD_GET_STATUS_OUTPUT_1_5
     * 所有值 收到此事件0xB8 WATER_GOD_GET_STATUS_ALL
     */
    public native byte[] getStatusRpt(int rpt_type);

    /**
     * 请求获取VMC相应系统信息
     * @param m_type 相应的ID
     *
     *  1.预留；
     *  2.机器编码（8字节）；
     *  3.固件版本号（2字节）；
     *  4.脉冲数（2字节）；
     *  5.单次购买水上限（1字节）；
     *  6.总计出水量（4字节）；
     *  7.购买水量等待时间（1字节）；
     *  8.排水等待时间（1字节）
     *  9.所有值
     *
     */
    public native void getInfo(int m_type);

    public static final int WATER_GOD_GET_INFO_MACHINE_ID = 2;
    public static final int WATER_GOD_GET_INFO_MACHINE_VERSION = 3;
    public static final int WATER_GOD_GET_INFO_PULSE = 4;
    public static final int WATER_GOD_GET_INFO_LIMIT_LITRE = 5;
    public static final int WATER_GOD_GET_INFO_TOTAL_LITRE = 6;
    public static final int WATER_GOD_GET_INFO_BUY_WATER_WAIT_TIME = 7;
    public static final int WATER_GOD_GET_INFO_DRAINAGE_WATER_WAIT_TIME = 8;
    public static final int WATER_GOD_GET_INFO_ALL = 0xFF;


    /**
     * 信息报告
     * @param rpt_type 相应的ID
     * @return 根据ID返回响应的值
     *
     * VMC机器编码：收到事件0xA1 WATER_GOD_GET_INFO_MACHINE_ID
     * VMC固件版本号：收到事件0xA2 WATER_GOD_GET_INFO_MACHINE_VERSION
     * VMC脉冲数：收到事件0xA3 WATER_GOD_GET_INFO_PULSE
     * VMC单次购买水量上限：收到事件0xA4 WATER_GOD_GET_INFO_LIMIT_LITRE
     * VMC总计出水量：收到事件0xA5 WATER_GOD_GET_INFO_TOTAL_LITRE
     * VMC 购买水量等待时间：收到事件0xA6 WATER_GOD_GET_INFO_BUY_WATER_WAIT_TIME
     * VMC 排水等待时间： 收到事件0xA7 WATER_GOD_GET_INFO_DRAINAGE_WATER_WAIT_TIME
     * VMC：所有值，收到事件0xA7 WATER_GOD_GET_INFO_ALL
     *
     */
    public native byte[] getRpt(int rpt_type);


    /**
     * 出水指令
     * 需要等待4秒的排水时间
     * @param nLitre 16进制，单位是升
     */
    public native int setVenderAction(int nLitre);

    public static final int WATER_GOD_OUT_GOODS_1_LITRE = 0x0A;
    public static final int WATER_GOD_OUT_GOODS_2_LITRE = 0x14;
    public static final int WATER_GOD_OUT_GOODS_3_LITRE = 0x1E;
    public static final int WATER_GOD_OUT_GOODS_4_LITRE = 0x28;
    public static final int WATER_GOD_OUT_GOODS_5_LITRE = 0x32;

    /**
     * 设置机器ID
     * 机器默认编号为“00000000”（字符形式）
     * @param machine_id 机器ID
     * @return 成功为0 失败为-1
     */
    public native int setMachineID(byte[] machine_id);



    /**
     * 脉冲数流量计
     * @param waterFlow 流量 ~= 水量
     * @return 成功0，失败-1
     */
    public native int setFlowControler(byte[] waterFlow);


    /**
     * 查看流量计
     * @return 出水流量
     */
    public native  byte[] getFlowControler();


//    public native  byte[] getMaxLitreAndTime();


    /**
     * 购买水量上限和供水超时时间
     * @param Litre 水量上限 16进制
     * @param waterTime 供水超时 16进制
     * @return 成功0，失败-1
     */
    public native int setMaxLitreAndTime(byte Litre ,byte waterTime);

    /**
     * 数据清零（慎用,暂时不用）
     */
    public native void SetReset();


    /**
     * 获取出水失败原因
     * @return 1、status 出水状态（1.成功 2.失败）
     *         2、value（实际出水值）
     *         3、ErrorNo（出水失败原因）
     */
    public native byte[] getVenderActionStatus();

    /**
     * 获取串口状态
     * @param rpt_type 1（read）或者2(write)
     * @return
     */
    public native byte[] getCommStatusRpt(int rpt_type);

    public static final int COMM_READ = 1;
    public static final int COMM_WRITE = 2;

    /**
     * 请求串口状态
     */
    public native void getCommStatus();



    /**
     * 设置排废水时间
     * @param nTime 时间 单位为秒
     * @return 设置成功0或者失败-1
     */
    public native int setPumptime(int nTime);





}


