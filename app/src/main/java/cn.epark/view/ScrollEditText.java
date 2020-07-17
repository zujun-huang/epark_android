package cn.epark.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * create by huangzujun on 2020-07-17
 * describe：嵌套在ScrollView中的ET，解决EditText的滚动无效
 */
public class ScrollEditText extends android.support.v7.widget.AppCompatEditText {

    public ScrollEditText(Context context) {
        super(context);
    }

    public ScrollEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理滑动冲突
        if (event.getAction() != MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }
}
