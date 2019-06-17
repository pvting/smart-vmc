package com.vmc.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;


import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.ads.Ads;
import com.want.base.sdk.utils.JsonUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <b>Create Date:</b> 8/30/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * <p>
 * <br>
 */
public class AdsUtils {

    private static final String AD_SP_NAME = "ads";
    private static final String AD_KEY_NAME = "ads";
    private static final String SP_ADS_NAME = "ads_download_file_attr";


    private AdsUtils() {
        //no instance
    }

    public static void saveAdList(Context context, AdList ads) {
        final SharedPreferences sp = context.getSharedPreferences(AD_SP_NAME, Context.MODE_PRIVATE);
        Set<String> strings = new HashSet<>();
        for (Ads ad : ads.records) {
            strings.add(ad.toJSONObject().toString());
        }

        final SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(AD_KEY_NAME, strings);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 根据广告类型，返回广告
     *
     * @param context
     * @param adsType 广告类型
     *
     * @return
     */
    public static AdList getAdList(Context context, Ads.AdType adsType) {
        final SharedPreferences sp = context.getSharedPreferences(AD_SP_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sp.getStringSet(AD_KEY_NAME, new HashSet<String>());
        if (0 == strings.size()) {
            return null;
        }

        AdList list = new AdList();
        list.records = new ArrayList<>();

        Ads ads;
        for (String s : strings) {

            ads = JsonUtils.fromJson(s, Ads.class);

            if (adsType == null) {
                list.records.add(ads);
            } else {
                Ads.AdType adType = Ads.AdType.adTypeOf(ads.ad_type);
                if (adType == adsType) {
                    list.records.add(ads);
                }
            }
        }

        list.total = list.records.size();
        // 根据广告次序排序
        Collections.sort(list.records);

        return list;
    }

    public static AdList getAdList(Context context) {
        return getAdList(context, null);
    }

    /**
     * 根据URL获取对应文件的缓存名称
     *
     * @param url
     *
     * @return
     */
    public static String getCacheFileName(String url) {
        return md5(url);
    }

    /**
     * 获取缓存目录
     *
     * @param context
     *
     * @return
     */
    public static File getCacheDir(Context context) {
        return new File(context.getCacheDir(), "ads");
    }

    /**
     * 根据URL获取缓存文件
     *
     * @param context
     * @param url
     *
     * @return
     */
    public static File getCacheFile(Context context, String url) {
        return new File(getCacheDir(context), getCacheFileName(url));
    }

    private static String md5(String str) {
        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byts = md.digest(str.getBytes(Charset.forName("utf-8")));
            for (byte byt : byts) {
                int d = byt & 0xFF;
                if (d < 16) {
                    buffer.append(0);
                }
                buffer.append(Integer.toHexString(d));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


    /**
     * 保存下载的文件的属性
     *
     * @param context 上下文
     */
    public static void setDownloadFileAttr(Context context,String fileName,int value) {
        getSp(context)
                .edit()
                .putInt(fileName, value)
                .apply();
    }


    /**
     * 获取下载文件的属性
     *
     * @param context 上下文
     *
     * @return 下载文件的大小
     */
    public static int getDownloadFileAttr(Context context,String fileName) {
        return getSp(context).getInt(fileName, -1);
    }


    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_ADS_NAME, Context.MODE_PRIVATE);
    }

}
