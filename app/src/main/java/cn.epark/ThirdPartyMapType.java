package cn.epark;

/**
 * created huangzujun on 2019/9/7
 * Describe: 第三方地图信息
 */
public interface ThirdPartyMapType {

    //百度地图包名
    String BAIDUMAP_PACKAGENAME = "com.baidu.BaiduMap";

    //高德地图包名
    String AUTONAVI_PACKAGENAME = "com.autonavi.minimap";

    //腾讯地图包名
    String TENCENTMAP_PACKAGENAME = "com.tencent.map";

    //百度地图申请码
    int BAIDUMAP_REQUSTCODE = 0x001000;

    //高德地图申请码
    int GDMAP_REQUSTCODE = 0x002000;

    //腾讯地图申请码
    int TENCENTMAP_REQUSTCODE = 0x003000;
}
