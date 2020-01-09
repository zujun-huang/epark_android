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
}
