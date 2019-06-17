package vmc.core;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogCatAppender;

/**
 * <b>Create Date:</b> 8/20/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class log {

    /** 日志文件最大值 */
    public static final long FILE_MAX_SIZE = 1 * 1024 * 1024;
    private static Logger logger;


    private log() {
        //no instance
    }

    public static void config(Level level,String fileName, String localfile) {

        logger = Logger.getLogger("vmc");

        // 清空所有appender
        logger.removeAllAppenders();

        // 设定Logger级别
        logger.setLevel(Level.ALL);

        // 打印格式
        PatternLayout layout = new PatternLayout("%d - [%p:%c] - %m%n");

        //控制台输出
        LogCatAppender logCatAppender = new LogCatAppender(layout);

        //控制台输出等级
        logCatAppender.setThreshold(Level.ALL);

        //应用设置
        logCatAppender.activateOptions();

        // 将新的Appender加到Logger中
        logger.addAppender(logCatAppender);

        try {
            //每天生成一个新的文件
            CustomDailyRollingFileAppender dailyRollingFileAppender = new CustomDailyRollingFileAppender(layout, localfile,"'.'yyyy-MM-dd");

            //根据指定大小生成文件
            RollingFileAppender rollingFileAppender = new RollingFileAppender(layout, fileName, true);

            //指定大小为1M
            rollingFileAppender.setMaxFileSize("1MB");

            //最多文件为10个
            rollingFileAppender.setMaxBackupIndex(500);

            //上传的日志等级
            rollingFileAppender.setThreshold(level);


            //打印等级
            dailyRollingFileAppender.setThreshold(Level.ALL);

            //打印编码
            dailyRollingFileAppender.setEncoding("UTF-8");

            //设置追加
            dailyRollingFileAppender.setAppend(true);

            //应用设置
            dailyRollingFileAppender.activateOptions();

            //最大文件个数
            dailyRollingFileAppender.setMaxBackupIndex(7);

            // log的文字码
            rollingFileAppender.setEncoding("UTF-8");

            // true:在已存在log文件后面追加 false:新log覆盖以前的log
            rollingFileAppender.setAppend(true);

            // 适用当前配置
            rollingFileAppender.activateOptions();

            // 将新的Appender加到Logger中
            logger.addAppender(rollingFileAppender);

            // 将新的Appender加到Logger中
            logger.addAppender(dailyRollingFileAppender);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void v(String tag, String msg) {
        logger.trace(format(tag, msg));
    }

    public static void d(String tag, String msg) {
        logger.debug(format(tag, msg));
    }

    public static void i(String tag, String msg) {
        logger.info(format(tag, msg));
    }

    public static void w(String tag, String msg) {
        logger.warn(format(tag, msg));
    }

    public static void e(String tag, String msg) {
        logger.error(format(tag, msg));
    }

    private static String format(String tag, String message) {
        return format("[%s] %s", tag, message);
    }

    private static String format(String tag, String message, Throwable t) {
        return format("[%s] %s. %s", tag, message, t.getMessage());
    }

    private static String format(String format, Object... message) {
        return String.format(Locale.getDefault(), format, message);
    }

}
