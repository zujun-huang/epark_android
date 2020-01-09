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
}
