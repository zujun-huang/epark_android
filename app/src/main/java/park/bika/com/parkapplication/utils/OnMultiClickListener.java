package park.bika.com.parkapplication.utils;

import android.view.View;

/**
 * created huangzujun on 2019/8/8
 * Company:重庆帮考教育科技有限公司
 * Describe: 防止重复点击
 *       ==>如：跳转时，多次点击导致
 */
public abstract class OnMultiClickListener implements View.OnClickListener {
    /**
     * 最小的单击时间
     */
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    /**
     * 最后单击时间
     */
    private static long lastClickTime;

    /**
     * 防止重复单击
     * <p>使用方式如：view.setOnClickListener(new OnMultiClickListener() {});</p>
     *
     * @param v
     */
    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }
    }
}
