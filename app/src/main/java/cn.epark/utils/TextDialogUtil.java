package cn.epark.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cn.epark.R;

/**
 * 自定义文本对话框
 */
public class TextDialogUtil extends Dialog{

    private View view, v_line;
    private Context context;
    private Button cancelBtn, positiveBtn;
    private View.OnClickListener btnClickListener;

    //默认文本dialog
    public TextDialogUtil(@NonNull Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
        view = View.inflate(context, R.layout.com_textdialog_layout, null);
        init(view);
        TextDialogClickListener textDialogClickListener = new TextDialogClickListener();
        cancelBtn = view.findViewById(R.id.dialog_button_cancel);
        positiveBtn = view.findViewById(R.id.dialog_button_confirm);
        v_line = view.findViewById(R.id.vertical_line);
        cancelBtn.setOnClickListener(textDialogClickListener);
        positiveBtn.setOnClickListener(textDialogClickListener);
    }

    //自定义样式dialog
    public TextDialogUtil(@NonNull Context context, View layout){
        super(context, R.style.dialog_style);
        this.context = context;
        init(layout);
    }

    private void init(View layout) {
        setContentView(layout);
        Window window = getWindow();
        if (window == null){
            LogUtil.e("TextDialog","TextDialog--getWindow() is null");
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public static TextDialogUtil createDialog(Context context) {
        TextDialogUtil dialogUtil = new TextDialogUtil(context);
        dialogUtil.setCancelable(false);
        return dialogUtil;
    }

    public TextDialogUtil setMessage(CharSequence message) {
        this.setMessage(null, message);
        return this;
    }

    public TextDialogUtil setMessage(String title, CharSequence message) {
        TextView tvTitle, tvContent;
        tvTitle = view.findViewById(R.id.dialog_title);
        tvContent = view.findViewById(R.id.dialog_msg);
        if (TextUtils.isEmpty(title)){
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(message)){
            tvContent.setText(message);
        }
        return this;
    }

    public TextDialogUtil setPositiveButton(View.OnClickListener li) {
        this.setPositiveButton(context.getString(R.string.app_confirm), li);
        return this;
    }

    public TextDialogUtil setPositiveButton(String buttonText, View.OnClickListener li) {
        this.setPositiveButton(-1, buttonText, li);
        return this;
    }

    public TextDialogUtil setPositiveButton(@ColorInt int color, String positiveText, View.OnClickListener li) {
        if (positiveBtn == null) {
            return this;
        }
        if (color > 0){
            positiveBtn.setTextColor(color);
        }
        if (!TextUtils.isEmpty(positiveText)){
            positiveBtn.setText(positiveText);
            positiveBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.com_btn_confirm_selector));
            cancelBtn.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
        }
        if (li != null){
            btnClickListener = li;
        }
        return this;
    }
    
    public TextDialogUtil setNegativeButton(View.OnClickListener li) {
        this.setNegativeButton(context.getString(R.string.app_cancel), li);
        return this;
    }

    public TextDialogUtil setNegativeButton(String buttonText, View.OnClickListener li) {
        this.setNegativeButton(-1, buttonText, li);
        return this;
    }

    public TextDialogUtil setNegativeButton(@ColorInt int color, String cancelText, View.OnClickListener li) {
        if (cancelBtn == null) {
            return this;
        }
        if (color > 0){
            cancelBtn.setTextColor(color);
        }
        if (!TextUtils.isEmpty(cancelText)){
            cancelBtn.setVisibility(View.VISIBLE);
            v_line.setVisibility(View.VISIBLE);
            positiveBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.com_btn_confirm_selector_right));
            cancelBtn.setText(cancelText);
        }
        if (li != null){
            btnClickListener = li;
        }
        return this;
    }
    
    //默认点击事件处理
    class TextDialogClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            dismiss();
            if (btnClickListener != null) {
                btnClickListener.onClick(v);
            }
        }
    }
}
