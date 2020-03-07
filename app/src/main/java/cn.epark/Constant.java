package cn.epark;

/**
 * @作者 huangzujun
 * @日期 2019-07-24
 * @描述 常量
 */
public interface Constant {

    /**
     * 支付方式
     */
    String[] PAYS = new String[]{"现金支付(暂未开通易派克)", "易派克支付", "微信支付", "支付宝支付"};

    /**
     * 默认检索范围 300米
     */
    Integer SEARCH_RADIUS = 300;

    /**
     * 高德 key
     */
    String AMAP_KEY = "13acae4efeb2ca8add837d33d1a58b1d";

    /**
     * 附近关键词
     */
    String[] TAB_TITLES = {"美食", "酒店", "玩乐", "生活"};

    /**
     * 3小时=180min，177min=2小时57分钟
     */
    int SESSION_MAX = 177;

    /** 用户本地头像 */
    String USER_HEAD_IMG = "head_img";

}
