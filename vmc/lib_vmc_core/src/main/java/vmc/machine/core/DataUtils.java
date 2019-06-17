package vmc.machine.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/3/23<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class DataUtils {


    /**
     * 安全类型转换
     *
     * @param value        需要转换的值
     * @param defaultValue 如果转换失败的默认值
     *
     * @return 转换后的值
     */
    public static int convertToInt(String value, int defaultValue) {
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            try {
                return Double.valueOf(value).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }


    /**
     * Ascii码字符串转 10进制字符串
     *
     * @param asciiString ascii字符串
     *
     * @return 10进制字符串
     */
    public static String asciiString2Decs(String asciiString) {
        String ret;
        int length = asciiString.length();
        char[] charData = new char[length / 2];
        for (int i = 0; i < charData.length; i++) {
            charData[i] = (char) (Integer.parseInt(asciiString.substring(2 * i, 2 * i + 2), 16));
        }
        ret = new String(charData);
        return ret;
    }


    /**
     * 16进制字符串转byte数组
     *
     * @param hexString 16进制字符串
     *
     * @return byte数组
     */
    public static byte[] hexString2Bytes(String hexString) {
        int stringLength = hexString.length();
        //新加判断
        if ((stringLength % 2) == 1) {
//            如果是奇数
            hexString = "0" + hexString;
            stringLength += 1;
        }
        //
        byte[] data = new byte[(stringLength / 2)];
        for (int i = 0, j = 0; i < data.length; i++, j = j + 2) {
            data[i] = (byte) Integer.parseInt(hexString.substring(j, (j + 2)), 16);
        }
        return data;
    }

    /**
     * 去除第一个byte
     *
     * @param src 源数据
     *
     * @return 去除第一个byte后的数组
     */
    public static byte[] reduceOneByte(byte[] src) {
        byte[] returnByte = new byte[src.length - 1];
        System.arraycopy(src, 1, returnByte, 0, src.length - 1);
        return returnByte;
    }

    /**
     * byte转16进制字符串函数
     *
     * @param b
     *
     * @return
     */
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }


    /**
     * 获取app版本
     *
     * @param context 上下文
     *
     * @return 版本
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.d("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static boolean getNetState(Context context) {
        ConnectivityManager
                connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

}
