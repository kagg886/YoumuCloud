package com.kagg886.youmucloud.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import com.kagg886.youmucloud.R;

/**
 * @projectName: YoumuCloud
 * @package: com.kagg886.youmucloud.util
 * @className: DyamicNotification
 * @author: kagg886
 * @description: 快捷修改内容的Notification
 * @date: 2022/12/31 19:26
 * @version: 1.0
 */
public class DynamicNotification {
    private Context ctx;
    private static Bitmap icon;
    private static String CHANNEL_ID = "YoumuCloud_KeepAlive";

    private Platform platform;

    public Platform getPlatform() {
        return platform;
    }

    public DynamicNotification(Context ctx, Platform plat) {
        this.platform = plat;
        this.ctx = ctx;
        icon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher);
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(CHANNEL_ID, platform.name(), NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager service = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            service.createNotificationChannel(chan);
        }
    }

    public Notification build(String str) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID);
        builder.setContentTitle("YoumuCloud保活_" + platform.name())
                .setContentText(str)
                .setWhen(System.currentTimeMillis())//通知显示时间
                .setSmallIcon(R.drawable.ic_launcher).setOngoing(true).setPriority(NotificationCompat.PRIORITY_MAX).setLargeIcon(icon);
        return builder.build();
    }

    public void show(String str) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(platform.getId(),build(str));
    }

    public enum Platform {
        QRSpeed(1000),Secluded(1001),SQVZ(1002);
        private int id;
        Platform(int i) {
            this.id = i;
        }

        public int getId() {
            return id;
        }
    }
}
