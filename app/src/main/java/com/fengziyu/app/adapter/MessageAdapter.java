package com.fengziyu.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages = new ArrayList<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private Set<Message> selectedMessages = new HashSet<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(Message message);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(Message message);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public void toggleSelection(Message message) {
        if (selectedMessages.contains(message)) {
            selectedMessages.remove(message);
        } else {
            selectedMessages.add(message);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedMessages.clear();
        notifyDataSetChanged();
    }

    public void removeMessages(List<Message> messagesToRemove) {
        messages.removeAll(messagesToRemove);
        selectedMessages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        
        // 设置标题和内容
        holder.tvTitle.setText(message.getTitle());
        holder.tvContent.setText(message.getContent());
        
        // 格式化并设置时间
        holder.tvTime.setText(dateFormat.format(new Date(message.getTimestamp())));
        
        // 设置消息类型图标
        setMessageTypeIcon(holder.ivIcon, message.getType());
        
        // 处理媒体内容
        if (message.getMediaUrl() != null && !message.getMediaUrl().isEmpty()) {
            holder.mediaContainer.setVisibility(View.VISIBLE);
            
            // 判断是否为视频
            boolean isVideo = message.getMediaUrl().endsWith(".mp4");
            holder.ivPlayIcon.setVisibility(isVideo ? View.VISIBLE : View.GONE);
            
            // 加载媒体缩略图
            Glide.with(holder.ivMedia.getContext())
                 .load(isVideo ? R.drawable.ic_video_placeholder : message.getMediaUrl())
                 .placeholder(R.drawable.ic_image_placeholder)
                 .error(R.drawable.ic_image_placeholder)
                 .centerCrop()
                 .into(holder.ivMedia);
        } else {
            holder.mediaContainer.setVisibility(View.GONE);
        }
        
        // 设置选中状态的背景
        holder.itemView.setSelected(selectedMessages.contains(message));
        
        // 处理未读标记
        holder.tvUnread.setVisibility(message.isRead() ? View.GONE : View.VISIBLE);
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(message);
            }
        });

        // 设置长按事件
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                return longClickListener.onItemLongClick(message);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        TextView tvUnread;
        ImageView ivMedia;
        ImageView ivPlayIcon;
        View mediaContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnread = itemView.findViewById(R.id.tvUnread);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivPlayIcon = itemView.findViewById(R.id.ivPlayIcon);
            mediaContainer = itemView.findViewById(R.id.mediaContainer);
        }
    }

    public void delete(Message message) {
        messages.remove(message);
        notifyDataSetChanged();
    }

    private void setMessageTypeIcon(ImageView imageView, String type) {
        int iconRes;
        switch (type) {
            case "SYSTEM":
                iconRes = R.drawable.ic_system_message;
                break;
            case "BUSINESS":
                iconRes = R.drawable.ic_business_message;
                break;
            case "OPERATION":
                iconRes = R.drawable.ic_operation_message;
                break;
            default:
                iconRes = R.drawable.ic_default_message;
        }
        imageView.setImageResource(iconRes);
    }
} 