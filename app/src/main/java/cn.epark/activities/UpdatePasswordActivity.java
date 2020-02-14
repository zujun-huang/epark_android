package cn.epark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.LoginTextWatcher;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2020/2/07.
 * Describe: 修改密码
 */
public class UpdatePasswordActivity extends BaseAct {

    private ImageView oldPwdClearBtn, pwdClearBtn, oldPwdShowBtn, pwdShowBtn;
    private EditText oldPwdEt, pwdEt;

    private String oldPwd, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initViews();
    }

    private void initViews() {
        View titleView = findViewById(R.id.title_layout);
        if (titleView != null) {
            TextView titleTv = titleView.findViewById(R.id.title_tv);
            titleTv.setText(R.string.update_pwd);
        }
        oldPwdEt = findViewById(R.id.input_old_pwd_et);
        oldPwdClearBtn = findViewById(R.id.pwd_old_clear_iv);
        oldPwdShowBtn = findViewById(R.id.pwd_old_show_iv);
        pwdEt = findViewById(R.id.input_pwd_et);
        pwdClearBtn = findViewById(R.id.pwd_clear_iv);
        pwdShowBtn = findViewById(R.id.pwd_show_iv);
        oldPwdClearBtn.setOnClickListener(clickListener);
        oldPwdShowBtn.setOnClickListener(clickListener);
        pwdClearBtn.setOnClickListener(clickListener);
        pwdShowBtn.setOnClickListener(clickListener);
        findViewById(R.id.finish_btn_tv).setOnClickListener(clickListener);
        findViewById(R.id.forget_pwd_tv).setOnClickListener(clickListener);
        oldPwdEt.addTextChangedListener(new LoginTextWatcher(oldPwdClearBtn));
        pwdEt.addTextChangedListener(new LoginTextWatcher(pwdClearBtn));
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.pwd_old_clear_iv:
                    oldPwdEt.setText("");
                    oldPwdEt.requestFocus();
                    break;
                case R.id.pwd_clear_iv:
                    pwdEt.setText("");
                    pwdEt.requestFocus();
                    break;
                case R.id.pwd_old_show_iv:
                    changeShowPwd(oldPwdEt.isSelected(), oldPwdEt, oldPwdShowBtn);
                    break;
                case R.id.pwd_show_iv:
                    changeShowPwd(pwdEt.isSelected(), pwdEt, pwdShowBtn);
                    break;
                case R.id.finish_btn_tv:
                    if (checkPwd()) {
                        updatePwd();
                    }
                    break;
                case R.id.forget_pwd_tv:
                    startActivity(new Intent(context, ForgetPasswordActivity.class)
                            .putExtra("phoneNum", App.getAccount().getTelphone())
                    );
                    break;
                default: break;
            }
        }
    };

    private void updatePwd() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getSessionId());
        params.put("oldPwd", oldPwd);
        params.put("newPwd", pwd);
        httpPost(App.URL + URLConstant.URL_UPDATE_PWD, params, URLConstant.ACTION_UPDATE_PWD);
    }

    private void changeShowPwd(boolean isShow, EditText editText, ImageView showView) {
        editText.setSelected(isShow);
        showView.setImageResource(isShow ? R.mipmap.show_pwd : R.mipmap.close_pwd);
        editText.setTransformationMethod(isShow? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        editText.setSelection(editText.length());
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_UPDATE_PWD:
                handler.obtainMessage(SHOW_TOAST, "新密码设置成功！").sendToTarget();
                App.getAccount().setPwd(pwd);
                finish();
                break;
            default: super.onResponseOk(data, actionCode);
        }
    }

    private boolean checkPwd() {
        boolean result;
        String loginErrorMsg = null;
        oldPwd = oldPwdEt.getText().toString();
        pwd = pwdEt.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            loginErrorMsg = "请输入原密码后重试!";
            result = false;
        } else if (oldPwd.length() < 8 || oldPwd.length() > 16) {
            loginErrorMsg = "原密码长度有误，请检查后重试！";
            result = false;
        } else if (TextUtils.isEmpty(pwd)){
            loginErrorMsg = "请输入新密码后重试！";
            result = false;
        } else if (pwd.length() < 8 || pwd.length() > 16) {
            loginErrorMsg = "新密码长度有误，请检查后重试！";
            result = false;
        } else {
            result = true;
        }
        ToastUtil.showToast(context, loginErrorMsg);
        return result;
    }
}
