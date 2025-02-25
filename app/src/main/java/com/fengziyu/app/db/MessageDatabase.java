package com.fengziyu.app.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fengziyu.app.model.Message;

@Database(entities = {Message.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase {
    private static volatile MessageDatabase INSTANCE;
    public abstract MessageDao messageDao();
    
    // 用于数据库操作的线程池
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static MessageDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MessageDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MessageDatabase.class, "message_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
} 