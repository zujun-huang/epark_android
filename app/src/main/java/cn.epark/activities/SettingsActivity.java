package cn.epark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.BuildConfig;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.utils.CacheManagerUtil;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.StringUtil;
import cn.epark.utils.ToastUtil;

/**
 * Created by huangzujun on 2020/2/13.
 * Describe: 设置页
 */
public class SettingsActivity extends BaseAct {

    private TextView phoneNumTv, clearCacheTv, appVersionTv;
    private View versionNewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews() {
        View titleView = findViewById(R.id.title_layout);
        if (titleView != null) {
            TextView titleTv = titleView.findViewById(R.id.title_tv);
            titleTv.setText(R.string.my_setting);
        }
        phoneNumTv = findViewById(R.id.phone_num_tv);
        String phoneNum = App.getAccount().getTelphone();
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNumTv.setText(StringUtil.hidePhoneNumber(phoneNum, 3));
        }
        versionNewView = findViewById(R.id.version_red_iv);
        clearCacheTv = findViewById(R.id.clear_cache_tv);
        clearCacheTv.setText(CacheManagerUtil.getInstance().getCacheSize(context));
        appVersionTv = findViewById(R.id.app_version_tv);
        appVersionTv.setText(BuildConfig.VERSION_NAME);
        findViewById(R.id.update_pwd_rl).setOnClickListener(clickListener);
        findViewById(R.id.check_version_rl).setOnClickListener(clickListener);
        findViewById(R.id.clear_cache_rl).setOnClickListener(clickListener);
        findViewById(R.id.feedback_rl).setOnClickListener(clickListener);
        findViewById(R.id.change_user_btn_tv).setOnClickListener(clickListener);
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.update_pwd_rl:
                    startActivity(new Intent(context, UpdatePasswordActivity.class));
                    break;
                case R.id.check_version_rl:
                    getAppVersionInfo();
                    break;
                case R.id.clear_cache_rl:
                    showAlertDialog("确认清除吗？",
                            "清除缓存会导致个人缓存信息删除，\n是否确认清除？",
                            "确认", view -> CacheManagerUtil.getInstance().clearAllCache(context));
                    break;
                case R.id.feedback_rl:
                    break;
                case R.id.change_user_btn_tv:
                    break;
                case R.id.login_out_btn_tv:
                    loginOut();
                    break;
                default: //Do nothing
            }
        }
    };

    private void getAppVersionInfo() {
        HashMap<String, String> params = new HashMap<>(6);
        //fixme 接口未完
        httpPost(App.URL + URLConstant.URL_GET_APP_INFO, params, URLConstant.ACTION_GET_APP_INFO);
    }

    private void loginOut() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getSessionId());
        httpPost(App.URL + URLConstant.URL_LOGIN_OUT, params, URLConstant.ACTION_LOGIN_OUT);
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_LOGIN_OUT:
                ToastUtil.showToast(context, "已退出当前账号");
                App.getInstance().setAccount(null);
                finish();
                break;
            case URLConstant.ACTION_GET_APP_INFO:
                showAlertDialog("温馨提示",
                        "当前已是最新版本，无需更新！", "确定", v -> dismiss());
                break;
            default: super.onResponseOk(data, actionCode);
        }
    }
}
