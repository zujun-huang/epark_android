package cn.epark.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.adapters.BasePhoneLoginTextWatcher;
import cn.epark.bean.Account;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2020/2/07.
 * Describe: 修改密码
 */
public class UpdatePasswordActivity extends BaseAct {

    public static final String SHAREPREFERENCES_USER_INPUT_PHONE = "USER_INPUT_PHONE";

    private ImageView closeBtn, phoneClearBtn, codeClearBtn, wxLoginIv, qqLoginIv;
    private EditText phoneEt, codeEt;
    private TextView sendCodeBtn, loginBtn, pwdLoginBtn, loginErrorTv;

    private String phoneNum, inputCode;
    private int codeCountDownNum = 59;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initView();
        initData();
    }

    private void initData() {
        String sharePhone = ShareUtil.newInstance().getLocData(context, SHAREPREFERENCES_USER_INPUT_PHONE);
        if (!TextUtils.isEmpty(sharePhone)) {
            phoneEt.setText(sharePhone);
            phoneEt.setSelection(phoneEt.getText().length());
        }
    }

    private void initView() {
        closeBtn = findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(clickListener);
        phoneEt = findViewById(R.id.mobile_phone_et);
        phoneClearBtn = findViewById(R.id.phone_clear_iv);
        phoneClearBtn.setOnClickListener(clickListener);
        codeClearBtn = findViewById(R.id.code_clear_iv);
        codeClearBtn.setOnClickListener(clickListener);
        codeEt = findViewById(R.id.code_et);
        sendCodeBtn = findViewById(R.id.send_code_btn_tv);
        sendCodeBtn.setOnClickListener(clickListener);
        loginBtn = findViewById(R.id.login_btn_tv);
        loginBtn.setOnClickListener(clickListener);
        pwdLoginBtn = findViewById(R.id.pwd_login_tv);
        pwdLoginBtn.setOnClickListener(clickListener);
        wxLoginIv = findViewById(R.id.iv_wx);
        wxLoginIv.setOnClickListener(clickListener);
        qqLoginIv = findViewById(R.id.iv_qq);
        qqLoginIv.setOnClickListener(clickListener);
        loginErrorTv = findViewById(R.id.error_msg_tv);
        codeEt.addTextChangedListener(new LoginTextWatcher(codeClearBtn));
        phoneEt.addTextChangedListener(new BasePhoneLoginTextWatcher(phoneEt, phoneClearBtn) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() == 13) {
                    phoneNum = s.toString().replace(" ", "");
                }
            }
        });
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.close_btn:
                    finish();
                    break;
                case R.id.code_clear_iv://清除验证码
                    codeEt.setText("");
                    codeEt.requestFocus();
                    break;
                case R.id.phone_clear_iv://一键清除号码
                    phoneEt.setText("");
                    phoneEt.requestFocus();
                    break;
                case R.id.send_code_btn_tv:
                    if (TextUtils.isEmpty(phoneNum)) {
                        ToastUtil.showToast(context, "请输入手机号码后重试!");
                    }  else if (!StringUtil.isPhoneNumber(phoneNum)) {
                        ToastUtil.showToast(context, "手机号码格式错误，请检查后重试！");
                    } else {
                        getCode(phoneNum);
                    }
                    break;
                case R.id.login_btn_tv:
                    if (checkLogin()) {
                        smsLogin();
                    }
                    break;
                case R.id.pwd_login_tv:
                    break;
                case R.id.iv_wx:
                    break;
                case R.id.iv_qq:
                    break;
                default: break;
            }
        }
    };

    private void smsLogin() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("telphone", phoneNum);
        params.put("yzm", inputCode);
        httpPost(App.URL + URLConstant.URL_LOGIN_OTP, params, URLConstant.ACTION_LOGIN_OTP);
    }

    private void getCode(String phoneNum) {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("telphone", phoneNum);
        httpPost(App.URL + URLConstant.URL_GET_OTP, params, URLConstant.ACTION_GET_OTP);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case URLConstant.ACTION_GET_OTP:
                sendCodeBtn.setEnabled(false);
                ToastUtil.showToast(context, "验证码已发送，请注意查收！");
                ShareUtil.newInstance().getShared(context).edit()
                        .putString(SHAREPREFERENCES_USER_INPUT_PHONE, phoneNum)
                        .apply();
                break;
            default: super.handleMessage(msg);
        }

    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_GET_OTP:
                handler.removeCallbacks(codeCountDown);
                handler.postDelayed(codeCountDown, 1000);
                handler.obtainMessage(URLConstant.ACTION_GET_OTP).sendToTarget();
                break;
            case URLConstant.ACTION_LOGIN_OTP:
                handler.removeCallbacks(codeCountDown);
                App.getInstance().setAccount(JSON.parseObject(data.toString(), Account.class));
                App.getInstance().startSessionTimer();
                if (App.getAccount().getPwdIsNull()) {
                    //todo 设置密码
                } else {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
            default: super.onResponseOk(data, actionCode);
        }
    }

    private Runnable codeCountDown = new Runnable() {
        @Override
        public void run() {
            sendCodeBtn.setText(String.format(getString(R.string.countdown_send_sms), codeCountDownNum));
            if (codeCountDownNum-- <= 0) {
                sendCodeBtn.setEnabled(true);
                sendCodeBtn.setText(R.string.send_sms);
                codeCountDownNum = 59;
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private boolean checkLogin() {
        boolean result;
        CharSequence loginErrorMsg = null;
        inputCode = codeEt.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            loginErrorMsg = "请输入手机号码后重试!";
            result = false;
        } else if (!StringUtil.isPhoneNumber(phoneNum)) {
            loginErrorMsg = "手机号码格式错误，请检查后重试！";
            result = false;
        } else if (TextUtils.isEmpty(inputCode)){
            loginErrorMsg = "请输入验证码后重试！";
            result = false;
        } else {
            result = true;
        }
        setLoginErrorInfo(loginErrorMsg);
        return result;
    }

    private void setLoginErrorInfo(CharSequence loginErrorMsg) {
        if (!TextUtils.isEmpty(loginErrorMsg)) {
            loginErrorTv.setVisibility(View.VISIBLE);
            loginErrorTv.setText(loginErrorMsg);
        } else {
            loginErrorTv.setVisibility(View.GONE);
        }
    }
}
