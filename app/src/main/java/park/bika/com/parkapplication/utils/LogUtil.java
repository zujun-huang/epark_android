package park.bika.com.parkapplication.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import park.bika.com.parkapplication.BuildConfig;

/**
 *
 * 日志工具类
 *
 * */
public class LogUtil {
    public static boolean isDebug = BuildConfig.DEBUG;
    private static final String NULL_STR = "msg is null";
    private static String LOGFILENAME = ".txt"; // 本类输出的日志文件名称
    private static String LOG_PATH_SDCARD_DIR = "/sdcard/EPLog/"; // 日志文件在sdcard中的路径
    @SuppressLint({"SdCardPath", "SimpleDateFormat"})
    private static SimpleDateFormat LogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyyMMdd"); // 日志文件格式


    public static void v(String tag, String msg) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (isDebug) {
            msg = TextUtils.isEmpty(msg) ? NULL_STR : msg;
            android.util.Log.e(tag, msg, t);
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private static void writeLogtoFile(String mylogtype, String tag, String text) {
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
        String needWriteMessage = LogSdf.format(nowtime) + " " + mylogtype
                + " " + tag + " " + text;

        File logdir = new File(LOG_PATH_SDCARD_DIR);// 如果没有log文件夹则新建该文件夹
        if (!logdir.exists()) {
            logdir.mkdirs();
        }
        File file = new File(LOG_PATH_SDCARD_DIR, needWriteFiel + LOGFILENAME);
        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
