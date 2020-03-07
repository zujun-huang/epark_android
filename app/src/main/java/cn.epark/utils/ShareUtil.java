package cn.epark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import cn.epark.App;
import cn.epark.Constant;
import cn.epark.bean.Account;

/**
 * created huangzujun on 2019/8/30
 * Describe: 本地数据保存工具类
 */
public class ShareUtil {

    private static ShareUtil share = null;
    private final String LOCAL_SHARED_PREFERENCES_NAME = "Epark";
    private final String USER_INFO = "USER_LOGIN_INFO";

    public static ShareUtil newInstance() {
        if (share == null) {
            share = new ShareUtil();
        }
        return share;
    }

    /**
     * 获取本地SharedPreferences
     * @param context context
     * @return 本地保存数据的SharedPreferences
     */
    public SharedPreferences getShared(Context context){
        return context.getSharedPreferences(LOCAL_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     *  获取本地SharedPreferences
     * @param context context
     * @param ShareName shared文件名
     * @return SharedPreferences
     */
    public SharedPreferences getShared(Context context, String ShareName){
        return context.getSharedPreferences(ShareName, Context.MODE_PRIVATE);
    }

    /**
     * 获取数据
     * @param context context
     * @param key key
     * @return key对应的值，不存在返回null
     */
    public String getLocData(Context context, String key){
        return getShared(context).getString(key, null);
    }

    /**
     * 设置App不是第一次运行
     */
    public void setNotFirstRun(Context context){
        getShared(context).edit().putBoolean("first_run", false).apply();
    }

    /**
     * 获取是否第一次运行参数
     */
    public boolean isFirstRun(Context context){
        return getShared(context).getBoolean("first_run", true);
    }

    public String getLocHeadImg(Context context){
        return getShared(context).getString(Constant.USER_HEAD_IMG, null);
    }

    /**
     * 保存登录用户缓存
     * @param context context
     * @param account 用户
     */
    public void saveLoginUser(Context context, Account account) {
        getShared(context).edit()
                .putString(USER_INFO, JSON.toJSONString(account))
                .apply();
    }

    /**
     * 是否能自动登录
     * @param context context
     * @return false
     */
    public boolean isEmptyLoginUser(Context context) {
        String userJson = getShared(context).getString(USER_INFO, null);
        boolean emptyUser = TextUtils.isEmpty(userJson);
        if (!emptyUser) {
            App.getInstance().setAccount(JSON.parseObject(userJson, Account.class));
        }
        return emptyUser;
    }
}
