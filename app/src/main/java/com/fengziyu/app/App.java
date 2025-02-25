package com.fengziyu.app;

import android.app.Application;
import android.content.Intent;
import androidx.room.Room;
import com.fengziyu.app.db.MessageDatabase;
import com.fengziyu.app.mqtt.MQTTService;

public class App extends Application {
    private static MessageDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化数据库
        database = Room.databaseBuilder(this, MessageDatabase.class, "message_db")
                .build();

        // 不在这里启动服务
        // startService(new Intent(this, MQTTService.class));
    }

    public static MessageDatabase getDatabase() {
        return database;
    }
} 