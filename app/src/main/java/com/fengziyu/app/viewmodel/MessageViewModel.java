package com.fengziyu.app.viewmodel;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.fengziyu.app.App;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.repository.MessageRepository;
import java.util.List;
import java.util.Locale;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository repository;
    private TextToSpeech textToSpeech;
    private final LiveData<List<Message>> allMessages;
    private final LiveData<List<Message>> unreadMessages;

    public MessageViewModel(Application application) {
        super(application);
        repository = new MessageRepository(application);
        allMessages = repository.getAllMessages();
        unreadMessages = repository.getUnreadMessages();
        
        // 初始化TTS
        textToSpeech = new TextToSpeech(application, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.CHINESE);
            }
        });
    }

    // 语音播报
    public void speakMessage(Message message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message.getContent(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // 获取指定消息
    public LiveData<Message> getMessageById(long id) {
        return repository.getMessageById(id);
    }

    // 获取所有消息
    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    // 获取未读消息
    public LiveData<List<Message>> getUnreadMessages() {
        return unreadMessages;
    }

    // 标记消息已读
    public void markAsRead(long messageId) {
        repository.markAsRead(messageId);
    }

    // 一键已读
    public void markAllAsRead() {
        repository.markAllAsRead();
    }

    // 删除消息
    public void deleteMessage(Message message) {
        repository.delete(message);
    }

    // 批量删除消息
    public void deleteMessages(List<Message> messages) {
        repository.deleteMessages(messages);
    }

    // 删除所有消息
    public void deleteAllMessages() {
        repository.deleteAll();
    }

    public void insert(Message message) {
        repository.insert(message);
    }

    public LiveData<Integer> getUnreadCountByType(String type) {
        return repository.getUnreadCountByType(type);
    }

    public void updateMessage(Message message) {
        repository.update(message);
    }

    @Override
    protected void onCleared() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        super.onCleared();
    }
} 