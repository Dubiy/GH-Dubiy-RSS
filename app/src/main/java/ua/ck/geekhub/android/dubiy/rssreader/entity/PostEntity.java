package ua.ck.geekhub.android.dubiy.rssreader.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.utils.BaseClass;

public class PostEntity extends BaseClass implements BaseColumns {
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
    public static final String SQL_CREATE = "CREATE TABLE " + PostEntity.TABLE_NAME + " (" +
            PostEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PostEntity.COLUMN_TITLE + " TEXT," +
            PostEntity.COLUMN_LINK + " TEXT," +
            PostEntity.COLUMN_DATE + " INTEGER," +
            PostEntity.COLUMN_CONTENT + " TEXT," +
            PostEntity.COLUMN_FAVOURITE + " INTEGER DEFAULT 0" +
            ")";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + PostEntity.TABLE_NAME;

    public PostEntity() {
        this.id = 0;
        this.title = "HabraReader";
        this.link = "http://garik.pp.ua";
        this.date = 0;
        this.content = "";
        this.favourite = 0;
    }

    public int updatePostFavourite(Context context, long postId, boolean favourite) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PostEntity.COLUMN_FAVOURITE, favourite ? 1 : 0);
        String selection = "_id = ?";
        String[] selectionArgs = new String[] { String.valueOf(postId)};
        return db.update(PostEntity.TABLE_NAME, values, selection, selectionArgs);
    }

    public boolean loadFromDatabase(Context context, long postId) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PostEntity._ID,
                PostEntity.COLUMN_TITLE,
                PostEntity.COLUMN_LINK,
                PostEntity.COLUMN_DATE,
                PostEntity.COLUMN_CONTENT,
                PostEntity.COLUMN_FAVOURITE
        };
        String selection = "_id = ?";
        String[] selectionArgs = new String[] { String.valueOf(postId)};
        Cursor cursor = db.query(PostEntity.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndexTitle = cursor.getColumnIndex(PostEntity.COLUMN_TITLE);
            int columnIndexLink = cursor.getColumnIndex(PostEntity.COLUMN_LINK);
            int columnIndexDate = cursor.getColumnIndex(PostEntity.COLUMN_DATE);
            int columnIndexContent = cursor.getColumnIndex(PostEntity.COLUMN_CONTENT);
            int columnIndexFavourite = cursor.getColumnIndex(PostEntity.COLUMN_FAVOURITE);

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

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public boolean getFavourite() {
        if (favourite == 1) {
            return true;
        }
        return false;
    }
}
