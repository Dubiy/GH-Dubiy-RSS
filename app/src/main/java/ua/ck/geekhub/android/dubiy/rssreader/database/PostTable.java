package ua.ck.geekhub.android.dubiy.rssreader.database;

import android.provider.BaseColumns;

/**
 * Created by Gary on 21.12.2014.
 */
public class PostTable implements BaseColumns {
    public static final String TABLE_NAME = "Posts";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTENT = "content";
    public static final String SQL_CREATE = "CREATE TABLE " + PostTable.TABLE_NAME + " (" +
            PostTable._ID + " INTEGER PRIMARY KEY," +
            PostTable.COLUMN_TITLE + " TEXT," +
            PostTable.COLUMN_LINK + " TEXT," +
            PostTable.COLUMN_DATE + " INTEGER," +
            PostTable.COLUMN_CONTENT + " TEXT" +
            ")";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + PostTable.TABLE_NAME;



}
