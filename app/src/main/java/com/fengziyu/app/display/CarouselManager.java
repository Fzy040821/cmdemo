package com.fengziyu.app.display;

import com.fengziyu.app.model.Message;

import java.util.ArrayList;
import java.util.List;

public class CarouselManager {
    private static CarouselManager instance;
    private List<Message> carouselMessages = new ArrayList<>();
    private List<CarouselCallback> callbacks = new ArrayList<>();

    public static CarouselManager getInstance() {
        if (instance == null) {
            instance = new CarouselManager();
        }
        return instance;
    }

    public void addMessage(Message message) {
        carouselMessages.add(message);
        notifyCallbacks();
    }

    public void removeMessage(Message message) {
        carouselMessages.remove(message);
        notifyCallbacks();
    }

    public List<Message> getCarouselMessages() {
        return carouselMessages;
    }

    public void registerCallback(CarouselCallback callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(CarouselCallback callback) {
        callbacks.remove(callback);
    }

    private void notifyCallbacks() {
        for (CarouselCallback callback : callbacks) {
            callback.onCarouselMessagesChanged(carouselMessages);
        }
    }

    public interface CarouselCallback {
        void onCarouselMessagesChanged(List<Message> messages);
    }
} 