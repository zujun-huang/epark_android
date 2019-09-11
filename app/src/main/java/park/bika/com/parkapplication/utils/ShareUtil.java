package park.bika.com.parkapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import park.bika.com.parkapplication.Constant;

/**
 * created huangzujun on 2019/8/30
 * Describe: 本地数据保存工具类
 */
public class ShareUtil {

    private static ShareUtil share = null;


    public static ShareUtil newInstance() {
        if (share == null) {
            share = new ShareUtil();
        }
        return share;
    }

    /**
     * 获取本地SharedPreferences
     * @param context
     * @return 本地保存数据的SharedPreferences
     */
    public SharedPreferences getShared(Context context){
        return context.getSharedPreferences("Epark", Context.MODE_PRIVATE);
    }

    /**
     *  获取本地SharedPreferences
     * @param context
     * @param SharedName shared文件名
     * @return SharedPreferences
     */
    public SharedPreferences getShared(Context context, String SharedName){
        return context.getSharedPreferences(SharedName, Context.MODE_PRIVATE);
    }

    /**
     * 获取数据
     * @param context
     * @param key key
     * @return key对应的值，不存在返回null
     */
    public String getLocData(Context context, String key){
        SharedPreferences preferences= context.getSharedPreferences("Epark", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    //是否第一次运行参数
    public boolean isFirstRun(Context context){
        SharedPreferences preferences= context.getSharedPreferences("Epark", Context.MODE_PRIVATE);
        return preferences.getBoolean("first_run", true);
    }

    public String getLocHeadImg(Context context){
        SharedPreferences preferences= context.getSharedPreferences(Constant.USER_HEAD_IMG, Context.MODE_PRIVATE);
        return preferences.getString(Constant.USER_HEAD_IMG, null);
    }
}
