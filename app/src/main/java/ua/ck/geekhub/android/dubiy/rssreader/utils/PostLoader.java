package ua.ck.geekhub.android.dubiy.rssreader.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;

/**
 * Created by Gary on 23.11.2014.
 */
public class PostLoader extends BaseClass {
    private Context context;
    private View view;
    private String url;
    private ProgressBar progressBar;
    private ArrayList<PostEntity> postEntities = new ArrayList<PostEntity>();
    private int newPostsCount = 0;

    public PostLoader() {
    }

    public PostLoader(Context context) {
        this.context = context;
        this.view = null;
        url = Const.FEED_URL;
    }

    public PostLoader(Context context, View view) {
        this.context = context;
        this.view = view;
        url = Const.FEED_URL;

    }


    public int refresh_posts() {



        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            Toast.makeText(context, "No internet access", Toast.LENGTH_LONG).show();
            return 0;
        }


        if (view != null) {
            progressBar = (ProgressBar) view.findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.VISIBLE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat parseDateFormat = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                DBHelper dbHelper = DBHelper.getInstance(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String[] projection = {
                        PostEntity.COLUMN_DATE
                };

                ArrayList<Long>postDates = new ArrayList<Long>();
                Cursor cursor = db.query(PostEntity.TABLE_NAME, projection, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int columnIndexDate = cursor.getColumnIndex(PostEntity.COLUMN_DATE);
                    do {
                        postDates.add(cursor.getLong(columnIndexDate));
                    } while (cursor.moveToNext());
                } else {
//                        Log.d(LOG_TAG, "0 rows");
                }
                cursor.close();

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse httpResponse = client.execute(httpGet);
                    final String response = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");
                    int entries_count = entries.length();

                    long rowId;
                    String title;
                    Long timestamp = new Long(0);
                    String link;
                    String content;

                    for (int i = 0; i < entries_count; i++) {
                        try {
                            Date parsedDate = parseDateFormat.parse(entries.getJSONObject(i).getString("publishedDate"));
                            timestamp = (long)parsedDate.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (postDates.contains(timestamp)) {
                            //Log.d(LOG_TAG, "dont insetr");
                        } else {
                            //Log.d(LOG_TAG, "INSERT");
                            title = entries.getJSONObject(i).getString("title");
                            link = entries.getJSONObject(i).getString("link");
                            content = entries.getJSONObject(i).getString("content");

                            ContentValues values = new ContentValues();
                            values.put(PostEntity.COLUMN_TITLE, title);
                            values.put(PostEntity.COLUMN_DATE, timestamp);
                            values.put(PostEntity.COLUMN_LINK, link);
                            values.put(PostEntity.COLUMN_CONTENT, content);
//                              values.put(HabraPost.COLUMN_FAVOURITE, 1);
                            rowId = db.insert(PostEntity.TABLE_NAME, null, values);


                            PostEntity tmp_post = new PostEntity();
                            tmp_post.setId(rowId);
                            tmp_post.setTitle(title);
                            tmp_post.setDate(timestamp);
                            tmp_post.setLink(link);
                            tmp_post.setContent(content);
                            postEntities.add(tmp_post);
                            newPostsCount++;
                        }
                    }

                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                ListView listView = (ListView) view.findViewById(R.id.listView);
                                if (listView == null) {
                                    listView = (ListView) view.findViewById(R.id.left_drawer);
                                }
                                HabraAdapter habraAdapter = (HabraAdapter) listView.getAdapter();
                                if (habraAdapter != null) {
                                    habraAdapter.addAll(postEntities);
                                    habraAdapter.notifyDataSetChanged();
                                }
                                int checkedItemPosition = listView.getCheckedItemPosition();
                                listView.setItemChecked(checkedItemPosition, true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    //do with newPostCount




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



        return newPostsCount;
    }


}
