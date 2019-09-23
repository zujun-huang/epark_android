package park.bika.com.parkapplication.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.StatusBarUtil;
import park.bika.com.parkapplication.utils.TextDialogUtil;
import park.bika.com.parkapplication.utils.VolleyHttpUtil;

/**
 * @作者 huangzujun
 * @日期 2019-07-26
 * @描述 基类
 */
public class BaseAct extends AppCompatActivity {

    public Context context;
    public String TAG;
    private Dialog modalDialog, loadingDialog;
    private View modalView;
    private TextDialogUtil textDialog;
    private VolleyHttpUtil volleyHttp;
    private static List<Activity> mActivitys = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseAct.this.handleMessage(msg);
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
        TAG = getClass().getSimpleName();
        setStatusBar();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initBackIcon();
    }

    private void initBackIcon() {
        View backView = findViewById(R.id.icon_back);
        if (backView != null){
            backView.setOnClickListener(v -> closeActivity(this));
        }
    }

    public void httpGet(String url, HashMap<String, String> maps, Response.Listener<String> listener, Response.ErrorListener errorListener){
        volleyHttp.httpRequest(Request.Method.GET, url, maps, listener, errorListener);
    }

    public void httpPost(String url, HashMap<String, String> maps, Response.Listener<String> listener, Response.ErrorListener errorListener){
        volleyHttp.httpRequest(Request.Method.POST, url, maps, listener, errorListener);
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
        if (context == null) return;
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
     * @param layoutResource 视图资源，可参考layout_modal.xml编写
     */
    public Dialog modal(Integer gravity, int layoutResource){
        if (gravity == null) gravity = Gravity.BOTTOM;
        modalDialog = new Dialog(context);
        modalView = LayoutInflater.from(context).inflate(layoutResource, null);
        Window dialogWindow = modalDialog.getWindow();
        dialogWindow.setGravity(gravity);
        modalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWindow.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        View cancel = modalView.findViewById(R.id.cancel_tv);
        if (cancel != null){
            cancel.setOnClickListener(v -> modalDialog.dismiss());//取消
        }
        modalDialog.setContentView(modalView);
        return modalDialog;
    }

    public void dismissModal(){
        if (modalDialog != null){
            modalDialog.dismiss();
        }
    }

    public View getModalView() {
        return modalView;
    }

    public void handleMessage(Message msg){

    }

    protected void setStatusBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);//预留出状态栏高度
        StatusBarUtil.setTranslucentStatus(this); //状态栏透明

          //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this, 0x55000000);
//        }
    }

    public static void addActivity(Activity a) {
        mActivitys.add(a);
    }

    public static void closeActivity(Activity a) {
        for (Activity activity : mActivitys) {
            if (a.getClass().getName().equals(activity.getClass().getName())) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭指定类名的Activity
     * @param cls 类名
     */
    public static void closeActivity(Class cls) {
        for (Activity activity : mActivitys) {
            if (cls.getName().equals(activity.getClass().getName())) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭除了指定类名的Activity以外的所有Activity
     * @param cls 不关闭的Activity类名
     */
    public static void closeAllExcept(Class cls) {
        for (Activity a : mActivitys) {
            if (!a.getClass().getName().equals(cls.getName())) {
                a.finish();
            }
        }
    }

    public static void closeAllActivity() {
        for (Activity a : mActivitys) {
            if (a != null) {
                a.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}
