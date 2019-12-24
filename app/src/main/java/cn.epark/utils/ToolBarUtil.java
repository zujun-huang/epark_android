package cn.epark.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.epark.MainBar;
import cn.epark.R;

/**
 * @作者 hzj
 * @日期 2019-07-09
 * @描述 底部菜单栏工具类
 * -->用于动态创建菜单栏、选中效果切换及点击事件回调
 */

public class ToolBarUtil {

    private onToolBarClickListener onToolBarClickListener;
    private TextView preTv;

    /**
     * 建立底部工具栏
     *
     * @param container     容器
     * @param toolBarTitles
     * @param iconArr       顶部图片资源
     */
    public void createToolBar(final LinearLayout container, String[] toolBarTitles, int[] iconArr) {
        for (int i = 0; i < toolBarTitles.length; i++) {
            TextView tv = (TextView) View.inflate(container.getContext(),
                    R.layout.layout_toolbar_text, null);
            tv.setText(toolBarTitles[i]); //设置底部文字
            //动态设置drawableTop属性
            tv.setCompoundDrawablesWithIntrinsicBounds(0, iconArr[i], 0, 0);
            //设置文字平分
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            int width = 0;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.weight = 1;
            container.addView(tv, params);
            int iconPosition;
            switch (iconArr[i]){
                case R.drawable.selector_toolbar_nearby:
                    iconPosition = MainBar.NEARBY_PAGE;
                    break;
                case R.drawable.selector_toolbar_share:
                    iconPosition = MainBar.SHARE_PAGE;
                    break;
                case R.drawable.selector_toolbar_myself:
                    iconPosition = MainBar.MYSELF_PAGE;
                    break;
                default:
                    iconPosition = MainBar.HOME_PAGE;
                    break;
            }
            if (i == 0){
                preTv = tv;
                tv.setSelected(true);//默认选中
            }
            tv.setOnClickListener(view -> {
                if (onToolBarClickListener != null){
                    onToolBarClickListener.onToolBarClick(iconPosition);
                }
                if (preTv != null){
                    preTv.setSelected(false);
                    preTv = tv;
                    preTv.setSelected(true);
                }
            });
        }

    }

    public interface onToolBarClickListener {
        void onToolBarClick(int position);
    }

    public void setOnToolBarClickListener(ToolBarUtil.onToolBarClickListener onToolBarClickListener) {
        this.onToolBarClickListener = onToolBarClickListener;
    }

}
