package com.fengziyu.app.recommend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.repository.MessageRepository;
import java.util.List;

public class MessageRecommendService extends Service {
    private MessageRepository repository;
    
    @Override
    public void onCreate() {
        super.onCreate();
        repository = new MessageRepository(getApplication());
    }
    
    private final IMessageRecommendInterface.Stub binder = new IMessageRecommendInterface.Stub() {
        @Override
        public List<Message> getRecommendedMessages(int count) throws RemoteException {
            return repository.getRecommendedMessages(count);
        }
    };
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
} 