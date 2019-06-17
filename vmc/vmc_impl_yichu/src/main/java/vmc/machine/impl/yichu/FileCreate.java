package vmc.machine.impl.yichu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <b>Project:</b> MyApplication<br>
 * <b>Create Date:</b> 2016/12/9<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class FileCreate {

    private static final String TAG = FileCreate.class.getSimpleName();
    private static final String AssetsFileName = "config.ini";

    private Context mContext;

    private String versionName = "";

    public FileCreate() {

    }


    /**
     * 初始化 创建文件
     *
     * @param context 上下文
     */
    public void createFile(Context context) {
        this.mContext = context;

        Log.d(TAG, "createFile");

        String
                filePath =
                File.separator +
                "sdcard" +
                File.separator +
                "Android" +
                File.separator +
                "data" +
                File.separator +
                context.getPackageName() +
                File.separator +
                "set";

        String fileName = filePath + File.separator + "config.ini";


        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String
                fileVersion =
                context.getSharedPreferences("version", Context.MODE_WORLD_READABLE)
                       .getString("fileVersion", "0.0.0");

        try {
            File file = new File(fileName);
            if (versionName.equals(fileVersion)) {
                if (file.exists()) {
                    Log.d(TAG, "文件已经存在且版本号一致,不需要修改" + fileName);
                    return;
                }
            }
            if (file.exists()) {
                file.delete();
            }

            createDir(filePath);

            InputStream is = context.getAssets().open(AssetsFileName);
            copyFile(is, file);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException=" + e.toString());
        }

    }

    /**
     * 创建文件夹
     */
    private void createDir(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            //mkdir只能创建一级目录，mkdirs可以创建多级目录
            boolean isMakeSuccess = file.mkdirs();
            if (!isMakeSuccess) {
                Log.e(TAG, "dir is make fail");
                return;
            }
            Log.d(TAG, "dir is make" + file.getAbsolutePath());
        }
    }

    private void copyFile(InputStream is, File file) {
        try {
            if (null == file || null == is) {
                Log.e(TAG, "File or InputStream is null");
                return;
            }
            int byteRead;
            if (!file.exists()) {
                try {
                    boolean isCreateSuccess = file.createNewFile();
                    if (!isCreateSuccess) {
                        Log.e(TAG, "file is create fail");
                        return;
                    }
                    FileOutputStream fs = new FileOutputStream(file.getAbsolutePath());
                    byte[] buffer = new byte[1024];
                    while ((byteRead = is.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteRead);
                    }
                    is.close();
                    fs.close();

                    SharedPreferences sp = mContext.getSharedPreferences("version", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("fileVersion", versionName);
                    SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "创建文件");
            }

        } catch (Exception e) {
            Log.e(TAG, "复制文件操作出错");
            e.printStackTrace();


        }

    }


}
