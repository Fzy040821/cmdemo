package com.fengziyu.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.fengziyu.app.db.MessageDao;
import com.fengziyu.app.db.MessageDatabase;
import com.fengziyu.app.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> allMessages;
    private LiveData<List<Message>> unreadMessages;

    // 优先级常量
    public static final int PRIORITY_EMERGENCY = 5;  // 紧急消息
    public static final int PRIORITY_HIGH = 4;       // 高优先级
    public static final int PRIORITY_NORMAL = 3;     // 普通优先级
    public static final int PRIORITY_LOW = 2;        // 低优先级
    public static final int PRIORITY_MINIMAL = 1;    // 最低优先级

    public MessageRepository(Application application) {
        MessageDatabase db = MessageDatabase.getInstance(application);
        messageDao = db.messageDao();
        allMessages = messageDao.getAllMessages();
        unreadMessages = messageDao.getUnreadMessages();
    }

    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    public LiveData<List<Message>> getUnreadMessages() {
        return unreadMessages;
    }

    public void insert(Message message) {
        setPriority(message);
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.insert(message);
        });
    }

    public void delete(Message message) {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.delete(message);
        });
    }

    public void markAsRead(long messageId) {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.markAsRead(messageId);
        });
    }

    public void markAllAsRead() {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.markAllAsRead();
        });
    }

    public void deleteMessages(List<Message> messages) {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            List<Long> messageIds = messages.stream()
                    .map(Message::getId)
                    .collect(Collectors.toList());
            messageDao.deleteMessages(messageIds);
        });
    }

    public void deleteAll() {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.deleteAll();
        });
    }

    public LiveData<Message> getMessageById(long id) {
        return messageDao.getMessageById(id);
    }

    public LiveData<Integer> getUnreadCountByType(String type) {
        return messageDao.getUnreadCountByType(type);
    }

    // 获取高优先级未读消息
    public LiveData<List<Message>> getHighPriorityUnreadMessages() {
        return messageDao.getHighPriorityUnreadMessages(PRIORITY_HIGH);
    }

    // 获取最近24小时内的高优先级消息
    public LiveData<List<Message>> getRecentHighPriorityMessages() {
        long currentTime = System.currentTimeMillis();
        long oneDayAgo = currentTime - (24 * 60 * 60 * 1000);
        return messageDao.getHighPriorityMessagesByTimeRange(oneDayAgo, currentTime, PRIORITY_HIGH);
    }

    // 设置消息优先级
    public void setPriority(Message message) {
        // 根据消息类型和内容设置优先级
        if (message.getType().equals("SYSTEM")) {
            if (message.getContent().contains("紧急") || message.getContent().contains("警告")) {
                message.setPriority(PRIORITY_EMERGENCY);
            } else {
                message.setPriority(PRIORITY_HIGH);
            }
        } else if (message.getType().equals("BUSINESS")) {
            if (message.getContent().contains("安全") || message.getContent().contains("故障")) {
                message.setPriority(PRIORITY_HIGH);
            } else {
                message.setPriority(PRIORITY_NORMAL);
            }
        } else {
            message.setPriority(PRIORITY_LOW);
        }
    }

    // 获取推荐消息
    public List<Message> getRecommendedMessages(int minPriority, int limit) {
        // 使用 Room 的 Executor 来执行数据库操作
        try {
            return MessageDatabase.databaseWriteExecutor.submit(() -> 
                messageDao.getRecommendedMessages(minPriority, limit)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 简化版本的推荐消息方法
    public List<Message> getRecommendedMessages(int count) {
        return getRecommendedMessages(PRIORITY_NORMAL, count);
    }

    public void update(Message message) {
        MessageDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.update(message);
        });
    }
} 