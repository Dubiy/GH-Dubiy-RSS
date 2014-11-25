package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
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


public class StartActivity extends Activity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {
    private final String LOG_TAG = getClass().getSimpleName();

    private TopicsFragment topicsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        topicsFragment = (TopicsFragment) getFragmentManager().findFragmentById(R.id.fragment_topics);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int position = prefs.getInt(ArticleActivity.ARG_ARTICLE_POSITION, -1);
        //WTF!? o_O
        //коли повертається на цю актівіті з альбомної орієнтації, то в SharedPreferences ПУСТО!!!
        //костилі і велосіпєди
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


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings: {
                Log.d(LOG_TAG, "menu Settings");
            }
            break;
            case R.id.action_refresh: {
                Log.d(LOG_TAG, "menu Refresh");
                topicsFragment.refresh_posts();
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
        ArticleFragment articleFragment = (ArticleFragment) getFragmentManager().findFragmentById(R.id.fragment_article);
        if (articleFragment != null) {
            try {
                /**
                 * articleFragment.loadArticle(position) в try тому, що виникає виключна ситуація при наступних умовах:
                 * на планшеті в портретній орієнтації (коли на екрані лише один фрагмент) може виникати помилка,
                 * якщо перед цим екран був в альбомній орієнтації (на екран виводився другий фрагмент - ArticleFragment)
                 * В цьому випадку getFragmentManager().findFragmentById(R.id.fragment_article) повертає не null,
                 * але при виклику методу loadArticle(position) програма помирає
                 * */
                articleFragment.loadArticle(position);
            } catch (Exception e) {
                Intent intent = new Intent(this, ArticleActivity.class);
                intent.putExtra(ArticleActivity.ARG_ARTICLE_POSITION, position);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ArticleActivity.ARG_ARTICLE_POSITION, position);
            startActivity(intent);
        }
    }
}
