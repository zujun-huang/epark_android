package park.bika.com.parkapplication.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import park.bika.com.parkapplication.utils.ToastUtil;

/**
 * @作者 huangzujun
 * @日期 2019-07-31
 * @描述 地图容器，解决地图与 NestedScrollView/ScrollView 的滑动冲突
 * <p>回调滑动监听</p>
 */
public class MapFrameLayout extends FrameLayout {

    private ScrollView scrollView;
    private NestedScrollView nestedScrollView;

    public MapFrameLayout(Context context) {
        super(context);
    }

    public MapFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置 ScrollView
     *
     * @param scrollView
     */
    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    /**
     * 设置 NestedScrollView
     *
     * @param nestedScrollView
     */
    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        this.nestedScrollView = nestedScrollView;
    }

    /**
     * 点击地图时，让滑动视图不拦截事件，把事件传递到子View
     *
     * @param ev
     * @return false
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (scrollView != null) {
                scrollView.requestDisallowInterceptTouchEvent(false);
            } else if (nestedScrollView != null) {
                nestedScrollView.requestDisallowInterceptTouchEvent(false);
            }
        } else {
            if (scrollView != null) {
                scrollView.requestDisallowInterceptTouchEvent(true);
            } else if (nestedScrollView != null) {
                nestedScrollView.requestDisallowInterceptTouchEvent(true);
            }
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;    //接收并消费掉该事件
    }
}
