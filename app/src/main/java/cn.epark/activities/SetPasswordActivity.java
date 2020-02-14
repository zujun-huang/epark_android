package cn.epark.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.ErrorCode;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.StatusBarUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2020/2/07.
 * Describe: 新用户设置密码 | 忘记密码最后一步
 */
public class SetPasswordActivity extends BaseAct {


    private ImageView pwdClearBtn, pwdShowBtn;
    private EditText passwordEt;

    private String newPwd, phoneNum, code;
    private boolean isForgetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
        initData(getIntent());
    }

    private void initData(Intent intent) {
        if (intent != null) {
            phoneNum = intent.getStringExtra("phoneNum");
            code = intent.getStringExtra("code");
            isForgetPwd = intent.getBooleanExtra("forgetPwd", false);
        }
    }

    private void initView() {
        passwordEt = findViewById(R.id.input_pwd_et);
        pwdClearBtn = findViewById(R.id.pwd_clear_iv);
        pwdClearBtn.setOnClickListener(clickListener);
        pwdShowBtn = findViewById(R.id.pwd_show_iv);
        pwdShowBtn.setOnClickListener(clickListener);
        findViewById(R.id.finish_btn_tv).setOnClickListener(clickListener);
        passwordEt.addTextChangedListener(new LoginTextWatcher(pwdClearBtn));
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.pwd_clear_iv:
                    passwordEt.setText("");
                    passwordEt.requestFocus();
                    break;
                case R.id.pwd_show_iv://明码暗码切换
                    changeShowPwd(!pwdShowBtn.isSelected());
                    break;
                case R.id.finish_btn_tv://完成
                    if (checkInputPassword()) {
                        if (isForgetPwd) {
                            setForgetPwd();
                        } else {
                            setUserPassword();
                        }
                    }
                    break;
                default: break;
            }
        }
    };

    private void changeShowPwd(boolean isShow) {
        pwdShowBtn.setSelected(isShow);
        pwdShowBtn.setImageResource(isShow ? R.mipmap.show_pwd : R.mipmap.close_pwd);
        passwordEt.setTransformationMethod(isShow? HideReturnsTransformationMethod.getInstance() :PasswordTransformationMethod.getInstance());
        passwordEt.setSelection(passwordEt.length());
    }

    private boolean checkInputPassword() {
        boolean result;
        newPwd = passwordEt.getText().toString();
        int length = newPwd.length();
        if (length < 8 || length > 16) {
            result = false;
            ToastUtil.showToast(context, "密码长度有误，请重新输入！");
        } else {
            result = true;
        }
        return result;
    }

    private void setForgetPwd() {
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(code)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>(6);
        params.put("telphone", phoneNum);
        params.put("yzm", code);
        params.put("newPwd", newPwd);
        httpPost(App.URL + URLConstant.URL_FORGET_PWD, params, URLConstant.ACTION_FORGET_PWD);
    }

    private void setUserPassword() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getSessionId());
        params.put("newPwd", newPwd);
        httpPost(App.URL + URLConstant.URL_UPDATE_PWD, params, URLConstant.ACTION_UPDATE_PWD);
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_UPDATE_PWD:
                App.getAccount().setPwd(newPwd);
            case URLConstant.ACTION_FORGET_PWD:
                handler.obtainMessage(SHOW_TOAST, "新密码设置成功！").sendToTarget();
                setResult(Activity.RESULT_OK);
                finish();
                break;
            default:
                super.onResponseOk(data, actionCode);
                break;
        }
    }

    @Override
    public void onResponseFail(int errorCode, String errorMsg, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_UPDATE_PWD:
                if (ErrorCode.PASSWORD == errorCode) {
                    handler.obtainMessage(SHOW_TOAST, "密码格式错误，请重新输入后重试！").sendToTarget();
                } else {
                    handler.obtainMessage(SHOW_TOAST, getString(R.string.unknow_error)).sendToTarget();
                }
                break;
            case URLConstant.ACTION_FORGET_PWD:
                if (ErrorCode.PASSWORD == errorCode) {
                    handler.obtainMessage(SHOW_TOAST, "密码格式错误，请重新输入后重试！").sendToTarget();
                } else {
                    handler.obtainMessage(SHOW_TOAST, errorCode, -1, errorMsg).sendToTarget();
                }
                break;
            default: super.onResponseFail(errorCode, errorMsg, actionCode);
        }
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }
}
