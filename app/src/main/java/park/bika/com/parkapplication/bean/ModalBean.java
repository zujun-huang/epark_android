package park.bika.com.parkapplication.bean;

import android.support.annotation.ColorRes;

import park.bika.com.parkapplication.R;

/**
 * created huangzujun on 2019/9/7
 * Company:重庆帮考教育科技有限公司
 * Describe: 模态框item
 */
public class ModalBean {

    private String contentTxt;
    private @ColorRes int color;

    public String getContentTxt() {
        return contentTxt;
    }

    public void setContentTxt(String contentTxt) {
        this.contentTxt = contentTxt;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ModalBean(String contentTxt) {
        this.contentTxt = contentTxt;
        this.color = R.color.g333333;
    }

    /**
     * ModalBean 模态实体
     * @param contentTxt 内容文本
     * @param color 颜色资源
     */
    public ModalBean(String contentTxt, @ColorRes int color) {
        this.contentTxt = contentTxt;
        this.color = color;
    }

    public ModalBean() {
    }

    @Override
    public String toString() {
        return "ModalBean{" +
                "contentTxt='" + contentTxt + '\'' +
                ", color=" + color +
                '}';
    }
}
