package park.bika.com.parkapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @作者 huangzujun
 * @日期 2019-08-04
 * @描述
 */
public class NetworkConnectUtil {

    /**
     * 是否有网络连接
     * @param context
     * @return true/false
     */
    public static boolean isNetworkConnected(Context context){
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = manager.getActiveNetworkInfo();
            return (mNetworkInfo != null && mNetworkInfo.isConnected());
        }
        return false;
    }
}
