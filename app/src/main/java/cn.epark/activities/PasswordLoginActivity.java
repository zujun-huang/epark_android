package cn.epark.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.ErrorCode;
import cn.epark.MainBar;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.BasePhoneLoginTextWatcher;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.bean.Account;
import cn.epark.fragments.MyselfFragment;
import cn.epark.utils.AuthLoginUtil;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.StringUtil;

/**
 * Created by huangzujun on 2020/02/07.
 * Describe: 账号密码登录
 */
public class PasswordLoginActivity extends BaseAct {

    private ImageView phoneClearBtn, pwdClearBtn, pwdShowBtn;
    private EditText phoneEt, pwdEt;
    private TextView errorTipTv;

    private String phoneNum, pwdNum;
    private AuthLoginUtil authLoginUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);
        initView();
        initData(getIntent());
    }

    private void initData(Intent intent) {
        if (intent != null) {
            String sharePhone = ShareUtil.newInstance().getLocData(context, SMSLoginActivity.SHAREPREFERENCES_USER_INPUT_PHONE);
            if (!TextUtils.isEmpty(sharePhone)) {
                phoneEt.setText(sharePhone);
                phoneEt.setSelection(phoneEt.length());
            }
        }
        authLoginUtil = new AuthLoginUtil(this, mTencent);
    }

    private void initView() {
        phoneEt = findViewById(R.id.mobile_phone_et);
        phoneEt = findViewById(R.id.mobile_phone_et);
        pwdEt = findViewById(R.id.pwd_et);
        phoneClearBtn = findViewById(R.id.phone_clear_iv);
        phoneClearBtn.setOnClickListener(clickListener);
        pwdClearBtn = findViewById(R.id.pwd_clear_iv);
        pwdClearBtn.setOnClickListener(clickListener);
        pwdShowBtn = findViewById(R.id.pwd_show_iv);
        pwdShowBtn.setOnClickListener(clickListener);
        errorTipTv = findViewById(R.id.error_msg_tv);
        findViewById(R.id.login_btn_tv).setOnClickListener(clickListener);
        findViewById(R.id.sms_login_tv).setOnClickListener(clickListener);
        findViewById(R.id.forget_pwd_tv).setOnClickListener(clickListener);
        findViewById(R.id.close_btn).setOnClickListener(clickListener);
        findViewById(R.id.iv_wx).setOnClickListener(clickListener);
        findViewById(R.id.iv_qq).setOnClickListener(clickListener);
        phoneEt.addTextChangedListener(new BasePhoneLoginTextWatcher(phoneEt, phoneClearBtn) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phoneNum = s.toString().replace(" ", "");
            }
        });
        pwdEt.addTextChangedListener(new LoginTextWatcher(pwdClearBtn));
    }


    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.phone_clear_iv:
                    phoneEt.setText("");
                    phoneEt.requestFocus();
                    break;
                case R.id.pwd_clear_iv:
                    pwdEt.setText("");
                    pwdEt.requestFocus();
                    break;
                case R.id.pwd_show_iv:
                    changeShowPwd(!pwdShowBtn.isSelected());
                    break;
                case R.id.login_btn_tv:
                    if (checkLogin()) {
                        ShareUtil.newInstance().getShared(context).edit()
                                .putString(SMSLoginActivity.SHAREPREFERENCES_USER_INPUT_PHONE, phoneNum)
                                .apply();
                        pwdLogin();
                    }
                    break;
                case R.id.close_btn:
                    finish();
                    break;
                case R.id.sms_login_tv:
                    startActivity(new Intent(context, SMSLoginActivity.class));
                    finish();
                    break;
                case R.id.forget_pwd_tv:
                    startActivity(new Intent(context, ForgetPasswordActivity.class)
                            .putExtra("phoneNum", phoneNum)
                    );
                    break;
                case R.id.iv_qq:
                    authLoginUtil.qqLogin();
                    break;
                case R.id.iv_wx:
                    break;
                default: super.onClick(v);
            }
        }
    };

    private boolean checkLogin() {
        boolean result;
        CharSequence errorTip = null;
        pwdNum = pwdEt.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            errorTip = "请输入手机号码后重试!";
            result = false;
        } else if (!StringUtil.isPhoneNumber(phoneNum)) {
            errorTip = "手机号码格式错误，请检查后重试！";
            result = false;
        } else if (TextUtils.isEmpty(pwdNum)) {
            errorTip = "请输入密码后重试!";
            result = false;
        } else {
            result = true;
        }
        SMSLoginActivity.setLoginErrorInfo(errorTipTv, errorTip);
        return  result;
    }

    private void pwdLogin() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("userName", phoneNum);
        params.put("pwd", pwdNum);
        httpPost(App.URL + URLConstant.URL_PWD_LOGIN, params, URLConstant.ACTION_PWD_LOGIN);
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_PWD_LOGIN:
                App.getInstance().setAccount(JSON.parseObject(data.toString(), Account.class));
                if (App.getAccount().getPwdIsNull()) {
                    startActivityForResult(new Intent(context, SetPasswordActivity.class), MyselfFragment.REQUEST_LOGIN);
                } else {
                    MainActivity.actShowBar(context, MainBar.MYSELF_PAGE);
                }
                break;
            default: super.onResponseOk(data, actionCode);
        }
    }

    @Override
    public void onResponseFail(int errorCode, String errorMsg, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_PWD_LOGIN:
                if (errorCode == ErrorCode.LOGIN_PASSWORD ||
                        errorCode == ErrorCode.LOGIN_PASSWORD_USER) {
                    handler.obtainMessage(SHOW_TOAST, "用户名或密码错误！").sendToTarget();
                }
                break;
            default: super.onResponseFail(errorCode, errorMsg, actionCode);
        }
    }

    private void changeShowPwd(boolean isShow) {
        pwdShowBtn.setSelected(isShow);
        pwdShowBtn.setImageResource(isShow ? R.mipmap.show_pwd : R.mipmap.close_pwd);
        pwdEt.setTransformationMethod(isShow? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        pwdEt.setSelection(pwdEt.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case MyselfFragment.REQUEST_LOGIN:
                if (resultCode != Activity.RESULT_OK) {
                    pwdEt.setText("");
                    pwdEt.requestFocus();
                    App.getInstance().setAccount(null);
                } else {
                    MainActivity.actShowBar(context, MainBar.MYSELF_PAGE);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
