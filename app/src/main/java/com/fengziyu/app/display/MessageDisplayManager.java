package com.fengziyu.app.display;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.fengziyu.app.MessageDetailActivity;
import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;

public class MessageDisplayManager {
    private static final String CHANNEL_ID = "message_channel";
    private static final int NOTIFICATION_ID = 1000;
    private Context context;
    private NotificationManager notificationManager;
    private CarouselManager carouselManager;

    public MessageDisplayManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.carouselManager = CarouselManager.getInstance();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "消息通知",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("接收车载系统的重要消息通知");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void displayMessage(Message message) {
        switch (message.getDisplayType()) {
            case Message.DISPLAY_NOTIFICATION:
                showNotification(message);
                break;
            case Message.DISPLAY_CAROUSEL:
                showInCarousel(message);
                break;
            case Message.DISPLAY_POPUP:
                // TODO: 实现弹窗显示
                break;
        }
    }

    private void showNotification(Message message) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("message_id", message.getId());
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(message.getTitle())
            .setContentText(message.getContent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify((int) message.getId(), builder.build());
    }

    private void showInCarousel(Message message) {
        carouselManager.addMessage(message);
    }

    public void removeFromCarousel(Message message) {
        carouselManager.removeMessage(message);
    }

    public void clearNotifications() {
        notificationManager.cancelAll();
    }
} 