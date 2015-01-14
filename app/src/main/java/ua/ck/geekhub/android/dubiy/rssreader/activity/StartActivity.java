package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AppEventsLogger;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;
import ua.ck.geekhub.android.dubiy.rssreader.service.RefreshPostsService;
import ua.ck.geekhub.android.dubiy.rssreader.utils.Const;

//TODO spalsh screen
//TODO localization
public class StartActivity extends BaseActivity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {

    private boolean isMultiPanel;
    private long postId = 0;
    private boolean postFavourite = false;
    private SharedPreferences sPref;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //service
        Intent intent = new Intent(this, RefreshPostsService.class);
        intent.putExtra("testValue", "value!!!");
        startService(intent);

        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(Const.PARAM_STATUS, 0);
                Log.d(LOG_TAG, "onReceive: status = " + status);
                Toast.makeText(getApplication(), "onReceive: status = " + status, Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter intentFilter = new IntentFilter(Const.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        isMultiPanel = findViewById(R.id.fragment_article) != null;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        TopicsFragment topicsFragment = TopicsFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_topics, topicsFragment);

        if (isMultiPanel) {
            ArticleFragment articleFragment = new ArticleFragment();
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed. isMultiPanel: " + isMultiPanel);
        FragmentManager fragmentManager = getFragmentManager();
        if (!isMultiPanel) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
//        if (fragmentManager.getBackStackEntryCount() == 0) {
//            Intent intent = new Intent(this, RefreshPostsService.class);
//            stopService(intent);
//        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMultiPanel = findViewById(R.id.fragment_article) != null;
        invalidateOptionsMenu();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Called whenever we call invalidateOptionsMenu()
        menu.findItem(R.id.action_share).setVisible(isMultiPanel);
        menu.findItem(R.id.action_favourite).setVisible(isMultiPanel);
        if (postFavourite) {
            menu.findItem(R.id.action_favourite).setIcon(R.drawable.ic_action_favorite);
        } else {
            menu.findItem(R.id.action_favourite).setIcon(R.drawable.ic_action_favorite_empty);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about: {
                aboutAuthor();
            }
            break;
            case R.id.action_login: {
                startActivity(new Intent(this, LoginActivity.class));
            }

            break;
            case R.id.action_stop_service: {
                Intent intent = new Intent(this, RefreshPostsService.class);
                stopService(intent);

/*
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                        setSmallIcon(R.drawable.ic_launcher).
                        setContentTitle("Hello, megaMAN!").
                        setContentText("wou! Wou, baby!").
                        setAutoCancel(true).
                        setProgress(100, 35, false).
                        setPriority(NotificationCompat.PRIORITY_DEFAULT).
                        setLights(android.R.color.holo_green_light, 500, 500);
//                        setVibrate(vibrate);

                Intent resultIntent = new Intent(this, ArticleActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = 1;
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.notify(mNotificationId, mBuilder.build());
*/

            }
            break;
            case R.id.action_refresh: {
                FragmentManager fragmentManager = getFragmentManager();
                TopicsFragment topicsFragment = (TopicsFragment) fragmentManager.findFragmentById(R.id.fragment_topics);
                topicsFragment.refresh_posts();
            }
            break;
            case R.id.action_share: {
                PostEntity postEntity = new PostEntity();
                if (postEntity.loadFromDatabase(this, postId)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, postEntity.getLink());
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, postEntity.getTitle());
                    startActivity(Intent.createChooser(intent, "Share"));
                } else {
                    Toast.makeText(this, "Post not found", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.action_favourite: {
                PostEntity postEntity = new PostEntity();
                if (postEntity.updatePostFavourite(this, postId, !postFavourite) > 0) {
                    postFavourite = !postFavourite;
                    invalidateOptionsMenu();
                }
            }
            break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;


    }


    @Override
    public void onFragmentInteraction(long postId) {
        this.postId = postId;

        sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putLong(ArticleFragment.ARG_POST_ID, postId);
        ed.commit();

        if (isMultiPanel) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            ArticleFragment articleFragment = ArticleFragment.newInstance(postId);
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ArticleFragment.ARG_POST_ID, postId);
            startActivity(intent);
        }
    }

    @Override
    public void onFragmentUpdateActionBarFavIcon(boolean favourite) {
        this.postFavourite = favourite;
        invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
