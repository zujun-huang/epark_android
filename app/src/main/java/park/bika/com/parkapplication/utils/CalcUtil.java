package park.bika.com.parkapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;

/**
 * @作者 huangzujun
 * @日期 2019-07-22
 * @描述 各尺寸间的转换及两经纬度间的计算
 */
public class CalcUtil {

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float px2sp(Context context, float pxValue) {
        return pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

    public static float sp2px(Context context, float spValue) {
        return spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

    /**
     * 计算两经纬度之间真实距离
     *
     * @param latitude1  纬度1
     * @param longitude1 经度1
     * @param latitude2  纬度2
     * @param longitude2 经度1
     * @return m/km <br>
     * 返回Km
     */
    public static double getLatLngDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double lat1 = (Math.PI / 180) * latitude1;
        double lat2 = (Math.PI / 180) * latitude2;
        double lon1 = (Math.PI / 180) * longitude1;
        double lon2 = (Math.PI / 180) * longitude2;
        final double earthRadius = 6371.004;
        return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * earthRadius;
    }
}
