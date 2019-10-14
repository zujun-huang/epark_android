package park.bika.com.parkapplication.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Toast;

import park.bika.com.parkapplication.R;

/**
 * @author hzj
 * @date 2019-07-09
 */
public class ToastUtil {

    private static Toast toast = null;

    /**
     * 在子线程中显示Toast
     *
     * @param context 上下文
     * @param msg     显示的消息
     */
    public static void showToastInUIThread(final Context context, final String msg) {
        ThreadUtil.runInUIThread(() -> showToast(context, msg));
    }

    /**
     * UI线程显示Toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0 , 0);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showLongToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0 , 0);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
