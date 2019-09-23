package park.bika.com.parkapplication.utils;

import android.content.Context;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Created by huangzujun on 2019/9/23.
 * Describe: 网络请求工具类
 */
public class VolleyHttpUtil {

    private boolean SHOW_REQUEST , SHOW_RESPONSE = BuildConfig.DEBUG;
    public static RequestQueue requestQueue ;

    public static VolleyHttpUtil newInstance(Context context){
        requestQueue = Volley.newRequestQueue(context);
        return new VolleyHttpUtil();
    }

    public void httpRequest(int method, String url, HashMap<String, String> maps, Listener<String> listener, ErrorListener errorListener) {
        if (SHOW_REQUEST){
            LogUtil.i("VolleyHttpUtil", "URL:" + url);
            if (maps != null) {
                LogUtil.i("VolleyHttpUtil", "params:" + maps.toString());
            }
        }
        requestQueue.add(new StringRequest(method, url, response -> {
            if (SHOW_RESPONSE) {
                LogUtil.i("VolleyHttpUtil", "response:" + response);
            }
            if (listener != null){
                listener.onResponse(response);
            }
        }, errorListener));
    }
}
