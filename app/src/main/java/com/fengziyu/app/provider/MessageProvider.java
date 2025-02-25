package com.fengziyu.app.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fengziyu.app.db.MessageDatabase;
import com.fengziyu.app.model.Message;

public class MessageProvider extends ContentProvider {
    private static final String AUTHORITY = "com.fengziyu.app.provider";
    private static final String PATH_MESSAGES = "messages";
    private static final int CODE_MESSAGES_DIR = 1;
    private static final int CODE_MESSAGES_ITEM = 2;
    
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static {
        uriMatcher.addURI(AUTHORITY, PATH_MESSAGES, CODE_MESSAGES_DIR);
        uriMatcher.addURI(AUTHORITY, PATH_MESSAGES + "/#", CODE_MESSAGES_ITEM);
    }
    
    private MessageDatabase db;
    
    @Override
    public boolean onCreate() {
        db = MessageDatabase.getInstance(getContext());
        return true;
    }
    
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                       @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case CODE_MESSAGES_DIR:
                cursor = db.messageDao().getAllMessagesCursor();
                break;
            case CODE_MESSAGES_ITEM:
                long id = Long.parseLong(uri.getLastPathSegment());
                cursor = db.messageDao().getMessageByIdCursor(id);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_MESSAGES_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + PATH_MESSAGES;
            case CODE_MESSAGES_ITEM:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + PATH_MESSAGES;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // 本示例中不支持外部插入
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // 本示例中不支持外部删除
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                     @Nullable String[] selectionArgs) {
        // 本示例中不支持外部更新
        return 0;
    }
} 