package cn.epark;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.epark.bean.Account;
import cn.epark.utils.LogUtil;
import cn.epark.utils.OkHttpUtil;
import cn.epark.utils.ShareUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * created huangzujun on 2019/12/25
 * Describe: 常驻运行类
 */
public class App extends MultiDexApplication
        implements Application.ActivityLifecycleCallbacks {

    private List<Activity> mActivitys = new ArrayList<>();
    public static String Market = BuildConfig.APPLICATION_ID;
    public static String URL;
    public static boolean hasNetwork = true, isWiFi = false;
    public static boolean isFirstRun;//首次启动App
    private static App instance;
    private static Account account;
    private Future future;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        URL = "http://120.77.214.120:8080";
        init();
    }

    private void init() {
        initBugly();
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(),
                getString(R.string.bugly_app_id), !BuildConfig.DEBUG);
    }

    public static Account getAccount() {
        if (account == null) {
            account = new Account();
        }
        return account;
    }

    public void setAccount(Account account) {
        App.account = account;
    }

    public static App getInstance() {
        return instance;
    }

    public void startSessionTimer() {
        //隔177分钟后刷新一次sessionid
        future = new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(()-> refreshSessionId(),
                Constant.SESSION_MAX, Constant.SESSION_MAX, TimeUnit.MINUTES);
    }

    public void cancelSessionTimer() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    public void refreshSessionId() {
        LogUtil.i("okHttpUtil", "----------刷新会话有效期---------");
        HashMap<String, String> params = new HashMap<>(2);
        params.put("user_id", getAccount().getId());
        params.put("session_id", getAccount().getEncryptionSession());
        OkHttpUtil.getInstance(this).request(OkHttpUtil.Method.POST,
            App.URL + URLConstant.URL_REFRESH_SESSION, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("okHttpUtil", "刷新会话有效期失败" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String status = jsonObject.optString("status");
                        if (ErrorCode.STATE_OK.equals(status)) {
                            getAccount().setEncryptionSession(jsonObject.optString("data"));
                            ShareUtil.newInstance().saveLoginUser(getApplicationContext(), getAccount());
                            LogUtil.i("okHttpUtil", "刷新会话有效期成功");
                        } else {
                            JSONObject error = jsonObject.optJSONObject("data");
                            LogUtil.i("okHttpUtil", "刷新会话有效期错误，错误信息："
                                    + error.optString("errMsg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("okHttpUtil", "刷新会话有效期失败" + e.getMessage());
                    }
                }
            });
    }

    public void addActivity(Activity a) {
        mActivitys.add(a);
    }

    public void closeActivity(Activity a) {
        for (Activity activity : mActivitys) {
            if (a.getClass().getName().equals(activity.getClass().getName())) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭指定类名的Activity
     * @param cls 类名
     */
    public void closeActivity(Class cls) {
        for (Activity activity : mActivitys) {
            if (cls.getName().equals(activity.getClass().getName())) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭除了指定类名的Activity以外的所有Activity
     * @param cls 不关闭的Activity类名
     */
    public void closeAllExcept(Class cls) {
        for (Activity a : mActivitys) {
            if (!a.getClass().getName().equals(cls.getName())) {
                a.finish();
            }
        }
    }

    public void closeAllActivity() {
        for (Activity a : mActivitys) {
            if (a != null) {
                a.finish();
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        closeActivity(activity);
    }
}
