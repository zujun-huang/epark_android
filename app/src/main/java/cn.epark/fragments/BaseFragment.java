package cn.epark.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.epark.App;
import cn.epark.ErrorCode;
import cn.epark.R;
import cn.epark.activities.SMSLoginActivity;
import cn.epark.adapters.ModalAdapter;
import cn.epark.bean.ModalBean;
import cn.epark.utils.LogUtil;
import cn.epark.utils.OkHttpUtil;
import cn.epark.utils.TextDialogUtil;
import cn.epark.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * created huangzujun on 2019/8/31
 * Describe: fragment 基类
 */
public class BaseFragment extends Fragment {

    public final int SHOW_TOAST = 0x00000009;

    protected Context context;
    private Dialog modalDialog, loadingDialog;
    private TextDialogUtil textDialog;
    protected String TAG;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    if (msg.arg1 == ErrorCode.SERVICES_ERROR) {
                        ToastUtil.showToast(context, getString(R.string.unknow_error));
                    } else {
                        ToastUtil.showToast(context, (String) msg.obj);
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    BaseFragment.this.handleMessage(msg);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        TAG = "ePark";
    }

    class BaseCallback implements Callback {
        private int actionCode;
        private boolean autoDismiss;

        BaseCallback(int actionCode, boolean autoDismiss){
            this.actionCode = actionCode;
            this.autoDismiss = autoDismiss;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            LogUtil.e(TAG, e.getMessage());
            onResponseError(call, actionCode);
            if (autoDismiss){
                dismissLoadingDialog();
            }
        }

        @Override
        public void onResponse(Call call, okhttp3.Response response) throws IOException {
            if (response.code() == 200) {
                onResponseOk(response.body().string(), actionCode);
            } else {
                onResponseFail(call, response);
            }
            if (autoDismiss){
                dismissLoadingDialog();
            }
        }
    }

    //actionCode 请求码，回调onResponseOk
    public void httpGet(String url, HashMap<String, String> params, int actionCode, boolean showDialog, boolean autoDismiss){
        if (showDialog){
            showLoadingDialog();
        }
        OkHttpUtil.getInstance(context).request(OkHttpUtil.Method.GET, url, params, new BaseCallback(actionCode, autoDismiss));
    }

    public void httpPost(String url, HashMap<String, String> params, int actionCode){
        httpPost(url, params, actionCode, true, true);
    }

    public void httpPost(String url, HashMap<String, String> params, int actionCode, boolean showDialog, boolean autoDismiss ){
        if (showDialog){
            showLoadingDialog();
        }
        OkHttpUtil.getInstance(context).request(OkHttpUtil.Method.POST, url, params, new BaseCallback(actionCode, autoDismiss));
    }

