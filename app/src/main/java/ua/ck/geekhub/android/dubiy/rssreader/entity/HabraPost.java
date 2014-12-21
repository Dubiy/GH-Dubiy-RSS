package ua.ck.geekhub.android.dubiy.rssreader.entity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.utils.BaseClass;

public class HabraPost extends BaseClass implements BaseColumns {
    private long id;
    private String title;
    private String link;
    private long date;
    private String content;
    private int favourite;

    public static final String TABLE_NAME = "Posts";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_FAVOURITE = "favourite";
    public static final String SQL_CREATE = "CREATE TABLE " + HabraPost.TABLE_NAME + " (" +
            HabraPost._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            HabraPost.COLUMN_TITLE + " TEXT," +
            HabraPost.COLUMN_LINK + " TEXT," +
            HabraPost.COLUMN_DATE + " INTEGER," +
            HabraPost.COLUMN_CONTENT + " TEXT," +
            HabraPost.COLUMN_FAVOURITE + " INTEGER DEFAULT 0" +
            ")";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + HabraPost.TABLE_NAME;

    public HabraPost() {
        this.id = 0;
        this.title = "HabraReader";
        this.link = "http://garik.pp.ua";
        this.date = 0;
        this.content = "";
        this.favourite = 0;
    }

    public boolean loadFromDatabase(Context context, long postId) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                HabraPost._ID,
                HabraPost.COLUMN_TITLE,
                HabraPost.COLUMN_LINK,
                HabraPost.COLUMN_DATE,
                HabraPost.COLUMN_CONTENT,
                HabraPost.COLUMN_FAVOURITE
        };
        String selection = "_id = ?";
        String[] selectionArgs = new String[] { String.valueOf(postId)};
        Cursor cursor = db.query(HabraPost.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndexTitle = cursor.getColumnIndex(HabraPost.COLUMN_TITLE);
            int columnIndexLink = cursor.getColumnIndex(HabraPost.COLUMN_LINK);
            int columnIndexDate = cursor.getColumnIndex(HabraPost.COLUMN_DATE);
            int columnIndexContent = cursor.getColumnIndex(HabraPost.COLUMN_CONTENT);
            int columnIndexFavourite = cursor.getColumnIndex(HabraPost.COLUMN_FAVOURITE);

            this.id = postId;
            this.title = cursor.getString(columnIndexTitle);
            this.link = cursor.getString(columnIndexLink);
            this.date = cursor.getLong(columnIndexDate);
            this.content = cursor.getString(columnIndexContent);
            this.favourite = cursor.getInt(columnIndexFavourite);

            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
