package cn.epark.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created huangzujun on 2019/10/13
 * Describe: 共享车位信息
 */
public class ShareParkInfo implements Parcelable{

    private String id;
    private String userId;
    private String number;//车位号
    private String phone;
    private String floor;//车位楼层
    private String fromTime;//共享开始时间
    private String toTime;//共享结束时间
    private String noTime;//限享日期，连续用-分割，单独用,分割
    private String lonLat;//经纬度 lon 经度
    private String address;
    private String parkInfoId;//车位材料
    private String parkImg;//车位图片
    private String createTime;
    private int state = UNAUDITED;//审核状态 UNAUDITED、AUDITED_FAILURE、AUDITED_SUCCESS、SHARE_SUCCESS
    private String msg;//审核失败信息
    private String auditTime;//审核时间
    private String carId;//租用车辆ID
    private Double applyPrice = 0.0;//车位价格
    private Double price = 0.0;//车位利润

    /**
     * 未审核
     */
    public static int UNAUDITED = 0;

    /**
     * 未审核-本地历史填写缓存
     */
    public static int UNAUDITED_LOCATION = -2;

    /**
     * 审核失败
     */
    public static int AUDITED_FAILURE = -1;

    /**
     * 审核通过
     */
    public static int AUDITED_SUCCESS = 1;

    /**
     * 已共享成功（表示车位正在使用中）
     */
    public static int SHARE_SUCCESS = 2;

    public ShareParkInfo() {
    }

    protected ShareParkInfo(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.number = in.readString();
        this.phone = in.readString();
        this.floor = in.readString();
        this.fromTime = in.readString();
        this.toTime = in.readString();
        this.noTime = in.readString();
        this.lonLat = in.readString();
        this.address = in.readString();
        this.parkInfoId = in.readString();
        this.parkImg = in.readString();
        this.createTime = in.readString();
        this.state = in.readInt();
        this.msg = in.readString();
        this.auditTime = in.readString();
        this.carId = in.readString();
        this.price = in.readDouble();
        this.applyPrice = in.readDouble();
    }

    public ShareParkInfo(String number, String floor, String fromTime, String toTime, String address, String parkImg, String createTime, int state, String msg, String auditTime, Double price) {
        this.number = number;
        this.floor = floor;
        this.fromTime = fromTime.replace("-" , ".");
        this.toTime = toTime.replace("-" , ".");
        this.address = address;
        this.parkImg = parkImg;
        this.createTime = createTime;
        this.state = state;
        this.msg = msg;
        this.auditTime = auditTime;
        this.price = price;
    }

    public static final Creator<ShareParkInfo> CREATOR = new Creator<ShareParkInfo>() {
        @Override
        public ShareParkInfo createFromParcel(Parcel in) {
            return new ShareParkInfo(in);
        }

        @Override
        public ShareParkInfo[] newArray(int size) {
            return new ShareParkInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.number);
        dest.writeString(this.phone);
        dest.writeString(this.floor);
        dest.writeString(this.fromTime);
        dest.writeString(this.toTime);
        dest.writeString(this.noTime);
        dest.writeString(this.lonLat);
        dest.writeString(this.address);
        dest.writeString(this.parkInfoId);
        dest.writeString(this.parkImg);
        dest.writeString(this.createTime);
        dest.writeInt(this.state);
        dest.writeString(this.msg);
        dest.writeString(this.auditTime);
        dest.writeString(this.carId);
        dest.writeDouble(this.price);
        dest.writeDouble(this.applyPrice);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime.replace("-" , ".");
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getNoTime() {
        return noTime;
    }

    public void setNoTime(String noTime) {
        this.noTime = noTime.replace("-" , ".");
    }

    public String getLonLat() {
        return lonLat;
    }

    public void setLonLat(String lonLat) {
        this.lonLat = lonLat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkInfoId() {
        return parkInfoId;
    }

    public void setParkInfoId(String parkInfoId) {
        this.parkInfoId = parkInfoId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getParkImg() {
        return parkImg;
    }

    public void setParkImg(String parkImg) {
        this.parkImg = parkImg;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getApplyPrice() {
        return applyPrice;
    }

    public void setApplyPrice(Double applyPrice) {
        this.applyPrice = applyPrice;
    }
}
