package com.fengziyu.app.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.recommend.IMessageRecommendInterface;
import com.fengziyu.app.recommend.MessageRecommendService;

import java.util.List;

public class MessageRecommendTestActivity extends AppCompatActivity {
    private IMessageRecommendInterface messageService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TestActivity", "Service connected");
            messageService = IMessageRecommendInterface.Stub.asInterface(service);
            isBound = true;
            loadRecommendedMessages();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TestActivity", "Service disconnected");
            messageService = null;
            isBound = false;
        }
    };

    private TextView resultView;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_recommend_test);

        resultView = findViewById(R.id.resultView);
        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> testRecommendService());

        // 修改绑定服务的方式
        Intent intent = new Intent();
        intent.setAction("com.fengziyu.app.recommend.IMessageRecommendInterface");
        intent.setPackage(getPackageName());
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void loadRecommendedMessages() {
        try {
            List<Message> messages = messageService.getRecommendedMessages(5);
            updateRecommendList(messages);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void testRecommendService() {
        if (!isBound || messageService == null) {
            updateStatus("服务未连接");
            return;
        }

        try {
            List<Message> messages = messageService.getRecommendedMessages(5);
            StringBuilder result = new StringBuilder();
            result.append("获取到 ").append(messages.size()).append(" 条推荐消息:\n\n");
            
            for (Message msg : messages) {
                result.append("标题: ").append(msg.getTitle()).append("\n");
                result.append("内容: ").append(msg.getContent()).append("\n");
                result.append("优先级: ").append(msg.getPriority()).append("\n");
                result.append("------------------------\n");
            }
            
            updateStatus(result.toString());
        } catch (RemoteException e) {
            updateStatus("测试失败: " + e.getMessage());
        }
    }

    private void updateStatus(String status) {
        resultView.setText(status);
    }

    private void updateRecommendList(List<Message> messages) {
        StringBuilder result = new StringBuilder();
        result.append("推荐消息列表:\n\n");
        
        for (Message msg : messages) {
            result.append("标题: ").append(msg.getTitle()).append("\n");
            result.append("内容: ").append(msg.getContent()).append("\n");
            result.append("------------------------\n");
        }
        
        updateStatus(result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
} 