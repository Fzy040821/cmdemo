package com.fengziyu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.viewmodel.MessageViewModel;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.appcompat.widget.Toolbar;

public class MessageDetailActivity extends AppCompatActivity {
    private MessageViewModel viewModel;
    private ExoPlayer player;
    private boolean isDriving = false; // 行车状态
    private CarStateReceiver carStateReceiver;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        
        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("消息详情");
        }

        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        long messageId = getIntent().getLongExtra("message_id", -1);
        if (messageId == -1) {
            finish();
            return;
        }

        // 初始化视图
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvContent = findViewById(R.id.tvContent);
        TextView tvTime = findViewById(R.id.tvTime);
        ImageView ivMedia = findViewById(R.id.ivMedia);
        PlayerView videoPlayer = findViewById(R.id.videoPlayer);

        // 获取消息详情
        viewModel.getMessageById(messageId).observe(this, message -> {
            if (message != null) {
                tvTitle.setText(message.getTitle());
                tvContent.setText(message.getContent());
                tvTime.setText(dateFormat.format(message.getTimestamp()));

                // 处理媒体内容
                String mediaUrl = message.getMediaUrl();
                if (mediaUrl != null && !mediaUrl.isEmpty()) {
                    if (mediaUrl.endsWith(".mp4")) {
                        // 视频内容
                        videoPlayer.setVisibility(View.VISIBLE);
                        initializePlayer(mediaUrl);
                    } else {
                        // 图片内容
                        ivMedia.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(mediaUrl)
                                .into(ivMedia);
                    }
                }
            }
        });

        // 注册行车状态广播接收器
        carStateReceiver = new CarStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.car.DRIVING_STATE_CHANGED");
        registerReceiver(carStateReceiver, filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 添加返回动画
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // 行车状态广播接收器
    private class CarStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.car.DRIVING_STATE_CHANGED".equals(intent.getAction())) {
                isDriving = intent.getBooleanExtra("driving_state", false);
                if (isDriving && player != null && player.isPlaying()) {
                    // 行车时暂停视频
                    player.pause();
                    showDrivingWarning();
                }
            }
        }
    }

    private void showDrivingWarning() {
        new AlertDialog.Builder(this)
            .setTitle("安全提醒")
            .setMessage("行车过程中暂停视频播放，请注意安全驾驶")
            .setPositiveButton("确定", null)
            .show();
    }

    private void initializePlayer(String mediaUrl) {
        player = new ExoPlayer.Builder(this).build();
        PlayerView videoPlayer = findViewById(R.id.videoPlayer);
        videoPlayer.setPlayer(player);
        
        // 设置控制器
        videoPlayer.setUseController(true);
        videoPlayer.setControllerShowTimeoutMs(3000);
        
        // 准备播放
        MediaItem mediaItem = MediaItem.fromUri(mediaUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        // 添加播放状态监听
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY && isDriving) {
                    // 准备播放时检查行车状态
                    player.pause();
                    showDrivingWarning();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        if (carStateReceiver != null) {
            unregisterReceiver(carStateReceiver);
        }
    }
} 