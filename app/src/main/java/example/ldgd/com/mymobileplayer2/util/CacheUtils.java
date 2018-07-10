package example.ldgd.com.mymobileplayer2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ldgd on 2018/7/9.
 * 功能： 缓存类
 * 说明：
 */

public class CacheUtils {


    /**
     * 保存缓存数据
     */
    public static void putString(Context context, String key, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldgd", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, data).commit();
    }

    /**
     * 得到缓存数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ldgd", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }


}
