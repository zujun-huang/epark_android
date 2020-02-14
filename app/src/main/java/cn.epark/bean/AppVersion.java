package cn.epark.bean;

/**
 * created huangzujun on 2020/2/14
 * Describe: 应用版本信息
 */
public class AppVersion {

    private int id;
    private String version; //版本号
    private String author; //更新人
    private String appDate; //更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate;
    }
}
