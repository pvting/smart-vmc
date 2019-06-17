package vmc.machine.impl.watergod;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
/**
 * Created by wisn on 2017/4/1.
 */

public class WaterGodUtils {

    private static final String SP_NAME = "ConfigParams";

    /**
     * 设置脉冲数和排废水时间
     * @param context
     * @param pulse
     * @param pumpTime
     */
    public static void setPulseAndPumpTimeConfig(Context context, int pulse, int  pumpTime) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pulse", pulse);
        editor.putInt("pumpTime", pumpTime);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }
}
