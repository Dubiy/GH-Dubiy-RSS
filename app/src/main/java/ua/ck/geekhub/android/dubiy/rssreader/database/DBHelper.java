package ua.ck.geekhub.android.dubiy.rssreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Gary on 21.12.2014.
 */
public class DBHelper extends SQLiteOpenHelper {
    protected final String LOG_TAG = "GARY_" + getClass().getSimpleName();
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "feed.db";
    protected static Context context;

    public static DBHelper getInstance(Context context) {
        DBHelper.context = context;
        final String LOG_TAG = "GARY_DBHelper_singletone (static)";
        Log.d(LOG_TAG, "getInstance()");
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private final static DBHelper instance = new DBHelper(DBHelper.context);
    }


    private DBHelper(Context integer) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "getInstance(" + context.toString() + ")");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "database created");
        db.execSQL(PostTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
