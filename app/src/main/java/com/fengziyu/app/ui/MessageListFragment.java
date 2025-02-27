package com.fengziyu.app.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fengziyu.app.MessageDetailActivity;
import com.fengziyu.app.R;
import com.fengziyu.app.adapter.MessageAdapter;
import com.fengziyu.app.model.Message;
import com.fengziyu.app.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageListFragment extends Fragment {
    private MessageViewModel viewModel;
    private MessageAdapter adapter;
    private String messageType;
    private ActionMode actionMode;
    private List<Message> selectedMessages = new ArrayList<>();

    //批量删除
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_message_selection, menu);
            mode.setTitle("已选择 1 项");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteSelectedMessages();
                return true;
            } else if (id == R.id.action_edit) {
                editSelectedMessage();
                return true;
            } else if (id == R.id.action_share) {
                shareSelectedMessages();
                return true;
            } else if (id == R.id.action_jump) {
                jumpToApp();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            selectedMessages.clear();
            adapter.clearSelection();
        }
    };

    public static MessageListFragment newInstance(String type) {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageType = getArguments().getString("type");
        viewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        TextView emptyView = view.findViewById(R.id.emptyView);
        ProgressBar loadingView = view.findViewById(R.id.loadingView);
        Button btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(message -> {
            if (actionMode != null) {
                // 在多选模式下，点击切换选中状态
                toggleSelection(message);
            } else {
                // 正常模式下，打开详情页
                Intent intent = new Intent(requireContext(), MessageDetailActivity.class);
                intent.putExtra("message_id", message.getId());
                startActivity(intent);
                viewModel.markAsRead(message.getId());
            }
        });

        adapter.setOnItemLongClickListener(message -> {
            if (actionMode == null) {
                // 进入多选模式
                actionMode = requireActivity().startActionMode(actionModeCallback);
                adapter.toggleSelection(message);
                selectedMessages.add(message);
                return true;
            }
            return false;
        });

        // 设置下拉刷新
        swipeRefresh.setOnRefreshListener(() -> {
            loadingView.setVisibility(View.VISIBLE);
            // 模拟刷新操作
            new Handler().postDelayed(() -> {
                swipeRefresh.setRefreshing(false);
                loadingView.setVisibility(View.GONE);
            }, 1000);
        });

        // 一键已读按钮点击事件
        btnMarkAllRead.setOnClickListener(v -> {
            viewModel.markAllAsRead();
        });

        // 观察消息列表变化
        viewModel.getAllMessages().observe(getViewLifecycleOwner(), messages -> {
            loadingView.setVisibility(View.GONE);
            
            List<Message> filteredMessages = messages.stream()
                    .filter(msg -> msg.getType().equals(messageType))
                    .collect(Collectors.toList());
                    
            adapter.setMessages(filteredMessages);
            
            // 处理空状态
            if (filteredMessages.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    // 添加切换选中状态的方法
    private void toggleSelection(Message message) {
        if (selectedMessages.contains(message)) {
            selectedMessages.remove(message);
        } else {
            selectedMessages.add(message);
        }
        adapter.toggleSelection(message);

        // 更新 ActionMode 标题
        if (actionMode != null) {
            actionMode.setTitle(selectedMessages.size() + " 已选择");
            // 如果没有选中的项目，退出多选模式
            if (selectedMessages.isEmpty()) {
                actionMode.finish();
            }
        }
    }

    // 删除选中的消息
    private void deleteSelectedMessages() {
        new AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除选中的 " + selectedMessages.size() + " 条消息吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                viewModel.deleteMessages(new ArrayList<>(selectedMessages));
                selectedMessages.clear();
                if (actionMode != null) {
                    actionMode.finish();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    // 编辑选中的消息
    private void editSelectedMessage() {
        if (selectedMessages.size() != 1) {
            Toast.makeText(requireContext(), "请选择一条消息进行编辑", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = selectedMessages.iterator().next();
        showEditDialog(message);
    }

    // 分享选中的消息
    private void shareSelectedMessages() {
        StringBuilder content = new StringBuilder();
        for (Message message : selectedMessages) {
            content.append(message.getTitle())
                  .append("\n")
                  .append(message.getContent())
                  .append("\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        startActivity(Intent.createChooser(shareIntent, "分享消息"));
    }

    // 跳转到其他应用
    private void jumpToApp() {
        if (selectedMessages.size() != 1) {
            Toast.makeText(requireContext(), "请选择一条消息进行跳转", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = selectedMessages.iterator().next();
        String jumpLink = message.getJumpLink();
        
        if (jumpLink != null && !jumpLink.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jumpLink));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "未找到可以打开的应用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "该消息没有关联的跳转链接", Toast.LENGTH_SHORT).show();
        }
    }

    // 显示编辑对话框
    private void showEditDialog(Message message) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_message, null);
        
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etContent = dialogView.findViewById(R.id.etContent);
        EditText etJumpLink = dialogView.findViewById(R.id.etJumpLink);
        
        etTitle.setText(message.getTitle());
        etContent.setText(message.getContent());
        etJumpLink.setText(message.getJumpLink());

        new AlertDialog.Builder(requireContext())
            .setTitle("编辑消息")
            .setView(dialogView)
            .setPositiveButton("保存", (dialog, which) -> {
                message.setTitle(etTitle.getText().toString());
                message.setContent(etContent.getText().toString());
                message.setJumpLink(etJumpLink.getText().toString());
                viewModel.updateMessage(message);
                actionMode.finish();
            })
            .setNegativeButton("取消", null)
            .show();
    }
} 