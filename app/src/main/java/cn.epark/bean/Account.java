package cn.epark.bean;

import android.text.TextUtils;

import cn.epark.utils.StringUtil;

/**
 * created huangzujun on 2020/1/9
 * Company:重庆帮考教育科技有限公司
 * Describe: 用户
 */
public class Account {

    private String id = "";
    private String pwd;//密码
    private String name;//真实姓名
    private String nickName;//昵称
    private String head;//头像地址
    private String telphone;//手机号码
    private Double balance;//余额
    private Integer credit;//信用分
    private Integer gender = 1;//性别 1男 2女
    private Integer type;//用户类型
    private String encryptionSession;//用户session
    private boolean pwdIsNull;//是否为新用户

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        if (TextUtils.isEmpty(nickName)) {
            nickName = getName();
        }
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getGender() {
        return gender;
    }

    public String getSex() {
        return gender == null ? "" : gender == 1 ? "男" : "女";
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getTelphone() {
        if (telphone == null) {
            telphone = "";
        }
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getBalance() {
        if (balance == null) {
            balance = 0D;
        }
        return StringUtil.formatAmount(balance);
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getHead() {
        if (head != null && !head.contains("http")) {
            head = "http://" + head;
        }
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Integer getCredit() {
        if (credit == null) {
            credit = 100;//默认100信用分
        }
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getType() {
        if (type == null) {
            type = 0;
        }
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEncryptionSession() {
        if (encryptionSession == null) {
            encryptionSession = "";
        }
        return encryptionSession;
    }

    public void setEncryptionSession(String encryptionSession) {
        this.encryptionSession = encryptionSession;
    }

    public void setPwdIsNull(boolean pwdIsNull) {
        this.pwdIsNull = pwdIsNull;
    }

    public boolean getPwdIsNull() {
        return pwdIsNull;
    }

    public boolean isEmptyId() {
        return TextUtils.isEmpty(id);
    }

    public boolean isEmptyNickName() {
        return TextUtils.isEmpty(getNickName());
    }
}
