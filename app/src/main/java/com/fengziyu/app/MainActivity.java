package com.fengziyu.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.fengziyu.app.mqtt.MQTTService;
import com.fengziyu.app.test.MessageRecommendTestActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.fengziyu.app.adapter.MessagePagerAdapter;
import com.fengziyu.app.adapter.CarouselAdapter;
import com.fengziyu.app.viewmodel.MessageViewModel;
import com.fengziyu.app.model.Message;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MessageViewModel viewModel;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Handler handler = new Handler();
    private CarouselAdapter carouselAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动MQTT服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MQTTService.class));
        } else {
            startService(new Intent(this, MQTTService.class));
        }

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        
        // 检查是否需要添加测试数据
        checkAndAddTestData();
        
        // 初始化视图
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        
        // 设置ViewPager适配器
        MessagePagerAdapter pagerAdapter = new MessagePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 设置TabLayout和ViewPager联动
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String type = "";
            switch (position) {
                case 0:
                    type = "SYSTEM";
                    tab.setText("系统通知");
                    break;
                case 1:
                    type = "BUSINESS";
                    tab.setText("业务消息");
                    break;
                case 2:
                    type = "OPERATION";
                    tab.setText("运营消息");
                    break;
            }
            // 为每个类型设置未读消息数监听
            final String finalType = type;
            viewModel.getUnreadCountByType(finalType).observe(this, count -> {
                if (count > 0) {
                    tab.setText(getTabText(position, count));
                } else {
                    tab.setText(getTabText(position, null));
                }
            });
        }).attach();

        setupCarousel();
        
        // 添加一些轮播测试数据
        addCarouselTestData();

        // 添加测试按钮点击事件
        findViewById(R.id.btnTest).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessageRecommendTestActivity.class);
            startActivity(intent);
        });
    }

    private String getTabText(int position, Integer unreadCount) {
        String baseText;
        switch (position) {
            case 0:
                baseText = "系统通知";
                break;
            case 1:
                baseText = "业务消息";
                break;
            case 2:
                baseText = "运营消息";
                break;
            default:
                baseText = "";
        }
        return unreadCount != null && unreadCount > 0 ? baseText + "(" + unreadCount + ")" : baseText;
    }

    private void checkAndAddTestData() {
        // 使用 SharedPreferences 来记录是否已添加过测试数据
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean hasTestData = prefs.getBoolean("has_test_data", false);
        
        if (!hasTestData) {
            addTestData();
            // 标记已添加测试数据
            prefs.edit().putBoolean("has_test_data", true).apply();
        }
    }

    private void addTestData() {
        // 系统通知
        Message message1 = new Message();
        message1.setTitle("系统维护通知");
        message1.setContent("尊敬的用户，系统将于2024年3月20日凌晨2:00-4:00进行例行维护，维护期间部分功能可能无法使用，请提前做好相关安排。给您带来的不便敬请谅解。");
        message1.setType("SYSTEM");
        message1.setTimestamp(System.currentTimeMillis() - 3600000);
        message1.setRead(false);
        message1.setPriority(2);
        message1.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");

        Message message2 = new Message();
        message2.setTitle("账号安全提醒");
        message2.setContent("我们检测到您的账号在新设备上登录，登录地点：上海市。如非本人操作，请立即修改密码。");
        message2.setType("SYSTEM");
        message2.setTimestamp(System.currentTimeMillis() - 7200000);
        message2.setRead(false);
        message2.setPriority(3);
        message2.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");

        // 业务消息
        Message message3 = new Message();
        message3.setTitle("新功能上线通知");
        message3.setContent("智能泊车辅助系统2.0版本现已上线，支持更精准的车位识别和自动泊车功能。查看视频了解详情 >>");
        message3.setType("BUSINESS");
        message3.setTimestamp(System.currentTimeMillis() - 86400000);
        message3.setRead(false);
        message3.setMediaUrl("https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4");
        message3.setPriority(1);

        Message message4 = new Message();
        message4.setTitle("车辆保养提醒");
        message4.setContent("您的爱车已行驶4800公里，建议进行常规保养。可点击预约最近的服务网点。");
        message4.setType("BUSINESS");
        message4.setTimestamp(System.currentTimeMillis() - 172800000);
        message4.setRead(true);
        message4.setPriority(2);
        message4.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");

        // 运营消息
        Message message5 = new Message();
        message5.setTitle("限时优惠活动");
        message5.setContent("春季焕新活动开启！3月15日-3月31日期间，到店保养享受工时费5折优惠，更有多重好礼相送。");
        message5.setType("OPERATION");
        message5.setTimestamp(System.currentTimeMillis() - 259200000);
        message5.setRead(false);
        message5.setMediaUrl("https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4");
        message5.setPriority(1);

        Message message6 = new Message();
        message6.setTitle("新车发布会直播");
        message6.setContent("诚邀您观看全新ID.7 VIZZION纯电动轿车线上发布会，感受科技与设计的完美融合。");
        message6.setType("OPERATION");
        message6.setTimestamp(System.currentTimeMillis() - 432000000);
        message6.setRead(true);
        message6.setPriority(2);
        message6.setMediaUrl("https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4");

        Message message7 = new Message();
        message7.setTitle("新款车型预览");
        message7.setContent("全新一代智能SUV即将上市，搭载最新的智能驾驶辅助系统，带来更安全、更舒适的驾驶体验。");
        message7.setType("BUSINESS");
        message7.setTimestamp(System.currentTimeMillis() - 345600000); // 4天前
        message7.setRead(false);
        message7.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");
        message7.setPriority(1);

        Message message8 = new Message();
        message8.setTitle("安全驾驶指南");
        message8.setContent("冬季安全驾驶小贴士：请查看视频了解详细的冬季行车注意事项。");
        message8.setType("SYSTEM");
        message8.setTimestamp(System.currentTimeMillis() - 518400000); // 6天前
        message8.setRead(false);
        message8.setMediaUrl("https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4");
        message8.setPriority(2);

        // 插入数据
        viewModel.insert(message1);
        viewModel.insert(message2);
        viewModel.insert(message3);
        viewModel.insert(message4);
        viewModel.insert(message5);
        viewModel.insert(message6);
        viewModel.insert(message7);
        viewModel.insert(message8);
    }

    private void setupCarousel() {
        ViewPager2 carouselPager = findViewById(R.id.carouselPager);
        carouselAdapter = new CarouselAdapter();
        carouselPager.setAdapter(carouselAdapter);

        // 自动轮播
        Runnable carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (carouselAdapter.getItemCount() > 0) {
                    int nextItem = (carouselPager.getCurrentItem() + 1) % carouselAdapter.getItemCount();
                    carouselPager.setCurrentItem(nextItem);
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(carouselRunnable, 3000);
    }

    private void addCarouselTestData() {
        Message carouselMessage1 = new Message();
        carouselMessage1.setTitle("新车发布");
        carouselMessage1.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");
        carouselMessage1.setDisplayType(Message.DISPLAY_CAROUSEL);

        Message carouselMessage2 = new Message();
        carouselMessage2.setTitle("限时优惠");
        carouselMessage2.setMediaUrl("https://ts1.cn.mm.bing.net/th?id=ORMS.9c29efcbf41aa37188b99b4fd4827e29&pid=Wdp&w=612&h=304&qlt=90&c=1&rs=1&dpr=1&p=0");
        carouselMessage2.setDisplayType(Message.DISPLAY_CAROUSEL);

        List<Message> carouselMessages = Arrays.asList(carouselMessage1, carouselMessage2);
        carouselAdapter.setMessages(carouselMessages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}