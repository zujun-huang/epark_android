package cn.epark.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * created huangzujun on 2019/9/5
 * Company:重庆帮考教育科技有限公司
 * Describe: 字符串处理工具类
 */
public class StringUtil {

    public static final Pattern PATTERN_PHONE = Pattern.compile("^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$");

    /**
     * 某程序是否安装
     * @param context 上下文
     * @param packageName 程序完整包名
     * @return 已安装 true , 未安装 false
     */
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前时间戳
     * @return 当前日期 yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取指定格式的当前时间
     * @param format 格式
     * @return 格式化后的字符型日期
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 将yyyy-MM-dd格式的时间转化为yyy年MM月dd日格式
     * @param time 日期
     * @return 字符型日期
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormatTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = "";
        try {
            Date date = sdf.parse(time);
            sdf = new SimpleDateFormat("yyyy年MM月dd日");
            formatDate = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * 获取日期与当前日期的秒数差
     *
     * @param time 日期 格式为 yyyy-MM-dd HH:mm:ss
     * @return 两时间的秒数差
     */
    @SuppressLint("SimpleDateFormat")
    public static int getSeconds(String time) {
        if (time.trim().length() < 1) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int formatDate = -1;
        try {
            Date date = sdf.parse(time);
            Date curDate = new Date();
            formatDate = (int) ((curDate.getTime() - date.getTime()) / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * 保留2位小数
     * @param v Double数据
     * @return 两位小数字符型
     */
    public static String formatAmount(Double v) {
        return new DecimalFormat("0.00").format(v);
    }

    /**
     * 保留1位小数
     * @param v Double数据
     * @return 1位小数字符型
     */
    public static String formatDiscount(Double v) {
        return new DecimalFormat("0.0").format(v);
    }

    /**
     * 不保留小数
     * @param v Double数据
     * @return 无小数字符型
     */
    public static String formatNoDecimals(Double v) {
        return new DecimalFormat("0").format(v);
    }

    /**
     * 如果为整，不保留小数，如果不为整，保留一位小数
     * @param d Double数据
     * @return 字符型
     */
    public static String doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return formatNoDecimals(d);
        }
        return formatDiscount(d);
    }

    /**
     * 格式化秒数，返回 HH:mmm:ss格式
     * @param seconds 秒
     * @return 字符型
     */
    @SuppressLint("DefaultLocale")
    public static String formatTime(int seconds) {
        return String.format("%02d:%02d:%02d",
                seconds / 60 / 60 % 60,
                seconds / 60 % 60,
                seconds % 60);
    }

    /**
     * 验证手机号码
     * @param mobileNumber 手机号码
     * @return true:是，false:否
     */
    public static boolean isPhoneNumber(String mobileNumber){
        return PATTERN_PHONE.matcher(mobileNumber).matches();
    }

    public static String hidePhoneNumber(String mobileNumber) {
        return hidePhoneNumber(mobileNumber, 3);
    }

    /**
     * 隐藏手机号码，默认显示前个三数字及后三个数字
     * @param mobileNumber 手机号码
     * @param afterNum 后几位数字
     * @return 前三后n的手机号
     */
    public static String hidePhoneNumber(String mobileNumber,Integer afterNum) {
        if (TextUtils.isEmpty(mobileNumber)){
            return "";
        }
        Integer n = 3;
        if (afterNum != null && afterNum > 0){
            n = afterNum;
        }
        return mobileNumber.substring(0, 3) + "****" +
                mobileNumber.substring(mobileNumber.length() - n);
    }

}
