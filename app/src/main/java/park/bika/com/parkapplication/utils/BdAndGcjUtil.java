package park.bika.com.parkapplication.utils;

import com.baidu.mapapi.model.LatLng;

/**
 * created huangzujun on 2019/9/7
 * Company:重庆帮考教育科技有限公司
 * Describe: GCJ02 与 BD09ll 坐标的转换
 */
public class BdAndGcjUtil {

    /**
     * BD-09ll 坐标转换成 GCJ-02 坐标
     */
    public static LatLng BD2GCJ(LatLng latLng) {
        double x = latLng.longitude - 0.0065, y = latLng.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        double lng = z * Math.cos(theta);
        double lat = z * Math.sin(theta);
        return new LatLng(lat, lng);
    }

    /**
     * GCJ-02 坐标转换成 BD-09ll 坐标
     */
    public static LatLng GCJ2BD(LatLng latLng) {
        double x = latLng.longitude, y = latLng.latitude;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double tempLon = z * Math.cos(theta) + 0.0065;
        double tempLat = z * Math.sin(theta) + 0.006;
        return new LatLng(tempLat, tempLon);
    }
}
