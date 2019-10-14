package park.bika.com.parkapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
/**
 * Created by huangzujun on 2019/8/30.
 * Describe: 滑动视图内的子listView
 */
public class ChildListView extends ListView {
    public ChildListView(Context context) {
        super(context);
    }

    public ChildListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 为了让ChildListView的adapter中的控件可以触发点击事件
     */
    /*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }*/


    /**
     * 为了让外层的AutoListView可以下拉刷新
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

}
