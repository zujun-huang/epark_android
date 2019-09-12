package park.bika.com.parkapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;

/**
 * @作者 huangzujun
 * @日期 2019-07-22
 * @描述 各尺寸间的转换及两经纬度间的计算
 */
public class CalcUtil {

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float px2sp(Context context, float pxValue) {
        return pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

    public static float sp2px(Context context, float spValue) {
        return spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

}
