package park.bika.com.parkapplication.bean;

/**
 * created huangzujun on 2019/9/7
 * Company:重庆帮考教育科技有限公司
 * Describe: 模态框item
 */
public class ModalBean {

    private String contentTxt;
    private int color;

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

    public ModalBean(String contentTxt, int color) {
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
