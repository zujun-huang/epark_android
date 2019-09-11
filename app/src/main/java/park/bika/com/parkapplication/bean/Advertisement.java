package park.bika.com.parkapplication.bean;

import android.support.annotation.DrawableRes;

/**
 * created huangzujun on 2019/9/9
 * Company:重庆帮考教育科技有限公司
 * Describe: 首页广告数据
 */
public class Advertisement {

//    private String icon;
    @DrawableRes
    private int icon; //本地资源
    private String title;
    private String content;
//    private String img;
    @DrawableRes
    private int img;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Advertisement(int icon, String title, String content, int img) {
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Advertisement() {

    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
