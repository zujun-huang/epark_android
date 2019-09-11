package park.bika.com.parkapplication.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import park.bika.com.parkapplication.R;
import park.bika.com.parkapplication.utils.TextDialogUtil;

/**
 * created huangzujun on 2019/8/31
 * Describe: fragment 基类
 */
public class BaseFragment extends Fragment {

    protected Context context;
    private Dialog modalDialog;
    private TextDialogUtil textDialog;
    protected String TAG;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment.this.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        TAG = getClass().getSimpleName();
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
        textDialog = TextDialogUtil.createDialog(context).setMessage(getString(R.string.modal_dialog_tip), msg)
                .setPositiveButton(positive_txt, positive_listener)
                .setNegativeButton(negative_listener);
        textDialog.show();
    }

    //显示警示框(没有取消按钮，没有事件监听)
    public void showAlertDialog(String title, String msg, View.OnClickListener listener) {
        textDialog=TextDialogUtil.createDialog(context).setMessage(title, msg)
                .setPositiveButton(listener);
        textDialog.show();
    }

    //显示警示框(没有取消按钮，没有事件监听, 设置空白区域取消)
    public void showAlertDialog(String title, String msg, View.OnClickListener listener, boolean cancel) {
        textDialog=TextDialogUtil.createDialog(context).setMessage(title, msg)
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
    public void showAlertDialog(String title, String msg, @ColorInt int positiveColor,@ColorInt int negativeColor, View.OnClickListener positive_listener) {
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

    public void dismiss(){
        if (textDialog != null){
            textDialog.dismiss();
        }
    }

    public Dialog modal(View layout){
        return modal(null, layout);
    }

    /**
     * 模态框--默认位置在底部，需手动调 show方法
     * @param gravity 显示位置，如 Gravity.BOTTOM
     * @param layout 视图，可参考layout_modal.xml编写,使用 View.inflate(context, R.layout.layout_modal, null) 获取
     */
    public Dialog modal(Integer gravity, View layout){
        if (gravity == null) gravity = Gravity.BOTTOM;
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
        if (cancel != null){
            cancel.setOnClickListener(v -> modalDialog.dismiss());//取消
        }
        modalDialog.setContentView(layout);
        return modalDialog;
    }

    public void dismissModal(){
        if (modalDialog != null){
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