package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;


public class StartActivity extends Activity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int position) {





        ArticleFragment articleFragment = (ArticleFragment)getFragmentManager().findFragmentById(R.id.fragment_article);
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
