package com.fengziyu.app.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.recommend.IMessageRecommendInterface;
import java.util.List;

public class MessageRecommendTestActivity extends AppCompatActivity {
    private IMessageRecommendInterface messageService;
    private TextView resultView;
    private boolean isBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messageService = IMessageRecommendInterface.Stub.asInterface(service);
            isBound = true;
            updateStatus("服务已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messageService = null;
            isBound = false;
            updateStatus("服务已断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_recommend_test);

        resultView = findViewById(R.id.resultView);
        Button testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(v -> testRecommendService());

        // 绑定服务
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
            "com.fengziyu.app",
            "com.fengziyu.app.recommend.MessageRecommendService"
        ));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void testRecommendService() {
        if (!isBound || messageService == null) {
            updateStatus("服务未连接");
            return;
        }

        try {
            // 获取5条推荐消息
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
        runOnUiThread(() -> resultView.setText(status));
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