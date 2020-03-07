package cn.epark;

/**
 * created huangzujun on 2019/12/25
 * Describe: 请求接口
 */
public interface URLConstant {

    /** 获取验证码 */
    String URL_GET_OTP = "/user/getotp";
    int ACTION_GET_OTP = 0x00000010;

    /** 短信登录 */
    String URL_LOGIN_OTP = "/user/thirdLogin";
    int ACTION_LOGIN_OTP = 0x00000011;

    /** 修改密码 */
    String URL_UPDATE_PWD = "/user/updataPwd";
    int ACTION_UPDATE_PWD = 0x00000012;

    /** 忘记密码 */
    String URL_FORGET_PWD = "/user/forgetPwd";
    int ACTION_FORGET_PWD = 0x00000013;

    /** 修改用户信息 */
    String URL_UPDATE_USER = "/user/updateUserInfo";
    int ACTION_UPDATE_USER = 0x00000014;

    /** 退出登录 */
    String URL_LOGIN_OUT = "/user/clearUserInfo";
    int ACTION_LOGIN_OUT = 0x00000015;

    /** 刷新session */
    String URL_REFRESH_SESSION = "/user/refreshSession";
    int ACTION_REFRESH_SESSION = 0x00000016;

    /** 密码登录 */
    String URL_PWD_LOGIN = "/user/login";
    int ACTION_PWD_LOGIN = 0x00000017;

    /** 获取app版本信息 */
    String URL_GET_APP_INFO = "/appInfo/getAppInfoList";
    int ACTION_GET_APP_INFO = 0x00000020;

    /** 查询用户反馈 */
    String URL_GET_FEEDBACK_LIST = "/feedback/getFeedbackList";
    int ACTION_GET_FEEDBACK_LIST = 0x00000021;

    /** 提交反馈 */
    String URL_SUBMIT_FEEDBACK = "/feedback/updateFeedback";
    int ACTION_SUBMIT_FEEDBACK = 0x00000022;

}
