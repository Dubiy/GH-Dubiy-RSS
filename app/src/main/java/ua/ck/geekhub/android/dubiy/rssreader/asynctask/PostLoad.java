package ua.ck.geekhub.android.dubiy.rssreader.asynctask;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
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

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.StartActivity;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.Const;

/**
 * Created by igor on 4/9/15.
 */
public class PostLoad extends AsyncTask<Object, Void, Integer> {

    private Context context;
    private ArrayList<PostEntity> postEntities = new ArrayList<PostEntity>();
    private int newPostsCount = 0;
    private PendingIntent resultPendingIntent;
    private NotificationManager mNotificationManager;
    private Activity activity;
    private boolean showNotifications = true;
    private TopicsFragment topicsFragment = null;

    public PostLoad(Context contextin, Activity activityIn) {
        context = contextin;
        activity = activityIn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (activity != null) {
            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.loading_indicator);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected Integer doInBackground(Object... params) {
        showNotifications = (Boolean) params[0];
        if (params.length > 1) {
            topicsFragment = (TopicsFragment) params[1];
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
//            Toast.makeText(context, "No internet access", Toast.LENGTH_LONG).show();
            return 0;
        }




        SimpleDateFormat parseDateFormat = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {
                PostEntity.COLUMN_DATE
        };

        ArrayList<Long> postDates = new ArrayList<Long>();
        Cursor cursor = db.query(PostEntity.TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndexDate = cursor.getColumnIndex(PostEntity.COLUMN_DATE);
            do {
                postDates.add(cursor.getLong(columnIndexDate));
            } while (cursor.moveToNext());
        }
        cursor.close();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Const.FEED_URL);
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
                    newPostsCount++;



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

                }
            }

//            if (view != null) {
//                view.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ListView listView = (ListView) view.findViewById(R.id.listView);
//                        if (listView == null) {
//                            listView = (ListView) view.findViewById(R.id.left_drawer);
//                        }
//                        HabraAdapter habraAdapter = (HabraAdapter) listView.getAdapter();
//                        if (habraAdapter != null) {
//                            habraAdapter.addAll(postEntities);
//                            habraAdapter.notifyDataSetChanged();
//                        }
//                        int checkedItemPosition = listView.getCheckedItemPosition();
//                        listView.setItemChecked(checkedItemPosition, true);
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
//            }

            //do with newPostCount




        } catch (Exception e) {
            e.printStackTrace();
        }

        return newPostsCount;
    }

    @Override
    protected void onPostExecute(Integer newPostsCount) {
        super.onPostExecute(newPostsCount);
        if (newPostsCount > 0 && showNotifications) {
            mNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(context, StartActivity.class);
            resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).
                    setSmallIcon(R.drawable.ic_launcher).
                    setContentTitle(context.getText(R.string.app_name)).
                    setContentText("Loaded new posts!").
                    setNumber(newPostsCount).
                    setAutoCancel(true);
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(Const.NOTIFICATION_ID + 1, builder.build());
        }
        if (newPostsCount > 0) {
            Toast.makeText(context, "New posts: " + newPostsCount, Toast.LENGTH_SHORT).show();
        }

        if (activity != null) {
            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.loading_indicator);
//            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.loading_indicator);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

//            if (topicsFragment != null) {
//                topicsFragment.reload_list();
//            }

            ListView listView = (ListView) activity.findViewById(R.id.listView);
            if (listView != null) {
                HabraAdapter habraAdapter = (HabraAdapter)listView.getAdapter();
                habraAdapter.notifyDataSetChanged();
                habraAdapter.notifyDataSetInvalidated();
            }
        }
    }
}