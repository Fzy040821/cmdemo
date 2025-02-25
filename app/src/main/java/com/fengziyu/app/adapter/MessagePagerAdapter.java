package com.fengziyu.app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.fengziyu.app.ui.MessageListFragment;

public class MessagePagerAdapter extends FragmentStateAdapter {
    private static final String[] MESSAGE_TYPES = {"SYSTEM", "BUSINESS", "OPERATION"};

    public MessagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MessageListFragment.newInstance(MESSAGE_TYPES[position]);
    }

    @Override
    public int getItemCount() {
        return MESSAGE_TYPES.length;
    }
} 