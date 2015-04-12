package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.AppEventsLogger;
import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.asynctask.PostLoad;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;
import ua.ck.geekhub.android.dubiy.rssreader.service.RefreshPostsService;
import ua.ck.geekhub.android.dubiy.rssreader.utils.Const;

public class StartActivity extends BaseActivity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {

    private boolean isMultiPanel;
    private long postId = -1;
    private boolean postFavourite = false;
    private SharedPreferences sPref;
    private BroadcastReceiver broadcastReceiver;
    private boolean serviceRunning = true;
    private final String SERVICE_RUNNING_KEY = "serviceRunning";
    private final String POST_ID_KEY = "postId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        isMultiPanel = findViewById(R.id.fragment_article) != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            TopicsFragment topicsFragment = TopicsFragment.newInstance();
            fragmentTransaction.replace(R.id.fragment_topics, topicsFragment);
        }
        if (savedInstanceState != null) {
            serviceRunning = savedInstanceState.getBoolean(SERVICE_RUNNING_KEY, serviceRunning);
            postId = savedInstanceState.getLong(POST_ID_KEY, postId);
        }


        if (isMultiPanel) {
            ArticleFragment articleFragment = null;
            if (postId > 0) {
                articleFragment = ArticleFragment.newInstance(postId);
            } else {
                articleFragment = new ArticleFragment();
            }
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        }
        fragmentTransaction.commit();

        if (savedInstanceState != null) {
            serviceRunning = savedInstanceState.getBoolean(SERVICE_RUNNING_KEY, serviceRunning);
            postId = savedInstanceState.getLong(POST_ID_KEY, postId);
        }

        if (serviceRunning) {
//                    startService(new Intent(getApplicationContext(), RefreshPostsService.class));
            
            serviceRunning = false;

        }

        broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(Const.PARAM_STATUS, 0);
                Log.d(LOG_TAG, "onReceive: status = " + status);
//                Toast.makeText(getApplication(), "onReceive: status = " + status, Toast.LENGTH_SHORT).show();
                TopicsFragment topicsFragment = (TopicsFragment)getFragmentManager().findFragmentById(R.id.fragment_topics);
                if (topicsFragment != null) {
                    topicsFragment.reload_list();
                }




            }
        };
        IntentFilter intentFilter = new IntentFilter(Const.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (!isMultiPanel) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
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
            } break;
            case R.id.action_login: {
                startActivity(new Intent(this, LoginActivity.class));
            } break;
            case R.id.action_stop_service: {
                Intent intent = new Intent(this, RefreshPostsService.class);
                stopService(intent);
                serviceRunning = false;
            } break;
            case R.id.action_refresh: {
                new PostLoad(getApplicationContext(), this).execute(false);

//                FragmentManager fragmentManager = getFragmentManager();
//                TopicsFragment topicsFragment = (TopicsFragment) fragmentManager.findFragmentById(R.id.fragment_topics);
//                topicsFragment.refresh_posts();
            } break;
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
            } break;
            case R.id.action_favourite: {
                PostEntity postEntity = new PostEntity();
                if (postEntity.updatePostFavourite(this, postId, !postFavourite) > 0) {
                    postFavourite = !postFavourite;
                    invalidateOptionsMenu();
                }
            } break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(long postId) {
        this.postId = postId;

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SERVICE_RUNNING_KEY, serviceRunning);
        outState.putLong(POST_ID_KEY, postId);
        super.onSaveInstanceState(outState);
    }
}