package com.fengziyu.app.db;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.fengziyu.app.model.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM messages")
    void deleteAll();

    @Query("DELETE FROM messages WHERE id IN (:messageIds)")
    void deleteMessages(List<Long> messageIds);

    @Query("SELECT * FROM messages ORDER BY priority DESC, isRead ASC, timestamp DESC")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM messages WHERE type = :type ORDER BY priority DESC, isRead ASC, timestamp DESC")
    LiveData<List<Message>> getMessagesByType(String type);

    @Query("SELECT * FROM messages WHERE isRead = 0")
    LiveData<List<Message>> getUnreadMessages();

    @Query("UPDATE messages SET isRead = 1 WHERE id = :messageId")
    void markAsRead(long messageId);

    @Query("UPDATE messages SET isRead = 1")
    void markAllAsRead();

    @Query("SELECT * FROM messages WHERE id = :messageId")
    LiveData<Message> getMessageById(long messageId);

    @Query("SELECT COUNT(*) FROM messages WHERE type = :type AND isRead = 0")
    LiveData<Integer> getUnreadCountByType(String type);

    @Query("SELECT * FROM messages WHERE targetAccount = :account")
    LiveData<List<Message>> getMessagesByAccount(String account);

    @Query("SELECT * FROM messages WHERE targetVehicle = :vehicle")
    LiveData<List<Message>> getMessagesByVehicle(String vehicle);

    @Query("SELECT * FROM messages WHERE targetLocation = :location")
    LiveData<List<Message>> getMessagesByLocation(String location);

    @Query("SELECT * FROM messages WHERE priority >= :minPriority AND isRead = 0 ORDER BY priority DESC, timestamp DESC")
    LiveData<List<Message>> getHighPriorityUnreadMessages(int minPriority);

    @Query("SELECT * FROM messages WHERE timestamp BETWEEN :startTime AND :endTime AND priority >= :minPriority ORDER BY priority DESC, timestamp DESC")
    LiveData<List<Message>> getHighPriorityMessagesByTimeRange(long startTime, long endTime, int minPriority);

    // 智能推荐相关查询
    @Query("SELECT * FROM messages WHERE priority >= :minPriority ORDER BY timestamp DESC LIMIT :limit")
    List<Message> getRecommendedMessages(int minPriority, int limit);

    // 提供给 ContentProvider 使用的游标查询
    @Query("SELECT * FROM messages")
    Cursor getAllMessagesCursor();

    @Query("SELECT * FROM messages WHERE id = :id")
    Cursor getMessageByIdCursor(long id);

    // 添加这些方法用于ContentProvider
    @Query("SELECT * FROM messages WHERE type = :type")
    Cursor getMessagesByTypeCursor(String type);
} 