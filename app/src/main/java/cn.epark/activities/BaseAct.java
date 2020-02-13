package cn.epark.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.epark.App;
import cn.epark.Constant;
import cn.epark.ErrorCode;
import cn.epark.R;
import cn.epark.adapters.ModalAdapter;
import cn.epark.bean.ModalBean;
import cn.epark.utils.LogUtil;
import cn.epark.utils.NetWorkReceiver;
import cn.epark.utils.OkHttpUtil;
import cn.epark.utils.OkHttpUtil.Method;
import cn.epark.utils.StatusBarUtil;
import cn.epark.utils.TextDialogUtil;
import cn.epark.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @作者 huangzujun
 * @日期 2019-07-26
 * @描述 基类
 */
public class BaseAct extends AppCompatActivity implements NetWorkReceiver.OnNetWorkListener  {

    public final int SHOW_TOAST = 0x00000001;
    public final int WX_ONRESP = 0x00000002;
    public Context context;
    public NetWorkReceiver netWorkReceiver;
    public String TAG;
    public IWXAPI wxApi;
    public BaseResp wxResp;
    public Handler wxHandler;
    private Dialog modalDialog, loadingDialog;
    private TextDialogUtil textDialog;

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
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
                case WX_ONRESP:
                    boolean authorize = (boolean) msg.obj;
                    if (authorize) {//授权成功
                        onWxResponseOk(((SendAuth.Resp) wxResp).code);
                    } else {
                        onWxResponseCancel();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    BaseAct.this.handleMessage(msg);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //vector图形兼容支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){ //Android 5.0
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
        super.onCreate(savedInstanceState);
        context = this;
        TAG = "ePark";
        setStatusBar();
        initWx();
    }

    private void initWx() {
        wxApi = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, true);
        wxApi.registerApp(Constant.WX_APP_ID);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initBackIcon();
    }

    private void initBackIcon() {
        View backView = findViewById(R.id.icon_back);
        if (backView != null){
            backView.setOnClickListener(v -> finish());
        }
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
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                onResponseOk(response.body().string(), actionCode);
            } else {
                onResponseError(call, actionCode);
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
        OkHttpUtil.getInstance(context).request(Method.GET, url, params, new BaseCallback(actionCode, autoDismiss));
    }

    public void httpPost(String url, HashMap<String, String> params, int actionCode){
        httpPost(url, params, actionCode, true, true);
    }

    public void httpPost(String url, HashMap<String, String> params, int actionCode, boolean showDialog, boolean autoDismiss ){
        if (showDialog){
            showLoadingDialog();
        }
        OkHttpUtil.getInstance(context).request(Method.POST, url, params, new BaseCallback(actionCode, autoDismiss));
    }

    public void onResponseOk(String response, int actionCode) {
        LogUtil.i("okHttpUtil", "response:" + response);
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
            ToastUtil.showToast(context, "抱歉，当前服务已遗失~");
        }
    }

    public void onResponseOk(JSONObject data, int actionCode){

    }

    //业务返回错误
    public void onResponseFail(Call call, Response response) throws IOException {
        LogUtil.e("okHttp", " response code:" + response.code() + " body:" +  response.body().string());
    }

    public void onResponseFail(int errorCode, String errorMsg, int actionCode) {
        LogUtil.e("okHttp", " errorCode:" + errorCode + ", errorMsg:" +  errorMsg);
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

    /**
     * 微信授权成功回调
     * @param code {@link SendAuth.Resp#code}
     */
    public void onWxResponseOk(String code) {

    }

    public void onWxResponseCancel() {
        ToastUtil.showToast(context, "取消授权");
    }

    public void dismissLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
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

    public void showNetWorkToast(){
        ToastUtil.showToast(context, getString(R.string.network_error));
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

    //显示警示框(没有取消按钮，没有事件监听, 设置空白区域取消)
    public void showAlertDialog(String title, String msg, View.OnClickListener listener, boolean cancel) {
        textDialog = TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(listener);
        textDialog.setCancelable(cancel);
        textDialog.show();
    }

    //显示警示框(没有取消按钮)
    public void showAlertDialog(String title, String msg, String positive_txt, View.OnClickListener listener) {
        TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(positive_txt, listener)
                .show();
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
        if (textDialog != null) {
            textDialog.dismiss();
        }
    }

    /**
     * 模态框--默认位置在底部，需手动调 show方法
     * @param gravity 显示位置，如 Gravity.BOTTOM
     * @param layout 视图，可参考layout_modal.xml编写
     *
     */
    public Dialog modal(Integer gravity, View layout) {
        if (gravity == null) {
            gravity = Gravity.BOTTOM;
        }
        modalDialog = new Dialog(context);
        Window dialogWindow = modalDialog.getWindow();
        if (dialogWindow != null){
            dialogWindow.setGravity(gravity);
            modalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);
        }
        View cancelV = layout.findViewById(R.id.cancel_tv);
        if (cancelV != null) {
            cancelV.setOnClickListener(v -> modalDialog.dismiss());//取消
        }
        modalDialog.setContentView(layout);
        return modalDialog;
    }

    /**
     * 显示底部模态框
     * @param modalBeanList 列表数据 ModalBean
     * @param cancel 能否点击外部取消
     * @return 返回列表
     */
    public ListView showModal(List<ModalBean> modalBeanList, boolean cancel){
        return showModal(modalBeanList, null, cancel);
    }

    public ListView showModal(List<ModalBean> modalBeanList){
        return showModal(modalBeanList, null, true);
    }

    public ListView showModal(List<ModalBean> modalBeanList,Integer gravity, boolean cancel){
        View modalView = View.inflate(context, R.layout.layout_modal, null);
        ListView modalLv = modalView.findViewById(R.id.modal_content);
        modalLv.setAdapter(new ModalAdapter(context, modalBeanList));
        modal(gravity , modalView).show();
        modalDialog.setCancelable(cancel);
        modalDialog.setCanceledOnTouchOutside(cancel);
        return modalLv;
    }

    public void dismissModal(){
        if (modalDialog != null){
            modalDialog.dismiss();
        }
    }

    public void handleMessage(Message msg){

    }

    protected void setStatusBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);//false预留出状态栏高度
        StatusBarUtil.setTranslucentStatus(this); //状态栏透明
          //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this, 0x55000000);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onNetworkChange(int networkType) {
        if (networkType == -1) {
            App.hasNetwork = false;
        } else {
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                App.isWiFi = true;
            } else {
                App.isWiFi = false;
            }
            App.hasNetwork = true;
        }
    }

}
