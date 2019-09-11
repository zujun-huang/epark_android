package park.bika.com.parkapplication.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import park.bika.com.parkapplication.R;
/**
 * @作者 huangzujun
 * @日期 2019/8/5
 * @描述 GPS前台服务
 */
public class ForegroundService extends Service {

    private static final int RES_ID = R.layout.activity_navigation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = "my_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, "我的后台服务");
        }

        Notification notification = new Notification();
        try {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId);
            notification = builder.setSmallIcon(R.mipmap.app_icon).setTicker("正在导航")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("正在导航")
                    .setContentText("易派克-您的简便停车助手")
                    .build();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        startForeground(RES_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }
}
