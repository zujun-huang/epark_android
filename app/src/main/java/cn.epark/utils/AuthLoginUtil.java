package cn.epark.utils;

import android.app.Activity;
import android.content.Intent;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import cn.epark.Listeners.BaseQQUiListener;
import cn.epark.bean.Account;

/**
 * created huangzujun on 2020/2/14
 * Describe: 授权登录工具类
 */
public class AuthLoginUtil {

    private Tencent mTencent;
    private Activity activity;
    private BaseQQUiListener loginListener;
    private Account account;

    public AuthLoginUtil(Activity activity, Tencent tencent) {
        this.activity = activity;
        this.mTencent = tencent;
        this.loginListener = new BaseQQUiListener(activity) {
            @Override
            public void onComplete(Object o) {
                JSONObject resp = (JSONObject) o;
                QQToken qqtoken = mTencent.getQQToken();
                mTencent.setOpenId(resp.optString("openid"));
                mTencent.setAccessToken(resp.optString("access_token"), resp.optString("expires_in"));
                UserInfo info = new UserInfo(activity.getApplicationContext(), qqtoken);
                info.getUserInfo(new BaseQQUiListener(activity) {
                    @Override
                    public void onComplete(Object o) {
                        JSONObject resp = (JSONObject) o;
                        account = new Account();
                        account.setNickName(resp.optString("nickname"));
                        account.setGender("男".equals(resp.optString("sex")) ? 1: 2);
                        account.setHead(resp.optString("figureurl_qq_2"));
                        authLogin();
                    }

                    @Override
                    public void onError(UiError uiError) {
                        super.onError(uiError);
                        ToastUtil.showToastInUIThread(activity, "登录失败，错误信息：" + uiError.errorMessage);
                    }

                    @Override
                    public void onCancel() {
                        ToastUtil.showToastInUIThread(activity, "登录取消");
                    }
                });
            }

            @Override
            public void onError(UiError uiError) {
                super.onError(uiError);
                ToastUtil.showToastInUIThread(activity, "授权失败，错误信息：" + uiError.errorMessage);
            }
        };
    }

    private void authLogin() {
        //fixme 三方登录接口
    }

    /**
     * QQ 登录
     * <p>
     *     注：必须在调用处重写onActivityResult方法并调用{@link #onActivityResult}
     * </p>
     */
    public void qqLogin() {
        if (mTencent != null && !mTencent.isSessionValid()) {
            mTencent.login(activity, "all", loginListener);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }
}
