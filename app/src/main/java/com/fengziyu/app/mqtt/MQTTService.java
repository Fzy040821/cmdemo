package com.fengziyu.app.mqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.repository.MessageRepository;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;

public class MQTTService extends Service {
    private static final String TAG = "MQTTService";
    private static final String BROKER = "tcp://your-mqtt-broker:1883";
    private static final String CLIENT_ID = "AndroidClient-" + System.currentTimeMillis();
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "mqtt_service_channel";
    private MqttClient mqttClient;
    private MessageRepository messageRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        messageRepository = new MessageRepository(getApplication());
        initMQTT();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "MQTT Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("MQTT Service Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("消息服务")
            .setContentText("正在运行中...")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void initMQTT() {
        try {
            mqttClient = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "Connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        String payload = new String(message.getPayload());
                        JSONObject json = new JSONObject(payload);
                        
                        Message msg = new Message();
                        msg.setTitle(json.getString("title"));
                        msg.setContent(json.getString("content"));
                        msg.setType(json.getString("type"));
                        msg.setTimestamp(System.currentTimeMillis());
                        msg.setRead(false);
                        
                        messageRepository.insert(msg);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing message", e);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Message delivered");
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe("svw/notification/#");
            
        } catch (MqttException e) {
            Log.e(TAG, "MQTT initialization failed", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                Log.e(TAG, "Error disconnecting", e);
            }
        }
    }
} 