package com.fengziyu.app.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fengziyu.app.MessageDetailActivity;
import com.fengziyu.app.R;
import com.fengziyu.app.model.Message;

import java.util.ArrayList;
import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    private List<Message> messages = new ArrayList<>();

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carousel, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvTitle.setText(message.getTitle());
        
        if (message.getMediaUrl() != null) {
            holder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.getMediaUrl())
                    .into(holder.ivMedia);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MessageDetailActivity.class);
            intent.putExtra("message_id", message.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMedia;
        TextView tvTitle;

        CarouselViewHolder(View view) {
            super(view);
            ivMedia = view.findViewById(R.id.ivMedia);
            tvTitle = view.findViewById(R.id.tvTitle);
        }
    }
} 