package cn.epark.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.epark.App;
import cn.epark.BuildConfig;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.bean.AppVersion;
import cn.epark.utils.CacheManagerUtil;
import cn.epark.utils.LogUtil;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.StringUtil;

/**
 * Created by huangzujun on 2020/2/13.
 * Describe: 设置页
 */
public class SettingsActivity extends BaseAct {

    private TextView phoneNumTv, clearCacheTv, appVersionTv;
    private View versionNewView;
    private Dialog updateDialog;

    private AppVersion versionNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        initData();
    }

    private void initData() {
        getAppVersionInfo();
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
        appVersionTv.setText(String.format(getString(R.string.current_version), BuildConfig.VERSION_NAME));
        findViewById(R.id.update_pwd_rl).setOnClickListener(clickListener);
        findViewById(R.id.check_version_rl).setOnClickListener(clickListener);
        findViewById(R.id.clear_cache_rl).setOnClickListener(clickListener);
        findViewById(R.id.feedback_rl).setOnClickListener(clickListener);
        findViewById(R.id.change_user_btn_tv).setOnClickListener(clickListener);
        findViewById(R.id.login_out_btn_tv).setOnClickListener(clickListener);
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.update_pwd_rl:
                    startActivity(new Intent(context, UpdatePasswordActivity.class));
                    break;
                case R.id.check_version_rl:
                    checkVersion();
                    break;
                case R.id.clear_cache_rl:
                    showAlertDialog("确认清除吗？",
                            "清除缓存会导致个人缓存信息删除，\n是否确认清除？",
                            "确认", view -> {
                                CacheManagerUtil.getInstance().clearAllCache(context);
                                initViews();
                            }, null);
                    break;
                case R.id.feedback_rl:
                    startActivity(new Intent(context, feedbackActivity.class));
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
        HashMap<String, String> params = new HashMap<>(3);
        params.put("page", "1");
        params.put("size", "3");
        httpPost(App.URL + URLConstant.URL_GET_APP_INFO, params, URLConstant.ACTION_GET_APP_INFO, false);
    }

    private void loginOut() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getEncryptionSession());
        httpPost(App.URL + URLConstant.URL_LOGIN_OUT, params, URLConstant.ACTION_LOGIN_OUT);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case URLConstant.ACTION_GET_APP_INFO:
                versionNewView.setVisibility(versionNew != null ? View.VISIBLE : View.GONE);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    private void checkVersion() {
        if (versionNew == null) {
            showAlertDialog("当前已是最新版本，无需更新！", null);
        } else {
            showUpdateDialog(versionNew);
        }
    }

    private void showUpdateDialog(AppVersion appVersion) {
        if (appVersion == null) {
            return;
        }
        if (updateDialog == null) {
            updateDialog = new Dialog(context);
            Window dialogWindow = updateDialog.getWindow();
            if (dialogWindow != null){
                dialogWindow.setGravity(Gravity.CENTER);
                updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
            }
            View view = View.inflate(context, R.layout.layout_app_update_dialog, null);
            view.findViewById(R.id.close_btn).setOnClickListener((v)-> updateDialog.dismiss());
            view.findViewById(R.id.tv_later).setOnClickListener((v)-> updateDialog.dismiss());
            view.findViewById(R.id.tv_update).setOnClickListener((v)-> {
                view.findViewById(R.id.ll_container).setVisibility(View.GONE);
                View tv_tip = view.findViewById(R.id.tv_download_tip);
                tv_tip.setVisibility(View.VISIBLE);
                ProgressBar progressBar = view.findViewById(R.id.progress_horizontal);
                progressBar.setVisibility(View.VISIBLE);
                //todo 1、显示下载通知栏  2、下载apk并更新进度条 3、完成关闭弹框及通知栏
                
            });
            if (!StringUtil.isEmpty(appVersion.getVersion())) {
                TextView tv_version = view.findViewById(R.id.tv_version);
                tv_version.setText(String.format("新版本 V%s", appVersion.getVersion()));
            }
            updateDialog.setContentView(view);
        }
        updateDialog.show();
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_LOGIN_OUT:
                handler.obtainMessage(SHOW_TOAST, "已退出当前账号").sendToTarget();
                App.getInstance().setAccount(null);
                ShareUtil.newInstance().clearLoginUser(context);
                finish();
                break;
            case URLConstant.ACTION_GET_APP_INFO: {
                JSONArray jsonArray = data.optJSONArray("list");
                List<AppVersion> versionList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    AppVersion appVersion = JSON.parseObject(jsonArray.optString(i), AppVersion.class);
                    versionList.add(appVersion);
                }
                if (versionList.size() > 0 ) {
                    versionNew = versionList.get(0).isCurrentVersion() ? null : versionList.get(0);
                    handler.obtainMessage(URLConstant.ACTION_GET_APP_INFO).sendToTarget();
                } else {
                    LogUtil.e(TAG, getString(R.string.no_update_info));
                }
                break;
            }
            default: super.onResponseOk(data, actionCode);
        }
    }
}
