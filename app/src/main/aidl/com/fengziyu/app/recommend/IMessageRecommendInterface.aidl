package com.fengziyu.app.recommend;

import com.fengziyu.app.model.Message;
import java.util.List;

interface IMessageRecommendInterface {
    List<Message> getRecommendedMessages(int count);
} 