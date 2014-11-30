package ua.ck.geekhub.android.dubiy.rssreader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;

/**
 * Created by Gary on 23.11.2014.
 */
public class PostLoader {
    private final String LOG_TAG = getClass().getSimpleName();
    private Context context;
    private View view;
    private String url;
    private ProgressBar progressBar;
    private ArrayList<HabraPost> habraPosts = new ArrayList<HabraPost>();

    public PostLoader() {
    }

    public PostLoader(Context context, View view) {
        //Log.d(LOG_TAG, "PostLoader constructor");
        this.context = context;
        this.view = view;
        url = context.getResources().getString(R.string.url);

    }

    public boolean refresh_posts() {
        //Log.d(LOG_TAG, "refresh_posts started");


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            Toast.makeText(context, "No internet access", Toast.LENGTH_LONG).show();
            return false;
        }

        if (PostHolder.needUpdate()) {
//            view.findViewById(R.id.loading_indicator)
            progressBar = (ProgressBar) view.findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("TEST", "in thread...");
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url);
                    try {
                        HttpResponse httpResponse = client.execute(httpGet);
                        final String response = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");
                        int entries_count = entries.length();
                        for (int i = 0; i < entries_count; i++) {
                            HabraPost tmp_post = new HabraPost();
                            tmp_post.setTitle(entries.getJSONObject(i).getString("title"));
                            tmp_post.setLink(entries.getJSONObject(i).getString("link"));
                            tmp_post.setPublishDate(entries.getJSONObject(i).getString("publishedDate"));
                            tmp_post.setShortContent(entries.getJSONObject(i).getString("contentSnippet"));
                            tmp_post.setContent(entries.getJSONObject(i).getString("content"));
                            habraPosts.add(tmp_post);
                        }
                        PostHolder.setPosts(habraPosts);

                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                ListView listView = (ListView) view.findViewById(R.id.listView);
                                if (listView == null) {
                                    listView = (ListView) view.findViewById(R.id.left_drawer);
                                }
                                HabraAdapter habraAdapter = (HabraAdapter) listView.getAdapter();
                                if (habraAdapter != null) {
                                    habraAdapter.clear();
                                    habraAdapter.notifyDataSetChanged();
                                    habraAdapter.addAll(habraPosts);
                                    habraAdapter.notifyDataSetChanged();
                                }
                                int checkedItemPosition = listView.getCheckedItemPosition();
                                listView.setItemChecked(checkedItemPosition , true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(context, "Too frequently. Wait some time...", Toast.LENGTH_LONG).show();
        }

        //on finish remove loader icon
        return true;
    }


}
