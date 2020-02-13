package cn.epark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.BasePhoneLoginTextWatcher;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.StatusBarUtil;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2020/2/07.
 * Describe: 忘记密码
 */
public class ForgetPasswordActivity extends BaseAct {


    private ImageView phoneClearBtn, codeClearBtn;
    private TextView sendCodeBtn, loginErrorTv;
    private EditText phoneEt, codeEt;

    private String phoneNum, inputCode;
    private int codeCountDownNum = 59;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        initData(getIntent());
    }

    private void initData(Intent intent) {
        if (intent != null) {
            phoneNum = intent.getStringExtra("phoneNum");
            if (!TextUtils.isEmpty(phoneNum)) {
                phoneEt.setText(phoneNum);
                phoneEt.setSelection(phoneEt.length());
            }
        }
    }

    private void initView() {
        phoneEt = findViewById(R.id.mobile_phone_et);
        phoneClearBtn = findViewById(R.id.phone_clear_iv);
        phoneClearBtn.setOnClickListener(clickListener);
        codeClearBtn = findViewById(R.id.code_clear_iv);
        codeClearBtn.setOnClickListener(clickListener);
        codeEt = findViewById(R.id.code_et);
        sendCodeBtn = findViewById(R.id.send_code_btn_tv);
        sendCodeBtn.setOnClickListener(clickListener);
        loginErrorTv = findViewById(R.id.error_msg_tv);
        findViewById(R.id.finish_btn_tv).setOnClickListener(clickListener);
        codeEt.addTextChangedListener(new LoginTextWatcher(codeClearBtn));
        phoneEt.addTextChangedListener(new BasePhoneLoginTextWatcher(phoneEt, phoneClearBtn) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phoneNum = s.toString().replace(" ", "");
            }
        });
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.phone_clear_iv:
                    phoneEt.setText("");
                    phoneEt.requestFocus();
                    break;
                case R.id.code_clear_iv:
                    codeEt.setText("");
                    codeEt.requestFocus();
                    break;
                case R.id.send_code_btn_tv:
                    if (TextUtils.isEmpty(phoneNum)) {
                        ToastUtil.showToast(context, "请输入手机号码后重试!");
                    } else if (!StringUtil.isPhoneNumber(phoneNum)) {
                        ToastUtil.showToast(context, "手机号码格式错误，请检查后重试！");
                    } else {
                        getCode(phoneNum);
                    }
                    break;
                case R.id.finish_btn_tv://下一步
                    if (checkParams()) {
                        startActivity(new Intent(context, SetPasswordActivity.class)
                            .putExtra("phoneNum", phoneNum)
                            .putExtra("code", inputCode)
                            .putExtra("forgetPwd", true)
                        );
                    }
                    break;
                default: break;
            }
        }
    };

    private boolean checkParams() {
        boolean result;
        CharSequence loginErrorMsg = null;
        inputCode = codeEt.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            loginErrorMsg = "请输入手机号码后重试!";
            result = false;
        } else if (!StringUtil.isPhoneNumber(phoneNum)) {
            loginErrorMsg = "手机号码格式错误，请检查后重试！";
            result = false;
        } else if (TextUtils.isEmpty(inputCode)) {
            loginErrorMsg = "请输入验证码后重试！";
            result = false;
        } else {
            result = true;
        }
        SMSLoginActivity.setLoginErrorInfo(loginErrorTv, loginErrorMsg);
        return result;
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

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }
}
