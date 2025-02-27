package com.fengziyu.app.recommend;

import com.fengziyu.app.model.Message;
import java.util.List;

interface IMessageRecommendService {
    List<Message> getRecommendedMessages();
    void saveRecommendedMessage(in Message message);
} 