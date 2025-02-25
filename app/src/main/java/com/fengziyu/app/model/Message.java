package com.fengziyu.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;           // 消息标题
    private String content;         // 消息内容
    private long timestamp;         // 时间戳
    private String type;           // 消息类型(SYSTEM/BUSINESS/OPERATION)
    private String mediaUrl;       // 媒体URL(图片/视频)
    private boolean isRead;        // 是否已读
    private int priority;          // 优先级
    private String targetAccount;  // 目标账号
    private String targetVehicle;  // 目标车辆
    private String targetLocation; // 目标位置
    private String displayType;    // 展示类型(POPUP/NOTIFICATION/CAROUSEL)
    private String jumpLink;       // 跳转链接

    // 常量定义
    public static final String DISPLAY_POPUP = "POPUP";         // 弹窗展示
    public static final String DISPLAY_NOTIFICATION = "NOTIFICATION"; // 通知栏展示
    public static final String DISPLAY_CAROUSEL = "CAROUSEL";    // 轮播展示

    // Room 将使用这个无参构造函数
    public Message() {
        // Room需要的空构造函数
    }

    @Ignore // 告诉 Room 忽略这个构造函数
    public Message(long id, String title, String content, long timestamp, String type, 
                  String mediaUrl, boolean isRead, int priority, String targetAccount, 
                  String targetVehicle, String targetLocation, String displayType, String jumpLink) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.isRead = isRead;
        this.priority = priority;
        this.targetAccount = targetAccount;
        this.targetVehicle = targetVehicle;
        this.targetLocation = targetLocation;
        this.displayType = displayType;
        this.jumpLink = jumpLink;
    }

    // Getters and Setters
    // ... 省略getter/setter方法

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }

    public String getTargetVehicle() {
        return targetVehicle;
    }

    public void setTargetVehicle(String targetVehicle) {
        this.targetVehicle = targetVehicle;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getJumpLink() {
        return jumpLink;
    }

    public void setJumpLink(String jumpLink) {
        this.jumpLink = jumpLink;
    }

    @Ignore // 告诉 Room 忽略这个 Parcelable 构造函数
    protected Message(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        timestamp = in.readLong();
        type = in.readString();
        mediaUrl = in.readString();
        isRead = in.readByte() != 0;
        priority = in.readInt();
        targetAccount = in.readString();
        targetVehicle = in.readString();
        targetLocation = in.readString();
        displayType = in.readString();
        jumpLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(timestamp);
        dest.writeString(type);
        dest.writeString(mediaUrl);
        dest.writeByte((byte) (isRead ? 1 : 0));
        dest.writeInt(priority);
        dest.writeString(targetAccount);
        dest.writeString(targetVehicle);
        dest.writeString(targetLocation);
        dest.writeString(displayType);
        dest.writeString(jumpLink);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}