package com.fengziyu.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.viewmodel.MessageViewModel;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.appcompat.widget.Toolbar;

public class MessageDetailActivity extends AppCompatActivity {
    private MessageViewModel viewModel;
    private ExoPlayer player;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
} 