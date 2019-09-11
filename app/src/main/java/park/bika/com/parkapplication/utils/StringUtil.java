package park.bika.com.parkapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * created huangzujun on 2019/9/5
 * Company:重庆帮考教育科技有限公司
 * Describe: 字符串处理工具类
 */
public class StringUtil {

    //测程序是否安装
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }

    //获取当前时间戳，格式为 yyyy-MM-dd HH:mm:ss
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    //获取指定格式的当前时间
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    //将yyyy-MM-dd格式的时间转化为yyy年MM月dd日格式
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

    //获取日期与当前日期的秒数差
    @SuppressLint("SimpleDateFormat")
    public static int getSeconds(String time) {
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

    // 保留2位小数
    public static String formatAmount(Double v) {
        return new DecimalFormat("0.00").format(v);
    }

    // 保留1位小数
    public static String formatDiscount(Double v) {
        return new DecimalFormat("0.0").format(v);
    }

    // 不保留小数
    public static String formatNoDecimals(Double v) {
        return new DecimalFormat("0").format(v);
    }

    //如果为整，不保留小数，如果不为整，保留一位小数
    public static String doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return formatNoDecimals(d);
        }
        return formatDiscount(d);
    }

    //格式化秒数，返回 HH:mmm:ss格式
    @SuppressLint("DefaultLocale")
    public static String formatTime(int seconds) {
        return String.format("%02d:%02d:%02d",
                seconds / 60 / 60 % 60,
                seconds / 60 % 60,
                seconds % 60);
    }
}
