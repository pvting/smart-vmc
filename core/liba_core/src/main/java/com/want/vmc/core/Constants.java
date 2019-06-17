package com.want.vmc.core;


/**
 * <b>Project:</b> apps<br>
 * <b>Create Date:</b> 16/1/19<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 基础的常量池.
 * <br>
 */
public interface Constants extends com.want.base.sdk.framework.Constants {
    /** 常用时间常量 */
    class Time {
        /** 1秒 */
        public static final int SECOND_1 = 1000;

        /** 10秒 */
        public static final int SECOND_10 = 10 * SECOND_1;

        /** 30秒 */
        public static final int SECOND_30 = 3 * SECOND_10;

        /** 1分钟 */
        public static final int MINUTE_1 = 60 * SECOND_1;

        /** 2分钟 */
        public static final int MINUTE_2= 2 * MINUTE_1;


        /** 5分钟 */
        public static final int MINUTE_5 = 5 * MINUTE_1;

        /** 10分钟 */
        public static final int MINUTE_10 = 10 * MINUTE_1;

        /** 工作时间间隔: 10分钟 */
        public static final int WORK_INTERVAL = MINUTE_10;
    }


    class Action {
        /** 系统重启 */
        public static final String REBOOT = "vmc.project.ACTION_REBOOT";

        public static final String SDK_ZIP_UPLOAD_LOG = "vmc.project.SDK_ZIP_UPLOAD_LOG";

        public static final String SDK_UPLOAD_LOG = "vmc.project.SDK_UPLOAD_LOG";
    }
}
