package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;


public class StartActivity extends BaseActivity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {
    private final String LOG_TAG = LOG_TAG_PREFIX + getClass().getSimpleName();
    private boolean isMultiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO вставить сюди перевірку чи TopicsFragment вже існує. щоб не перестворювати його заново


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        isMultiPanel = findViewById(R.id.fragment_article) != null;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        TopicsFragment topicsFragment = TopicsFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_topics, topicsFragment);

        if (isMultiPanel) {
            //TODO show default Article here. maybe app info, or short help
            ArticleFragment articleFragment = new ArticleFragment();
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        }

        //create frsagments here
        //if article frag here, load empty frag

        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed. isMultiPanel: " + isMultiPanel);
        if ( ! isMultiPanel) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        super.onBackPressed();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isMultiPanel = findViewById(R.id.fragment_article) != null;
        Log.d(LOG_TAG, "onResume. isMultiPanel: " + isMultiPanel);

        /**
         * ПЕРЕПИСАТЬ
         * */

/*
        topicsFragment = (TopicsFragment) getFragmentManager().findFragmentById(R.id.fragment_topics);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int position = prefs.getInt(ArticleActivity.ARG_ARTICLE_POSITION, -1);
        //WTF!? o_O
        //коли повертається на цю актівіті з альбомної орієнтації, то в SharedPreferences ПУСТО!!!

        if (position != -1) {
            Log.d(LOG_TAG, "pos == " + position);

            if (topicsFragment != null) {
                ListView listView = (ListView) findViewById(R.id.listView);
                Log.d(LOG_TAG, "setItemChecked" + position);
                listView.setItemChecked(position, true);
            }

            ArticleFragment articleFragment = (ArticleFragment) getFragmentManager().findFragmentById(R.id.fragment_article);
            if (articleFragment != null) {
                try {
                    articleFragment.loadArticle(position);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "articleFragment != null");
                    e.printStackTrace();
                }
            }


        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         *  НАПИСАТЬ КОД ТУТ!
         * */

        switch (item.getItemId()) {
            case R.id.action_settings: {
                Log.d(LOG_TAG, "menu Settings");
            }
            break;
            case R.id.action_refresh: {
                Log.d(LOG_TAG, "menu Refresh");
                //topicsFragment.refresh_posts();
            }
            break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;


    }

    @Override
    public void onFragmentInteraction(int position) {
        Log.d(LOG_TAG, "onFragmentInteraction. isMultiPanel: " + isMultiPanel + ". position: " + position);
        if (isMultiPanel) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            ArticleFragment articleFragment = ArticleFragment.newInstance(position);
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ArticleActivity.ARG_ACTIVE_HABRA_POST, position);
            startActivity(intent);
        }
    }
}
