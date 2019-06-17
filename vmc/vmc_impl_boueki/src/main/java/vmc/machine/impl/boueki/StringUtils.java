package vmc.machine.impl.boueki;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/5/17<br>
 * <b>Author:</b> pengdun<br>
 * <b>Description:</b> <br>
 */
public final class StringUtils {

    private final static char[] mChars = "0123456789ABCDEF".toCharArray();
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static char[] CHAR_CACHED = new char[512];

    /**
     * 将byte[]数组转化为String类型
     * @param bytes
     *            需要转换的byte[]数组
     * @param length
     *            需要转换的数组长度
     * @return 转换后的String队形
     */
    public static String toHexString(byte[] bytes, int length) {
        String result = "";
        if (bytes != null) {
            for (int i = 0; i < length; i++) {
                result = result +
                         (Integer.toHexString(bytes[i] < 0 ? bytes[i] + 256 : bytes[i]).length() == 1
                          ? "0" + Integer.toHexString(bytes[i] < 0 ? bytes[i] + 256 : bytes[i])
                          : Integer.toHexString(bytes[i] < 0 ? bytes[i] + 256 : bytes[i])) +
                         "";//将这里的空格给去掉了,方便后面的数据解析
            }
            return result;
        }
        return "";
    }

    /**
     * 把byte[]转换为string
     *
     * @param bytes  bytes
     * @param length max length is 64
     * @param out    {@link StringBuilder}
     */
    public static void toHexString(byte[] bytes, int length, StringBuilder out) {
        // NOTE!!!
        // 由于CHAR_CACHED长度的限制，调试的时候会导致数组越界，
        // 需要根据实际情况进行重新赋值。
        // 原则上，128位的长度已经足够缓存数据的了。
        final int size = length * 2;
        Arrays.fill(CHAR_CACHED, 0, size, '0');
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            CHAR_CACHED[j * 2] = DIGITS_LOWER[v >>> 4];
            CHAR_CACHED[j * 2 + 1] = DIGITS_LOWER[v & 0x0F];
        }
        out.append(CHAR_CACHED, 0, size);
    }

    /**
     * 将byte[]数组转化为String类型
     * @param arg
     *            需要转换的byte[]数组
     * @param start
     *            开始位
      * @param end
     *            结束位
     * @return 转换后的String队形
     */
    public static String toHexString(List<Byte> arg, int start , int end) {
        String result = new String();
        if (arg != null) {
            for (int i = start; i < end; i++) {
                result = result
                        + (Integer.toHexString(
                        arg.get(i) < 0 ? arg.get(i) + 256 : arg.get(i)).length() == 1 ? "0"
                        + Integer.toHexString(arg.get(i) < 0 ? arg.get(i) + 256
                        : arg.get(i))
                        : Integer.toHexString(arg.get(i) < 0 ? arg.get(i) + 256
                        : arg.get(i))) + "";//将这里的空格给去掉了,方便后面的数据解析
            }
            return result;
        }
        return "";
    }

    /**
     * 字符串转换成十六进制字符串
     * @param str String 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str){
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();

        for (int i = 0; i < bs.length; i++){
            sb.append(mChars[(bs[i] & 0xFF) >> 4]);
            sb.append(mChars[bs[i] & 0x0F]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * bytes转换成十六进制字符串
     * @param b byte[] byte数组
     * @param iLen int 取前N位处理 N=iLen
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b, int iLen){
        StringBuilder sb = new StringBuilder();
        for (int n=0; n<iLen; n++){
            sb.append(mChars[(b[n] & 0xFF) >> 4]);
            sb.append(mChars[b[n] & 0x0F]);
//            sb.append(' ');
        }
        return sb.toString().trim().toUpperCase(Locale.US);
    }


    public static String hexStr2Str(String hexStr){
        String str = "0123456789abcdef";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /* *
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)
     *来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static String toHexString_space(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }
    /**
     * 将String转化为byte[]数组
     * @param arg
     *            需要转换的String对象
     * @return 转换后的byte[]数组
     */
    public static byte[] toByteArray(String arg) {
        int len = arg.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(arg.charAt(i), 16) << 4)
                                  + Character.digit(arg.charAt(i + 1), 16));
        }
        return data;
    }
}
