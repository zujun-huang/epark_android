package cn.epark.utils;

import android.os.Handler;

/**
 * @author hzj
 * @date 2019-07-08
 */
public class ThreadUtil {
    /**
     * 子线程执行task
     */
    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * 主线程handler
     */
    private static Handler handler = new Handler();

    /**
     * UI线程执行task
     */
    public static void runInUIThread(Runnable task) {
        handler.post(task);
    }


}
