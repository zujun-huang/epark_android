package park.bika.com.parkapplication;

/**
 * @作者 huangzujun
 * @日期 2019-07-24
 * @描述 常量
 */
public interface Constant {

    /**
     * 选择地区返回码
     */
    int CHOOSE_AREA_CODE = 1800;

    /**
     * 选择的地区
     */
    String CHOOSE_AREA_KEY = "CHOOSE_AREA";

    /**
     * 支付方式
     */
    String[] PAYS = new String[]{"现金支付(暂未开通易派克)", "易派克支付", "微信支付", "支付宝支付"};

    /**
     * 默认检索范围 300米
     */
    Integer SEARCH_RADIUS = 300;

    /**
     * GPS请求码
     */
    int GPS_REQUEST_CODE = 3020;

    /**
     * 导航目的经纬度信息
     */
    String END_LATLNG_KEY = "endLL";

    /**
     * 导航起点信息
     */
    String START_LATLNG_KEY = "startLL";

    /**
     * 用户本地头像
     */
    String USER_HEAD_IMG = "head_img";
}
