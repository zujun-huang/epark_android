package cn.epark.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.epark.App;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.adapters.BaseRecyclerViewAdapter;
import cn.epark.adapters.CommonItemTouchHelperCallback;
import cn.epark.adapters.UploadPicAdapter;
import cn.epark.utils.LogUtil;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.SoftHideKeyBoardUtil;
import cn.epark.utils.StringUtil;
import cn.epark.view.ScrollEditText;

/**
 * Created by huangzujun on 2020/3/17.
 * Describe: 意见反馈
 */
public class feedbackActivity extends BaseAct implements BaseRecyclerViewAdapter.OnItemClickListener {

    private TextView title_tv;
    private ScrollEditText et_question;
    private EditText et_contact;
    private RecyclerView rv_img;

    private List<String> imgPathList;
    private UploadPicAdapter uploadPicAdapter;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        SoftHideKeyBoardUtil.assistActivity(this);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.feedback);
        et_question = findViewById(R.id.et_question);
        et_contact = findViewById(R.id.et_contact);
        findViewById(R.id.submit_btn).setOnClickListener(clickListener);
        rv_img = findViewById(R.id.rv_img);
        rv_img.setLayoutManager(new GridLayoutManager(context, 3));
        imgPathList = new ArrayList<>();
//        imgPathList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2107714412,3147537458&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3070114061,2076007878&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3324650843,4203827181&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2743621153,3952756650&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3489150134,2737835973&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2550393541,65822338&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2816433753,267880517&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3738614737,1539088837&fm=26&gp=0.jpg");
//        imgPathList.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3457241862,1157858476&fm=26&gp=0.jpg");
        uploadPicAdapter = new UploadPicAdapter(imgPathList);
        uploadPicAdapter.setOnItemClickListener(this);
        rv_img.setAdapter(uploadPicAdapter);
        itemTouchHelper = new ItemTouchHelper(new CommonItemTouchHelperCallback(uploadPicAdapter, false));
        itemTouchHelper.attachToRecyclerView(rv_img);
        if (!StringUtil.isEmpty(App.getAccount().getTelphone())) {
            et_contact.setText(App.getAccount().getTelphone());
            et_contact.setSelection(App.getAccount().getTelphone().length());
        }
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
        showAlertDialog(getString(R.string.modal_dialog_tip), "获取相册权限失败,\n请授权后重试~", "去设置", v -> {
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
                default:
                    break;
            }
        }
    };

    @Override
    public void OnItemClick(RecyclerView.ViewHolder holder) {
        LogUtil.i(TAG, "OnItemClick --> position:" + holder.getAdapterPosition());
    }

    @Override
    public boolean OnItemLongClick(RecyclerView.ViewHolder holder) {
        boolean isStartDrag = false;
        if (itemTouchHelper != null && uploadPicAdapter != null && holder != null) {
            if (uploadPicAdapter.getItemCount() > 2) {
                if (uploadPicAdapter.getList().size() == uploadPicAdapter.MAX_IMG_NUMBER
                        || holder.getAdapterPosition() != uploadPicAdapter.getItemCount() - 1) {
                    isStartDrag = true;
                }
            }
            //总数大于等于2、数据总数等于9或者不是最后一个开启拖拽
            if (isStartDrag) {
                itemTouchHelper.startDrag(holder);
            }
        }
        return isStartDrag;
    }
}
