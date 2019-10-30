package cn.epark.utils;

import android.content.Context;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.epark.BuildConfig;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by huangzujun on 2019/9/23.
 * Describe: 网络请求工具类
 */
public class OkHttpUtil {

    private boolean SHOW_REQUEST , SHOW_RESPONSE = BuildConfig.DEBUG;
    private static OkHttpUtil mOkHttpUtil;
    private OkHttpClient mOkHttpClient;

    public interface Method{
        int GET = 0;
        int POST = 1;
    }

    public static OkHttpUtil getInstance(Context context) {
        if (mOkHttpUtil == null) {
            //同步锁，只能有一个执行路径进入
            synchronized (OkHttpUtil.class) {
                if (mOkHttpUtil == null) {
                    mOkHttpUtil = new OkHttpUtil(context);
                }
            }
        }
        return mOkHttpUtil;
    }

    private OkHttpUtil(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        int cacheSize = 10 << 20; //10Mb
        mOkHttpClient = builder.cache(new Cache(context.getCacheDir(), cacheSize))//设置缓存目录和大小
                .connectTimeout(15, TimeUnit.SECONDS) //连接时间 15s
                .readTimeout(20, TimeUnit.SECONDS) //读取时间 20s
                .writeTimeout(20, TimeUnit.SECONDS) //写入的时间20s
                .build();
    }

    /**
     * 网络请求，默认post
     * @param method 请求方式
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 返回监听
     */
    public void request(int method, String url, Map<String, String> params, Callback callback) {
        Request request;
        Call call;
        switch (method){
            case Method.GET:
                if (params.size() > 0){
                    url += "?" + prepareParam(params);
                }
                request = new Request.Builder().url(url).build();
                call = mOkHttpClient.newCall(request);
                break;
            default:
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : params.keySet()){
                    builder.add(key, params.get(key));
                }
                request = new Request.Builder().url(url).post(builder.build()).build();
                call = mOkHttpClient.newCall(request);
                break;
        }
        if (SHOW_REQUEST){
            LogUtil.i("okHttpUtil", "URL:" + url);
            LogUtil.i("okHttpUtil", "params:" + params.toString());
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null){
                    if (SHOW_RESPONSE) {
                        LogUtil.i("okHttpUtil", "response:" + response.toString());
                    }
                    callback.onResponse(call, response);
                }
            }
        });
    }

    private String prepareParam(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (String key: params.keySet()) {
            if (params.size() > 1){
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key));
        }
        return sb.toString();
    }
}
