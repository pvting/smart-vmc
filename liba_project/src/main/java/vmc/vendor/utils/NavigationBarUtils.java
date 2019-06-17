package vmc.vendor.utils;

import com.want.base.sdk.utils.StreamUtils;

import java.io.OutputStream;

/**
 * <b>Create Date:</b> 9/7/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class NavigationBarUtils {

    private NavigationBarUtils() {
        //no instance
    }

    public static void show() {
        new ShowThread().start();
    }

    public static void hide() {
        new HideThread().start();
    }

    private static class HideThread extends Thread {
        @Override
        public void run() {
            super.run();
            Process process = null;
            OutputStream ops = null;
            try {
                process = Runtime.getRuntime().exec("su");
                ops = process.getOutputStream();
                ops.write("service call activity 42 s16 com.android.systemui\n".getBytes());
                ops.write("exit\n".getBytes());
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != ops) {
                    StreamUtils.close(ops);
                }
                if (null != process) {
                    process.destroy();
                }
            }
        }
    }

    private static class ShowThread extends Thread {
        @Override
        public void run() {
            super.run();
            Process process = null;
            OutputStream ops = null;
            try {
                process = Runtime.getRuntime().exec("su");
                ops = process.getOutputStream();
                ops.write("am startservice --user 0 -n com.android.systemui/.SystemUIService\n".getBytes());
                ops.write("exit\n".getBytes());
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != ops) {
                    StreamUtils.close(ops);
                }
                if (null != process) {
                    process.destroy();
                }
            }
        }
    }
}
