package cn.epark;

/**
 * created huangzujun on 2020/1/9
 * Company:重庆帮考教育科技有限公司
 * Describe: 请求错误定义
 */
public interface ErrorCode {

    String STATE_FAIL = "fail";
    String STATE_OK = "success";

    /** sessionID不合法 */
    int ILLEGAL_SESSIONID = 10005;

    /** 密码格式或复杂度 */
    int PASSWORD = 20005;

    /** 登录密码错误 */
    int LOGIN_PASSWORD = 20006;

    /** 登录用户名不存在 */
    int LOGIN_PASSWORD_USER = 20001;
}
