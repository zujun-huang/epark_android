package cn.epark.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cn.epark.App;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.utils.OnMultiClickListener;

/**
 * Created by huangzujun on 2020/3/17.
 * Describe: 意见反馈
 */
public class feedbackActivity extends BaseAct {

    private TextView title_tv;
    private EditText et_question, et_contact;
    private LinearLayout ll_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.feedback);
        et_question = findViewById(R.id.et_question);
        et_contact = findViewById(R.id.et_contact);
        ll_pic = findViewById(R.id.ll_pic);
        findViewById(R.id.iv_add_pic).setOnClickListener(clickListener);
        findViewById(R.id.submit_btn).setOnClickListener(clickListener);
    }

    private void submitFeedback(String questionStr) {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getEncryptionSession());
        params.put("proContent", questionStr);
//        params.put("imgJson", questionStr);
        httpPost(App.URL + URLConstant.URL_SUBMIT_FEEDBACK, params, URLConstant.ACTION_SUBMIT_FEEDBACK);
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_SUBMIT_FEEDBACK:
                handler.obtainMessage(SHOW_TOAST, "提交成功！").sendToTarget();
                break;
            default:
                super.onResponseOk(data, actionCode);
        }
    }

    /**
     * 显示没有权限手动设置框
     */
    private void showNoPermissionsToast() {
        showAlertDialog(getString(R.string.modal_dialog_tip), "获取拍摄权限失败,\n请授权后重试~", "去设置", v -> {
            dismiss();
            //用户手动授权
            Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.parse("package:" + context.getPackageName());
            settingIntent.setData(uri);
            startActivity(settingIntent);
        });
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.submit_btn:
                    String questionStr = et_question.getText().toString();
                    if(TextUtils.isEmpty(questionStr)){
                        showTip("请先描述您要反馈的问题和意见~");
                    } else {
                        submitFeedback(questionStr);
                    }
                    break;
                case R.id.iv_add_pic:
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(mainAct, Manifest.permission.CAMERA)) {
//                            showNoPermissionsToast();
//                        } else {
//                            ActivityCompat.requestPermissions(mainAct, new String[]{
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO_PERMISSION
//                            );
//                        }
//                    } else {
//                        showHeadImgModal();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

}
