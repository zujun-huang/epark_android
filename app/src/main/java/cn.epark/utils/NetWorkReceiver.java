package cn.epark.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * created huangzujun on 2019/12/11
 * Describe: 监听网络广播
 */
public class NetWorkReceiver extends BroadcastReceiver {

    private Context mContext;
    private OnNetWorkListener netWorkListener;
    private final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    public NetWorkReceiver(Context context, OnNetWorkListener listener) {
        if (context != null && listener != null){
            this.mContext = context;
            this.mContext.registerReceiver(this, new IntentFilter(CONNECTIVITY_ACTION));
            this.netWorkListener = listener;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CONNECTIVITY_ACTION.equals(intent.getAction())){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()){
                    netWorkListener.onNetworkChange(networkInfo.getType());
                } else {
                    netWorkListener.onNetworkChange(-1);
                }
            }
        }
    }

    /**
     * 销毁网络监听广播
     */
    public void unregisterReceiver(){
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnNetWorkListener{

        /**
         * 网络连接改变
         * @param networkType 当前网络类型，-1即没有网络
         * {@link ConnectivityManager#TYPE_MOBILE},
         * {@link ConnectivityManager#TYPE_WIFI},
         * {@link ConnectivityManager#TYPE_WIMAX},
         * {@link ConnectivityManager#TYPE_ETHERNET},
         * {@link ConnectivityManager#TYPE_BLUETOOTH}，或者其他类型
         */
        void onNetworkChange(int networkType);
    }
}
