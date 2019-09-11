package park.bika.com.parkapplication.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import park.bika.com.parkapplication.R;

/**
 * @作者 hzj
 * @日期 2019-07-09
 * @描述 底部菜单栏工具类
 * -->用于动态创建菜单栏、选中效果切换及点击事件回调
 */

public class ToolBarUtil {

    private List<TextView> mTextViews;
    private onToolBarClickListener onToolBarClickListener;

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
            if (mTextViews == null) {
                mTextViews = new ArrayList<>();
            }
            mTextViews.add(tv);
            final int finalI = i;
            tv.setOnClickListener(view -> onToolBarClickListener.onToolBarClick(finalI));
        }
    }

    /**
     * 改变底部选择状态
     *
     * @param position
     */
    public void changeChoosedState(int position) {
        //还原所有颜色
        for (TextView tv : mTextViews) {
            tv.setSelected(false);
        }
        //设置selected属性改变选中效果
        mTextViews.get(position).setSelected(true);
    }

    public interface onToolBarClickListener {
        void onToolBarClick(int position);
    }

    public void setOnToolBarClickListener(ToolBarUtil.onToolBarClickListener onToolBarClickListener) {
        this.onToolBarClickListener = onToolBarClickListener;
    }

}
