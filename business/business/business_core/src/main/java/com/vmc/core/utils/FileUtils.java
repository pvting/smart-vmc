package com.vmc.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import vmc.core.log;

/**
 * <b>Create Date:</b>2017/7/10 14:46<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class FileUtils {


    private static final String TAG = "FileUtils";

    /**
     * 压缩多个文件
     *
     * @param filename
     * @param temp_path
     * @param list
     */
    public static boolean createZip(String filename, String temp_path, List<File> list) {

        File file = new File(temp_path);

        log.i(TAG, "输出日志包地址: " + temp_path);

        File zipFile = new File(temp_path + File.separator + filename);

        if (zipFile.exists()){
            log.i(TAG, "文件存在不需要压缩: "+zipFile.getAbsolutePath());
            return true;
        }



        InputStream input;
        try {
            //ZipOutputStream:此类为以 ZIP 文件格式写入文件实现输出流过滤器。包括对已压缩和未压缩条目的支持。
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            BufferedOutputStream bos = new BufferedOutputStream(zipOut);
      /*
       *  setComment(String comment)设置 ZIP 文件注释。
        参数：
        comment - 注释字符串

       * */
            zipOut.setComment(file.getName());

            if (file.isDirectory()) {
                for (int i = 0; i < list.size(); ++i) {
                    input = new FileInputStream(list.get(i));
                    BufferedInputStream bis = new BufferedInputStream(input);
                    zipOut.putNextEntry(new ZipEntry(file.getName() +
                                                     File.separator +
                                                     list.get(i).getName()));
                    int temp;
                    while ((temp = bis.read()) != -1) {
                        bos.write(temp);
                    }
                    bis.close();
                    input.close();
                    log.i(TAG, "[" + list.get(i).getName() + "] zip to File:[" + filename + "] success ");
                }
            }
            bos.flush();
            bos.close();
            zipOut.close();
            return true;
        } catch (Exception e) {
            log.e(TAG, "压缩失败: " + e.getMessage());
            return  false;
        }
    }


    /**
     * 压缩单个文件
     *
     * @param filename
     * @param temp_path
     * @param srcFile
     */
    public static boolean createZip(String filename, String temp_path, File srcFile) {
        boolean isSuccess = false;

//        File fileDir = new File(temp_path);
//
//        log.i(TAG, "输出日志包地址: " + temp_path);

        File zipFile = new File(temp_path + File.separator + filename);


        BufferedOutputStream bos = null;
        ZipOutputStream zipOut = null;


        BufferedInputStream bis = null;


        try {


            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

            bos = new BufferedOutputStream(zipOut);

            zipOut.setComment(zipFile.getName());

            zipOut.putNextEntry(new ZipEntry(zipFile.getName() +
                                             File.separator +
                                             zipFile.getName().replace("zip", "txt")));


            bis = new BufferedInputStream(new FileInputStream(srcFile));


            int temp;
            while ((temp = bis.read()) != -1) {
                bos.write(temp);
            }
            log.i(TAG, "[" + zipFile.getName() + "] zip to File:[" + filename + "] success ");
            isSuccess = true;
        } catch (Exception e) {
            log.e(TAG, "压缩失败: " + e.getMessage());
            if (zipFile.exists()) {
                zipFile.delete();
            }
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }


}