    public void onResponseOk(String response, int actionCode) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.optJSONObject("data");
            if ("success".equals(jsonObject.optString("status"))){
                onResponseOk(data, actionCode);
            } else {
                onResponseFail(data.optInt("errCode"), data.optString("errMsg"), actionCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_TOAST, getString(R.string.unknow_error)).sendToTarget();
        }
    }

    public void onResponseOk(JSONObject data, int actionCode){

    }

    //业务返回错误
    public void onResponseFail(Call call, Response response) throws IOException {
        LogUtil.e("okHttp", " response code:" + response.code() + " body:" +  response.body().string());
    }

    public void onResponseFail(int errorCode, String errorMsg, int actionCode) {
        LogUtil.e("okHttp", " errorCode:" + errorCode + " errorMsg:" +  errorMsg);
        Message message = new Message();
        message.what = SHOW_TOAST;
        message.obj = errorMsg;
        message.arg1 = errorCode;
        handler.sendMessage(message);
    }

    //网络错误
    public void onResponseError(Call call, int actionCode){
        showNetWorkToast();
    }

    public boolean isLogin() {
        return isLogin(true);
    }

    /**
     * 登录否
     * @param shoTip 是否显示提示
     * @return true：未登录
     */
    public boolean isLogin(boolean shoTip) {
        boolean result = App.getAccount().isEmptyId();
        if (result && shoTip) {
            showAlertDialog("温馨提示", "您当前处于未登录状态，请登录后再尝试此操作~",
                    "立即登录", v -> startActivity(new Intent(context, SMSLoginActivity.class)),
                    null);
        }
        return result;
    }

    public void showNetWorkToast(){
        handler.obtainMessage(SHOW_TOAST, getString(R.string.network_error)).sendToTarget();
    }

    public void dismissLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void showNetworkErrorToast(){
        ToastUtil.showLongToast(context, getString(R.string.network_error));
    }

    public void showLoadingDialog(){
        showLoadingDialog("正在加载...");
    }

    //显示加载进度
    public void showLoadingDialog(String message) {
        if (context == null) {
            return;
        }
        TextView tipTextView;
        if (loadingDialog == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null);
            tipTextView = view.findViewById(R.id.tipTextView);
            if (!TextUtils.isEmpty(message)){
                tipTextView.setText(message);
            }
            loadingDialog = new Dialog(context, R.style.loading_dialog_style);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setContentView(view.findViewById(R.id.dialog_loading_view), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            Window window = loadingDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(lp);
        } else if (loadingDialog.isShowing()) {
            tipTextView = loadingDialog.findViewById(R.id.tipTextView);
            if (!TextUtils.isEmpty(message)){
                tipTextView.setText(message);
            }
        }
        loadingDialog.show();
    }

    //获取resId对应组件的资源文本
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String msg) {
        showToast(msg, null);
    }

    //显示警示框(没有取消按钮及标题)
    public void showToast(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg)) {
            showAlertDialog(null, msg, listener, false);
        }
    }

    //显示警示框(没有取消按钮，有默认标题)
    public void showAlertDialog(String msg, View.OnClickListener listener) {
        if (!TextUtils.isEmpty(msg)) {
            showAlertDialog(getString(R.string.modal_dialog_tip), msg, listener);
        }
    }

    //显示警示框(有取消按钮，有默认标题)
    public void showAlertDialog(String msg, View.OnClickListener positive_listener, View.OnClickListener negative_listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(getString(R.string.modal_dialog_tip), msg)
                .setPositiveButton(positive_listener)
                .setNegativeButton(negative_listener);
        textDialog.show();
    }

    //显示警示框(有取消按钮，有默认标题，可设置确认文字)
    public void showAlertDialog(String msg, String positive_txt, View.OnClickListener positive_listener, View.OnClickListener negative_listener) {
        showAlertDialog(getString(R.string.modal_dialog_tip), msg, positive_txt, positive_listener, negative_listener);
    }

    //显示警示框(有取消按钮，自定义标题，可设置确认文字)
    public void showAlertDialog(String title, String msg, String positive_txt, View.OnClickListener positive_listener, View.OnClickListener negative_listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title , msg)
                .setPositiveButton(positive_txt, positive_listener)
                .setNegativeButton(negative_listener);
        textDialog.show();
    }

    //显示警示框(没有取消按钮，没有事件监听)
    public void showAlertDialog(String title, String msg, View.OnClickListener listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(listener);
        textDialog.show();
    }

    //显示警示框(有默认标题，有取消按钮，有事件监听, 设置空白区域取消)
    public void showAlertDialog(String msg, String positive_txt, View.OnClickListener positive_listener,
                                String negative_txt, View.OnClickListener negative_listener, boolean cancel) {
        textDialog = TextDialogUtil.createDialog(context)
                .setMessage(getString(R.string.modal_dialog_tip), msg)
                .setPositiveButton(positive_txt, positive_listener)
                .setNegativeButton(negative_txt, negative_listener);
        textDialog.setCancelable(cancel);
        textDialog.show();
    }

    //显示警示框(没有取消按钮，没有事件监听, 设置空白区域取消)
    public void showAlertDialog(String title, String msg, View.OnClickListener listener, boolean cancel) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(listener);
        textDialog.setCancelable(cancel);
        textDialog.show();
    }

    //显示警示框(没有取消按钮)
    public void showAlertDialog(String title, String msg, String positive_txt, View.OnClickListener listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(positive_txt, listener);
        textDialog.show();
    }

    //显示警示框（没有按钮颜色设置）
    public void showAlertDialog(String title, String msg, String positive_txt, View.OnClickListener positive_listener,
                                String negative_txt, View.OnClickListener negative_listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(positive_txt, positive_listener)
                .setNegativeButton(negative_txt, negative_listener);
        textDialog.show();
    }

    //显示警示框（可设置按钮颜色，使用默认监听）
    public void showAlertDialog(String title, String msg, @ColorInt int positiveColor, @ColorInt int negativeColor, View.OnClickListener positive_listener) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(positiveColor, null, positive_listener)
                .setNegativeButton(negativeColor, null, null);
        textDialog.show();
    }

    //显示警示框
    public void showAlertDialog(String title, String msg,
                                String positive_txt, int positive_color, View.OnClickListener positive_listener,
                                String negative_txt, int negative_color, View.OnClickListener negative_listener, boolean cancel) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(positive_color, positive_txt, positive_listener)
                .setNegativeButton(negative_color, negative_txt, negative_listener);
        textDialog.setCancelable(cancel);
        textDialog.show();
    }

    public void dismiss() {
        if (textDialog != null && textDialog.isShowing()) {
            textDialog.dismiss();
        }
    }

    public Dialog modal(View layout) {
        return modal(null, layout);
    }

    /**
     * 模态框--默认位置在底部，需手动调 show方法
     *
     * @param gravity 显示位置，如 Gravity.BOTTOM
     * @param layout  视图，可参考layout_modal.xml编写,使用 View.inflate(context, R.layout.layout_modal, null) 获取
     */
    public Dialog modal(Integer gravity, View layout) {
        if (gravity == null) {
            gravity = Gravity.BOTTOM;
        }
        modalDialog = new Dialog(context);
        Window dialogWindow = modalDialog.getWindow();
        dialogWindow.setGravity(gravity);
        modalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        View cancel = layout.findViewById(R.id.cancel_tv);
        if (cancel != null) {
            cancel.setOnClickListener(v -> modalDialog.dismiss());//取消
        }
        modalDialog.setContentView(layout);
        return modalDialog;
    }

    /**
     * 显示底部模态框
     * @param modalBeanList 列表数据 ModalBean
     * @return 返回列表
     */
    public ListView showModal(List<ModalBean> modalBeanList){
        return showModal(modalBeanList, null);
    }

    public ListView showModal(List<ModalBean> modalBeanList,Integer gravity){
        View modalView = View.inflate(context, R.layout.layout_modal, null);
        ListView modalLv = modalView.findViewById(R.id.modal_content);
        modalLv.setAdapter(new ModalAdapter(context, modalBeanList));
        modal(gravity , modalView).show();
        return modalLv;
    }

    public void dismissModal() {
        if (modalDialog != null) {
            modalDialog.dismiss();
        }
    }


    public void handleMessage(Message msg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
